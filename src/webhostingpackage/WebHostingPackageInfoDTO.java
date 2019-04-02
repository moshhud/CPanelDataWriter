package webhostingpackage;

import java.util.LinkedHashMap;

public class WebHostingPackageInfoDTO{
	 
	long ID;	
	String packageName;
	double price;
	int isDisplayed;
	int isDeleted;
	long activationDate;
	long lastModificationTime;
	int packageType;
	int cpanelWrittingStatus;
	LinkedHashMap<Long, WebHostingPackageResourceDTO> packResourceDTOMap;	
	
	public int getCpanelWrittingStatus() {
		return cpanelWrittingStatus;
	}
	public void setCpanelWrittingStatus(int cpanelWrittingStatus) {
		this.cpanelWrittingStatus = cpanelWrittingStatus;
	}
	
	public long getID() {
		return ID;
	}
	public void setID(long iD) {
		ID = iD;
	}
	public String getPackageName() {
		return packageName;
	}
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	public int getIsDisplayed() {
		return isDisplayed;
	}
	public void setIsDisplayed(int isDisplayed) {
		this.isDisplayed = isDisplayed;
	}
	public int getIsDeleted() {
		return isDeleted;
	}
	public void setIsDeleted(int isDeleted) {
		this.isDeleted = isDeleted;
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
	
	public LinkedHashMap<Long, WebHostingPackageResourceDTO> getPackResourceDTOMap() {
		return packResourceDTOMap;
	}
	public void setPackResourceDTOMap(LinkedHashMap<Long, WebHostingPackageResourceDTO> packResourceDTOMap) {
		this.packResourceDTOMap = packResourceDTOMap;
	}
	public int getPackageType() {
		return packageType;
	}
	public void setPackageType(int packageType) {
		this.packageType = packageType;
	}
	

}
