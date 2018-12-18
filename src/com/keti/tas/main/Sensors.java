package com.keti.tas.main;

import java.util.ArrayList;

import kr.re.keti.ncube.mqttclient.ResourceStructure;


public class Sensors {
	public String name = "";
	public String isAlive = "";
	
	public static ArrayList<Sensors> mSensorList = new ArrayList<Sensors>();
	
	public static void addSensor(String name){
		Sensors tmp = new Sensors();
    	
    	tmp.name = name;
    	tmp.isAlive = "Initialize";
    	if(!Sensors.isContain(tmp))
    		mSensorList.add(tmp);
	}
	
	public static boolean isContain(Sensors sen){
    	for(int i =0;i<mSensorList.size();i++){
    		if(mSensorList.get(i).name.equals(sen.name))
    			return true;
    	}
    	return false;
    }
	
	public static void delSensor(String name){
    	for(int i =0;i<mSensorList.size();i++){
    		if(mSensorList.get(i).name.equals(name))
    			mSensorList.remove(i);
    	}
    	ResourceStructure.deleteContainerRequest(name);
    }
	
	public static void printSensor(){
    	for(int i =0;i<mSensorList.size();i++)
    		System.out.println("SensorName: "+ mSensorList.get(i).name+ ", isAlive: " + mSensorList.get(i).isAlive);
    }
}
