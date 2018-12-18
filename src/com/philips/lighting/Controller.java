package com.philips.lighting;

import java.util.List;
import java.util.Random;

import javax.swing.JDialog;

import com.philips.lighting.data.HueProperties;
import com.philips.lighting.gui.AccessPointList;
import com.philips.lighting.gui.LightColoursFrame;
import com.philips.lighting.gui.PushLinkFrame;
import com.philips.lighting.hue.sdk.PHAccessPoint;
import com.philips.lighting.hue.sdk.PHBridgeSearchManager;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.hue.sdk.PHMessageType;
import com.philips.lighting.hue.sdk.PHSDKListener;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHBridgeResourcesCache;
import com.philips.lighting.model.PHHueError;
import com.philips.lighting.model.PHHueParsingError;
import com.philips.lighting.model.PHLight;
import com.philips.lighting.model.PHLightState;
import com.keti.tas.main.DeviceFunction;
import com.keti.tas.main.Devices;

public class Controller {

    private PHHueSDK phHueSDK;
    
    private PushLinkFrame pushLinkDialog;
    private LightColoursFrame lightColoursFrame;
    
    private static final int MAX_HUE=65535;
    private Controller instance;

    public Controller() {
        this.phHueSDK = PHHueSDK.getInstance();
        this.instance = this;
    }

    public void findBridges() {
        phHueSDK = PHHueSDK.getInstance();
        PHBridgeSearchManager sm = (PHBridgeSearchManager) phHueSDK.getSDKService(PHHueSDK.SEARCH_BRIDGE);
        sm.search(true, true);
    }

    private PHSDKListener listener = new PHSDKListener() {

        @Override
        public void onAccessPointsFound(List<PHAccessPoint> accessPointsList) {
            //AccessPointList accessPointList = new AccessPointList(accessPointsList, instance);
            //accessPointList.setVisible(true);
            //accessPointList.setLocationRelativeTo(null);  // Centre the AccessPointList Frame
        }

        @Override
        public void onAuthenticationRequired(PHAccessPoint accessPoint) {
            // Start the Pushlink Authentication.
        	
            phHueSDK.startPushlinkAuthentication(accessPoint);
            
            pushLinkDialog = new PushLinkFrame(instance);
            pushLinkDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            pushLinkDialog.setModal(true);
            pushLinkDialog.setLocationRelativeTo(null); // Center the dialog.
            pushLinkDialog.setVisible(true);
        	
        }

        @Override
        public void onBridgeConnected(PHBridge bridge, String username) {
            phHueSDK.setSelectedBridge(bridge);
            phHueSDK.enableHeartbeat(bridge, PHHueSDK.HB_INTERVAL);
            String lastIpAddress =  bridge.getResourceCache().getBridgeConfiguration().getIpAddress();   
            HueProperties.storeUsername(username);
            HueProperties.storeLastIPAddress(lastIpAddress);
            HueProperties.saveProperties();
            // Close the PushLink dialog (if it is showing).
            if (pushLinkDialog!=null && pushLinkDialog.isShowing()) {
                pushLinkDialog.setVisible(false);
            }
        	/* check cache 17.0629 */
        	PHBridgeResourcesCache cache = bridge.getResourceCache();
        	List<PHLight> allLights = cache.getAllLights();
        	System.out.print("Philips Hue device: ");
			for (PHLight light : allLights) {
				 System.out.print("["+light.getName() + "] ");
				 Devices.setDevice(light.getName(),light.getModelNumber(), DeviceFunction.HueBulb);
			}
			System.out.println("");
			/* check cache 17.0629 */
        }

        @Override
        public void onCacheUpdated(List<Integer> arg0, PHBridge arg1) {
        }

        @Override
        public void onConnectionLost(PHAccessPoint arg0) {
        }

        @Override
        public void onConnectionResumed(PHBridge arg0) {
        }

        @Override
        public void onError(int code, final String message) {
        }

        @Override
        public void onParsingErrors(List<PHHueParsingError> parsingErrorsList) {  
            for (PHHueParsingError parsingError: parsingErrorsList) {
                System.out.println("ParsingError : " + parsingError.getMessage());
            }
        } 
    };

    public PHSDKListener getListener() {
        return listener;
    }

    public void setListener(PHSDKListener listener) {
        this.listener = listener;
    }

    /* 17.06.21 edited by gw start */
    public void SetOn(String id){
    	PHBridge bridge = phHueSDK.getSelectedBridge();
    	PHBridgeResourcesCache cache = bridge.getResourceCache();
    	List<PHLight> allLights = cache.getAllLights();
        
    	for (PHLight light : allLights) {
    		if(id.equals(light.getName())){
        		PHLightState lightState = new PHLightState();
                lightState.setOn(true);
                bridge.updateLightState(light, lightState); 
    		}
    	}
    }
    
    public void SetBrightness(String id, int value){
    	if(value > 100)
			value = 100;
		else if(value < 0)
			value = 0;
    	
    	int setVal = (int)(2.54 * value);
    	PHBridge bridge = phHueSDK.getSelectedBridge();
    	PHBridgeResourcesCache cache = bridge.getResourceCache();
    	List<PHLight> allLights = cache.getAllLights();
        
    	for (PHLight light : allLights) {
    		if(id.equals(light.getName())){
        		PHLightState lightState = new PHLightState();
                lightState.setBrightness(setVal);
                bridge.updateLightState(light, lightState); 
    		}
    	}
    }
    
    public void SetOff(String id){
    	PHBridge bridge = phHueSDK.getSelectedBridge();
    	PHBridgeResourcesCache cache = bridge.getResourceCache();
    	List<PHLight> allLights = cache.getAllLights();
    	
    	for (PHLight light : allLights) {
    		if(id.equals(light.getName())){
        		PHLightState lightState = new PHLightState();
                lightState.setOn(false);
                bridge.updateLightState(light, lightState); 
    		}
    	}    	
    }
    
    public void TurnLights(String id, int color){ 
    	if(color > 100)
    		color = 100;
		else if(color < 0)
			color = 0;
    	
    	int setVal = (int)(468 * color);
    	
    	PHBridge bridge = phHueSDK.getSelectedBridge();
    	
    	PHBridgeResourcesCache cache = bridge.getResourceCache();
    	List<PHLight> allLights = cache.getAllLights();
        
    	for (PHLight light : allLights) {
    		if(id.equals(light.getName())){
    			//System.out.println(light.getName());
        		//System.out.println(light.getIdentifier());
        		PHLightState lightState = new PHLightState();
        		lightState.setHue(setVal);
//                lightState.setHue(color);
                bridge.updateLightState(light, lightState); 
    		}
    		
    	}
    }
    
    public List<PHLight> getLightList(){
    	PHBridge bridge = phHueSDK.getSelectedBridge();
    	PHBridgeResourcesCache cache = bridge.getResourceCache();
    	List<PHLight> allLights = cache.getAllLights();
    	return allLights;
    }
    /* 17.06.21 edited by gw end */    
    
    public void randomLights() {
        PHBridge bridge = phHueSDK.getSelectedBridge();
        PHBridgeResourcesCache cache = bridge.getResourceCache();

        List<PHLight> allLights = cache.getAllLights();
        Random rand = new Random();
        
        for (PHLight light : allLights) {
        	System.out.println(light);
            PHLightState lightState = new PHLightState();
            lightState.setHue(rand.nextInt(MAX_HUE));
            bridge.updateLightState(light, lightState); // If no bridge response is required then use this simpler form.
        }
    }

    public void showControlLightsWindow() {
        if (lightColoursFrame == null) {
            lightColoursFrame = new LightColoursFrame(); 
        }
        lightColoursFrame.setLocationRelativeTo(null); // Centre window
        lightColoursFrame.setVisible(true);
    }
    
    /**
     * Connect to the last known access point.
     * This method is triggered by the Connect to Bridge button but it can equally be used to automatically connect to a bridge.
     * 
     */
    public boolean connectToLastKnownAccessPoint() {
        String username = HueProperties.getUsername();
        String lastIpAddress =  HueProperties.getLastConnectedIP();     
        
        if (username==null || lastIpAddress == null) {
            return false;
        }
        PHAccessPoint accessPoint = new PHAccessPoint();
        accessPoint.setIpAddress(lastIpAddress);
        accessPoint.setUsername(username);
        phHueSDK.connect(accessPoint);
        return true;
    }

    /* 2017.0621 edited by gw start */
    public boolean connectIP(String srcIp){
    	//String username = HueProperties.getUsername();
    	//String username = "i7pjgbNez8u2PPFtAUWIomjvMD3A37xZqIsNbyyP";
    	String username = "P51ZA53OVE4z5c98MxoJkxHN-pEhdWp8PA2B8mWJ";
    	PHAccessPoint accessPoint = new PHAccessPoint();
        accessPoint.setIpAddress(srcIp);
        accessPoint.setUsername(username);
        phHueSDK.connect(accessPoint);
    	return true;
    }
    /* 2017.0621 edited by gw end */
}
