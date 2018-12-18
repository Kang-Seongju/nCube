package com.keti.tas.conf;

public class DeviceInfo {
	private String container_name;
	private String deivce_id;
	private boolean isHello;
	
	public DeviceInfo(){
		this.isHello = false;
	}
	
	public void setContainerName(String value){
		this.container_name = value;
	}
	
	public void setDeviceID(String value){
		this.deivce_id = value;
	}
	
	public void setHelloStatus(boolean value){
		this.isHello = value;
	}
	
	public String getContainerName(){
		return this.container_name;
	}
	
	public String getDeviceID(){
		return this.deivce_id;
	}
	
	public boolean getHelloStatus(){
		return this.isHello;
	}
}
