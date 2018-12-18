package com.keti.tas.alljoyn;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.alljoyn.bus.BusAttachment;
import org.alljoyn.bus.BusException;
import org.alljoyn.bus.ProxyBusObject;
import org.alljoyn.bus.Variant;
import org.alljoyn.bus.ifaces.Introspectable;

public class DewonAlljoynPlug extends AlljoynDevice{
	
	static{
		System.loadLibrary("alljoyn_java");
	}
	
	private static final Logger LOG = Logger.getLogger(DewonAlljoynPlug.class.getName());
	
	private static final String CONTROL_ROOT_PATH = "/ControlPanel/MyDevice/rootContainer/en";
	
	private static final String POWER_STATUS_PATH = CONTROL_ROOT_PATH + "/controlsContainer/ac_mode";
	private static final String POWER_CONSUMPTION_PATH = CONTROL_ROOT_PATH + "/tempAndHumidityContainer/CurrentTempStringProperty";
	private static final String POWER_CUMULATIVE_POWER_PATH = CONTROL_ROOT_PATH + "/tempAndHumidityContainer/CurrentHumidityStringProperty";
	private static final String VOLT_PATH = CONTROL_ROOT_PATH + "/tempAndHumidityContainer/CurrentVoltStringProperty";
	private static final String CURRENT_PATH = CONTROL_ROOT_PATH + "/tempAndHumidityContainer/CurrentCurrStringProperty";
	private static final String FREQUENCY_PATH = CONTROL_ROOT_PATH + "/tempAndHumidityContainer/CurrentFreqStringProperty";
	private static final String POWER_FACTOR_PATH = CONTROL_ROOT_PATH + "/tempAndHumidityContainer/CurrentPfacStringProperty";
	
	private static final String CONTROL_PATH = CONTROL_ROOT_PATH + "/controlsContainer/ac_mode";
	
	private String powerStatus = "";
	private String beforeStatus = "3";
	private String powerConsumption = "";
	private String powerCumulative = "";
	private String volt = "";
	private String currentValue = "";
	private String frequencyValue = "";
	private String powerFactor = "";

	private Class<?> ifaces[] = { Introspectable.class, PropertyControl.class };

	public DewonAlljoynPlug(BusAttachment mBus, String busName, short port) {
		super(mBus, busName, port);
		
		target = DeviceTarget.DEWON_SMART_PLUG;
		
	}
	
	public void TurnON(){
		
		if(!isJoinSession){
			JoinSession();
		}
		
		if(isJoinSession){
			ProxyBusObject mProxy = mBus.getProxyBusObject(busName, CONTROL_PATH, sessionId.value, ifaces);
			PropertyControlSuper controlProxy = (PropertyControlSuper) mProxy.getInterface(PropertyControl.class);
			
			try {
				controlProxy.setValue(new Variant(1, "q"));
				
				LOG.log(Level.INFO, "Device <" + DeviceId + "> power on!");
				
			} catch (BusException e) {
				
				LOG.log(Level.WARNING,e.getMessage());
			}
		}
	}
	
	public void TurnOFF(){
		
		if(!isJoinSession){
			JoinSession();
		}
		
		if(isJoinSession){
			ProxyBusObject mProxy = mBus.getProxyBusObject(busName, CONTROL_PATH, sessionId.value, ifaces);
			PropertyControlSuper controlProxy = (PropertyControlSuper) mProxy.getInterface(PropertyControl.class);
			
			try {
				controlProxy.setValue(new Variant(0, "q"));
				
				LOG.log(Level.INFO, "Device <" + DeviceId + "> power off!");
				
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
					ProxyBusObject powerStatusProxyObj = mBus.getProxyBusObject(busName, POWER_STATUS_PATH, sessionId.value, ifaces);
					ProxyBusObject powerConsumptionProxyObj = mBus.getProxyBusObject(busName, POWER_CONSUMPTION_PATH, sessionId.value, ifaces);
					ProxyBusObject powerCumulativePowerProxyObj = mBus.getProxyBusObject(busName, POWER_CUMULATIVE_POWER_PATH, sessionId.value, ifaces);
					ProxyBusObject voltProxyObj = mBus.getProxyBusObject(busName, VOLT_PATH, sessionId.value, ifaces);
					ProxyBusObject currentProxyObj = mBus.getProxyBusObject(busName, CURRENT_PATH, sessionId.value, ifaces);
					ProxyBusObject frequencyProxyObj = mBus.getProxyBusObject(busName, FREQUENCY_PATH, sessionId.value, ifaces);
					ProxyBusObject powerFactorProxyObj = mBus.getProxyBusObject(busName, POWER_FACTOR_PATH, sessionId.value, ifaces);
					
					PropertyControlSuper powerStatusProxy = powerStatusProxyObj.getInterface(PropertyControl.class);
					PropertyControlSuper powerConsumptionProxy = powerConsumptionProxyObj.getInterface(PropertyControl.class);
					PropertyControlSuper powerCumulativePowerProxy = powerCumulativePowerProxyObj.getInterface(PropertyControl.class);
					PropertyControlSuper voltProxy = voltProxyObj.getInterface(PropertyControl.class);
					PropertyControlSuper currentProxy = currentProxyObj.getInterface(PropertyControl.class);
					PropertyControlSuper frequencyProxy = frequencyProxyObj.getInterface(PropertyControl.class);
					PropertyControlSuper powerFactorProxy = powerFactorProxyObj.getInterface(PropertyControl.class);
					
	
		            powerStatus = powerStatusProxy.getValue().getObject(Short.class).toString();
		            powerConsumption = powerConsumptionProxy.getValue().getObject(String.class);
		            powerCumulative = powerCumulativePowerProxy.getValue().getObject(String.class);
		            volt = voltProxy.getValue().getObject(String.class);
		            currentValue = currentProxy.getValue().getObject(String.class);
		            frequencyValue = frequencyProxy.getValue().getObject(String.class);
		            powerFactor = powerFactorProxy.getValue().getObject(String.class);
		            
		            if(ajDataRecv != null){
		            	StringBuilder sb = new StringBuilder();
		            	sb.append(powerStatus).append(",")
		            	.append(powerConsumption).append(",")
		            	.append(powerCumulative).append(",")
		            	.append(volt).append(",")
		            	.append(currentValue).append(",")
		            	.append(frequencyValue).append(",")
		            	.append(powerFactor);
		            	
		            	if(!powerStatus.equals(beforeStatus))
		            	{
		            		ajDataRecv.SensingDataReceived(this, sb.toString());
		            		
		            		beforeStatus = powerStatus;
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
