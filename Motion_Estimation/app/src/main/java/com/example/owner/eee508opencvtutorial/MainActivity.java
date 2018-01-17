package com.example.owner.eee508opencvtutorial;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.widget.TextView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;


public class MainActivity extends AppCompatActivity implements OnTouchListener,CvCameraViewListener2 {
    private CameraBridgeViewBase mOpenCvCameraView;
    private Scalar mBlobcolorRGBa;
    private Scalar mBlobcolorHSV;
    private Mat mRGBa;
    double x = -1;
    double y = -1;

    TextView touch_coordinates;
    TextView touch_color;
    private BaseLoaderCallback mLoaderCallback= new BaseLoaderCallback(this) {

        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    mOpenCvCameraView.enableView();
                    mOpenCvCameraView.setOnTouchListener(MainActivity.this);
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
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        touch_coordinates = (TextView) findViewById(R.id.touch_coordinates);
        touch_color = (TextView) findViewById(R.id.touch_color);
        mOpenCvCameraView= (CameraBridgeViewBase) findViewById(R.id.opencv_tutorial_activity_surface_view);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);

    }

    @Override
    public void onPause() {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    public void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug())
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        else
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int cols= mRGBa.cols();
        int rows= mRGBa.rows();
        double ylow = (double)mOpenCvCameraView.getHeight() * 0.2401961;
        double yhigh= (double)mOpenCvCameraView.getHeight() * 0.7696078;
        double xscale= (double)cols/(double)mOpenCvCameraView.getWidth();
        double yscale= (double)rows/(yhigh - ylow);
        x=event.getX();
        y=event.getY();
        y=y-ylow;
        x=x*xscale;
        y=y*yscale;
        if((x<0)||(y<0)||(x>cols)||(y>rows)) return false;
        touch_coordinates.setText("X: " + x + ",Y: " + y );

        Rect touchedRect = new Rect();
        touchedRect.x= (int)x;
        touchedRect.y= (int)y;
        touchedRect.width= 8;
        touchedRect.height= 8;

        Mat touchedRegionRgba = mRGBa.submat(touchedRect);
        Mat touchedRegionHsv = new Mat();
        Imgproc.cvtColor(touchedRegionRgba,touchedRegionHsv,Imgproc.COLOR_RGB2HSV_FULL);
        Imgproc.cvtColor(touchedRegionRgba,touchedRegionHsv,Imgproc.COLOR_RGB2HSV_FULL);

        mBlobcolorHSV= Core.sumElems(touchedRegionHsv);
        int pointcount = touchedRect.width * touchedRect.height;
        for( int i=0; i< mBlobcolorHSV.val.length; i++)
            mBlobcolorHSV.val[i] /= pointcount;

        mBlobcolorRGBa= convertScalarHsv2Rgba(mBlobcolorHSV);

        touch_color.setText("Color: #" + String.format("%02X", (int)mBlobcolorRGBa.val[0])+ String.format("%02X", (int)mBlobcolorRGBa.val[1]) + String.format("%02X", (int)mBlobcolorRGBa.val[2]));
        touch_color.setTextColor(Color.rgb((int)mBlobcolorRGBa.val[0],(int)mBlobcolorRGBa.val[1],(int)mBlobcolorRGBa.val[2]));


        return false;

    }

    private Scalar convertScalarHsv2Rgba(Scalar hsvcolor)
    {
        Mat pointMatRgba = new Mat();
        Mat pointMatHsv = new Mat(1,1, CvType.CV_8UC3, hsvcolor);
        Imgproc.cvtColor(pointMatHsv,pointMatRgba,Imgproc.COLOR_HSV2RGB_FULL,4);

        return new Scalar(pointMatRgba.get(0,0));
    }



    @Override
    public void onCameraViewStarted(int width, int height) {
        mRGBa= new Mat();
        mBlobcolorRGBa= new Scalar(255);
        mBlobcolorHSV = new Scalar(255);


    }

    @Override
    public void onCameraViewStopped() {
        mRGBa.release();

    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        mRGBa= inputFrame.rgba();
        mRGBa=mRGBa.t();
        Core.flip(mRGBa,mRGBa,1);

        return mRGBa;
    }






}
