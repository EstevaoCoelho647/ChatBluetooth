package com.project.estevao.chatbluetooth.controller.activities;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;


import com.project.estevao.chatbluetooth.BluetoothHelper.PictureHelper;
import com.project.estevao.chatbluetooth.R;
import com.project.estevao.chatbluetooth.entities.User;
import com.project.estevao.chatbluetooth.persistence.BluetoothContract;
import com.project.estevao.chatbluetooth.persistence.BluetoothRepository;

import java.util.TooManyListenersException;

/**
 * Created by C1284520 on 01/12/2015.
 */
public class UserProfileActivity extends AppCompatActivity {
    private ImageView photoUser;
    private TextView nameUser;
    private User user;
    private ImageView editNome;
    private ImageView btnEditImage;
    private static final int CAMERA_REQUEST = 1888;
    private String photoS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile);

        getUser();
        bindPhotoUser();
        bindNameUser();
        bindEditName();
        bindBtnEditImage();
    }

    private void bindBtnEditImage() {
        btnEditImage = (ImageView) findViewById(R.id.editImage);
        btnEditImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnEditImage.startAnimation(AnimationUtils.loadAnimation(UserProfileActivity.this, R.anim.click));
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            photoUser.setImageBitmap(photo);
        }
    }

    private void bindEditName() {
        editNome = (ImageView) findViewById(R.id.editNome);
        editNome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(UserProfileActivity.this)
                        .setTitle("Name Edit")
                        .setMessage("Digite seu Nome nome de Dispositivo")

                        .setPositiveButton("Editar", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // continue with delete
                            }
                        })
                        .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });
    }


    private void bindNameUser() {
        nameUser = (TextView) findViewById(R.id.txt_nameView);
        nameUser.setText(BluetoothAdapter.getDefaultAdapter().getName());

    }

    private void bindPhotoUser() {
        photoUser = (ImageView) findViewById(R.id.picture);
        if (user.getPhoto() == null) {
            photoUser.setImageDrawable(getResources().getDrawable(R.drawable.ic_picture));
        } else
            photoUser.setImageBitmap(PictureHelper.getImage(user.getPhoto()));
    }

    public void getUser() {
        user = BluetoothRepository.getUser();
    }
}
