package com.keti.tas.main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceListener;
import javax.jmdns.ServiceTypeListener;

import kr.re.keti.ncube.Registration;
import kr.re.keti.ncube.mqttclient.MqttClientKetiPub;
import kr.re.keti.ncube.mqttclient.MqttClientKetiSub;
import kr.re.keti.ncube.mqttclient.MqttClientPingSub;
import kr.re.keti.ncube.mqttclient.MqttClientSensorCheck;
import kr.re.keti.ncube.mqttclient.MqttClientSensorSub;
import kr.re.keti.ncube.mqttclient.MqttClientServiceDiscovery;
import kr.re.keti.ncube.mqttclient.ResourceStructure;
import kr.re.keti.ncube.resource.AE;
import kr.re.keti.ncube.resource.CSEBase;
import kr.re.keti.ncube.resource.ResourceRepository;

import org.json.JSONObject;

import com.keti.tas.alljoyn.Alljoyn;
import com.keti.tas.alljoyn.AlljoynDevice;
import com.keti.tas.alljoyn.AlljoynDeviceAnounceHandler;
import com.keti.tas.alljoyn.AlljoynDeviceSensingHandler;
import com.keti.tas.alljoyn.AlljoynProcesser;
import com.keti.tas.alljoyn.JoinSessionFailedHandler;
import com.philips.lighting.Controller;
import com.philips.lighting.data.HueProperties;
import com.philips.lighting.hue.sdk.PHHueSDK;

public class MainProcesser{

	public static MqttClientKetiSub requestClient;
	public static MqttClientKetiSub responseClient;
	public static MqttClientKetiPub publishClient;
	public static MqttClientSensorSub tasClient;
	public static MqttClientSensorCheck tasCheckClient;
	public static MqttClientPingSub pingClient;
	public static MqttClientServiceDiscovery discoveryClient;
	public static MqttClientKetiPub discoveryPubClient;

	public static AlljoynProcesser alljoyn;
	public static ResourceDirectory rd = new ResourceDirectory(); // Resource directory for MQTT service discovery
	
	public static HashMap<String, AlljoynDevice> devices = new HashMap<>();
	private static ArrayList<AlljoynDevice> list = new ArrayList<>();
	
	
    static String bridge = "192.168.86.61"; // Philips Hue bridge
    
    static MainProcesser m;
    static JmDNS jmdns;
    
    /* Philipse Hue variable */
    public static Controller controller;
    /* Philipse Hue variable end */

    private static CSEBase hostingCSE = new CSEBase();
	public static AE hostingAE = new AE();
	private static InetAddress ip;
	public static String gatewayIP;
	
    Devices dev = new Devices();
    
    
	private void configurationFileLoader() throws Exception {
		
		System.out.println("[&CubeThyme] Configuration file loading...");
		
		String jsonString = "";
		
		BufferedReader br = new BufferedReader(new FileReader("./conf.json"));
		while(true) {
			String line = br.readLine();
			if (line == null) break;
			jsonString += line;
		}
		br.close();
				
		JSONObject conf = new JSONObject(jsonString);
		
		JSONObject cseObj = conf.getJSONObject("cse");
		hostingCSE.CSEHostAddress = cseObj.getString("cbhost");
		System.out.println("[&CubeThyme] CSE - cbhost : " + hostingCSE.CSEHostAddress);
		hostingCSE.CSEPort = cseObj.getString("cbport");
		System.out.println("[&CubeThyme] CSE - cbport : " + hostingCSE.CSEPort);
		hostingCSE.CSEName = cseObj.getString("cbname");
		System.out.println("[&CubeThyme] CSE - cbname : " + hostingCSE.CSEName);
		hostingCSE.CSEId = cseObj.getString("cbcseid");
		System.out.println("[&CubeThyme] CSE - cbcseid : " + hostingCSE.CSEId);
		hostingCSE.mqttPort = cseObj.getString("mqttport");
		System.out.println("[&CubeThyme] CSE - mqttPort : " + hostingCSE.mqttPort);
		ResourceRepository.setCSEBaseInfo(hostingCSE);

		JSONObject aeObj = conf.getJSONObject("ae");
		hostingAE.aeId = aeObj.getString("aeid");
		System.out.println("[&CubeThyme] AE - aeId : " + hostingAE.aeId);
		hostingAE.appId = aeObj.getString("appid");
		System.out.println("[&CubeThyme] AE - appid : " + hostingAE.appId);
		hostingAE.appName = aeObj.getString("appname");
		System.out.println("[&CubeThyme] AE - appname : " + hostingAE.appName);
		hostingAE.appPort = aeObj.getString("appport");
		System.out.println("[&CubeThyme] AE - appport : " + hostingAE.appPort);
		hostingAE.bodyType = aeObj.getString("bodytype");
		System.out.println("[&CubeThyme] AE - bodytype : " + hostingAE.bodyType);
		hostingAE.tasPort = aeObj.getString("tasport");
		System.out.println("[&CubeThyme] AE - tasport : " + hostingAE.tasPort);
		ResourceRepository.setAEInfo(hostingAE);

	}    
    
	private void init(String mobiusIP){
    	try{
    		FileWriter fw = new FileWriter("./conf.json");
    		String initSet = 
    			"{\n" +
    			    "\t\"useprotocol\": \"http\",\n" +
    			    "\t\"cse\": {\n" + 
    			        "\t\t\"cbhost\": \"" + mobiusIP + "\",\n" +
    			        "\t\t\"cbport\": \"7579\",\n" +
    			        "\t\t\"cbname\": \"Mobius\",\n" +
    			        "\t\t\"cbcseid\": \"/Mobius\",\n" +
    			        "\t\t\"mqttport\": \"1883\"\n" +
    			    "\t},\n" + 
    			    "\t\"ae\": {\n" + 
    			        "\t\t\"aeid\": \"S\",\n" + 
    			        "\t\t\"appid\": \"0.2.481.1.1\",\n" + 
    			        "\t\t\"appname\": \"kwu-hub\",\n" + 
    			        "\t\t\"appport\": \"9727\",\n" + 
    			        "\t\t\"bodytype\": \"xml\",\n" + 
    			        "\t\t\"tasport\": \"3105\"\n" + 
    			    "\t},\n" +
    			    "\t\"cnt\": [\n" +
    			    "\t],\n" +
    			    "\t\"sub\": [\n" +
    			    "\t]\n" +
    			"}\n";
    		fw.write(initSet);
    		fw.close();
    	}catch(Exception e){
    		
    	}
    	
    }
    
	static class SampleTypeListener implements ServiceTypeListener {
    	@Override
    	public void serviceTypeAdded(ServiceEvent event){
    		//System.out.println("Service type added: " + event.getType());
//    		jmdns.addServiceListener("_gw._tcp.local.", new SampleListener());
    		if(event.getType().contains("_onem2m_")){
    			System.out.println("addServiceListener: " + event.getType());
    			jmdns.addServiceListener(event.getType(), new SampleListener());
    		}
    	}
    	
    	@Override
    	public void subTypeForServiceTypeAdded(ServiceEvent event){
    		System.out.println("SubType for service type added: " + event.getType());
    	}
    }
    
	static class SampleListener implements ServiceListener {
        @Override
        public void serviceAdded(ServiceEvent event) {
        	System.out.println("[ServiceAdded: " + event.getName() + "]");
        	System.out.println("===================================================");
        	try{
//        		jmdns.requestServiceInfo(event.getType(), event.getName());
        		event.getDNS().requestServiceInfo(event.getType(), event.getName(), true);
        		System.out.println("requestServiceInfo()");
        	}catch(Exception e){
        		e.printStackTrace();
        	}
        }

        @Override
        public void serviceRemoved(ServiceEvent event) {
            System.out.println("[ServiceRemoved: " + event.getName() + "/" + event.getType() + "]");
            System.out.println("===================================================");
            String[] tmp = event.getType().split("._tcp.local.");
    		String[] services = tmp[0].split("_onem2m_");
    		String service = "onem2m_" + services[1];
            if(rd.isContain(event.getName(), service)){
            	rd.deleteService(event.getName(), service);
            	ResourceStructure.deleteContainerRequest(event.getName());
            }
        }

        @Override
        public void serviceResolved(ServiceEvent event) {
        	System.out.println("[ServiceResolved]");
        	System.out.println("INFO: " + event.toString());
        	
        	if(event.getType().contains("_onem2m_")){
	    		System.out.println("NAME: " + event.getName() + ", IP: " + event.getInfo().getInetAddresses()[0].getHostAddress() + ", SERVICE: " + event.getType());
	    		String[] tmp = event.getType().split("._tcp.local.");
	    		String[] services = tmp[0].split("_onem2m_");
	    		String service = "onem2m_" + services[1];
	    		System.out.println("Service: " + service);	    		
	    		if(!rd.isContain(event.getName(), service)){
	    			System.out.println("insertService");
	    			rd.insertService(event.getName(), service, event.getInfo().getInetAddresses()[0].getHostAddress());
	    			rd.print();
	    	        try{
	    	        	ResourceStructure.createContainerRequest(event.getName(), "");
	    	        	Thread.sleep(300);
	    	        	ResourceStructure.createContainerRequest(service, "/"+event.getName());
	    	        	System.out.println("createContainRequest");
	    	        }catch(Exception e){
	    	        	e.printStackTrace();
	    	        }
	    		}
        	}
    		
//        	if(!rd.isContain(event.getName())){
////        		Devices.setDevice(event.getName(), event.getInfo().getInetAddresses()[0].getHostAddress(), sName[0]);
//        		Devices.setDevice(event.getName(), event.getInfo().getInetAddresses()[0].getHostAddress(), event.getType());
////        		MqttClientGatewayDevSub devClient = new MqttClientGatewayDevSub("tcp://" + gatewayIP);
////            	devClient.subscribe("devResp/" + event.getInfo().getInetAddresses()[0].getHostAddress()); // Device -> Gateway communication channel
//            	
//    	        try{
//    	        	ResourceStructure.createContainerRequest(event.getName(), "");
//    	        	Thread.sleep(300);
////    	        	ResourceStructure.createContainerRequest(sName[0], "/"+event.getName());
//    	        	ResourceStructure.createContainerRequest(event.getType(), "/"+event.getName());
//    	        	System.out.println("createContainRequest");
//    	        }catch(Exception e){
//    	        	e.printStackTrace();
//    	        }
//            	
//        	}
//        	else{ // Device contain event.getName()
////        		if(!Devices.isContainService(Devices.getDevice(event.getName()), sName[0])){
//        		if(!Devices.isContainService(Devices.getDevice(event.getName()), event.getType())){
//        			try{
////        	        	ResourceStructure.createContainerRequest(sName[0], "/"+event.getName());
//        				ResourceStructure.createContainerRequest(event.getType(), "/"+event.getName());
//        				Devices.addService(event.getName(), event.getType());
//        	        }catch(Exception e){
//        	        	e.printStackTrace();
//        	        }
//        		}
//        	}
//        	System.out.println("HOST: " + event.getInfo().getInetAddresses()[0].getHostAddress());
//        	String[] functionList = new String(event.getInfo().getTextBytes()).substring(1).split("/");
//        	
//        	for(int i=0;i<functionList.length;i++){
//        		System.out.println("Function: " + functionList[i]);	
//        	}
//        	System.out.println("===================================================");
//
//
//        	ResourceStructure.createContainerRequest(event.getName(), "");
//        	
//        	for (int i = 0; i < functionList.length; i++) {	
//        		try{
//	        		Thread.sleep(300);
//	        		ResourceStructure.createContainerRequest(functionList[i], "/"+event.getName());
//        		}catch(Exception e){
//        			e.printStackTrace();
//        		}
//			}
//        	
//        	MqttClientGatewayDevSub devClient = new MqttClientGatewayDevSub("tcp://" + mobiusIP);
//        	devClient.subscribe("devResp/" + event.getName()); // Device -> Gateway communication channel
        }
    }
	
	public static void main(String[] args) throws Exception {
		
		
		 m = new MainProcesser();
		//String mobiusIP = "128.134.65.118";
		 //String mobiusIP = "203.253.128.161";
		
		 Scanner scan = new Scanner(System.in);
		 System.out.print("Please enter the mobius ip: ");
		 String mobiusIP = scan.next();
		 System.out.println("mobius ip: [" + mobiusIP + "]");
		 
		 m.init(mobiusIP);
		 
		/* Philips Hue */
		 PHHueSDK phHueSDK = PHHueSDK.create();
	     HueProperties.loadProperties();  // Load in HueProperties, if first time use a properties file is created.
	     controller = new Controller();

	     phHueSDK.getNotificationManager().registerSDKListener(controller.getListener());
	     if (controller.connectIP(bridge)) {
	    	 System.out.println("Bridge connect success");
	    	 // Philips Hue bridge successfully connected
         }
	     else
	    	 System.out.println("Bridge connect fail");
	     
	     try {
	    	 Thread.sleep(1000); // Wait for bridge setting
		 }catch (Exception e) {
			 e.printStackTrace();
		 }
		/* Philips Hue end */
	     
		Alljoyn alljoynManagement = new Alljoyn();
		alljoynManagement.start();
	     
	     
		 try {
	    	 Thread.sleep(3000); // Wait for bridge setting & Alljoyn init
		 }catch (Exception e) {
			 e.printStackTrace();
		 }
		
		
	    
	    System.out.println("[&CubeThyme] &CubeThyme SW start.......\n");
		
		// Load the &CubeThyme configuration file
		m.configurationFileLoader();

		// Initialize the HTTP server for receiving the notification messages
		System.out.println("[&CubeThyme] &CubeThyme initialize.......\n");
		
		// Registration sequence
		Registration regi = new Registration();
		regi.registrationStart();
		
		
		System.out.println("[MAIN END]");
		
		 try {
	    	 Thread.sleep(3000); // Wait a moment
		 }catch (Exception e) {
			 e.printStackTrace();
		 }
		
		jmdns = JmDNS.create(InetAddress.getLocalHost());				
		jmdns.addServiceTypeListener(new SampleTypeListener());
			
		DeviceMonitoring devMonitor = new DeviceMonitoring();
		devMonitor.start();
		
		ServerSocket server = new ServerSocket(9192);
		
		while(true){
			try{				
				Socket sock = server.accept();
				InteroperabilityTest intp = new InteroperabilityTest(sock);
				intp.start();	
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	
	}

	
	static class AlljoynAnounceRevceiver implements AlljoynDeviceAnounceHandler, JoinSessionFailedHandler{
		
		private final static Logger LOG = Logger.getLogger(AlljoynAnounceRevceiver.class.getName());
		
		@Override
		public void AlljoynDeviceConnected(AlljoynDevice device) {
			// TODO Auto-generated method stub
		}

		@Override
		public void joinSessionFailed() {
			// TODO Auto-generated method stub
			System.out.println("Main-joinSessionFaild() call");
			for(String key : devices.keySet()){
				//devices.get(key).setOnJoinSessionFailedHandler(null);
				devices.get(key).MonitoringStop();
			}
			
			//AlljoynProcesser.alljoynDevices.clear();
			
			devices.clear();
			
			alljoyn.StopProcesser();
			alljoyn = new AlljoynProcesser();
			alljoyn.setDeviceSensingListener(new AlljoynSensingReceiver());
			alljoyn.start();
		}
	}
	
	public static class AlljoynSensingReceiver implements AlljoynDeviceSensingHandler{

		private final static Logger LOG = Logger.getLogger(AlljoynSensingReceiver.class.getName());
		
		@Override
		public void SensingDataReceived(AlljoynDevice device, String msg) {
			if(msg != null && msg.length() > 0)
				LOG.log(Level.INFO, "Recevie a sensing data [" + msg + "]");
		}
	}
}
