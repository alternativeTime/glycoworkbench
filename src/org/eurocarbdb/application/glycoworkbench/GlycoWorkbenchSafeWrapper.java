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
 @author Alessio Ceroni (a.ceroni@imperial.ac.uk), David Damerell (d.damerell@imperial.ac.uk)
 */

package org.eurocarbdb.application.glycoworkbench;


//import chrriis.dj.nativeswing.swtimpl.NativeInterface;
import javax.swing.SwingUtilities;
import java.io.IOException;

import org.eurocarbdb.application.glycoworkbench.GlycoWorkbench;

public class GlycoWorkbenchSafeWrapper{
	public static void main(final String[] args) {
//		NativeInterface.open();
		SwingUtilities.invokeLater(new Runnable() {
    			public void run() {
				System.out.println("Starting up GlycoWorkbench in EDT");	
				try{
					GlycoWorkbench.staticStartup(args);
				}catch(IOException ex){
					ex.printStackTrace();
				}
			}
		});
//	        NativeInterface.runEventPump();

	}
}
