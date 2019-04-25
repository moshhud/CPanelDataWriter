package util;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class SSLCertificate {
	public SSLCertificate() {
		
	}
	
	public void setSSL(){
		
	       TrustManager[] trustAllCerts = { new X509TrustManager() {
	         public X509Certificate[] getAcceptedIssuers() {
	            return null;
	         }
	         public void checkClientTrusted(X509Certificate[] certs, String authType)
	         {
	         }
	         public void checkServerTrusted(X509Certificate[] certs, String authType)
	         {
	         }
	       }
	       };
	     
	     SSLContext sc = null;
	     try {
	       sc = SSLContext.getInstance("SSL");
	     }
	     catch (NoSuchAlgorithmException e) {
	      e.printStackTrace();
	     }
	     try {
	       sc.init(null, trustAllCerts, new SecureRandom());
	     }
	     catch (KeyManagementException e) {
	      e.printStackTrace();
	     }
	     HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

	     HostnameVerifier allHostsValid = new HostnameVerifier() {
	       public boolean verify(String hostname, SSLSession session) {
	        return true;
	       }
	     };
	     HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
	     
			
	   }

}
