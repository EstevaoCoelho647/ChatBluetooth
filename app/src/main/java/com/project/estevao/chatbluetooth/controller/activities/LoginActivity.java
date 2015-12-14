package com.project.estevao.chatbluetooth.controller.activities;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.LoginFilter;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.project.estevao.chatbluetooth.BluetoothHelper.PictureHelper;
import com.project.estevao.chatbluetooth.R;
import com.project.estevao.chatbluetooth.entities.User;
import com.project.estevao.chatbluetooth.persistence.BluetoothContract;
import com.project.estevao.chatbluetooth.persistence.BluetoothRepository;
import com.project.estevao.chatbluetooth.persistence.DatabaseHelper;

/**
 * Created by C1284520 on 30/11/2015.
 */
public class LoginActivity extends AppCompatActivity {

    private static final int CAMERA_REQUEST = 1888;
    EditText edTxtName;
    TextView btnOk;
    ImageView picture;
    CardView cardViewPicture;
    private BluetoothAdapter mBluetoothAdapter;
    Bitmap photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (BluetoothRepository.getUser() == null) {

            setContentView(R.layout.login);
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            bindCardViewPicture();
            bindEditTextName();
            bindPicture();
            bindButtonOk();
        } else {
            Intent goToChat = new Intent(LoginActivity.this, BluetoothChatActivity.class);
            startActivity(goToChat);
        }
    }

    private void bindCardViewPicture() {
        cardViewPicture = (CardView) findViewById(R.id.card_view_photo);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            photo = (Bitmap) data.getExtras().get("data");
            picture.setImageBitmap(photo);
        }
    }

    private void bindPicture() {
        picture = (ImageView) findViewById(R.id.picture);
        picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                picture.startAnimation(AnimationUtils.loadAnimation(LoginActivity.this, R.anim.click));
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
        });
    }

    private void bindButtonOk() {
        btnOk = (TextView) findViewById(R.id.btn_ok);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToChat = new Intent(LoginActivity.this, BluetoothChatActivity.class);
                String deviceName = edTxtName.getText().toString();
                mBluetoothAdapter.setName(deviceName);

                User user = new User();
                user.setPhoto(PictureHelper.getBytes(photo));
                user.setLogin(true);

                BluetoothRepository.save(user);

                Toast.makeText(LoginActivity.this, "Bem vindo " + deviceName + "\nLogin realizado com Sucesso!", Toast.LENGTH_SHORT).show();
                startActivity(goToChat);
            }
        });
    }

    private void bindEditTextName() {
        edTxtName = (EditText) findViewById(R.id.txt_name);
    }
}
