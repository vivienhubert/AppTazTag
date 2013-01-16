package com.taztag.tazpad.telegram;

import java.util.StringTokenizer;
import java.util.ArrayList;


public class Telegram {
	
	private ArrayList<String> tabTrame = new ArrayList<String>();
	private int dataLenght;
	private int optionalLenght;
	
	
	
	
	/*public static void main(String argv[]) throws Exception
	  {
		Telegram t = new Telegram("55 00 07 07 04 7a f6 01 00 27 87 7d 30 01 ff ff ff ff 3a 00 13");
		t.getDataLenght();
		t.getOptionalLenght();
		System.out.println(t.getData())
		t.getData();
	  }*/
	
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
			return (dataLenght);
		}
		
		public int getOptionalLenght()
		{
			optionalLenght = Integer.parseInt(tabTrame.get(3), 16); 
			System.out.println("OptionalLenght : "+optionalLenght);
			return (optionalLenght);
		}
			
		// Type de la trame
		
		public String getPacketType()
		{	
			String STypeCom="";
		
			int PacketType = Integer.parseInt(tabTrame.get(4),16);
			
			if(PacketType == 1){STypeCom="RADIO";}
			else if(PacketType == 2){STypeCom="RESPONSE";}
			else if(PacketType == 3){STypeCom="RADIO_SUB_TEL";}
			else if(PacketType == 4){STypeCom="EVENT";}
			else if(PacketType == 5){STypeCom="COMMON_COMMAND";}
			else if(PacketType == 6){STypeCom="SMART_ACK_COMMAND";}
			else if(PacketType == 7){STypeCom="REMOTE_MAN_COMMAND";}
			else{STypeCom = "UNKNOWN";}	
			
			return(STypeCom);
		}
		
		public String getDevice (){
			
			return("");
		}
	
		
		
		/********** GESTION DATA ************/
		
		
		
		
		public String getTelegramType(){
	
		String TelegramType ;
		String value = tabTrame.get(6);
        
             if(value.equals("F6")){TelegramType="RPS";}
             else if(value.equals("D5")){TelegramType="1BS";}
             else if(value.equals("A5")){TelegramType="4BS";}
             else {TelegramType="UNKNOWN";}
         
        return(TelegramType);      
        }
		
	
		
		
		public String getDataStatus(){
			
			/* Structure du BYTE Status 
			 * 
			 * |  7-6   |  5  |  4 |   3-2-1-0  |
			 * |RESERVED| T21 | NU | RP_COUNTER |
			 * 
			 */
			int T21;
			int NU;
			int RP_COUNTER;
			
			String status ="";
			int indexStatus = this.getDataLenght(); // Situe l'index pour savoir o� ce situe le BYTE Status
			status = tabTrame.get(6+indexStatus);
			
			
			
			
			/*if(this.getTelegramType()=="RPS"){  ;}
			else if(this.getTelegramType()=="1BS"){status=tabTrame.get();}
			else if(this.getTelegramType()=="4BS"){status=tabTrame.get(13);}
			else{status = "UNKNOWN";}	*/
			
			return(status);
		
		
		}
		
		
		
		public String getData(){
			
			String Data="";
			String valByteCurrent;
			
			if(this.getTelegramType()=="RPS")
				{  
				valByteCurrent= tabTrame.get(7);
				
				/*int i=Integer.parseInt(valByteCurrent,16); // Passage en Binaire
				String binaryVal = Integer.toBinaryString(i);*/
				
				if(valByteCurrent.equals("50")){Data=" Bouton Presse Interrupteur Bas";}
				else if(valByteCurrent.equals("70")){Data=" Bouton Presse Interrupteur Haut";}
				else if(valByteCurrent.equals("00")){Data=" Bouton Released ";}
				else if(valByteCurrent.equals("10")){Data=" Bouton Presse";}
				
				
				}
			
			/*else if(this.getTelegramType()=="1BS")
			{
				status=tabTrame.get();
			}*/
			
			else if(this.getTelegramType()=="4BS") //Gestion température pour une gamme 60 -20°
			{
			double dataTemp;
			double currentTemp;
			dataTemp = Integer.parseInt(tabTrame.get(9),16); // Passe de String en Decimal
			currentTemp=dataTemp*(0.31);
			Data = String.valueOf(currentTemp+" C°"); 		
			}
			
			else{ 
				Data = "UNKNOWN";
				
			}	
			return(Data);
		}
		
		
		
		 
		public static int getDataFromHeader(String header){
			int dataLenght = Integer.parseInt((String)(header.charAt(4)+""+header.charAt(5)), 16); 
			return (dataLenght);
		}
		
		
		
}
	
	

