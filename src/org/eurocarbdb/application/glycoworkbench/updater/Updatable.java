package org.eurocarbdb.application.glycoworkbench.updater;

public interface Updatable {
	public String getMajorVersion();
	public String getMinorVersion();
	public String getBuildState();
	public String getBuildNo();
}
