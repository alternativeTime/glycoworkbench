/*
 *   EuroCarbDB, a framework for carbohydrate bioinformatics
 *
 *   Copyright (c) 2006-2009, Eurocarb project, or third-party contributors as
 *   indicated by the @author tags or express copyright attribution
 *   statements applied by the authors.  
 *
 *   This copyrighted material is made available to anyone wishing to use, modify,
 *   copy, or redistribute it subject to the terms and conditions of the GNU
 *   Lesser General Public License, as published by the Free Software Foundation.
 *   A copy of this license accompanies this distribution in the file LICENSE.txt.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 *   or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 *   for more details.
 *
 *   Last commit: $Rev$ by $Author$ on $Date::             $  
 */

/**
 * @author David Damerell (d.damerell@imperial.ac.uk)
 */
package org.eurocarbdb.application.glycoworkbench;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Window;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;

import org.eurocarbdb.application.glycoworkbench.GlycoWorkbench.CONTAINER;
//import org.eurocarbdb.application.glycoworkbench.test.BasicDraggableResizableWindow;
//import org.eurocarbdb.application.glycoworkbench.test.ComponentResizer;

public class DockableEvent {
	private Window defaultDockedWindow;
	private Container defaultDockedContainer;
	private Container currentDockedWindow;
	private Container currentDockedContainer;
	
	public DockableEvent(Window _defaultDockedWindow,Container _defaultDockedContainer){
		defaultDockedWindow=_defaultDockedWindow;
		defaultDockedContainer=_defaultDockedContainer;
	}
	
	protected void changeCanvasPaneContainer(CONTAINER container){
		if(currentDockedWindow==null){
			currentDockedWindow=defaultDockedWindow;
		}else if(
					(container==CONTAINER.DOCKED && currentDockedWindow==defaultDockedWindow) ||
					(container==CONTAINER.FRAME && currentDockedWindow!=defaultDockedWindow && currentDockedWindow instanceof JFrame) //||
					//(container==CONTAINER.NODEC_DIALOG && currentDockedWindow instanceof BasicDraggableResizableWindow)
				){
			return ;
		}
		
		if(container==CONTAINER.NODEC_DIALOG){
//			BasicDraggableResizableWindow dialog=new BasicDraggableResizableWindow();
//			ComponentResizer cr = new ComponentResizer();
//			cr.setSnapSize(new Dimension(10, 10));
//			cr.registerComponent(dialog);
//			dialog.setSize(200,200);
//			
//			JPanel contentPane = dialog.getContentPane();
//			contentPane.setLayout(new BorderLayout());
//
//			initialise(contentPane,currentDockedContainer);
//			
//			dialog.pack();
//			dialog.setVisible(true);
//			currentDockedWindow=dialog;
//			currentDockedContainer=contentPane;
		}else if(container==CONTAINER.FRAME){
			JFrame.setDefaultLookAndFeelDecorated(true);
			final JFrame frame=new JFrame();
			
			
			frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			
			frame.addWindowListener(new WindowListener(){

				@Override
				public void windowActivated(WindowEvent arg0) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void windowClosed(WindowEvent arg0) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void windowClosing(WindowEvent arg0) {
					// TODO Auto-generated method stub
					System.err.println("here");
					changeCanvasPaneContainer(CONTAINER.DOCKED);
					frame.setVisible(false);
					frame.dispose();
				}

				@Override
				public void windowDeactivated(WindowEvent arg0) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void windowDeiconified(WindowEvent arg0) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void windowIconified(WindowEvent arg0) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void windowOpened(WindowEvent arg0) {
					// TODO Auto-generated method stub
					
				}
			});
			
			frame.setLayout(new BorderLayout());
			
			initialise(frame,currentDockedContainer);
			currentDockedWindow=frame;
			currentDockedContainer=frame;
			frame.pack();
			
			frame.setVisible(true);
			
		}else if(container==CONTAINER.DOCKED){
			initialise(defaultDockedContainer,currentDockedContainer);
			currentDockedContainer=defaultDockedContainer;
			currentDockedWindow=defaultDockedWindow;
		}
	}

	protected void initialise(Container moveToContainer,Container currentDockedContainer) {
		
	}
}
