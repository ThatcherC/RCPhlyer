package com.example.rcphlyer;

import java.util.Timer;
import java.util.TimerTask;

import android.hardware.SensorManager;

public class RotationManager {
	float[] DCM;
	final float[] zeroArray = {0f,0f,0f};
	float[] accelData = zeroArray;		//these could all be one array
	float[] magData = zeroArray;		//or some custom data type
	float[] gyroData = zeroArray;
	long magTimeStamp;
	long gyroTimeStamp;
	long accelTimeStamp;
	final static char GYROSCOPE = 0;
	final static char ACCELEROMETER = 1;
	final static char MAGNETOMETER = 2;
	Timer timer = new Timer();
	
	public RotationManager(){
		this.DCM = new float[9];
	}
	
	public void initialize(float[] acc, float[] mag){
		SensorManager.getRotationMatrix(DCM, null, acc, mag);
	}
	
	public void imuStart(){
		timer.scheduleAtFixedRate(new imuUpdate(), 0, 20);
		accelData = zeroArray;
		magData = zeroArray;
		gyroData = zeroArray;
	}
	
	private class imuUpdate extends TimerTask{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	public void addData(char type, float[] values, long time){
		if(type==0){
			gyroData[0]+=values[0];
			gyroData[1]+=values[1];
			gyroData[2]+=values[2];
			gyroTimeStamp = time;
		}else if(type==1){
			accelData[0]+=values[0];
			accelData[1]+=values[1];
			accelData[2]+=values[2];
			accelTimeStamp = time;
		}else if(type==2){
			magData[0]+=values[0];
			magData[1]+=values[1];
			magData[2]+=values[2];
			magTimeStamp = time;
		}
				
	}
	
	public float getYaw(){
		return (float) (Math.atan2(DCM[3], DCM[0])*180.0/Math.PI);
	}
	
	public float getPitch(){
		return (float) (Math.atan2(DCM[7], DCM[8])*180.0/Math.PI);
	}
	
	public float getRoll(){
		double mag = Math.sqrt(DCM[7]*DCM[7]+DCM[8]*DCM[8]);
		return (float) (Math.atan2(-DCM[6],mag)*180.0/Math.PI);
	}
}
