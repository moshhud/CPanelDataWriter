package webhostingpackage;

public class WebHostingPackageResourceDTO{

	
	long ID;
	long packageID;
	long lastModificationTime;
	int diskQuota;
	int monthlyBW;
	int maxFTPAccount;
	int maxEmailAccount;
	int maxEmailList;
	int maxDatabases;
	int maxSubDomain;
	int maxParkedDomain;
	int maxAddonDomain;
	int maxHourlyEmail;
	int maxPerFailureMsg;
		
	public long getID() {
		return ID;
	}

	public void setID(long iD) {
		ID = iD;
	}

	public long getPackageID() {
		return packageID;
	}

	public void setPackageID(long packageID) {
		this.packageID = packageID;
	}

	public long getLastModificationTime() {
		return lastModificationTime;
	}

	public void setLastModificationTime(long lastModificationTime) {
		this.lastModificationTime = lastModificationTime;
	}

	public int getDiskQuota() {
		return diskQuota;
	}

	public void setDiskQuota(int diskQuota) {
		this.diskQuota = diskQuota;
	}

	public int getMonthlyBW() {
		return monthlyBW;
	}

	public void setMonthlyBW(int monthlyBW) {
		this.monthlyBW = monthlyBW;
	}

	public int getMaxFTPAccount() {
		return maxFTPAccount;
	}

	public void setMaxFTPAccount(int maxFTPAccount) {
		this.maxFTPAccount = maxFTPAccount;
	}

	public int getMaxEmailAccount() {
		return maxEmailAccount;
	}

	public void setMaxEmailAccount(int maxEmailAccount) {
		this.maxEmailAccount = maxEmailAccount;
	}

	public int getMaxEmailList() {
		return maxEmailList;
	}

	public void setMaxEmailList(int maxEmailList) {
		this.maxEmailList = maxEmailList;
	}

	public int getMaxDatabases() {
		return maxDatabases;
	}

	public void setMaxDatabases(int maxDatabases) {
		this.maxDatabases = maxDatabases;
	}

	public int getMaxSubDomain() {
		return maxSubDomain;
	}

	public void setMaxSubDomain(int maxSubDomain) {
		this.maxSubDomain = maxSubDomain;
	}

	public int getMaxParkedDomain() {
		return maxParkedDomain;
	}

	public void setMaxParkedDomain(int maxParkedDomain) {
		this.maxParkedDomain = maxParkedDomain;
	}

	public int getMaxAddonDomain() {
		return maxAddonDomain;
	}

	public void setMaxAddonDomain(int maxAddonDomain) {
		this.maxAddonDomain = maxAddonDomain;
	}

	public int getMaxHourlyEmail() {
		return maxHourlyEmail;
	}

	public void setMaxHourlyEmail(int maxHourlyEmail) {
		this.maxHourlyEmail = maxHourlyEmail;
	}

	public int getMaxPerFailureMsg() {
		return maxPerFailureMsg;
	}

	public void setMaxPerFailureMsg(int maxPerFailureMsg) {
		this.maxPerFailureMsg = maxPerFailureMsg;
	}

	

}
