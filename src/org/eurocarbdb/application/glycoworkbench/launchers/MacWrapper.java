package org.eurocarbdb.application.glycoworkbench.launchers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.eurocarbdb.application.glycanbuilder.LogUtils;

public class MacWrapper {
	private String bitVersion;
	public MacWrapper(String _bitVersion){
		bitVersion=_bitVersion;
	}
	
	public void run(){
		LogUtils.setGraphicalReport(true);
		try {
			final Process process=Runtime.getRuntime().exec("java -d"+bitVersion+"  -Xmx200M -jar eurocarb-glycoworkbench-1.0rc.jar");
			
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
}
