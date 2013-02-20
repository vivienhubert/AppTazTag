package com.taztag.tazpad.app.activities;

import java.io.File;
import java.io.IOException;
import java.security.InvalidParameterException;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;




import com.taztag.tazpad.app.R;
import com.taztag.tazpad.app.R.drawable;
import com.taztag.tazpad.app.R.id;
import com.taztag.tazpad.app.R.layout;
import com.taztag.tazpad.app.R.menu;
import com.taztag.tazpad.app.enocean.SerialPort;
import com.taztag.tazpad.app.enocean.SerialPortActivity;
import com.taztag.tazpad.app.zigbee.Notif;
import com.taztag.tazpad.app.zigbee.SimpDesc;
import com.taztag.tazpad.telegram.Telegram;
import com.taztag.zigbee.Zdo;
import com.taztag.zigbee.ZigbeeManager;
import com.taztag.zigbee.utils.ZChannel;
import com.taztag.zigbee.utils.ZConf;



public class AndroidNDK1SampleActivity extends SerialPortActivity {

	private static String TAG="TZTG_debug";
	private Intent intent;
	private boolean previous_val = false; 
	private Button resetButton;
	private Button startButton;
	private ImageView imgTecho;
	private ImageView imgEquipement;
	private String str;
	private TextView tvTrame;
	private TextView tvDataLenght;
	private TextView tvTelegramType;
	private TextView tvEquipementType;
	private TextView tvTemperature;
	private TextView tvStatus;

	// Zigbee part
	ZigBeeThread zbThread;
	ProgressBar progressZigbee = null;

	private boolean configOk = false;
	public boolean zdoStarted = false;
	private boolean zbmInit = false;
	static boolean isReady = false;
	private String extPanId = "1234567890123456";
	private String nwkPanId = "ABCD";
	private boolean toRestart = false;
	private Notif nf;
	private ZigbeeManager zbm;
	private SimpDesc sd;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Log.d(TAG,"onCreate");
		
		/****** DECLARATION *********/


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


		// ZigBee management from OMAR
		//progressZigbee = (ProgressBar) findViewById(R.id.progressBar1);
		//progressZigbee.setVisibility(ProgressBar.INVISIBLE);

		zbThread = new ZigBeeThread();
		init();
		
		//intent = new Intent(this,EnOceanReceiver.class);
		//startService(intent);
		startButton= (Button) findViewById(R.id.start);
		startButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {

				// Run Enocean
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
				// Run Zigbee
				zbThread.start();

			}
		});

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
				imgTecho.setVisibility(View.INVISIBLE);
				//startService(intent);
				//registerReceiver(broadcastReceiver, new IntentFilter(EnOceanReceiver.BROADCAST_ACTION));

			}
		});


	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_tazpad, menu);
		return true;
	}

	@Override
	protected void onDataReceived(final String trame) {
		runOnUiThread(new Runnable() {
			public void run() {
				try{
					Log.v(TAG,"trame : "+trame);
					imgTecho.setImageResource(R.drawable.enlogosf);	// mise a jour du logo
					imgTecho.setVisibility(View.VISIBLE);
					
					Telegram myTl = new Telegram(trame);	// On cree un telegram avec la trame
					
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


	public void updateMyValue(final double p) {
		runOnUiThread(new Runnable() {
			public void run() {
				imgTecho.setImageResource(R.drawable.zogosf);	// mise a jour du logo
				imgTecho.setVisibility(View.VISIBLE);
				imgEquipement.setVisibility(View.INVISIBLE);
				tvTemperature.setVisibility(View.VISIBLE);
				tvTemperature.setText(p+" C°");
			}
		});

	}


public class ZigBeeThread extends Thread {


	public ZigBeeThread() {
		// TODO Auto-generated constructor stub
		//parent = _parent;
		
	}

	public ZigBeeThread(Runnable runnable) {
		super(runnable);
		// TODO Auto-generated constructor stub
	}

	public ZigBeeThread(String threadName) {
		super(threadName);
		// TODO Auto-generated constructor stub
	}

	public ZigBeeThread(Runnable runnable, String threadName) {
		super(runnable, threadName);
		// TODO Auto-generated constructor stub
	}

	public ZigBeeThread(ThreadGroup group, Runnable runnable) {
		super(group, runnable);
		// TODO Auto-generated constructor stub
	}

	public ZigBeeThread(ThreadGroup group, String threadName) {
		super(group, threadName);
		// TODO Auto-generated constructor stub
	}

	public ZigBeeThread(ThreadGroup group, Runnable runnable, String threadName) {
		super(group, runnable, threadName);
		// TODO Auto-generated constructor stub
	}

	public ZigBeeThread(ThreadGroup group, Runnable runnable,
			String threadName, long stackSize) {
		super(group, runnable, threadName, stackSize);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run() {
		while(!isInterrupted()) {
			sd.readFromTarget();
			SystemClock.sleep(500);
		}
	}
	
	
}
protected void init(){
	zbm = ZigbeeManager.getInstance();

	configOk = false;
	zdoStarted = false;
	zbmInit = false;

	int res = connectZigbee();
	Log.d("TT_ZigBee_Sample", "[Main] connectZigbee = " + res);
	if (res != 0){
		//progressZigbee.setVisibility(ProgressBar.VISIBLE);
		Log.d(TAG,"Failed to start ZigBee");
	}
	else{
		//progressZigbee.setVisibility(ProgressBar.INVISIBLE);
		Log.d(TAG,"Success to start ZigBee");
		//btnstart.setVisibility(Button.GONE);
	}
}

	// Method to start Zigbee
	public int connectZigbee() {
		toRestart = false;
		int res = -1;
		if (zdoStarted)
			return -10;
		if (!zbmInit) {
			res = zbm.init();

			if (res == 0) {
				//Log.d("TT_ZigBee_Sample","[Main] connectZigbee - ZTM - Init ZigBee Ok");
				zbmInit = true;
			} else {
				//Log.d("TT_ZigBee_Sample","[Main] connectZigbee - ZTM - Init ZigBee Ko - res = "								+ res);
				zbmInit = false;
				return -1;
			}
		}
		if (zbmInit) {
			do {
				res = zbm.setConfiguration(ZConf.RX_ON_WHEN_IDLE_ID, "0");
				if (res != 0) {
					//Log.d("TT_ZigBee_Sample",	"[Main] connectZigbee - ZTM - RX_ON_WHEN_IDLE_ID failed");
					return -2;
				} else {
					//Log.d("TT_ZigBee_Sample",	"[Main] connectZigbee - ZTM - RX_ON_WHEN_IDLE_ID passed");
				}
				res = zbm.setConfiguration(ZConf.NWK_UNIQUE_ADDR_ID, "0");
				if (res != 0) {
					//Log.d("TT_ZigBee_Sample","[Main] connectZigbee - ZTM - NWK_UNIQUE_ADDR_ID failed");
					return -3;
				} else {
					//Log.d("TT_ZigBee_Sample","[Main] connectZigbee - ZTM - NWK_UNIQUE_ADDR_ID passed");
				}
				res = zbm
						.setConfiguration(ZConf.CHANNEL_MASK_ID, ZChannel.sC12);
				if (res != 0) {
					//Log.d("TT_ZigBee_Sample","[Main] connectZigbee - ZTM - CHANNEL_MASK_ID failed");
					return -4;
				} else {
					//Log.d("TT_ZigBee_Sample","[Main] connectZigbee - ZTM - CHANNEL_MASK_ID passed");
				}
				res = zbm.setConfiguration(ZConf.NWK_PREDEFINED_PANID_ID, "1");
				if (res != 0) {
					//Log.d("TT_ZigBee_Sample","[Main] connectZigbee - ZTM - NWK_PREDEFINED_PANID_ID failed");
					return -5;
				} else {
					//Log.d("TT_ZigBee_Sample","[Main] connectZigbee - ZTM - NWK_PREDEFINED_PANID_ID passed");
				}
				res = zbm.setConfiguration(ZConf.EXT_PANID_ID, extPanId);
				if (res != 0) {
					//Log.d("TT_ZigBee_Sample",	"[Main] connectZigbee - ZTM - EXT_PANID_ID failed");
					return -6;
				} else {
					//Log.d("TT_ZigBee_Sample","[Main] connectZigbee - ZTM - EXT_PANID_ID passed");
				}
				res = zbm.setConfiguration(ZConf.NWK_PANID_ID, nwkPanId);
				if (res != 0) {
					//Log.d("TT_ZigBee_Sample",	"[Main] connectZigbee - ZTM - EXT_PANID_ID failed");
					return -10;
				} else {
					//Log.d("TT_ZigBee_Sample",	"[Main] connectZigbee - ZTM - EXT_PANID_ID passed");
				}				
				res = zbm.setConfiguration(ZConf.DEVICE_TYPE_ID, "0");
				if (res != 0) {
					//Log.d("TT_ZigBee_Sample","[Main] connectZigbee - ZTM - DEVICE_TYPE_ID failed");
					return -7;
				} else {
					//Log.d("TT_ZigBee_Sample","[Main] connectZigbee - ZTM - DEVICE_TYPE_ID passed");
				}
			} while (false);
			configOk = true;
		}
		if (configOk) {
			//Log.d("TT_ZigBee_Sample","[Main] connectZigbee - ZTM - Coordinator Configured");
			createClusters();
			//Log.d("TT_ZigBee_Sample","[Main] connectZigbee - Zdo.startNetwork - Before");
			res = Zdo.startNetwork();
			//Log.d("TT_ZigBee_Sample","[Main] connectZigbee - Zdo.startNetwork - res = " + res);
			if (res == -1) {
				Log.d("TT_ZigBee_Sample","[Main] connectZigbee - ZTM - Coordinator Not Started");
				zdoStarted = false;
				return -8;
			} else {
				//Log.d("TT_ZigBee_Sample","[Main] connectZigbee - ZTM - Started");
				//Log.d("TT_ZigBee_Sample","[Main] connectZigbee - ZTM - Coordinator");
				zdoStarted = true;
				return 0;
			}
		}
		if (res == 1)
			return 0;
		return -9;
	}

	// Reset zigbee et dislay a message if a plug is connected
	private final Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Notif.CONNECTED:
				Log.d(TAG,"connected");
				break;

			case Notif.RESTART_ZIGBEE:
				//Log.d("TT_ZigBee_Sample",	"[Main] handleMessage - Restart ZigBee");
				
				toRestart = true;
				new Thread() {
					public void run() {
						try {
							sleep(3000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				};
				int restmp = connectZigbee();
				if (restmp == 0) {
					//Log.d("TT_ZigBee_Sample", "[Main] Zigbee Network is ok");
				} else {
					//Log.d("TT_ZigBee_Sample",	"[Main] Zigbee Network is KO, try again to restart");
				}
				break;
			case Notif.DATA_RESPONSE:
				String data = msg.obj.toString();
				//Log.d("TT_ZigBee_Sample", "[Main] data = " + data);
				
				break;
			case Notif.CLOSE_ZIGBEE:
				//Log.d("TT_ZigBee_Sample","[Main] close zigbee by notif ; zbminit = " + zbmInit+ " ; torestart = " + toRestart);
				break;
			case Notif.STARTED:
				//Log.d("TT_ZigBee_Sample", "[Main] zigbee started by notif");
				
				break;
			default:
				//Log.d("TT_ZigBee_Sample", "[Main] MHandler msg.what unknown");
			}
		};

	};

//Method to create clusters
private int createClusters() {
	if (zbmInit) {
		//Log.d("TT_ZigBee_Sample", "[Main] createClusters");
		nf = Notif.getInstance();

		sd = new SimpDesc(this);
		sd.init();
		sd.addClusters();
		zbm.addNetworkListener(sd);
		zbm.addZclListener(sd, sd.sDesc);
		zbm.addConfListener(sd, sd.sDesc);
	}
	return 0;
}
// Method which initialize the message

public void update(int who, double val) {
	//Log.d("TT_ZigBee_Sample", "[Main] update = " + who);
	mHandler.obtainMessage(who, -1, -1, val).getData();

}


public void update(int who, String val) {
	//Log.d("TT_ZigBee_Sample", "[Main] update string = " + who);
	mHandler.obtainMessage(who, -1, -1, val).getData();
}	

}
	

	
