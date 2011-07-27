package org.eurocarbdb.application.glycoworkbench;

import java.awt.BorderLayout;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.eurocarbdb.application.glycanbuilder.LogUtils;

import sun.swing.SwingUtilities2;

import chrriis.dj.nativeswing.swtimpl.components.JWebBrowser;

public class WebBrowser extends JPanel {
	final public JWebBrowser webBrowser;
	final public JPanel webBrowserPanel;

	public WebBrowser() {
		super(new BorderLayout());
		webBrowserPanel = new JPanel(new BorderLayout());
		webBrowserPanel.setBorder(BorderFactory
				.createTitledBorder("Native Web Browser component"));
		webBrowser = new JWebBrowser();
		
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){	
				webBrowser.navigate("http://www.google.com");
				webBrowser.setMenuBarVisible(false);
				webBrowserPanel.add(webBrowser, BorderLayout.CENTER);
				add(webBrowserPanel, BorderLayout.CENTER);
			}
		});
	}

	public void openResource(final URL remoteResource, final String localResource,
			boolean localInJar) throws URISyntaxException, IOException {
		//this.webBrowser.navigate(remoteResource.toString());

		if (checkSiteExists(remoteResource)) {
			SwingUtilities.invokeLater(new Runnable(){
				@Override
				public void run() {
					webBrowser.navigate(remoteResource.toString());
				}
			});
		}else{
			if (localInJar) {
				this.openResource(new File(localResource));
			} else {
				SwingUtilities.invokeLater(new Runnable(){
					@Override
					public void run() {
						webBrowser.navigate(localResource.toString());
					}
				});
			}
		}
	}
	
	public void navigate(final URL remoteResource) throws URISyntaxException, IOException {
		//this.webBrowser.navigate(remoteResource.toString());

		if (checkSiteExists(remoteResource)) {
			SwingUtilities.invokeLater(new Runnable(){
				public void run(){
					webBrowser.navigate(remoteResource.toString());
				}
			});
		}
	}

	public boolean checkSiteExists(URL url) {
		try {
			HttpURLConnection.setFollowRedirects(false);

			HttpURLConnection con = (HttpURLConnection) url
					.openConnection();
			con.setRequestMethod("HEAD");
			return (con.getResponseCode() == HttpURLConnection.HTTP_OK);
		} catch (Exception e) {
			//e.printStackTrace();
			return false;
		}
	}

	public void openResource(URL url) throws URISyntaxException, IOException {
		openResource(url.toURI());
	}

	public void openResource(URI uri) throws IOException {
		openResource(new File(uri));
	}

	public void openResource(InputStream inputStream) throws IOException {

		openResource(new InputStreamReader(inputStream));
	}

	public void openResource(File file) throws FileNotFoundException,
			IOException {
		openResource(new FileReader(file));
	}

	public void openResource(Reader reader) throws IOException {
		BufferedReader bufferedReader = new BufferedReader(reader);

		StringBuffer buf = new StringBuffer();
		String line;

		while ((line = bufferedReader.readLine()) != null) {
			buf.append(line);
		}

		bufferedReader.close();
		reader.close();

		this.setHTMLContent(buf.toString());
	}

	public void setHTMLContent(final String content) {
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				webBrowser.setHTMLContent(content);
			}
		});
	}
}
