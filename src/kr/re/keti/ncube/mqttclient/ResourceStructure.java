package kr.re.keti.ncube.mqttclient;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import kr.re.keti.ncube.Registration;
import kr.re.keti.ncube.httpclient.HttpClientRequest;

import com.keti.tas.main.Devices;
import com.keti.tas.main.Sensors;
import com.keti.tas.main.MainProcesser;

public class ResourceStructure {
	public static ArrayList<String> Dev = new ArrayList<String>();
	public static int seq = 0 ;
	public static void createContainerRequest(String cntName, String path){
		
		System.out.println("PRINT LOG" + cntName +"//" +path);
		
		String requestBody =
				"<?xml version='1.0' encoding='UTF-8'?>\n"+
				"<m2m:rqp xmlns:m2m=\"http://www.onem2m.org/xml/protocols\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
					"<op>1</op>\n" +
					"<to>/Mobius/Mobius2/kwu-hub" + path + "</to>\n" +
					//"<to>/Mobius/kwu-hub" + path + "</to>\n" +
					"<fr>Middleware</fr>\n" +
					"<rqi>gw-1227"+seq+"</rqi>\n"+
					"<ty>3</ty>\n" + 
					"<pc>\n" +
						"<m2m:cnt rn=\"" + cntName + "\">\n" +
							"<lbl>hub</lbl>\n" +
							"<mni>5</mni>" +
						"</m2m:cnt>\n" +  
					"</pc>\n" + 
				"</m2m:rqp>";

		//MainProcesser.publishClient.publishFullPayload("/oneM2M/req/" + Registration.ae.aeId + "/Mobius2/xml", requestBody);
//		System.out.println(requestBody);
		seq++;
		MainProcesser.publishClient.publishFullPayload("/oneM2M/req/" + Registration.ae.aeId + "/Mobius/xml", requestBody);
	}
	
	
//	"{\n"+
//		"\"m2m:rqp\" : {\n"+
//	
//		"\"op\":1\n"+
//		"\"to\":/Mobius/kwu-hub\n"+
//		"\"fr\":Middleware\n"+
//		"\"ty\":3\n"+
//		"\"pc\" : {\n"+
//			"\"m2m:cnt\" : {\n"+
//				"\"rn\": \"Miracle\"\n+" +
//			"}\n"+
//		"}\n"+
//		"}\n"+
//	"}\n";
	
	public static void deleteContainerRequest(String cntName){
		String requestBody =
				"<m2m:rqp xmlns:m2m=\"http://www.onem2m.org/xml/protocols\">\n" +
					"<op>4</op>\n" +
					"<to>/Mobius/" + MainProcesser.hostingAE.appName + "/" + cntName + "</to>\n" +
					"<fr>/Mobius</fr>\n" + 
					"<ty>3</ty>\n" +
					"<pc>\n" +
					"</pc>\n" + 
				"</m2m:rqp>";
		MainProcesser.publishClient.publishFullPayload("/oneM2M/req/" + Registration.ae.aeId + "/Mobius/xml", requestBody);
	}
	
	public static void updateContentInstance(String cntName, String function, String con){
		String requestBody =
				"<m2m:rqp xmlns:m2m=\"http://www.onem2m.org/xml/protocols\">\n" +
					"<op>1</op>\n" +
					"<to>/Mobius/" + MainProcesser.hostingAE.appName + "/" + cntName + "/" + function + "</to>\n" +
					"<fr>/Mobius</fr>\n" +
					"<ty>4</ty>\n" + 
					"<pc>\n" +
						"<m2m:cin\n" +
							"xmlns:m2m=\"http://www.onem2m.org/xml/protocols\"\n" +
							"xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
							"<con>" + con + "</con>" +
						"</m2m:cin>\n" +  
					"</pc>\n" + 
				"</m2m:rqp>";
		
		MainProcesser.publishClient.publishFullPayload("/oneM2M/req/" + Registration.ae.aeId + "/Mobius/xml", requestBody);
	}
	public static void resetResource(){
		for (int i = 0; i < ResourceStructure.Dev.size(); i++) {
			if(Devices.findDeviceModel(ResourceStructure.Dev.get(i)).equals("Fail")){
				for(int j=0;j<Devices.middlewareDevice.length;j++){
					if(ResourceStructure.Dev.get(i).contains(Devices.middlewareDevice[j]))
						ResourceStructure.deleteContainerRequest(ResourceStructure.Dev.get(i));
				}
			}
			else
				Devices.getDevice(ResourceStructure.Dev.get(i)).registration = true;
			try {
		    	 Thread.sleep(100); // Wait a moment
			}catch (Exception e) {
				 e.printStackTrace();
			}
		}
	}
	public static void InitResourceStructure(){
		boolean aeCreate = false;
		
		while (!aeCreate) {
			try {
				int response = HttpClientRequest.aeCreateRequest(Registration.cse, Registration.ae);
				if (response == 201) {
					aeCreate = true;

					File file = new File("AE_ID.back");
					FileWriter fw = new FileWriter(file, false);
					
					fw.write(Registration.ae.aeId);
					fw.flush();
					
					fw.close();
				}
				else if (response == 409) {
					response = HttpClientRequest.aeRetrieveRequest(Registration.cse, Registration.ae);
					
					if (response == 200) {
						aeCreate = true;
						
						File file = new File("AE_ID.back");
						FileWriter fw = new FileWriter(file, false);
						
						fw.write(Registration.ae.aeId);
						fw.flush();
						
						fw.close();
					}
				}
				else {
					Thread.sleep(3000);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		MainProcesser.publishClient = new MqttClientKetiPub("tcp://" + Registration.cse.CSEHostAddress, Registration.ae.aeId);
		MainProcesser.discoveryPubClient = new MqttClientKetiPub("tcp://" + Registration.middlewareIp, Registration.ae.aeId);
		
		getContainerResource("http://"+Registration.cse.CSEHostAddress+":7579/Mobius?fu=1&ty=3");
	}
	
	public static void getContainerResource(String url) {
        BufferedReader bufferedReader = null;
        try {
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            con.setRequestMethod("GET");
            con.setRequestProperty("Accept", "application/xml");
            con.setRequestProperty("x-m2m-ri", "12345");
            con.setRequestProperty("x-m2m-origin", "SOrigin");

            bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String str;
            StringBuilder sb = new StringBuilder();

            while ((str = bufferedReader.readLine()) != null) {
                sb.append(str);
            }
 
            String[] list = new String(sb).split("Mobius/");
            for(int i = 1 ; i < list.length ; i ++){
                String[] cntList = new String(list[i]).split("/");
                if(cntList.length < 3 && cntList[0].equals(MainProcesser.hostingAE.appName)){
                    Dev.add(cntList[1].trim());
                    //System.out.println("Dev add: " + cntList[1].trim());
                    if(cntList[1].trim().contains("Sensor-"))
                    	Sensors.addSensor(cntList[1].trim());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
   }
	
}
