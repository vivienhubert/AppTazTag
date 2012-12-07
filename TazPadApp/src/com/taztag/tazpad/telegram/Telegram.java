package com.taztag.tazpad.telegram;

import java.util.StringTokenizer;
import java.util.ArrayList;

public class Telegram {
	
	String tabTrame[]; 
	
	public static void main(String argv[]) throws Exception
	  {
		new Telegram("55 00 07 07 01 7a f6 50 00 27 87 7d 30 01 ff ff ff ff 3a 00 13");
	  }
	
	public Telegram(String trame){
		
		StringTokenizer st = new StringTokenizer(trame);
		
		int sizeTrame = st.countTokens(); // Taile de la trame
		System.out.println("Size Trame "+sizeTrame);
		int tablenght = sizeTrame-1;
		ArrayList<String> tabTrame = new ArrayList<String>();

		for(int i=0 ;i<=tablenght;i++) {
		
			tabTrame.add(st.nextToken());	        
			
	     }
		
		
		/* DIFFINITION TYPE DE TRAME */
		
		
		/************** BOUTON POUSSOIRE **************/
		
		if (sizeTrame==21){
			
			
			
			
			
		}
		
		
		
		/**********************************************/
		
		
		
		
		
		/************ CAPTEUR TEMPERATURE *************/
		
		
		
		for(int i=0 ;i<=tablenght;i++) {
			
			System.out.println("Dans Array List ["+i+"] ="+tabTrame.get(i));      
	     }
		
		
		// 
		
		
		
		
	}

		
		
		
		
		
	}
	
	
	
	
	


	
	
	
	

