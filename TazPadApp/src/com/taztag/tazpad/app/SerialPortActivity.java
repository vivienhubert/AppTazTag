/*
 * Copyright 2009 Cedric Priscal
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */

package com.taztag.tazpad.app;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidParameterException;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.format.Time;
import android.util.Log;

import com.taztag.tazpad.telegram.*;

public abstract class SerialPortActivity extends Activity {

	protected SerialPort mSerialPort;
	protected OutputStream mOutputStream;
	protected InputStream mInputStream;
	protected ReadThread mReadThread;
	private static String TAG="TZTG_debug";

	public class ReadThread extends Thread {

		@Override
		public void run() {
			super.run();
			Log.d(TAG,"Thread launched");
			boolean available = false;

			while(!isInterrupted()) {
				Log.d(TAG,"Thread iterating");

				long begin_time = android.os.SystemClock.currentThreadTimeMillis();
				byte[] buffer = new byte[24];
				String trame = null;
				int size= 0;
				if (mInputStream == null) {
					Log.d(TAG,"mInputStream null");
					available = false;
					return;
				}
				trame=read();
				Log.d(TAG,"Thread initial data " +trame);
				if (trame.length() > 0) {		// Some data received
					while(trame.length() < 6 && size != -1) {	// Header not completed

						Log.d(TAG,"while size < 6"); 
						trame+=read();// updating trame content
						Log.d(TAG,"Trame updated: " +trame);
						if(begin_time + 100 < android.os.SystemClock.currentThreadTimeMillis()){
							Log.v(TAG,"time limit");
							size = -1;
						}
					}
					if(trame.startsWith("5500") && size != -1){
						int sizeToReach=0;
						if(Telegram.getDataFromHeader(trame.substring(0, 6)) == 10)
							sizeToReach=48;
						else 
							sizeToReach=42;
						Log.d(TAG,"size to reach :"+sizeToReach);
						while((trame.length() < sizeToReach  || trame.length() < 24) && size != -1) {	// Wait to receive reception
							Log.d(TAG,"while size ("+trame.length()+ ") < " + sizeToReach);
							trame+=read();// updating trame content
							Log.d(TAG,"Trame updated: " +trame);
							if(begin_time + 100 < android.os.SystemClock.currentThreadTimeMillis()) {
								Log.v(TAG,"time limit");
								size = -1;
							}
						}
						if(size!=-1){
							//TODO: send only the necessary and save the rest in order to reuse for next trame
							onDataReceived(trame);//addSpace(trame));
							available = true;
						}
					} else {
						Log.i(TAG,"corrupted trame :"+trame);
					}
				}
				if(!available)
					SystemClock.sleep(500);
			}
		}
	}
	private String read(){
		byte[] buffer = new byte[24];
		int current_size;
		String toRet= null;
		
		try {
			Log.d(TAG,"Thread trying to read");
			current_size = mInputStream.read(buffer);
			toRet =AndroidNDK1SampleActivity.bytesToHex(buffer,current_size).substring(0, current_size * 2);	// updating trame content
			Log.d(TAG,"Thread size of data " + toRet.length()); 
			// size of current data 
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		return toRet;
	}

	private void DisplayError(int resourceId) {
		AlertDialog.Builder b = new AlertDialog.Builder(this);
		b.setTitle("Error");
		b.setMessage(resourceId);
		b.setPositiveButton("OK", new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				SerialPortActivity.this.finish();
			}
		});
		b.show();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

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

	protected abstract void onDataReceived(final String trame);

	@Override
	protected void onDestroy() {
		if (mReadThread != null)
			mReadThread.interrupt();
		if (mSerialPort != null) {
			mSerialPort.close();
			mSerialPort = null;
		}
		super.onDestroy();
	}
}
