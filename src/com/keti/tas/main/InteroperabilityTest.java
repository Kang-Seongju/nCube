package com.keti.tas.main;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

import kr.re.keti.ncube.Registration;

public class InteroperabilityTest extends Thread{
	private Socket sock;
	private BufferedReader br;
	
	InteroperabilityTest(Socket sock){
		this.sock = sock;
		try{
			br = new BufferedReader(new InputStreamReader(sock.getInputStream()));
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {		
		try{
			String line = null;
			if((line = br.readLine()) != null){
				if(line.equals("ae-test")){
					System.out.println("Interoperability Test: AE CREATE & DELETE");
					MainProcesser.publishClient.publishFullPayload("/oneM2M/test_req", "ae-test");
				}
				else{
					System.out.println("Interoperability Test: CNT CREATE & DELETE");
					MainProcesser.publishClient.publishFullPayload("/oneM2M/test_req", "cnt-test");
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
