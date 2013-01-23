package com.taztag.tazpad.app;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

public class ClassGestionInterfaceAImplement extends Activity implements OnCheckedChangeListener {

	private RadioGroup radioTechnoGroup;
	private RadioButton radioTechnoButton;
	private ImageView imgTecho;
	
	private String str;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		radioTechnoGroup=(RadioGroup)findViewById(R.id.radio_group_techno);
		imgTecho=(ImageView)findViewById(R.id.logoTechno);
		radioTechnoGroup.setOnCheckedChangeListener(this);

	}


	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		// TODO Auto-generated method stub

		switch(checkedId){
		case R.id.radio_enocean:
			str="EnOcean";
			imgTecho.setImageResource(R.drawable.enlogosf);
			break;
		case R.id.radio_zigbee:
			str="Zigbee";
			imgTecho.setImageResource(R.drawable.zogosf);
			break;

		}
		Toast.makeText(ClassGestionInterfaceAImplement.this,"Technologie : "+str , Toast.LENGTH_SHORT).show();
	}


}


