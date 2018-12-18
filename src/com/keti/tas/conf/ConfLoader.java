package com.keti.tas.conf;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Node;

public class ConfLoader {
	
	private final static Logger LOG = Logger.getLogger(ConfLoader.class.getName());
	
	private static String file_path = "tas_conf.xml";
	
	public static String HostName = "localhost";
	public static int ThymePort = 7622;
	
	public static ArrayList<DeviceInfo> uploads = new ArrayList<>();
	public static ArrayList<DeviceInfo> downloads = new ArrayList<>();
	
	public static void loadFile(String filePath){
		file_path = filePath;
		
		parse();
	}
	
	public static void loadFile(){
		
		parse();
	}
	
	private static String getFileContent(){
		
		String strResult = "";
		
		File file = new File(file_path);
		
		if(file.exists()){
		
			BufferedReader br = null;
			try{
				
				br = new BufferedReader(new FileReader(file));
				
				String strLine = null;
				
				
				while((strLine = br.readLine()) != null){
					if(strLine.trim().length() > 0 ){
						strResult += strLine;
					}
				}
				br.close();
								
			}catch(Exception exp){
				strResult = "";
			}
		}
		
		return strResult;
	}
	
	private static void parse(){
		try{
			String strXML = getFileContent();
			
			Document doc = DocumentHelper.parseText(strXML);
			
			HostName = doc.selectSingleNode("m2m:conf/tas/parenthostname").getText();
			ThymePort = Integer.parseInt(doc.selectSingleNode("m2m:conf/tas/parentport").getText());
			
			@SuppressWarnings("unchecked")
			List<Node> uploadNodes = doc.selectNodes("m2m:conf/upload");
			
			for(Node node : uploadNodes){
				String ctname = node.selectSingleNode("ctname").getText(); 
				String id = node.selectSingleNode("id").getText();
				
				DeviceInfo device = new DeviceInfo();
				device.setContainerName(ctname);
				device.setDeviceID(id);
				
				uploads.add(device);
			}
			
			@SuppressWarnings("unchecked")
			List<Node> downloadNodes = doc.selectNodes("m2m:conf/download");
			
			for(Node node : downloadNodes){
				String ctname = node.selectSingleNode("ctname").getText(); 
				String id = node.selectSingleNode("id").getText();
				
				DeviceInfo device = new DeviceInfo();
				device.setContainerName(ctname);
				device.setDeviceID(id);
				
				downloads.add(device);
			}
			
		}catch(Exception exp){
			LOG.log(Level.SEVERE, exp.getMessage());
		}
	}
}
