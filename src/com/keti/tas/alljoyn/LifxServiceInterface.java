/*
 * Copyright (c) 2009-2011, 2015 KETI. All rights reserved.
 *
 *
 */

package com.keti.tas.alljoyn;

import java.util.Map;

import org.alljoyn.bus.BusException;
import org.alljoyn.bus.Variant;
import org.alljoyn.bus.annotation.BusInterface;
import org.alljoyn.bus.annotation.BusMethod;
import org.alljoyn.bus.annotation.BusProperty;
import org.alljoyn.bus.annotation.BusSignal;

/***
 * 
 * @author ChenNan
 * @version 0.0.0.1
 */
@BusInterface(name = "org.allseen.LSF.LampState")
public interface LifxServiceInterface {

	public static final short VERSION = 1;
	
	@BusProperty(signature="u")
    public int getVersion() throws BusException;
	
	@BusMethod(signature="ta{sv}u", replySignature="u", name="TransitionLampState") 
	public int TransitionLampState(long Timestamp, Map<String, Variant> NewState, int TransitionPeriod) throws BusException;

	@BusMethod(signature="a{sv}a{sv}uuut", replySignature="u", name="ApplyPulseEffect")
	public int ApplyPulseEffect(Map<String, Variant> FromState, Map<String, Variant> ToState, int period,int duration, int numPulses, long timestamp) throws BusException;
	
	@BusSignal(name="LampStateChanged")
	public void LampStateChanged(String LampID) throws BusException;
	
	@BusProperty(name="OnOff", signature="b")
	public void setOnOff(boolean OnOff) throws BusException;
	
	@BusProperty(name="OnOff", signature="b")
	public boolean getOnOff() throws BusException;
	
	@BusProperty(name="Hue", signature="u")
	public void setHue(int Hue) throws BusException;
	
	@BusProperty(name="Hue", signature="u")
	public int getHue() throws BusException;
	
	@BusProperty(name="Saturation", signature="u")
	public void setSaturation(int Saturation) throws BusException;
	
	@BusProperty(name="Saturation", signature="u")
	public int getSaturation() throws BusException;
	
	@BusProperty(name="ColorTemp", signature="u")
	public void setColorTemp(int ColorTemp) throws BusException;
	
	@BusProperty(name="ColorTemp", signature="u")
	public int getColorTemp() throws BusException;
	
	@BusProperty(name="Brightness", signature="u")
	public void setBrightness(int Brightness) throws BusException;
	
	@BusProperty(name="Brightness", signature="u")
	public int getBrightness() throws BusException;
}
