package com.example.owner.myapplication2;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.gms.vision.face.Landmark;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class MainActivity extends AppCompatActivity {

    private ImageView imageView;
    private TextView mTextOnImage;
    public static final int PICK_IMAGE = 1;
    private Button btnProgress, btnProgress1, btn,button;
    Canvas canvas;
   int REQUEST_WRITE_STORAGE=5;
   Bitmap bmp1,bmp2;
   Bitmap by;

    Paint rectPaint = new Paint();
    Paint paint = new Paint();
    private String m_Text = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTextOnImage = (TextView)findViewById(R.id.text_on_image);

        imageView = (ImageView) findViewById(R.id.imageView);
        btnProgress = (Button) findViewById(R.id.btnProgress);
        btnProgress1 = (Button) findViewById(R.id.btnProgress1);
        btn = (Button) findViewById(R.id.btn);
        button= (Button) findViewById(R.id.buttonSave);
        showImageDialog();


        }
    private void showImageDialog() {
        btnProgress1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnProgress1.setVisibility(View.GONE);
                Intent intent = new Intent();
                File folder = new File(Environment.getExternalStorageDirectory().getPath() + "/HemPics");
                Uri uri = Uri.fromFile(folder);
                String file = folder.getAbsolutePath();
                //Uri uri= Uri.fromFile(folder);
                intent.setDataAndType(uri, "image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
            }

        });


    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == PICK_IMAGE) && (resultCode == RESULT_OK) && (data != null)) {
            //TODO: action
            String path = null;

            Uri uri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                Facedetect(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }

            //uri.getContentResolver()
            /*if (uri != null && "content".equals(uri.getScheme())) {
                Cursor cursor = this.getContentResolver().query(uri, new String[]{android.provider.MediaStore.Images.ImageColumns.DATA}, null, null, null);
                cursor.moveToFirst();

                path = cursor.getString(0);
            }
            else {
                path = uri.getPath();

            }
            if(path.isEmpty())
            { Log.d("MainActivity","NOOOOOOOOO");
                Toast.makeText(MainActivity.this, "Face Detector could not be set up on your device", Toast.LENGTH_SHORT).show();

            }

              */  //Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);


        }
    }

    /* public void choosevideoFromGallery() {

         //Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
         // getIntent.setType("video/*");

         Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
         pickIntent.setType("video/*");
         pickIntent.setAction(Intent.ACTION_GET_CONTENT);
         //Uri mVideoCaptureUri = pickIntent.getData();
         //pickIntent.putExtra(MediaStore.EXTRA_OUTPUT,mVideoCaptureUri);

         //Intent chooserIntent = Intent.createChooser(getIntent, "Select Video");
         //chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

         startActivityForResult(Intent.createChooser(pickIntent,"Select Video"),PICK_VIDEO);

     }
 */
    public void Facedetect(final Bitmap bitmap) {
        final Bitmap myBitmap = Bitmap.createBitmap(bitmap);
        //final Bitmap myBitmap = BitmapFactory.decodeFile(path);
        if (myBitmap == null) {
            Log.d("MainActivity", "NOOOOOOOOO");
        }
        imageView.setImageBitmap(myBitmap);
        rectPaint.setStrokeWidth(5);
        rectPaint.setColor(Color.WHITE);
        rectPaint.setStyle(Paint.Style.STROKE);

        final Bitmap tempBitmap = Bitmap.createBitmap(myBitmap.getWidth(), myBitmap.getHeight(), Bitmap.Config.RGB_565);
        canvas = new Canvas(tempBitmap);
        canvas.drawBitmap(myBitmap, 0, 0, null);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.BLACK);
        paint.setTextSize(75);


        btnProgress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FaceDetector faceDetector = new FaceDetector.Builder(getApplicationContext())
                        .setTrackingEnabled(false)
                        .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                        .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                        .setMode(FaceDetector.ACCURATE_MODE)
                        .build();
                /*mCameraSource = new CameraSource.Builder(context, detector)
                        .setRequestedPreviewSize(640, 480)
                        .setFacing(CameraSource.CAMERA_FACING_FRONT)
                        .setRequestedFps(30.0f)
                        .build();*/

                if (!faceDetector.isOperational()) {
                    Toast.makeText(MainActivity.this, "Face Detector could not be set up on your device", Toast.LENGTH_SHORT).show();
                    return;
                }
                Frame frame = new Frame.Builder().setBitmap(myBitmap).build();
                SparseArray<Face> sparseArray = faceDetector.detect(frame);

                for (int i = 0; i < sparseArray.size(); i++) {
                    Face face = sparseArray.valueAt(i);
                    float x1 = face.getPosition().x;
                    float y1 = face.getPosition().y;
                    float x2 = x1 + face.getWidth();
                    float y2 = y1 + face.getHeight();
                    RectF rectF = new RectF(x1, y1, x2, y2);
                    canvas.drawRoundRect(rectF, 2, 2, rectPaint);
                    //canvas.drawText("Some Text here", 3,3,rectPaint);
                    //canvas.drawText("Some Text here", 3, 3, paint);
                    //canvas.drawText("Hello Image", x1, y2, paint);
                    //final Bitmap bmp4= addTag1();
                    //canvas.drawBitmap(bmp4,x1,y2,paint);
                    //String text=addTag1();
                    //canvas.drawText(text,100,100,paint);
                    //addTag1(tempBitmap,canvas);


                }

                imageView.setImageDrawable(new BitmapDrawable(getResources(), tempBitmap));

            }
        });
        //bmp2=tempBitmap;



        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplication(), "Save btn Clicked", Toast.LENGTH_LONG);
                Save(tempBitmap);

            }


        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTag1(tempBitmap, canvas);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(getApplication(), "Save btn Clicked", Toast.LENGTH_LONG);
                        Save(tempBitmap);

                    }


                });


            }
        });



    }

    private Canvas addTag1(Bitmap tempBitmap, Canvas canvas)
    {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Title");

// Set up the input
                final EditText input = new EditText(this);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_TEXT );
                builder.setView(input);

// Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        m_Text = input.getText().toString().trim();
                        mTextOnImage.setText(m_Text);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
  canvas.drawText(m_Text,10,10,paint);
        /*input.setCursorVisible(false);
        input.buildDrawingCache();
        input.setDrawingCacheEnabled(true);
        Bitmap bmp = Bitmap.createBitmap(input.getDrawingCache());

        final Bitmap combined = combineImages(tempBitmap,bmp);
        input.destroyDrawingCache();*/
        //imageView.setImageDrawable(new BitmapDrawable(getResources(), combined));

        /*button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplication(), "Save btn Clicked", Toast.LENGTH_LONG);
                Save(combined);

            }


        });
*/
        /*input.setDrawingCacheEnabled(true);
        input.measure(View.MeasureSpec.makeMeasureSpec(input.getLayoutParams().width, View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(input.getLayoutParams().height, View.MeasureSpec.EXACTLY));
        input.layout(0, 0, input.getMeasuredWidth(), input.getMeasuredHeight());
        input.buildDrawingCache(true);
        final Bitmap bmp6= Bitmap.createBitmap(input.getDrawingCache());
        input.setDrawingCacheEnabled(false);*/
return canvas;

    }

        /*public Bitmap addTag(Bitmap tempBitmap) {
        final EditText textContent = (EditText) findViewById(R.id.plain_text_input);
            Bitmap bmp = Bitmap.createBitmap(textContent.getDrawingCache());
         if (bmp==null)
         {
             Log.d("APPP","It is empty");
         }
            final Bitmap combined = combineImages(tempBitmap,bmp);
            imageView.setImageDrawable(new BitmapDrawable(getResources(), combined));

return bmp1;
        }*/

    public Bitmap combineImages(Bitmap background, Bitmap foreground) {

        int width = 0, height = 0;
        Bitmap cs;
        DisplayMetrics displaymetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        width = displaymetrics.widthPixels;
        height = displaymetrics.heightPixels;

        //width = getWindowManager().getDefaultDisplay().getWidth();
        //height = getWindowManager().getDefaultDisplay().getHeight();

        cs = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas comboImage = new Canvas(cs);
        background = Bitmap.createScaledBitmap(background, width, height, true);
        comboImage.drawBitmap(background, 0, 0, null);
        comboImage.drawBitmap(foreground, 0,0, null);

        return cs;
    }

/*
    public void addTag() {

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayTextBox();
            }
        });

    }
    private void displayTextBox(){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_dialog, null);
        dialogBuilder.setView(dialogView);
        final EditText textContent = (EditText) dialogView.findViewById(R.id.add_text_on_image);
        dialogBuilder.setTitle("");
        dialogBuilder.setMessage("");
        dialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                userInputValue = textContent.getText().toString();
                if(!userInputValue.equals("") || !userInputValue.isEmpty()){
                    // assign the content to the TextView object
                    mTextOnImage.setText(userInputValue);
                }
            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }

    private Bitmap ProcessingBitmap(String captionString) {
        Bitmap bm1 = null;
        Bitmap newBitmap = null;
        try {
            Toast.makeText(MainActivity.this, pickedImage.getPath(), Toast.LENGTH_LONG).show();
            bm1 = BitmapFactory.decodeStream(getContentResolver().openInputStream(pickedImage));
            Bitmap.Config config = bm1.getConfig();
            if (config == null) {
                config = Bitmap.Config.ARGB_8888;
            }
            newBitmap = Bitmap.createBitmap(bm1.getWidth(), bm1.getHeight(), config);
            Canvas canvas = new Canvas(newBitmap);
            canvas.drawBitmap(bm1, 0, 0, null);
            Paint paintText = new Paint(Paint.ANTI_ALIAS_FLAG);
            paintText.setColor(Color.BLUE);
            paintText.setTextSize(50);
            paintText.setStyle(Paint.Style.FILL);
            paintText.setShadowLayer(10f, 10f, 10f, Color.BLACK);
            Rect textRect = new Rect();
            paintText.getTextBounds(captionString, 0, captionString.length(), textRect);
            if(textRect.width() >= (canvas.getWidth() - 4))
                paintText.setTextSize(convertToPixels(7));
            int xPos = (canvas.getWidth() / 2) - 2;
            int yPos = (int) ((canvas.getHeight() / 2) - ((paintText.descent() + paintText.ascent()) / 2)) ;
            canvas.drawText(captionString, xPos, yPos, paintText);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return newBitmap;
    }
*/

    public void Save(Bitmap bmm) {


        OutputStream fOut1 = null;

        boolean hasPermission = (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);

        if (!hasPermission) {
            //temp(bmm);

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
                bmm.compress(Bitmap.CompressFormat.PNG, 100, baos);
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

 public Bitmap temp(Bitmap bmm)
 {
     return bmm;
 }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode)
        {
            case 5: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    Save(by);  //reload my activity with permission granted or use the features what required the permission

                } else
                {
                    Toast.makeText(this, "The app was not allowed to write to your storage. Hence, it cannot function properly. Please consider granting it this permission", Toast.LENGTH_LONG).show();
                }
            }
        }

    }


}


