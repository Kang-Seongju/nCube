package com.keti.tas.alljoyn;

public enum DeviceTarget {
	
	DEWON_SMART_PLUG("DewonSmartPlug"), 
	POWERTECH_SMART_PLUG("PowertechSmartPlug"), 
	LIFX_WHITE_800("LifxWhite800"), 
	LIFX_COLOR_1000("LifxColor1000"),
	MUSAIC_MP10("MusaicMP10"); 
	
	private String target = new String();
	
	private DeviceTarget(String target) {
		
		this.target = target;
	}
	
	public String GetValue(){
		return this.target;
	}
}
