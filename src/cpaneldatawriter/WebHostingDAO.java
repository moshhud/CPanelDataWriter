package cpaneldatawriter;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.log4j.Logger;

import com.mysql.jdbc.PreparedStatement;

import databasemanager.DatabaseManager;
import diskusage.DiskUsageDTO;
import diskusage.WebHostingServerManagementDTO;
import mail.MailDTO;
import mail.MailServerInformationDTO;
import util.ReturnObject;
import webhosting.ManageWebHostingDTO;
import webhostingpackage.WebHostingPackageInfoDTO;
import webhostingpackage.WebHostingPackageResourceDTO;

public class WebHostingDAO {
	Logger logger = Logger.getLogger(WebHostingDAO.class);
	ReturnObject ro = new ReturnObject();
	
	@SuppressWarnings({ "null" })
	public ReturnObject getIDList(String tableName,String colName,String condition) {
		Connection connection = null;
		Statement stmt = null;
		ResultSet rs = null;
		String sql = null;		
		List<Long> IDList = null;	
		try {
			connection = DatabaseManager.getInstance().getConnection();
			IDList = new ArrayList<Long>();
			if(condition==null&&condition.isEmpty()) {
				condition="";
			}
			
			sql = "select "+colName+" from "+tableName+" where 1=1 "+condition;
			//logger.debug(sql);
			stmt = connection.createStatement();
			rs = stmt.executeQuery(sql);
			while(rs.next())
			{
				IDList.add(rs.getLong(colName));
			}
			
			rs.close();
			stmt.close();
			
			if(IDList.size()>0) {
				ro.setData(IDList);
				ro.setIsSuccessful(true);
			}
			
		}catch(Exception ex){
			logger.debug("fatal",ex);
		}finally{
			try{
				DatabaseManager.getInstance().freeConnection(connection);
				
			}catch(Exception exx){}
		}
		return ro;
	}
	
	@SuppressWarnings("null")
	public ReturnObject getPackageInfoMap(String tableName,String condition) {
		Connection connection = null;
		Statement stmt = null;
		ResultSet rs = null;
		String sql = null;	
		
		LinkedHashMap<Long, WebHostingPackageInfoDTO> data = null;
		WebHostingPackageInfoDTO dto = null;
		
		try {
			connection = DatabaseManager.getInstance().getConnection();			 
			if(condition==null&&condition.isEmpty()) {
				condition="";
			}
			sql = "select webPackID,webPackName,webPackWriteCPanel"					
					+ "  from "+tableName+" where 1=1 "+condition;
			
			stmt = connection.createStatement();
			rs = stmt.executeQuery(sql);
			data = new LinkedHashMap<Long, WebHostingPackageInfoDTO>();	
			while(rs.next()) {
				dto = new WebHostingPackageInfoDTO();
				dto.setID(rs.getLong("webPackID"));
				dto.setPackageName(rs.getString("webPackName"));
				dto.setCpanelWrittingStatus(rs.getInt("webPackWriteCPanel"));				
				data.put(rs.getLong("webPackID"), dto);
			}
			
			rs.close();
			stmt.close();
			
			if(data != null && data.size() > 0) {
				ro.setData(data);
				ro.setIsSuccessful(true);
			}
			
			
		}catch(Exception ex){
			logger.debug("fatal",ex);
		}finally{
			try{
				DatabaseManager.getInstance().freeConnection(connection);
				
			}catch(Exception exx){}
		}
		
		
		return ro;
		
	}
	
	@SuppressWarnings("null")
	public ReturnObject getPackageResourceMap(String tableName,String condition) {
		Connection connection = null;
		Statement stmt = null;
		ResultSet rs = null;
		String sql = null;	
		
		LinkedHashMap<Long, WebHostingPackageResourceDTO> data = null;
		WebHostingPackageResourceDTO dto = null;
		
		try {
			connection = DatabaseManager.getInstance().getConnection();			 
			if(condition==null&&condition.isEmpty()) {
				condition="";
			}
			sql = "select wpdID,wpdPackageID,wpdDiskQuota,wpdMonthlyBW,wpdMaxFTPAccount,wpdMaxEmailAccount,"
					+ "wpdMaxEmailList,wpdMaxDatabases,wpdMaxSubDomain,wpdMaxParkedDomain,wpdMaxAddonDomain,"
					+ "wpdMaxHourlyEmail,wpdMaxPerFailureMsg"					
					+ "  from "+tableName+" where 1=1 "+condition;
			
			stmt = connection.createStatement();
			rs = stmt.executeQuery(sql);
			data = new LinkedHashMap<Long, WebHostingPackageResourceDTO>();	
			while(rs.next()) {
				dto = new WebHostingPackageResourceDTO();
				dto.setID(rs.getLong("wpdID"));
				dto.setPackageID(rs.getLong("wpdPackageID"));	
				dto.setDiskQuota(rs.getInt("wpdDiskQuota"));
				dto.setMonthlyBW(rs.getInt("wpdMonthlyBW"));
				dto.setMaxFTPAccount(rs.getInt("wpdMaxFTPAccount"));
				dto.setMaxEmailAccount(rs.getInt("wpdMaxEmailAccount"));
				dto.setMaxEmailList(rs.getInt("wpdMaxEmailList"));
				dto.setMaxDatabases(rs.getInt("wpdMaxDatabases"));
				dto.setMaxSubDomain(rs.getInt("wpdMaxSubDomain"));
				dto.setMaxParkedDomain(rs.getInt("wpdMaxParkedDomain"));
				dto.setMaxAddonDomain(rs.getInt("wpdMaxAddonDomain"));
				dto.setMaxHourlyEmail(rs.getInt("wpdMaxHourlyEmail"));
				dto.setMaxPerFailureMsg(rs.getInt("wpdMaxPerFailureMsg"));
				data.put(rs.getLong("wpdID"), dto);
			}
			
			rs.close();
			stmt.close();
			
			if(data != null && data.size() > 0) {
				ro.setData(data);
				ro.setIsSuccessful(true);
			}
			
			
		}catch(Exception ex){
			logger.debug("fatal",ex);
		}finally{
			try{
				DatabaseManager.getInstance().freeConnection(connection);
				
			}catch(Exception exx){}
		}
		
		
		return ro;
		
	}
	
	public ReturnObject updateStatus(String ids,String tableName) {
		Connection connection = null;
		PreparedStatement pstmt = null;
		String sql = null;
		
		try {
			sql = "UPDATE "+tableName +" set webPackWriteCPanel=? where webPackID in("+ids+")";
			connection = DatabaseManager.getInstance().getConnection();
			pstmt = (PreparedStatement) connection.prepareStatement(sql);
			int i=1;
			pstmt.setInt(i++, 0);
			
			if (pstmt.executeUpdate() > 0) {
				ro.clear();
				ro.setIsSuccessful(true);
			}
			
		}catch (Exception e) {
			logger.fatal("Error : "+e);
		}finally {			
			try{
				DatabaseManager.getInstance().freeConnection(connection);
				
			}catch(Exception exx){}
		}
		
		return ro;
	}
	
	public ReturnObject updateWebHostingStatus(String ids,String tableName) {
		Connection connection = null;
		PreparedStatement pstmt = null;
		String sql = null;
		
		try {
			sql = "UPDATE "+tableName +" set whWriteCPanel=? where whID in("+ids+")";
			connection = DatabaseManager.getInstance().getConnection();
			pstmt = (PreparedStatement) connection.prepareStatement(sql);
			int i=1;
			pstmt.setInt(i++, 0);
			
			if (pstmt.executeUpdate() > 0) {
				ro.clear();
				ro.setIsSuccessful(true);
			}
			
		}catch (Exception e) {
			logger.fatal("Error : "+e);
		}finally {			
			try{
				DatabaseManager.getInstance().freeConnection(connection);
				
			}catch(Exception exx){}
		}
		
		return ro;
	}
	
	public ReturnObject updateWebHostingServerDiskUsage(DiskUsageDTO diskUsageDTO,String tableName) {
		Connection connection = null;
		PreparedStatement pstmt = null;
		String sql = null;
		
		try {
			sql = "UPDATE "+tableName +" set smTotalDiskSize=?,smAvailableDiskSize=?,smCurrentDiskUsage=?,smDiskUsagePercent=? where smID=?";
			connection = DatabaseManager.getInstance().getConnection();
			pstmt = (PreparedStatement) connection.prepareStatement(sql);
			int i=1;
			pstmt.setLong(i++, diskUsageDTO.getTotal());
			pstmt.setLong(i++, diskUsageDTO.getAvailable());
			pstmt.setLong(i++, diskUsageDTO.getUsed());
			pstmt.setLong(i++, diskUsageDTO.getPercentage());
			pstmt.setLong(i++, diskUsageDTO.getID());
			
			if (pstmt.executeUpdate() > 0) {
				ro.clear();
				ro.setIsSuccessful(true);
			}
			
		}catch (Exception e) {
			logger.fatal("Error : "+e);
		}finally {			
			try{
				DatabaseManager.getInstance().freeConnection(connection);
				
			}catch(Exception exx){}
		}
		
		return ro;
	}
	
	
	@SuppressWarnings("null")
	public ReturnObject getWebHostingInfoMap(String tableName,String condition) {
		Connection connection = null;
		Statement stmt = null;
		ResultSet rs = null;
		String sql = null;	
		
		LinkedHashMap<Long, ManageWebHostingDTO> data = null;
		ManageWebHostingDTO dto = null;
		
		try {
			connection = DatabaseManager.getInstance().getConnection();			 
			if(condition==null&&condition.isEmpty()) {
				condition="";
			}
			sql = "select whID,whDomain,whEmail,"
					+ "whUserName,whUserPass,whPackageID,webPackName,whWriteCPanel"					
					+ "  from "+tableName+",at_webhosting_package where webPackID=whPackageID "+condition;
			
			stmt = connection.createStatement();
			rs = stmt.executeQuery(sql);
			data = new LinkedHashMap<Long, ManageWebHostingDTO>();	
			while(rs.next()) {
				dto = new ManageWebHostingDTO();
				dto.setID(rs.getLong("whID"));
				dto.setDomain(rs.getString("whDomain"));
				dto.setEmail(rs.getString("whEmail"));
				dto.setUserName(rs.getString("whUserName"));
				dto.setUserPass(rs.getString("whUserPass"));
				dto.setPackageID(rs.getLong("whPackageID"));
				dto.setPackageName(rs.getString("webPackName"));
				dto.setCpanelWrittingStatus(rs.getInt("whWriteCPanel"));
				data.put(rs.getLong("whID"), dto);
			}
			
			rs.close();
			stmt.close();
			
			if(data != null && data.size() > 0) {
				ro.setData(data);
				ro.setIsSuccessful(true);
			}
			
		}catch(Exception ex){
			logger.debug("fatal",ex);
		}finally{
			try{
				DatabaseManager.getInstance().freeConnection(connection);
				
			}catch(Exception exx){}
		}		
		return ro;
	}
	
	@SuppressWarnings("null")
	public ReturnObject getSMSAndEmailLogMap(String tableName,String condition) {
		Connection connection = null;
		Statement stmt = null;
		ResultSet rs = null;
		String sql = null;	
		
		LinkedHashMap<Long, MailDTO> data = null;
		MailDTO dto = null;
		
		try {
			connection = DatabaseManager.getInstance().getConnection();			 
			if(condition==null&&condition.isEmpty()) {
				condition="";
			}
			sql = "select id,sent_to,sent_cc,"
					+ "sent_subject,sent_body,attachment"					
					+ "  from "+tableName+" where 1=1 "+condition;
			
			stmt = connection.createStatement();
			rs = stmt.executeQuery(sql);
			data = new LinkedHashMap<Long, MailDTO>();	
			while(rs.next()) {
				dto = new MailDTO();
				dto.setID(rs.getLong("id"));
				dto.setToList(rs.getString("sent_to"));
				dto.setCcList(rs.getString("sent_cc"));
				dto.setMailSubject(rs.getString("sent_subject"));
				dto.setMsgText(rs.getString("sent_body"));
				dto.setAttachmentPath(rs.getString("attachment"));
				data.put(rs.getLong("id"), dto);
			}
			
			rs.close();
			stmt.close();
			
			if(data != null && data.size() > 0) {
				ro.setData(data);
				ro.setIsSuccessful(true);
			}
			
		}catch(Exception ex){
			logger.debug("fatal",ex);
		}finally{
			try{
				DatabaseManager.getInstance().freeConnection(connection);
				
			}catch(Exception exx){}
		}		
		return ro;
	}
	
	@SuppressWarnings("null")
	public ReturnObject getWebHostingServerInfoMap(String tableName,String condition) {
		Connection connection = null;
		Statement stmt = null;
		ResultSet rs = null;
		String sql = null;	
		
		LinkedHashMap<Long, WebHostingServerManagementDTO> data = null;
		WebHostingServerManagementDTO dto = null;
		DiskUsageDTO diskUsageDTO = null;
		try {
			connection = DatabaseManager.getInstance().getConnection();			 
			if(condition==null&&condition.isEmpty()) {
				condition="";
			}
			sql = "select smID,smServerName,smServerIP,"
					+ "smOSType,smMaxAllowed,smAlarmThreshold,smNotification,smAPIURL,"
					+ "smDiskUsagePercent"
					+ "  from "+tableName+" where 1=1 "+condition;
			
			stmt = connection.createStatement();
			rs = stmt.executeQuery(sql);
			data = new LinkedHashMap<Long, WebHostingServerManagementDTO>();	
			while(rs.next()) {
				dto = new WebHostingServerManagementDTO();
				diskUsageDTO = new DiskUsageDTO();
				dto.setID(rs.getLong("smID"));
				dto.setServerName(rs.getString("smServerName"));
				dto.setServerIP(rs.getString("smServerIP"));
				dto.setoSType(rs.getInt("smOSType"));
				dto.setMaxAllowed(rs.getInt("smMaxAllowed"));
				dto.setAlarmThreshold(rs.getInt("smAlarmThreshold"));
				dto.setNotification(rs.getInt("smNotification"));
				dto.setApiURL(rs.getString("smAPIURL"));
				diskUsageDTO.setPercentage(rs.getLong("smDiskUsagePercent"));				
				dto.setDiskUsageDTO(diskUsageDTO);
				data.put(rs.getLong("smID"), dto);
				
			}
			
			rs.close();
			stmt.close();
			
			if(data != null && data.size() > 0) {
				ro.setData(data);
				ro.setIsSuccessful(true);
			}
			
			
		}catch(Exception ex){
			logger.debug("fatal",ex);
		}finally{
			try{
				DatabaseManager.getInstance().freeConnection(connection);
				
			}catch(Exception exx){}
		}
		
		
		return ro;
		
	}
	
	@SuppressWarnings("null")
	public WebHostingServerManagementDTO getWebHostingServerInfoDTO(String tableName,String condition) {
		Connection connection = null;
		Statement stmt = null;
		ResultSet rs = null;
		String sql = null;	
		WebHostingServerManagementDTO dto = null;
		DiskUsageDTO diskUsageDTO = null;
		try {
			connection = DatabaseManager.getInstance().getConnection();			 
			if(condition==null&&condition.isEmpty()) {
				condition="";
			}
			sql = "select smID,smServerName,smServerIP,"
					+ "smOSType,smMaxAllowed,smAlarmThreshold,smNotification,smAPIURL,"
					+ "smDiskUsagePercent"
					+ "  from "+tableName+" where 1=1 "+condition;
			
			stmt = connection.createStatement();
			rs = stmt.executeQuery(sql);
			dto = new WebHostingServerManagementDTO();
			diskUsageDTO = new DiskUsageDTO();	
			while(rs.next()) {
				
				dto.setID(rs.getLong("smID"));
				dto.setServerName(rs.getString("smServerName"));
				dto.setServerIP(rs.getString("smServerIP"));
				dto.setoSType(rs.getInt("smOSType"));
				dto.setMaxAllowed(rs.getInt("smMaxAllowed"));
				dto.setAlarmThreshold(rs.getInt("smAlarmThreshold"));
				dto.setNotification(rs.getInt("smNotification"));
				dto.setApiURL(rs.getString("smAPIURL"));
				diskUsageDTO.setPercentage(rs.getLong("smDiskUsagePercent"));				
				dto.setDiskUsageDTO(diskUsageDTO);				
			}
			
			rs.close();
			stmt.close();
			
			
		}catch(Exception ex){
			logger.debug("fatal",ex);
		}finally{
			try{
				DatabaseManager.getInstance().freeConnection(connection);
				
			}catch(Exception exx){}
		}
		
		
		return dto;
		
	}	
	 
	public MailServerInformationDTO getEmailServerInfoDTO() {
		Connection connection = null;
		Statement stmt = null;
		ResultSet rs = null;
		String sql = null;	
		MailServerInformationDTO dto = null;
		 
		try {
			connection = DatabaseManager.getInstance().getConnection();		 
			
			sql = "select columnName,value from at_universal_table where tableName = 'MailServerInformationDTO'";			
			stmt = connection.createStatement();
			rs = stmt.executeQuery(sql);
			dto = new MailServerInformationDTO();
			 
			while(rs.next()) {
				String colname = rs.getString("columnName");
				String colVal = rs.getString("value");
				switch (colname) {
				   case "authEmailAddesstxt":
					   dto.setAuthEmailAddesstxt(colVal);
					   break;
				   case "authEmailPasstxt":
					   dto.setAuthEmailPasstxt(colVal);
					   break;   
				   case "fromAddresstxt":
					   dto.setFromAddresstxt(colVal);
					   break;
				   case "isActive":
					   dto.setActive(colVal.equals("1"));
					   break;
				   case "mailServertxt":
					   dto.setMailServertxt(colVal);
					   break;
				   case "mailServerPorttxt":
					   dto.setMailServerPorttxt(colVal);
					   break;
				   case "tlsRequired":
					   dto.setTlsRequired(colVal.equals("1"));
					   break;
				   case "authFromServerChk":
					   dto.setAuthFromServerChk(colVal.equals("1"));
					   break;
				}
			}
			
			rs.close();
			stmt.close();
			
			
		}catch(Exception ex){
			logger.debug("fatal",ex);
		}finally{
			try{
				DatabaseManager.getInstance().freeConnection(connection);
				
			}catch(Exception exx){}
		}
		
		
		return dto;
		
	}
	
	public String getStringFromArrayList(
			@SuppressWarnings("rawtypes") ArrayList vals,
			boolean useInvertedComma) {
		String data = null;
		try {
			if (vals != null && vals.size() > 0) {
				data = "";
				for (Object val : vals) {
					if (useInvertedComma) {
						data += "'" + val + "',";
					} else {
						data += val + ",";
					}
				}
				if (data != null && data.endsWith(",")) {
					data = data.substring(0, data.length() - 1);
				}
			}
		} catch (RuntimeException e) {
			logger.fatal("RuntimeException", e);
		}
		return data;
	}

}
