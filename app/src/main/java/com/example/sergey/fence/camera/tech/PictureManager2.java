package com.example.sergey.fence.camera.tech;

/**
 * Created by Sergey on 08.10.2017.
 */


        import android.app.Activity;
        import android.content.Context;
        import android.content.pm.PackageManager;
        import android.graphics.ImageFormat;
        import android.hardware.camera2.CameraAccessException;
        import android.hardware.camera2.CameraCaptureSession;
        import android.hardware.camera2.CameraDevice;
        import android.hardware.camera2.CameraManager;
        import android.hardware.camera2.CameraMetadata;
        import android.hardware.camera2.CaptureRequest;
        import android.hardware.camera2.CaptureResult;
        import android.hardware.camera2.TotalCaptureResult;
        import android.media.Image;
        import android.media.ImageReader;
        import android.support.annotation.NonNull;
        import android.util.Log;
        import android.view.Surface;
        import java.nio.ByteBuffer;
        import java.util.ArrayList;
        import java.util.List;



public class PictureManager2 {


    private final String TAG = PictureManager2.class.getSimpleName(); // "Terskikh";


    /**
     * Check if this device has a camera
     */
    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }


    public String[] getCameraList(Context context) {
        CameraManager manager = getCameraManager(context);
        try {
            return manager.getCameraIdList();
        } catch (Exception e) {
            Log.e(TAG, "getCameraList ", e);
        }
        return new String[0]; // returns null if camera list is unavailable
    }

    public boolean makePictures(Activity context) {
        Log.i(TAG, "makePictures ");
        if (!checkCameraHardware(context))
            return false;

        String[] cameraList = getCameraList(context);

        //for (int index = 0; index < cameraList.length; ++index)
        {
            makePicture(context, cameraList[0]);
            //makePicture(context, cameraList[1]);
        }
        return true;
    }

    private CameraManager getCameraManager(Context context) {
        return (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
    }

    public boolean makePicture(Activity context, String cameraId) {
        Log.i(TAG, "makePicture " +cameraId);
        if (!checkCameraHardware(context))
            return false;
        stopFoto = false;
        imageReady = false;
        CameraManager manager = getCameraManager(context);
        try {

            manager.openCamera(cameraId, deviceCallback, null /*backgroundHandler*/);
        } catch (CameraAccessException | SecurityException exc) {
            Log.e(TAG, "couldn't open camera: " + cameraId, exc);
        }
        catch (Exception exc) {
            Log.e(TAG, "couldn't open camera: " + cameraId, exc);
        }
        return true;
    }

    CameraDevice.StateCallback deviceCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(CameraDevice camera) {
            Log.i(TAG, "deviceCallback.onOpened() start");
            startCamera(camera);
        }

        @Override
        public void onDisconnected(CameraDevice camera) {
            Log.i(TAG, "deviceCallback.onDisconnected() start");
        }

        @Override
        public void onError(CameraDevice camera, int error) {
            Log.i(TAG, "deviceCallback.onError() start");
        }
    };
    CameraCaptureSession.CaptureCallback mCaptureCallback;
    void  startCamera(final CameraDevice cameraDevice)
    {
        if(cameraDevice==null)
        {
            Log.i(TAG, "startCamera: cameraDevice==null");
            return;
        }
        try
        {
            Log.i(TAG, "createCaptureSession start");
            final ImageReader reader = ImageReader.newInstance(1080, 1920, ImageFormat.JPEG, 1);
            List<Surface> outputSurfaces = new ArrayList<Surface>(1);
            outputSurfaces.add(reader.getSurface());
            ImageReader.OnImageAvailableListener imageAvailableListener = new ImageReader.OnImageAvailableListener() {
                @Override
                public void onImageAvailable(ImageReader reader) {
//                    if(!imageReady)
//                        return;
//                    stopFoto = true;
                    Log.i(TAG, "onImageAvailable start");
                    Image image = null;
                    try {
                        image = reader.acquireLatestImage();
                        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                        byte[] bytes = new byte[buffer.capacity()];
                        buffer.get(bytes);
                        ImageSaver saver  = new ImageSaver("MyCameraApp");
                        saver.save(bytes);
                    } catch (Exception ee) {
                        Log.e(TAG, "image Failed ", ee);
                    } finally {
                        if (image != null)
                            image.close();
                    }
                }
            };
            reader.setOnImageAvailableListener(imageAvailableListener, null);
            cameraDevice.createCaptureSession(outputSurfaces , new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(final CameraCaptureSession session) {
                    Log.i(TAG, "onConfigured start");
                    try {
                        final CaptureRequest.Builder captureBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW); // TEMPLATE_STILL_CAPTURE, TEMPLATE_PREVIEW
                        captureBuilder.addTarget(reader.getSurface());

                        mCaptureCallback  = new CameraCaptureSession.CaptureCallback() {

                            private void process(CaptureResult result) {
                                if(stopFoto)
                                    return;
                                Log.i(TAG, "process start");

                                Integer afState = result.get(CaptureResult.CONTROL_AF_STATE);
                                Integer aeState = result.get(CaptureResult.CONTROL_AE_STATE);
                                Log.i(TAG, "afState = " + afState);
                                Log.i(TAG, "aeState = " + aeState);
                                if (afState == null) //  || afState == 0
                                {
                                    takePicture(cameraDevice, session,  captureBuilder);
                                } else if ((CaptureResult.CONTROL_AF_STATE_FOCUSED_LOCKED == afState ||
                                        CaptureResult.CONTROL_AF_STATE_NOT_FOCUSED_LOCKED == afState ||
                                        CaptureResult.CONTROL_AF_STATE_INACTIVE == afState) && ((aeState == null ||
                                        aeState == CaptureResult.CONTROL_AE_STATE_CONVERGED))) {
                                    takePicture(cameraDevice, session, captureBuilder);
                                } else {
                                    runPrecaptureSequence();
                                }
                            }
                            private void runPrecaptureSequence() {
                                if(stopFoto)
                                    return;
                                Log.i(TAG, "runPrecaptureSequence start: " + mCaptureCallback);
                                try {
                                    // This is how to tell the camera to trigger.
//                                    captureBuilder.set(CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER,
//                                            CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER_START);
                                    //captureBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, CameraMetadata.CONTROL_AF_TRIGGER_START);

                                    configureCaprureBuilder(captureBuilder);
                                    // Tell #mCaptureCallback to wait for the precapture sequence to be set.
                                    session.capture(captureBuilder.build(), mCaptureCallback,
                                            null);
                                } catch (Exception e) {
                                    Log.e(TAG, "runPrecaptureSequence failed", e);
                                }
                            }
                            @Override
                            public void onCaptureProgressed(@NonNull CameraCaptureSession session,
                                                            @NonNull CaptureRequest request,
                                                            @NonNull CaptureResult partialResult) {
                                Log.i(TAG, "onCaptureProgressed start");
                                //process(partialResult);
                            }

                            @Override
                            public void onCaptureCompleted(@NonNull CameraCaptureSession session,
                                                           @NonNull CaptureRequest request,
                                                           @NonNull TotalCaptureResult result) {
                                Log.i(TAG, "onCaptureCompleted start");
                                process(result);
                            }
                        };

                        configureCaprureBuilder(captureBuilder);

                        session.capture(captureBuilder.build(), mCaptureCallback/* captureListener*/, null/* mBackgroundHandler*/);
                    }
                    catch(Exception exc){Log.e(TAG, "capture Failed ", exc);}
                }

                @Override
                public void onConfigureFailed(CameraCaptureSession session) {
                    Log.e(TAG, "onConfigureFailed ");
                }
            }, null);

        }catch (Exception e)
        {
            Log.e(TAG, "startCamera failed", e);
        }
    }
    void configureCaprureBuilder(CaptureRequest.Builder captureBuilder)
    {
        captureBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
        captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, 90);
        captureBuilder.set(CaptureRequest.CONTROL_AF_MODE,
                CameraMetadata.CONTROL_AF_MODE_AUTO);
        captureBuilder.set(CaptureRequest.CONTROL_AE_MODE,
                CameraMetadata.CONTROL_AE_MODE_ON_AUTO_FLASH);
        captureBuilder.set(CaptureRequest.CONTROL_AWB_MODE,
                CameraMetadata.CONTROL_AWB_MODE_AUTO);
        captureBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, CameraMetadata.CONTROL_AF_TRIGGER_START);
        captureBuilder.set(CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER, CameraMetadata.CONTROL_AE_PRECAPTURE_TRIGGER_START);
    }

    void takePicture(final CameraDevice cameraDevice, CameraCaptureSession session, CaptureRequest.Builder captureBuilder)
    {
        Log.i(TAG, "takePicture start");
        imageReady = true;
        if(stopFoto)
            return;
        stopFoto = true;
        try {
            Log.i(TAG, "takePicture capture");
            configureCaprureBuilder(captureBuilder);

            session.capture(captureBuilder.build(), null/* captureListener*/, null/* mBackgroundHandler*/);
        }
        catch(Exception exc){Log.e(TAG, "capture Failed ", exc);}
        session.close();
        cameraDevice.close();
    }
    boolean stopFoto = false;
    boolean imageReady = false;

}
