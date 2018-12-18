package com.keti.tas.main;

import kr.re.keti.ncube.mqttclient.ResourceStructure;

import com.keti.tas.alljoyn.AlljoynDevice;
import com.keti.tas.alljoyn.AlljoynProcesser;
import com.keti.tas.alljoyn.PowertechAlljoynPlug;
import com.keti.tas.main.MainProcesser.AlljoynSensingReceiver;

public class DeviceMonitoring extends Thread{
	
	@Override
	public void run() {
		while(true){
			try {
				// Every 1 minute, updates target device information
				for(int i =0;i<Devices.mDeviceList.size();i++){
		    		if(Devices.mDeviceList.get(i).deviceModel.equals("Smart Plug")){
		    			for(AlljoynDevice dev: MainProcesser.alljoyn.alldev){ 
							String Property = ((PowertechAlljoynPlug)dev).GetProperties();
							ResourceStructure.updateContentInstance(dev.GetDeviceName(), "Status" ,Property);
		    			}
		    		}
		    	}
				Thread.sleep(60000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
