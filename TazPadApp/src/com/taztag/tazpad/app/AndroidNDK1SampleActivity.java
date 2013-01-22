package com.taztag.tazpad.app;

import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.taztag.tazpad.telegram.Telegram;



public class AndroidNDK1SampleActivity extends Activity {

	private static String TAG="TZTG_debug";
	private Intent intent;
	private boolean previous_val = false; 
	
	
	
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tazpad);
		Log.d(TAG,"onCreate");
		intent = new Intent(this,EnOceanReceiver.class);
		//startService(intent);
		
		Button clrscreen = (Button) findViewById(R.id.clrscr);
		clrscreen.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				TextView tv = (TextView)findViewById(R.id.tv);
				tv.setText("");
			}
		});
	}

	private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			 Log.d(TAG, "broadcast reception in main activity");
			if(intent.getExtras().getBoolean("containsError") &&  intent.getExtras().getBoolean("containsError") != previous_val){
				Toast.makeText(getApplicationContext(), intent.getExtras().getString("error"), Toast.LENGTH_SHORT).show();
				previous_val = true; // reset of boolean value for one time only error print
			}
			else{
				if(!intent.getExtras().getBoolean("containsError"))
					previous_val = false; // reset of boolean value for one time only error print
				if(intent.getExtras().getBoolean("hasReceipt"))
					updateUI(intent);
			}
			Log.d(TAG,"onReceive : error    : " + intent.getExtras().getBoolean("containsError"));
			Log.d(TAG,"onReceive : previous : " + previous_val);
		}
	};

	private void updateUI(Intent intent) {
		try{
		Log.d(TAG,"UpdateUI");
		TextView tv = (TextView)findViewById(R.id.tv);
		String trame = intent.getExtras().getString("trame");
		Telegram myTl = new Telegram(trame);
		//tv.setText("Trame: "+msg.getData().getString("dataLength")+"\n "+myTl.getPacketType());
		tv.setText(
				"\nTrame: "+trame
				+"\nTelegrameType: "+myTl.getTelegramType()
				+"\nData: "+myTl.getData()
				+tv.getText()
				);
		}
		catch(IndexOutOfBoundsException e){
			Toast.makeText(getApplicationContext(), intent.getExtras().getString("error"), Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_tazpad, menu);
		return true;
	}
	public static String toHex(byte[] array) {
		String myString=new String(array);
		/*
		//for(int i = 0;i<arg.length() ;i++){//
		//myString += String.format("%02X", new BigInteger(arg.substring(0,arg.length()).getBytes(/*YOUR_CHARSET?*///)));

		//		StringBuilder sb = new StringBuilder(array.length * 2);
		//		java.util.Formatter formatter = new java.util.Formatter(sb);
		//		Log.d(TAG,"init");
		//		for (byte b : array) {  
		//	        formatter.format("%02X", b); 
		//	        sb.append(' ');
		//		}
		//		Log.d("trame_to_hexa", sb.toString());
		//		
		//		return sb.toString();
		return myString;
	}


	private void initDrivers(){
		List<String> myList = new ArrayList<String> ();
		//myList.add("reboot");
		myList.add("insmod /data/Taztag/ftdi_sio.ko");
		try {
			doCmds(myList);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void doCmds(List<String> cmds) throws Exception {
		Process process = Runtime.getRuntime().exec("su");
		DataOutputStream os = new DataOutputStream(process.getOutputStream());

		for (String tmpCmd : cmds) {
			os.writeBytes(tmpCmd+"\n");
		}

		os.writeBytes("exit\n");
		os.flush();
		os.close();

		process.waitFor();
	}

	static {  
		System.loadLibrary("ndk1");  
	}

	@Override
	public void onResume() {
		super.onResume();		
		startService(intent);
		registerReceiver(broadcastReceiver, new IntentFilter(EnOceanReceiver.BROADCAST_ACTION));
	}
	@Override
	public void onPause() {
		super.onPause();
		unregisterReceiver(broadcastReceiver);
		stopService(intent); 		
	}
}
