package org.eurocarbdb.application.glycoworkbench.updater;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Updater {
	String updateSite;
	public static String LATEST_VERSION_INFO_FILE="latest_version";
	
	public static int MAJOR_VERSION;
	public static int MINOR_VERSION;
	public static String BUILD_STATE;
	public static int BUILD_NUMBER;
	
	public Updater(String _updateSite) throws Exception{
		updateSite=_updateSite;
		
		if(!checkSiteExists(new URL(updateSite))){
				throw new Exception("Update couldn't be conntacted");
		}
		
		getLatestVersionInfo();
	}
	
	public boolean isUptoDate(Updatable updatable){
		int majorVersion=Integer.parseInt(updatable.getMajorVersion());
		int minorVersion=Integer.parseInt(updatable.getMinorVersion());
		int buildNo=Integer.parseInt(updatable.getBuildNo());
		
		if(majorVersion < MAJOR_VERSION || 
				(majorVersion==MAJOR_VERSION && minorVersion < MINOR_VERSION) ||
				(majorVersion==MAJOR_VERSION && minorVersion== MINOR_VERSION && buildNo < BUILD_NUMBER)
			){
			return false;
		}else{
			return true;
		}
	}
	
	public void getLatestVersionInfo(){
		try {
			URL url = new URL(updateSite+"/"+LATEST_VERSION_INFO_FILE);
		    InputStream is = url.openStream();
		    DataInputStream dis = new DataInputStream(new BufferedInputStream(is));
		    String line;
		    
		    while ((line = dis.readLine()) != null) {
		        String cols[]=line.split(" ");
		        MAJOR_VERSION=Integer.parseInt(cols[0]);
		        MINOR_VERSION=Integer.parseInt(cols[1]);
		        BUILD_STATE=cols[2];
		        BUILD_NUMBER=Integer.parseInt(cols[3]);
		    }
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public boolean checkSiteExists(URL url) {
		try {
			HttpURLConnection.setFollowRedirects(false);

			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("HEAD");
			return (con.getResponseCode() == HttpURLConnection.HTTP_OK);
		} catch (Exception e) {
			//e.printStackTrace();
			return false;
		}
	}
}
