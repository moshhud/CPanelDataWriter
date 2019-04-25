package cpaneldatawriter;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import common.ApplicationConstants;
import diskusage.DiskUsageMonitor;
import diskusage.WebHostingServerManagementDTO;
import util.ReturnObject;
import util.SSLCertificate;
import webhostingpackage.WebHostingPackageInfoDTO;
import webhostingpackage.WebHostingPackageResourceDTO;

public class ProcessPackageWritingRequest {
	static Logger logger = Logger.getLogger(ProcessPackageWritingRequest.class);
	
	String method[] = {"addpkg","editpkg","killpkg"};
	int api_version = 1;
	String unlimited = "unlimited";
	
	@SuppressWarnings("unchecked")
	public  boolean processData(LinkedHashMap<Long, WebHostingPackageInfoDTO> data) {
		boolean status = false;
		try {
			if(data!=null && data.size()>0) {
				
				LinkedHashMap<Long,WebHostingServerManagementDTO> serverData = null;
				ReturnObject ro = new ReturnObject();
				
				for(WebHostingPackageInfoDTO dto :data.values()) {
					ro = DiskUsageMonitor.getInstance().getData();
					if (ro != null && ro.getIsSuccessful() && ro.getData() instanceof LinkedHashMap) {
						serverData = (LinkedHashMap<Long, WebHostingServerManagementDTO>)ro.getData();
					}
					
					if(serverData!=null && serverData.size()>0) {
						for(WebHostingServerManagementDTO serverDTO:serverData.values()) {
							logger.debug("Server ID: "+serverDTO.getID());
							status = sendRequest(dto,serverDTO);
						}
					}
					
					if(status) {
						updateStatus(dto.getID()+"");
						
					}
										
				}
			}
			
		}catch(Exception e) {
			logger.fatal(e.toString());
		}
		
		return status;
	}
	
	public boolean sendRequest(WebHostingPackageInfoDTO dto ,WebHostingServerManagementDTO serverDTO){
		boolean status = false;	
		String API = serverDTO.getApiURL();
		try{
			int methodType = dto.getCpanelWrittingStatus();
			int index=0;
			switch (methodType) {
			     case ApplicationConstants.CPANEL_PACKAGE.ADD:
			    	 index=0;
			    	 break;			    	 
			     case ApplicationConstants.CPANEL_PACKAGE.EDIT:
			    	 index=1;			    	
			    	 break;
			     case ApplicationConstants.CPANEL_PACKAGE.DELETE:
			    	 index=2;
			    	 break;
			     default:
			    	 index=3;
			    	 break;			     
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
			
			
			if(index==0||index==1) {
				writer.write("&ip=n");
				writer.write("&cgi=1");
				writer.write("&frontpage=0");
				writer.write("&cpmod=paper_lantern");
				writer.write("&language=en");
				writer.write("&hasshell=0");
				writer.write("&name="+getEncodedValue(dto.getPackageName()));
				for(WebHostingPackageResourceDTO resDTO:dto.getPackResourceDTOMap().values()) {
					writer.write("&maxftp="+(resDTO.getMaxFTPAccount() > 0 ? resDTO.getMaxFTPAccount() : unlimited));
					writer.write("&maxsql="+(resDTO.getMaxDatabases() > 0 ? resDTO.getMaxDatabases() : unlimited));
					writer.write("&maxpop="+(resDTO.getMaxEmailAccount() > 0 ? resDTO.getMaxEmailAccount() : unlimited));
					writer.write("&MAXLST="+(resDTO.getMaxEmailList() > 0 ? resDTO.getMaxEmailList() : unlimited));
					writer.write("&maxsub="+(resDTO.getMaxSubDomain() > 0 ? resDTO.getMaxSubDomain() : unlimited));
					writer.write("&maxpark="+(resDTO.getMaxParkedDomain() > 0 ? resDTO.getMaxParkedDomain() : unlimited));
					writer.write("&maxaddon="+(resDTO.getMaxAddonDomain() > 0 ? resDTO.getMaxAddonDomain() : unlimited));
					writer.write("&bwlimit="+(resDTO.getMonthlyBW() > 0 ? resDTO.getMonthlyBW() : unlimited));
					/*writer.write("&max_emailacct_quota="+(resDTO.getMaxEmailAccount() > 0 ? resDTO.getMaxEmailAccount() : unlimited));*/					
					writer.write("&MAX_EMAIL_PER_HOUR="+(resDTO.getMaxHourlyEmail() > 0 ? resDTO.getMaxHourlyEmail() : unlimited));
					writer.write("&MAX_DEFER_FAIL_PERCENTAGE="+(resDTO.getMaxPerFailureMsg() > 0 ? resDTO.getMaxPerFailureMsg() : unlimited));
					writer.write("&quota="+(resDTO.getDiskQuota() > 0 ? resDTO.getDiskQuota() : unlimited));
				}
				
			}
			if(index==2) {
				writer.write("&pkgname="+getEncodedValue(dto.getPackageName()));
				
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
			ro = dao.updateStatus(id,PackageDataWriter.PACKAGE_TABLE_NAME);
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
	
	public String getHeaderValue(String login, String token) {		
		if(login==null||token==null) {
			login = CPanelDataWriterMain.login;
			token = CPanelDataWriterMain.token;
		}		
		String authorization = "whm "+ login+":"+ token;
		return authorization;
	}

}
