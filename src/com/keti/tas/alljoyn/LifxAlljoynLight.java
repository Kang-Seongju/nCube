package com.keti.tas.alljoyn;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.alljoyn.bus.BusAttachment;
import org.alljoyn.bus.BusException;
import org.alljoyn.bus.ProxyBusObject;
import org.alljoyn.bus.ifaces.Introspectable;


public class LifxAlljoynLight extends AlljoynDevice{

	private static Logger LOG = Logger.getLogger(LifxAlljoynLight.class.getName());
	
	private static final String LIFX_SERVICE_PATH = "/org/allseen/LSF/Lamp"; 
	
	private int colorTempDegree = -1;
	private int hueDegree = -1;
	private String powerStatus = "";
	private String beforeStatus = "";
	
	private Class<?> ifaces[] = { Introspectable.class, LifxServiceInterface.class};
	
	public LifxAlljoynLight(BusAttachment mBus, String busName, short port) {
		super(mBus, busName, port);
		// TODO Auto-generated constructor stub
		
		this.target = DeviceTarget.LIFX_WHITE_800;
	}
	
	public LifxAlljoynLight(BusAttachment mBus, String busName, short port, DeviceTarget dt) {
		super(mBus, busName, port);
		// TODO Auto-generated constructor stub
		
		this.target = dt;
	}
	
	public void TurnON(){
		if(!isJoinSession){
			JoinSession();
		}
		
		if(isJoinSession){
			ProxyBusObject mProxy = mBus.getProxyBusObject(busName, LIFX_SERVICE_PATH, sessionId.value, ifaces);
			LifxServiceInterface controlProxy = mProxy.getInterface(LifxServiceInterface.class);
			
			try {
				controlProxy.setOnOff(true);
				
				LOG.log(Level.INFO, "Device <" + DeviceId + "> power on!");
				
			} catch (BusException e) {
				
				LOG.log(Level.WARNING,e.getMessage());
			}
		}
	}
	
	public void TurnOFF(){
		//System.out.println("turn off device");
		if(!isJoinSession){
			JoinSession();
		}
		
		if(isJoinSession){
			ProxyBusObject mProxy = mBus.getProxyBusObject(busName, LIFX_SERVICE_PATH, sessionId.value, ifaces);
			LifxServiceInterface controlProxy = mProxy.getInterface(LifxServiceInterface.class);
			
			try {
				controlProxy.setOnOff(false);
				
				LOG.log(Level.INFO, "Device <" + DeviceId + "> power off!");
				
			} catch (BusException e) {
				
				LOG.log(Level.WARNING,e.getMessage());
			}
		}
	}
	
	public void SetHue(int degree){
		
		if(degree <= 0 || degree > 360){
			return;
		}
		
		if(!isJoinSession){
			JoinSession();
		}
		
		if(isJoinSession){
			ProxyBusObject mProxy = mBus.getProxyBusObject(busName, LIFX_SERVICE_PATH, sessionId.value, ifaces);
			LifxServiceInterface controlProxy = mProxy.getInterface(LifxServiceInterface.class);
			
			try {
				
				Long max_value = Long.decode("0xffffffff");
				
				int hue_value = (int) ((max_value / 360) * degree);
				controlProxy.setHue(hue_value);
				
				LOG.log(Level.INFO, "Ligth <" + DeviceId + "> change Hue to " + degree + "!");
				
			} catch (BusException e) {
				
				LOG.log(Level.WARNING,e.getMessage());
			}
		}
	}
	
	public void SetColorTemp(int degree){
		if(!isJoinSession){
			JoinSession();
		}
		
		if(isJoinSession){
			ProxyBusObject mProxy = mBus.getProxyBusObject(busName, LIFX_SERVICE_PATH, sessionId.value, ifaces);
			LifxServiceInterface controlProxy = mProxy.getInterface(LifxServiceInterface.class);
			
			try {
				if(degree >= 100) degree = 100;
				Long max_value = Long.decode("0xffffffff");
				
				int color_value = (int) ((max_value / 360) * degree);				
				controlProxy.setColorTemp(color_value);
				
				LOG.log(Level.INFO, "Ligth <" + DeviceId + "> change ColorTemp to " + degree + "!");
				
			} catch (BusException e) {
				
				LOG.log(Level.WARNING,e.getMessage());
			}
		}
	}
	
	public void SetBrightness(int degree){
		if(!isJoinSession){
			JoinSession();
		}
		
		if(isJoinSession){
			ProxyBusObject mProxy = mBus.getProxyBusObject(busName, LIFX_SERVICE_PATH, sessionId.value, ifaces);
			LifxServiceInterface controlProxy = mProxy.getInterface(LifxServiceInterface.class);
			
			try {
				Long max_value = Long.decode("0xffffffff");
				if(degree == 0) degree = 1;
//				int color_value = (int) ((max_value / 360) * degree);
				int color_value = (int)(max_value * degree / 100);
				controlProxy.setBrightness(color_value);
				
				LOG.log(Level.INFO, "Ligth <" + DeviceId + "> change Brightness to " + degree + "!");
				
			} catch (BusException e) {
				
				LOG.log(Level.WARNING,e.getMessage());
			}
		}
	}
	
	@Override
	public void run() {
		while(isActived){
			
			if(!isJoinSession){
				JoinSession();
			}
			
			try{
			
				if(isJoinSession){
					ProxyBusObject statusProxyObj = mBus.getProxyBusObject(busName, LIFX_SERVICE_PATH, sessionId.value, ifaces);

					LifxServiceInterface statusProxy = statusProxyObj.getInterface(LifxServiceInterface.class);
	
		            powerStatus = statusProxy.getOnOff() ? "1" : "0";
		            int colorTemp = statusProxy.getColorTemp();
		            int hue = statusProxy.getHue();
		            int temp = 0;
		            
		            Long max_value = Long.decode("0xffffffff");
		            
		            temp = (int)(((double)colorTemp / max_value) * 360);
		            
		            if(temp >= 0 && temp <= 180){
		            	colorTempDegree = temp + 1;
		            } else if(temp < 0){
		            	colorTempDegree = 360 + temp;
		            }
		            
		            temp = (int)(((double)hue / max_value) * 360);
		            
		            if(temp >= 0 && temp <= 180){
		            	hueDegree = temp + 1;
		            } else if(temp < 0){
		            	hueDegree = 360 + temp;
		            }
		                   
		            if(ajDataRecv != null){
		            	StringBuilder sb = new StringBuilder();
		            	sb.append(powerStatus).append(",")
		            	.append(hueDegree).append(",")
		            	.append(colorTempDegree);
		            	
		            	if(!sb.toString().equals(beforeStatus))
		            	{
		            		ajDataRecv.SensingDataReceived(this, sb.toString());
		            		
		            		beforeStatus = sb.toString();
		            	}
		            }
				}  
			}catch (BusException e) {
				LeaveSession();
				
				LOG.log(Level.WARNING, e.getMessage());
			}
			
			try{
				
				Thread.sleep(tickTime);
			}catch(InterruptedException e){
				LOG.log(Level.WARNING, e.getMessage());
			}
		}
	}
}
