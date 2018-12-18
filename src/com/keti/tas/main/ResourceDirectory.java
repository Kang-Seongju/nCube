package com.keti.tas.main;

import java.util.ArrayList;

/*
 * Edit; GW
 * MQTT Resource Directory
 * */

public class ResourceDirectory {
	public static ArrayList<Resource> resourceDir = new ArrayList<Resource>();
	
	public void insertService(String device, String service, String ip){
		if(isContain(device, service)) return;
		Resource tmp = new Resource(device, service, ip);
		resourceDir.add(tmp);
	}
	
	public void deleteService(String device, String service){
		if(!isContain(device, service)) return;
		
		for(int i=0;i<resourceDir.size();i++){
			if(resourceDir.get(i).mDevice.equals(device) && resourceDir.get(i).mService.equals(service))
				resourceDir.remove(i);
		}
	}
	
	public boolean isContain(String device, String service){
		for(int i=0;i<resourceDir.size();i++){
			if(resourceDir.get(i).mDevice.equals(device) && resourceDir.get(i).mService.equals(service))
				return true;
		}
		return false;
	}
	
	public void print(){
		System.out.println("=========================[Resource Dir.]==========================");
		for(int i=0;i<resourceDir.size();i++){
			System.out.println("Device: " + resourceDir.get(i).mDevice);
			System.out.println("Service: " + resourceDir.get(i).mService);
			System.out.println("Address: " + resourceDir.get(i).mIp);
			System.out.println("--------------------------------------------");
		}
		System.out.println("==================================================================");
	}
}
