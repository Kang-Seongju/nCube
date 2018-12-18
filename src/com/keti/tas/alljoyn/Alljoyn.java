package com.keti.tas.alljoyn;


import com.keti.tas.main.MainProcesser;
import com.keti.tas.main.MainProcesser.AlljoynSensingReceiver;

public class Alljoyn extends Thread{
	

	@Override
	public void run() {
		MainProcesser.alljoyn = new AlljoynProcesser();
		MainProcesser.alljoyn.setDeviceSensingListener(new AlljoynSensingReceiver());
		MainProcesser.alljoyn.start();
	}
}
