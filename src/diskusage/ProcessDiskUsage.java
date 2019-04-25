package diskusage;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.net.ssl.HttpsURLConnection;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import common.ApplicationConstants;
import common.SmsMailLogDAO;
import cpaneldatawriter.CPanelDataWriterMain;
import cpaneldatawriter.WebHostingDAO;
import util.ReturnObject;
import util.SSLCertificate;


public class ProcessDiskUsage {
	static Logger logger = Logger.getLogger(ProcessDiskUsage.class);
	String method[] = {"getdiskusage"};
	int api_version = 1;
	LinkedHashMap<String, DiskUsageDTO> usageData = null;
	
	public  boolean processData(LinkedHashMap<Long, WebHostingServerManagementDTO> data) {
		boolean status = false;
		
		try {
			if(data!=null && data.size()>0) {
				for(WebHostingServerManagementDTO dto:data.values()) {
					if(sendRequest(dto)) {						
						logger.debug("Previous Percent: "+dto.getDiskUsageDTO().getPercentage());
						DiskUsageDTO diskUsageDTO = usageData.get("/");
						diskUsageDTO.setID(dto.getID());
						logger.debug("Server ID: "+diskUsageDTO.getID());
						logger.debug("Mount: "+diskUsageDTO.getMount());
						logger.debug("File System: "+diskUsageDTO.getFilesystem());
						logger.debug("Total: "+diskUsageDTO.getTotal());
						logger.debug("Available: "+diskUsageDTO.getAvailable());
						logger.debug("Used: "+diskUsageDTO.getUsed());
						logger.debug("Percent: "+diskUsageDTO.getPercentage());
						updateStatus(diskUsageDTO);
						status = true;	
						
						if(dto.getNotification()==1) {
							sendAlertNotification(dto);
						}
						
					}					
				}
			}
		}
		catch(Exception e) {
			logger.fatal(e.toString());
		}
		
		return status;
	}
	
	
	
	
	public boolean sendRequest(WebHostingServerManagementDTO dto){
		
		boolean status = false;
		try {
			SSLCertificate ssl = new SSLCertificate();
			ssl.setSSL();
			String API_URL = dto.getApiURL()+CPanelDataWriterMain.API_TYPE+method[0];
			logger.debug("Calling: "+API_URL);
			URL url=new URL(API_URL);
			HttpsURLConnection con=(HttpsURLConnection)url.openConnection();
			con.setDoInput(true);
			con.setDoOutput(true);
			con.setRequestProperty("Authorization", getHeaderValue(dto.getApiLogin(),dto.getApiToken()));
			con.setRequestMethod("POST");
			con.setUseCaches(false);
			
			PrintWriter writer=new PrintWriter(con.getOutputStream());
			writer.write("&api.version="+api_version);			
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
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
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
			
			if(result==1) {
				Iterator<Map.Entry> itr1 = null;
				Map data = ((Map)jsonObject.get("data")); 	          	        
				JSONArray ja = (JSONArray) data.get("partition"); 
				Iterator itr2 = ja.iterator(); 
				
				usageData = new LinkedHashMap<String, DiskUsageDTO>();
				DiskUsageDTO dto = null;
		        while (itr2.hasNext())  
		        { 
		            itr1 = ((Map) itr2.next()).entrySet().iterator();
		            dto = new DiskUsageDTO();
		            while (itr1.hasNext()) { 
		                Map.Entry pair = itr1.next();
		                 
		                if(pair.getKey().equals("mount")) {
		                	dto.setMount(pair.getValue().toString());
		                }else if(pair.getKey().equals("filesystem")) {
		                	dto.setFilesystem(pair.getValue().toString());
		                }
		                else if(pair.getKey().equals("disk")) {
		                	dto.setDisk(pair.getValue().toString());
		                }
		                else if(pair.getKey().equals("device")) {
		                	dto.setDevice(pair.getValue().toString());
		                }
		                else if(pair.getKey().equals("percentage")) {
		                	dto.setPercentage(Long.parseLong(pair.getValue().toString()));
		                }
		                else if(pair.getKey().equals("total")) {
		                	dto.setTotal(Long.parseLong(pair.getValue().toString()));
		                }
		                else if(pair.getKey().equals("used")) {
		                	dto.setUsed(Long.parseLong(pair.getValue().toString()));
		                }
		                else if(pair.getKey().equals("available")) {
		                	dto.setAvailable(Long.parseLong(pair.getValue().toString()));
		                }
		                
		            } 
		            usageData.put(dto.getMount(), dto);
		            
		        }
			}
			
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
	
	public void updateStatus(DiskUsageDTO diskUsageDTO) {
		ReturnObject ro = new ReturnObject();
		try {
			WebHostingDAO dao = new WebHostingDAO();
			ro = dao.updateWebHostingServerDiskUsage(diskUsageDTO,DiskUsageMonitor.WEBHOSTING_SERVER_TABLE_NAME);
			if(ro.getIsSuccessful()) {
				logger.debug("Table Status Updated successfully for : "+diskUsageDTO.getID());
			}
		}catch(Exception e) {
			 logger.fatal("Error : "+e);
		  
		}
	}
	
	public void sendAlertNotification(WebHostingServerManagementDTO dto) {
		try {
			if(dto.getAlarmThreshold()<=dto.getDiskUsageDTO().getPercentage()) {
				logger.debug("Disk usage is exceeded for the server: "+dto.getServerName());
				logger.debug("Alarm(%): "+dto.getAlarmThreshold());
				logger.debug("Current Usage: "+dto.getDiskUsageDTO().getPercentage());
				StringBuilder sb = new StringBuilder();
				sb.append("Dear Concern,<br>");
				sb.append("Please check below server and take necessary steps to reduce disk usage: <br><br>");
				sb.append("Server IP: "+dto.getServerIP()+" <br>");
				sb.append("Server Name: "+dto.getServerName()+" <br>");
				sb.append("Current usage(%): "+dto.getDiskUsageDTO().getPercentage()+" <br>");
				sb.append("Alarm (%): "+dto.getAlarmThreshold()+" <br>");
				sb.append("<br>");
				sb.append("Regards,<br>");
				sb.append("Webhosting Automation Service.");
				
				String msgText = sb.toString();
				String mailBody = new String(msgText.getBytes(),"UTF-8");
				SmsMailLogDAO log = new SmsMailLogDAO(
						ApplicationConstants.EMAIL_CONSTANT.MSG_TYPE_EMAIL,
						ApplicationConstants.EMAIL_CONSTANT.TO, 
						ApplicationConstants.EMAIL_CONSTANT.FROM, 
						ApplicationConstants.EMAIL_CONSTANT.SUBJECT+": "+dto.getServerName(),
						mailBody, 
						ApplicationConstants.EMAIL_CONSTANT.CC);
				log.run();
			}
		}
		catch(Exception e) {
			 logger.fatal("Error : "+e);		  
		}
	}

}
