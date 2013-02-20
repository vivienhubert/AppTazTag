package com.taztag.tazpad.app.zigbee;



import android.util.Log;

import com.taztag.tazpad.app.activities.AndroidNDK1SampleActivity;
import com.taztag.zigbee.Cluster;
import com.taztag.zigbee.ConfListener;
import com.taztag.zigbee.DataConf;
import com.taztag.zigbee.JoinedIndication;
import com.taztag.zigbee.NetworkListener;
import com.taztag.zigbee.NetworkUpdate;
import com.taztag.zigbee.NwkAddrRsp;
import com.taztag.zigbee.RemovedIndication;
import com.taztag.zigbee.SimpleDesc;
import com.taztag.zigbee.ZclListener;
import com.taztag.zigbee.utils.TAddress;
import com.taztag.zigbee.utils.TAddress16;
import com.taztag.zigbee.utils.TAddress64;
import com.taztag.zigbee.utils.TCluster;
import com.taztag.zigbee.utils.TDevice;
import com.taztag.zigbee.utils.TEndpoint;
import com.taztag.zigbee.utils.TProfile;
import com.taztag.zigbee.zcl.taztag.PassThroughClient;
import com.taztag.zigbee.zcl.taztag.PassThroughInd;


public class SimpDesc implements ConfListener, ZclListener, NetworkListener {
	public SimpleDesc sDesc = null;
	private TProfile profile = new TProfile(TProfile.HOME_AUTOMATION);
	private TEndpoint ep = new TEndpoint(1);
	private TDevice device = new TDevice(5);
	private Notif nf = null;
	private AndroidNDK1SampleActivity activity;
	private PassThroughClient measurement_temp;
	private int hd = -1;
	TAddress device_addr;
	
	public SimpDesc( AndroidNDK1SampleActivity _thread) {
		//Log.d("TT_ZigBee_Sample", "[SimpDesc] +++ SimpDesc +++");
		//activity = _parent;
		activity = _thread;
		nf = Notif.getInstance();
	}

	public void init() {
		//Log.d("TT_ZigBee_Sample", "[SimpDesc] +++ init +++");
		sDesc = SimpleDesc.create(profile, ep, device, (byte) 1);
	}
	
	public void addClusters() {
		//Log.d("TT_ZigBee_Sample", "[SimpDesc] +++ addCluster +++");
		measurement_temp=(PassThroughClient) sDesc.addPassThroughCluster(new TCluster(0x0402), Cluster.CLIENT);
		measurement_temp.setRadius(3);
		measurement_temp.setTxoption(4);
		
	}

	public void onCommand(Object o, int clusterId, int command) {
		//Log.d("TT_ZigBee_Sample", "[SimpDesc] +++ onCommand +++");
		PassThroughInd rdi = (PassThroughInd) o;
		TAddress srcAddr =  rdi.getSrcAddress();
		String srcAddrString =null;
		int srcAddrMode = srcAddr.getAddressMode();
		if (srcAddrMode == 2) {
			TAddress16 tAddress16 = (TAddress16) srcAddr;
			// Converts to hexadecimal
			srcAddrString = tAddress16.toHexString();
		} else if (srcAddrMode == 3) {
			TAddress64 tAddress64 = (TAddress64) srcAddr;
			// Converts to hexadecimal
			srcAddrString = tAddress64.toHexString();

		} else {
			//Log.d("TT_ZigBee_Sample", "[SimpDesc] Error="	+ "Zigbee bad address");
			return;
		}
		int size=0;
		size=rdi.getData().available();
		//Log.d("Size of Data available", "[SimpDesc] =" + size);
		if (size == -1) {
			//Log.d("TT_ZigBee_Sample", "[SimpDesc] Error="	+ "reading zigbee data");
			return;
		}
		
		byte[] data =new byte[size] ;
		//Log.d("ZigBee", "[SimpDesc] ="	+ "Start to receive data");
		String trame = "";
		for ( int k=0; k < size; k++){
			data[k] = (byte) rdi.getData().read();
			trame += data[k];
			Log.d("Sensor Data Trame", "[SimpDesc] =" + trame);
			//Log.d("Sensor Data", "[SimpDesc] =" + data[k]);
			
		}
	
		int temp_value=Integer.parseInt(trame.substring(5), 16);
		double value=(double) Math.abs(temp_value/100);
		
		activity.updateMyValue(value);
		//Log.d("ZigBee", "[SimpDesc] ="	+ "finished to receive data : "+value);
		
		//Log.d("TT_ZigBee_Sample", "[SimpDesc] Zigbee=" + srcAddrMode + ";"	+ srcAddrString + ";" + new String(data));
		activity.update(Notif.DATA_RESPONSE, new String(data));
		
	}

	public void networkInd(int type, Object ind) {
		//Log.d("TT_ZigBee_Sample", "[SimpDesc] networkInd");
		switch (type) {
		case NetworkListener.CHILD_JOINED:
			//Log.d("TT_ZigBee_Sample", "[SimpDesc] CHILD JOINED");
			JoinedIndication ind0 = (JoinedIndication) ind;
			//Log.d("TT_ZigBee_Sample", "[SimpDesc] Child joined: ea="+ ind0.getExtAdd().toHexString() + " - sa="+ ind0.getShortAdd().toHexString() + " - capability="+ ind0.getCapability());
			//Log.d("TT_ZigBee_Sample", "[SimpDesc] On reconnait l'add switch");
			nf.setConnected(true);
			nf.joined();
			nf.setAddr(ind0.getExtAdd().toHexString());
			nf.setAddr16(ind0.getShortAdd());
			nf.setCanSend(true);
			//Log.d("TT_ZigBee_Sample", "[SimpDesc] Switch connected");
			readFromTarget();
			activity.update(Notif.CONNECTED, 0);
			break;
		case NetworkListener.CHILD_REMOVED:
			//Log.d("TT_ZigBee_Sample", "[SimpDesc] CHILD REMOVED");
			RemovedIndication ind1 = (RemovedIndication) ind;
			//Log.d("TT_ZigBee_Sample","[SimpDesc] Child removed: ea="+ ind1.getExtAdd().toHexString() + " - sa="+ ind1.getShortAdd().toHexString() + " addr = "+ nf.getAddr());
			if (ind1.getExtAdd().toHexString()
					.compareToIgnoreCase(nf.getAddr()) == 0) {
				nf.setConnected(false);
				nf.setCanSend(false);
				nf.disjoined();
				activity.update(Notif.DISCONNECTED, 0);
				//Log.d("TT_ZigBee_Sample", "[SimpDesc] disconnected");
			}
			break;
		case NetworkListener.NETWORK_LEFT:
			//Log.d("TT_ZigBee_Sample", "[SimpDesc] NETWORK LEFT");
			NetworkUpdate ind2 = (NetworkUpdate) ind;
			//Log.d("TT_ZigBee_Sample",		"[SimpDesc] Network left(" + Integer.toHexString(type)+ ") - parentsa="+ ind2.getParentShortAdd().toHexString()+ " - panid=" + ind2.getPanId().toHexString()+ " - channel=" + ind2.getCurrentChannel()+ " - sa=" + ind2.getShortAdd().toHexString());
			break;
		case NetworkListener.NETWORK_LOST:
			//Log.d("TT_ZigBee_Sample", "[SimpDesc] NETWORK LOST");
			NetworkUpdate ind3 = (NetworkUpdate) ind;
			//Log.d("TT_ZigBee_Sample",	"[SimpDesc] Network lost(" + Integer.toHexString(type)+ ") - parentsa="+ ind3.getParentShortAdd().toHexString()	+ " - panid=" + ind3.getPanId().toHexString()+ " - channel=" + ind3.getCurrentChannel()+ " - sa=" + ind3.getShortAdd().toHexString());
			break;
		case NetworkListener.NETWORK_STARTED:
			//Log.d("TT_ZigBee_Sample", "[SimpDesc] NETWORK STARTED");
			NetworkUpdate ind4 = (NetworkUpdate) ind;
			//Log.d("TT_ZigBee_Sample",	"[SimpDesc] Network started(" + Integer.toHexString(type)+ ") - parentsa="+ ind4.getParentShortAdd().toHexString()	+ " - panid=" + ind4.getPanId().toHexString()+ " - channel=" + ind4.getCurrentChannel()	+ " - sa=" + ind4.getShortAdd().toHexString());
			break;
		}
	}

	public void apsConfirmation(int type, int handle, Object ob) {
		//Log.d("TT_ZigBee_Sample", "[SimpDesc] aps confirmation");
		switch (type) {
		case ConfListener.APS_REGISTER_EP_CNF:
			//Log.d("TT_ZigBee_Sample", "[SimpDesc] APS_REGISTER_EP_CNF hd = "	+ nf.getHandle() + " - handle = " + handle);

			if (nf.getHandle() == handle) {
				//Log.d("TT_ZigBee_Sample",	"[SimpDesc] APS_REGISTER_EP_CNF hd was equals to handle ; now hd = "								+ hd);
				activity.update(Notif.STARTED, 0);
			}

			break;
		case ConfListener.APS_UNREGISTER_EP_CNF:
			//Log.d("TT_ZigBee_Sample", "[SimpDesc] APS_UNREGISTER_EP_CNF // "	+ "unregisterEndpointConf handle = " + handle + " - res = "					+ ((Integer) ob).intValue());
			break;
		case ConfListener.APS_DATA_CNF:
			//Log.d("TT_ZigBee_Sample", "[SimpDesc] APS_DATA_CNF + handle = "+ handle + " - +hd = " + nf.getHandle() + " - status = "	+ ((DataConf) ob).getStatus());
			nf.setCanSend(true);
			if (((DataConf) ob).getStatus() != 0) {
				//Log.d("TT_ZigBee_Sample", "[SimpDesc] APS_DATA_CNF // cas 2 ");
				nf.setConnected(false);
				nf.setCanSend(false);
				activity.update(Notif.DISCONNECTED, 0);
			} else
				nf.setCanSend(true);
			break;
		default:
		}
		//Log.d("TT_ZigBee_Sample", "[SimpDesc] FIN aps confirmation");
	}

	public void zdoConfirmation(int type, int handle, Object ob) {
		//Log.d("TT_ZigBee_Sample", "[SimpDesc] zdo confirmation");
		switch (type) {
		case ConfListener.ZDO_STARTNETWORK_CNF:
			//Log.d("TT_ZigBee_Sample",	"[SimpDesc] ZDO_STARTNETWORK_CNF 0 ; hd = "	+ nf.getHandle() + " ; handle = " + handle);
			int res = ((Integer) ob).intValue();
			//Log.d("TT_ZigBee_Sample",	"[SimpDesc] ZDO_STARTNETWORK_CNF - test = " + res);
			nf.setCanSend(false);
			if (res == 0) {
				nf.setHandle(sDesc.registerEndpoint());
				//Log.d("TT_ZigBee_Sample",	"[SimpDesc] ZDO_STARTNETWORK_CNF 1 ; hd = "	+ nf.getHandle() + " ; handle = " + handle);
			} else {
				//Log.d("TT_ZigBee_Sample", "[SimpDesc] startNetwork fails("+ Integer.toHexString(res) + ")");
				nf.setCanSend(false);
				activity.update(Notif.RESTART_ZIGBEE, 0);
			}
			break;
		case ConfListener.ZDO_MGMTLEAVE_CNF:
			//Log.d("TT_ZigBee_Sample", "[SimpDesc] ZDO_MGMTLEAVE_CNF ; hd = "	+ nf.getHandle() + " ; handle = " + handle);

			int res2 = ((Integer) ob).intValue();
			if (res2 == 0) {
				//Log.d("TT_ZigBee_Sample", "[SimpDesc] mgmtLeave succeeds");
			} else {
				//Log.d("TT_ZigBee_Sample", "[SimpDesc] mgmtLeave fails("	+ Integer.toHexString(res2) + ")");
			}
			break;
		case ConfListener.ZDO_RESET_CNF:
			;//Log.d("TT_ZigBee_Sample", "[SimpDesc] resetConf handle " + handle	+ " - res=" + ((Integer) ob).intValue());			break;
		default:
		}
	}

	public void zdoResponse(int type, int handle, Object ob) {
		//Log.d("TT_ZigBee_Sample", "[SimpDesc] ZDO_NWKADDR_RSP - handle = "+ handle + " - hd = " + nf.getHandle() + " - addr = "				+ ((NwkAddrRsp) ob).getNwkAddrRemoteDev().toHexString());
		switch (type) {
		case ConfListener.ZDO_NWKADDR_RSP:
			//Log.d("TT_ZigBee_Sample",	"[SimpDesc] ZDO_NWKADDR_RSP : " + nf.getHandle() + " handle = " + handle + " connected : "+ nf.isConnected());
			if (nf.getAddr16() != null
					&& ((NwkAddrRsp) ob).getNwkAddrRemoteDev().toHexString()
							.toUpperCase()
							.equals(nf.getAddr16().toHexString().toUpperCase())) {
				nf.setCanSend(true);
				//Log.d("TT_ZigBee_Sample",	"[SimpDesc] ZDO_NWKADDR_RSP SWITCH CONNECTED");				activity.update(Notif.CONNECTED, 0);
			}
			break;
		default:
		}
	}

	public void readFromTarget() {
		//Log.d("TT_ZigBee_Sample", "[SimpDesc] +++ readFromTarget");
		device_addr=TAddress64.parseHex("0123456789ABCDEF"); 
		byte[] data =new byte[2] ;
		data[0]=0x00;
		data[1]=0x00;
		//Log.d("TT_ZigBee_Sample", "[SimpDesc] +++ readFromTarget not null");
		measurement_temp.command(device_addr, new TEndpoint(1), 0, false, 0, 0, 0, data);
		//Log.d("ZIGBEE", "data received");
		
	}
}
