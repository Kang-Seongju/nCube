package com.keti.tas.main;
import java.util.ArrayList;

import org.eclipse.paho.client.mqttv3.MqttMessage;

import com.keti.tas.main.*; 

import kr.re.keti.ncube.mqttclient.ResourceStructure;
public class Devices {
	public String[] deviceFunction; // HueBulb, AlljoynBulb, AlljoynPlug, Nest ..
	//public static String[] exceptDevice = {"AJ-Lamp", "AJ-Plug", "Hue-Lamp", "Sensor-"};
//	public static String[] middlewareDevice = {"AJ-Lamp", "AJ-Plug", "Hue-Lamp"};
	public static String[] middlewareDevice = {"AJ-Lamp","Hue-Lamp"};
	public String deviceName = "";
    public String deviceModel = "";
    public String specificId = "";
    public String parentPath = "";
	public String cntName = "";
	public boolean registration = false;
	
	
    public static ArrayList<Devices> mDeviceList = new ArrayList<Devices>();
    
    public static void setDevice(String dName, String dModel, String[] dFunction){
    	Devices tmp = new Devices();
    	
    	dName = dName.replaceAll(" ", "-");
    	
    	tmp.deviceName = dName;
    	tmp.deviceModel = dModel;
    	tmp.deviceFunction = dFunction;
    	if(!Devices.isContain(tmp)){
    		mDeviceList.add(tmp);
    		System.out.println("[Device Register]\n" + dName + "\n" + dModel);  	
    		for(int i = 0 ; i < dFunction.length; i ++){
    			System.out.println(dFunction[i]);
    		}
    	}
    }
    
    public static void printDevice(){
    	for(int i =0;i<mDeviceList.size();i++)
    		System.out.println("DeviceName: "+ mDeviceList.get(i).deviceName+ ", ModelNumber: " + mDeviceList.get(i).deviceModel);
    }
    
    public static void delDevice(String dName){
    	//dName = dName.replaceAll(" ", "-");
    	for(int i =0;i<mDeviceList.size();i++){
    		if(mDeviceList.get(i).deviceName.equals(dName))
    			mDeviceList.remove(i);
    	}
    }
    
    public static boolean isContain(Devices dev){
    	for(int i =0;i<mDeviceList.size();i++){
    		if(mDeviceList.get(i).deviceName.equals(dev.deviceName) && mDeviceList.get(i).deviceModel.equals(dev.deviceModel))
    			return true;
    	}
    	return false;
    }
    
    public static Devices getDevice(String dName){
    	for(int i =0;i<mDeviceList.size();i++){
    		if(mDeviceList.get(i).deviceName.equals(dName))
    			return mDeviceList.get(i);
    	}
    	return null;
    }
    
    public static String findDeviceModel(String dName){
    	for(int i =0;i<mDeviceList.size();i++){
    		if(mDeviceList.get(i).deviceName.equals(dName))
    			return mDeviceList.get(i).deviceModel;
    	}
    	return "Fail";
    }
}
