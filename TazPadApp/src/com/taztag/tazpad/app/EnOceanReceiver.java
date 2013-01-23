package com.taztag.tazpad.app;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import com.taztag.tazpad.telegram.Telegram;

import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

public class EnOceanReceiver extends IntentService {



	private static String TAG="TZTG_debug";
	public static String BROADCAST_ACTION = "com.taztag.tazpad.displayevent";


	private final Handler handler = new Handler();


	//Intent msgIntent = new Intent(this, EnOceanReceiver.class);

	public EnOceanReceiver() {
		super("MyEnOceanReceiver");
		// TODO Auto-generated constructor stub
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

	@Override
	protected void onHandleIntent(Intent myIntent) {
		// TODO Auto-generated method stub
		Intent intent = new Intent(BROADCAST_ACTION);


		File myDevice = new File("/dev/ttyUSB0");
		Log.d(TAG,"file exists: " + myDevice.exists());

		InputStream in = null;
		
		while(true)
		{

			String trameContent = "";

			Log.d(TAG,"sendUpdates");

			Log.d(TAG,"file length :"+myDevice.length());

			byte[] header = new byte [3] ;
			byte[] data;

			try {
				// Init intent with default values
				intent.putExtra("error", "");
				intent.putExtra("containsError", false);

				in = new BufferedInputStream(new FileInputStream(myDevice));

				in.read(header);						
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
			intent.putExtra("trame", trameContent);
			sendBroadcast(intent);
		}//startService(msgIntent);
	}
}
