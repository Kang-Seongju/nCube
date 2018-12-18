package kr.re.keti.ncube.mqttclient;

import kr.re.keti.ncube.Registration;

import org.eclipse.paho.client.mqttv3.MqttMessage;

import com.keti.tas.main.MainProcesser;

public class MqttClientSensorSub extends MqttClientKetiSub{
	
	public MqttClientSensorSub(String serverUrl){
		super(serverUrl);
	}
	
	@Override
	public void messageArrived(String topic, MqttMessage message) throws Exception {
		
		String payload = byteArrayToString(message.getPayload());
		Translation.containerParse(payload);
		MainProcesser.publishClient.publishFullPayload("/oneM2M/req/" + Registration.ae.aeId + "/Mobius/xml", payload);
	}
	
}
