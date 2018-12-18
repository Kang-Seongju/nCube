/*
 * ------------------------------------------------------------------------
 * Copyright 2014 Korea Electronics Technology Institute
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *     
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ------------------------------------------------------------------------
 */

package kr.re.keti.ncube;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import kr.re.keti.ncube.mqttclient.MqttClientKetiSub;
import kr.re.keti.ncube.mqttclient.MqttClientPingSub;
import kr.re.keti.ncube.mqttclient.MqttClientSensorCheck;
import kr.re.keti.ncube.mqttclient.MqttClientSensorSub;
import kr.re.keti.ncube.mqttclient.MqttClientServiceDiscovery;
import kr.re.keti.ncube.mqttclient.ResourceStructure;
import kr.re.keti.ncube.mqttclient.Translation;
import kr.re.keti.ncube.resource.AE;
import kr.re.keti.ncube.resource.CSEBase;
import kr.re.keti.ncube.resource.Container;
import kr.re.keti.ncube.resource.ResourceRepository;
import kr.re.keti.ncube.resource.Subscription;

import com.keti.tas.main.Devices;
import com.keti.tas.main.MainProcesser;
import com.keti.tas.main.SensorHealthCheck;

/**
 * Class for registration about AE and container resources
 * @author NakMyoung Sung (nmsung@keti.re.kr)
 */
public class Registration {
	
	public static CSEBase cse;
	public static AE ae;
	private ArrayList<Container> containers;
	private ArrayList<Subscription> subscriptions;
	
	public static String middlewareIp = "192.168.86.93";
	private boolean aeCreate = false;
	
	public Registration() {
		this.cse = ResourceRepository.getCSEInfo();
		this.ae = ResourceRepository.getAEInfo();
		//this.containers = ResourceRepository.getContainersInfo();
		//this.subscriptions = ResourceRepository.getSubscriptionInfo();
	}

	/**
	 * registrationStart Method
	 * Start the registration procedure for AE and containers
	 * @throws Exception
	 */
	public void registrationStart() {
		System.out.println("[&CubeThyme] &CubeThyme registration start.......\n");
		
		try {
			BufferedReader in = new BufferedReader(new FileReader("AE_ID.back"));
			try {
				ae.aeId = in.readLine();
				System.out.println("AeID: [" + ae.aeId + "]");
				in.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("[&CubeThyme] AE_ID value not found");
				try {
					in.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					System.out.println("[&CubeThyme] AE_ID file close failed");
				}
			}
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			System.out.println("[&CubeThyme] Backup AE_ID file not found");
		}
		
		ResourceStructure.InitResourceStructure();
		
		try {
	    	 Thread.sleep(1000); // Wait a moment
		 }catch (Exception e) {
			 e.printStackTrace();
		 }
		
		int conRegCount = 0;
		
		Translation.makeResourceOrientedArchitecture();
		
		MainProcesser.requestClient = new MqttClientKetiSub("tcp://" + cse.CSEHostAddress, ae.aeId);
		MainProcesser.responseClient = new MqttClientKetiSub("tcp://" + cse.CSEHostAddress);
		MainProcesser.tasClient = new MqttClientSensorSub("tcp://" + cse.CSEHostAddress);
		MainProcesser.pingClient = new MqttClientPingSub("tcp://" + cse.CSEHostAddress);
		MainProcesser.tasCheckClient = new MqttClientSensorCheck("tcp://" + cse.CSEHostAddress);
		MainProcesser.discoveryClient = new MqttClientServiceDiscovery("tcp://" + middlewareIp);
		
		ResourceStructure.resetResource();
		//int flag2 = 1;
		//ResourceStructure.createContainerRequest("cntTest", "");
		//System.out.println("cntTest make END");
//		while(flag2 == 1){
//			
//		};
		try {
			System.out.println("Device List size : "+Devices.mDeviceList.size());
			for (int i = 0; i < Devices.mDeviceList.size(); i++) {
				
				if (!Devices.mDeviceList.get(i).registration) {
					System.out.println(Devices.mDeviceList.get(i).deviceName);
					ResourceStructure.createContainerRequest(Devices.mDeviceList.get(i).cntName, "");
					for(int j =0; j < Devices.mDeviceList.get(i).deviceFunction.length; j++){
						System.out.println("CNT>>>>: " + "/"+Devices.mDeviceList.get(i).cntName);
						System.out.println("SERVICE>>>>: " + "/"+Devices.mDeviceList.get(i).deviceFunction[j]);
						Thread.sleep(300);
						// Hierarchy function structure register
						ResourceStructure.createContainerRequest(Devices.mDeviceList.get(i).deviceFunction[j], "/"+Devices.mDeviceList.get(i).cntName);
					}
					Devices.mDeviceList.get(i).registration = true;
					conRegCount++;
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
//	
		//MainProcesser.requestClient.subscribe("/oneM2M/resp/" + ae.aeId + "/Mobius/xml"); // response for request message;
//		MainProcesser.responseClient.subscribe("/oneM2M/resp/" + ae.aeId + "/Mobius2/xml/forward"); // forwarding topic(app->mobius->&cube)
//		MainProcesser.tasClient.subscribe("/oneM2M/sen_req/" + ae.aeId + "/Mobius2/xml"); // forwarding topic(tas->&cube->mobius)
//		MainProcesser.pingClient.subscribe("/oneM2M/ping/" + ae.aeId + "/Mobius2/xml"); // forwarding topic(tas->&cube->mobius)
		MainProcesser.responseClient.subscribe("/oneM2M/resp/" + ae.aeId + "/Mobius/xml/forward"); // forwarding topic(app->mobius->&cube)
		MainProcesser.tasClient.subscribe("/oneM2M/sen_req/" + ae.aeId + "/Mobius/xml"); // forwarding topic(tas->&cube->mobius)
		MainProcesser.pingClient.subscribe("/oneM2M/ping/" + ae.aeId + "/Mobius/xml"); // forwarding topic(tas->&cube->mobius)
		MainProcesser.tasCheckClient.subscribe("sensor/resp"); // sensor health check channel
		MainProcesser.discoveryClient.subscribe("/serviceDiscovery/req/#");
		
		Devices.printDevice();
		
		ResourceRepository.setAEInfo(ae);
		
		System.out.println("[&CubeThyme] &CubeThyme registration complete.......\n");
		SensorHealthCheck senMonitor = new SensorHealthCheck();
		senMonitor.start();
		
	}
}