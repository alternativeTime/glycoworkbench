package org.eurocarbdb.application.glycoworkbench.launchers;

import java.io.IOException;

import org.eurocarbdb.application.glycanbuilder.LogUtils;

public class MacWrapper32 {
	public static void main(String args[]){
		MacWrapper macWrapper=new MacWrapper("32");
		macWrapper.run();
	}
}
