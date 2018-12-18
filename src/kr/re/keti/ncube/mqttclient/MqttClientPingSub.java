package kr.re.keti.ncube.mqttclient;

import kr.re.keti.ncube.Registration;

import org.eclipse.paho.client.mqttv3.MqttMessage;

import com.keti.tas.main.MainProcesser;

public class MqttClientPingSub extends MqttClientKetiSub{
	
	public MqttClientPingSub(String serverUrl){
		super(serverUrl);
	}
	
	@Override
	public void messageArrived(String topic, MqttMessage message) throws Exception {
		
		String payload = byteArrayToString(message.getPayload());
//		System.out.println("Ping Check");
		MainProcesser.publishClient.publishFullPayload("/oneM2M/ping_resp/" + Registration.ae.aeId + "/Mobius/xml", "OK");
	}
	
}
