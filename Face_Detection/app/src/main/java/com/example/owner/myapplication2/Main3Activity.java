package com.example.owner.myapplication2;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Main3Activity extends AppCompatActivity {
    Button cButton, cSavaBtn;
    ImageView ivCamera;
    int REQUEST_WRITE_STORAGE=5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        cButton = (Button) findViewById(R.id.button);
        cSavaBtn = (Button) findViewById(R.id.buttonSave);
        ivCamera = (ImageView) findViewById(R.id.imageView);
        cButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplication(), "Camera Clicked", Toast.LENGTH_LONG);
                Intent cIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cIntent, 0);

            }
        });


        cSavaBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Toast.makeText(getApplication(), "Save btn Clicked", Toast.LENGTH_LONG);
                Save();

            }


        });
    }



    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap bm = (Bitmap) data.getExtras().get("data");
        ivCamera.setImageBitmap(bm);

    }


    public void Save() {
        OutputStream fOut1 = null;
        ivCamera.buildDrawingCache();
        Bitmap bm = ivCamera.getDrawingCache();
        boolean hasPermission = (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);

        if (!hasPermission) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_WRITE_STORAGE);
        }


        File folder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "HemPics");
        /*if (!folder.exists()) {
            if (!folder.mkdirs()) {
                Log.d("App", "failed to create directory");

            }
        }*/

        if (!folder.mkdirs()) {
            Log.d("App", "failed to create directory");

        }


        boolean success = true;
        if (!folder.exists()) {
            success = folder.mkdirs();
        }
        if (success) {
            // Do something on success

            File sdImageMainDirectory1 = new File(folder, "Pic" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime()) + ".png");
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
                MediaScannerConnection.scanFile(this, new String[]{sdImageMainDirectory1.getAbsolutePath()}, null, null);
                fOut1 = new FileOutputStream(sdImageMainDirectory1);
                Toast.makeText(this, "Correct end now",
                        Toast.LENGTH_SHORT).show();


                if (fOut1 != null) {

                    fOut1.write(baos.toByteArray());
                    Toast.makeText(this, "Doneeeeeeeeeeeeeeeeee",
                            Toast.LENGTH_SHORT).show();
                    fOut1.flush();
                    fOut1.close();
                }

            } catch (Exception e) {
                Toast.makeText(this, "Error occured. Please try again later.",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode)
        {
            case 5: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                  Save();  //reload my activity with permission granted or use the features what required the permission

                } else
                {
                    Toast.makeText(this, "The app was not allowed to write to your storage. Hence, it cannot function properly. Please consider granting it this permission", Toast.LENGTH_LONG).show();
                }
            }
        }

    }


    /* public String storeSdCard(Bitmap bitmap) {
        FileOutputStream fos = null;
        String status = Environment.getExternalStorageState();
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            File root = new File(Environment.getExternalStorageDirectory(),
                    "FolderName");
            if (!root.exists()) {
                root.mkdirs();
            }
            String filename = "" + System.currentTimeMillis();
            File file = new File(root, filename + ".jpg");

            try {
                fos = new FileOutputStream(file);
                if (fos != null) {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 70, fos);
                    fos.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            Uri muri = Uri.fromFile(file);
            String path = muri.getPath();

            return path;
        }
        return null;
    }
*/
    public void AfterSaveClick() {

        ivCamera.buildDrawingCache();
        Bitmap bm = ivCamera.getDrawingCache();


        OutputStream fOut = null;

        try {
            Toast.makeText(this, "Correct now",
                    Toast.LENGTH_SHORT).show();
            File root = new File(Environment.getExternalStorageDirectory()
                    + File.separator + "sharathFolder" + File.separator);
            root.mkdirs();
            File sdImageMainDirectory = new File(root, "myPicName.jpg");
            fOut = new FileOutputStream(sdImageMainDirectory);

            Toast.makeText(this, "Correct end now",
                    Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Toast.makeText(this, "Error occured. Please try again later.",
                    Toast.LENGTH_SHORT).show();
        }

        try {
            bm.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
            fOut.close();
        } catch (Exception e) {
            Toast.makeText(this, "Exception.",
                    Toast.LENGTH_SHORT).show();
        }
    }
}