package com.taztag.tazpad.app;

import android.R.integer;
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
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.taztag.tazpad.telegram.Telegram;



public class AndroidNDK1SampleActivity extends Activity implements android.widget.RadioGroup.OnCheckedChangeListener {

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

	private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			 Log.d(TAG, "broadcast reception in main activity");
			if(intent.getExtras().getBoolean("containsError") &&  intent.getExtras().getBoolean("containsError") != previous_val){
				//Toast.makeText(getApplicationContext(), intent.getExtras().getString("error"), Toast.LENGTH_SHORT).show();
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
	
	/*
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
		//return myString;
	


	/*private void initDrivers(){
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
	}*/

	/*
	@Override
	public void onResume() {
		super.onResume();		
		startService(intent);
		registerReceiver(broadcastReceiver, new IntentFilter(EnOceanReceiver.BROADCAST_ACTION));
	}
	*/
	@Override
	public void onPause() {
		super.onPause();
		unregisterReceiver(broadcastReceiver);
		stopService(intent); 		
	}

	
	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
			
		switch(checkedId){
		case R.id.radio_enocean:
			str="EnOcean";
			imgTecho.setImageResource(R.drawable.enlogosf);
			startService(intent);
			registerReceiver(broadcastReceiver, new IntentFilter(EnOceanReceiver.BROADCAST_ACTION));	
			break;
		case R.id.radio_zigbee:
			str="Zigbee";
			imgTecho.setImageResource(R.drawable.zogosf);
			break;

		}
		
		imgTecho.setVisibility(View.VISIBLE);
		Toast.makeText(AndroidNDK1SampleActivity.this,"Technologie : "+str , Toast.LENGTH_SHORT).show();
	}
}
