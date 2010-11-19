package org.eurocarbdb.application.glycoworkbench.launchers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import org.apache.tools.ant.launch.Locator;
import org.eurocarbdb.application.glycanbuilder.LogUtils;
import org.eurocarbdb.application.glycoworkbench.ResourceUtilities;

public class StartupWrapper {
	public StartupWrapper(){

	}
	
	public static String getProperty(Properties prop,String key){
		return prop.getProperty(key);
	}

	public String getStartupCommand() throws Exception{
		File startupConfigurationFile=ResourceUtilities.getConfigurationFile(".glycoworkbench.launch");
		
		if(!startupConfigurationFile.exists()){
			try{
				URL url=ResourceUtilities.getResource("/startup_options.properties");
				BufferedReader reader=new BufferedReader(new InputStreamReader(url.openStream()));

				FileWriter writer=new FileWriter(startupConfigurationFile);

				String line=null;
				while((line=reader.readLine())!=null){
					writer.write(line+"\n");
				}

				writer.flush();
				writer.close();
			}catch(Exception ex){
				throw ex;
			}
		}
		
		System.err.println(startupConfigurationFile.toString());
		
		
		Properties prop=new Properties();
		
		
		prop.load(new FileReader(startupConfigurationFile));
		
		String os=StartupWrapper.getProperty(prop,"os");
		String javaCommand=StartupWrapper.getProperty(prop, "java");
		String javaMaxMem=StartupWrapper.getProperty(prop, "Xmx");
		String javaArch=StartupWrapper.getProperty(prop, "Arch");
		String javaJar=StartupWrapper.getProperty(prop, "jar");
		
		javaJar=ResourceUtilities.getParentDirectory(ResourceUtilities.class)+javaJar;
		
		System.err.println(javaJar.toString());
		//System.exit(0);
		
		if(os.equals("windows") || os.equals("linux")){
			return javaCommand+" -Xmx"+javaMaxMem+" -jar "+javaJar;
		}else if(os.contains("macosx")){
			return javaCommand+" -d"+javaArch+" -XstartOnFirstThread -Xmx"+javaMaxMem+" -jar "+javaJar;
		}else{
			throw new Exception("Startup properties file (startup_options.properties) contains an invalid value for property os");
		}
	}
	
	public void run(){
		LogUtils.setGraphicalReport(true);
		try {
			final Process process=Runtime.getRuntime().exec(getStartupCommand());
			
			Thread thread=new Thread(){
				public void run(){
					InputStream stream=process.getErrorStream();
					BufferedReader bStream=new BufferedReader(new InputStreamReader(stream));
					String line="";
					StringBuffer stringBuffer=new StringBuffer();
					try {
						while((line=bStream.readLine())!=null){
							stringBuffer.append(line);
						}
					} catch (IOException e) {
						LogUtils.report(e);
					}
					String output=stringBuffer.toString();
					if(!output.contains("NORMAL STARTUP COMPLETE")){
						//Above doesn't always get printed, so catch this instead
						if(!output.contains("NSConditionLock")){
							LogUtils.report(new Exception(stringBuffer.toString()));
						}
					}
				}
			};
			thread.run();
			process.waitFor();
			thread.join();
			System.exit(0);
		} catch (Exception e) {
			LogUtils.report(e);
		}
	}
	
	public static void main(String args[]){
		StartupWrapper wrapper=new StartupWrapper();
		wrapper.run();
	}
}