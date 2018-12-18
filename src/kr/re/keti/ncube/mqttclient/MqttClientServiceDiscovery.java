package kr.re.keti.ncube.mqttclient;

import kr.re.keti.ncube.Registration;

import org.eclipse.paho.client.mqttv3.MqttMessage;

import com.keti.tas.main.MainProcesser;
import com.keti.tas.main.ResourceDirectory;
import com.keti.tas.main.Sensors;

public class MqttClientServiceDiscovery extends MqttClientKetiSub{
	
	public MqttClientServiceDiscovery(String serverUrl){
		super(serverUrl);
	}
	
	@Override
	public void messageArrived(String topic, MqttMessage message) throws Exception {
		System.out.println("[ServiceDiscovery Handler]: Resouce Dir.");
		String payload = "";
		String[] topics = topic.split("/");
		int topic_length = topics.length;
		String respTopic = "/serviceDiscovery/" + topics[3] + "/" + topics[topic_length-1];
		
		System.out.println("topic length: " + topic_length);
		System.out.println("respTopic: " + respTopic);
		
		switch(topic_length){
		case 5:
			payload += "[";
			for(int i=0;i<ResourceDirectory.resourceDir.size();i++){
				payload += "{";
				payload += "device: " + ResourceDirectory.resourceDir.get(i).mDevice + ",\n";
				payload += "service: " + ResourceDirectory.resourceDir.get(i).mService + ",\n";
				payload += "ip: " + ResourceDirectory.resourceDir.get(i).mIp;
				payload += "},\n";
			}
			payload += "]";
			System.out.println(payload);
			MainProcesser.discoveryPubClient.publishFullPayload(respTopic, payload);
			break;
			
		case 6:
			payload += "[";
			for(int i=0;i<ResourceDirectory.resourceDir.size();i++){
				if(ResourceDirectory.resourceDir.get(i).mService.equals(topics[4])){
					payload += "{";
					payload += "device: " + ResourceDirectory.resourceDir.get(i).mDevice + ",\n";
					payload += "service: " + ResourceDirectory.resourceDir.get(i).mService + ",\n";
					payload += "ip: " + ResourceDirectory.resourceDir.get(i).mIp;
					payload += "},";
				}
			}
			payload += "]";
			MainProcesser.discoveryPubClient.publishFullPayload(respTopic, payload);
			break;
			
		case 7:
			for(int i=0;i<ResourceDirectory.resourceDir.size();i++){
				if(ResourceDirectory.resourceDir.get(i).mDevice.equals(topics[4]) && ResourceDirectory.resourceDir.get(i).mService.equals(topics[5])){
					payload += "{";
					payload += "topic: " + "/oneM2M/req/" + ResourceDirectory.resourceDir.get(i).mIp + "/Gateway/xml";
					payload += "}";
				}				
			}
			MainProcesser.discoveryPubClient.publishFullPayload(respTopic, payload);
			break;
			
		default:
			System.out.println("Discovery request error");
			return;
		}
	}
	
}
