package com.keti.tas.main;


import com.keti.tas.alljoyn.AlljoynDevice;
import com.keti.tas.alljoyn.LifxAlljoynLight;
import com.keti.tas.alljoyn.PowertechAlljoynPlug;
import com.keti.tas.main.Devices;
import com.keti.tas.main.MainProcesser;


public class InterworkingInterface { 
		
		public static boolean control(String target, String function, String value){
			
			String targetModel = Devices.findDeviceModel(target);
			if(!(targetModel.equals("Fail"))){
				switch(targetModel){
				// Alljoyn bulb
				case "LIFX Color 1000":
					if(function.equals("Brightness")){ // Bulb Set Brightness
						for(LifxAlljoynLight dev : MainProcesser.alljoyn.light)
							if(dev.GetDeviceName().equals(target))
								dev.SetBrightness(Integer.parseInt(value));
					}
					else if(function.equals("Power")){
						if(value.equals("ON") || value.equals("on")){ // Bulb ON
							for(LifxAlljoynLight dev : MainProcesser.alljoyn.light)
								if(dev.GetDeviceName().equals(target))
									dev.TurnON();
						}else if(value.equals("OFF") || value.equals("0") || value.equals("off") ){ // Bulb OFF
							for(LifxAlljoynLight dev : MainProcesser.alljoyn.light)
								if(dev.GetDeviceName().equals(target))
									dev.TurnOFF();
						}
					}else if(function.equals("Color")){ // Bulb Set Color
						for(LifxAlljoynLight dev : MainProcesser.alljoyn.light)
							if(dev.GetDeviceName().equals(target))
//								dev.SetColorTemp("red");
								dev.SetColorTemp(Integer.parseInt(value));
					}
					return true;
				case "LIFX OEM 750 HV":
					if(function.equals("Brightness")){ // Bulb Set Brightness
						if(value.equals("ON") || value.equals("on")){ // Bulb ON
							for(LifxAlljoynLight dev : MainProcesser.alljoyn.light)
								if(dev.GetDeviceName().equals(target))
									dev.TurnON();
						}else if(value.equals("OFF") || value.equals("off")){ // Bulb OFF
							for(LifxAlljoynLight dev : MainProcesser.alljoyn.light)
								if(dev.GetDeviceName().equals(target))
									dev.TurnOFF();
						}else{
							for(LifxAlljoynLight dev : MainProcesser.alljoyn.light)
								if(dev.GetDeviceName().equals(target))
									dev.SetBrightness(Integer.parseInt(value));
						}
					}else if(function.equals("Color")){ // Bulb Set Color
						for(LifxAlljoynLight dev : MainProcesser.alljoyn.light)
							if(dev.GetDeviceName().equals(target))
								dev.SetColorTemp(Integer.parseInt(value));
					}
					return true;
				// Alljoyn plug
				case "Smart Plug":
					if(function.equals("Power")){
						if(value.equals("ON") || value.equals("on")){
							for(AlljoynDevice dev: MainProcesser.alljoyn.alldev) // Plug ON
								if(dev.GetDeviceName().equals(target))
									((PowertechAlljoynPlug)dev).TurnON();
						}else if(value.equals("OFF") || value.equals("off")){
							for(AlljoynDevice dev: MainProcesser.alljoyn.alldev) // Plug ON
								if(dev.GetDeviceName().equals(target))
										((PowertechAlljoynPlug)dev).TurnOFF();
						}
						return true;
					}
					else
						return false;
					
				// Philis hue lamp
				case "LCT007":
					if(function.equals("Brightness")){ // Bulb Set Brightness
						MainProcesser.controller.SetBrightness(target, Integer.parseInt(value));
					}
					else if(function.equals("Power")){
						if(value.equals("ON") || value.equals("on"))
							MainProcesser.controller.SetOn(target);
						else if(value.equals("OFF") || value.equals("off") || value.equals("0"))
							MainProcesser.controller.SetOff(target);
					}
					else if(function.equals("Color")){
						MainProcesser.controller.TurnLights(target, Integer.parseInt(value));
					}
					return true;
				}
			}
//			else
//				System.out.println("[DeviceController]: Target model is not found");
			return false;
		}
}
