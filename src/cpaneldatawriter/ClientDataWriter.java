package cpaneldatawriter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import org.apache.log4j.Logger;
import util.ReturnObject;
import webhosting.ManageWebHostingDTO;

public class ClientDataWriter extends Thread{
	public static ClientDataWriter obClientDataWriter = null;
	static Logger logger = Logger.getLogger(ClientDataWriter.class);
	boolean running = false;
	String ids = "";
	LinkedHashMap<Long,ManageWebHostingDTO> data = null;
	public static final String WEBHOSTING_TABLE_NAME = "at_webhosting";
	
	public static ClientDataWriter getInstance(){
		if(obClientDataWriter==null) {
			createInstance();
		}		
		return obClientDataWriter;
	}
	
	public static synchronized ClientDataWriter createInstance(){
		if(obClientDataWriter==null) {
			obClientDataWriter = new ClientDataWriter();
		}		
		return obClientDataWriter;
	}
	
	@Override
	public void run(){
		running = true;
		logger.debug("Client Service started.");
		long t1=0L,t2=0L;
		
		while(running)
        {
			try {
				t1 = System.currentTimeMillis();
				ReturnObject ro = new ReturnObject();
				ro = getData();
				if(ro.getIsSuccessful()) {
					ProcessClientWritingRequest pcwr = new ProcessClientWritingRequest();
					boolean status = pcwr.processData(data);
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
	
	public void shutdown()
	{
		 			
	    running = false;	    
	 }
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public ReturnObject getData() {
		ReturnObject ro = new ReturnObject();
		WebHostingDAO dao = new WebHostingDAO();
		
		try {
			ro = dao.getIDList(WEBHOSTING_TABLE_NAME,"whID"," and whWriteCPanel!=0 and whIsDeleted=0");
			if(ro != null && ro.getIsSuccessful()) {
				ArrayList<Long> IDList = (ArrayList)ro.getData();
				if(IDList!=null&&IDList.size()>0) {
					ids = dao.getStringFromArrayList(IDList, false);
					logger.debug(ids);
					ro = dao.getWebHostingInfoMap(WEBHOSTING_TABLE_NAME, " and whID in("+ids+") ");
					if (ro != null && ro.getIsSuccessful() && ro.getData() instanceof LinkedHashMap) {
						data = (LinkedHashMap<Long, ManageWebHostingDTO>)ro.getData();
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
				logger.debug("No data found");
			}
		}
		catch (Exception ex)
	    {
			logger.fatal("Exception: "+ex.toString());
	    }
		
		
		return ro;
	}
	

}
