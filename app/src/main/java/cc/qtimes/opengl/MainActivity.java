package cc.qtimes.opengl;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import cc.qtimes.opengl.glhelper.TextureProgram;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_FRAMEBUFFER;
import static android.opengl.GLES20.glBindFramebuffer;
import static android.opengl.GLES20.glViewport;

public class MainActivity extends AppCompatActivity implements Camera.PreviewCallback {

    public SurfaceTexture mSurfaceTexture;
    private TextureProgram mTextureProgram;
    public static Camera mCamera;
    GLSurfaceView mGLSurfaceView;
    private int mOESTextureId, mPreviewTextureId = -1;
    private static final int WIDTH = 1280, HEIGHT = 720;
    private Object cameraLock = new Object();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mGLSurfaceView = findViewById(R.id.glsv);
        //配置OpenGL ES，主要是版本设置和设置Renderer，Renderer用于执行OpenGL的绘制
        mGLSurfaceView.setEGLContextClientVersion(2);
        mGLSurfaceView.setRenderer(new GLSurfaceView.Renderer() {
            @Override
            public void onSurfaceCreated(GL10 gl, EGLConfig config) {
                startCamera();
                initSurfaceTexture();
            }

            @Override
            public void onSurfaceChanged(GL10 gl, int width, int height) {

            }

            @Override
            public void onDrawFrame(GL10 gl) {
                glBindFramebuffer(GL_FRAMEBUFFER, 0);
                glViewport(0, 0, WIDTH, HEIGHT);
                mTextureProgram.draw(mPreviewTextureId);
                Log.i("ooooooooo", "tid: " + mPreviewTextureId);
                mSurfaceTexture.updateTexImage();
            }
        });
    }

    //在onDrawFrame方法中调用此方法
    public void initSurfaceTexture() {
        mOESTextureId = createOESTextureObject();
        //根据外部纹理ID创建SurfaceTexture
        mSurfaceTexture = new SurfaceTexture(0);
        mTextureProgram = new TextureProgram(this);
        mSurfaceTexture.setOnFrameAvailableListener(new SurfaceTexture.OnFrameAvailableListener() {
            @Override
            public void onFrameAvailable(SurfaceTexture surfaceTexture) {
                //每获取到一帧数据时请求OpenGL ES进行渲染
                mGLSurfaceView.requestRender();
            }
        });
        //讲此SurfaceTexture作为相机预览输出
        try {
            mCamera.setPreviewTexture(mSurfaceTexture);
            mCamera.setPreviewCallback(this);
            //开启预览
            mCamera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static int createOESTextureObject() {
        int[] tex = new int[1];
        //生成一个纹理
        GLES20.glGenTextures(1, tex, 0);
        //将此纹理绑定到外部纹理上
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, tex[0]);
        //设置纹理过滤参数
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);
        //解除纹理绑定
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0);
        return tex[0];
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        int[] rgbBytes = new int[WIDTH * HEIGHT];
        Bitmap rgbFrameBitmap = Bitmap.createBitmap(WIDTH, HEIGHT, Bitmap.Config.ARGB_8888);
        ImageUtils.convertYUV420SPToARGB8888(data, WIDTH, HEIGHT, rgbBytes);
        rgbFrameBitmap.setPixels(rgbBytes, 0, WIDTH, 0, 0, WIDTH, HEIGHT);
        mPreviewTextureId = createTextureObjectFromBitmap(rgbFrameBitmap);
        Log.i("ID", "mPreviewTextureId: " + mPreviewTextureId);
    }

    public static int createTextureObjectFromBitmap(Bitmap bitmap) {
        int[] texture = new int[1];
        if (bitmap != null && !bitmap.isRecycled()) {
            GLES20.glGenTextures(1, texture, 0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture[0]);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
            //根据以上指定的参数，生成一个2D纹理
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
            return texture[0];
        }
        return -1;
    }

    private void startCamera() {
        if (mCamera != null) {
            return;
        }

        synchronized (cameraLock) {
            Camera.CameraInfo camInfo = new Camera.CameraInfo();

            int numCameras = Camera.getNumberOfCameras();
            for (int i = 0; i < numCameras; i++) {
                Camera.getCameraInfo(i, camInfo);
                if (camInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                    mCamera = Camera.open(i);
                    break;
                }
            }

            if (mCamera == null) {
                throw new RuntimeException("Unable to open camera");
            }

            Camera.Parameters camParams = mCamera.getParameters();

            List<Camera.Size> sizes = camParams.getSupportedPreviewSizes();
            for (int i = 0; i < sizes.size(); i++) {
                Camera.Size size = sizes.get(i);
                Log.v("MainActivity", "Camera Supported Preview Size = " + size.width + "x" + size.height);
            }

            camParams.setPreviewSize(WIDTH, HEIGHT);
            camParams.setRecordingHint(true);

            mCamera.setParameters(camParams);

            if (mSurfaceTexture != null) {
                try {
                    mCamera.setPreviewTexture(mSurfaceTexture);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                mCamera.startPreview();
            }
        }
    }
}
