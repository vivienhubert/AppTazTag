package com.taztag.tazpad.app;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import com.taztag.tazpad.telegram.Telegram;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;



public class AndroidNDK1SampleActivity extends Activity {

	private Handler handler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tazpad);

		Button start = (Button) findViewById(R.id.Start);
		handler = new Handler() {
			public void handleMessage(android.os.Message msg) {
				Log.d("USB",msg.getData().getString("line"));
				TextView tv = (TextView)findViewById(R.id.tv);
				Telegram myTl = new Telegram(msg.getData().getString("line"));
				tv.setText("Trame: "+msg.getData().getString("line")+"\n "+myTl.getPacketType());

			};
		};
		start.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				File myDevice = new File("/dev/ttyUSB0");
				InputStream in = null;
				DataInputStream dis = null;

				try {
					
						in = new BufferedInputStream(new FileInputStream(myDevice));
						dis = new DataInputStream(in);

						String temp = toHex(dis.readLine());
						TextView tv = (TextView)findViewById(R.id.tv);
						//Telegram myTl = new Telegram(temp);
						tv.setText("Trame size: "+temp.length()+"\n Trame: "+temp);//+"/n Telegram PacketType: "+myTl.getPacketType());
					
				}catch(Exception e){
					Log.d("Test", "erreur ouverture "+e.getMessage());
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
		});


	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_tazpad, menu);
		return true;
	}
	public String toHex(String arg) {
		String myString="";
		//for(int i = 0;i<arg.length() ;i++){//
			myString += String.format("%02X", new BigInteger(arg.substring(0,arg.length()).getBytes(/*YOUR_CHARSET?*/)));
		return myString;
	}

	private void receiveData(){
		new Thread(new Runnable() {

			public void run() {
				File myDevice = new File("/dev/ttyUSB0");
				InputStream in = null;
				DataInputStream dis = null;

				try {
					while(true){ 
						in = new BufferedInputStream(new FileInputStream(myDevice));

						dis = new DataInputStream(in);

						Bundle bdle = new Bundle();
						String temp = dis.readLine();
						bdle.putString("line :",temp);
						Message msg = new Message();
						msg.setData(bdle);
						handler.sendMessage(msg);
					}
				}catch(Exception e){
					Log.d("Test", "erreur ouverture");
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
	static {  
	    System.loadLibrary("ndk1");  
	}
	

}
