package com.example.owner.eee508opencvtutorial; // Motion Detection through ORB keypoints

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;
import android.widget.VideoView;
import wseemann.media.FFmpegMediaMetadataRetriever;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
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
import org.opencv.features2d.ORB;
import org.opencv.imgproc.Imgproc;
import org.opencv.video.Video;

import java.util.ArrayList;

import static android.media.MediaRecorder.VideoSource.CAMERA;
import static org.opencv.features2d.ORB.HARRIS_SCORE;


public class Main5Activity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {

    public Mat mRgba;
    public Mat mGrayMat;
    public Mat mGrayMat1;
    public Mat mGrayMat2;
    public Mat Mask;
    public Mat Img;

    public int frames;
    private CameraBridgeViewBase mOpenCvCameraView;

    public FeatureDetector featureDetector;
    public MatOfKeyPoint keyPoints;
    public MatOfKeyPoint keyPoints2;
    public MatOfPoint2f pointsPrev;
    public MatOfPoint2f pointsThis;
    public MatOfPoint2f pointsPrev1;
    public MatOfPoint2f pointsPrev2;

    MatOfByte status;
    MatOfFloat err;

    public static final int PICK_VIDEO = 1;
    public int count=0;
    @Override
    protected void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main5);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main5);

        mOpenCvCameraView= (CameraBridgeViewBase) findViewById(R.id.opencv_tutorial_activity_surface_view);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);


    }


    private void resetVars() {
        keyPoints = new MatOfKeyPoint();
        keyPoints2 = new MatOfKeyPoint();
        pointsPrev= new MatOfPoint2f();
        pointsThis= new MatOfPoint2f();
         status = new MatOfByte();
         err = new MatOfFloat();
    }



    @Override
    public void onPause() {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    private BaseLoaderCallback mLoaderCallback= new BaseLoaderCallback(this) {

        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    Log.i("Main5Activity", "OpenCV loaded successfully");

                    mOpenCvCameraView.enableView();



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
    public void onCameraViewStarted(int width, int height) {

        mRgba = new Mat();
        mGrayMat = new Mat();
        mGrayMat1 = new Mat();
        mGrayMat2 = new Mat();
        Img= new Mat();
        frames=0;
        int nfeatures=500,nlevels=8,edgeThreshold=10,firstLevel=0,WTA_K=3;
        int patchSize=15,fastThreshold=10;
        float scaleFactor=2.0f;
        //ORB.create(nfeatures,scaleFactor,nlevels,edgeThreshold,firstLevel,WTA_K,HARRIS_SCORE,patchSize,fastThreshold);// Orb Detection
        featureDetector = FeatureDetector.create(FeatureDetector.ORB);
        resetVars();

    }

    @Override
    public void onCameraViewStopped() {
        mRgba.release();
        mGrayMat.release();
        mGrayMat1.release();
        mGrayMat2.release();

    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {


        ArrayList<Point> lp2 = new ArrayList<>();
        ArrayList<Point> lp4 = new ArrayList<>();
        keyPoints = new MatOfKeyPoint();

        mRgba = inputFrame.rgba(); // Input Frame to RGB
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
            Imgproc.cvtColor(mRgba, mGrayMat, Imgproc.COLOR_RGBA2GRAY);
            pointsThis.convertTo(pointsThis, CvType.CV_32F);
            mGrayMat1.convertTo(mGrayMat1, CvType.CV_8UC1);
            mGrayMat.convertTo(mGrayMat, CvType.CV_8UC1);
            // In the below function, mGrayMat1 is the previous frame, mGrayMat is the current frame
            // pointsThis are those points of the previous frame, pointsPrev are those points to be calculated for the current frame
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
