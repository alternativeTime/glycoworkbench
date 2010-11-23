package org.eurocarbdb.application.glycoworkbench;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.tools.ant.launch.Locator;

public class ResourceUtilities {
	public static URL getResource(String resource) throws MalformedURLException{
		URL url;
		if (ResourceUtilities.class.getResource(resource) == null) {
			String urlString = Locator.getClassSource(ResourceUtilities.class).getParent()
					+ resource;

			if (urlString.contains("!")) {
				urlString = "jar:" + urlString.replaceAll("\\\\", "/");
			} else {
				urlString = "file:" + urlString;
			}

			url = new URL(urlString);
		} else {
			url = ResourceUtilities.class.getResource(resource);
		}

		return url;
	}
	
	public static String getParentDirectory(Class clazz){
		return Locator.getClassSource(clazz).getParent();
	}
	
	public static File getConfigurationFile(String configFileName,String subDirectory) throws IOException{
		String userHomeDirectory = System.getProperty("user.home");
		String osName = System.getProperty("os.name");

		File configurationFile;
		if (osName.equals("Linux")) {
			configurationFile = new File(userHomeDirectory
					+ "/"+configFileName);
		} else if (osName.startsWith("Windows")) {
			String applicationDataDirectory=System.getenv("APPDATA");
			if(applicationDataDirectory==null){
				applicationDataDirectory=userHomeDirectory+File.separator+"Application Data";
			}
			
			File glycoworkBenchProfilesDirectory = new File(applicationDataDirectory+File.separator+subDirectory);
			
			if (!glycoworkBenchProfilesDirectory.exists()) {
				if (!glycoworkBenchProfilesDirectory.mkdir()) {
					throw new IOException("Could not create directory: "
							+ glycoworkBenchProfilesDirectory.toString());
				}
			}
			
			configurationFile = new File(glycoworkBenchProfilesDirectory+File.separator+configFileName);
		} else {
			configurationFile = new File(userHomeDirectory
					+ configFileName);
		}
		System.out.println("Using startup configuration file: "+ configurationFile.toString());
		
		return configurationFile;
	}
}
