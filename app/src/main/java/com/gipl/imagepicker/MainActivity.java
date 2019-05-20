package com.gipl.imagepicker;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;


import java.io.File;


public class MainActivity extends AppCompatActivity {

    private ImageView cropImageView;
    private ImagePickerDialog imagePickerDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cropImageView = findViewById(R.id.cropImageView);

        final PickerConfiguration pickerConfiguration = PickerConfiguration.build()
                .setTextColor(Color.parseColor("#000000"))
                .setIconColor(Color.parseColor("#000000"))
                .setBackGroundColor(Color.parseColor("#ffffff"))
                .setPickerDialogListener(new PickerListener() {
                    @Override
                    public void onCancelClick() {
                        super.onCancelClick();
                        Toast.makeText(MainActivity.this, "Cancel", Toast.LENGTH_SHORT).show();
                    }
                })
                .setImagePickerResult(new PickerResult() {

                    @Override
                    public void onImageGet(String sPath, Bitmap bitmap) {
                        super.onImageGet(sPath, bitmap);
                        setImage(sPath, bitmap);
                    }

                    @Override
                    public void onError(ImagePicker.CameraErrors cameraErrors) {
                        super.onError(cameraErrors);
                        setError(cameraErrors);
                    }
                })
                .setSetCustomDialog(true);



        findViewById(R.id.btn_open_camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imagePickerDialog = ImagePickerDialog.display(getSupportFragmentManager(), pickerConfiguration.setSetCustomDialog(true));
            }
        });


        findViewById(R.id.btn_open_picker).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (imagePickerDialog != null && imagePickerDialog.isVisible())
                    imagePickerDialog.dismiss();
                imagePickerDialog = ImagePickerDialog.display(getSupportFragmentManager(),
                        pickerConfiguration.setSetCustomDialog(false));
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        imagePickerDialog.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        imagePickerDialog.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void setImage(String sPath, Bitmap bitmap) {
        if (!sPath.isEmpty()) {
            cropImageView.setImageURI(Uri.fromFile(new File(sPath)));
        } else
            cropImageView.setImageBitmap(bitmap);
    }

    public void setError(ImagePicker.CameraErrors cameraErrors) {
        if (cameraErrors.getErrorType() == ImagePicker.CameraErrors.PERMISSION_ERROR) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
            alertDialog.setTitle("Camera permission deny!");
            alertDialog.setMessage("Camera will be available after enabling Camera and Storage permission from setting");
            alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                }
            });
            alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });

            alertDialog.show();
        }
        Toast.makeText(MainActivity.this, cameraErrors.getMessage(), Toast.LENGTH_SHORT).show();
    }
}
