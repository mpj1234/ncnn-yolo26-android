/**
 * @author mpj
 * @date 2026/1/19 21:18
 * @version V1.0
 * @since jdk1.8
 **/
package com.mpj.mpj_yolo26;

import android.content.res.AssetManager;
import android.view.Surface;

public class MpjYolo26 {

    /**
     * 加载模型
     * @param assetManager 资源管理器
     * @param modelName 模型在asserts中名称
     * @param cpugpu 0:cpu 1:gpu
     * @return 是否加载成功
     */
    public static native boolean loadModel(AssetManager assetManager, String modelName, int targetSize, int cpugpu, String[] labels);

    /**
     * 释放资源
     */
    public static native boolean releaseModel();

    /**
     * 打开相机
     * @param facing 0:后置 1:前置
     * @return 是否打开成功
     */
    public static native boolean openCamera(int facing);

    /**
     * 关闭相机
     */
    public static native boolean closeCamera();

    /**
     * 设置输出窗口
     * @param surface 输出窗口
     * @return 是否设置成功
     */
    public static native boolean setOutputWindow(Surface surface);

    static {
        System.loadLibrary("mpj_yolo26");
    }
}
