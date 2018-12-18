package com.keti.tas.alljoyn;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.alljoyn.bus.BusAttachment;
import org.alljoyn.bus.BusException;
import org.alljoyn.bus.ProxyBusObject;
import org.alljoyn.bus.ifaces.Introspectable;

import com.keti.tas.alljoyn.MusaicPlay10Interface.PlayState;

public class MusaicMP10 extends AlljoynDevice{
	
	private static Logger LOG = Logger.getLogger(MusaicMP10.class.getName());
	
	private static final String MEDIAPLAYER_SERVICE_PATH = "/net/allplay/MediaPlayer";
	
	private String playStatus = "";
	
	private String beforeStatus = "";
	
	private Class<?> ifaces[] = { Introspectable.class, MusaicPlay10Interface.class};

	public MusaicMP10(BusAttachment mBus, String busName, short port) {
		super(mBus, busName, port);
		// TODO Auto-generated constructor stub
		
		this.target = DeviceTarget.MUSAIC_MP10;
	}

	public void Play(){
		if(!isJoinSession){
			JoinSession();
		}
		
		if(isJoinSession){
			ProxyBusObject mProxy = mBus.getProxyBusObject(busName, MEDIAPLAYER_SERVICE_PATH, sessionId.value, ifaces);
			MusaicPlay10Interface controlProxy = mProxy.getInterface(MusaicPlay10Interface.class);
			
			try {
				long position = controlProxy.getPlayState().value2;
				
				controlProxy.Play(1, position, false);
				
				LOG.log(Level.INFO, "Device <" + DeviceId + "> music play!");
				
			} catch (BusException e) {
				
				LOG.log(Level.WARNING,e.getMessage());
			}
		}
	}
	
	public void Pause(){
		if(!isJoinSession){
			JoinSession();
		}
		
		if(isJoinSession){
			ProxyBusObject mProxy = mBus.getProxyBusObject(busName, MEDIAPLAYER_SERVICE_PATH, sessionId.value, ifaces);
			MusaicPlay10Interface controlProxy = mProxy.getInterface(MusaicPlay10Interface.class);
			
			try {
				
				controlProxy.Pause();
				
				LOG.log(Level.INFO, "Device <" + DeviceId + "> music pause!");
				
			} catch (BusException e) {
				
				LOG.log(Level.WARNING,e.getMessage());
			}
		}
	}
	
	public void Stop(){
		if(!isJoinSession){
			JoinSession();
		}
		
		if(isJoinSession){
			ProxyBusObject mProxy = mBus.getProxyBusObject(busName, MEDIAPLAYER_SERVICE_PATH, sessionId.value, ifaces);
			MusaicPlay10Interface controlProxy = mProxy.getInterface(MusaicPlay10Interface.class);
			
			try {
				
				controlProxy.Stop();
				
				LOG.log(Level.INFO, "Device <" + DeviceId + "> music stop!");
				
			} catch (BusException e) {
				
				LOG.log(Level.WARNING,e.getMessage());
			}
		}
	}
	
	public void Previous(){
		if(!isJoinSession){
			JoinSession();
		}
		
		if(isJoinSession){
			ProxyBusObject mProxy = mBus.getProxyBusObject(busName, MEDIAPLAYER_SERVICE_PATH, sessionId.value, ifaces);
			MusaicPlay10Interface controlProxy = mProxy.getInterface(MusaicPlay10Interface.class);
			
			try {
				
				controlProxy.Previous();
				
				LOG.log(Level.INFO, "Device <" + DeviceId + "> music previous!");
				
			} catch (BusException e) {
				
				LOG.log(Level.WARNING,e.getMessage());
			}
		}
	}
	
	public void Next(){
		if(!isJoinSession){
			JoinSession();
		}
		
		if(isJoinSession){
			ProxyBusObject mProxy = mBus.getProxyBusObject(busName, MEDIAPLAYER_SERVICE_PATH, sessionId.value, ifaces);
			MusaicPlay10Interface controlProxy = mProxy.getInterface(MusaicPlay10Interface.class);
			
			try {
				
				controlProxy.Next();
				
				LOG.log(Level.INFO, "Device <" + DeviceId + "> music next!");
				
			} catch (BusException e) {
				
				LOG.log(Level.WARNING,e.getMessage());
			}
		}
	}
	
	public void Resume(){
		if(!isJoinSession){
			JoinSession();
		}
		
		if(isJoinSession){
			ProxyBusObject mProxy = mBus.getProxyBusObject(busName, MEDIAPLAYER_SERVICE_PATH, sessionId.value, ifaces);
			MusaicPlay10Interface controlProxy = mProxy.getInterface(MusaicPlay10Interface.class);
			
			try {
				
				controlProxy.Resume();
				
				LOG.log(Level.INFO, "Device <" + DeviceId + "> music resume!");
				
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
					ProxyBusObject statusProxyObj = mBus.getProxyBusObject(busName, MEDIAPLAYER_SERVICE_PATH, sessionId.value, ifaces);

					MusaicPlay10Interface statusProxy = statusProxyObj.getInterface(MusaicPlay10Interface.class);
	
					PlayState pst = statusProxy.getPlayState();
					
					playStatus = pst.value1;
					
//					System.out.println("Play Status: " + pst.value1);
//					System.out.println("Music Time: " + pst.value2);
//					System.out.println("Source Quality: " + pst.value3);
//					System.out.println(pst.value4);
//					System.out.println(pst.value5);
//					System.out.println(pst.value6);
//					System.out.println(pst.value7);
		            
		            if(ajDataRecv != null){
		            	
		            	if(!playStatus.equals(beforeStatus))
		            	{
		            		ajDataRecv.SensingDataReceived(this, playStatus);
		            		
		            		beforeStatus = playStatus;
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
