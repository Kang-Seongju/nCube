package com.keti.tas.alljoyn;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.alljoyn.bus.BusAttachment;
import org.alljoyn.bus.Mutable;
import org.alljoyn.bus.SessionOpts;
import org.alljoyn.bus.Status;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import com.keti.tas.alljoyn.AlljoynProcesser;

import com.keti.tas.main.*; 
import com.keti.tas.main.MainProcesser.AlljoynSensingReceiver;
import com.keti.tas.alljoyn.AlljoynDeviceSensingHandler;
import kr.re.keti.ncube.mqttclient.ResourceStructure;
import kr.re.keti.ncube.mqttclient.Translation;

public abstract class AlljoynDevice extends Thread {
	
	static{
		System.loadLibrary("alljoyn_java");
	}
	
	private static final Logger LOG = Logger.getLogger(AlljoynDevice.class.getName());
	
	public DeviceTarget target;
	
	protected AlljoynDeviceSensingHandler ajDataRecv = null;
	
	private JoinSessionFailedHandler sessionHandler = null;
	
    protected String DeviceName = "";
    protected String ModelNumber = "";
    protected String DefaultLanguage = "";
    protected String DeviceId = "";
    protected String Manufacturer = "";
    protected int MaxLength = -1;
    protected String AppName = "";
    protected String AppID = ""; 
    
    protected BusAttachment mBus;
    protected String busName;
    protected short port = -1;
    //private short fix_port = 1000;
    
    protected boolean isActived = false;
    
    public static boolean isActivedPlug = false;
    
    protected SessionOpts sessionOpts;
    
	protected Mutable.IntegerValue sessionId;
	protected int tickTime = 1000;
	
	protected boolean isJoinSession = false;
	
	public AlljoynDevice(BusAttachment mBus, String busName, short port){
		
		this.mBus = mBus;
		this.busName = busName;
		this.port = port;
		
		isActived = false;
		isJoinSession = false;

	}
	
	public void setOnJoinSessionFailedHandler(JoinSessionFailedHandler handler){
		this.sessionHandler = handler;
	}
	
	public void JoinSession(){
		
		try{
			sessionOpts = new SessionOpts();
			
			sessionId = new Mutable.IntegerValue();
			
	        sessionOpts.traffic = SessionOpts.TRAFFIC_MESSAGES;
	        sessionOpts.isMultipoint = false;
	        sessionOpts.proximity = SessionOpts.PROXIMITY_ANY;
	        sessionOpts.transports = SessionOpts.TRANSPORT_ANY;
	
	        mBus.enableConcurrentCallbacks();
	        
	        Status status = mBus.joinSession(busName, port, sessionId, sessionOpts, new AlljoynDeviceSession());
	        
			if( status == Status.OK)
			{	
				LOG.log(Level.INFO,  DeviceName + " had join a session in bus <" + busName +"> successfully!");
				
//				Translation.makeResourceOrientedArchitecture();
				ResourceStructure.createContainerRequest(DeviceName, "");
				System.out.println("DeviceName: " + DeviceName + " length: " + Devices.getDevice(DeviceName).deviceFunction.length);
//				
////				for(int i = 0 ; i < Devices.getDevice(DeviceName).deviceFunction.length ; i ++){
////					
////				}
				
				for(int j =0; j < Devices.getDevice(DeviceName).deviceFunction.length; j++){
					Thread.sleep(300);
					ResourceStructure.createContainerRequest(Devices.getDevice(DeviceName).deviceFunction[j], "/"+DeviceName);
					System.out.println("DeviceName: " + DeviceName + " ServiceName: " + Devices.getDevice(DeviceName).deviceFunction[j]);
				}
				isJoinSession = true;
				
	
			} else {
				LOG.log(Level.SEVERE, DeviceName + " had join a session in bus <" + busName +"> fail!");
				System.out.println("JOIN SESSION FAIL");
				Devices.delDevice(DeviceName);
				ResourceStructure.deleteContainerRequest(DeviceName);
			
				isJoinSession = false;
				throw new Exception("Join session failed!");
			}
		} catch (Exception e) {
			
			LeaveSession();
			
			if(this.sessionHandler != null){
				this.sessionHandler.joinSessionFailed();
				System.out.println("SessionFailed()");
			} 
			LOG.log(Level.SEVERE, e.getMessage());
		}
	}
	
	public void LeaveSession(){
//		mBus.leaveJoinedSession(sessionId.value);
//		
//		isJoinSession = false;
//		
//		AlljoynProcesser.alljoynDevices.clear();
//		
//		MainProcesser.devices.clear();
//		
//		MainProcesser.alljoyn.StopProcesser();
//		MainProcesser.alljoyn = new AlljoynProcesser();
//		MainProcesser.alljoyn.setDeviceSensingListener(new AlljoynSensingReceiver());
//		MainProcesser.alljoyn.start();
		
	}
	
	public void RegisterSensingDataLister(AlljoynDeviceSensingHandler hanler){
		this.ajDataRecv = hanler;
	}
	
	public int GetSenssionID(){
		return sessionId.value;
	}
	
	public String GetDeviceName(){
		return this.DeviceName;
	}
	
	public String GetModelNumber(){
		return this.ModelNumber;
	}
	
	public String GetDefaultLanguage(){
		return this.DefaultLanguage;
	}
	
	public String GetDeviceId(){
		return this.DeviceId;
	}
	
	public String GetManufacturer(){
		return this.Manufacturer;
	}
	
	public int GetMaxLength(){
		return this.MaxLength;
	}
	
	public String GetAppName(){
		return this.AppName;
	}
	
	public String GetAppID(){
		return this.AppID;
	}
	
	public void SetDeviceName(String value){
		this.DeviceName = value;
	}
	
	public void SetModelNumber(String value){
		this.ModelNumber = value;
	}
	
	public void SetDefaultLanguage(String value){
		this.DefaultLanguage = value;
	}
	
	public void SetDeviceId(String value){
		this.DeviceId =  value;
	}
	
	public void SetManufacturer(String value){
		this.Manufacturer = value;
	}
	
	public void SetMaxLength(int value){
		this.MaxLength = value;
	}
	
	public void SetAppName(String value){
		this.AppName = value;
	}
	
	public void SetAppID(String value){
		this.AppID = value;
	}
	
	public void MonitoringStart(){
		isActived = true;
		
		LOG.log(Level.INFO, "[" + DeviceId + "] sensing data monitorring start...");
		
		this.start();
	}
	
	public void MonitoringStop(){
		
		LOG.log(Level.INFO, "[" + DeviceId + "] sensing data monitorring stop...");
		
		isActived = false;
	}
	
}
