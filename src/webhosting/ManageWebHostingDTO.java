package webhosting;


public class ManageWebHostingDTO{

	private static final long serialVersionUID = 1L;
	long ID;
	long clientID;
	int clientType;
	String domain;
	String email;
	String userName;
	String userPass;
	long packageID;
	long upgradePackageID;
	long expiryDate;
	long activationDate;
	long lastModificationTime;
	int isBlocked;
	int isPrivileged;
	int currentStatus;
	int latestStatus;
	long serverID;
	boolean isDeleted;	
	int slot;
	int cpanelWrittingStatus;
	String packageName;
				
	public String getPackageName() {
		return packageName;
	}
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	public int getCpanelWrittingStatus() {
		return cpanelWrittingStatus;
	}
	public void setCpanelWrittingStatus(int cpanelWrittingStatus) {
		this.cpanelWrittingStatus = cpanelWrittingStatus;
	}
	public boolean getIsDeleted() {
		return isDeleted;
	}
	public void setIsDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}
	public int getSlot() {
		return slot;
	}
	public void setSlot(int slot) {
		this.slot = slot;
	}
	public long getID() {
		return ID;
	}
	public void setID(long iD) {
		ID = iD;
	}
	public long getClientID() {
		return clientID;
	}
	public void setClientID(long clientID) {
		this.clientID = clientID;
	}
	public int getClientType() {
		return clientType;
	}
	public void setClientType(int clientType) {
		this.clientType = clientType;
	}
	public String getDomain() {
		return domain;
	}
	public void setDomain(String domain) {
		this.domain = domain;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getUserPass() {
		return userPass;
	}
	public void setUserPass(String userPass) {
		this.userPass = userPass;
	}
	public long getPackageID() {
		return packageID;
	}
	public void setPackageID(long packageID) {
		this.packageID = packageID;
	}
	public long getUpgradePackageID() {
		return upgradePackageID;
	}
	public void setUpgradePackageID(long upgradePackageID) {
		this.upgradePackageID = upgradePackageID;
	}
	public long getExpiryDate() {
		return expiryDate;
	}
	public void setExpiryDate(long expiryDate) {
		this.expiryDate = expiryDate;
	}
	public long getActivationDate() {
		return activationDate;
	}
	public void setActivationDate(long activationDate) {
		this.activationDate = activationDate;
	}
	public long getLastModificationTime() {
		return lastModificationTime;
	}
	public void setLastModificationTime(long lastModificationTime) {
		this.lastModificationTime = lastModificationTime;
	}
	public int getIsBlocked() {
		return isBlocked;
	}
	public void setIsBlocked(int isBlocked) {
		this.isBlocked = isBlocked;
	}
	public int getIsPrivileged() {
		return isPrivileged;
	}
	public void setIsPrivileged(int isPrivileged) {
		this.isPrivileged = isPrivileged;
	}
	
	public long getServerID() {
		return serverID;
	}
	public void setServerID(long serverID) {
		this.serverID = serverID;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}	
	
}
