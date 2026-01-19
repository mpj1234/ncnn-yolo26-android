/**
 * @author mpj
 * @date 2026/1/19 21:18
 * @version V1.0
 * @since jdk1.8
 **/
package com.mpj.mpjyolo26;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.mpj.mpj_yolo26.MpjYolo26;
import com.mpj.mpjyolo26.databinding.ActivityMainBinding;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback {
    private static final String TAG = "MainActivity";

    private static final String[] classLabels = {
            "person", "bicycle", "car", "motorcycle", "airplane", "bus", "train", "truck", "boat", "traffic light",
            "fire hydrant", "stop sign", "parking meter", "bench", "bird", "cat", "dog", "horse", "sheep", "cow",
            "elephant", "bear", "zebra", "giraffe", "backpack", "umbrella", "handbag", "tie", "suitcase", "frisbee",
            "skis", "snowboard", "sports ball", "kite", "baseball bat", "baseball glove", "skateboard", "surfboard",
            "tennis racket", "bottle", "wine glass", "cup", "fork", "knife", "spoon", "bowl", "banana", "apple",
            "sandwich", "orange", "broccoli", "carrot", "hot dog", "pizza", "donut", "cake", "chair", "couch",
            "potted plant", "bed", "dining table", "toilet", "tv", "laptop", "mouse", "remote", "keyboard",
            "cell phone", "microwave", "oven", "toaster", "sink", "refrigerator", "book", "clock", "vase",
            "scissors", "teddy bear", "hair drier", "toothbrush"
    };

    public static final int REQUEST_CAMERA = 100;

    private int facing = 0;
    private int current_model = 0;
    private int current_cpugpu = 0;
    private SurfaceView cameraView = null;
    /**
     * true 启动检测
     * false 关闭检测
     */
    boolean stopStart = false;

    private int targetSize = 320;
    private float probThreshold = 0.25f;
    private final HashMap<Integer, String> modelMap = new HashMap<Integer, String>() {
        {
            put(0, "yolo26n");
            put(1, "yolo26s");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        cameraView = findViewById(R.id.cameraview);
        cameraView.getHolder().setFormat(PixelFormat.RGBA_8888);
        cameraView.getHolder().addCallback(this);

        Button buttonSwitchCamera = findViewById(R.id.buttonSwitchCamera);
        buttonSwitchCamera.setOnClickListener(arg0 -> {
            int new_facing = 1 - facing;

            MpjYolo26.closeCamera();

            MpjYolo26.openCamera(new_facing);
            stopStart = true;

            facing = new_facing;

            // cameraView设置为白色背景
            cameraView.setBackgroundColor(0x00000000);
        });

        Button buttonStopStart = findViewById(R.id.buttonStopStart);
        buttonStopStart.setOnClickListener(arg0 -> {
            if (!stopStart) {
                stopStart = true;

                MpjYolo26.openCamera(facing);
            } else {
                stopStart = false;
                MpjYolo26.closeCamera();
            }

            // cameraView设置为白色背景
            cameraView.setBackgroundColor(0x00000000);
        });

        Spinner spinnerModel = findViewById(R.id.spinnerModel);
        spinnerModel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
                if (position != current_model) {
                    current_model = position;
                    reload(modelMap.get(position));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        Spinner spinnerCPUGPU = findViewById(R.id.spinnerCPUGPU);
        spinnerCPUGPU.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
                if (position != current_cpugpu) {
                    current_cpugpu = position;
                    reload(modelMap.get(current_model));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        // cameraView设置为白色背景
        cameraView.setBackgroundColor(0xFFFFFFFF);

        reload(modelMap.get(current_model));
    }

    private void reload(String modelName) {
        MpjYolo26.releaseModel();
        boolean ret_init = MpjYolo26.loadModel(getAssets(), modelName, targetSize, probThreshold, current_cpugpu, classLabels);
        if (!ret_init) {
            Log.e(TAG, "loadModel failed");
        }
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        MpjYolo26.setOutputWindow(surfaceHolder.getSurface());
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {
    }

    @Override
    public void onResume() {
        super.onResume();

        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        MpjYolo26.closeCamera();
        stopStart = false;
        // cameraView设置为白色背景
        if (cameraView != null) {
            cameraView.setBackgroundColor(0xFFFFFFFF);
        }
    }
}