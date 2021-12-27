package com.facedetect;

import android.Manifest;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Base64;

import androidx.appcompat.app.AppCompatActivity;

import com.facedetect.permission.IManager;
import com.facedetect.permission.PermissionsManager;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.facedetect.DetectionBasedTracker;
import org.opencv.imgproc.Imgproc;

import java.io.ByteArrayOutputStream;

/**
 * @author fan.jiang
 */
public class MainActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {

    private JavaCameraView cameraView;
    private int mAbsoluteFaceSize = 0;
    /**
     * r g b a
     */
    private static final Scalar FACE_RECT_COLOR = new Scalar(0, 255, 0, 255);

    private Mat mRgba;
    private Mat mGray;
    private DetectionBasedTracker mNativeDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cameraView = findViewById(R.id.camera_view);

        cameraView.setVisibility(CameraBridgeViewBase.VISIBLE);
        cameraView.setCvCameraViewListener(this);

        PermissionsManager.requestPermission(this, Manifest.permission.CAMERA, new IManager.IPermission() {
            @Override
            public void accede() {
                cameraView.setCameraPermissionGranted();

                if (OpenCVLoader.initDebug()) {
                    mNativeDetector = new DetectionBasedTracker(MainActivity.this, 0);
                    cameraView.enableView();
                }
            }

            @Override
            public void reject() {

            }
        });


    }

    @Override
    public void onCameraViewStarted(int i, int i1) {
        mGray = new Mat();
        mRgba = new Mat();
    }

    @Override
    public void onCameraViewStopped() {
        mGray.release();
        mRgba.release();
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame cvCameraViewFrame) {
        mRgba = cvCameraViewFrame.rgba();
        mGray = cvCameraViewFrame.gray();

        if (mAbsoluteFaceSize == 0) {
            int height = mGray.rows();
            float mRelativeFaceSize = 0.2f;
            if (Math.round(height * mRelativeFaceSize) > 0) {
                mAbsoluteFaceSize = Math.round(height * mRelativeFaceSize);
            }
            mNativeDetector.setMinFaceSize(mAbsoluteFaceSize);
        }
        MatOfRect faces = new MatOfRect();

        if (mNativeDetector != null) {
            mNativeDetector.detect(mGray, faces);
        }

        Rect[] facesArray = faces.toArray();
        for (int i = 0; i < facesArray.length; i++) {
            Imgproc.rectangle(mRgba, facesArray[i].tl(), facesArray[i].br(), FACE_RECT_COLOR, 1);
        }

        return mRgba;
    }

    /**
     * mat2base64
     *
     * @return
     */
    private String mat2Base64(Mat mRgba) {
        if (mRgba != null) {
            try {
                Bitmap bitmap = Bitmap.createBitmap(mRgba.width(), mRgba.height(), Bitmap.Config.ARGB_8888);
                Utils.matToBitmap(mRgba, bitmap);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
                bitmap.recycle();
                byte[] data = baos.toByteArray();
                return Base64.encodeToString(data, Base64.DEFAULT);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraView.disableView();
    }
}