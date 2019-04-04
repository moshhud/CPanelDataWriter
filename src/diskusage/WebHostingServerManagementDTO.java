package diskusage;

public class WebHostingServerManagementDTO{
		
	private static final long serialVersionUID = 1L;
	long ID;
	String serverName;
	String serverIP;
	int oSType;
	int maxAllowed;
	int alarmThreshold;
	int notification;
	long lastModificationTime;
	int isBlocked;	
	String apiURL;
	long diskUsagePercent;
	String apiLogin;
	String apiToken;
	
	public String getApiLogin() {
		return apiLogin;
	}

	public void setApiLogin(String apiLogin) {
		this.apiLogin = apiLogin;
	}

	public String getApiToken() {
		return apiToken;
	}

	public void setApiToken(String apiToken) {
		this.apiToken = apiToken;
	}

	DiskUsageDTO diskUsageDTO;
			
	public DiskUsageDTO getDiskUsageDTO() {
		return diskUsageDTO;
	}

	public void setDiskUsageDTO(DiskUsageDTO diskUsageDTO) {
		this.diskUsageDTO = diskUsageDTO;
	}

	public long getDiskUsagePercent() {
		return diskUsagePercent;
	}

	public void setDiskUsagePercent(long diskUsagePercent) {
		this.diskUsagePercent = diskUsagePercent;
	}

	public int getIsBlocked() {
		return isBlocked;
	}

	public void setIsBlocked(int isBlocked) {
		this.isBlocked = isBlocked;
	}
	
	public String getApiURL() {
		return apiURL;
	}

	public void setApiURL(String apiURL) {
		this.apiURL = apiURL;
	}

	public long getID() {
		return ID;
	}

	public void setID(long iD) {
		ID = iD;
	}

	public String getServerName() {
		return serverName;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	public String getServerIP() {
		return serverIP;
	}

	public void setServerIP(String serverIP) {
		this.serverIP = serverIP;
	}

	public int getoSType() {
		return oSType;
	}

	public void setoSType(int oSType) {
		this.oSType = oSType;
	}

	public int getMaxAllowed() {
		return maxAllowed;
	}

	public void setMaxAllowed(int maxAllowed) {
		this.maxAllowed = maxAllowed;
	}

	public int getAlarmThreshold() {
		return alarmThreshold;
	}

	public void setAlarmThreshold(int alarmThreshold) {
		this.alarmThreshold = alarmThreshold;
	}

	public int getNotification() {
		return notification;
	}

	public void setNotification(int notification) {
		this.notification = notification;
	}

	public long getLastModificationTime() {
		return lastModificationTime;
	}

	public void setLastModificationTime(long lastModificationTime) {
		this.lastModificationTime = lastModificationTime;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
