package cpaneldatawriter;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import diskusage.DiskUsageMonitor;
import mail.MailService;
import shutdown.ShutDownListener;
import shutdown.ShutDownService;

public class CPanelDataWriterMain  implements ShutDownListener{
	static Logger logger = Logger.getLogger(CPanelDataWriterMain.class);
	public static CPanelDataWriterMain ob = null;
	public static PackageDataWriter obPackageDataWriter = null;
	public static ClientDataWriter obClientDataWriter = null;
	public static DiskUsageMonitor obDiskUsageMonitor = null;
	public static MailService obMailService = null;
	public static long interval = 60;
	public static long emailCheck = 60;
	public static String API = "https://mail.btcl.com.bd:2087";
	public static String API_TYPE = "/json-api/";
	public static String login = "root";
	public static String token = "IUMYZ028WQ75YE233TFSRNNT3WQDJXUT00";
	
	public static void main(String[] args)	
	{
		PropertyConfigurator.configure("log4j.properties");
		ob = new CPanelDataWriterMain();
		LoadConfiguration();
		
		obPackageDataWriter = PackageDataWriter.getInstance();
		obPackageDataWriter.start();
		
		obClientDataWriter = ClientDataWriter.getInstance();
		obClientDataWriter.start();
		
		obDiskUsageMonitor = DiskUsageMonitor.getInstance();
		obDiskUsageMonitor.start();
		
		obMailService = MailService.getInstance();
		obMailService.start();
		
		ShutDownService.getInstance().addShutDownListener(ob);		
		logger.debug("Cpanel Data Writer started successfully.");
		 
	}

	@Override
	public void shutDown() {
		// TODO Auto-generated method stub
		obPackageDataWriter.shutdown();
		obClientDataWriter.shutdown();
		obDiskUsageMonitor.shutdown();
		obMailService.shutdown();
		logger.debug("Shut down server successfully");
		System.exit(0);
	}
	
	public static void LoadConfiguration(){
		FileInputStream fileInputStream = null;
		String strConfigFileName = "properties.cfg";
		try
	    {
			Properties properties = new Properties();
		    File configFile = new File(strConfigFileName);
		    if (configFile.exists())
		      {
		    	fileInputStream = new FileInputStream(strConfigFileName);
		        properties.load(fileInputStream);	
		        		        
		        if(properties.get("API")!=null){
		        	API = (String) properties.get("API");
		        }
		        if(properties.get("login")!=null){
		        	login = (String) properties.get("login");
		        }
		        if(properties.get("token")!=null){
		        	token = (String) properties.get("token");
		        }		        
		        String strInterval = "";
		        if(properties.get("interval")!=null){
		        	strInterval =  (String) properties.get("interval");
		        	if(strInterval!=null&&strInterval.length()>0) {
		        		interval = Long.parseLong(strInterval);
		        	}
		        }
		        interval = interval*1000;
		        logger.debug("Interval: "+interval);
		        
		        
		        if(properties.get("emailCheck")!=null){
		        	strInterval =  (String) properties.get("emailCheck");
		        	if(strInterval!=null&&strInterval.length()>0) {
		        		emailCheck = Long.parseLong(strInterval);
		        	}
		        	
		        	
		        }
		        emailCheck = emailCheck*1000;
		        logger.debug("emailCheck interval: "+emailCheck);
		        
		        fileInputStream.close();

		      }
	    }
		catch (Exception ex)
	    {
	      logger.fatal("Error while loading configuration file :" + ex.toString(), ex);
	   
	      System.exit(0);
	    }
	    finally
	    {
	      if (fileInputStream != null)
	      {
	        try
	        {
	        	fileInputStream.close();
	        }
	        catch (Exception ex)
	        {
	        	logger.fatal(ex.toString());
	        }
	      }
	    }
		
		
	}
	

}
