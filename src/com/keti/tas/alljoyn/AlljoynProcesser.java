package com.keti.tas.alljoyn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.alljoyn.bus.AboutListener;
import org.alljoyn.bus.AboutObjectDescription;
import org.alljoyn.bus.BusAttachment;
import org.alljoyn.bus.BusException;
import org.alljoyn.bus.Variant;
import org.alljoyn.bus.BusAttachment.RemoteMessage;
import org.alljoyn.bus.Status;

import com.keti.tas.main.*; 

public class AlljoynProcesser extends Thread{

	static{
		System.loadLibrary("alljoyn_java");
	}
	
	private static final Logger LOG = Logger.getLogger(AlljoynProcesser.class.getName());
	
	public static HashMap<String, AlljoynDevice> alljoynDevices = new HashMap<>();
	
	public static ArrayList<AlljoynDevice> alldev = new ArrayList<>();
	public static ArrayList<LifxAlljoynLight> light = new ArrayList<>();
	
	private BusAttachment mBus;

	private boolean isActived = false;
	
	private AlljoynDeviceAnounceHandler ajDevCon = null;
	
	private AlljoynDeviceSensingHandler ajDevSen = null;
	
	public AlljoynProcesser() {
		
		mBus = new BusAttachment("AlljoynZone", RemoteMessage.Receive);
		
		LOG.log(Level.INFO, "Alljoyn bus initialization...");
		
		this.isActived = false;
	}
	
	public void setDeviceConnectListener(AlljoynDeviceAnounceHandler handler){
		this.ajDevCon = handler;
	}
	
	public void setDeviceSensingListener(AlljoynDeviceSensingHandler handler){
		this.ajDevSen = handler;
	}
	
	public void StopProcesser(){
		
		this.isActived = true;
		//this.isActived = false;
	}
	
	@Override
	public void run() {
		
		LOG.log(Level.INFO, "Alljoyn proccessor starting...");
		
		isActived = true;
		
		if(mBus.connect() == Status.OK){
			
			LOG.log(Level.INFO, "Alljoyn bus connect succussfully...");
			
			mBus.registerAboutListener(new AboutListener() {
				
				@Override
				public void announced(String busName, int version, short port,
						AboutObjectDescription[] objectDescriptions,
						Map<String, Variant> aboutData) {
					
					System.out.println("Announced");
					try{
						String deviceName = aboutData.get("DeviceName").getObject(String.class);
						String deviceId = aboutData.get("DeviceId").getObject(String.class);
						String manufacturer = aboutData.get("Manufacturer").getObject(String.class);
						String modelNumber = aboutData.get("ModelNumber").getObject(String.class);
						byte[] appID = aboutData.get("AppId").getObject(byte[].class);
						String strAppID = "";
						
						deviceName = deviceName.replaceAll(" ", "-");
						
						System.out.println("deviceName : "+deviceName);
						System.out.println("deviceId : " + deviceId);
						System.out.println("manufacturer : " + manufacturer);
						System.out.println("modelNumber : " + modelNumber);
						
						for(byte b : appID){
							strAppID += String.format("%02X", b);
						}
						
						String defaultLanguage = aboutData.get("DefaultLanguage").getObject(String.class);
						String appName = aboutData.get("AppName").getObject(String.class);
					
						
						if( modelNumber.equals("Smart Plug") && manufacturer.equals("Powertech")){
							
							LOG.log(Level.INFO, "Found a Powertech Smart Plug named <" + deviceName + "> from bus <" + busName + ">");
							PowertechAlljoynPlug device = new PowertechAlljoynPlug(mBus, busName, port);
							device.SetModelNumber(modelNumber);
							device.SetAppID(strAppID);
							device.SetDefaultLanguage(defaultLanguage);
							device.SetDeviceId(deviceId);
							device.SetManufacturer(manufacturer);
							device.SetDeviceName(deviceName);
							device.SetAppName(appName);
							
							device.MonitoringStart();
							alldev.add(device);
							Devices.setDevice(deviceName, modelNumber, DeviceFunction.AlljoynPlug);
							
							if(ajDevCon != null){
								ajDevCon.AlljoynDeviceConnected(device);
							}
							
							if(ajDevSen != null){
								device.RegisterSensingDataLister(ajDevSen);
							}
							
							if(!AlljoynProcesser.alljoynDevices.containsKey(deviceId)){
								AlljoynProcesser.alljoynDevices.put(deviceId, device);
							}
						} 
//						else if(modelNumber.equals("0.0.1") && manufacturer.equals("Company A(EN)")){
//							
//							LOG.log(Level.INFO, "Found a Dewon Smart Plug named <" + deviceName + "> from bus <" + busName + ">");
//							System.out.println("2");
//							DewonAlljoynPlug device = new DewonAlljoynPlug(mBus, busName, port);
//							device.SetModelNumber(modelNumber);
//							device.SetAppID(strAppID);
//							device.SetDefaultLanguage(defaultLanguage);
//							device.SetDeviceId(deviceId);
//							device.SetManufacturer(manufacturer);
//							device.SetDeviceName(deviceName);
//							device.SetAppName(appName);
//							
//							device.MonitoringStart();
//							
//							if(ajDevCon != null){
//								ajDevCon.AlljoynDeviceConnected(device);
//							}
//							
//							if(ajDevSen != null){
//								device.RegisterSensingDataLister(ajDevSen);
//							}
//							
//							if(!AlljoynProcesser.alljoynDevices.containsKey(deviceId)){
//								AlljoynProcesser.alljoynDevices.put(deviceId, device);
//							}
//						}
//						else if(modelNumber.equals("LIFX White 800") && manufacturer.equals("LIFX")){
//							LOG.log(Level.INFO, "Found a LIFX White 800 light named <" + deviceName + "> from bus <" + busName + ">");
//							System.out.println("3");
//							LifxAlljoynLight device = new LifxAlljoynLight(mBus, busName, port, DeviceTarget.LIFX_WHITE_800);
//							device.SetModelNumber(modelNumber);
//							device.SetAppID(strAppID);
//							device.SetDefaultLanguage(defaultLanguage);
//							device.SetDeviceId(deviceId);
//							device.SetManufacturer(manufacturer);
//							device.SetDeviceName(deviceName);
//							device.SetAppName(appName);
//							
//							device.MonitoringStart();
//							
//							if(ajDevCon != null){
//								ajDevCon.AlljoynDeviceConnected(device);
//							}
//							
//							if(ajDevSen != null){
//								device.RegisterSensingDataLister(ajDevSen);
//							}
//							
//							if(!AlljoynProcesser.alljoynDevices.containsKey(deviceId)){
//								AlljoynProcesser.alljoynDevices.put(deviceId, device);
//							}
//						}
//						else if(modelNumber.equals("LIFX OEM 750 HV") && manufacturer.equals("LIFX")){
//							LOG.log(Level.INFO, "Found a LIFX 0EM 750 HV named <" + deviceName + "> from bus <" + busName + ">");
//							System.out.println("3");
//							LifxAlljoynLight device = new LifxAlljoynLight(mBus, busName, port, DeviceTarget.LIFX_0EM_750_HV);
//							device.SetModelNumber(modelNumber);
//							device.SetAppID(strAppID);
//							device.SetDefaultLanguage(defaultLanguage);
//							device.SetDeviceId(deviceId);
//							device.SetManufacturer(manufacturer);
//							device.SetDeviceName(deviceName);
//							device.SetAppName(appName);
//							
//							device.MonitoringStart();
//							
//							if(ajDevCon != null){
//								ajDevCon.AlljoynDeviceConnected(device);
//							}
//							
//							if(ajDevSen != null){
//								device.RegisterSensingDataLister(ajDevSen);
//							}
//							
//							if(!AlljoynProcesser.alljoynDevices.containsKey(deviceId)){
//								AlljoynProcesser.alljoynDevices.put(deviceId, device);
//							}
//						}
						else if(modelNumber.equals("LIFX Color 1000") && manufacturer.equals("LIFX")){
							//deviceName = "AJ-Lamp";
							LOG.log(Level.INFO, "Found a LIFX White 1000 light named <" + deviceName + "> from bus <" + busName + ">");
							System.out.println("LIFX device get");
							LifxAlljoynLight device = new LifxAlljoynLight(mBus, busName, port, DeviceTarget.LIFX_COLOR_1000);
							device.SetModelNumber(modelNumber);
							device.SetAppID(strAppID);
							device.SetDefaultLanguage(defaultLanguage);
							device.SetDeviceId(deviceId);
							device.SetManufacturer(manufacturer);
							device.SetDeviceName(deviceName);
							device.SetAppName(appName);
							
							device.MonitoringStart();
							
							Devices.setDevice(deviceName, modelNumber, DeviceFunction.AlljoynBulb);
							
							light.add(device);
							device.SetColorTemp(3);
							if(ajDevCon != null){
								ajDevCon.AlljoynDeviceConnected(device);
							}
							
							if(ajDevSen != null){
								device.RegisterSensingDataLister(ajDevSen);
							}
							
							if(!AlljoynProcesser.alljoynDevices.containsKey(deviceId)){
								AlljoynProcesser.alljoynDevices.put(deviceId, device);
							}
						}
//						else if(modelNumber.equals("1") && manufacturer.equals("Qualcomm Connected Experiences, Inc.")) {
//							LOG.log(Level.INFO, "Found a Musaic MP10 named <" + deviceName + "> from bus <" + busName + ">");
//							System.out.println("music");
//							MusaicMP10 device = new MusaicMP10(mBus, busName, port);
//							device.SetModelNumber(modelNumber);
//							device.SetAppID(strAppID);
//							device.SetDefaultLanguage(defaultLanguage);
//							device.SetDeviceId(deviceId);
//							device.SetManufacturer(manufacturer);
//							device.SetDeviceName(deviceName);
//							device.SetAppName(appName);
//							
//							device.MonitoringStart();
//							
//							if(ajDevCon != null){
//								ajDevCon.AlljoynDeviceConnected(device);
//							}
//							
//							if(ajDevSen != null){
//								device.RegisterSensingDataLister(ajDevSen);
//							}
//							
//							if(!AlljoynProcesser.alljoynDevices.containsKey(deviceId)){
//								AlljoynProcesser.alljoynDevices.put(deviceId, device);
//							}
//						}
					} catch (BusException ex){
						LOG.log(Level.WARNING, ex.getMessage());
					}
				}
			});
			
			
			String ifaces[] = null;
			
			if(mBus.whoImplements(ifaces) == Status.OK){
				
				LOG.log(Level.INFO, "Discovery device in local network...");
				
				while(isActived){
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			
			
//			for(String key : AlljoynProcesser.alljoynDevices.keySet()){
//				if(AlljoynProcesser.alljoynDevices.get(key).isJoinSession){
//					AlljoynProcesser.alljoynDevices.get(key).MonitoringStop();
//					AlljoynProcesser.alljoynDevices.get(key).LeaveSession();
//				}
//			}
//			
//			mBus.disconnect();
//	
//			LOG.log(Level.INFO, "Alljoyn bus disconnect succussfully...");
		}
	}
}
