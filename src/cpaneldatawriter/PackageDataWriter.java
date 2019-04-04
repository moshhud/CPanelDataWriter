package cpaneldatawriter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import org.apache.log4j.Logger;
import util.ReturnObject;
import webhostingpackage.WebHostingPackageInfoDTO;
import webhostingpackage.WebHostingPackageResourceDTO;

public class PackageDataWriter extends Thread{
	
	public static PackageDataWriter obPackageDataWriter = null;
	static Logger logger = Logger.getLogger(PackageDataWriter.class);
	boolean running = false;
	LinkedHashMap<Long, WebHostingPackageInfoDTO> packageData = null;
	String ids = "";
	public static final String PACKAGE_TABLE_NAME = "at_webhosting_package";
	public static final String PACKAGE_DETAILS_TABLE_NAME = "at_webhosting_package_details";
	
	public static PackageDataWriter getInstance(){
		if(obPackageDataWriter==null) {
			createInstance();
		}
		return obPackageDataWriter;
	}
	
	public static synchronized PackageDataWriter createInstance(){
		if(obPackageDataWriter==null) {
			obPackageDataWriter = new PackageDataWriter();
		}
		return obPackageDataWriter;
	}
	
	@Override
	public void run(){
		running = true;
		logger.debug("Package Service started.");
		long t1=0L,t2=0L;
		
		while(running)
        {
			try {
				t1 = System.currentTimeMillis();
				ReturnObject ro = new ReturnObject();
				ro = getData();
				if(ro.getIsSuccessful()) {
					ProcessPackageWritingRequest pwr = new ProcessPackageWritingRequest();
					boolean status = pwr.processData(packageData);
					if(status) {						
						logger.debug("Data writing completed successfully");
						packageData=null;
						ids="";	
						
					}
					
				}
				t2 = System.currentTimeMillis();				
				logger.debug("Time to finish job(ms): "+(t2-t1));
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
			ro = dao.getIDList(PACKAGE_TABLE_NAME,"webPackID"," and webPackWriteCPanel!=0");
			if(ro != null && ro.getIsSuccessful()) {
				ArrayList<Long> IDList = (ArrayList)ro.getData();
				if(IDList!=null&&IDList.size()>0) {
					ids = dao.getStringFromArrayList(IDList, false);
					logger.debug(ids);
					ro = dao.getPackageInfoMap(PACKAGE_TABLE_NAME, " and webPackID in("+ids+") ");
					if (ro != null && ro.getIsSuccessful() && ro.getData() instanceof LinkedHashMap) {
						packageData = (LinkedHashMap<Long, WebHostingPackageInfoDTO>)ro.getData();
						if (packageData != null && packageData.size() > 0) {
							Thread t1 = new Thread(new Runnable() {
								@Override
								public void run() {
									WebHostingPackageInfoDTO packDTO = null;
									LinkedHashMap<Long, WebHostingPackageResourceDTO> resourceData;
									long key;
									ReturnObject ro = dao.getPackageResourceMap(PACKAGE_DETAILS_TABLE_NAME, " and wpdPackageID in("+ids+")");
									if (ro != null && ro.getIsSuccessful() && ro.getData() instanceof LinkedHashMap) {
										resourceData = (LinkedHashMap<Long, WebHostingPackageResourceDTO>) ro.getData();
										if (resourceData != null && resourceData.size() > 0) {
											for(WebHostingPackageResourceDTO resourceDTO : resourceData.values()) {
												key = resourceDTO.getPackageID();
												if (packageData.containsKey(key)) {
													packDTO = packageData.get(key);
													if (packDTO != null) {
														if(packDTO.getPackResourceDTOMap()==null) {
															packDTO.setPackResourceDTOMap(new LinkedHashMap<Long, WebHostingPackageResourceDTO>());
														}														
														packDTO.getPackResourceDTOMap().put(resourceDTO.getID(), resourceDTO);
													}
												}
											}
										}
										
									}
								}
							});
							t1.start();
							
							while(true) {
								if (t1 != null && t1.isAlive()) {
									t1.join(10);
								}else {
									break;
								}
								
							}
						}
					}
				}
				ro = ReturnObject.clearInstance(ro);
				ro.setIsSuccessful(true);
				ro.setData(packageData);
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
	
	public void shutdown()
	{
		logger.debug("Server shuting down");			
	    running = false;	    
	 }
	

}
