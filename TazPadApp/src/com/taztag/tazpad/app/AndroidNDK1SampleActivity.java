package com.taztag.tazpad.app;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;



public class AndroidNDK1SampleActivity extends Activity {

	private native void helloLog(String logThis);
	private native String eoinit();
	private native String read();

	private Button bt;
	private TextView tv;
	private Socket socket;
	private String serverIpAddress = "10.0.2.2";
	// AND THAT'S MY DEV'T MACHINE WHERE PACKETS TO
	// PORT 5000 GET REDIRECTED TO THE SERVER EMULATOR'S
	// PORT 6000
	private static final int REDIRECTED_SERVERPORT = 5000;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tazpad);
		bt = (Button) findViewById(R.id.Start);
		tv = (TextView) findViewById(R.id.tv);
		try {
			InetAddress serverAddr = InetAddress.getByName(serverIpAddress);
			socket = new Socket(serverAddr, REDIRECTED_SERVERPORT);
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		bt.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				try {
					EditText et = (EditText) findViewById(R.id.et);
					String str = et.getText().toString();
					PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())),true);
					out.println(str);
					Log.d("Client", "Client sent message");
				} catch (UnknownHostException e) {
					tv.setText("Error1");
					e.printStackTrace();
				} catch (IOException e) {
					tv.setText("Error2");
					e.printStackTrace();
				} catch (Exception e) {
					tv.setText("Error3");
					e.printStackTrace();
				}
			}
		});
	}

}
