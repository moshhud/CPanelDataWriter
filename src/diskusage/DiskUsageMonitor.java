package diskusage;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import org.apache.log4j.Logger;
import cpaneldatawriter.CPanelDataWriterMain;
import cpaneldatawriter.WebHostingDAO;
import util.ReturnObject;

public class DiskUsageMonitor extends Thread{
	public static DiskUsageMonitor obDiskUsageMonitor = null;
	static Logger logger = Logger.getLogger(DiskUsageMonitor.class);
	boolean running = false;	
	String ids = "";
	LinkedHashMap<Long,WebHostingServerManagementDTO> data = null;
	public static final String WEBHOSTING_SERVER_TABLE_NAME = "at_webhosting_servermanagement";
	
	public static DiskUsageMonitor getInstance(){
		if(obDiskUsageMonitor==null) {
			createInstance();
		}
		return obDiskUsageMonitor;
	}
	
	public static synchronized  DiskUsageMonitor createInstance() {
		if(obDiskUsageMonitor==null) {
			obDiskUsageMonitor = new DiskUsageMonitor();
		}
		return obDiskUsageMonitor;
		
	}
	
	@Override
	public void run(){
		running = true;
		logger.debug("Disk usage monitoring service started.");
		long t1=0L,t2=0L;
		
		while(running)
        {
			try {
				t1 = System.currentTimeMillis();
				ReturnObject ro = new ReturnObject();
				ro = getData();
				if(ro.getIsSuccessful()) {
					ProcessDiskUsage pdu = new ProcessDiskUsage();
					boolean status = pdu.processData(data);
					if(status) {						
						logger.debug("Data writing completed successfully");
						data=null;
						ids="";	
						
					}
				}
								
				t2 = System.currentTimeMillis();				
				logger.debug("Time to finish client service job(ms): "+(t2-t1));
				Thread.sleep(CPanelDataWriterMain.interval);
				
			}
			catch(Exception e){
		   	  	 logger.fatal("Error : "+e);		   	  	  
		   	}
			
			
        }
	}	
	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public ReturnObject getData() {
		ReturnObject ro = new ReturnObject();
		WebHostingDAO dao = new WebHostingDAO();
		
		try {
			ro = dao.getIDList(WEBHOSTING_SERVER_TABLE_NAME,"smID"," AND smIsBlocked=0  and smAPIURL is not null");
			if(ro != null && ro.getIsSuccessful()) {
				ArrayList<Long> IDList = (ArrayList)ro.getData();
				if(IDList!=null&&IDList.size()>0) {
					ids = dao.getStringFromArrayList(IDList, false);
					logger.debug(ids);
					ro = dao.getWebHostingServerInfoMap(WEBHOSTING_SERVER_TABLE_NAME, " and smID in("+ids+") ");
					if (ro != null && ro.getIsSuccessful() && ro.getData() instanceof LinkedHashMap) {
						data = (LinkedHashMap<Long, WebHostingServerManagementDTO>)ro.getData();
						if (data != null && data.size() > 0) {
							ro = ReturnObject.clearInstance(ro);
							ro.setIsSuccessful(true);
							ro.setData(data);
						}
					}					
				}
			}
			else {
				ro.setIsSuccessful(false);
				logger.debug("No data found to write into cpanel");
			}
		}
		catch (Exception ex)
	    {
			logger.fatal("Exception: "+ex.toString());
	    }
		
		
		return ro;
	}

}
