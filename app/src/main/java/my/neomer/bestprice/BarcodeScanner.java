package my.neomer.bestprice;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.support.v4.app.ActivityCompat;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.accessibility.AccessibilityEvent;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

public class BarcodeScanner extends SurfaceView implements SurfaceHolder.Callback  {


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        cameraRenderer.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        cameraRenderer.terminate();
    }

    private CameraRenderer cameraRenderer;

    public BarcodeScanner(Context context) {
        super(context);

        createCameraSource(context);
        cameraRenderer = new CameraRenderer(this);

        cameraRenderer.start();
    }

    public BarcodeDetector getBarcodeDetector() {
        return barcodeDetector;
    }

    public CameraSource getCameraSource() {
        return cameraSource;
    }

    private CameraSource cameraSource;
    private BarcodeDetector barcodeDetector;

    private void createCameraSource(Context context) {
        // set preferred mBarcodeTypes before this :)
        barcodeDetector = new BarcodeDetector.Builder(context)
                .setBarcodeFormats(Barcode.DATA_MATRIX | Barcode.ALL_FORMATS)
                .build();

        // Creates and starts the camera.  Note that this uses a higher resolution in comparison
        // to other detection examples to enable the barcode detector to detect small barcodes
        // at long distances.
        cameraSource = new CameraSource.Builder(context, barcodeDetector)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedPreviewSize(1600, 900)
                .setRequestedFps(15.0f)
                .build();
    }

    private class CameraRenderer extends Thread {

        private BarcodeScanner barcodeScanner;
        private volatile boolean bRun;

        public CameraRenderer(BarcodeScanner barcodeScanner) {
            this.barcodeScanner = barcodeScanner;
            bRun = false;
        }

        @Override
        public synchronized void start() {
            bRun = true;
            super.start();
        }

        public synchronized void terminate() {
            bRun = false;
        }

        @Override
        public void run() {

            while (bRun) {
                SurfaceHolder holder = null;

                synchronized (getHolder()) {
                    if (ActivityCompat.checkSelfPermission(barcodeScanner.getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    try {
                        barcodeScanner.getCameraSource().start(getHolder());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
