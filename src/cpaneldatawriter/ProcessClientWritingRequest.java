package cpaneldatawriter;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import javax.net.ssl.HttpsURLConnection;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import common.ApplicationConstants;
import common.SmsMailLogDAO;
import diskusage.DiskUsageMonitor;
import diskusage.WebHostingServerManagementDTO;
import util.ReturnObject;
import util.SSLCertificate;
import webhosting.ManageWebHostingDTO;



public class ProcessClientWritingRequest {
	static Logger logger = Logger.getLogger(ProcessClientWritingRequest.class);
	String method[] = {"createacct","suspendacct","unsuspendacct","changepackage","setupreseller","setacls"};
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
		logger.debug(dto.getUserName()+","+dto.getServerID());
		boolean status = false;
		String API = null;
		try {
			int methodType = dto.getCpanelWrittingStatus();
			int index=0;
			switch (methodType) {
		     case ApplicationConstants.CPANEL_ACCOUNT.CREATE_ACCOUNT:
		    	 index=0;
		    	 dto.setServerID(getRandomlySelectedServer());				
		    	 break;			    	 
		     case ApplicationConstants.CPANEL_ACCOUNT.SUSPEND_ACCOUNT:
		    	 index=1;
		    	  break;
		     case ApplicationConstants.CPANEL_ACCOUNT.UNSUSPEND_ACCOUNT:
		    	 index=2;
		    	 break;
		     case ApplicationConstants.CPANEL_ACCOUNT.PACKAGE_CHANGE:
		    	 index=3;
		    	 break;			     
		    }
						
	    	WebHostingServerManagementDTO serverDTO = null;	    	
	    	if(dto.getServerID()>0) {
	    		WebHostingDAO dao = new WebHostingDAO();
	    		serverDTO = dao.getWebHostingServerInfoDTO(DiskUsageMonitor.WEBHOSTING_SERVER_TABLE_NAME, " and smID="+dto.getServerID());
	    	}
	    	
	    	if(serverDTO!=null) {
	    		API = serverDTO.getApiURL();
	    	}
	    	
			if(API==null) {
				API = CPanelDataWriterMain.API;
			}
			
			SSLCertificate ssl = new SSLCertificate();
			ssl.setSSL();
			
			String API_URL = API+CPanelDataWriterMain.API_TYPE+method[index];
			logger.debug("Calling: "+API_URL);
			
			
			URL url=new URL(API_URL);
			HttpsURLConnection con=(HttpsURLConnection)url.openConnection();
			con.setDoInput(true);
			con.setDoOutput(true);
			con.setRequestProperty("Authorization", getHeaderValue(serverDTO.getApiLogin(),serverDTO.getApiToken()));
			con.setRequestMethod("POST");
			con.setUseCaches(false);
			
			PrintWriter writer=new PrintWriter(con.getOutputStream());
			writer.write("&api.version="+api_version);
			
			if(index==0) {
				writer.write("&ip=n");
				writer.write("&domain="+getEncodedValue(dto.getDomain()));
				writer.write("&plan="+getEncodedValue(dto.getPackageName()));
				writer.write("&username="+getEncodedValue(dto.getUserName()));
				writer.write("&password="+getEncodedValue(dto.getUserPass()));
				writer.write("&contactemail="+getEncodedValue(dto.getEmail()));				
				writer.write("&reseller="+getEncodedValue(dto.getClientType()+""));
				
			}
			else if(index==1) {
				writer.write("&user="+getEncodedValue(dto.getUserName()));
				writer.write("&reason=Admin suspended the service");
			}
			else if(index==2) {
				writer.write("&user="+getEncodedValue(dto.getUserName()));			
			}
			else if(index==3) {
				writer.write("&user="+getEncodedValue(dto.getUserName()));
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
			logger.debug("response: "+response);
			String arr[] = response.split(":");
			if(arr[1].equals("1")) {
				status = true;				
				if(methodType==1) {					
					status = setServerID(dto.getID(),dto.getServerID());
					if(dto.getClientType()==1) {
						if(setResellerACL(dto,serverDTO)) {
							logger.debug("Reseller ACL Permission added.");
						}
					}
					sendEMailToClient(dto,API);
				}
				
			}else {
				//send notification if failed to complete process successfully
				logger.debug("Failed: "+response);
			}
		
			
		}
		catch(Exception e){
			logger.fatal("Error: "+e.toString());
		 }
		
		return status;
	}
	
	public boolean setResellerACL(ManageWebHostingDTO dto,WebHostingServerManagementDTO serverDTO){
		boolean status = false;
		String API = "";
		try {
			SSLCertificate ssl = new SSLCertificate();
			ssl.setSSL();
			int index = ApplicationConstants.CPANEL_ACCOUNT.ACL_SETUP-1;
			API = serverDTO.getApiURL();
			String API_URL = API+CPanelDataWriterMain.API_TYPE+method[index];
			logger.debug("Calling(ACL): "+API_URL);
			
			
			URL url=new URL(API_URL);
			HttpsURLConnection con=(HttpsURLConnection)url.openConnection();
			con.setDoInput(true);
			con.setDoOutput(true);
			con.setRequestProperty("Authorization", getHeaderValue(serverDTO.getApiLogin(),serverDTO.getApiToken()));
			con.setRequestMethod("POST");
			con.setUseCaches(false);
			
			PrintWriter writer=new PrintWriter(con.getOutputStream());
			writer.write("&api.version="+api_version);
			writer.write("&reseller="+getEncodedValue(dto.getUserName()));
			writer.write("&acl-add-pkg=1");
			writer.write("&acl-edit-pkg=1");
			writer.write("&acl-list-pkgs=1");
			writer.write("&acl-list-accts=1");
			writer.write("&acl-acct-summary=1");
			writer.write("&acl-create-acct=1");
			writer.write("&acl-suspend-acct=1");
			writer.write("&acl-passwd=1");
			writer.write("&acl-upgrade-account=1");
			writer.write("&acl-kill-acct=1");
			writer.write("&acl-edit-account=1");
			writer.write("&acl-mailcheck=1");
			writer.write("&acl-show-bandwidth=1");
			
			writer.flush();			
			BufferedReader reader=new BufferedReader(new InputStreamReader(con.getInputStream()));
			
			String line="";
			StringBuilder sb = new StringBuilder();
			while((line=reader.readLine())!=null){
				sb.append(line+"\n");				
			}			
			reader.close();
			
			String response = getResponseStatus(sb.toString());
			logger.debug("Response: "+response);
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
	
	public String getEncodedValue(String text) {
		String str = "";
		try{
			str = URLEncoder.encode(text,"utf-8");		     
		}
		catch(Exception e){
			logger.fatal(e.toString());
		}
		
		return str;
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
	
	public boolean setServerID(Long id, Long serverID) {
		 
		ReturnObject ro = new ReturnObject();
		try {
			WebHostingDAO dao = new WebHostingDAO();
			ro = dao.setWebHostingServerID(id,serverID,ClientDataWriter.WEBHOSTING_TABLE_NAME);
			if(ro.getIsSuccessful()) {
				logger.debug("Server id: "+serverID+" Updated successfully for : "+id);
			}
		}catch(Exception e) {
			 logger.fatal("Error : "+e);
		  
		}
		 
		return ro.getIsSuccessful();
	}
	
	public void sendEMailToClient(ManageWebHostingDTO dto,String API) {
		try {
			String API2="";
			logger.debug("dto.getClientType(): "+dto.getClientType());
			if(dto.getClientType()==1) {
				API2 = API.replace(ApplicationConstants.CPANEL_WHM_PORT.WHM_ADMIN+"", ApplicationConstants.CPANEL_WHM_PORT.WHM_RESELLER+"");
			}
			else {
				API2 = API.replace(ApplicationConstants.CPANEL_WHM_PORT.WHM_ADMIN+"", ApplicationConstants.CPANEL_WHM_PORT.WHM_ENDUSER_2+"");
			}
			StringBuilder sb = new StringBuilder();
			sb.append("Dear Sir/Madam,<br>");
			sb.append("Congratulations!!!<br>");
			sb.append("Your cpanel account activated with below credentials: <br><br>");
			sb.append("Login URL: <br>");
			sb.append(API+" <br>");
			sb.append(API2+" <br>");
			sb.append("User Name: "+dto.getUserName()+" <br>");
			sb.append("Password: "+dto.getUserPass()+" <br>");
			 
			sb.append("<br>");
			sb.append("Regards,<br>");
			sb.append("Webhosting Automation Service.");
			
			String msgText = sb.toString();
			String mailBody = new String(msgText.getBytes(),"UTF-8");
			SmsMailLogDAO log = new SmsMailLogDAO(
					ApplicationConstants.EMAIL_CONSTANT.MSG_TYPE_EMAIL,
					dto.getEmail(), 
					ApplicationConstants.EMAIL_CONSTANT.FROM, 
					ApplicationConstants.EMAIL_CONSTANT.SUBJECT_Cpanel_ACCESS,
					mailBody, 
					"");
			log.run();
		}
		catch(Exception e) {
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
		
	public String getHeaderValue(String login, String token) {
		
		if(login==null||token==null) {
			login = CPanelDataWriterMain.login;
			token = CPanelDataWriterMain.token;
		}
		
		String authorization = "whm "+ login+":"+ token;
		return authorization;
	}
	
	@SuppressWarnings("unchecked")
	public Long getRandomlySelectedServer() {		 
		Long serverID = 0L;
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
				 Random random = new Random();
				 int  n = random.nextInt(ids.size());				 
				 long id = ids.get(n);
				 serverID = id;
				 logger.debug("Selected Server ID: "+id);
				 //WebHostingServerManagementDTO serverDTO = null;
				 //serverDTO = serverData.get(id);				 
				 //API = serverDTO.getApiURL();
			 }
		}
		catch(Exception e) {
			 logger.fatal("Error : "+e);
			  
		}
		
		return serverID;
	}
	

}
