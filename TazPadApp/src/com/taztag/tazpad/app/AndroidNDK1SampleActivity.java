package com.taztag.tazpad.app;


import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.List;

import com.taztag.tazpad.app.R;



import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;



public class AndroidNDK1SampleActivity extends Activity {

		private native void helloLog(String logThis);
		private native String eoinit();
		private native String read();
		
		private ProgressBar mProgressBar;
		
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_tazpad);
			mProgressBar = ((ProgressBar)findViewById(R.id.progressBar));
			
			initDrivers();
			
			final Button myBut = (Button) findViewById(R.id.Start);
			myBut.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	
            	mProgressBar.setProgress(0);
            	helloLog("This will log to LogCat via the native call.");
            	//((TextView)findViewById(R.id.trame)).setText("State of eo_init:"+eoinit());
            	//helloLog("State of eo_init:"+eoinit());
            	Receiver recept = new Receiver();
            	helloLog("Launch Receiver");
            	recept.execute();
            }}); 
			
		}

		@Override
		public boolean onCreateOptionsMenu(Menu menu) {
			// Inflate the menu; this adds items to the action bar if it is present.
			getMenuInflater().inflate(R.menu.activity_tazpad, menu);
			return true;
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
		class Receiver extends AsyncTask<Void, String	, Void> {


			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				Toast.makeText(getApplicationContext(), "Début du traitement asynchrone", Toast.LENGTH_LONG).show();
			}
			@Override
			protected Void doInBackground(Void... arg0) {
				while(true){
					String received = read();
					helloLog("doInBackground received: "+received);
					//publishProgress(received);
				}
			}
			protected void onProgressUpdate(String text){
				TextView trameTextview = (TextView) AndroidNDK1SampleActivity.this.findViewById(R.id.trame);
		        trameTextview.setText(text);
			}
		}
		

	}
