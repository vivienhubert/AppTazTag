package com.taztag.tazpad.telegram;

import java.util.StringTokenizer;
import java.util.ArrayList;

// Class permetant la gestion du Telegram (Recupération de donnée importantes)


public class Telegram {
	
	private ArrayList<String> tabTrame = new ArrayList<String>();
	private int dataLenght;
	private int optionalLenght;
	
	
	
	
	public static void main(String argv[]) throws Exception
	  {
		Telegram t = new Telegram("55 00 07 07 04 7a f6 50 00 27 87 7d 30 01 ff ff ff ff 3a 00 13"); 
		t.getDataLenght();
		t.getOptionalLenght();
		System.out.println(t.getPacketType());
	  }
	
	public Telegram(String trame){
		
		StringTokenizer st = new StringTokenizer(trame);
		int sizeTrame = st.countTokens(); // Taile de la trame
		System.out.println("Size Trame "+sizeTrame);
		int tablenght = sizeTrame-1;
		
		
		for(int i=0 ;i<=tablenght;i++) {
		
			tabTrame.add(st.nextToken());	// Trame enregistrer dans un Tableau        
			
	     }
	}
		
		
		
		/********** ANALYSE DU HEADER ************/
		
		
		/* Composition du HEADER [item tabTrame] : 
		 * 
		 * SyncByte[0] + DataLenght[1,2] + OptionalLenght[3] + PacketType [4] + CRC8 [5]
		 * 
		 */
		
		
		public int getDataLenght()
		{
			dataLenght = Integer.parseInt(tabTrame.get(1)+tabTrame.get(2), 16); 
			System.out.println("DataLenght : "+dataLenght);
			return (dataLenght);
		}
		
		public int getOptionalLenght()
		{
			optionalLenght = Integer.parseInt(tabTrame.get(3), 16); 
			System.out.println("OptionalLenght : "+optionalLenght);
			return (optionalLenght);
		}
			
		public String getPacketType()
		{	String STypeCom="";
		
			int PacketType = Integer.parseInt(tabTrame.get(4),16);
			
			if(PacketType == 1){STypeCom="RADIO";}
			else if(PacketType == 2){STypeCom="RESPONSE";}
			else if(PacketType == 3){STypeCom="RADIO_SUB_TEL";}
			else if(PacketType == 4){STypeCom="EVENT";}
			else if(PacketType == 5){STypeCom="COMMON_COMMAND";}
			else if(PacketType == 6){STypeCom="SMART_ACK_COMMAND";}
			else if(PacketType == 7){STypeCom="REMOTE_MAN_COMMAND";}
			
			
			return(STypeCom);
		}
		
		public String getDevice (){
			
			return("");
		}
	
}
	
	

