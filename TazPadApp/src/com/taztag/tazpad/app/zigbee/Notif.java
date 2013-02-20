package com.taztag.tazpad.app.zigbee;

import com.taztag.zigbee.utils.TAddress16;



public class Notif {
	public static final int CONNECTED = 20;
	public static final int DISCONNECTED = 21;
	public static final int DATA_RESPONSE = 25;
	public static final int RESTART_ZIGBEE = 11;
	public static final int CLOSE_ZIGBEE = 12;
	public static final int STARTED = 01;

	private static Notif nf = new Notif();

	private boolean connected;
	private String addr;
	private TAddress16 addr16;
	private boolean canSend = false;
	private int hd = -1;
	private boolean joined = false;

	
	
	/** initialisation de Notif */
	private Notif() {
		addr = "";
		addr16 = null;
		connected = false;
		canSend = false;
		joined = false;
		hd = -1;
	}

	public boolean hasJoined() {
		return joined;
	}

	public void joined() {
		joined = true;
	}

	public void disjoined() {
		joined = false;
	}

	public boolean isConnected() {
		return connected;
	}

	public String getAddr() {
		return addr;
	}

	public void setAddr(String _addr) {
		addr = _addr;
	}

	public TAddress16 getAddr16() {
		return addr16;
	}

	public void setAddr16(TAddress16 _addr16) {
		addr16 = _addr16;
	}

	public void setConnected(boolean isConnected) {
		connected = isConnected;
		if (isConnected == false) {
			addr16 = null;
			addr = "";
		}
	}

	public int getHandle() {
		return hd;
	}

	public void setHandle(int _hd) {
		hd = _hd;
	}

	public static Notif getInstance() {
		return nf;
	}

	public boolean getCanSend() {
		return canSend;
	}

	public void setCanSend(boolean _canSend) {
		canSend = _canSend;
	}
}