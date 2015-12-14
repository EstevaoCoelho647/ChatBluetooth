package com.project.estevao.chatbluetooth.controller.activities;

import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.speech.RecognizerIntent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.project.estevao.chatbluetooth.BluetoothHelper.BluetoothChatService;
import com.project.estevao.chatbluetooth.BluetoothHelper.PictureHelper;
import com.project.estevao.chatbluetooth.controller.adapters.ChatAdapter;
import com.project.estevao.chatbluetooth.R;
import com.project.estevao.chatbluetooth.entities.DataMessage;
import com.project.estevao.chatbluetooth.entities.User;
import com.project.estevao.chatbluetooth.persistence.BluetoothContract;
import com.project.estevao.chatbluetooth.persistence.BluetoothRepository;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

/**
 * Created by c1284520 on 12/11/2015.
 */
public class BluetoothChatActivity extends AppCompatActivity {
    // Debugging
    private static final String TAG = "BluetoothChatActivity";

    protected static final int RESULT_SPEECH = 10;
    Bitmap photo;

    private final String read = "READ";
    private final String write = "WRITE";
    private static final boolean D = true;
    DataMessage dataMessage;

    // DataMessage types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int CAMERA_REQUEST = 1888;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;

    // Layout Views
    private ListView mConversationView;
    private EditText mOutEditText;
    private FloatingActionButton mSendButton;

    // Name of the connected device
    private String mConnectedDeviceName = null;
    // Array adapter for the conversation thread
    private ChatAdapter mConversationArrayAdapter;
    // String buffer for outgoing messages
    private StringBuffer mOutStringBuffer;
    // Local Bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter = null;
    // Member object for the chat services
    private BluetoothChatService mChatService = null;
    private Toolbar toolbar;

    private ImageView buttonIcons;

    private TextView text;
    private ImageView photoButton;
    private boolean IsSeted = false;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (D) Log.e(TAG, "+++ ON CREATE +++");

        // Set up the window layout
        setContentView(R.layout.main);

        toolbar = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);

        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();


        bindButtonPhoto();

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
    }

    private void bindButtonPhoto() {
        photoButton = (ImageView) findViewById(R.id.button_picture);
        photoButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                photoButton.startAnimation(AnimationUtils.loadAnimation(BluetoothChatActivity.this, R.anim.click));
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        if (D) Log.e(TAG, "++ ON START ++");

        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            // Otherwise, setup the chat session
        } else {
            if (mChatService == null) setupChat();
        }
    }

    @Override
    public synchronized void onResume() {
        super.onResume();
        if (D) Log.e(TAG, "+ ON RESUME +");

        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        if (mChatService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mChatService.getState() == BluetoothChatService.STATE_NONE) {
                // Start the Bluetooth chat services
                mChatService.start();
            }
        }
    }

    private void setupChat() {
        Log.d(TAG, "setupChat()");

        // Initialize the array adapter for the conversation thread
        mConversationView = (ListView) findViewById(R.id.in);
        mConversationArrayAdapter = new ChatAdapter(BluetoothChatActivity.this);
        mConversationView.setAdapter(mConversationArrayAdapter);
        text = (TextView) mConversationView.findViewById(R.id.textMessage);


        //initialize the icon button
        buttonIcons = (ImageView) findViewById(R.id.iconShow);
        buttonIcons.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonIcons.startAnimation(AnimationUtils.loadAnimation(BluetoothChatActivity.this, R.anim.click));
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            }
        });


        // Initialize the send button with a listener that for click events
        mSendButton = (FloatingActionButton) findViewById(R.id.button_send);
        mSendButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                mSendButton.startAnimation(AnimationUtils.loadAnimation(BluetoothChatActivity.this, R.anim.click));
                // Send a message using content of the edit text widget
                TextView view = (TextView) findViewById(R.id.edit_text_out);
                String message = view.getText().toString();
                DataMessage dataMessage = new DataMessage();
                dataMessage.setTxt(message);
                dataMessage.setNameUser("Me");
                dataMessage.setTime(new Date().getTime());
                dataMessage.setUser(BluetoothRepository.getUser());
                sendMessage(dataMessage);
            }
        });
        mSendButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Intent intent = new Intent(
                        RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");

                try {
                    startActivityForResult(intent, RESULT_SPEECH);
                    mOutEditText.setText("");
                } catch (ActivityNotFoundException a) {
                    Toast t = Toast.makeText(getApplicationContext(),
                            "Ops! Your device doesn't support Speech to Text",
                            Toast.LENGTH_SHORT);
                    t.show();
                }
                return false;
            }
        });

        // Initialize the BluetoothChatService to perform bluetooth connections
        mChatService = new BluetoothChatService(this, mHandler);

        // Initialize the buffer for outgoing messages
        mOutStringBuffer = new StringBuffer("");
        // Initialize the compose field with a listener for the return key
        mOutEditText = (EditText) findViewById(R.id.edit_text_out);
        mOutEditText.setOnEditorActionListener(mWriteListener);

        mOutEditText.addTextChangedListener(new TextWatcher() {
                                                @Override
                                                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                                                }

                                                @Override
                                                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                                                }

                                                @Override
                                                public void afterTextChanged(Editable editable) {
                                                    if (mOutEditText.getText().toString().equals("")) {

                                                        if (IsSeted == true) {
                                                            mSendButton.setAnimation(AnimationUtils.loadAnimation(BluetoothChatActivity.this, R.anim.scale_down));
                                                            mSendButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_voice));


                                                            IsSeted = false;
                                                        }
                                                        if (photoButton.getVisibility() == View.GONE) {
                                                            photoButton.setVisibility(View.VISIBLE);
                                                            photoButton.setAnimation(AnimationUtils.loadAnimation(BluetoothChatActivity.this, R.anim.appears));
                                                        }
                                                    } else {
                                                        if (photoButton.getVisibility() == View.VISIBLE) {
                                                            photoButton.setAnimation(AnimationUtils.loadAnimation(BluetoothChatActivity.this, R.anim.disappear));
                                                            photoButton.setVisibility(View.GONE);
                                                        }
                                                        if (IsSeted == false) {

                                                            mSendButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_send));
                                                            mSendButton.setAnimation(AnimationUtils.loadAnimation(BluetoothChatActivity.this, R.anim.scale_up));
                                                            IsSeted = true;
                                                        }
                                                    }
                                                }
                                            }

        );
    }

    @Override
    public synchronized void onPause() {
        super.onPause();
        if (D) Log.e(TAG, "- ON PAUSE -");
    }

    @Override
    public void onStop() {
        super.onStop();
        if (D) Log.e(TAG, "-- ON STOP --");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Stop the Bluetooth chat services
        if (mChatService != null) mChatService.stop();
        if (D) Log.e(TAG, "--- ON DESTROY ---");
    }

    private void ensureDiscoverable() {
        if (D) Log.d(TAG, "ensure discoverable");
        if (mBluetoothAdapter.getScanMode() !=
                BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);

        }
    }

    /**
     * Sends a message.
     *
     * @param message A string of text to send.
     */
    private void sendMessage(DataMessage message) {
        // Check that we're actually connected before trying anything
        if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
            Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
            return;
        }

        // Check that there's actually something to send
        if (!message.getTxt().equals("")) {
            // Get the message bytes and tell the BluetoothChatService to write
            try {
                mChatService.write(BluetoothChatService.convertToBytes(message));
                // Reset out string buffer to zero and clear the edit text field
                mOutStringBuffer.setLength(0);
                mOutEditText.setText(mOutStringBuffer);
            } catch (IOException e) {
                Toast.makeText(BluetoothChatActivity.this, "erro", Toast.LENGTH_SHORT);
                e.printStackTrace();

            }


        }
    }

    // The action listener for the EditText widget, to listen for the return key
    private TextView.OnEditorActionListener mWriteListener =
            new TextView.OnEditorActionListener() {
                public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
                    // If the action is a key-up event on the return key, send the message
                    if (actionId == EditorInfo.IME_NULL && event.getAction() == KeyEvent.ACTION_UP) {
                        DataMessage message = new DataMessage();
                        message.setTxt(view.getText().toString());
                        message.setNameUser("Me");
                        message.setTime(new Date().getTime());
                        message.setUser(BluetoothRepository.getUser());
                        sendMessage(message);
                    }
                    if (D) Log.i(TAG, "END onEditorAction");
                    return true;
                }
            };

    private final void setStatus(int resId) {
        final android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setSubtitle(resId);
    }

    private final void setStatus(CharSequence subTitle) {
        final android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setSubtitle(subTitle);
    }

    // The Handler that gets information back from the BluetoothChatService
    public final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            ChatAdapter adapter;
            switch (msg.what) {
                case MESSAGE_STATE_CHANGE:
                    if (D) Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                    switch (msg.arg1) {
                        case BluetoothChatService.STATE_CONNECTED:
                            setStatus(R.string.title_connected);
                            setTitle(getString(R.string.title_connected_to, mConnectedDeviceName));
                            toolbar.setTitleTextColor(Color.WHITE);
                            break;
                        case BluetoothChatService.STATE_CONNECTING:
                            setStatus(R.string.title_connecting);
                            toolbar.setSubtitleTextColor(Color.WHITE);
                            break;
                        case BluetoothChatService.STATE_LISTEN:
                            break;
                        case BluetoothChatService.STATE_NONE:
                            setStatus(R.string.title_not_connected);
                            toolbar.setSubtitleTextColor(Color.WHITE);
                            break;
                    }
                    break;
                case MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    try {
                        DataMessage dataMessage = (DataMessage) BluetoothChatService.convertFromBytes(writeBuf);
                        if (dataMessage.getTxt().startsWith("*IMAGE*")) {
                            dataMessage.setType(write + "image");
                        } else
                            dataMessage.setType(write);
                        mConversationArrayAdapter.add(dataMessage);
                        mConversationArrayAdapter.notifyDataSetChanged();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    break;
                case MESSAGE_READ:
                    byte[] writeBuf2 = (byte[]) msg.obj;
//                    try {
                    //DataMessage dataMessage = (DataMessage) BluetoothChatService.convertFromBytes(writeBuf2);
                    DataMessage dataMessage = new DataMessage();
                    User user = new User();
                    user.setId(1l);
                    user.setPhoto(PictureHelper.getBytes(photo));
                    user.setLogin(true);
                    dataMessage.setTxt("*IMAGE*");
                    dataMessage.setUser(user);
                    dataMessage.setNameUser("oi");
                    dataMessage.setTime(25l);
                    if (dataMessage.getTxt().startsWith("*IMAGE*")) {
                        dataMessage.setType(read + "image");
                    } else
                        dataMessage.setType(read);


                    // toolbar.setNavigationIcon(new BitmapDrawable(PictureHelper.StringToBitMap(dataMessage.getUser().getPhoto())));
                    mConversationArrayAdapter.add(dataMessage);
                    mConversationArrayAdapter.notifyDataSetChanged();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    } catch (ClassNotFoundException e) {
//                        e.printStackTrace();
//                    }

                    break;
                case MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                    Toast.makeText(getApplicationContext(), "Connected to "
                            + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_TOAST:
                    Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),
                            Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (D) Log.d(TAG, "onActivityResult " + resultCode);
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE_SECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, true);
                }
                break;
            case REQUEST_CONNECT_DEVICE_INSECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, false);
                }
                break;
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a chat session
                    setupChat();
                } else {
                    // User did not enable Bluetooth or an error occurred
                    Log.d(TAG, "BT not enabled");
                    Toast.makeText(this, R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
                    finish();
                }
            case RESULT_SPEECH: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> text = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                    mOutEditText.setText(text.get(0));
                }
                break;
            }
        }
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            photo = (Bitmap) data.getExtras().get("data");
            User user = new User();
            user.setPhoto(PictureHelper.getBytes(photo));
            user.setLogin(true);
            user.setId(1l);
            DataMessage dataMessage = new DataMessage();
            dataMessage.setTxt("*IMAGE*");
            dataMessage.setNameUser("Me");
            dataMessage.setTime(new Date().getTime());
            dataMessage.setUser(user);
            //dataMessage.setUser(BluetoothRepository.getUser());
            sendMessage(dataMessage);
        }
    }

    private void connectDevice(Intent data, boolean secure) {
        // Get the device MAC address
        String address = data.getExtras()
                .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        // Get the BluetoothDevice object
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        // Attempt to connect to the device
        mChatService.connect(device, secure);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu, menu);
        return true;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent serverIntent = null;
        switch (item.getItemId()) {
//            case R.id.secure_connect_scan:
//                // Launch the DeviceListActivity to see devices and do scan
//                serverIntent = new Intent(this, DeviceListActivity.class);
//                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);
//                return true;
            case R.id.insecure_connect_scan:
                // Launch the DeviceListActivity to see devices and do scan
                serverIntent = new Intent(this, DeviceListActivity.class);
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);
                return true;
            case R.id.discoverable:
                item.getIcon().setColorFilter(Color.WHITE, PorterDuff.Mode.DST_IN);// Ensure this device is discoverable by others
                ensureDiscoverable();
                return true;
        }
        return false;
    }

}