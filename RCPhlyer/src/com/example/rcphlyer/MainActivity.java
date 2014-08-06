package com.example.rcphlyer;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity implements SensorEventListener{
	TextView gyroView;
	TextView accView;
	TextView magView;
	Button button;
	private SensorManager sManager;
	private int accelAverage;
	private int magAverage;
	private final int numAverages = 20;
	private float[] accelAveValues = {0f,0f,0f};
	private float[] magAveValues = {0f,0f,0f};
	private float[] newAccelValues = {0f,0f,0f};
	private float[] newGyroValues = {0f,0f,0f};
	private float[] newMagValues = {0f,0f,0f};
	RotationManager rotationManager = new RotationManager();
	//TODO remove after filter testing
	private float[] alpha = {0.8f,0.8f,0.8f};	//gyro, accel, mag
	
	long pts = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		gyroView = (TextView)findViewById(R.id.gyro);
		accView = (TextView)findViewById(R.id.acc);
		magView = (TextView)findViewById(R.id.mag);
		button = (Button)findViewById(R.id.button1);
		
		sManager = (SensorManager)getSystemService(SENSOR_SERVICE);
		
		sManager.registerListener(this,sManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE),SensorManager.SENSOR_DELAY_GAME);
		sManager.registerListener(this,sManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),SensorManager.SENSOR_DELAY_GAME);
		sManager.registerListener(this,sManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),SensorManager.SENSOR_DELAY_GAME);
		
		accelAverage = numAverages;
		magAverage = numAverages;
	}
	
	protected void onResume(){
		super.onResume();
		sManager.registerListener(this,sManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE),SensorManager.SENSOR_DELAY_GAME);
		sManager.registerListener(this,sManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),SensorManager.SENSOR_DELAY_GAME);
		sManager.registerListener(this,sManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),SensorManager.SENSOR_DELAY_GAME);
	}
	
	protected void onPause(){
		super.onPause();
		sManager.unregisterListener(this);
	}
	
	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		int type = event.sensor.getType();
		//--can be removed later---
		Float magnitude = 0f;
		for(int i = 0; i < 3; i++){
			magnitude += event.values[i]*event.values[i];
		}
		magnitude = (float) Math.sqrt(magnitude);
		//--end can remove ^----
		//TODO remove setTexts
		if(type==Sensor.TYPE_GYROSCOPE){
			newGyroValues[0] = event.values[0];
			newGyroValues[1] = event.values[1];
			newGyroValues[2] = event.values[2];
			rotationManager.addData(RotationManager.GYROSCOPE, newGyroValues, event.timestamp);
		}else if(type==Sensor.TYPE_ACCELEROMETER){
			newAccelValues[0] = newAccelValues[0]*alpha[1]+(1-alpha[1])*event.values[0];
			newAccelValues[1] = newAccelValues[1]*alpha[1]+(1-alpha[1])*event.values[1];
			newAccelValues[2] = newAccelValues[2]*alpha[1]+(1-alpha[1])*event.values[2];
			rotationManager.addData(RotationManager.ACCELEROMETER, newAccelValues, event.timestamp);
			if(accelAverage > 0){
				accelAveValues[0]+=event.values[0]/numAverages;
				accelAveValues[1]+=event.values[1]/numAverages;
				accelAveValues[2]+=event.values[2]/numAverages;
				accelAverage-=1;
			}
			
		}else if(type==Sensor.TYPE_MAGNETIC_FIELD){
			newMagValues[0] = newMagValues[0]*alpha[2]+(1-alpha[2])*event.values[0];
			newMagValues[1] = newMagValues[1]*alpha[2]+(1-alpha[2])*event.values[1];
			newMagValues[2] = newMagValues[2]*alpha[2]+(1-alpha[2])*event.values[2];
			rotationManager.addData(RotationManager.MAGNETOMETER, newMagValues, event.timestamp);
			if(magAverage > 0){
				magAveValues[0]+=event.values[0]/numAverages;
				magAveValues[1]+=event.values[1]/numAverages;
				magAveValues[2]+=event.values[2]/numAverages;
				magAverage-=1;
			}
		}
		if(accelAverage+magAverage==0){
			rotationManager.initialize(accelAveValues, magAveValues);
			accelAverage = -1;
			magAverage = -1;
		}
	}
	//TODO remove this once unneeded
	public void printYaw(View v){
		rotationManager.initialize(newAccelValues, newMagValues);
		gyroView.setText(Float.toString(rotationManager.getYaw()));
		accView.setText(Float.toString(rotationManager.getPitch()));
		magView.setText(Float.toString(rotationManager.getRoll()));
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
