package com.taztag.tazpad.app;

import java.io.File;
import java.io.IOException;
import java.security.InvalidParameterException;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;


import com.taztag.tazpad.telegram.Telegram;



public class AndroidNDK1SampleActivity extends SerialPortActivity implements android.widget.RadioGroup.OnCheckedChangeListener {

	private static String TAG="TZTG_debug";
	private Intent intent;
	private boolean previous_val = false; 

	private RadioGroup radioTechnoGroup;
	private RadioButton radioTechnoButton;
	private Button resetButton;
	private ImageView imgTecho;
	private ImageView imgEquipement;
	private String str;
	private TextView tvTrame;
	private TextView tvDataLenght;
	private TextView tvTelegramType;
	private TextView tvEquipementType;
	private TextView tvTemperature;
	private TextView tvStatus;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		/****** DECLARATION *********/

		radioTechnoGroup=(RadioGroup)findViewById(R.id.radio_group_techno);
		imgTecho=(ImageView)findViewById(R.id.logoTechno);
		imgEquipement=(ImageView)findViewById(R.id.imgEquip);
		tvTrame = (TextView)findViewById(R.id.trame);
		tvDataLenght = (TextView)findViewById(R.id.datalenght);
		tvTelegramType = (TextView)findViewById(R.id.telegramtype);
		tvEquipementType = (TextView)findViewById(R.id.typeEquipement);
		tvTemperature = (TextView)findViewById(R.id.temperature);
		tvStatus = (TextView)findViewById(R.id.status);


		/*********VISIBILITY********/

		tvTemperature.setVisibility(View.INVISIBLE);
		imgEquipement.setVisibility(View.INVISIBLE);
		imgTecho.setVisibility(View.INVISIBLE);





		radioTechnoGroup.setOnCheckedChangeListener(this);



		Log.d(TAG,"onCreate");
		intent = new Intent(this,EnOceanReceiver.class);
		//startService(intent);

		resetButton = (Button) findViewById(R.id.reset);
		resetButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				//unregisterReceiver(broadcastReceiver);
				//stopService(intent); 
				tvTrame.setText("Trame : ");
				tvDataLenght.setText("Data Lenght : ");
				tvTelegramType.setText("Telegramme Type : ");
				tvEquipementType.setText("Type : ");			
				tvStatus.setText("Status : ");
				tvTemperature.setVisibility(View.INVISIBLE);
				imgEquipement.setVisibility(View.INVISIBLE);
				//startService(intent);
				//registerReceiver(broadcastReceiver, new IntentFilter(EnOceanReceiver.BROADCAST_ACTION));

			}
		});
	}



	private void updateUI(Intent intent) {
		try{
			Log.d(TAG,"UpdateUI");
			String trame = intent.getExtras().getString("trame");
			Telegram myTl = new Telegram(trame);
			//tv.setText("Trame: "+msg.getData().getString("dataLength")+"\n "+myTl.getPacketType());
			tvTrame.setText("Trame : "+myTl.getTrame());
			tvDataLenght.setText("Data Lenght : "+myTl.getDataLenght());
			tvTelegramType.setText("Telegramme Type : "+myTl.getTelegramType());
			tvEquipementType.setText("Type : "+myTl.getEquipement());
			tvStatus.setText("Status : "+myTl.getData());
			if (myTl.getEquipement().equals("bouton_poussoire")){

				if(myTl.getData().equals("Bouton_Bas")){tvTemperature.setVisibility(View.INVISIBLE);imgEquipement.setImageResource(R.drawable.swpresb);imgEquipement.setVisibility(View.VISIBLE);}
				else if(myTl.getData().equals("Bouton_Haut")){tvTemperature.setVisibility(View.INVISIBLE);imgEquipement.setImageResource(R.drawable.swpresh);imgEquipement.setVisibility(View.VISIBLE);}
				else if(myTl.getData().equals("Bouton_Presse")){tvTemperature.setVisibility(View.INVISIBLE);imgEquipement.setImageResource(R.drawable.swnoe);imgEquipement.setVisibility(View.VISIBLE);}
				else if(myTl.getData().equals("Bouton_Released")){tvTemperature.setVisibility(View.INVISIBLE);imgEquipement.setImageResource(R.drawable.swnoe);imgEquipement.setVisibility(View.VISIBLE);}

			}

			else if (myTl.getEquipement().equals("capteur_temperature")){imgEquipement.setVisibility(View.INVISIBLE);tvTemperature.setText(myTl.getData());tvTemperature.setVisibility(View.VISIBLE);}





		}
		catch(IndexOutOfBoundsException e){
			//Toast.makeText(getApplicationContext(), intent.getExtras().getString("error"), Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_tazpad, menu);
		return true;
	}



	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {

		switch(checkedId){
		case R.id.radio_enocean:
			str="EnOcean";
			imgTecho.setImageResource(R.drawable.enlogosf);
			//startService(intent);
			//registerReceiver(broadcastReceiver, new IntentFilter(EnOceanReceiver.BROADCAST_ACTION));
			/* Create a receiving thread */
			try {
				mSerialPort = new SerialPort(new File("dev/ttyUSB0"), 57600, 0);

				mOutputStream = mSerialPort.getOutputStream();
				mInputStream = mSerialPort.getInputStream();

				
			mReadThread = new ReadThread();
			mReadThread.start();
			} catch (InvalidParameterException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case R.id.radio_zigbee:
			str="Zigbee";
			imgTecho.setImageResource(R.drawable.zogosf);
			break;

		}

		imgTecho.setVisibility(View.VISIBLE);
		Toast.makeText(AndroidNDK1SampleActivity.this,"Technologie : "+str , Toast.LENGTH_SHORT).show();
	}

	@Override
	protected void onDataReceived(final String trame) {
		runOnUiThread(new Runnable() {
			public void run() {
				try{
					Log.v(TAG,"UpdateUI");
					Log.v(TAG,"trame : "+trame);
					
					Telegram myTl = new Telegram(trame);
					//tv.setText("Trame: "+msg.getData().getString("dataLength")+"\n "+myTl.getPacketType());
					tvTrame.setText("Trame : "+myTl.getTrame());
					tvDataLenght.setText("Data Lenght : "+myTl.getDataLenght());
					tvTelegramType.setText("Telegramme Type : "+myTl.getTelegramType());
					tvEquipementType.setText("Type : "+myTl.getEquipement());
					tvStatus.setText("Status : "+myTl.getData());
					if (myTl.getEquipement().equals("bouton_poussoire")){

						if(myTl.getData().equals("Bouton_Bas")){tvTemperature.setVisibility(View.INVISIBLE);imgEquipement.setImageResource(R.drawable.swpresb);imgEquipement.setVisibility(View.VISIBLE);}
						else if(myTl.getData().equals("Bouton_Haut")){tvTemperature.setVisibility(View.INVISIBLE);imgEquipement.setImageResource(R.drawable.swpresh);imgEquipement.setVisibility(View.VISIBLE);}
						else if(myTl.getData().equals("Bouton_Presse")){tvTemperature.setVisibility(View.INVISIBLE);imgEquipement.setImageResource(R.drawable.swnoe);imgEquipement.setVisibility(View.VISIBLE);}
						else if(myTl.getData().equals("Bouton_Released")){tvTemperature.setVisibility(View.INVISIBLE);imgEquipement.setImageResource(R.drawable.swnoe);imgEquipement.setVisibility(View.VISIBLE);}

					}

					else if (myTl.getEquipement().equals("capteur_temperature")){imgEquipement.setVisibility(View.INVISIBLE);tvTemperature.setText(myTl.getData());tvTemperature.setVisibility(View.VISIBLE);}

				}
				catch(IndexOutOfBoundsException e){
					//Toast.makeText(getApplicationContext(), intent.getExtras().getString("error"), Toast.LENGTH_SHORT).show();
				}
			}
		});
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
	public static String bytesToHex(byte[] bytes, int size) {
		final char[] hexArray = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
		char[] hexChars = new char[bytes.length * 2];
		int v;
		for ( int j = 0; j < bytes.length && j < size; j++ ) {
			v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}
		return new String(hexChars);
	}
	public void OnStop(){
		mReadThread.stop();
	}
}
