package cpaneldatawriter;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import javax.net.ssl.HttpsURLConnection;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import diskusage.DiskUsageMonitor;
import diskusage.WebHostingServerManagementDTO;
import util.ReturnObject;
import webhosting.ManageWebHostingDTO;



public class ProcessClientWritingRequest {
	static Logger logger = Logger.getLogger(ProcessClientWritingRequest.class);
	String method[] = {"createacct","suspendacct","unsuspendacct","changepackage"};
	int api_version = 1;
	
	public  boolean processData(LinkedHashMap<Long, ManageWebHostingDTO> data) {
		boolean status = false;
		try {
			if(data!=null && data.size()>0) {
				for(ManageWebHostingDTO dto :data.values()) {					
					if(sendRequest(dto)) {
						updateStatus(dto.getID()+"");
						status = true;
					}
				}
			}
		}
		catch(Exception e) {
			logger.fatal(e.toString());
		}
		
		return status;
	}
	
	public boolean sendRequest(ManageWebHostingDTO dto){
		logger.debug(dto.getUserName());
		boolean status = false;
		boolean isExistingClient = false;
		String API = null;
		try {
			int methodType = dto.getCpanelWrittingStatus();
			int index=0;
			switch (methodType) {
		     case 1:
		    	 index=0;
		    	 API = getRandomlySelectedServerAPI();					
		    	 break;			    	 
		     case 2:
		    	 index=1;
		    	 isExistingClient=true;
		    	  break;
		     case 3:
		    	 index=2;
		    	 isExistingClient=true;
		    	 break;
		     case 4:
		    	 index=3;
		    	 isExistingClient=true;
		    	 break;			     
		    }
			
			if(isExistingClient) {
				WebHostingDAO dao = new WebHostingDAO();
		    	WebHostingServerManagementDTO serverDTO = dao.getWebHostingServerInfoDTO(DiskUsageMonitor.WEBHOSTING_SERVER_TABLE_NAME, " and smID="+dto.getServerID());
		    	API = serverDTO.getApiURL();
			}
			if(API==null) {
				API = CPanelDataWriterMain.API;
			}
			String API_URL = API+CPanelDataWriterMain.API_TYPE+method[index];
			logger.debug("Calling: "+API_URL);
			
			URL url=new URL(API_URL);
			HttpsURLConnection con=(HttpsURLConnection)url.openConnection();
			con.setDoInput(true);
			con.setDoOutput(true);
			con.setRequestProperty("Authorization", getHeaderValue());
			con.setRequestMethod("POST");
			con.setUseCaches(false);
			
			PrintWriter writer=new PrintWriter(con.getOutputStream());
			writer.write("&api.version="+api_version);
			
			if(index==0) {
				writer.write("&ip=n");
				writer.write("&domain="+dto.getDomain());
				writer.write("&plan="+dto.getPackageName());
				writer.write("&username="+dto.getUserName());
				writer.write("&password="+dto.getUserPass());
				writer.write("&contactemail="+dto.getEmail());				
				writer.write("&reseller="+dto.getClientType());
				
			}
			else if(index==1) {
				writer.write("&user="+dto.getUserName());
				writer.write("&reason=Admin suspended the service");
			}
			else if(index==2) {
				writer.write("&user="+dto.getUserName());				
			}
			else if(index==3) {
				writer.write("&user="+dto.getUserName());	
				writer.write("&pkg="+dto.getPackageName());
			}
			
            writer.flush();
			
			BufferedReader reader=new BufferedReader(new InputStreamReader(con.getInputStream()));
			
			String line="";
			StringBuilder sb = new StringBuilder();
			while((line=reader.readLine())!=null){
				sb.append(line+"\n");				
			}			
			reader.close();
			
			String response = getResponseStatus(sb.toString());
			String arr[] = response.split(":");
			if(arr[1].equals("1")) {
				status = true;
				
			}
		
			
		}
		catch(Exception e){
			logger.fatal("Error: "+e.toString());
		 }
		
		return status;
	}
	
	public void updateStatus(String id) {
		ReturnObject ro = new ReturnObject();
		try {
			WebHostingDAO dao = new WebHostingDAO();
			ro = dao.updateWebHostingStatus(id,ClientDataWriter.WEBHOSTING_TABLE_NAME);
			if(ro.getIsSuccessful()) {
				logger.debug("Table Status Updated successfully for : "+id);
			}
		}catch(Exception e) {
			 logger.fatal("Error : "+e);
		  
		}
	}
	
	@SuppressWarnings({ "rawtypes" })
	public String getResponseStatus(String responseString) {
		String responseStatus = "";
		Long result = 0L;
		String reason ="";
		String command = "";
		
		try {
			JSONParser parser = new JSONParser();
			JSONObject jsonObject = (JSONObject) parser.parse(responseString);
			Map metadata = ((Map)jsonObject.get("metadata"));					
			command = (String)metadata.get("command");
			result = (Long)metadata.get("result");
			reason = (String)metadata.get("reason");			
			responseStatus = command+":"+""+result+":"+reason;
			logger.debug(responseStatus);			
			
		}catch(Exception e) {
			 logger.fatal("Error : "+e);
			  
		}
		return responseStatus;
	}
		
	public String getHeaderValue() {
		String authorization = "whm "+ CPanelDataWriterMain.login+":"+ CPanelDataWriterMain.token;
		return authorization;
	}
	
	@SuppressWarnings("unchecked")
	public String getRandomlySelectedServerAPI() {
		String API = null;
		try {
			LinkedHashMap<Long,WebHostingServerManagementDTO> serverData = null;
			 ReturnObject ro = new ReturnObject();
			 ArrayList<Long> ids = new ArrayList<Long>();
			 
			 ro = DiskUsageMonitor.getInstance().getData();
			 if (ro != null && ro.getIsSuccessful() && ro.getData() instanceof LinkedHashMap) {
				serverData = (LinkedHashMap<Long, WebHostingServerManagementDTO>)ro.getData();
			 }
			 if(serverData!=null && serverData.size()>0) {
					for(WebHostingServerManagementDTO serverDTO:serverData.values()) {
						logger.debug("Server ID: "+serverDTO.getID());
						logger.debug("Percent: "+serverDTO.getDiskUsageDTO().getPercentage());
						 if(serverDTO.getMaxAllowed()>serverDTO.getDiskUsageDTO().getPercentage()) {
							 ids.add(serverDTO.getID());
						 }
					}
				}
			 if(ids.size()>0) {
				 WebHostingServerManagementDTO serverDTO = null;
				 Random random = new Random();
				 int  n = random.nextInt(ids.size());				 
				 long id = ids.get(n);			
				 logger.debug("Selected Server ID: "+id);
				 serverDTO = serverData.get(id);				 
				 API = serverDTO.getApiURL();
			 }
		}
		catch(Exception e) {
			 logger.fatal("Error : "+e);
			  
		}
		
		return API;
	}
	

}
