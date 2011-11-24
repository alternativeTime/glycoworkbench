package org.eurocarbdb.application.glycoworkbench.analytics;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

import org.apache.commons.lang.StringUtils;
import org.eurocarbdb.util.MutableInteger;

import edu.emory.mathcs.backport.java.util.Collections;

public class ApacheLogAnalyser {
	private static Logger logger=Logger.getLogger(ApacheLogAnalyser.class.toString());
	
	private Pattern compressedExtensionPattern=Pattern.compile("\\.gz$", Pattern.CASE_INSENSITIVE);
	private Pattern errorLogPattern=Pattern.compile("error\\.log(\\.[0-9]+)?$",Pattern.CASE_INSENSITIVE);
	private Pattern accessLogPattern=Pattern.compile("access\\.log(\\.[0-9]+)?$",Pattern.CASE_INSENSITIVE);
	private Pattern hostLinePattern=Pattern.compile("^(.+)[ ]+-[ ]+-", Pattern.CASE_INSENSITIVE);
	private Pattern dateLinePattern=Pattern.compile("^.+[ ]+-[ ]+-[ ]+\\[([^ ]+)", Pattern.CASE_INSENSITIVE);
	private Pattern errorLogHostPattern=Pattern.compile("\\[error\\][ ]+\\[client[ ]+([^\\]]+)\\]", Pattern.CASE_INSENSITIVE);
	private Pattern ipOnly=Pattern.compile("^\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}$",Pattern.CASE_INSENSITIVE);
	private Pattern adslPattern=Pattern.compile("adsl",Pattern.CASE_INSENSITIVE);
	private Pattern extractTopLevelPattern=Pattern.compile("([^\\.]+)$");
	
	
	private File logDirectory;

	private List<File> logList;
	private List<File> accessLogList;
	private List<File> errorLogList;
	
	private List<String> exclusionList;

	private boolean enableBotExcluder=false;
	private boolean excludeIpAddresses=false;
	private boolean excludeHostsContainingAdsl=false;

	private TLDLibrary tldLibrary;

	private HostNameConstraint hostNameConstraint;
	
	public ApacheLogAnalyser(File logDirectory){
		this.logDirectory=logDirectory;
	}
	
	public void printLogListToStdout() throws UnableToDecompressLogFileException{
		List<File> logList=getLogList();
		printLogListToStdout(logList);
	}
	
	public void printErrorLogListToStdout() throws UnableToDecompressLogFileException{
		List<File> logList=getErrorLogList();
		printLogListToStdout(logList);
	}
	
	public void printAccessLogListToStdout() throws UnableToDecompressLogFileException{
		List<File> logList=getAccessLogList();
		printLogListToStdout(logList);
	}
	
	protected void printLogListToStdout(List<File> logList){
		for(File file:logList){
			System.out.println("Log file: "+file);
		}
	}
	
	public void botExcluder(boolean enable){
		enableBotExcluder=enable;
	}
	
	public List<File> getErrorLogList() throws UnableToDecompressLogFileException{
		if(errorLogList==null){
			errorLogList=getLogList();
			for(File file:new ArrayList<File>(errorLogList)){
				if(errorLogPattern.matcher(file.getPath()).find()==false){
					errorLogList.remove(file);
				}
			}
		}
		
		return new ArrayList<File>(errorLogList);
	}
	
	public List<File> getAccessLogList() throws UnableToDecompressLogFileException{
		if(accessLogList==null){
			accessLogList=getLogList();
			for(File file:new ArrayList<File>(accessLogList)){
				if(accessLogPattern.matcher(file.getPath()).find()==false){
					accessLogList.remove(file);
				}
			}
		}
		
		return new ArrayList<File>(accessLogList);
	}
	
	public List<File> getLogList() throws UnableToDecompressLogFileException {
		if(logList==null){
			logList=new ArrayList<>();
			for(String file:logDirectory.list()){
				file=logDirectory.getPath()+"/"+file;
				if(compressedExtensionPattern.matcher(file).find()){
					logList.add(decompressFile(new File(file)));
				}else{
					logList.add(new File(file));
				}
			}
		}
		
		return new ArrayList<File>(logList);
	}
	
	public void excludeClientsInErrorLog(int minExclusion) throws UnableToDecompressLogFileException, UnableToReadErrorLogFileException{
		Map<String, MutableInteger> clientList=getClientsInErrorLogs();
		
		for(String client:new ArrayList<String>(clientList.keySet())){
			if(clientList.get(client).mutI<minExclusion){
				clientList.remove(client);
			}
		}
		
		exclusionList=new ArrayList<String>(clientList.keySet());
	}
	
	public void clear(){
		logList=null;
		accessLogList=null;
		errorLogList=null;
	}
	
	public File decompressFile(File compressedFile) throws UnableToDecompressLogFileException{
		String path=compressedFile.getPath();

		File decompressedFile=new File(path.replaceAll("\\.gz$", ""));
		decompressedFile.deleteOnExit();
		
		try(
			GZIPInputStream gis=new GZIPInputStream(new FileInputStream(compressedFile));
			FileOutputStream writer=new FileOutputStream(decompressedFile);
		){
			byte buf[]=new byte[1000];
			int read=0;
			while((read=gis.read(buf, 0, 1000))!=-1){
				writer.write(buf, 0, read);
			}

			writer.flush();
			writer.close();

			return decompressedFile;
		} catch (IOException e) {
			throw new UnableToDecompressLogFileException(e);
		}
	}
	
	public URIResults retrieveGetStastitics(Collection<String> trackUris) throws UnableToDecompressLogFileException,UnableToReadLogFileException{
		URIResults results=new URIResults();
		
		Map<String, Pattern> uriToPattern=getURIPatternMap(trackUris);
		for(File logFile:getAccessLogList()){
			retrieveGetStastitics(uriToPattern, logFile, results);
		}
		
		return results;
	}
	
	public URIResults retrieveGetStastitics(Collection<String> trackUris, File logFile) throws UnableToReadLogFileException{
		return retrieveGetStastitics(getURIPatternMap(trackUris), logFile, new URIResults());
	}
	
	public URIResults retrieveGetStastitics(Map<String, Pattern> uriToPattern, File logFile, URIResults results) throws UnableToReadLogFileException {
		try(
				BufferedReader reader=new BufferedReader(new FileReader(logFile));
		){
			String line;
			
			while((line=reader.readLine())!=null){
				for(Entry<String, Pattern> entry:uriToPattern.entrySet()){
					if(entry.getValue().matcher(line).find()){
						String host=getHostFromLine(line);
						String uri=entry.getKey();
						String date=getDateFromLine(line);
						
						if( (exclusionList==null || exclusionList.contains(host)==false) 
								&& (enableBotExcluder==false || notABot(host))
									&& (excludeIpAddresses==false || ipOnly.matcher(host).matches()==false)
										&& (excludeHostsContainingAdsl==false || adslPattern.matcher(host).find()==false)
										  && (hostNameConstraint==null || hostNameConstraint.isAllowed(host))
						){
							results.addEntry(uri, host, date);
						}else{
							System.out.println("Excluding: "+host);
						}
					}
				}
			}
			
			return results;
		} catch (IOException e) {
			throw new UnableToReadLogFileException(e);
		}
	}
	
	private boolean notABot(String host) {
		if(host.contains("bot") || host.contains("spider")){
			return false;
		}else{
			return true;
		}
	}

	private String getHostFromLine(String line){
		Matcher matcher=hostLinePattern.matcher(line);
		if(matcher.find()){
			return matcher.group(1);
		}else{
			return null;
		}
	}
	
	private String getDateFromLine(String line){
		Matcher matcher=dateLinePattern.matcher(line);
		if(matcher.find()){
			return matcher.group(1);
		}else{
			return null;
		}
	}
	
	public Map<String, Pattern> getURIPatternMap(Collection<String> trackUris){
		Map<String, Pattern> map=new HashMap<>();
		for(String uri:trackUris){
			map.put(uri, Pattern.compile("GET[ ]+"+Pattern.quote(uri), Pattern.CASE_INSENSITIVE));
		}
		
		return map;
	}
	
	public void excludeIpAddresses(boolean exclude){
		excludeIpAddresses=exclude;
	}
	
	public class URIResults {
		private Map<String,Map<String,MutableInteger>> uriToHostToCount=new HashMap<>();
		private Map<String, List<String>> uriToDownloadDates=new HashMap<>();
		private Map<String, MutableInteger> uriToDownloadCount=new HashMap<>();
		
		private HashSet<String> uniqueHosts=new HashSet<String>();
		
		private Date earlestDate=new Date(System.currentTimeMillis()*1000);
		
		public void addEntry(String uri, String host, String date){
			if(uriToDownloadCount.containsKey(uri)==false){
				uriToDownloadCount.put(uri, new MutableInteger());
			}
			
			if(uriToHostToCount.containsKey(uri)==false){
				uriToHostToCount.put(uri, new HashMap<String,MutableInteger>());
			}
			
			if(uriToHostToCount.get(uri).containsKey(host)==false){
				uriToHostToCount.get(uri).put(host, new MutableInteger());
				
				uriToDownloadCount.get(uri).mutI++;
			}
			
			uriToHostToCount.get(uri).get(host).mutI++;
			
			if(uriToDownloadDates.containsKey(uri)==false){
				uriToDownloadDates.put(uri, new ArrayList<String>());
			}
			
			uriToDownloadDates.get(uri).add(date);

			uniqueHosts.add(host);
		}
		
		public void printURIToDownloadCountToStdout(){
			for(Entry<String, MutableInteger> entry:uriToDownloadCount.entrySet()){
				System.out.println("uri="+entry.getKey()+" downloaded="+entry.getValue().mutI);
			}
		}
		
		public void printUniqueHostCount(){
			System.out.println("Unique hosts="+uniqueHosts.size());
		}
		
		public void printHostsMatching(Pattern pattern){
			List<String> hostList=new ArrayList<>(uniqueHosts);
			Collections.sort(hostList);
			
			for(String host:hostList){
				if(pattern.matcher(host).find()){
					System.out.println(host);
				}
			}
		}
		
		public void printHosts(){
			List<String> hostList=new ArrayList<>(uniqueHosts);
			Collections.sort(hostList);
			
			for(String host:hostList){
				System.out.println(host);
			}
		}
		
		private Pattern generateDomainPattern(int depth){
			String patternParts[]=new String[depth];
			for(int i=0;i<depth;i++){
				patternParts[i]="[^\\.]+";
			}
			
			String patternStr="("+StringUtils.join(patternParts, "\\.")+")$";
			
			Pattern domainPattern=Pattern.compile(patternStr);
			
			return domainPattern;
		}
		
		public List<String> extractDomainNames(){
			Pattern singlePattern=generateDomainPattern(2);
			Pattern doublePattern=generateDomainPattern(3);
			
			HashSet<String> domainNames=new HashSet<>();
			for(String host:uniqueHosts){
				String tld=extractTopLevel(host);
				
				Pattern patternToUse;
				if(tldLibrary.isGenericTLD(tld)){
					patternToUse=singlePattern;
				}else if(tldLibrary.isCountryTLD(tld)){
					patternToUse=doublePattern;
				}else{
					System.out.println("Skipping: "+host);
					continue;
				}
				
				Matcher matcher=patternToUse.matcher(host);
				if(matcher.find()){
					String domainName=matcher.group(1);
					
					if(patternToUse==doublePattern && domainName.length()==host.length()){
						matcher=singlePattern.matcher(host);
						if(matcher.find()){
							domainName=matcher.group(1);
						}
					}

					domainNames.add(domainName);					
				}else{
					System.out.println("Skipping: "+host);
				}
			}
			
			return new ArrayList<String>(domainNames);
		}
		
		public void printUniqueDomainNameCount(){
			List<String> domainNames=extractDomainNames();
			System.out.println("Unique domains: "+domainNames.size());
		}
		
		public void printDomainNames(){
			List<String> domainNames=extractDomainNames();
			Collections.sort(domainNames);
			for(String domainName:domainNames){
				System.out.println(domainName);
			}
		}
	}
	
	public Map<String,MutableInteger> getClientsInErrorLogs() throws UnableToDecompressLogFileException, UnableToReadErrorLogFileException{
		Map<String,MutableInteger> clientList=new HashMap<>();
		
		for(File errorLog:getErrorLogList()){
			getClientsInErrorLogs(errorLog, clientList);
		}
		
		return clientList;
	}
	
	public void getClientsInErrorLogs(File errorLog, Map<String,MutableInteger> clientList) throws UnableToReadErrorLogFileException{
		try(
				BufferedReader reader=new BufferedReader(new FileReader(errorLog));
		){
			String line;
			while((line=reader.readLine())!=null){
				Matcher matcher=errorLogHostPattern.matcher(line);
				if(matcher.find()){
					String client=matcher.group(1);
					
					if(clientList.containsKey(client)==false){
						clientList.put(client, new MutableInteger());
					}
					
					clientList.get(client).mutI++;
				}
			}
		}catch(IOException e){
			throw new UnableToReadErrorLogFileException(e);
		}
	}
	
	public void excludeHostsContainingAdsl(boolean exclude){
		excludeHostsContainingAdsl=exclude;
	}
	
	public TLDLibrary loadTldLibrary(File tldLibraryFile) throws UnableToReadTLDLibraryFileException{
		tldLibrary=new TLDLibrary(tldLibraryFile);
		
		return tldLibrary;
	}
	
	public class TLDLibrary{
		private File tldLibraryFile;
		private Map<String, String> tldToCountry=new HashMap<>();
		
		private Map<String,HashSet<String>> typeToTldSet=new HashMap<>();
		
		private Pattern tldLinePattern=Pattern.compile("^\\.([^,]+),([^,]+)$");
		private Pattern classLinePattern=Pattern.compile("^>(.+)");
		
		public static final String GENERAL_TLD_TYPE="general_tld";
		public static final String CC_TLD_TYPE="country_tld"; 
		
		public TLDLibrary(File tldLibraryFile) throws UnableToReadTLDLibraryFileException{
			setLibraryFile(tldLibraryFile);
		}
		
		public void setLibraryFile(File libraryFile) throws UnableToReadTLDLibraryFileException{
			this.tldLibraryFile=libraryFile;
			
			loadTldLibraryFile();
		}
		
		protected void loadTldLibraryFile() throws UnableToReadTLDLibraryFileException{
			tldToCountry.clear();
			typeToTldSet.clear();
			
			try(
					BufferedReader reader=new BufferedReader(new FileReader(tldLibraryFile));
			){
				String tldClass=null;
				String line;
				while((line=reader.readLine())!=null){
					
					if(line.startsWith("#")==false){
						Matcher matcher=tldLinePattern.matcher(line);
						if(matcher.matches()){
							tldToCountry.put(matcher.group(1), matcher.group(2));
							if(tldClass!=null){
								typeToTldSet.get(tldClass).add(matcher.group(1));
							}
						}else{
							matcher=classLinePattern.matcher(line);
							if(matcher.find()){
								tldClass=matcher.group(1);
								typeToTldSet.put(tldClass, new HashSet<String>());
							}
						}
					}
				}
			}catch(IOException ex){
				throw new UnableToReadTLDLibraryFileException(ex);
			}
		}
		
		protected void printTldLibrary(){
			for(Entry<String,String> entry:tldToCountry.entrySet()){
				System.out.println("cc="+entry.getKey()+" country="+entry.getValue());
			}
		}
		
		protected boolean isGenericTLD(String tld){
			return typeToTldSet.get(GENERAL_TLD_TYPE).contains(tld);
		}
		
		protected boolean isCountryTLD(String tld){
			return typeToTldSet.get(CC_TLD_TYPE).contains(tld);
		}
	}
	
	protected String extractTopLevel(String host){
		Matcher matcher=extractTopLevelPattern.matcher(host);
		if(matcher.find()){
			return matcher.group(1);
		}else{
			return null;
		}
	}
	
	public void loadHostNameConstraintFile(File constraintFile) throws UnableToReadHostNameConstraintFileException{
		hostNameConstraint=new HostNameConstraint(constraintFile);
	}
	
	public class HostNameConstraint{
		private File constraintFile;
		
		private Map<String,String> domainNameToName=new HashMap<String,String>();
		private List<Pattern> domainNamePatterns=new ArrayList<>();
		
		public HostNameConstraint(File constraintFile) throws UnableToReadHostNameConstraintFileException{
			setConstraintFile(constraintFile);
		}
		
		public void setConstraintFile(File constraintFile) throws UnableToReadHostNameConstraintFileException{
			this.constraintFile=constraintFile;
			
			loadConstraintFile();
		}
		
		private void loadConstraintFile() throws UnableToReadHostNameConstraintFileException{
			try(
					BufferedReader reader=new BufferedReader(new FileReader(this.constraintFile));
			){
				reader.readLine();//skip header
				
				String line;
				while((line=reader.readLine())!=null){
					String cols[]=line.split(",");
					
					String domainName=cols[0];
					String name=cols[1];
					
					domainNameToName.put(domainName, name);
					
					domainNamePatterns.add(Pattern.compile(Pattern.quote(domainName)+"$", Pattern.CASE_INSENSITIVE));
				}
			}catch(IOException ex){
				throw new UnableToReadHostNameConstraintFileException();
			}
		}
		
		public boolean isAllowed(String hostname){
			for(Pattern pattern:domainNamePatterns){
				if(pattern.matcher(hostname).find()){
					return true;
				}
			}
			
			return false;
		}
	}
	
	public static void defaultRun() throws UnableToDecompressLogFileException, UnableToReadLogFileException, UnableToReadErrorLogFileException, UnableToReadTLDLibraryFileException, URISyntaxException{
		ApacheLogAnalyser analyser=new ApacheLogAnalyser(new File("/users/david/gwb_logs/"));
		
		analyser.excludeClientsInErrorLog(4);
		analyser.botExcluder(true);
		analyser.excludeIpAddresses(true);
		analyser.excludeHostsContainingAdsl(true);
		
		analyser.loadTldLibrary(new File(ApacheLogAnalyser.class.getResource("tlds.csv").toURI())).printTldLibrary();

		List<String> trackUris=new ArrayList<>();
		trackUris.add("/current_version/windows/GlycoWorkbenchWin_x86-64.exe");
		trackUris.add("/current_version/windows/GlycoWorkbenchWin_x86.exe"); 
		trackUris.add("/current_version/windows/GlycoWorkbenchWin_x86-64.zip");
		trackUris.add("/current_version/windows/GlycoWorkbenchWin_x86.zip");
		
		trackUris.add("/current_version/linux/GlycoWorkbenchLin_x86.zip");
		trackUris.add("/current_version/linux/GlycoWorkbenchLin_x86-64.zip");
		
		trackUris.add("/current_version/macosx/GlycoWorkbenchMac_Carbon_32.zip");
		trackUris.add("/current_version/macosx/GlycoWorkbenchMac_Cocoa_32.zip");
		trackUris.add("/current_version/macosx/GlycoWorkbenchMac_Cocoa_x86-64.zip");
		
		URIResults results=analyser.retrieveGetStastitics(trackUris);
		results.printURIToDownloadCountToStdout();
		results.printUniqueHostCount();
		
		results.printHosts();
		
		results.printDomainNames();
	}
	
	public static void defaultAllBarBots() throws UnableToDecompressLogFileException, UnableToReadLogFileException, UnableToReadErrorLogFileException, UnableToReadTLDLibraryFileException, URISyntaxException{
		ApacheLogAnalyser analyser=new ApacheLogAnalyser(new File("/users/david/gwb_logs/"));
		
		analyser.excludeClientsInErrorLog(4);
		analyser.botExcluder(true);
		//analyser.excludeIpAddresses(true);
		//analyser.excludeHostsContainingAdsl(true);
		
		analyser.loadTldLibrary(new File(ApacheLogAnalyser.class.getResource("tlds.csv").toURI())).printTldLibrary();

		List<String> trackUris=new ArrayList<>();
		trackUris.add("/current_version/windows/GlycoWorkbenchWin_x86-64.exe");
		trackUris.add("/current_version/windows/GlycoWorkbenchWin_x86.exe"); 
		trackUris.add("/current_version/windows/GlycoWorkbenchWin_x86-64.zip");
		trackUris.add("/current_version/windows/GlycoWorkbenchWin_x86.zip");
		
		trackUris.add("/current_version/linux/GlycoWorkbenchLin_x86.zip");
		trackUris.add("/current_version/linux/GlycoWorkbenchLin_x86-64.zip");
		
		trackUris.add("/current_version/macosx/GlycoWorkbenchMac_Carbon_32.zip");
		trackUris.add("/current_version/macosx/GlycoWorkbenchMac_Cocoa_32.zip");
		trackUris.add("/current_version/macosx/GlycoWorkbenchMac_Cocoa_x86-64.zip");
		
		URIResults results=analyser.retrieveGetStastitics(trackUris);
		results.printURIToDownloadCountToStdout();
		results.printUniqueHostCount();
		results.printUniqueDomainNameCount();
	}
	
	public static void defaultConstrainedRun() throws UnableToDecompressLogFileException, UnableToReadLogFileException, UnableToReadErrorLogFileException, UnableToReadTLDLibraryFileException, URISyntaxException, UnableToReadHostNameConstraintFileException{
		ApacheLogAnalyser analyser=new ApacheLogAnalyser(new File("/users/david/gwb_logs/"));
		
		analyser.excludeClientsInErrorLog(4);
		analyser.botExcluder(true);
		analyser.excludeIpAddresses(true);
		analyser.excludeHostsContainingAdsl(true);
		
		analyser.loadTldLibrary(new File(ApacheLogAnalyser.class.getResource("tlds.csv").toURI())).printTldLibrary();

		analyser.loadHostNameConstraintFile(new File("/users/david/gwb_host_constrain_list.txt"));
		
		List<String> trackUris=new ArrayList<>();
		trackUris.add("/current_version/windows/GlycoWorkbenchWin_x86-64.exe");
		trackUris.add("/current_version/windows/GlycoWorkbenchWin_x86.exe"); 
		trackUris.add("/current_version/windows/GlycoWorkbenchWin_x86-64.zip");
		trackUris.add("/current_version/windows/GlycoWorkbenchWin_x86.zip");
		
		trackUris.add("/current_version/linux/GlycoWorkbenchLin_x86.zip");
		trackUris.add("/current_version/linux/GlycoWorkbenchLin_x86-64.zip");
		
		trackUris.add("/current_version/macosx/GlycoWorkbenchMac_Carbon_32.zip");
		trackUris.add("/current_version/macosx/GlycoWorkbenchMac_Cocoa_32.zip");
		trackUris.add("/current_version/macosx/GlycoWorkbenchMac_Cocoa_x86-64.zip");
		
		URIResults results=analyser.retrieveGetStastitics(trackUris);
		results.printURIToDownloadCountToStdout();
		results.printUniqueHostCount();
		results.printUniqueDomainNameCount();
	}
	
	public static void defaultMatch() throws UnableToDecompressLogFileException, UnableToReadLogFileException, UnableToReadErrorLogFileException, UnableToReadTLDLibraryFileException, URISyntaxException, UnableToReadHostNameConstraintFileException{
		ApacheLogAnalyser analyser=new ApacheLogAnalyser(new File("/users/david/gwb_logs/"));
		
		analyser.excludeClientsInErrorLog(4);
		analyser.botExcluder(true);
		analyser.excludeIpAddresses(true);
		analyser.excludeHostsContainingAdsl(true);
		
		analyser.loadTldLibrary(new File(ApacheLogAnalyser.class.getResource("tlds.csv").toURI())).printTldLibrary();

		analyser.loadHostNameConstraintFile(new File("/users/david/gwb_host_constrain_list.txt"));
		
		List<String> trackUris=new ArrayList<>();
		trackUris.add("/current_version/windows/GlycoWorkbenchWin_x86-64.exe");
		trackUris.add("/current_version/windows/GlycoWorkbenchWin_x86.exe"); 
		trackUris.add("/current_version/windows/GlycoWorkbenchWin_x86-64.zip");
		trackUris.add("/current_version/windows/GlycoWorkbenchWin_x86.zip");
		
		trackUris.add("/current_version/linux/GlycoWorkbenchLin_x86.zip");
		trackUris.add("/current_version/linux/GlycoWorkbenchLin_x86-64.zip");
		
		trackUris.add("/current_version/macosx/GlycoWorkbenchMac_Carbon_32.zip");
		trackUris.add("/current_version/macosx/GlycoWorkbenchMac_Cocoa_32.zip");
		trackUris.add("/current_version/macosx/GlycoWorkbenchMac_Cocoa_x86-64.zip");
		
		URIResults results=analyser.retrieveGetStastitics(trackUris);
		results.printURIToDownloadCountToStdout();
		results.printUniqueHostCount();
		results.printHostsMatching(Pattern.compile("ox\\.ac\\.uk$"));
	}
	
	public static void main(String args[]) throws UnableToDecompressLogFileException, UnableToReadLogFileException, UnableToReadErrorLogFileException, UnableToReadTLDLibraryFileException, URISyntaxException, UnableToReadHostNameConstraintFileException{
//		defaultRun();
//		defaultConstrainedRun();
		//defaultAllBarBots();
		
		defaultMatch();
	}
}
