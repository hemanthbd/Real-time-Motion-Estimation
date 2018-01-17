package com.example.owner.eee508opencvtutorial; // To Select a video from Gallery and display its motion detection by optical flow

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvException;
import org.opencv.core.CvType;
import org.opencv.core.KeyPoint;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.core.TermCriteria;
import org.opencv.features2d.FeatureDetector;
import org.opencv.imgproc.Imgproc;
import org.opencv.utils.Converters;
import org.opencv.video.Video;

import java.io.File;
import java.util.ArrayList;
import java.util.Vector;

import wseemann.media.FFmpegMediaMetadataRetriever;




public class Main6Activity extends AppCompatActivity {


    public static final int PICK_VIDEO = 1;

    // directory name to store captured images and videos

    private VideoView videoview;
    private ImageView imageview;
    private Button btnCaptureVideo;
    private VideoView videoPreview;

    public Mat mRgba;
    public Mat mGrayMat;
    public Mat mGrayMat1;
    public Mat mGrayMat2;
    public Mat Mask;
    public Mat Img;
    public Mat frame1;
    public Mat frame2;
    public Bitmap bmp;
    Canvas canvas;
Paint paint;
    MatOfByte status;
    MatOfFloat err;


    public int frames;

    public FeatureDetector featureDetector;
    public MatOfKeyPoint keyPoints;
    public MatOfKeyPoint keyPoints2;
    public MatOfPoint2f pointsPrev;
    public MatOfPoint2f pointsThis;
    public MatOfPoint2f pointsPrev1;
    public MatOfPoint2f pointsPrev2;

    private BaseLoaderCallback mLoaderCallback= new BaseLoaderCallback(this) {

        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    Log.i("Main6Activity", "OpenCV loaded successfully");
                    keyPoints = new MatOfKeyPoint();
                    keyPoints2 = new MatOfKeyPoint();
                    pointsPrev= new MatOfPoint2f();

                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main6);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //videoview = (VideoView) findViewById(R.id.videoview);
        imageview = (ImageView) findViewById(R.id.imageview);
        btnCaptureVideo = (Button) findViewById(R.id.btnCaptureVideo);

        /**
         * Capture image button click event
         */
        btnCaptureVideo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // capture picture
                showVideoDialog();
                //captureImage();
            }
        });
        /*btnRecordVideo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // record video
                recordVideo();
            }
        });*/

        // Checking camera availability
        if (!isDeviceSupportCamera()) {
            Toast.makeText(getApplicationContext(),
                    "Sorry! Your device doesn't support camera",
                    Toast.LENGTH_LONG).show();
            // will close the app if the device does't have camera
            finish();
        }
    }

    private boolean isDeviceSupportCamera() {
        if (getApplicationContext().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    public void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()){
            Log.d("OpenCV", "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);}
        else{
            Log.d("OpenCV", "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);}
    }


    private void showVideoDialog() {


        AlertDialog.Builder videoDialog = new AlertDialog.Builder(this);
        videoDialog.setTitle("Select Action");
        String[] videoDialogItems = {
                "Select video from Gallery(Not from RECENT)",
                "Use Camera itself"};
        videoDialog.setItems(videoDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                choosevideoFromGallery();
                                break;
                            case 1:
                                choosecamera();
                                break;
                        }
                    }
                });
        videoDialog.show();
    }

    public void choosevideoFromGallery() {

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


    private void choosecamera() {

        goToMain5Activity();
    }

    private void goToMain5Activity() {

        Intent intent = new Intent(this, Main5Activity.class);

        startActivity(intent);

    }


    public void onActivityResult(int requestCode,int resultCode, Intent data) {
        Log.d("Main6Activity", "Did it arrive?");


        if(resultCode == Activity.RESULT_OK && requestCode == PICK_VIDEO) {
            if (data != null) {

                Uri mVideoCaptureUri = data.getData();
                FFmpegMediaMetadataRetriever med = new FFmpegMediaMetadataRetriever();
                Log.d("uri",mVideoCaptureUri.toString());
                try {
                    //videoview.setVideoURI(mVideoCaptureUri);

                    mRgba = new Mat();
                    mGrayMat = new Mat();
                    mGrayMat1 = new Mat();
                    mGrayMat2 = new Mat();
                    Img= new Mat();
                    frames=0;
                    pointsThis= new MatOfPoint2f();
                    featureDetector = FeatureDetector.create(FeatureDetector.ORB);

                    File file = new File(mVideoCaptureUri.getPath());
                    String path = file.getAbsolutePath();
                    //if (file.exists()) {
                    //med.setDataSource(this,mVideoCaptureUri);

                    try {
                        for(int t=1000; t<21000; t=t+1000)
                        {
                        med.setDataSource(this,mVideoCaptureUri);
                        Bitmap bm =med.getFrameAtTime(t,FFmpegMediaMetadataRetriever.OPTION_CLOSEST);
                        imageview.setImageBitmap(bm);
                       /* Bitmap bArray = med.getFrameAtTime(10000000, FFmpegMediaMetadataRetriever.OPTION_CLOSEST);
                        frame1 = new Mat();
                        Utils.bitmapToMat(bArray, frame1);
                        Log.d("Main6Activity", "Gone");
                        frame2 = new Mat();
                        frame2=CameraFrame(frame1);
                        Log.d("Main6Activity", "CameBack");
                        if (frame2.empty()) {
                            Log.d("Main6Activity", "Nooo");
                        }
                        //Imgproc.cvtColor(frame1, frame1, Imgproc.COLOR_RGB2GRAY, 4);
                        bmp = Bitmap.createBitmap(frame2.cols(), frame2.rows(), Bitmap.Config.ARGB_8888);
                        int y=bmp.getWidth();
                        int y1= bmp.getHeight();
                        int y2=frame2.rows();
                        int y3= frame2.cols();

                        Log.d("Main6Activity", "BMP Width "+ String.format("%d",y));
                        Log.d("Main6Activity", "BMP Height "+ String.format("%d",y1));
                        Log.d("Main6Activity", "Frame Width "+ String.format("%d",y3));
                        Log.d("Main6Activity", "Frame Height "+ String.format("%d",y2));

                        Utils.matToBitmap(frame2, bmp);
                        //imageview.setImageBitmap(bmp);
                        Drawable drawable = new BitmapDrawable(getResources(), bmp);
                        imageview.setImageDrawable(drawable);// Displaying every frame
                       // In microsecond, As frames are stored in every 0.034483 second as frame rate= 29fps

*/}
                    }catch (IllegalArgumentException ex) {
                        ex.printStackTrace();}

                    //else
                    //{Log.d("Main6Activity", "NOOOOOOOO");}
                    /*int l= 34483; //In microsecond, As frames are stored in every 0.034483 second as frame rate= 29fps.

                    for(int t=0; t<21 ;t++) { ///Duration of the video
                        Log.d("Main6Activity", "Going");
                        //since med has to be calculated in microseconds, we multiply by 1000000
                        // The Below loop gives output for every 1 second
                        for (int t1 = 0; t1 < 30; t1++) { // Number of frames/sec=29
                            Bitmap bArray = med.getFrameAtTime(l, FFmpegMediaMetadataRetriever.OPTION_CLOSEST);
                            frame1 = new Mat();
                            Utils.bitmapToMat(bArray, frame1);
                            Log.d("Main6Activity", "Gone");
                            CameraFrame(frame1);
                            Log.d("Main6Activity", "CameBack");
                            if (frame1.empty()) {
                                Log.d("Main6Activity", "Nooo");
                            }
                            Imgproc.cvtColor(frame1, frame1, Imgproc.COLOR_RGB2GRAY, 4);
                            bmp = Bitmap.createBitmap(frame1.cols(), frame1.rows(), Bitmap.Config.ARGB_8888);
                            ;
                            Utils.matToBitmap(frame1, bmp);
                            //imageview.setImageBitmap(bmp);
                            Drawable drawable = new BitmapDrawable(getResources(), bmp);
                            imageview.setImageDrawable(drawable);// Displaying every frame
                            l=l+34483; // In microsecond, As frames are stored in every 0.034483 second as frame rate= 29fps

                        }
                    }*/

                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    try {
                        med.release();
                    } catch (RuntimeException ex) {
                    }
                }

            }

        }
    }

    public Mat CameraFrame(Mat frame1) {


        ArrayList<Point> lp2 = new ArrayList<>();
        ArrayList<Point> lp4 = new ArrayList<>();
        keyPoints = new MatOfKeyPoint();

        mRgba = frame1;
        if(frame1.empty())// Input Frame to RGB
            Log.d("Main6Activity", "\nFrame1 is Empty\n");
        mRgba=mRgba.t();
        Core.flip(mRgba,mRgba,1); // Making frame aligned in the right orientation

        Mask= Mat.ones(mRgba.rows(),mRgba.cols(), CvType.CV_8UC3);

        if (mGrayMat1.empty()) {

            Imgproc.cvtColor(mRgba, mGrayMat, Imgproc.COLOR_RGBA2GRAY);// Converting RBG to Gray
            featureDetector.detect(mGrayMat, keyPoints); // Detecting Keypoints

            KeyPoint[] tkp = keyPoints.toArray();// Converting keypoints into an Array
            int u = tkp.length; // Checking if length!=0
            Log.d("Main5Activity", "Array int" + String.format("%d", u));


            ArrayList<Point> lp1 = new ArrayList<>(); // Creating a List of <Point>

            /*Point[] lp1 = new Point[tkp.length];*/
            for (int i = 0; i < tkp.length; i++) {

                lp1.add(tkp[i].pt); // Adding keypoints to the List
            }

            pointsPrev.fromList(lp1); // Adding every element of List to Point2f

            for (int d = 0; d < tkp.length; d++) {
                Imgproc.circle(mRgba, pointsPrev.toList().get(d), 3, new Scalar(255, 0, 0),-1, 8, 0 );
            } // Drawing the initial keypoints of the first frame

            //Img= new Mat();
            //Size y= mGrayMat.size();
            //Size z= Mask.size();
            //Log.d("Main5Activity", "Channels1:" + String.format("%s", y));
            //Log.d("Main5Activity", "Channnels2:" + String.format("%s", z));
            //Core.add(mRgba,Mask,Img);

        }
        else {// After the first frame gives output, this 'else' function continues for every-other subsequent frame

            if (frames%6==0) { //If keypoints are reduced, introduce more keypoints
                //Imgproc.cvtColor(mRgba, mGrayMat2, Imgproc.COLOR_RGBA2GRAY);
                keyPoints2= new MatOfKeyPoint();
                featureDetector.detect(mGrayMat1, keyPoints2);


                KeyPoint[] tkp2;
                tkp2 = keyPoints2.toArray();
                pointsPrev1= new MatOfPoint2f();

                ArrayList<Point> lp3 = new ArrayList<>();


            /*Point[] lp1 = new Point[tkp.length];*/
                for (int i1 = 0; i1 < tkp2.length; i1++) {

                    lp3.add(tkp2[i1].pt);

                }

                pointsPrev1.fromList(lp3);
                pointsThis.push_back(pointsPrev1);


            }
            ArrayList<Point> lp5 = new ArrayList<>();

            long o1= pointsThis.total();
            int o  = (int) (long) o1;
            //int q1=0;
            if(o>500) { // If total number of points become > 500, try to make it below.
                for(int h=0;h<500; h++)
                { pointsPrev2= new MatOfPoint2f();
                    lp5.add(h,pointsThis.toList().get(h));
                    //q1++;

                }
                pointsThis= new MatOfPoint2f();
                pointsThis.fromList(lp5);
            }

            TermCriteria optical_flow_termination_criteria = new TermCriteria();//=(TermCriteria.MAX_ITER|TermCriteria.EPS,20,.3);//  ( CV_TERMCRIT_ITER | CV_TERMCRIT_EPS, 20, .3 );
            optical_flow_termination_criteria.epsilon = .03;
            optical_flow_termination_criteria.maxCount = 10;
            mGrayMat= new Mat();

            Imgproc.cvtColor(mRgba, mGrayMat, Imgproc.COLOR_RGBA2GRAY);

            pointsThis.convertTo(pointsThis, CvType.CV_32F);
            mGrayMat1.convertTo(mGrayMat1, CvType.CV_8UC1);
            mGrayMat.convertTo(mGrayMat, CvType.CV_8UC1);

            if(mGrayMat.empty())
                Log.d("Main6Activity", "\nIt is Empty\n");
            pointsPrev= new MatOfPoint2f();
            // In the below function, mGrayMat1 is the previous frame, mGrayMat is the current frame
            // pointsThis are those points of the previous frame, pointsPrev are those points to be calculated for the current frame
            if(pointsThis.empty())// Input Frame to RGB
                Log.d("Main6Activity", "\nPointsthis is Empty\n");

            int y21=mGrayMat1.rows();
            int y31= mGrayMat1.cols();
            Log.d("Main6Activity", "GRAY Width "+ String.format("%d",y21));
            Log.d("Main6Activity", "GRAY Height "+ String.format("%d",y31));

            Video.calcOpticalFlowPyrLK(mGrayMat1, mGrayMat, pointsThis, pointsPrev, status, err, new Size(20, 20), 3, optical_flow_termination_criteria, 0, 0.001);// Optical Flow
            int k, m,l,l1;

            for (int j = l=l1= k = m = 0; j < pointsPrev.total(); j++) {
                // We have to check if status[i] exists, ie, if the corresponding points of the previous frames were detected in the current frame
                if (status.toList().get(j) == 1) {
                    pointsPrev.toList().set(k, pointsPrev.toList().get(j));
                    pointsThis.toList().set(m, pointsThis.toList().get(j));
                    lp2.add(l, pointsPrev.toList().get(k));
                    lp4.add(l1, pointsThis.toList().get(m));
                    if (pointsPrev.empty()) {
                        Log.d("Main5Activity", "PointsPrevINITIALEMPTYYYYY");
                    }
                    Imgproc.circle(mGrayMat, pointsPrev.toList().get(m), 3, new Scalar(255, 0, 0),-1, 8, 0 );
                    //Imgproc.line(Mask, pointsThis.toList().get(k),pointsPrev.toList().get(k), new Scalar(255,0,0),2);
                    Img= new Mat();
                    //            Core.add(mGrayMat,Mask,Img);
                    k++;
                    m++;
                    l++;
                    l1++;

                }

            }

            Log.d("Main5Activity", "K1" + String.format("%d", k));
            if (pointsPrev.empty()) {
                Log.d("Main5Activity", "PointsPrevEMPTYYYYY");
            }
            int q = lp2.size();
            Log.d("Main5Activity", "K2" + String.format("%d", q));

            pointsPrev = new MatOfPoint2f();
            pointsPrev.fromList(lp2);
            pointsThis = new MatOfPoint2f();
            pointsThis.fromList(lp4);

            for(int y=0; y<pointsThis.total(); y++)
            { Imgproc.line(mRgba, pointsThis.toList().get(y),pointsPrev.toList().get(y), new Scalar(255,0,0),2);
            }
            //Img= new Mat();
            //Core.add(mRgba,Mask,Img);
        }

        pointsThis = new MatOfPoint2f();
        pointsPrev.copyTo(pointsThis);
        if (pointsThis.empty())
            Log.d("Main5Activity", "EMPTYYYYY");


        pointsPrev = new MatOfPoint2f();

        //mGrayMat.copyTo(mGrayMat1);
        mGrayMat1= mGrayMat.clone();
        frames++;
        if(mGrayMat.empty())
        { Log.d("Main5Activity", "Heelllsno");}

            /*Drawable[] layers = new Drawable[2];
            layers[0] = ResourcesCompat.getDrawable(R.drawable.);
            layers[1] = ResourcesCompat.getDrawable(R.drawable.tt);
            LayerDrawable layerDrawable = new LayerDrawable(layers);
            img.setImageDrawable(layerDrawable);*/

        /*Bitmap result = Bitmap.createBitmap(mRgba.width(), mRgba.height(), mRgba.getc());
             Canvas canvas = new Canvas();
             canvas.drawBitmap(mRgba);
             canvas.drawBitmap(secondImage, 10, 10, null);
        return result;*/

        Log.d("Main5Activity", "Frame" + String.format("%d", frames));
        return mRgba;


    }
}
