package com.keti.tas.arg;

import java.util.HashMap;

public class ArgPicker {
	
	private HashMap<String, String> values = new HashMap<>();
	
	public ArgPicker(String args[]) {
		if(args.length > 0){
			for(int i = 0; i < args.length; i++){
				if(i < args.length - 1){
					if(args[i].contains("-")){
						if(!args[i+1].contains("-")){
							values.put(args[i], args[i+1].trim());
						} else {
							values.put(args[i], "");
						}
					}
				} else {
					if(args[i].contains("-")){
						values.put(args[i], "");
					}
				}
			}
		}
	}
	
	public String getValue(String opt){
		return values.get(opt);
	}
	
	public boolean isHasOption(String opt){
		return values.containsKey(opt);
	}
}
