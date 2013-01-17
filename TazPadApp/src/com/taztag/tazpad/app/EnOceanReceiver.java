package com.taztag.tazpad.app;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;

import com.taztag.tazpad.telegram.Telegram;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

public class EnOceanReceiver extends Service {

	private static String TAG="TZTG_debug";
	public static String BROADCAST_ACTION = "com.taztag.tazpad.displayevent";
	private String trameContent;

	private final Handler handler = new Handler();
	Intent intent;

	
	/**
	 * initialisation des ressources
	 */
	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(TAG,"service created");
		intent = new Intent(BROADCAST_ACTION);	
	}

	/**
	 * Demarre la tache de fond
	 * SDK > 2
	 */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		handler.removeCallbacks(sendUpdatesToUI);
		handler.postDelayed(sendUpdatesToUI, 1000); // 1 second
		Log.d(TAG,"onStartService");
		return startId;

	}

	/**
	 * Demarre la tache de fond 
	 * SDK < 2
	 */
	@Override
	public void onStart(Intent intent, int startId) {
		handler.removeCallbacks(sendUpdatesToUI);
		handler.postDelayed(sendUpdatesToUI, 1000); // 1 second
		Log.d(TAG,"onStartService");

	}

	private Runnable sendUpdatesToUI = new Runnable() {
		public void run() {
			Log.d(TAG,"sendUpdates");
			
			File myDevice = new File("/dev/ttyUSB0");
			InputStream in = null;
			InputStreamReader isr = null;
			
			Log.d(TAG,"file length :"+myDevice.length());

			byte[] header = new byte [3] ;
			byte[] data;

			try {
					in = new BufferedInputStream(new FileInputStream(myDevice));
					isr = new InputStreamReader(in);
					in.read(header);
					if(isr.ready()){
											
					Log.d(TAG, "header : "+header);
					String trame = bytesToHex(header);
					int size=Telegram.getDataFromHeader(trame);
					Log.d(TAG, ""+size);
					for(int i = 0;i<size-1;i++){
						data = new byte [3] ;
						in.read(data);
						trame+=bytesToHex(data);
					}
					Log.d(TAG, "trame " + addSpace(trame));
					
					trameContent = addSpace(trame);
					intent.putExtra("hasReceipt", true);
					}
					
			}catch(Exception e){
				Log.d(TAG, "Test"+"erreur ouverture "+e.getMessage());
				intent.putExtra("error", "Error while opening device");
				intent.putExtra("containsError", true);
			}
			finally {
				if (in != null) {
					try {
						in.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			} 
				
			
			DisplayLoggingInfo();    		
			handler.postDelayed(this, 1000); // 1 second
		}
	};

	private void DisplayLoggingInfo() {
		Log.d(TAG, "entered DisplayLoggingInfo");

		intent.putExtra("trame", trameContent);
		
		sendBroadcast(intent);
	}



	/**
	 * Connexion
	 */
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Deconnexion
	 */
	@Override
	public boolean onUnbind(Intent intent){
		return false;// TODO

	}


	/**
	 * libere les ressouces
	 */
	@Override
	public void onDestroy() {
		handler.removeCallbacks(sendUpdatesToUI);		
		super.onDestroy();
	}
	
	public String addSpace(String toSeparate){
		String toRet = "";
		for(int i = 0;i<toSeparate.length();i++){
			toRet+=toSeparate.charAt(i);
			if(i%2==1){
				toRet+=" ";
			}
		}
		return toRet;
	}
	public static String bytesToHex(byte[] bytes) {
	    final char[] hexArray = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
	    char[] hexChars = new char[bytes.length * 2];
	    int v;
	    for ( int j = 0; j < bytes.length; j++ ) {
	        v = bytes[j] & 0xFF;
	        hexChars[j * 2] = hexArray[v >>> 4];
	        hexChars[j * 2 + 1] = hexArray[v & 0x0F];
	    }
	    return new String(hexChars);
	}
}
