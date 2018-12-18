package com.keti.tas.alljoyn;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.alljoyn.bus.BusAttachment;
import org.alljoyn.bus.BusException;
import org.alljoyn.bus.ProxyBusObject;
import org.alljoyn.bus.ifaces.Introspectable;

public class PowertechAlljoynPlug extends AlljoynDevice {

	static{
		System.loadLibrary("alljoyn_java");
	}
	
	private static final Logger LOG = Logger.getLogger(PowertechAlljoynPlug.class.getName());
	
	private final static String ROOT_PATH = "/ControlPanel/SmartPlug/rootContainer/en";
	private final static String POWER_STATUS_PATH = ROOT_PATH + "/State";
	private final static String VOLT_PATH = ROOT_PATH + "/MeasureContainer/VoltageProperty";
	private final static String CURRENT_PATH = ROOT_PATH + "/MeasureContainer/CurrentProperty";
	private final static String FREQUENCY_PATH = ROOT_PATH + "/MeasureContainer/FrequencyProperty";
	private final static String POWER_PATH = ROOT_PATH + "/MeasureContainer/PowerProperty";
	private final static String ACCUMULATE_ENERGY_PATH = ROOT_PATH + "/MeasureContainer/AccumulateEnergyProperty";
	private final static String POWER_FACTOR_PATH = ROOT_PATH + "/MeasureContainer/PowerFactorProperty";
	
	private final static String TURN_ON_PATH = ROOT_PATH + "/ControlsContainer/On";
	private final static String TURN_OFF_PATH = ROOT_PATH + "/ControlsContainer/Off";
	
	private String powerStatus = "";
	private String voltValue = "";
	private String currentValue = "";
	private String freValue = "";
	private String wattValue = "";
	private String accuValue = "";
	private String pfValue =  "";
	private String beforeStatus = "3";
	
	private Class<?> ifaces[] = { Introspectable.class, ActionControl.class, PropertyControl.class };
	
	public PowertechAlljoynPlug(BusAttachment mBus, String busName, short port) {
		super(mBus, busName, port);

		target = DeviceTarget.POWERTECH_SMART_PLUG;
	}
	
	public void TurnON(){
		
		if(!isJoinSession){
			JoinSession();
		}
		
		if(isJoinSession){
			ProxyBusObject turnOnProxyObj = mBus.getProxyBusObject(busName, TURN_ON_PATH, sessionId.value, ifaces);
			ActionControlSuper turnOnProxy = turnOnProxyObj.getInterface(ActionControl.class);
			
			try{
				turnOnProxy.Exec();
				
				LOG.log(Level.INFO, "Device <" + DeviceId + "> power on!");
				
			}catch(BusException e){
				LOG.log(Level.WARNING,e.getMessage());
			}
			
//			LeaveSession();
		}
	}
	
	public void TurnOFF(){
		
		if(!isJoinSession){
			JoinSession();
		}
		
		if(isJoinSession){
			ProxyBusObject turnOffProxyObj = mBus.getProxyBusObject(busName, TURN_OFF_PATH, sessionId.value, ifaces);
			ActionControlSuper turnOffProxy = turnOffProxyObj.getInterface(ActionControl.class);
			
			try{
				turnOffProxy.Exec();
				
				LOG.log(Level.INFO, "Device <" + DeviceId + "> power off!");
				
			}catch(BusException e){
				LOG.log(Level.WARNING,e.getMessage());
			}
			
//			LeaveSession();
		}
	}
	
	public String GetProperties(){
		String protert = "Volt(V): "+voltValue + " /Curr(A): " +currentValue + " /Freq(Hz): " + freValue + " /Watt(W): " + wattValue + " /ACCU(KWH): " + accuValue + " /PF(%): " + pfValue;
		//System.out.println(protert);
		return protert;
	}
	
	@Override
	public void run() {
		while(isActived){
			
			if(!isJoinSession){
				JoinSession();
			}
			
			try{
				
				if(isJoinSession){
				
					ProxyBusObject powerStatusProxyObj = mBus.getProxyBusObject(busName, POWER_STATUS_PATH, sessionId.value, ifaces);
					ProxyBusObject voltProxyObj = mBus.getProxyBusObject(busName, VOLT_PATH, sessionId.value, ifaces);
					ProxyBusObject currentProxyObj = mBus.getProxyBusObject(busName, CURRENT_PATH, sessionId.value, ifaces);
					ProxyBusObject frequencyProxyObj = mBus.getProxyBusObject(busName, FREQUENCY_PATH, sessionId.value, ifaces);
					ProxyBusObject powerProxyObj = mBus.getProxyBusObject(busName, POWER_PATH, sessionId.value, ifaces);
					ProxyBusObject accumulateEnergyProxyObj = mBus.getProxyBusObject(busName, ACCUMULATE_ENERGY_PATH, sessionId.value, ifaces);
					ProxyBusObject powerFactorProxyObj = mBus.getProxyBusObject(busName, POWER_FACTOR_PATH, sessionId.value, ifaces);
					
					PropertyControlSuper powerStatusProxy = powerStatusProxyObj.getInterface(PropertyControl.class);
					PropertyControlSuper voltProxy = voltProxyObj.getInterface(PropertyControl.class);
					PropertyControlSuper currentProxy = currentProxyObj.getInterface(PropertyControl.class);
					PropertyControlSuper frequencyProxy = frequencyProxyObj.getInterface(PropertyControl.class);
					PropertyControlSuper powerProxy = powerProxyObj.getInterface(PropertyControl.class);
					PropertyControlSuper accumulateEnergyProxy = accumulateEnergyProxyObj.getInterface(PropertyControl.class);
					PropertyControlSuper powerFactorProxy = powerFactorProxyObj.getInterface(PropertyControl.class);
					
					
				    String strValue = powerStatusProxy.getValue().getObject(String.class);
				    
				    if(strValue.equals("Switch On")){
				    	powerStatus = "1";
				    }else if(strValue.equals("Switch Off")){
				    	powerStatus = "0";
				    }
				    
				    voltValue = voltProxy.getValue().getObject(String.class);
				    currentValue = currentProxy.getValue().getObject(String.class);
				    freValue = frequencyProxy.getValue().getObject(String.class);
				    wattValue = powerProxy.getValue().getObject(String.class);
				    accuValue = accumulateEnergyProxy.getValue().getObject(String.class);
				    pfValue = powerFactorProxy.getValue().getObject(String.class);
				    
				    if(ajDataRecv != null){
		            	StringBuilder sb = new StringBuilder();
		            	sb.append(powerStatus).append(",")
		            	.append(voltValue).append(",")
		            	.append(currentValue).append(",")
		            	.append(freValue).append(",")
		            	.append(currentValue).append(",")
		            	.append(wattValue).append(",")
		            	.append(accuValue).append(",")
		            	.append(pfValue);
		            	
		            	if(!powerStatus.equals(beforeStatus)){
		            		
		            		ajDataRecv.SensingDataReceived(this, sb.toString());
		            		
		            		beforeStatus = powerStatus;
		            	}
		            }  
//				    LeaveSession();
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
