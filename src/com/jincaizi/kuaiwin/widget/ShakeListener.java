package com.jincaizi.kuaiwin.widget;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
  
/**  
 * 一个检测手机摇晃的监听器  
 */  
public class ShakeListener implements SensorEventListener {   
    // 速度阈值，当摇晃速度达到这值后产生作用   
    private int SPEED_SHRESHOLD = 2000;   
    // 两次检测的时间间隔   
    private int UPTATE_INTERVAL_TIME = 140;   
//    mShakeListener.setSPEED_SHRESHOLD(2000);
//	mShakeListener.setUPTATE_INTERVAL_TIME(160);
    // 传感器管理器   
    private SensorManager sensorManager;   
    // 传感器   
    private Sensor sensor;   
    // 重力感应监听器   
    private OnShakeListener onShakeListener;   
    // 上下文   
    private Context mContext;   
    // 手机上一个位置时重力感应坐标   
    private float lastX;   
    private float lastY;   
    private float lastZ;   
    // 上次检测时间   
    private long lastUpdateTime = 0;   
  
    public int getSPEED_SHRESHOLD() {
		return SPEED_SHRESHOLD;
	}

	public void setSPEED_SHRESHOLD(int sPEED_SHRESHOLD) {
		SPEED_SHRESHOLD = sPEED_SHRESHOLD;
	}
	
   

	public int getUPTATE_INTERVAL_TIME() {
		return UPTATE_INTERVAL_TIME;
	}

	public void setUPTATE_INTERVAL_TIME(int uPTATE_INTERVAL_TIME) {
		UPTATE_INTERVAL_TIME = uPTATE_INTERVAL_TIME;
	}

	// 构造器   
    public ShakeListener(Context c) {   
        // 获得监听对象   
        mContext = c;   
        start();   
    }   
  
    // 开始   
    public void start() {   
        // 获得传感器管理器   
        sensorManager = (SensorManager) mContext   
                .getSystemService(Context.SENSOR_SERVICE);   
        if (sensorManager != null) {   
            // 获得重力传感器   
            sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);   
        }   
        // 注册   
        if (sensor != null) {   
            sensorManager.registerListener(this, sensor,   
                    SensorManager.SENSOR_DELAY_GAME);   
        }   
  
    }   
  
    // 停止检测   
    public void stop() {   
        sensorManager.unregisterListener(this);   
    }   
  
    // 设置重力感应监听器   
    public void setOnShakeListener(OnShakeListener listener) {   
        onShakeListener = listener;   
    }   
  
    // 重力感应器感应获得变化数据   
    public void onSensorChanged(SensorEvent event) {   
        // 现在检测时间   
        long currentUpdateTime = System.currentTimeMillis();   
        // 两次检测的时间间隔   
        long timeInterval = currentUpdateTime - lastUpdateTime;   
        // 判断是否达到了检测时间间隔   
        if (timeInterval < UPTATE_INTERVAL_TIME)   {
        	//lastUpdateTime = currentUpdateTime; 
            return;   
        }
        // 现在的时间变成last时间   
        lastUpdateTime = currentUpdateTime;   
  
        // 获得x,y,z坐标   
        float x = event.values[0];   
        float y = event.values[1];   
        float z = event.values[2];   
  
        // 获得x,y,z的变化值   
        float deltaX = x - lastX;   
        float deltaY = y - lastY;   
        float deltaZ = z - lastZ;   
  
        // 将现在的坐标变成last坐标   
        lastX = x;   
        lastY = y;   
        lastZ = z;   
  
        double speed = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ   
                * deltaZ)   
                / timeInterval * 10000;   
        // 达到速度阀值，发出提示   
        if (speed >= SPEED_SHRESHOLD) {   
            onShakeListener.onShake();   
        }   
    }   
  
    public void onAccuracyChanged(Sensor sensor, int accuracy) {   
  
    }   
  
    // 摇晃监听接口   
    public interface OnShakeListener {   
        void onShake();   
    }   
  
}  

