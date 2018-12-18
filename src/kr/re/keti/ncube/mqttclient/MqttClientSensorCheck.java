package kr.re.keti.ncube.mqttclient;

import kr.re.keti.ncube.Registration;

import org.eclipse.paho.client.mqttv3.MqttMessage;

import com.keti.tas.main.MainProcesser;
import com.keti.tas.main.Sensors;

public class MqttClientSensorCheck extends MqttClientKetiSub{
	
	public MqttClientSensorCheck(String serverUrl){
		super(serverUrl);
	}
	
	@Override
	public void messageArrived(String topic, MqttMessage message) throws Exception {
		
		String payload = byteArrayToString(message.getPayload());
//		System.out.println(payload);
		String[] command = new String(payload).split("/");
		switch(command[0]){
			case "register":
				Sensors.addSensor(command[1]);
				break;
				
			case "healthCheck":
				for(int i =0;i<Sensors.mSensorList.size();i++){
					if(Sensors.mSensorList.get(i).name.equals(command[1]))
						Sensors.mSensorList.get(i).isAlive = "OK";
				}
				break;
		}
	}
	
}
