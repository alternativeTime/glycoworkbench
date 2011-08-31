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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.print.PrinterJob;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileFilter;

import org.apache.tools.ant.launch.Locator;
import org.eurocarbdb.application.glycanbuilder.ActionManager;
import org.eurocarbdb.application.glycanbuilder.BaseDocument;
import org.eurocarbdb.application.glycanbuilder.BuilderWorkspace;
import org.eurocarbdb.application.glycanbuilder.Configuration;
import org.eurocarbdb.application.glycanbuilder.Context;
import org.eurocarbdb.application.glycanbuilder.ContextAwareContainer;
import org.eurocarbdb.application.glycanbuilder.CoreDictionary;
import org.eurocarbdb.application.glycanbuilder.CoreType;
import org.eurocarbdb.application.glycanbuilder.DictionaryConfiguration;
import org.eurocarbdb.application.glycanbuilder.ExtensionFileFilter;
import org.eurocarbdb.application.glycanbuilder.FileConstants;
import org.eurocarbdb.application.glycanbuilder.FileHistory;
import org.eurocarbdb.application.glycanbuilder.FileUtils;
import org.eurocarbdb.application.glycanbuilder.GlycanAction;
import org.eurocarbdb.application.glycanbuilder.GlycanCanvas;
import org.eurocarbdb.application.glycanbuilder.GlycanDocument;
import org.eurocarbdb.application.glycanbuilder.GlycanRendererAWT;
import org.eurocarbdb.application.glycanbuilder.GraphicOptions;
import org.eurocarbdb.application.glycanbuilder.ICON_SIZE;
import org.eurocarbdb.application.glycanbuilder.JCommandButtonAction;
import org.eurocarbdb.application.glycanbuilder.LogUtils;
import org.eurocarbdb.application.glycanbuilder.Monitor;
import org.eurocarbdb.application.glycanbuilder.MouseUtils;
import org.eurocarbdb.application.glycanbuilder.NotationChangeListener;
import org.eurocarbdb.application.glycanbuilder.ResidueDictionary;
import org.eurocarbdb.application.glycanbuilder.STOCK_ICON;
import org.eurocarbdb.application.glycanbuilder.SVGUtils;
import org.eurocarbdb.application.glycanbuilder.ThemeManager;
import org.eurocarbdb.application.glycanbuilder.UIActionListener;
import org.eurocarbdb.application.glycoworkbench.plugin.AnnotationPlugin;
import org.eurocarbdb.application.glycoworkbench.plugin.FragmentsPlugin;
import org.eurocarbdb.application.glycoworkbench.plugin.Plugin;
import org.eurocarbdb.application.glycoworkbench.plugin.PluginManager;
import org.eurocarbdb.application.glycoworkbench.plugin.ProfilerPlugin;
import org.eurocarbdb.application.glycoworkbench.plugin.ReportingPlugin;
import org.eurocarbdb.application.glycoworkbench.plugin.reporting.AnnotationReportDocument;
import org.eurocarbdb.application.glycoworkbench.updater.Updatable;
import org.eurocarbdb.application.glycoworkbench.updater.Updater;
import org.pushingpixels.flamingo.api.common.CommandButtonDisplayState;
import org.pushingpixels.flamingo.api.common.JCommandButton;
import org.pushingpixels.flamingo.api.common.JCommandButton.CommandButtonKind;
import org.pushingpixels.flamingo.api.common.JCommandButtonPanel;
import org.pushingpixels.flamingo.api.common.JCommandMenuButton;
import org.pushingpixels.flamingo.api.common.RichTooltip;
import org.pushingpixels.flamingo.api.common.icon.ResizableIcon;
import org.pushingpixels.flamingo.api.common.popup.JCommandPopupMenu;
import org.pushingpixels.flamingo.api.common.popup.JPopupPanel;
import org.pushingpixels.flamingo.api.common.popup.PopupPanelCallback;
import org.pushingpixels.flamingo.api.ribbon.AbstractRibbonBand;
import org.pushingpixels.flamingo.api.ribbon.JFlowRibbonBand;
import org.pushingpixels.flamingo.api.ribbon.JRibbonBand;
import org.pushingpixels.flamingo.api.ribbon.JRibbonFrame;
import org.pushingpixels.flamingo.api.ribbon.RibbonApplicationMenu;
import org.pushingpixels.flamingo.api.ribbon.RibbonApplicationMenuEntryPrimary;
import org.pushingpixels.flamingo.api.ribbon.RibbonContextualTaskGroup;
import org.pushingpixels.flamingo.api.ribbon.RibbonElementPriority;
import org.pushingpixels.flamingo.api.ribbon.RibbonTask;
import org.pushingpixels.flamingo.api.ribbon.resize.CoreRibbonResizePolicies;
import org.pushingpixels.flamingo.api.ribbon.resize.IconRibbonBandResizePolicy;
import org.pushingpixels.flamingo.api.ribbon.resize.RibbonBandResizePolicy;
import org.pushingpixels.substance.api.SubstanceLookAndFeel;
import chrriis.dj.nativeswing.swtimpl.NativeInterface;

public class GlycoWorkbench extends JRibbonFrame implements ActionListener,
		BaseDocument.DocumentChangeListener,
		GlycanCanvas.SelectionChangeListener, FileHistory.Listener,
		MouseListener, GlycanWorkspace.Listener, ContextAwareContainer,
		NotationChangeListener, UIActionListener, Updatable {

	public static boolean SUBSTANCE_ENABLED=false;
	private static final long serialVersionUID = 0L;
	private static ICON_SIZE defaultMenuIconSize = ICON_SIZE.L1;
	private static ICON_SIZE barIconSize = ICON_SIZE.L2;

	private static String MAJOR_VERSION = "GWB_MAJOR";
	private static String MINOR_VERSION = "GWB_MINOR";
	private static String BUILD_NUMBER = "GWB_BUILD";
	private static String BUILD_STATE = "GWB_STATE";

//	 private static String MAJOR_VERSION="2";
//	 private static String MINOR_VERSION="0";
//	 private static String BUILD_NUMBER="50";
//	 private static String BUILD_STATE="ALPHA";

	// singletons
	protected GlycanWorkspace theWorkspace;
	static protected ThemeManager defaultThemeManager;

	public static ThemeManager getDefaultThemeManager() {
		return defaultThemeManager;
	}

	protected GlycanDocument theDoc;

	public GlycanDocument getTheDoc() {
		return theDoc;
	}

	@Deprecated
	protected ActionManager theActionManager;
	protected ActionManager theActionManager2;
	protected RibbonManager ribbonManager;
	// graphical objects
	protected JMenuBar theMenuBar;
	protected JToolBar theToolBarFile;
	protected JToolBar theToolBarPanes;
	protected JToolBar theToolBarTools;
	protected JPanel theToolBarPanel;

	protected GlycanCanvas theCanvas;
	protected JSplitPane theSplitPane;
	protected JSplitPane theLeftSplitPane;
	protected JSplitPane theTopSplitPane;

	// plugins
	protected PluginManager thePluginManager;
	protected ThemeManager themeManager;

	public ThemeManager getThemeManager() {
		return themeManager;
	}

	// menus
	protected JMenu recent_files_menu;
	protected String last_exported_file = null;

	private Monitor halt_interactions = null;
	private DockableEvent leftPanelDockableEvent;
	private DockableEvent bottomPanelDockableEvent;
	private DockableEvent rightPanelDockableEvent;

	public static File getConfigurationFile() throws IOException {
		String userHomeDirectory = System.getProperty("user.home");
		String osName = System.getProperty("os.name");

		File configurationFile;
		if (osName.equals("Linux")) {
			configurationFile = new File(userHomeDirectory
					+ "/.glycoworkbench.xml");
		} else if (osName.startsWith("Windows")) {
			String applicationDataDirectory = System.getenv("APPDATA");
			if (applicationDataDirectory == null) {
				applicationDataDirectory = userHomeDirectory + File.separator
						+ "Application Data";
			}

			File glycoworkBenchProfilesDirectory = new File(
					applicationDataDirectory + File.separator
							+ "GlycoWorkBench");

			if (!glycoworkBenchProfilesDirectory.exists()) {
				if (!glycoworkBenchProfilesDirectory.mkdir()) {
					throw new IOException("Could not create directory: "
							+ glycoworkBenchProfilesDirectory.toString());
				}
			}

			configurationFile = new File(glycoworkBenchProfilesDirectory
					+ File.separator + "glycoworkbench.xml");
		} else {
			configurationFile = new File(userHomeDirectory
					+ "/glycoworkbench.xml");
		}
		System.out.println("Using configuration file: "
				+ configurationFile.toString());
		return configurationFile;
	}

	private Updater updater;

	public GlycoWorkbench() throws IOException {
		try {
			updater = new Updater("http://download.glycoworkbench.org");
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		initGwb();
	}

	private boolean glycanCanvasDetached = false;
	private boolean spectrumPanelDetached = false;
	private boolean leftPanelDetached = false;
	private boolean rightPanelDetached = false;

	private void updateDividerLocations() {
		if ((!glycanCanvasDetached || !spectrumPanelDetached || !leftPanelDetached)
				&& !rightPanelDetached) {
			theSplitPane.setDividerLocation(theLastSplitPaneDividerLocation);
		}

		if (!spectrumPanelDetached
				&& (!glycanCanvasDetached || !leftPanelDetached)) {
			theLeftSplitPane
					.setDividerLocation(theLastLeftSplitPaneDividerLocation);
		}

		if (!glycanCanvasDetached && !leftPanelDetached) {
			theTopSplitPane
					.setDividerLocation(theLastTopSplitPaneDividerLocation);
		} else if (!glycanCanvasDetached) {
			theTopSplitPane.setDividerLocation(.0);
		} else if (!leftPanelDetached) {
			System.err.println("Setting divider location to 1");
			theTopSplitPane.setDividerLocation(1.);
		}

		if (detachedSplitPaneCount == 3) {
			if (!glycanCanvasDetached) {
				theSplitPane.setDividerLocation(1.);
				theTopSplitPane.setDividerLocation(0.);
				theLeftSplitPane.setDividerLocation(1.);
			} else if (!spectrumPanelDetached) {
				theSplitPane.setDividerLocation(1.);
			} else if (!leftPanelDetached) {
				theSplitPane.setDividerLocation(1.);
				theTopSplitPane.setDividerLocation(1.);
				theLeftSplitPane.setDividerLocation(1.);
			} else if (!rightPanelDetached) {
				theSplitPane.setDividerLocation(0.);
			}
		}
	}

	public void initGwb() throws IOException {
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		ribbonManager = new RibbonManager(this.getRibbon());

		try {
			themeManager = new ThemeManager(null, this.getClass());
			defaultThemeManager = themeManager;
			try {
				themeManager.addIconPath("/icons/glycan_builder",
						this.getClass());
				themeManager.addIconPath("/gwb/icons", GlycoWorkbench.class);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				themeManager.addIconPath("/icons/crystal_project",
						this.getClass());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			FileUtils.defaultThemeManager = themeManager;
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		ThemeManager.lookupNoneCached = false;

		FileUtils.setThemeManager(themeManager);
		

		theActionManager2 = new ActionManager();
		createActions();

		applicationMenu = new RibbonApplicationMenu();

		this.getRibbon().configureHelp(
				themeManager.getResizableIcon(STOCK_ICON.HELP_CONTENTS,
						ICON_SIZE.L3).getResizableIcon(), new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent arg0) {
						// TODO Auto-generated method stub
						onHelp();
					}

				});

		this.setSize(600, 300);
		this.setLocationRelativeTo(null);
		// this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		LogUtils.setReportOwner(this);
		LogUtils.setGraphicalReport(true);

		theWorkspace = new GlycanWorkspace(this.getConfigurationFile()
				.toString(), true, new GlycanRendererAWT());
		theWorkspace.setAutoSave(true);

		theDoc = theWorkspace.getStructures();
		theActionManager = new ActionManager();
		
		thePluginManager = new PluginManager(this, theWorkspace);
		
		
		halt_interactions = new Monitor(this);

		theCanvas = new GlycanCanvas(this, theWorkspace, themeManager, true);
		theCanvas.registerUIListener(this);

		
		DockableEvent.addGlobalAction(theCanvas.getActionManager().get(
				"implode"));
		DockableEvent.addGlobalAction(theCanvas.getActionManager().get(
				"explode"));
		DockableEvent.addGlobalAction(this.theActionManager2.get("saveall"));

		DockableEvent.initiliseGlobalKeyBindings(this);

		setIconImage(themeManager.getImageIcon("gwb_logo", ICON_SIZE.L4)
				.getImage());

		initNewMenu();
		initOpenMenu();
		initOpenRecent();
		initSaveMenu();
		initExportMenu();
		initImportMenu();
		initAboutAppMenuItem();
		initOthersMenu();
		initSettingsMenu();

		createFileBand();

		// this.setApplicationIcon(themeManager.getResizableIcon("open",
		// ICON_SIZE.L4).getResizableIcon());

		this.getRibbon().setApplicationMenu(applicationMenu);

		createEditBand();
		createViewBand();
		createStructureBand();
		createLinkageBand();
		createToolsRibbonTask();

		UIManager.getDefaults().put("ToolTip.hideAccelerator", Boolean.TRUE);

		JPanel c2Panel = new JPanel();
		c2Panel.setLayout(new BorderLayout());
		getContentPane().add(c2Panel, BorderLayout.CENTER);

		theSplitPane = new JSplitPaneCustom();

		theSplitPane.setResizeWeight(1.);
		theSplitPane.setOneTouchExpandable(true);
		c2Panel.add(theSplitPane, BorderLayout.CENTER);

		theLeftSplitPane = new JSplitPaneCustom(JSplitPane.VERTICAL_SPLIT);
		theLeftSplitPane.setResizeWeight(1.);
		theLeftSplitPane.setOneTouchExpandable(true);
		theSplitPane.setLeftComponent(theLeftSplitPane);

		theTopSplitPane = new JSplitPaneCustom(JSplitPane.HORIZONTAL_SPLIT);
		theTopSplitPane.setResizeWeight(0.);
		theTopSplitPane.setOneTouchExpandable(true);
		theLeftSplitPane.setTopComponent(theTopSplitPane);

		canvasPanel = new JSplitPaneCustom(JSplitPane.VERTICAL_SPLIT);
		canvasPanel.setLayout(new BorderLayout());

		canvasScrollPane = new JScrollPane(theCanvas);
		theCanvas.setScrollPane(canvasScrollPane);

		toolBarPanel = new JPanel();
		toolBarPanel.setLayout(new BorderLayout());
		toolBarPanel.add(theCanvas.getToolBarStructure(), BorderLayout.CENTER);

		toolBarPanelLinkage = new JPanel();
		toolBarPanelLinkage.setLayout(new BorderLayout());
		toolBarPanelLinkage.add(theCanvas.getToolBarProperties(),
				BorderLayout.CENTER);

		glycanCanvasDockableEvent = new DockableEvent(this, canvasPanel,
				"Glycan canvas") {
			protected void initialise(Container moveToContainer,
					Container currentDockedContainer) {
				if (currentDockedContainer != null) {
					currentDockedContainer.remove(canvasScrollPane);
					currentDockedContainer.remove(toolBarPanel);
					currentDockedContainer.remove(toolBarPanelLinkage);
				}

				if (canvasPanel != moveToContainer) {
					detachedSplitPaneCount++;
					glycanCanvasDetached = true;
				} else {
					detachedSplitPaneCount--;

					topHeightRequired = true;

					glycanCanvasDetached = false;
				}

				moveToContainer.add(canvasScrollPane, BorderLayout.CENTER);
				moveToContainer.add(toolBarPanel, BorderLayout.NORTH);
				moveToContainer.add(toolBarPanelLinkage, BorderLayout.SOUTH);

				hideAll();
			}

			protected void finaliseAttachment() {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						while (true) {
							if (theSplitPane.isVisible()) {
								updateDividerLocations();
								break;
							}
						}
					}
				});
			}
		};

		glycanCanvasDockableEvent.changeCanvasPaneContainer(CONTAINER.DOCKED);

		theTopSplitPane.setRightComponent(canvasPanel);

		final JPanel leftPanel = new JPanel();
		leftPanel.setLayout(new BorderLayout());
		leftPanel.add(thePluginManager.getLeftComponent());

		leftPanelDockableEvent = new DockableEvent(this, leftPanel,
				"Project explorer") {
			protected void initialise(Container moveToContainer,
					Container currentDockedContainer) {
				if (currentDockedContainer != null) {

					currentDockedContainer.remove(thePluginManager
							.getLeftComponent());
					if (currentDockedContainer instanceof JPanel) {
						((JPanel) currentDockedContainer).revalidate();
					}
				}

				if (moveToContainer != leftPanel) {
					hideLeftPanels();
					detachedSplitPaneCount++;
					leftPanelDetached = true;
				} else {
					detachedSplitPaneCount--;

					topHeightRequired = true;
					leftPanelDetached = false;
				}

				moveToContainer.add(thePluginManager.getLeftComponent());

				hideAll();
			}

			protected void finaliseAttachment() {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						while (true) {
							if (theSplitPane.isVisible()) {
								updateDividerLocations();
								break;
							}
						}
					}
				});
			}
		};

		leftPanelDockableEvent.changeCanvasPaneContainer(CONTAINER.DOCKED);

		theTopSplitPane.setLeftComponent(leftPanel);
		// hideBottomPanels();

		final JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new BorderLayout());
		bottomPanel.add(thePluginManager.getBottomComponent());

		bottomPanelDockableEvent = new DockableEvent(this, bottomPanel,
				"Spectrum viewer") {
			protected void initialise(Container moveToContainer,
					Container currentDockedContainer) {
				if (currentDockedContainer != null) {
					currentDockedContainer.remove(thePluginManager
							.getBottomComponent());
				}

				if (bottomPanel != moveToContainer) {
					hideBottomPanels();
					detachedSplitPaneCount++;
					spectrumPanelDetached = true;
				} else {
					showBottomPanels();
					detachedSplitPaneCount--;

					bottomHeightRequired = true;
					spectrumPanelDetached = false;
				}

				moveToContainer.add(thePluginManager.getBottomComponent());

				hideAll();
			}

			protected void finaliseAttachment() {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						while (true) {
							if (theSplitPane.isVisible()) {
								updateDividerLocations();
								break;
							}
						}
					}
				});
			}
		};

		bottomPanelDockableEvent.changeCanvasPaneContainer(CONTAINER.DOCKED);

		theLeftSplitPane.setBottomComponent(bottomPanel);

		final JPanel rightPanel = new JPanel();
		rightPanel.setLayout(new BorderLayout());
		rightPanel.add(thePluginManager.getRightComponent());

		rightPanelDockableEvent = new DockableEvent(this, rightPanel,
				"Annotation tools") {
			protected void initialise(Container moveToContainer,
					Container currentDockedContainer) {
				if (currentDockedContainer != null) {
					currentDockedContainer.remove(thePluginManager
							.getRightComponent());
				}

				if (moveToContainer != rightPanel) {
					hideRightPanels();
					detachedSplitPaneCount++;
					rightPanelDetached = true;
				} else {
					detachedSplitPaneCount--;

					topHeightRequired = true;
					bottomHeightRequired = true;

					rightPanelDetached = false;
				}

				moveToContainer.add(thePluginManager.getRightComponent());

				hideAll();
			}

			protected void finaliseAttachment() {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						while (true) {
							if (theSplitPane.isVisible()) {
								updateDividerLocations();
								break;
							}
						}
					}
				});
			}
		};

		rightPanelDockableEvent.changeCanvasPaneContainer(CONTAINER.DOCKED);

		theSplitPane.setRightComponent(rightPanel);
		// hideRightPanels();

		// // add listeners
		theDoc.addDocumentChangeListener(this);
		theCanvas.addSelectionChangeListener(this);
		theCanvas.addMouseListener(this);

		theCanvas.addContextAwareContainer(this);

		theWorkspace.addDocumentChangeListener(this);
		theWorkspace.addWorkspaceListener(this);
		theWorkspace.getFileHistory().addHistoryChangedListener(this);
		//
		// setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new WindowAdapter() {

			public void windowOpened(WindowEvent we) {
				hideRightPanels();
				hideBottomPanels();
			}

			public void windowClosing(WindowEvent we) {
				try {
					onExit();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		setSize(880, 660);
		setLocationRelativeTo(null);
		onNew(theWorkspace);
		theWorkspace.setChanged(false);

		updateActions();

		theCanvas.addNotationChangeListener(this);
		
	}

	public void runStartupTasksInBackground() {
		new Thread(){
			public void run() {
				((ProfilerPlugin) thePluginManager.get("Profiler"))
						.deferredOnStartup();
				
			}
		}.start();
		
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				createPopupMenu();
			}
		});
		
		final GlycoWorkbench self=this;
		new Thread() {
			public void run() {
				checkForUpdates();
				
				final String upToDate;
				if (updater == null) {
					upToDate = "unable to contact update site";
				} else if (updater.isUptoDate(self)) {
					upToDate = "latest";
				} else {
					upToDate = "update available";
				}

				SwingUtilities.invokeLater(new Runnable(){
					public void run(){
						status.setText("Version: " + GlycoWorkbench.MAJOR_VERSION
								+ "." + GlycoWorkbench.MINOR_VERSION + " "
								+ GlycoWorkbench.BUILD_STATE + " ("
								+ GlycoWorkbench.BUILD_NUMBER + ")" + "(" + upToDate
								+ ")");
					}
				});
				
				if (upToDate.equals("update available")) {
					status.setText("<html><u>" + status.getText()
							+ "</u></html>");

					
						
					status.addMouseListener(new MouseListener() {
						@Override
						public void mouseClicked(MouseEvent arg0) {
							final JFrame browserFrame = new JFrame();
							
							final WebBrowser browser = new WebBrowser();
							SwingUtilities.invokeLater(new Runnable(){
								public void run(){
									try{
										browser.navigate(new URL(
										"http://download.glycoworkbench.org/current_version/"));
										browserFrame.setIconImages(getIconImages());
										browserFrame.add(browser);
										browserFrame.setSize(500, 500);
										browserFrame.setTitle("Download update...");
									} catch (IOException e) {
										LogUtils.report(e);
									} catch (URISyntaxException e) {
										LogUtils.report(e);
									}
								}
							});
							
							
							browserFrame.setVisible(true);
							status.setForeground(Color.GRAY);
						}

						@Override
						public void mouseEntered(MouseEvent e) {
							// TODO Auto-generated method stub
							Component component = (Component) e.getSource();
							Cursor cursor = Cursor
									.getPredefinedCursor(Cursor.HAND_CURSOR);
							setCursor(cursor);
						}

						@Override
						public void mouseExited(MouseEvent e) {
							// TODO Auto-generated method stub
							Component component = (Component) e.getSource();
							Cursor cursor = Cursor
									.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
							setCursor(cursor);
						}

						@Override
						public void mousePressed(MouseEvent arg0) {
							// TODO Auto-generated method stub

						}

						@Override
						public void mouseReleased(MouseEvent arg0) {
							// TODO Auto-generated method stub

						}

					});
					
				}
			}
		}.start();
		
		
	}

	public Dimension largeIcon = new Dimension(30, 30);
	protected RibbonApplicationMenu applicationMenu;
	private int detachedSplitPaneCount = 4;

	public void initOpenMenu() {
		RibbonApplicationMenuEntryPrimary menuPrimary = new RibbonApplicationMenuEntryPrimary(
				theActionManager2.get("open").getResizableIcon(ICON_SIZE.L4),
				"Open", new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent arg0) {
						// collect file formats
						JFileChooser fileChooser = new JFileChooser();
						fileChooser.setAcceptAllFileFilterUsed(false);

						HashMap<javax.swing.filechooser.FileFilter, BaseDocument> all_ff = new HashMap<javax.swing.filechooser.FileFilter, BaseDocument>();
						for (BaseDocument doc : theWorkspace.getAllDocuments()) {
							javax.swing.filechooser.FileFilter ff = doc
									.getAllFileFormats();
							fileChooser.addChoosableFileFilter(ff);
							all_ff.put(ff, doc);
						}

						// open file chooser
						fileChooser.setCurrentDirectory(theWorkspace
								.getFileHistory().getRecentFolder());
						if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
							// retrieve file path and document type
							String filename = fileChooser.getSelectedFile()
									.getAbsolutePath();

							tryOpen(filename, false,
									fileChooser.getFileFilter());
						}

					}
				}, CommandButtonKind.ACTION_ONLY);

		// menuPrimary
		// .setRolloverCallback(new
		// RibbonApplicationMenuEntryPrimary.PrimaryRolloverCallback() {
		// @Override
		// public void menuEntryActivated(JPanel targetPanel) {
		// targetPanel.removeAll();
		//
		// JCommandButtonPanel openTypes = new JCommandButtonPanel(
		// CommandButtonDisplayState.MEDIUM);
		//
		// String groupName = "Types";
		// openTypes.addButtonGroup(groupName);
		//
		// JCommandButton openWorkspace = theActionManager2.get(
		// "openall").getJCommandButton("Workspace");
		// JCommandButton openIntoWorkspace = theActionManager2
		// .get("open").getJCommandButton("Document");
		//
		// openWorkspace
		// .setHorizontalAlignment(SwingUtilities.LEFT);
		// openIntoWorkspace
		// .setHorizontalAlignment(SwingUtilities.LEFT);
		//
		// ActionListener typeListener = new ActionListener() {
		// @Override
		// public void actionPerformed(ActionEvent e) {
		// Object source = e.getSource();
		// if (source instanceof JCommandButton) {
		// JCommandButton button = (JCommandButton) source;
		// String type = button.getText();
		// if (type.equals("Workspace")) {
		// onOpenDocument(theWorkspace
		// .getAllDocuments(), false);
		// } else if (type
		// .equals("Open into workspce")) {
		// onOpenDocument(theWorkspace
		// .getAllDocuments(), true);
		// }
		// }
		// }
		// };
		//
		// openTypes.setMaxButtonColumns(1);
		// openTypes.setMaxButtonRows(3);
		//
		// openWorkspace.addActionListener(typeListener);
		// openIntoWorkspace.addActionListener(typeListener);
		//
		// openTypes.addButtonToLastGroup(openWorkspace);
		// openTypes.addButtonToLastGroup(openIntoWorkspace);
		//
		// targetPanel.setLayout(new BorderLayout());
		// targetPanel.add(openTypes, BorderLayout.CENTER);
		//
		// }
		// });

		applicationMenu.addMenuEntry(menuPrimary);
	}

	private boolean topHeightRequired = false;
	private boolean bottomHeightRequired = false;

	public void hideAll() {
		if (detachedSplitPaneCount == 4) {
			theSplitPane.setVisible(false);
		} else {
			theSplitPane.setVisible(true);

			int height = lastHeight;

			if (topHeightRequired) {
				height += beforeHeightTop;
			}

			if (bottomHeightRequired) {
				height += beforeHeightBottom;
			}

			setSize(new Dimension(getWidth(), height));
		}
	}

	public void initAboutAppMenuItem() {
		RibbonApplicationMenuEntryPrimary menuPrimary = new RibbonApplicationMenuEntryPrimary(
				theActionManager2.get("about").getResizableIcon(ICON_SIZE.L4),
				"About", null, CommandButtonKind.ACTION_ONLY);

		menuPrimary
				.setRolloverCallback(new RibbonApplicationMenuEntryPrimary.PrimaryRolloverCallback() {
					@Override
					public void menuEntryActivated(JPanel targetPanel) {
						targetPanel.removeAll();

						try {
							URL url = GlycoWorkbench.class
									.getResource("/html/about_gwb.html");
							JEditorPane html = new JEditorPane(url);
							html.setEditable(false);
							html.setBorder(new EmptyBorder(20, 20, 20, 20));

							JScrollPane scrollPanel = new JScrollPane(html);
							scrollPanel.setSize(targetPanel.getSize());
							targetPanel.add(scrollPanel, BorderLayout.CENTER);

						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
				});

		applicationMenu.addMenuEntry(menuPrimary);
	}

	public void initOpenRecent() {
		RibbonApplicationMenuEntryPrimary menuPrimary = new RibbonApplicationMenuEntryPrimary(
				theActionManager2.get("open").getResizableIcon(ICON_SIZE.L4),
				"Open Recent", null, CommandButtonKind.ACTION_ONLY);

		final GlycoWorkbench self = this;

		menuPrimary
				.setRolloverCallback(new RibbonApplicationMenuEntryPrimary.PrimaryRolloverCallback() {
					@Override
					public void menuEntryActivated(JPanel targetPanel) {
						targetPanel.removeAll();

						JCommandButtonPanel openTypes = new JCommandButtonPanel(
								CommandButtonDisplayState.MEDIUM);

						Map<String, ArrayList<JCommandButtonAction>> fileTypeToList = new HashMap<String, ArrayList<JCommandButtonAction>>();
						fileTypeToList.put("Workspace",
								new ArrayList<JCommandButtonAction>());
						fileTypeToList.put("Structures",
								new ArrayList<JCommandButtonAction>());
						fileTypeToList.put("Fragments",
								new ArrayList<JCommandButtonAction>());
						fileTypeToList.put("PeakList",
								new ArrayList<JCommandButtonAction>());
						fileTypeToList.put("Annotated PeakList",
								new ArrayList<JCommandButtonAction>());
						fileTypeToList.put("Spectra",
								new ArrayList<JCommandButtonAction>());

						FileHistory theFileHistory = theWorkspace
								.getFileHistory();
						if (theFileHistory.getRecentFiles().size() > 1) {
							for (Iterator<String> i = theFileHistory.iterator(); i
									.hasNext();) {
								String file_path = i.next();
								String file_type = theFileHistory
										.getFileType(file_path);

								JCommandButtonAction jCommandButton = new JCommandButtonAction(
										theFileHistory
												.getAbbreviatedName(file_path),
										null);

								if (file_type.equals("Workspace")) {
									jCommandButton.setActionCommand("openall="
											+ file_path);
									fileTypeToList.get("Workspace").add(
											jCommandButton);
								} else if (file_type.equals("Structures")) {
									jCommandButton
											.setActionCommand("openstruct="
													+ file_path);
									fileTypeToList.get("Structures").add(
											jCommandButton);
								} else if (file_type.equals("Fragments")) {
									jCommandButton
											.setActionCommand("openfragments="
													+ file_path);
									fileTypeToList.get("Fragments").add(
											jCommandButton);
								} else if (file_type.equals("PeakList")) {
									jCommandButton
											.setActionCommand("openpeaks="
													+ file_path);
									fileTypeToList.get("PeakList").add(
											jCommandButton);
								} else if (file_type
										.equals("Annotated PeakList")) {
									jCommandButton
											.setActionCommand("openannpeaks="
													+ file_path);
									fileTypeToList.get("Annotated PeakList")
											.add(jCommandButton);
								} else if (file_type
										.equals("Annotated PeakList")) {
									jCommandButton
											.setActionCommand("openspectra="
													+ file_path);
									fileTypeToList.get("Annotated PeakList")
											.add(jCommandButton);
								} else {
									continue;
								}

								jCommandButton
										.setHorizontalAlignment(SwingUtilities.LEFT);
								jCommandButton.addActionListener(self);
							}

							ArrayList<String> withFiles = new ArrayList<String>();
							for (String fileType : fileTypeToList.keySet()) {
								if (fileTypeToList.get(fileType).size() > 0) {
									withFiles.add(fileType);
								}
							}

							String[] withFilesArray = withFiles
									.toArray(new String[1]);
							Arrays.sort(withFilesArray);
							for (String fileType : withFilesArray) {
								if(fileType!=null){
									openTypes.addButtonGroup(fileType);
									for (JCommandButtonAction button : fileTypeToList
											.get(fileType)) {
										openTypes
										.addButtonToGroup(fileType, button);
									}
								}
							}

							openTypes.setMaxButtonColumns(1);
							openTypes.setMaxButtonRows(3);
							JScrollPane scrollPanel = new JScrollPane(openTypes);
							scrollPanel.setSize(targetPanel.getSize());
							targetPanel.add(scrollPanel, BorderLayout.CENTER);

						} else {

						}
					}
				});

		applicationMenu.addMenuEntry(menuPrimary);
	}

	public void initSaveMenu() {
		RibbonApplicationMenuEntryPrimary saveItem = new RibbonApplicationMenuEntryPrimary(
				theActionManager2.get("save").getResizableIcon(ICON_SIZE.L4),
				"Save", new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent arg0) {
						// TODO Auto-generated method stub
						onSave(theWorkspace, true);
					}

				}, CommandButtonKind.ACTION_ONLY);

		saveItem.setRolloverCallback(new RibbonApplicationMenuEntryPrimary.PrimaryRolloverCallback() {
			private JCheckBox saveSpectraCheckBox;

			@Override
			public void menuEntryActivated(JPanel targetPanel) {
				targetPanel.removeAll();

				JCommandButtonPanel openTypes = new JCommandButtonPanel(
						CommandButtonDisplayState.MEDIUM);

				JCommandButton saveAll = theActionManager2.get("saveall")
						.getJCommandButton("Workspace");
				JCommandButton saveAllAs = theActionManager2.get("saveallas")
						.getJCommandButton("Workspace as...");

				JCommandButtonPanel openTypes2 = new JCommandButtonPanel(
						CommandButtonDisplayState.MEDIUM);

				openTypes2.addButtonGroup("Structures only");

				JCommandButton saveStructures = theActionManager2.get("save")
						.getJCommandButton("Structures");
				JCommandButton saveStructuresAs = theActionManager2.get(
						"saveas").getJCommandButton("Structures as...");

				saveAll.setHorizontalAlignment(SwingUtilities.LEFT);
				saveAllAs.setHorizontalAlignment(SwingUtilities.LEFT);
				saveStructures.setHorizontalAlignment(SwingUtilities.LEFT);
				saveStructuresAs.setHorizontalAlignment(SwingUtilities.LEFT);

				ActionListener typeListener = new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						Object source = e.getSource();
						if (source instanceof JCommandButton) {
							JCommandButton button = (JCommandButton) source;
							String type = button.getText();
							if (type.equals("Workspace")) {
								onSave(theWorkspace, true);
							} else if (type.equals("Workspace as...")) {
								onSaveAs(theWorkspace);
							} else if (type.equals("Structures")) {
								onSave(theDoc, true);
							} else if (type.equals("Structures as...")) {
								onSaveAs(theDoc);
							}
						}
					}
				};

				openTypes.setMaxButtonColumns(2);
				openTypes.setMaxButtonRows(2);

				saveAll.addActionListener(typeListener);
				saveAllAs.addActionListener(typeListener);
				saveStructures.addActionListener(typeListener);
				saveStructuresAs.addActionListener(typeListener);

				openTypes.addButtonGroup("Workspace");
				openTypes.addButtonToLastGroup(saveAll);
				openTypes.addButtonToLastGroup(saveAllAs);
				openTypes.addButtonGroup("Structures only");

				openTypes.addButtonToLastGroup(saveStructures);
				openTypes.addButtonToLastGroup(saveStructuresAs);

				openTypes.addButtonGroup("Save settings");
				//
				JPanel panel = new JPanel();
				panel.setLayout(new BorderLayout());

				if (saveSpectraCheckBox == null) {
					saveSpectraCheckBox = theCanvas.getTheActionManager().get("savespec").getJCheckBox(
							"Save spectra in workspace file", theCanvas);
					
					theCanvas.getTheActionManager().get("savespec").setSelected(
							theCanvas.getTheGlycanRenderer().getGraphicOptions().SAVE_SPECTRA_CUSTOM);
				}

				panel.add(saveSpectraCheckBox, BorderLayout.NORTH);

				JPanel main = new JPanel();
				main.setLayout(new BorderLayout());
				main.add(openTypes, BorderLayout.NORTH);
				main.add(panel, BorderLayout.CENTER);

				JScrollPane scrollPane = new JScrollPane(main);
				scrollPane.setSize(targetPanel.getSize());

				targetPanel.setLayout(new BorderLayout());
				targetPanel.add(scrollPane, BorderLayout.CENTER);
				//

			}
		});

		applicationMenu.addMenuEntry(saveItem);
	}

	public void initNewMenu() {
		RibbonApplicationMenuEntryPrimary saveItem = new RibbonApplicationMenuEntryPrimary(
				themeManager.getResizableIcon(STOCK_ICON.DOCUMENT_NEW,
						ICON_SIZE.L4).getResizableIcon(), "New",
				new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent arg0) {
						onNew(theWorkspace);
					}
				}, CommandButtonKind.ACTION_ONLY);

		applicationMenu.addMenuEntry(saveItem);
	}

	public void initOthersMenu() {
		GlycanAction printAction = this.theActionManager2.get("print");

		RibbonApplicationMenuEntryPrimary printItem = new RibbonApplicationMenuEntryPrimary(
				printAction.getResizableIcon(ICON_SIZE.L4), "Print",
				new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						// TODO Auto-generated method stub
						onPrint();
					}

				}, CommandButtonKind.ACTION_ONLY);

		this.applicationMenu.addMenuEntry(printItem);

		GlycanAction quitAction = this.theActionManager2.get("quit");

		RibbonApplicationMenuEntryPrimary quitItem = new RibbonApplicationMenuEntryPrimary(
				quitAction.getResizableIcon(ICON_SIZE.L4), "Quit",
				new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						// TODO Auto-generated method stub
						try {
							onExit();
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}

				}, CommandButtonKind.ACTION_ONLY);

		quitAction.addComponent(quitItem);

		this.applicationMenu.addMenuEntry(quitItem);

	}
	
	public void initSettingsMenu() {
		final GlycoWorkbench self=this;
		
		RibbonApplicationMenuEntryPrimary settingsItem = new RibbonApplicationMenuEntryPrimary(
						themeManager.getResizableIcon("export", ICON_SIZE.L7)
			.getResizableIcon(), "Settings", new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e){
					JDialog dialog=new JDialog(self);
					dialog.setTitle("Settings");

					JPanel panel=new JPanel();
					panel.setLayout(new GridBagLayout());
					int row=0;
					for(String dictionaryKey:DictionaryConfiguration.getDictionaryNameList()){
						createCustomGlycanBuilderResourcePanel(dictionaryKey,DictionaryConfiguration.getDefaultDictionaryFile(dictionaryKey),panel,row++);
					}
					
					JScrollPane scrollPanel = new JScrollPane(panel);
					scrollPanel.setSize(100,100);
					scrollPanel.setMaximumSize(new Dimension(100,100));
					
					dialog.add(scrollPanel);
					dialog.setVisible(true);
					dialog.setModal(true);
					dialog.setSize(900, 500);
					
					//dialog.pack();

				}
			},CommandButtonKind.ACTION_ONLY);
		
		applicationMenu.addMenuEntry(settingsItem);
	}
	
	private void createCustomGlycanBuilderResourcePanel(final String configKey,final String resource,JPanel parent,int row){
		//JPanel panel=new JPanel();
		//panel.setLayout(new GridLayout(1,3));
		//panel.setLayout(new GridBagLayout());
		
		JLabel label=new JLabel(configKey);
		
		
		final JTextField fileInputBox=new JTextField(getWorkspace().getDictionaryConfig().getDictionaryFile(configKey));
		
		
		fileInputBox.setPreferredSize(new Dimension(200,25));
		fileInputBox.setCaretPosition(0);
		
		JButton fileChooserButton=new JButton("Create custom");
		JButton fileOpenButton=new JButton("Open");
		JButton restoreButton=new JButton("Restore");
		final JButton editButton=new JButton("Edit");
		
		fileInputBox.getDocument().addDocumentListener(new DocumentListener(){

			@Override
			public void insertUpdate(DocumentEvent e){
				update();
			}

			@Override
			public void removeUpdate(DocumentEvent e){
				update();
			}

			@Override
			public void changedUpdate(DocumentEvent e){
				update();
			}
			
			public void update(){
				theWorkspace.getDictionaryConfig().setDictionaryFile(configKey, fileInputBox.getText());
				editButton.setEnabled(!theWorkspace.getDictionaryConfig().isDefault(configKey));
			}
		});
		
		final JFileChooser fileChooser=new JFileChooser();
		
		final GlycoWorkbench self=this;
		
		fileChooserButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				int returnValue=fileChooser.showDialog(self, "Save");
				
				if(returnValue==JFileChooser.APPROVE_OPTION){
					String selectedFileName=fileChooser.getSelectedFile().getPath();
					
					if(new File(selectedFileName).exists()){
						int value=JOptionPane.showConfirmDialog(self, "Overwrite existing file?", "File exists warning", JOptionPane.YES_NO_CANCEL_OPTION);
						if(value!=JOptionPane.YES_OPTION){
							return;
						}
					}
					
					try{
						BufferedReader is;
						if(resource.startsWith("http")){
							URLConnection conn=new URL(resource).openConnection();
							is=new BufferedReader(new InputStreamReader(conn.getInputStream()));
						}else{
							// open file
							java.net.URL file_url = BuilderWorkspace.getResource(resource);
							if( file_url==null )
								throw new FileNotFoundException(resource);
							is = new BufferedReader(new InputStreamReader(file_url.openStream()));
						}
						
						FileWriter writer=new FileWriter(selectedFileName);
						
						String line;
						while((line=is.readLine())!=null){
							writer.write(line+"\n");
						}
						
						writer.flush();
						writer.close();
					}catch(Exception ex){
						LogUtils.report(ex);
					}
					
					fileInputBox.setText(selectedFileName);
					theWorkspace.getDictionaryConfig().setDictionaryFile(configKey, selectedFileName);
					editButton.setEnabled(!theWorkspace.getDictionaryConfig().isDefault(configKey));
				}
			}
		});
		
		fileOpenButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				int returnValue=fileChooser.showDialog(self, "Open");
				
				if(returnValue==JFileChooser.APPROVE_OPTION){
					String selectedFileName=fileChooser.getSelectedFile().getPath();
					
					fileInputBox.setText(selectedFileName);
					theWorkspace.getDictionaryConfig().setDictionaryFile(configKey, selectedFileName);
					editButton.setEnabled(!theWorkspace.getDictionaryConfig().isDefault(configKey));
				}
			}
		});
		
		restoreButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				String defaultDictionary=DictionaryConfiguration.getDefaultDictionaryFile(configKey);
				
				fileInputBox.setText(defaultDictionary);
				theWorkspace.getDictionaryConfig().setDictionaryFile(configKey, defaultDictionary);
				editButton.setEnabled(!theWorkspace.getDictionaryConfig().isDefault(configKey));
			}
		});
		
		editButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				try{
					String contents=theWorkspace.getDictionaryConfig().getDictionaryResourceAsString(configKey);
					
					final JFrame dialog=new JFrame();
					dialog.setTitle("Editor: "+theWorkspace.getDictionaryConfig().getDictionaryFile(configKey));
//					JPanel panel=new JPanel();
//					panel.setLayout(new GridBagLayout());
					
					final JTextArea contentPanel=new JTextArea();
					contentPanel.setText(contents);
					
					JButton saveButton=new JButton("Save");
					
					saveButton.addActionListener(new ActionListener(){
						@Override
						public void actionPerformed(ActionEvent e){
							if(theWorkspace.getDictionaryConfig().isRemoteResource(configKey)){
								JOptionPane.showMessageDialog(self, "Current resource is remote\nYou will now be prompted to save the resource locally");
								
								JFileChooser fileChooser=new JFileChooser();
							
								int returnValue=fileChooser.showDialog(self, "Save");
								
								if(returnValue==JFileChooser.APPROVE_OPTION){
									String selectedFileName=fileChooser.getSelectedFile().getPath();
									
									if(new File(selectedFileName).exists()){
										returnValue=JOptionPane.showConfirmDialog(dialog, "File exists overwrite?");
										if(returnValue!=JOptionPane.YES_OPTION){
											return;
										}
									}
									
									theWorkspace.getDictionaryConfig().setDictionaryFile(configKey, selectedFileName);
								}else{
									return;
								}
							}
							
							try{
								FileWriter fileWriter=new FileWriter(new File(theWorkspace.getDictionaryConfig().getDictionaryFile(configKey)));
								fileWriter.write(contentPanel.getText());
								fileWriter.flush();
								fileWriter.close();
							}catch(IOException e1){
								LogUtils.report(e1);
							}
						}
					});
					
//					GridBagConstraints c = new GridBagConstraints();
//					c.gridx=0;
//					c.gridy=0;
//					c.anchor=GridBagConstraints.CENTER;
//					c.weightx=0.5;
//					c.weighty=0.5;
//					panel.add(contentPanel,c);
					
//					GridBagConstraints c1 = new GridBagConstraints();
//					c1.gridx=0;
//					c1.gridy=1;
//					c1.anchor=GridBagConstraints.CENTER;
//					c1.weightx=0.5;
//					c1.weighty=0.5;
					//panel.add(saveButton,c1);
					
					JScrollPane scrollPanel = new JScrollPane(contentPanel);
					
					JPanel panel1=new JPanel();
					panel1.setLayout(new BorderLayout());
					panel1.add(scrollPanel,BorderLayout.CENTER);
					panel1.add(saveButton,BorderLayout.SOUTH);
					
					dialog.add(panel1);
					dialog.setVisible(true);
					dialog.setSize(900, 500);
				}catch(Exception e1){
					LogUtils.report(e1);
				}
			}
		});
		
		JPanel buttonPanel=new JPanel();
		
		buttonPanel.add(fileChooserButton);
		buttonPanel.add(fileOpenButton);
		buttonPanel.add(restoreButton);
		buttonPanel.add(editButton);
		
		editButton.setEnabled(!theWorkspace.getDictionaryConfig().isDefault(configKey));
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx=0;
		c.gridy=row;
		c.anchor=GridBagConstraints.LINE_START;
		c.weightx=0.5;
		c.weighty=0.5;
		
		parent.add(label,c);
		
		GridBagConstraints c1 = new GridBagConstraints();
		c1.gridx=1;
		c1.gridy=row;
		c1.anchor=GridBagConstraints.LINE_START;
		c1.weightx=0.5;
		c1.weighty=0.5;
		//c1.fill=GridBagConstraints.HORIZONTAL;
		parent.add(fileInputBox,c1);
		
		GridBagConstraints c2 = new GridBagConstraints();
		c2.gridx=2;
		c2.gridy=row;
		c2.weightx=0.5;
		c2.weighty=0.5;
		
		parent.add(buttonPanel,c2);
		
		//return panel;
	}
	

	private JCheckBox exportRedEndCheckBox;
	private JCheckBox exportMassesCheckBox;

	public void initExportMenu() {
		RibbonApplicationMenuEntryPrimary saveItem = new RibbonApplicationMenuEntryPrimary(
				themeManager.getResizableIcon("export", ICON_SIZE.L7)
						.getResizableIcon(), "Export to", null,
				CommandButtonKind.ACTION_ONLY);

		final GlycoWorkbench self = this;

		saveItem.setRolloverCallback(new RibbonApplicationMenuEntryPrimary.PrimaryRolloverCallback() {
			@Override
			public void menuEntryActivated(JPanel targetPanel) {
				targetPanel.removeAll();

				JCommandButtonPanel openTypes = new JCommandButtonPanel(
						CommandButtonDisplayState.MEDIUM);
				openTypes.addButtonGroup("Export to sequence format");

				for (java.util.Map.Entry<String, String> e : GlycanDocument
						.getExportFormats().entrySet()) {
					JCommandButtonAction button = new JCommandButtonAction(e
							.getKey(), null);
					button.setActionCommand("export=" + e.getKey());
					button.setHorizontalAlignment(SwingUtilities.LEFT);
					button.addActionListener(self);
					openTypes.addButtonToLastGroup(button);
				}

				openTypes.addButtonGroup("Export to graphical format");

				for (java.util.Map.Entry<String, String> e : SVGUtils
						.getExportFormats().entrySet()) {
					JCommandButtonAction button = new JCommandButtonAction(e
							.getKey(), null);
					button.setActionCommand("export=" + e.getKey());
					button.setHorizontalAlignment(SwingUtilities.LEFT);
					button.addActionListener(self);
					openTypes.addButtonToLastGroup(button);
				}

				openTypes.addButtonGroup("Settings");

				openTypes.setMaxButtonColumns(2);
				openTypes.setMaxButtonRows(2);

				JPanel panel = new JPanel();
				panel.setLayout(new GridLayout(2,1));

				if (exportRedEndCheckBox == null) {
					exportRedEndCheckBox = theCanvas
							.getTheActionManager()
							.get("showredend")
							.getJCheckBox("Export reducing end indicator",
									theCanvas);

					exportMassesCheckBox = theCanvas.getTheActionManager()
							.get("showmasses")
							.getJCheckBox("Export mass values", theCanvas);

					theCanvas
							.getTheActionManager()
							.get("showmasses")
							.setSelected(
									theCanvas.getTheGlycanRenderer()
											.getGraphicOptions().SHOW_MASSES);
					theCanvas
							.getTheActionManager()
							.get("showredend")
							.setSelected(
									theCanvas.getTheGlycanRenderer()
											.getGraphicOptions().SHOW_REDEND);
				}

				panel.add(exportRedEndCheckBox);
				panel.add(exportMassesCheckBox);

				JPanel main = new JPanel();
				main.setLayout(new BorderLayout());
				main.add(openTypes, BorderLayout.NORTH);
				main.add(panel, BorderLayout.CENTER);

				JScrollPane scrollPane = new JScrollPane(main);
				scrollPane.setSize(targetPanel.getSize());

				targetPanel.setLayout(new BorderLayout());
				targetPanel.add(scrollPane, BorderLayout.CENTER);

			}
		});

		applicationMenu.addMenuEntry(saveItem);

	}

	public void initImportMenu() {

		RibbonApplicationMenuEntryPrimary saveItem = new RibbonApplicationMenuEntryPrimary(
				themeManager.getResizableIcon("import", ICON_SIZE.L4)
						.getResizableIcon(), "Import from", null,
				CommandButtonKind.ACTION_ONLY);

		final GlycoWorkbench self = this;

		saveItem.setRolloverCallback(new RibbonApplicationMenuEntryPrimary.PrimaryRolloverCallback() {
			@Override
			public void menuEntryActivated(JPanel targetPanel) {
				targetPanel.removeAll();

				JCommandButtonPanel openTypes = new JCommandButtonPanel(
						CommandButtonDisplayState.MEDIUM);
				openTypes.addButtonGroup("Import from sequence format");

				for (java.util.Map.Entry<String, String> e : GlycanDocument
						.getImportFormats().entrySet()) {
					JCommandButtonAction button = new JCommandButtonAction(e
							.getKey(), null);
					button.setActionCommand("import=" + e.getKey());
					button.setHorizontalAlignment(SwingUtilities.LEFT);
					button.addActionListener(self);
					openTypes.addButtonToLastGroup(button);
				}

				openTypes.setMaxButtonColumns(2);
				openTypes.setMaxButtonRows(2);

				JScrollPane scrollPane = new JScrollPane(openTypes);
				scrollPane.setSize(targetPanel.getSize());

				targetPanel.setLayout(new BorderLayout());
				targetPanel.add(scrollPane, BorderLayout.CENTER);
			}
		});

		applicationMenu.addMenuEntry(saveItem);
	}

	public void initRibbon() {

	}

	public void exit(int err_level) throws IOException {
		cleanup(err_level);
		System.exit(err_level);
	}

	public void cleanup(int err_level) throws IOException {
		// save configurations
		thePluginManager.exit();
		theWorkspace.exit(this.getConfigurationFile().toString());

		// clear memory
		theWorkspace.init();

		System.gc();
		System.runFinalization();
	}

	public GlycanWorkspace getWorkspace() {
		return theWorkspace;
	}

	public PluginManager getPluginManager() {
		return thePluginManager;
	}

	public GlycanCanvas getCanvas() {
		return theCanvas;
	}

	public void hideTopPanels() {
		theLeftSplitPane.setDividerLocation(0.);
	}

	public void hideLeftPanels() {
		theTopSplitPane.setDividerLocation(0.);
	}

	public void showLeftPanels() {
		if (theTopSplitPane.getDividerLocation() < (0.1 * theTopSplitPane
				.getSize().width))
			theTopSplitPane.setDividerLocation(0.3);
	}

	public void toggleLeftPanels() {
		if (theTopSplitPane.getDividerLocation() < (0.1 * theTopSplitPane
				.getSize().width))
			theTopSplitPane.setDividerLocation(0.3);
		else
			theTopSplitPane.setDividerLocation(0.);
	}

	public void hideBottomPanels() {
		theLeftSplitPane.setDividerLocation(1.);
	}

	public void showBottomPanels() {
		if (theLeftSplitPane.getDividerLocation() > (0.9 * theLeftSplitPane
				.getSize().height))
			theLeftSplitPane.setDividerLocation(0.6);
	}

	public void toggleBottomPanels() {
		if (theLeftSplitPane.getDividerLocation() > (0.9 * theLeftSplitPane
				.getSize().height))
			theLeftSplitPane.setDividerLocation(0.6);
		else
			theLeftSplitPane.setDividerLocation(1.);
	}

	public void hideRightPanels() {
		theSplitPane.setDividerLocation(1.);
	}

	public void showRightPanels() {
		// theSplitPane.resetToPreferredSizes();
		if (theSplitPane.getDividerLocation() > (0.9 * theSplitPane.getSize().width))
			theSplitPane.setDividerLocation(0.6);
	}

	public void toggleRightPanels() {
		if (theSplitPane.getDividerLocation() > (0.9 * theSplitPane.getSize().width))
			theSplitPane.setDividerLocation(0.6);
		else
			theSplitPane.setDividerLocation(1.);
	}

	public void hideAllLeftPanels() {
		theSplitPane.setDividerLocation(0.);
	}

	public void haltInteractions() {

		// display the wait cursor and block user input
		if (halt_interactions.isFree()) {
			Component glassPane = getGlassPane();
			glassPane.addMouseListener(new MouseAdapter() {
			});
			glassPane.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			glassPane.setVisible(true);
		}
		halt_interactions.hold();
	}

	public void restoreInteractions() {

		// restore normal user interaction
		halt_interactions.release();
		if (halt_interactions.isFree())
			getGlassPane().setVisible(false);
	}

	private void createActions() {

		// cores
		theActionManager2.add("empty",
				ThemeManager.getResizableEmptyIcon(ICON_SIZE.L3), "Empty", -1,
				"", this);
		for (CoreType t : CoreDictionary.getCores())
			theActionManager2.add("new=" + t.getName(),
					ThemeManager.getResizableEmptyIcon(ICON_SIZE.L3),
					t.getDescription(), -1, "", this);

		// workspace
		theActionManager2.add("newall", themeManager.getResizableIcon(
				STOCK_ICON.REFRESH, defaultMenuIconSize),
				"Clear the workspace", KeyEvent.VK_C, "", this);
		theActionManager2.add("openall", themeManager.getResizableIcon(
				STOCK_ICON.DOCUMENT_OPEN, defaultMenuIconSize),
				"Open a workspace", KeyEvent.VK_P, "", this); // done,ribbon app
		// bar
		// System.err.println("saveall");
		theActionManager2.add("saveall", themeManager.getResizableIcon(
				STOCK_ICON.DOCUMENT_SAVE, defaultMenuIconSize),
				"Save the workspace", KeyEvent.VK_S, "ctrl S", this); // done,ribbon
		// System.exit(0);
		// app bar
		theActionManager2.add("saveallas", themeManager.getResizableIcon(
				STOCK_ICON.DOCUMENT_SAVE_AS, defaultMenuIconSize),
				"Save the workspace as...", KeyEvent.VK_E, "", this);// done,ribbon
		// app
		// bar

		// file

		theActionManager2.add("new", themeManager.getResizableIcon(
				STOCK_ICON.DOCUMENT_NEW, defaultMenuIconSize), "New",
				KeyEvent.VK_N, "ctrl N", this);
		theActionManager2.add("open", themeManager.getResizableIcon(
				STOCK_ICON.DOCUMENT_OPEN, defaultMenuIconSize),
				"Open document...", KeyEvent.VK_O, "ctrl O", this); // done,ribbon
		// app bar
		theActionManager2.add("openinto", themeManager.getResizableIcon(
				STOCK_ICON.DOCUMENT_OPEN_ADDITIONAL, defaultMenuIconSize),
				"Open additional document...", KeyEvent.VK_I, "ctrl I", this);// done,ribbon
		// app
		// bar

		theActionManager2.add("save", themeManager.getResizableIcon(
				STOCK_ICON.DOCUMENT_SAVE, defaultMenuIconSize),
				"Save the structures", KeyEvent.VK_S, "ctrl S", this);// done,ribbon
		// app
		// bar
		theActionManager2.add("saveas", themeManager.getResizableIcon(
				STOCK_ICON.DOCUMENT_SAVE_AS, defaultMenuIconSize),
				"Save the structures as...", KeyEvent.VK_A, "shift ctrl S",
				this);// done,ribbon app bar
		theActionManager2.add("print", themeManager.getResizableIcon(
				STOCK_ICON.DOCUMENT_PRINT, defaultMenuIconSize),
				"Print the structures...", KeyEvent.VK_P, "ctrl P", this);
		theActionManager2.add("quit", themeManager.getResizableIcon(
				STOCK_ICON.QUIT, defaultMenuIconSize), "Quit", KeyEvent.VK_Q,
				"ctrl Q", this);

		// import/export
		for (java.util.Map.Entry<String, String> e : GlycanDocument
				.getImportFormats().entrySet())
			theActionManager2.add("import=" + e.getKey(),
					ThemeManager.getResizableEmptyIcon(ICON_SIZE.L4),
					"Import from " + e.getValue() + "...", -1, "", this);
		for (java.util.Map.Entry<String, String> e : GlycanDocument
				.getExportFormats().entrySet())
			theActionManager2.add("export=" + e.getKey(),
					ThemeManager.getResizableEmptyIcon(ICON_SIZE.L4),
					"Export to " + e.getValue() + "...", -1, "", this);
		for (java.util.Map.Entry<String, String> e : SVGUtils
				.getExportFormats().entrySet())
			theActionManager2.add("export=" + e.getKey(),
					ThemeManager.getResizableEmptyIcon(ICON_SIZE.L4),
					"Export to " + e.getValue() + "...", -1, "", this);

		// panes
		// theActionManager2.add("toggleleft", FileUtils.getIcon("toggleleft"),
		// "Show/hide left panels", -1, "", this);
		// theActionManager2.add("togglebottom",
		// FileUtils.getIcon("togglebottom"),
		// "Show/hide bottom panels", -1, "", this);
		// theActionManager2.add("toggleright",
		// FileUtils.getIcon("toggleright"),
		// "Show/hide right panels", -1, "", this);

		// tools
		// theActionManager2.add("massopt", FileUtils.getIcon("massopt"),
		// "Mass options of all structures", KeyEvent.VK_M, "", this);
		// theActionManager2.add("fragopt", FileUtils.getIcon("fragopt"),
		// "Fragment options", KeyEvent.VK_O, "", this);
		// theActionManager2.add("annopt", FileUtils.getIcon("annopt"),
		// "Annotation options", KeyEvent.VK_N, "", this);

		// help
		theActionManager2.add("help", themeManager.getResizableIcon(
				STOCK_ICON.HELP_CONTENTS, defaultMenuIconSize), "Help content",
				KeyEvent.VK_H, "F1", this);
		theActionManager2.add("about", themeManager.getResizableIcon(
				STOCK_ICON.HELP_ABOUT, defaultMenuIconSize), "About",
				KeyEvent.VK_B, "", this);

	}

	private void updateActions() {
		theActionManager2.get("save").setEnabled(theDoc.hasChanged());
	}

	private JMenuBar createMenuBar() {

		JMenuBar menubar = new JMenuBar();

		menubar.add(theCanvas.getEditMenu());
		menubar.add(theCanvas.getStructureMenu());
		menubar.add(createToolsMenu());
		menubar.add(theCanvas.getViewMenu());

		// help menu
		JMenu help_menu = new JMenu("Help");
		help_menu.setMnemonic(KeyEvent.VK_H);
		help_menu.add(theActionManager.get("help"));
		help_menu.addSeparator();
		help_menu.add(theActionManager.get("about"));
		menubar.add(help_menu);

		return menubar;
	}

	protected void createToolsRibbonTask() {

		final HashMap<JCommandMenuButton, JCommandPopupMenu> buttons = new HashMap<JCommandMenuButton, JCommandPopupMenu>();

		ArrayList<AbstractRibbonBand> bands = new ArrayList<AbstractRibbonBand>();
		JRibbonBand band = new JRibbonBand(
				"Tools",
				new org.pushingpixels.flamingo.api.common.icon.EmptyResizableIcon(
						10));
		for (Plugin p : thePluginManager.getPlugins()) {
			boolean addDefault = true;
			if (p.getBandsForToolBar() != null) {
				bands.addAll(p.getBandsForToolBar());
				addDefault = false;
			}

			if (p.getRibbonTask() != null) {
				this.getRibbon().addTask(p.getRibbonTask());
				addDefault = false;
			}

			if (addDefault) {

				if (p.getActions().size() > 0) {
					JCommandMenuButton but;
					try {
						Method method = p.getClass().getMethod(
								"getResizableIcon", null);
						but = new JCommandMenuButton(p.getName(),
								(ResizableIcon) method.invoke(p, null));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						but = new JCommandMenuButton(p.getName(), ThemeManager
								.getResizableEmptyIcon(ICON_SIZE.L3)
								.getResizableIcon());
					}

					JCommandPopupMenu menu = new JCommandPopupMenu();

					buttons.put(but, menu);

					for (GlycanAction a : p.getActions()) {
						if (a == null) {
							menu.addMenuSeparator();
						} else {
							menu.addMenuButton(a.getJCommandMenuButton());
						}
					}

					but.setPopupCallback(new PopupPanelCallback() {

						@Override
						public JPopupPanel getPopupPanel(
								JCommandButton commandButton) {
							// TODO Auto-generated method stub

							// JCommandPopupMenu menu=new JCommandPopupMenu();
							// menu.addMenuButton(new JCommandMenuButton("test",
							// ThemeManager.getResizableEmptyIcon(ICON_SIZE.L3).getResizableIcon()));

							// return menu;
							return buttons.get(commandButton);
						}

					});

					band.addCommandButton(but, RibbonElementPriority.TOP);

				}

			}
		}

		// JCommandButton but=new JCommandButton("Test1",new
		// org.pushingpixels.flamingo.api.common.icon.EmptyResizableIcon(10));
		// band.addCommandButton(but,RibbonElementPriority.TOP);
		// JCommandButton but1=new JCommandButton("Test2",new
		// org.pushingpixels.flamingo.api.common.icon.EmptyResizableIcon(10));
		// band.addCommandButton(but1,RibbonElementPriority.TOP);

		// bands.add(band);,bands.toArray(new AbstractRibbonBand[1])
		this.getRibbon().addTask(new RibbonTask("Tools", band));
	}

	protected JMenu createToolsMenu() {

		JMenu tools_menu = new JMenu("Tools");
		tools_menu.setMnemonic(KeyEvent.VK_T);

		for (Plugin p : thePluginManager.getPlugins()) {
			if (p.getActions().size() > 0) {
				JMenu plugin_menu = new JMenu(p.getName());
				plugin_menu.setMnemonic(p.getMnemonic());
				plugin_menu.setIcon(p.getIcon());
				for (GlycanAction a : p.getActions()) {
					if (a == null)
						plugin_menu.addSeparator();
					else
						plugin_menu.add(a);
				}

				tools_menu.add(plugin_menu);
			}
		}

		tools_menu.addSeparator();

		tools_menu.add(theActionManager.get("massopt"));
		tools_menu.add(theActionManager.get("fragopt"));
		tools_menu.add(theActionManager.get("annopt"));

		return tools_menu;
	}

	protected JPopupMenu createPopupMenu() {
		// if(tools_menu==null){
		// tools_menu = createToolsMenu();
		// tools_menu.setIcon(ThemeManager.getEmptyIcon(ICON_SIZE.TINY));
		// }

		return theCanvas.createPopupMenu();
	}

	private void createFileBand() {
		JRibbonBand band = new JRibbonBand(
				"Workspace",
				new org.pushingpixels.flamingo.api.common.icon.EmptyResizableIcon(
						10));

		band.addCommandButton(
				theActionManager2.get("newall").getJCommandButton(ICON_SIZE.L3,
						"Clear", this, new RichTooltip("Clear", " ")),
				RibbonElementPriority.TOP);
		band.addCommandButton(
				theActionManager2.get("openall").getJCommandButton(
						ICON_SIZE.L3, "Open", this,
						new RichTooltip("Open workspace", " ")),
				RibbonElementPriority.TOP);
		band.addCommandButton(
				theActionManager2.get("saveall").getJCommandButton(
						ICON_SIZE.L3, "Save", this,
						new RichTooltip("Save workspace", " ")),
				RibbonElementPriority.TOP);
		band.addCommandButton(
				theActionManager2.get("saveallas").getJCommandButton(
						ICON_SIZE.L3, "Save as", this,
						new RichTooltip("Save workspace as", " ")),
				RibbonElementPriority.TOP);
		band.addCommandButton(
				theActionManager2.get("new").getJCommandButton(ICON_SIZE.L3,
						"New", this, new RichTooltip("New workspace", " ")),
				RibbonElementPriority.TOP);

		ArrayList<RibbonBandResizePolicy> resizePolicies1 = new ArrayList<RibbonBandResizePolicy>();
		resizePolicies1.add(new CoreRibbonResizePolicies.Mirror(band
				.getControlPanel()));
		resizePolicies1.add(new CoreRibbonResizePolicies.Mid2Low(band
				.getControlPanel()));

		resizePolicies1.add(new IconRibbonBandResizePolicy(band
				.getControlPanel()));

		band.setResizePolicies(resizePolicies1);

		JRibbonBand band2 = new JRibbonBand(
				"Structures",
				new org.pushingpixels.flamingo.api.common.icon.EmptyResizableIcon(
						10));
		ArrayList<RibbonBandResizePolicy> resizePolicies = new ArrayList<RibbonBandResizePolicy>();
		resizePolicies.add(new CoreRibbonResizePolicies.Mirror(band2
				.getControlPanel()));
		resizePolicies.add(new CoreRibbonResizePolicies.Mid2Low(band2
				.getControlPanel()));

		resizePolicies.add(new IconRibbonBandResizePolicy(band2
				.getControlPanel()));

		band2.setResizePolicies(resizePolicies);

		band2.addCommandButton(
				theActionManager2.get("open").getJCommandButton(ICON_SIZE.L3,
						"Open", this, new RichTooltip("Open", "")),
				RibbonElementPriority.TOP);
		band2.addCommandButton(
				theActionManager2.get("openinto").getJCommandButton(
						ICON_SIZE.L3, "Insert", this,
						new RichTooltip("Open into workspace", " ")),
				RibbonElementPriority.TOP);
		band2.addCommandButton(
				theActionManager2.get("save").getJCommandButton(ICON_SIZE.L3,
						"Save", this, new RichTooltip("Save structures", " ")),
				RibbonElementPriority.TOP);
		band2.addCommandButton(
				theActionManager2.get("saveas").getJCommandButton(ICON_SIZE.L3,
						"Save as", this,
						new RichTooltip("Save structures as", " ")),
				RibbonElementPriority.TOP);

		JRibbonBand band4 = new JRibbonBand(
				"Tools",
				new org.pushingpixels.flamingo.api.common.icon.EmptyResizableIcon(
						10));
		ArrayList<RibbonBandResizePolicy> resizePolicies4 = new ArrayList<RibbonBandResizePolicy>();
		resizePolicies4.add(new CoreRibbonResizePolicies.Mirror(band4
				.getControlPanel()));
		resizePolicies4.add(new CoreRibbonResizePolicies.Mid2Low(band4
				.getControlPanel()));

		resizePolicies4.add(new IconRibbonBandResizePolicy(band4
				.getControlPanel()));

		band4.setResizePolicies(resizePolicies4);

		{
			ProfilerPlugin plugin = (ProfilerPlugin) thePluginManager
					.get(ProfilerPlugin.PLUGIN_NAME);
			GlycanAction gAction = plugin.getAction("matchDatabase");

			JCommandButton button = gAction.getJCommandButton(ICON_SIZE.L3,
					"Annotate from DB", plugin, new RichTooltip(
							"Database annotate",
							"Annotation peaks from database"));

			band4.addCommandButton(button, RibbonElementPriority.TOP);
		}

		{
			AnnotationPlugin plugin = (AnnotationPlugin) thePluginManager
					.get(AnnotationPlugin.PLUGIN_NAME);
			GlycanAction gAction = plugin.getAction("matchFragments");

			JCommandButton button = gAction
					.getJCommandButton(
							ICON_SIZE.L3,
							"Annotate from Canvas",
							plugin,
							new RichTooltip("Canvas Annotation",
									"Annotation peaks with structures from glycan canvas"));

			band4.addCommandButton(button, RibbonElementPriority.TOP);
		}

		{
			ReportingPlugin plugin = (ReportingPlugin) thePluginManager
					.get(ReportingPlugin.PLUGIN_NAME);
			GlycanAction gAction = plugin.getAction("reportAnnotations");

			JCommandButton button = gAction.getJCommandButton(ICON_SIZE.L3,
					"Report", plugin, new RichTooltip("Report",
							"Generate annotation report"));

			band4.addCommandButton(button, RibbonElementPriority.TOP);
		}

		{
			FragmentsPlugin plugin = (FragmentsPlugin) thePluginManager
					.get(FragmentsPlugin.PLUGIN_NAME);
			GlycanAction gAction = plugin.getAction("computeFragments");

			JCommandButton button = gAction.getJCommandButton(ICON_SIZE.L3,
					"Fragment", plugin, new RichTooltip("Fragment",
							"In silco fragmentation"));

			band4.addCommandButton(button, RibbonElementPriority.TOP);
		}

		JFlowRibbonBand band3 = new JFlowRibbonBand(
				"Information",
				new org.pushingpixels.flamingo.api.common.icon.EmptyResizableIcon(
						10));
		status=new JLabel("Checking for updates");
		

		band3.addFlowComponent(status);
		band3.addFlowComponent(new JLabel("OS: "
				+ System.getProperty("os.name")));

		// band3.addFlowComponent(new JLabel(upToDate));

		ArrayList<RibbonBandResizePolicy> resizePolicies3 = new ArrayList<RibbonBandResizePolicy>();
		resizePolicies3.add(new CoreRibbonResizePolicies.FlowTwoRows(band3
				.getControlPanel()));
		resizePolicies3.add(new IconRibbonBandResizePolicy(band3
				.getControlPanel()));

		band3.setResizePolicies(resizePolicies3);
		// /
		RibbonTask task = new RibbonTask("Home", band, band2, band4, band3);

		this.getRibbon().addTask(task);
	}

	private void createEditBand() {
		RibbonTask editBand = this.theCanvas.getTheEditRibbon();

		HashSet<Context> contexts = new HashSet<Context>();
		contexts.add(Context.GLYCAN_CANVAS_ITEM);
		ribbonManager.registerContextSupport(editBand, contexts);

		this.getRibbon().addTask(editBand);
	}

	private void createLinkageBand() {
		RibbonContextualTaskGroup group = this.theCanvas.getTheLinkageRibbon();

		HashSet<Context> contexts = new HashSet<Context>();
		contexts.add(Context.GLYCAN_CANVAS_ITEM);

		ribbonManager.registerContextSupport(group.getTask(0), contexts);
		ribbonManager.setRibbonAsDefault(group.getTask(0),
				Context.GLYCAN_CANVAS_ITEM);

		this.getRibbon().addContextualTaskGroup(group);
	}

	private void createViewBand() {
		this.getRibbon().addTask(this.theCanvas.getTheViewRibbon());
	}

	private void createStructureBand() {
		RibbonTask structureBand = this.theCanvas.getTheStructureRibbon();

		HashSet<Context> contexts = new HashSet<Context>();
		contexts.add(Context.GLYCAN_CANVAS_ITEM);
		ribbonManager.registerContextSupport(structureBand, contexts);

		this.getRibbon().addTask(structureBand);
	}

	private JToolBar createToolBarPanes() {

		JToolBar toolbar = new JToolBar();
		toolbar.setFloatable(false);

		toolbar.add(theActionManager.get("toggleleft"));
		toolbar.add(theActionManager.get("togglebottom"));
		toolbar.add(theActionManager.get("toggleright"));

		return toolbar;
	}

	private JToolBar createToolBarTools() {
		JToolBar toolbar = new JToolBar();
		toolbar.setFloatable(false);

		boolean first = true;
		for (Plugin p : thePluginManager.getPlugins()) {
			if (p.getToolbarActions().size() > 0) {
				if (first) {
					toolbar.addSeparator();
					first = false;
				}

				for (GlycanAction a : p.getToolbarActions()) {
					if (a != null)
						toolbar.add(a);
				}
			}
		}

		return toolbar;
	}

	public String askName(String what) {
		return JOptionPane.showInputDialog(this, "Insert " + what + " name:",
				"", JOptionPane.QUESTION_MESSAGE);
	}

	// ----------------------------
	// Document handling actions

	public void updateTitle() {
		String title = FileHistory.getAbbreviatedName(theWorkspace
				.getFileName()) + " - GlycoWorkbench";
		if (theWorkspace.hasChanged())
			title = "* " + title;
		setTitle(title);
	}

	private File getLastExportedFile() {
		if (last_exported_file != null && last_exported_file.length() > 0) {
			return new File(last_exported_file);
		}
		return null;
	}

	private void setLastExportedFile(String name) {
		last_exported_file = name;
	}

	public boolean checkDocumentChanges(BaseDocument doc) {
		if (doc.hasChanged() && !doc.isEmpty()) {
			int ret = JOptionPane.showConfirmDialog(this, "Save changes to "
					+ doc.getName().toLowerCase() + "?", null,
					JOptionPane.YES_NO_CANCEL_OPTION,
					JOptionPane.QUESTION_MESSAGE);
			if (ret == JOptionPane.CANCEL_OPTION)
				return false;
			if (ret == JOptionPane.YES_OPTION) {
				if (!onSaveAs(doc))
					return false;
				return true;
			}
			if (ret == JOptionPane.NO_OPTION)
				return true;
			return false;
		}
		return true;
	}

	public boolean checkWorkspaceChanges() {
		Collection<BaseDocument> unsaved_docs = theWorkspace
				.getUnsavedDocuments();
		if (unsaved_docs.size() > 0) {
			int ret = JOptionPane.showConfirmDialog(this,
					"Save changes to modified documents?", null,
					JOptionPane.YES_NO_CANCEL_OPTION,
					JOptionPane.QUESTION_MESSAGE);
			if (ret == JOptionPane.CANCEL_OPTION)
				return false;
			if (ret == JOptionPane.YES_OPTION) {
				for (BaseDocument doc : unsaved_docs)
					onSave(doc, false);
				return true;
			}
			if (ret == JOptionPane.NO_OPTION)
				return true;
			return false;
		}
		return true;
	}

	public boolean checkExisting(String filename) {
		if (!(new File(filename)).exists()) {
			JOptionPane.showMessageDialog(this,
					"The file selected is not existing.", "File not found",
					JOptionPane.ERROR_MESSAGE);
			theWorkspace.getFileHistory().remove(filename);
			return false;
		}
		return true;
	}

	public boolean onNew(BaseDocument doc) {
		if (doc == null)
			return false;

		if (!checkDocumentChanges(doc))
			return false;

		// init document
		doc.init();
		return true;
	}

	public boolean onNew(String name) {
		try {
			if (!checkDocumentChanges(theDoc))
				return false;

			// add structure from template
			if (name != null && name.length() > 0)
				return theDoc.init(CoreDictionary.getCoreType(name)
						.getStructure());

			theDoc.init();
			return true;
		} catch (Exception e) {
			LogUtils.report(e);
			return false;
		}
	}

	private boolean fileFiltersEqual(FileFilter src, FileFilter dest) {
		return src.getDescription().equals(dest.getDescription()) ? true
				: false;
	}

	public boolean tryOpen(String filename, boolean merge,
			FileFilter selectedFilter) {

		try {
			if (!checkExisting(filename))
				return false;

			File file = new File(filename);

			FileFilter fileFormats = theWorkspace.getSpectra()
					.getAllFileFormats();
			if (fileFiltersEqual(selectedFilter, fileFormats)
					&& fileFormats.accept(file)) {
				if (theWorkspace.getSpectra().open(filename, merge, false)) {
					theWorkspace.getFileHistory().add(filename,
							theWorkspace.getSpectra().getName());
					System.err.println("Opening spectra");
					thePluginManager.show("Spectra", "Spectra");
					return true;
				}
			}

			fileFormats = theWorkspace.getStructures().getAllFileFormats();
			if (fileFiltersEqual(selectedFilter, fileFormats)
					&& fileFormats.accept(file)) {
				if (theWorkspace.getStructures().open(filename, merge, false)) {
					theWorkspace.getFileHistory().add(filename,
							theWorkspace.getStructures().getName());
					return true;
				}
			}

			fileFormats = theWorkspace.getAnnotatedPeakList()
					.getAllFileFormats();
			if (fileFiltersEqual(selectedFilter, fileFormats)
					&& fileFormats.accept(file)) {
				if (theWorkspace.getAnnotatedPeakList().open(filename, merge,
						false)) {
					theWorkspace.getFileHistory().add(filename,
							theWorkspace.getAnnotatedPeakList().getName());
					thePluginManager.show("Annotation", "Summary");
					return true;
				}
			}

			fileFormats = theWorkspace.getPeakList().getAllFileFormats();
			if (fileFiltersEqual(selectedFilter, fileFormats)
					&& fileFormats.accept(file)) {
				if (theWorkspace.getPeakList().open(filename, merge, false)) {
					theWorkspace.getFileHistory().add(filename,
							theWorkspace.getPeakList().getName());
					thePluginManager.show("PeakList", "PeakList");
					return true;
				}
			}

			fileFormats = theWorkspace.getAllFileFormats();
			if (fileFiltersEqual(selectedFilter, fileFormats)
					&& fileFormats.accept(file)) {
				// try to open one document
				if (theWorkspace.open(filename, merge, false)) {
					theWorkspace.getFileHistory().add(filename,
							theWorkspace.getName());
					System.err.println("Opening workspace");
					return true;
				}
			}

			throw new Exception("Unrecognized file format");
		} catch (Exception e) {
			LogUtils.report(e);
			return false;
		}
	}

	public boolean onOpenDocument(Collection<BaseDocument> documents,
			boolean merge) {
		if (documents == null || documents.size() == 0)
			return false;

		// collect file formats
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setAcceptAllFileFilterUsed(false);

		HashMap<javax.swing.filechooser.FileFilter, BaseDocument> all_ff = new HashMap<javax.swing.filechooser.FileFilter, BaseDocument>();
		for (BaseDocument doc : documents) {
			javax.swing.filechooser.FileFilter ff = doc.getAllFileFormats();
			fileChooser.addChoosableFileFilter(ff);
			all_ff.put(ff, doc);
		}

		// open file chooser
		fileChooser.setCurrentDirectory(theWorkspace.getFileHistory()
				.getRecentFolder());
		if (fileChooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION)
			return false;

		// retrieve file path and document type
		String filename = fileChooser.getSelectedFile().getAbsolutePath();
		BaseDocument document = all_ff.get(fileChooser.getFileFilter());

		// open the file
		return onOpen(filename, document, merge);
	}

	/**
	 * There's a missmatch between the old 1.2 GWB and what's been uploaded into
	 * the SVN.
	 * 
	 * - Until I have traced it completely down, there's now an extra parameter
	 * mergeScan, that this method defaults to false to maintain the old
	 * behaviour. Code that wishes to override the default/wrong behaviour
	 * should call this method with the additional mergeScan parameter.
	 * 
	 * @param fileName
	 * @param doc
	 * @param mergeData
	 * @return
	 */
	public boolean onOpen(String fileName, BaseDocument doc, boolean mergeData) {
		return onOpen(fileName, doc, mergeData, false);
	}

	public boolean onOpen(String filename, BaseDocument doc, boolean merge,
			boolean mergeScan) {
		if (doc == null)
			return false;

		if (filename == null) {

			if (doc.getFileFormats().size() == 0)
				return false;

			// imposto la dialog per l'apertura del file
			JFileChooser fileChooser = new JFileChooser();
			for (javax.swing.filechooser.FileFilter ff : doc.getFileFormats())
				fileChooser.addChoosableFileFilter(ff);
			fileChooser.setCurrentDirectory(theWorkspace.getFileHistory()
					.getRecentFolder());

			// visualizzo la dialog
			if (fileChooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION)
				return false;

			// retrieve file path
			filename = fileChooser.getSelectedFile().getAbsolutePath();
		}

		if (!checkExisting(filename))
			return false;
		if (filename.equals(doc.getFileName())) {
			// ask for reload if document has changed
			if (!doc.hasChanged())
				return false;
			int retValue = JOptionPane.showOptionDialog(this,
					"Reload document from the file: " + filename + "?",
					"Load document", JOptionPane.YES_NO_CANCEL_OPTION,
					JOptionPane.QUESTION_MESSAGE, null, null, null);
			if (retValue != JOptionPane.YES_OPTION)
				return false;
		} else if (!merge && !checkDocumentChanges(doc))
			return false;

		// open document
		haltInteractions();
		/*
		 * if( merge && doc.getName().equals("Structures") ) { // add new scan
		 * Scan s = new Scan(theWorkspace); if(
		 * !s.getStructures().open(filename,merge,true) ) {
		 * restoreInteractions(); return false; } theWorkspace.addScan(null,s);
		 * theWorkspace.setCurrentScan(s);
		 * 
		 * } else
		 */

		if (merge && doc.getName().equals("Fragments")) {
			// add new scan
			Scan s = new Scan(theWorkspace);
			if (!s.getFragments().open(filename, merge, true)) {
				restoreInteractions();
				return false;
			}
			theWorkspace.addScan(null, s);
			theWorkspace.setCurrentScan(s);

		} else if (merge && doc.getName().equals("Spectra")) {
			// add new scan
			Scan s = new Scan(theWorkspace);
			if (!s.getSpectra().open(filename, merge, true)) {
				restoreInteractions();
				return false;
			}
			theWorkspace.addScan(null, s);
			theWorkspace.setCurrentScan(s);

		} else if (merge && doc.getName().equals("PeakList")) {
			// add new scan
			System.err.println("Opening peak list");

			Scan s = null;

			if (mergeScan) {
				s = theWorkspace.getCurrentScan();
			} else {
				s = new Scan(theWorkspace);
			}

			if (!s.getPeakList().open(filename, merge, true)) {
				restoreInteractions();
				return false;
			}
			if (!mergeScan) {
				theWorkspace.addScan(null, s);
				theWorkspace.setCurrentScan(s);
			}

		} else if (merge && doc.getName().equals("Annotated PeakList")) {
			// add new scan
			Scan s = new Scan(theWorkspace);
			if (!s.getAnnotatedPeakList().open(filename, merge, true)) {
				restoreInteractions();
				return false;
			}
			theWorkspace.addScan(null, s);
			theWorkspace.setCurrentScan(s);
		} else if (!doc.open(filename, merge, true)) {
			restoreInteractions();
			return false;
		}

		if (doc.getName().equals("Annotation report"))
			theWorkspace.addAnnotationReport((AnnotationReportDocument) doc);

		restoreInteractions();

		// show panel
		try {
			if (doc.getName().equals("Workspace"))
				thePluginManager.show("Workspace", "Workspace");
			else if (doc.getName().equals("Fragments"))
				thePluginManager.show("Fragments", "Summary");
			else if (doc.getName().equals("PeakList"))
				thePluginManager.show("PeakList", "PeakList");
			else if (doc.getName().equals("Annotated PeakList"))
				thePluginManager.show("Annotation", "Summary");
			else if (doc.getName().equals("Spectra"))
				thePluginManager.show("Spectra", "Spectra");
			else if (doc.getName().equals("Notes"))
				thePluginManager.show("Notes", "Notes");
			else if (doc.getName().equals("Annotation report"))
				((ReportingPlugin) this.getPluginManager().get("Reporting"))
						.showAnnotationsReport((AnnotationReportDocument) doc,
								false);
		} catch (Exception e) {
			LogUtils.report(e);
		}

		// update history
		theWorkspace.getFileHistory().add(filename, doc.getName());
		return true;
	}

	public boolean onSave(BaseDocument doc) {
		return onSave(doc, true);
	}

	public boolean onSave(BaseDocument doc, boolean ask_filename) {
		if (doc == null)
			return false;

		File cur = doc.getFile();
		if (cur != null && cur.canWrite()) {
			doc.save(cur.getAbsolutePath());
			return true;
		}

		if (ask_filename)
			return onSaveAs(doc);
		return false;
	}

	public boolean onSaveAs(BaseDocument doc) {
		if (doc == null)
			return false;

		JFileChooser fileChooser = new JFileChooser();
		for (javax.swing.filechooser.FileFilter ff : doc.getFileFormats())
			fileChooser.addChoosableFileFilter(ff);
		fileChooser.setCurrentDirectory(theWorkspace.getFileHistory()
				.getRecentFolder());

		if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
			String filename = fileChooser.getSelectedFile().getAbsolutePath();
			javax.swing.filechooser.FileFilter ff = fileChooser.getFileFilter();
			if (ff != fileChooser.getAcceptAllFileFilter()
					&& (ff instanceof ExtensionFileFilter))
				filename = FileUtils.enforceExtension(filename,
						((ExtensionFileFilter) ff).getDefaultExtension());

			File file = new File(filename);
			if (file.exists()) {
				int retValue = JOptionPane.showOptionDialog(this,
						"File exists. Overwrite file: " + filename + "?",
						"Save document", JOptionPane.YES_NO_CANCEL_OPTION,
						JOptionPane.QUESTION_MESSAGE, null, null, null);
				if (retValue != JOptionPane.YES_OPTION)
					return false;
			}

			if (!doc.save(filename))
				return false;

			// update history
			theWorkspace.getFileHistory().add(filename, doc.getName());
			return true;
		}
		return false;
	}

	public boolean onImportFrom(String format) {
		// imposto la dialog per l'apertura del file
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setCurrentDirectory(theWorkspace.getFileHistory()
				.getRecentFolder());

		// visualizzo la dialog
		if (fileChooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION)
			return false;

		// retrieve file path
		String filename = fileChooser.getSelectedFile().getAbsolutePath();

		// if( filename.equals(theDoc.getFileName()) )
		// return false;
		// if( !checkDocumentChanges(theDoc) )
		// return false;
		if (!checkExisting(filename))
			return false;
		
		// import structures into the document
		theWorkspace.getFileHistory().add(filename, format);
		
		return theDoc.importFrom(filename, format);
	}

	public boolean onExportTo(String format) {
		if (theDoc.getStructures().size() > 1
				&& !theDoc.supportMultipleStructures(format)) {
			int retValue = JOptionPane
					.showOptionDialog(
							this,
							"The selected format does not support multiple structures.\n"
									+ "Only the first structure will be exported. Continue?",
							"Cannot export all structures",
							JOptionPane.YES_NO_OPTION,
							JOptionPane.WARNING_MESSAGE, null, null, null);

			if (retValue != JOptionPane.YES_OPTION)
				return false;
		}

		// imposto la dialog per il salvataggio del file
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.addChoosableFileFilter(new ExtensionFileFilter(format));
		fileChooser.setCurrentDirectory(theWorkspace.getFileHistory()
				.getRecentFolder());

		// visualizzo la dialog
		int returnVal = fileChooser.showSaveDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {

			// aggiunge l'estension
			String filename = fileChooser.getSelectedFile().getAbsolutePath();
			filename = FileUtils.enforceExtension(filename, format);

			// chiede conferma prima di sovrascrivere il file
			File file = new File(filename);
			if (file.exists()) {
				int retValue = JOptionPane.showOptionDialog(this,
						"File exists. Overwrite file: " + filename + "?",
						"Salva documento", JOptionPane.YES_NO_CANCEL_OPTION,
						JOptionPane.QUESTION_MESSAGE, null, null, null);
				if (retValue != JOptionPane.YES_OPTION)
					return false;
			}

			// esporta il documento su file
			if (theDoc.isSequenceFormat(format)) {
				if (theDoc.exportTo(filename, format))
					setLastExportedFile(filename);
					theWorkspace.getFileHistory().add(filename, format);
				return true;
			} else if (SVGUtils.export((GlycanRendererAWT) theWorkspace.getGlycanRenderer(),
					filename, theDoc.getStructures(),
					theWorkspace.getGraphicOptions().SHOW_MASSES,
					theWorkspace.getGraphicOptions().SHOW_REDEND, format)) {
				theWorkspace.getFileHistory().add(filename, format);
				setLastExportedFile(filename);
				return true;
			}
		}
		return false;
	}

	public void onPrint() {
		try {
			PrinterJob pj = theWorkspace.getPrinterJob();
			if (pj != null) {
				pj.setPrintable(theCanvas);
				if (pj.printDialog())
					theCanvas.print(pj);
			}
		} catch (Exception e) {
			LogUtils.report(e);
		}
	}

	public void onExit() throws IOException {
		/*
		 * if( checkDocumentChanges(theWorkspace) &&
		 * checkDocumentChanges(theWorkspace.getStructures()) &&
		 * checkDocumentChanges(theWorkspace.getPeakList()) &&
		 * checkDocumentChanges(theWorkspace.getAnnotatedPeakList()) )
		 */
		System.err.println("On exit has been called");
		if (checkDocumentChanges(theWorkspace) && checkWorkspaceChanges())
			this.exit(0);
	}

	boolean restart = false;
	String skin;
	protected static Object lock = new Object();
	private JSplitPane canvasPanel;
	private JScrollPane canvasScrollPane;
	private JPanel panelghg;
	private JPanel toolBarPanel;
	private JPanel toolBarPanelLinkage;
	private DockableEvent glycanCanvasDockableEvent;
	private double theLastSplitPaneDividerLocation = 0.6;
	private double theLastTopSplitPaneDividerLocation = 0.4;
	private double theLastLeftSplitPaneDividerLocation = 0.5;

	public void restart(String themeManager) {
		// if(JOptionPane.showConfirmDialog(this,
		// "Restart required")==JOptionPane.OK_OPTION){
		// if (checkDocumentChanges(theWorkspace) && checkWorkspaceChanges()){
		try {
			cleanup(0);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		synchronized (lock) {
			for (Frame frame : Frame.getFrames()) {
				frame.dispose();
			}

		}
		restart = true;
		skin = themeManager;
		// }
		// }
	}

	// ------------
	// tools actions

	public void onMassOptionsAllStructures() {
		theCanvas.onMassOptionsAllStructures();
	}

	public void onFragmentOptions() {
		new FragmentOptionsDialog(this, theWorkspace.getFragmentOptions())
				.setVisible(true);
	}

	public void onAnnotationOptions() {
		new AnnotationOptionsDialog(this, theWorkspace.getFragmentOptions(),
				theWorkspace.getAnnotationOptions(), false, false)
				.setVisible(true);
	}

	// ------------------
	// Help actions

	public void onHelp() {
		// JDialog dlg=new JDialog(this,"Help",true);
		// dlg.setModal(false);

		JFrame frame = new JFrame();
		WebBrowser browser = new WebBrowser();
		try {
			// System.err.println("Path: "+Locator.getClassSource(this.getClass()));
			browser.openResource(new URL(
					"http://wiki.glycoworkbench.org/index.php/Manual"), Locator
					.getClassSource(this.getClass()).getParentFile()
					+ File.separator + "www" + File.separator + "manual.html",
					false);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			browser.setHTMLContent("<html>"
					+ "	<head>"
					+ "		<title>Error reading manual</title>"
					+ "	</head>"
					+ "		<body>"
					+ "			<h1>Unable to load local copy of the GWB manual</h1>"
					+ "			<p>Please report this error to <mailto: info@glycoworkbench.org>info@glycoworkbench.org</mailto></p>"
					+ "			<p>For further information, please see: <a href=\"www.glycoworkbench.org\">www.glycoworkbench.org</a>"
					+ "			<p>Exception:" + e.getMessage() + "			</p>"
					+ "		</body>" + "</html>");
			e.printStackTrace();
		}

		frame.setIconImages(getIconImages());
		frame.add(browser);
		frame.setSize(500, 500);
		frame.setTitle("Manual");
		frame.setVisible(true);
	}

	public void onAbout() {
		try {
			JDialog dlg = new JDialog(this, "About GlycoWorkbench", true);
			URL url = GlycoWorkbench.class.getResource("/html/about_gwb.html");
			JEditorPane html = new JEditorPane(url);
			html.setEditable(false);
			html.setBorder(new EmptyBorder(20, 20, 20, 20));

			dlg.add(html);
			dlg.setSize(360, 480);
			dlg.setResizable(false);
			dlg.setLocationRelativeTo(this);

			dlg.setVisible(true);
		} catch (Exception e) {
			LogUtils.report(e);
		}
	}

	// --------------------------
	// Listeners

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
		if (MouseUtils.isPopupTrigger(e)) {
			theCanvas.setLastMouseButton(e.getButton());
			theCanvas.enforceSelection(e.getPoint());
			createPopupMenu().show(theCanvas, e.getX(), e.getY());
			theCanvas.setLastMouseButton(null);
		}
	}

	public void mouseReleased(MouseEvent e) {
		if (MouseUtils.isPopupTrigger(e)) {
			theCanvas.setLastMouseButton(e.getButton());
			theCanvas.enforceSelection(e.getPoint());
			createPopupMenu().show(theCanvas, e.getX(), e.getY());
			theCanvas.setLastMouseButton(null);
		}
	}

	public void mouseClicked(MouseEvent e) {
		if (MouseUtils.isActionTrigger(e)) {
			theCanvas.setLastMouseButton(e.getButton());
			try {
				if (theCanvas.hasCurrentResidue())
					thePluginManager.runAction("Fragments",
							"editFragmentsResidue");
				else if (theCanvas.hasCurrentLinkage())
					thePluginManager.runAction("Fragments",
							"editFragmentsLinkage");
			} catch (Exception ex) {
				LogUtils.report(ex);
			}
			theCanvas.setLastMouseButton(null);
		}
	}

	public void documentInit(BaseDocument.DocumentChangeEvent e) {
		if (e.getSource() == theWorkspace) {
			theDoc = theWorkspace.getStructures();
			theDoc.addDocumentChangeListener(this);
			theCanvas.setDocument(theDoc);

			thePluginManager.updateViews();
		}
		updateTitle();
		updateActions();
	}

	public void internalDocumentChanged(GlycanWorkspace.Event e) {
		updateTitle();
		updateActions();
	}

	public void documentChanged(BaseDocument.DocumentChangeEvent e) {
		updateTitle();
		updateActions();
	}

	public void selectionChanged(GlycanCanvas.SelectionChangeEvent e) {
		updateActions();
	}

	public void currentScanChanged(GlycanWorkspace.Event e) {
		theDoc = theWorkspace.getStructures();
		theDoc.addDocumentChangeListener(this);
		theCanvas.setDocument(theDoc);

		updateTitle();
		updateActions();
	}

	public void scanAdded(GlycanWorkspace.Event e) {
	}

	public void scanRemoved(GlycanWorkspace.Event e) {
	}

	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();

		String action = GlycanAction.getAction(e);
		String param = GlycanAction.getParam(e);

		if (action.equals("empty"))
			onNew(theDoc);
		else if (action.equals("newall"))
			onNew(theWorkspace);
		else if (action.equals("openall"))
			onOpen(param, theWorkspace, false);
		else if (action.equals("saveall"))
			onSave(theWorkspace, true);
		else if (action.equals("saveallas"))
			onSaveAs(theWorkspace);

		else if (action.equals("new")) {
			onNew(param);
		} else if (action.equals("open"))
			onOpenDocument(theWorkspace.getAllDocuments(), false);
		else if (action.equals("openinto"))
			onOpenDocument(theWorkspace.getAllDocuments(), true);
		else if (action.equals("openstruct"))
			onOpen(param, theWorkspace.getStructures(), false);
		else if (action.equals("openfragments"))
			onOpen(param, theWorkspace.getFragments(), false);
		else if (action.equals("openpeaks"))
			onOpen(param, theWorkspace.getPeakList(), false);
		else if (action.equals("openannpeaks"))
			onOpen(param, theWorkspace.getAnnotatedPeakList(), false);
		else if (action.equals("openspectra"))
			onOpen(param, theWorkspace.getSpectra(), false);
		else if (action.equals("save"))
			onSave(theDoc, true);
		else if (action.equals("saveas"))
			onSaveAs(theDoc);
		else if (action.equals("print"))
			onPrint();
		else if (action.equals("import"))
			onImportFrom(param);
		else if (action.equals("export"))
			onExportTo(param);
		else if (action.equals("quit"))
			try {
				onExit();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		else if (action.equals("toggleleft"))
			toggleLeftPanels();
		else if (action.equals("togglebottom"))
			toggleBottomPanels();
		else if (action.equals("toggleright"))
			toggleRightPanels();

		// tools
		else if (action.equals("massopt"))
			onMassOptionsAllStructures();
		else if (action.equals("fragopt"))
			onFragmentOptions();
		else if (action.equals("annopt"))
			onAnnotationOptions();

		// help
		else if (action.equals("help"))
			onHelp();
		else if (action.equals("about"))
			onAbout();

		updateActions();
	}

	public static void main(String[] args) throws IOException {
		NativeInterface.open();

		JFrame.setDefaultLookAndFeelDecorated(true);
		JDialog.setDefaultLookAndFeelDecorated(true);

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					String configFileName = GlycoWorkbench
							.getConfigurationFile().getPath();
					Configuration theConfiguration = new Configuration();
					theConfiguration.open(configFileName);
					GraphicOptions options = new GraphicOptions();
					options.retrieve(theConfiguration);

					// System.err.println(options.THEME);

					if (!options.THEME.equals("basic.white")) {
						SubstanceLookAndFeel.setSkin(options.THEME);
						GlycoWorkbench.SUBSTANCE_ENABLED=true;
					}

					long start = System.currentTimeMillis();
					final GlycoWorkbench gwb = new GlycoWorkbench();
					long end = System.currentTimeMillis();
					System.err.println("Startup phase took "+(end - start) / 1000+" seconds");
					gwb.setVisible(true);

					// Correct divider locations once the split pane has been
					// painted
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							gwb.updateDividerLocations();
						}
					});

					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							while (true) {
								if (gwb.theSplitPane.isVisible()) {
									gwb.applyApplicationIcon();
									break;
								}
							}
						}

					});

					gwb.runStartupTasksInBackground();
					
					

				} catch (Exception e) {
					LogUtils.setGraphicalReport(true);
					LogUtils.report(e);
				}
			}
		});
		NativeInterface.runEventPump();
		System.err.println("NORMAL STARTUP COMPLETE");
	}

	protected void applyApplicationIcon() {
		// TODO Auto-generated method stub
		// final ImageWrapperResizableIcon
		// icon=ImageWrapperResizableIcon.getIcon(themeManager.getResizableIcon(STOCK_ICON.COPY,
		// ICON_SIZE.L4).getIconProperties().imgURL, new Dimension(22,22));
		// icon.addAsynchronousLoadListener(new AsynchronousLoadListener(){
		//
		// @Override
		// public void completed(boolean arg0) {
		// // TODO Auto-generated method stub
		// System.err.println("icon loaded: "+arg0);
		// setApplicationIcon(icon);
		// }
		//
		// });

	}

	@Override
	public void fileHistoryChanged() {

	}

	@Override
	public void fireContextChanged(Context context, boolean switchToDefault) {
		if (Context.GLYCAN_CANVAS_ITEM == context) {
			this.ribbonManager.setCurrentContext(context, false);
		}
	}

	@Override
	public void fireUndoContextChanged(Context context) {
		if (Context.GLYCAN_CANVAS_ITEM == context) {
			this.ribbonManager.undoContextChange(context);
		}
	}

	@Override
	public void notationChanged(String notation) {
		createPopupMenu();
	}

	/**
	 * Panel container enums
	 */
	public enum CONTAINER {
		FRAME, NODEC_DIALOG, // Dialog without window decoration, with custom
								// move and resize code.
		DOCKED
	}

	private Dimension lastSize;
	private int lastHeight;
	private int beforeHeightTop;
	private int beforeHeightBottom;
	private JLabel status;

	/**
	 * Detach all detachable panels from the main GWB frame.
	 */
	@Override
	public void explode() {
		if (detachedSplitPaneCount < 4) {
			lastSize = this.getSize();

			beforeHeightTop = theLeftSplitPane.getTopComponent().getHeight();
			beforeHeightBottom = theLeftSplitPane.getBottomComponent()
					.getHeight();

			int jy = theSplitPane.getHeight();
			lastHeight = lastSize.height - jy;
			int ix = this.getWidth();
			int iy = this.getHeight();

			// setDividerLocation is called with the percentage (fraction) of
			// width or height that the divider should be placed at
			// getDividerLocation returns the dividers location by pixels and
			// not as a fraction, so we have to convert
			theLastSplitPaneDividerLocation = (double) theSplitPane
					.getDividerLocation() / (double) theSplitPane.getWidth();
			theLastTopSplitPaneDividerLocation = (double) theTopSplitPane
					.getDividerLocation() / (double) theTopSplitPane.getWidth();
			theLastLeftSplitPaneDividerLocation = (double) theLeftSplitPane
					.getDividerLocation()
					/ (double) theLeftSplitPane.getHeight();

			glycanCanvasDockableEvent
					.changeCanvasPaneContainer(CONTAINER.FRAME);
			glycanCanvasDockableEvent.getCurrentDockedWindow()
					.setSize(400, 400);
			leftPanelDockableEvent.changeCanvasPaneContainer(CONTAINER.FRAME);
			rightPanelDockableEvent.changeCanvasPaneContainer(CONTAINER.FRAME);
			bottomPanelDockableEvent.changeCanvasPaneContainer(CONTAINER.FRAME);

			hideTopPanels();
			hideAllLeftPanels();

			this.setSize(new Dimension(ix, iy - jy + 10));
		}
	}

	/**
	 * Attach all detachable panels to the main GWB frame.
	 */
	@Override
	public void implode() {
		if (detachedSplitPaneCount > 0) {
			rightPanelDockableEvent.changeCanvasPaneContainer(CONTAINER.DOCKED);
			bottomPanelDockableEvent
					.changeCanvasPaneContainer(CONTAINER.DOCKED);
			glycanCanvasDockableEvent
					.changeCanvasPaneContainer(CONTAINER.DOCKED);
			leftPanelDockableEvent.changeCanvasPaneContainer(CONTAINER.DOCKED);

			// There's still a race condition here between the painter and call
			// to updateDividerLocations()
			// Maybe we can rig a ComponentListener up?
			JSplitPaneCustom.toPaint.clear();
			JSplitPaneCustom.toPaint.add(theSplitPane);
			JSplitPaneCustom.toPaint.add(theLeftSplitPane);
			JSplitPaneCustom.toPaint.add(theTopSplitPane);

			Thread thread = new Thread() {
				public void run() {
					while (true) {
						if (JSplitPaneCustom.painted.size() == 3) {
							updateDividerLocations();
							break;
						}
						try {
							Thread.currentThread().sleep(200);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			};
			thread.start();

			DockableEvent.setLastPlacedWindowPosition(new Point(0, 0));
		}
	}

	public void checkForUpdates() {
		if (updater.isUptoDate(this)) {
			System.err.println("GlycoWorkbench is up to date");
		} else {
			System.err.println("An update is available");
		}

	}

	@Override
	public String getMajorVersion() {
		// TODO Auto-generated method stub
		return GlycoWorkbench.MAJOR_VERSION;
	}

	@Override
	public String getMinorVersion() {
		// TODO Auto-generated method stub
		return GlycoWorkbench.MINOR_VERSION;
	}

	@Override
	public String getBuildState() {
		// TODO Auto-generated method stub
		return GlycoWorkbench.BUILD_STATE;
	}

	@Override
	public String getBuildNo() {
		// TODO Auto-generated method stub
		return GlycoWorkbench.BUILD_NUMBER;
	}
}
