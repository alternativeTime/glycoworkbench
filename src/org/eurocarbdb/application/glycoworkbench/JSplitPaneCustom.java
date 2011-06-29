package org.eurocarbdb.application.glycoworkbench;

import java.awt.Graphics;
import java.util.HashSet;

import javax.swing.JSplitPane;

public class JSplitPaneCustom extends JSplitPane{
	public static HashSet<JSplitPane> toPaint=new HashSet<JSplitPane>();
	public static HashSet<JSplitPaneCustom> painted=new HashSet<JSplitPaneCustom>();
	
	public JSplitPaneCustom(){
		super();
	}
	
	public JSplitPaneCustom(int verticalSplit) {
		super(verticalSplit);
	}

	@Override
	public void paint(Graphics g){
		super.paint(g);
		if(toPaint.contains(this) && !painted.contains(this)){
			painted.add(this);
		}
	}
}