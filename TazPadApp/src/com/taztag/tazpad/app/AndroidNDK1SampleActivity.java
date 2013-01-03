package com.taztag.tazpad.app;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.taztag.tazpad.telegram.Telegram;



public class AndroidNDK1SampleActivity extends Activity {

	String TAG="TZTG_debug";
	
	private Handler handler;
	 UsbManager manager;
	   HashMap<String, UsbDevice> deviceList;
	   //Button scanButton;
	   UsbDevice device;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tazpad);

		Button start = (Button) findViewById(R.id.Start);
		handler = new Handler() {
			public void handleMessage(android.os.Message msg) {
				Log.d(TAG,msg.getData().getString("dataLength"));
				TextView tv = (TextView)findViewById(R.id.tv);
				//Telegram myTl = new Telegram(msg.getData().getString("dataLength"));
				//tv.setText("Trame: "+msg.getData().getString("dataLength")+"\n "+myTl.getPacketType());
				tv.setText("Trame: "+msg.getData().getString("trame"));

			};
		};
		start.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				receiveData();				
			}
		});


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

	private void receiveData(){
		Log.d(TAG,"thread launched");
		new Thread(new Runnable() {

			public void run() {
				File myDevice = new File("/dev/ttyUSB0");
				InputStream in = null;
				DataInputStream dis = null;

				byte[] header = new byte [3] ;
				byte[] data;

				try {
					
						in = new BufferedInputStream(new FileInputStream(myDevice));
						in.read(header);
						//dis = new DataInputStream(in);
						//dis.read(input);
												
						Log.d(TAG, "header : "+header);
						String trame = bytesToHex(header);
						TextView tv = (TextView)findViewById(R.id.tv);
						//Telegram myTl = new Telegram("55 00 07 07 04 7a f6 01 00 27 87 7d 30 01 ff ff ff ff 3a 00 13");
						int size=Telegram.getDataFromHeader(trame);
						Log.d(TAG, ""+size);
						for(int i = 0;i<size-1;i++){
							data = new byte [3] ;
							in.read(data);
							trame+=bytesToHex(data);
						}
						Log.d(TAG, "trame " + addSpace(trame));
						
						Telegram myTl = new Telegram(addSpace(trame));
						/*tv.setText("Trame size: "+trame.length()
									+"\n Trame: "+trame+"\n Size: "+size
									+"\n Telegram PacketType: "+myTl.getPacketType()
									+"\n Telegram DataLength: "+myTl.getDataLenght()
								);
								*/
						Log.d(TAG,"bundle creation");
						Bundle b = new Bundle();
						b.putString("trame", addSpace(trame));
						b.putString("dataLength", ""+myTl.getDataLenght());
						b.putString("packetType", myTl.getPacketType());
						
						Message msg = new Message();
						msg.setData(b);
						handler.sendMessage(msg);
						Log.d(TAG,"bundle sent");
						
				}catch(Exception e){
					Log.d(TAG, "Test"+"erreur ouverture "+e.getMessage());
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
			}
		}).start();

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
	static {  
	    System.loadLibrary("ndk1");  
	}

}
