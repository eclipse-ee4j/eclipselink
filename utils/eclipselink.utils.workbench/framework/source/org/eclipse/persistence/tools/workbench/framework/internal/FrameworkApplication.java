/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
******************************************************************************/
package org.eclipse.persistence.tools.workbench.framework.internal;

import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.prefs.BackingStoreException;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;
import java.util.prefs.Preferences;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.UIManager;

import org.eclipse.persistence.tools.workbench.framework.AbstractApplication;
import org.eclipse.persistence.tools.workbench.framework.Application;
import org.eclipse.persistence.tools.workbench.framework.NodeManager;
import org.eclipse.persistence.tools.workbench.framework.OpenException;
import org.eclipse.persistence.tools.workbench.framework.Plugin;
import org.eclipse.persistence.tools.workbench.framework.PluginFactory;
import org.eclipse.persistence.tools.workbench.framework.UnsupportedFileException;
import org.eclipse.persistence.tools.workbench.framework.app.AbstractPreferencesNode;
import org.eclipse.persistence.tools.workbench.framework.app.ApplicationNode;
import org.eclipse.persistence.tools.workbench.framework.app.PreferencesNode;
import org.eclipse.persistence.tools.workbench.framework.context.AbstractApplicationContext;
import org.eclipse.persistence.tools.workbench.framework.context.ApplicationContext;
import org.eclipse.persistence.tools.workbench.framework.context.PreferencesContext;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.help.HelpFacade;
import org.eclipse.persistence.tools.workbench.framework.help.HelpManager;
import org.eclipse.persistence.tools.workbench.framework.help.HelpManagerConfig;
import org.eclipse.persistence.tools.workbench.framework.resources.DefaultResourceRepository;
import org.eclipse.persistence.tools.workbench.framework.resources.ResourceRepository;
import org.eclipse.persistence.tools.workbench.framework.resources.ResourceRepositoryWrapper;
import org.eclipse.persistence.tools.workbench.framework.uitools.UIToolsIconResourceFileNameMap;
import org.eclipse.persistence.tools.workbench.framework.uitools.UIToolsResourceBundle;
import org.eclipse.persistence.tools.workbench.uitools.Console;
import org.eclipse.persistence.tools.workbench.uitools.RecentFilesManager;
import org.eclipse.persistence.tools.workbench.uitools.SplashScreen;
import org.eclipse.persistence.tools.workbench.uitools.app.BufferedPropertyValueModel;
import org.eclipse.persistence.tools.workbench.utility.ClassTools;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.Command;
import org.eclipse.persistence.tools.workbench.utility.SynchronizedBoolean;
import org.eclipse.persistence.tools.workbench.utility.SynchronizedObject;
import org.eclipse.persistence.tools.workbench.utility.io.InvalidInputStream;
import org.eclipse.persistence.tools.workbench.utility.io.TeeOutputStream;
import org.eclipse.persistence.tools.workbench.utility.log.DeathHandler;
import org.eclipse.persistence.tools.workbench.utility.log.FileHandlerCleanup;


/**
 * The UI framework starts here - this is the public entry into the
 * TopLink Workbench application. We build plug-ins here and we
 * build the objects for which there should only be a single instance
 * per application (e.g. the node manager, the help manager).
 * 
 * Hard code your PluginFactory class name and static method name in the static
 * array PLUGIN_FACTORY_STATIC_METHOD_SPECS.
 */
public final class FrameworkApplication 
	extends AbstractApplication 
{
	/** This is false during launch and set to true once the launch is complete. */
	private SynchronizedBoolean launchCompleteFlag;

	/** This flag can be set to true via a command line argument, "-dev". */
	private boolean developmentMode;

	/** The logger used mostly for recording uncaught exceptions. */
	private Logger logger;

	/** The root node for all the preferences used by the application. */
	private Preferences rootPreferences;

	/** The node manager tracks all the "project-level" nodes. */
	private FrameworkNodeManager nodeManager;

	/** The default resource repository - includes both UI Tools and Framework resources. */
	private ResourceRepository resourceRepository;

	/** The OHJ context-sensitive help manager. */
	private SynchronizedObject synchronizedHelpManager;

	/** The "root" application context. */
	private ApplicationContext rootApplicationContext;

	/** The plug-ins built by the plug-in factories at start-up. */
	private Plugin[] plugins;

	/** The current set of open workbench windows. */
	private Set workbenchWindows;

	/** The preferences view passed to the preferences dialog(s). */
	private PreferencesView preferencesView;

	/** This is true only during the first execution. */
	private boolean firstExecution;

	/** There are two different consoles depending on development mode or runtime */
	private Console frameworkConsole;


	// ********** static fields **********

	/** The name of the application's splash screen file - displayed on start-up. */
	private static final String SPLASH_SCREEN_FILE_NAME = "/logo/splash.screen.gif";
	/** The name of the application's splash screen icon - shown in the ALT-TAB dialog. */
	private static final String SPLASH_SCREEN_ICON_FILE_NAME = "/logo/MappingWorkbench.large.gif";

	/**
	 * The current preferences version number (major.minor). This string
	 * will be used to name a preferences under the root node under which
	 * all the other preferences nodes will be located.
	 * Change the minor number when the organization changes but is still
	 * backward-compatible. Change the major number when the organization
	 * changes and is no longer backward-compatible.
	 * If the major number changes, make the previous version available
	 * for migration.
	 */
	private static final String PREFERENCES_CURRENT_VERSION_NODE = "1.0";

	/**
	 * The name of the "general" preferences node used to store
	 * preferences used by the framework, as opposed to the plug-ins.
	 */
	static final String GENERAL_PREFERENCES_NODE = "general";

	/** Various start-up preferences. */
	static final String DISPLAY_SPLASH_SCREEN_PREFERENCE = "display splash screen";
		static final boolean DISPLAY_SPLASH_SCREEN_PREFERENCE_DEFAULT = true;
		
	static final String LOOK_AND_FEEL_PREFERENCE = "look and feel";

	static final String HTTP_PROXY_HOST_PREFERENCE = "http proxy host";
		static final String HTTP_PROXY_HOST_PREFERENCE_DEFAULT = "";

	static final String HTTP_PROXY_PORT_PREFERENCE = "http proxy port";
		static final String HTTP_PROXY_PORT_PREFERENCE_DEFAULT = "80";

	static final String NETWORK_CONNECT_TIMEOUT_PREFERENCE = "network connect timeout";		// specified in seconds
		static final String NETWORK_CONNECT_TIMEOUT_PREFERENCE_DEFAULT = "10";

	static final String NETWORK_READ_TIMEOUT_PREFERENCE = "network read timeout";		// specified in seconds
		static final String NETWORK_READ_TIMEOUT_PREFERENCE_DEFAULT = "10";

	static final String REOPEN_PROJECTS_PREFERENCE = "reopen projects";
		static final boolean REOPEN_PROJECTS_PREFERENCE_DEFAULT = true;
		
	static final String DYNAMIC_LAYOUT_PREFERENCE = "dynamic layout";

	/**
	 * The list of plug-in factory static methods. Each static method must
	 * return an instance of PluginFactory. These factories will be used by
	 * the application to build the plug-ins. These are stored as strings so
	 * we don't have compile-time dependencies and they can be
	 * "externalized" at some later date.
	 */
	private static final PluginFactoryStaticMethodSpec[] PLUGIN_FACTORY_STATIC_METHOD_SPECS =
		new PluginFactoryStaticMethodSpec[] {
			// these class names MUST be strings  ~bjv
			new PluginFactoryStaticMethodSpec("org.eclipse.persistence.tools.workbench.mappingsplugin.MappingsPluginFactory", "instance"),
			new PluginFactoryStaticMethodSpec("org.eclipse.persistence.tools.workbench.scplugin.SCPluginFactory", "instance"),
			new PluginFactoryStaticMethodSpec("org.eclipse.persistence.tools.workbench.platformsplugin.ui.PlatformsPluginFactory", "instance"),
		};


	// ********** static methods **********

	/**
	 * Launch the application.
	 */
	public static void launch(Logger logger, Preferences preferences, File projectFile, boolean developmentMode) {
		boolean firstExecution = buildFirstExecution(preferences, logger);
		boolean displaySplashScreen = buildDisplaySplashScreen(preferences);

		Handler deathHandler = null;
		SplashScreen ss = null;
		if (displaySplashScreen) {
			// if we start a splash screen, we need a "death handler" during launch
			deathHandler = buildDeathHandler();
			logger.addHandler(deathHandler);
			ss = buildSplashScreen();
			ss.start();
		}

		new FrameworkApplication(logger, preferences, firstExecution, developmentMode).launch(projectFile);

		if (displaySplashScreen) {
			ss.stop();
			logger.removeHandler(deathHandler);
		}
	}

	/**
	 * Creates the splash screen for this application.
	 */
	private static SplashScreen buildSplashScreen()
	{
		JFrame frame = new JFrame("EclipseLink Workbench");
		Icon icon = getIcon(SPLASH_SCREEN_ICON_FILE_NAME);
		if (icon instanceof ImageIcon) {
			frame.setIconImage(((ImageIcon) icon).getImage());
		}
//		return new SplashScreen(frame, getString("COPYRIGHT"), getIcon(SPLASH_SCREEN_FILE_NAME));
		return new SplashScreen(frame, getIcon(SPLASH_SCREEN_FILE_NAME));

	}

	/**
	 * Shut down the JVM if we have any exceptions during launch.
	 * We need this because if we launch the splash screen, there seems
	 * to be no way to get the JVM to die naturally. Even if we close the
	 * splash screen, the JVM keeps running.... ~bjv
	 */
	private static Handler buildDeathHandler() {
		// wait 5 seconds and exit with a status code of 1
		Handler handler = new DeathHandler(5000, 1);
		handler.setLevel(Level.SEVERE);
		return handler;
	}

	/**
	 * Check for the preferences node for the current preferences version;
	 * if it does NOT exist, this must be our first execution.
	 */
	private static boolean buildFirstExecution(Preferences preferences, Logger logger) {
		try {
			return ! CollectionTools.contains(preferences.childrenNames(), PREFERENCES_CURRENT_VERSION_NODE);
		} catch (BackingStoreException ex) {
			// if there are any problems, log the exception and act like it's a first execution
			logger.log(Level.WARNING, "", ex);
			return true;
		}
	}

	/**
	 * No preferences yet - hard-code the appropriate preference.
	 */
	private static boolean buildDisplaySplashScreen(Preferences preferences) {
		return preferences.node(PREFERENCES_CURRENT_VERSION_NODE).node(GENERAL_PREFERENCES_NODE).getBoolean(DISPLAY_SPLASH_SCREEN_PREFERENCE, DISPLAY_SPLASH_SCREEN_PREFERENCE_DEFAULT);
	}

	/**
	 * Return the string for the specified key.
	 * No string repository yet - hard-code the bundle class name.
	 */
// this method was used by buildSplashScreen() until the copyright notice was removed
//	private static String getString(String key) {
//		return ResourceBundle.getBundle(getResourceBundleName()).getString(key);
//	}

	public static String getResourceBundleName() {
		return FrameworkResourceBundle.class.getName();
	}

	/**
	 * No icon repository yet - hard-code the icon resource.
	 */
	private static Icon getIcon(String resourceName) {
		return new ImageIcon(getResource(resourceName));
	}

	/**
	 * Return the URL of the specified resource on the classpath.
	 */
	private static URL getResource(String name) {
		return FrameworkApplication.class.getResource(name);
	}


	// ********** constructor/initialization **********

	/**
	 * Construct an application with the specified root preferences node.
	 */
	private FrameworkApplication(Logger logger, Preferences preferences, boolean firstExecution, boolean developmentMode) {
		super();
		this.firstExecution = firstExecution;
		this.logger = logger;
		this.developmentMode = developmentMode;
		this.initialize(preferences);
	}

	private void initialize(Preferences preferences) {
		this.launchCompleteFlag = new SynchronizedBoolean(false);
		// shift the root to a version-specific subnode
		this.rootPreferences = preferences.node(PREFERENCES_CURRENT_VERSION_NODE);
		this.configureLoggerForLaunch();
		this.initializeLookAndFeel();
		this.initializeNetworkSettings();
		this.nodeManager = this.buildNodeManager();
		this.resourceRepository = this.buildResourceRepository();
		this.startInitializeSynchronizedHelpManager();
		this.rootApplicationContext = new RootApplicationContext();
		this.plugins = this.buildPlugins(PLUGIN_FACTORY_STATIC_METHOD_SPECS);
		this.initializeWorkbenchWindows();
		// postpone building the preferences view until it is first needed

		Toolkit.getDefaultToolkit().setDynamicLayout(this.generalPreferences().getBoolean(DYNAMIC_LAYOUT_PREFERENCE, true));
	}

	/**
	 * Initialize the logger:
	 * 	- add a handler that writes log entries to a file
	 * 	- register a hook to clean up the lock file on system exit
	 */
	private void configureLoggerForLaunch() {
		FileHandler fileHandler;
		try {
			// TODO get log file name and size from preferences?
			fileHandler = new FileHandler(System.getProperty("user.home") + "/org.eclipse.persistence.tools.workbench.log", 50000, 1, true);	// true = append
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
		// once the file handler is built, the lock file is present and open
		String version = System.getProperty("java.version");
		if (version.startsWith("1.4")) {
			FileHandlerCleanup.register(fileHandler);
		}
		fileHandler.setFormatter(new SimpleFormatter());
		this.logger.addHandler(fileHandler);
	}

	/**
	 * Initialize the LAF to the user's preference.
	 */
	private void initializeLookAndFeel() {
		try {
			UIManager.setLookAndFeel(this.lookAndFeel());
		} catch (Exception exception1) {
			try {
				// Revert back to the system look and feel
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch (Exception exception2) {
				try {
					// Revert back to the cross-platform look and feel
					UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
				} catch (Exception exception3) {
					// This should never happen
					throw new RuntimeException(exception3);
				}
			}
		}
	}

	/**
	 * Default to the current platform's LAF;
	 * overriding the default, Metal LAF.
	 */
	private String lookAndFeel() {
		String laf = this.generalPreferences().get(LOOK_AND_FEEL_PREFERENCE, defaultLookAndFeel());

		// uncomment one of the following lines to try the other LAFs
//		laf = UIManager.getCrossPlatformLookAndFeelClassName();	// Metal
//		laf = com.sun.java.swing.plaf.windows.WindowsLookAndFeel.class.getName();
//		laf = com.sun.java.swing.plaf.motif.MotifLookAndFeel.class.getName();

		return laf;
	}

	/**
	 * Returns the default LAF. Under Linux, GTK+ is the default but because it
	 * has too many problems, the Metal LAF is used instead.
	 */
	private String defaultLookAndFeel() {
		return (System.getProperty("os.name", "").toLowerCase().indexOf("linux") != -1) ?
			UIManager.getCrossPlatformLookAndFeelClassName()
		:
			UIManager.getSystemLookAndFeelClassName();
	}

	/**
	 * Check for network settings. These are used by
	 * OHJ and for reading Schemas from the WWW.
	 */
	private void initializeNetworkSettings() {
		String host = this.generalPreferences().get(HTTP_PROXY_HOST_PREFERENCE, null);
		if (host != null) {
			System.setProperty("http.proxyHost", host);
		}

		String port = this.generalPreferences().get(HTTP_PROXY_PORT_PREFERENCE, null);
		if (port != null) {
			System.setProperty("http.proxyPort", port);
		}

		String connectTimeout = this.generalPreferences().get(NETWORK_CONNECT_TIMEOUT_PREFERENCE, NETWORK_CONNECT_TIMEOUT_PREFERENCE_DEFAULT);
		connectTimeout = String.valueOf(Integer.parseInt(connectTimeout) * 1000);		// convert to milliseconds
		System.setProperty("sun.net.client.defaultConnectTimeout", connectTimeout);

		String readTimeout = this.generalPreferences().get(NETWORK_READ_TIMEOUT_PREFERENCE, NETWORK_READ_TIMEOUT_PREFERENCE_DEFAULT);
		readTimeout = String.valueOf(Integer.parseInt(readTimeout) * 1000);		// convert to milliseconds
		System.setProperty("sun.net.client.defaultReadTimeout", readTimeout);

		this.generalPreferences().addPreferenceChangeListener(this.buildNetworkSettingsListener());
	}
	
	/**
	 * Check for changes to the network settings and apply them
	 */
	private PreferenceChangeListener buildNetworkSettingsListener() {
		return new PreferenceChangeListener() {
			public void preferenceChange(PreferenceChangeEvent e) {
				String key = e.getKey();
				if (key.equals(HTTP_PROXY_HOST_PREFERENCE)) {
					System.setProperty("http.proxyHost", e.getNewValue());
				}
				else if (key.equals(HTTP_PROXY_PORT_PREFERENCE)) {
					System.setProperty("http.proxyPort", e.getNewValue());
				}
				else if (key.equals(NETWORK_CONNECT_TIMEOUT_PREFERENCE)) {
					System.setProperty("sun.net.client.defaultConnectTimeout", e.getNewValue());
				}
				else if (key.equals(NETWORK_READ_TIMEOUT_PREFERENCE)) {
					System.setProperty("sun.net.client.defaultReadTimeout", e.getNewValue());
				}	
			}
		};
	}

	/**
	 * Build a node manager for opening, holding, and closing nodes.
	 */
	private FrameworkNodeManager buildNodeManager() {
		return new FrameworkNodeManager(this);
	}

	/**
	 * Build a resource repository that holds all the resources for UI Tools
	 * and the Framework.
	 */
	private ResourceRepository buildResourceRepository() {
		ResourceRepository uiToolsRepository = new DefaultResourceRepository(UIToolsResourceBundle.class, new UIToolsIconResourceFileNameMap());
		return new ResourceRepositoryWrapper(uiToolsRepository, FrameworkResourceBundle.class, new FrameworkIconResourceFileNameMap());
	}

	/**
	 * Build an empty "synchronized" help manager and
	 * start a thread to initialize it with a help manager.
	 */
	private void startInitializeSynchronizedHelpManager() {
		// put the synchronized object in place -
		// #getHelpManager() will wait until it is initialized and unlocked
		this.synchronizedHelpManager = new SynchronizedObject();
		new Thread(this.buildInitializeSynchronizedHelpManagerRunnable(), "Initialize Help").start();
	}

	/**
	 * Build a Runnable that will initialize the "synchronized" help manager.
	 */
	private Runnable buildInitializeSynchronizedHelpManagerRunnable() {
		return new Runnable() {
			public void run() {
				FrameworkApplication.this.initializeSynchronizedHelpManager();
			}
		};
	}

	/**
	 * Hold the the lock while building the help manager
	 * and putting it in the synchronized object.
	 */
	void initializeSynchronizedHelpManager() {
		try {
			this.synchronizedHelpManager.execute(this.buildInitializeHelpManagerCommand());
		} catch (InterruptedException ex) {
			throw new RuntimeException(ex);	// shouldn't happen
		}
	}

	/**
	 * Build the command that will be executed while the "synchronized"
	 * help manager is locked.
	 */
	private Command buildInitializeHelpManagerCommand() {
		return new Command() {
			public void execute() {
				FrameworkApplication.this.initializeHelpManager();
			}
		};
	}

	/**
	 * Build the appropriate help manager and put it in the
	 * synchronized object that wraps it.
	 */
	void initializeHelpManager() {
		HelpManagerConfig config = new HelpManagerConfig();
		config.setPreferences(this.generalPreferences());
		config.setResourceRepository(this.resourceRepository);
		config.setLogger(this.logger);
		config.setDevelopmentMode(this.isDevelopmentMode());
		config.setLaunchCompleteFlag(this.launchCompleteFlag);
		config.setForceStupidWelcomeScreen(this.isFirstExecution());
		this.synchronizedHelpManager.setValue(HelpFacade.buildHelpManager(config));
	}

	/**
	 * maintain the order of the specs
	 */
	private Plugin[] buildPlugins(PluginFactoryStaticMethodSpec[] pluginFactoryStaticMethodSpecs) {
		int len = pluginFactoryStaticMethodSpecs.length;
		Plugin[] result = new Plugin[len];
		for (int i = 0; i < len; i++) {
			result[i] = this.buildPlugin(pluginFactoryStaticMethodSpecs[i]);
		}
		return result;
	}

	private Plugin buildPlugin(PluginFactoryStaticMethodSpec pluginFactoryStaticMethodSpec) {
		Class pluginFactoryClass;
		try {
			pluginFactoryClass = Class.forName(pluginFactoryStaticMethodSpec.getClassName());
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
		PluginFactory pluginFactory = (PluginFactory) ClassTools.invokeStaticMethod(pluginFactoryClass, pluginFactoryStaticMethodSpec.getStaticMethodName());
		return pluginFactory.createPlugin(this.rootApplicationContext);
	}

	private void initializeWorkbenchWindows() {
		this.workbenchWindows = new HashSet();
		int windowCount = this.generalPreferences().node("windows").getInt("count", 1);
		for (int i = 0; i < windowCount; i++) {
			WorkbenchWindow window = new WorkbenchWindow(this);
			window.restoreState(this.generalPreferences().node("windows").node("window" + i));
			this.workbenchWindows.add(window);
		}
	}


	// ********** queries **********

	Logger getLogger() {
		return this.logger;
	}

	/**
	 * Return the root application preferences node.
	 * Each plug-in will have its own child node under this node.
	 */
	Preferences getRootPreferences() {
		return this.rootPreferences;
	}

	FrameworkNodeManager getNodeManager() {
		return this.nodeManager;
	}

	/**
	 * Postpone building the preferences view until it is first needed.
	 */
	PreferencesView getPreferencesView() {
		if (this.preferencesView == null) {
			// store the trigger in the context so the preferences pages have access to it;
			// and put it in the view so the dialog OK button has access to it
			BufferedPropertyValueModel.Trigger bufferTrigger = new BufferedPropertyValueModel.Trigger();
			PreferencesContext context = new FrameworkPreferencesContext(this, bufferTrigger);
			this.preferencesView = new PreferencesView(this.buildRootPreferencesNode(context), bufferTrigger);
		}
		return this.preferencesView;
	}

	/**
	 * Return the application's default resource repository, which
	 * includes the resources for UI Tools and the Framework.
	 */
	ResourceRepository getResourceRepository() {
		return this.resourceRepository;
	}

	/**
	 * Return the "root" application context that all other application
	 * contexts and workbench contexts extend.
	 */
	ApplicationContext getRootApplicationContext() {
		return this.rootApplicationContext;
	}

	/**
	 * Return all the application's plug-ins.
	 */
	Plugin[] getPlugins() {
		return this.plugins;
	}

	/**
	 * Return the recent files manager that maintains a list
	 * of the recently-opened files.
	 */
	RecentFilesManager recentFilesManager() {
		return this.nodeManager.getRecentFilesManager();
	}

	/**
	 * Return the preferences node that holds the "general" framework
	 * preferences, as opposed to the preferences nodes for the plug-ins.
	 */
	Preferences generalPreferences() {
		return this.rootPreferences.node(GENERAL_PREFERENCES_NODE);
	}


	// ********** behavior **********

	/**
	 * Open all the initial workbench windows.
	 */
	private void launch(File projectToOpen) {
		int i = 0;
		WorkbenchWindow window = null;
		for (Iterator stream = this.workbenchWindows.iterator(); stream.hasNext(); i++) {
			window = (WorkbenchWindow) stream.next();
			window.setVisible(true);
		}
		if (projectToOpen != null) {
			this.nodeManager.open(projectToOpen, window.getContext());
		}
		else if (generalPreferences().getBoolean(REOPEN_PROJECTS_PREFERENCE, REOPEN_PROJECTS_PREFERENCE_DEFAULT)) {
			this.nodeManager.restoreProjectsState(window, generalPreferences().node("windows").node("window" + i));
		}
		this.redirectSystemStreams();
		this.configureLoggerForExecution();
		this.launchCompleteFlag.setTrue();
	}
	
	/**
	 * Once we are executing with a GUI, redirect the system streams.
	 * In "development" mode we "tee" the output streams to the
	 * "development console" and the "standard console";
	 * in "production" mode we re-direct the streams to a stream that
	 * throws an exception if anything is written to it (we will log this
	 * exception and notify the user).
	 * Changed this because of some bugs we were encountering where help 
	 * was writing to System.err.  The InvalidOuputStream was causing
	 * difficult debugging and causing the process to halt making progressing
	 * beyond it impossible.  LDD
	 */
	private void redirectSystemStreams() {
		System.setIn(InvalidInputStream.instance());
		if (this.isDevelopmentMode()) {
			this.frameworkConsole = new Console();
			System.setOut(new PrintStream(new TeeOutputStream(System.out, this.frameworkConsole.getOutStream())));
			System.setErr(new PrintStream(new TeeOutputStream(System.err, this.frameworkConsole.getErrStream())));
		} else {
			this.frameworkConsole = new FrameworkConsole(this.getRootApplicationContext());
			System.setOut(new PrintStream(this.frameworkConsole.getOutStream()));
			System.setErr(new PrintStream(this.frameworkConsole.getErrStream()));			
		}
	}

	/**
	 * Once we are executing with a GUI, we can stop forwarding
	 * log records to the root console handler and add a more
	 * "user-friendly" handler.
	 */
	private void configureLoggerForExecution() {
		// at this point the root console handler is using the old,
		// invalid, System.err and is pretty much useless
		this.logger.setUseParentHandlers(false);
		if (this.isDevelopmentMode()) {
			// this will print all entries to the "development console"
			this.logger.addHandler(new ConsoleHandler());
		} else {
			// any SEVERE log entries (typically unhandled exceptions)
			// will be displayed to the user via a dialog
			this.logger.addHandler(new FrameworkLogHandler(this, Level.SEVERE));
		}
	}

	ApplicationNode open(File file, WorkbenchContext context) throws UnsupportedFileException, OpenException {
		for (int i = 0; i < this.plugins.length; i++) {
			try {
				return this.plugins[i].open(file, context);
			} catch (UnsupportedFileException ex) {
				continue;	// try the next plug-in
			} 
		}
		throw new UnsupportedFileException("could not find plug-in support for file: " + file);
	}

	void openNewWindow(WorkbenchWindow currentWindow) {
		WorkbenchWindow window = new WorkbenchWindow(this, currentWindow);
		this.workbenchWindows.add(window);
		window.setVisible(true);
	}
	
	void openDevelopmentConsole() {
		if (this.frameworkConsole == null) {
			throw new IllegalStateException("This operation is only supported in \"development\" mode.");
		}
		this.frameworkConsole.open();
	}

	private AbstractPreferencesNode buildRootPreferencesNode(PreferencesContext context) {
		AbstractPreferencesNode root = new RootPreferencesNode(context);
		int childIndex = 0;
		root.insert(new GeneralPreferencesNode(context), childIndex++);

		for (int i = 0; i < this.plugins.length; i++) {
			PreferencesNode[] nodes = this.plugins[i].buildPreferencesNodes(context);
			for (int j = 0; j < nodes.length; j++) {
				root.insert(nodes[j], childIndex++);
			}
		}

		return root;
	}

	void close(WorkbenchWindow window) {
		// if this is the last window, exit the application
		if (this.workbenchWindows.size() == 1) {
			// don't remove the last window, because the node manager will prompt
			// the user to save any unsaved projects, at which point the user can
			// cancel the exit instead of saving the projects
			if (this.workbenchWindows.iterator().next() != window) {
				throw new IllegalStateException("unknown window: " + window);
			}
			this.nodeManager.exit(window.getContext());
		} else {
			if ( ! this.workbenchWindows.remove(window)) {
				throw new IllegalStateException("unknown window: " + window);
			}
			window.dispose();
		}
	}

	/**
	 * This method is called from FrameworkNodeManager.exit(WorkbenchContext)
	 * once the user is prompted to save any unsaved projects.
	 */
	void exit() {
		this.saveWindowsState();
		for (Iterator stream = this.workbenchWindows.iterator(); stream.hasNext(); ) {
			((WorkbenchWindow) stream.next()).dispose();
		}
		this.shutDown();
	}

	private void saveWindowsState() {
		Preferences windowsPreferences =  generalPreferences().node("windows");
		try {
			windowsPreferences.clear();
		} catch (BackingStoreException e) {
			//do nothing if this occurs
		}
		windowsPreferences.putInt("count", this.workbenchWindows.size());
		int i = 0;
		for (Iterator stream = this.workbenchWindows.iterator(); stream.hasNext(); i++) {
			WorkbenchWindow window = (WorkbenchWindow) stream.next();
			window.saveState(windowsPreferences.node("window" + i));		
			window.dispose();
		}
	}
	
	protected void saveTreeExpansionStates() {
		Preferences windowsPreferences =  generalPreferences().node("windows");
		int i = 0;
		for (Iterator stream = this.workbenchWindows.iterator(); stream.hasNext(); i++) {
			WorkbenchWindow window = (WorkbenchWindow) stream.next();
			window.saveTreeExpansionState(windowsPreferences.node("window" + i));		
		}
	}
	
	private void shutDown() {
		this.getHelpManager().shutDown();
		// allow all the outstanding UI events to be handled before shutting down
		EventQueue.invokeLater(
				new Runnable() {
					public void run() {
						System.exit(0);
					}
					public String toString() {
						return "runnable shutdown";
					}
				}
		);
	}

	// ********** Help **********

	/**
	 * Return the application's OHJ context-sensitive help manager.
	 * If necessary, wait for the initialization to complete.
	 */
	HelpManager getHelpManager() {
		// once the help mgr is initialized, it should never change back to null
		try {
			this.synchronizedHelpManager.waitUntilNotNull();
		} catch (InterruptedException ex) {
			throw new RuntimeException(ex);	// shouldn't happen
		}
		return (HelpManager) this.synchronizedHelpManager.getValue();
	}


	// ********** Application implementation **********

	/**
	 * @see org.eclipse.persistence.tools.workbench.framework.Application#isFirstExecution()
	 */
	public boolean isFirstExecution() {
		return this.firstExecution;
	}

	/**
	 * Allow the user to force "development mode" via a command line argument, "-dev".
	 * @see org.eclipse.persistence.tools.workbench.framework.AbstractApplication#isDevelopmentMode()
	 */
	public boolean isDevelopmentMode() {
		return this.developmentMode || super.isDevelopmentMode();
	}


	// ********** AbstractApplication implementation **********

	/**
	 * @see org.eclipse.persistence.tools.workbench.utility.ManifestInterrogator.Defaults#defaultSpecificationTitle()
	 */
	public String defaultSpecificationTitle() {
		return "EclipseLink Workbench";
	}

	/**
	 * @see org.eclipse.persistence.tools.workbench.utility.ManifestInterrogator.Defaults#defaultSpecificationVendor()
	 */
	public String defaultSpecificationVendor() {
		return "";
	}

	/**
	 * @see org.eclipse.persistence.tools.workbench.utility.ManifestInterrogator.Defaults#defaultReleaseDesignation()
	 */
	public String defaultReleaseDesignation() {
		return "Version 1.0.0";
	}

	/**
	 * @see org.eclipse.persistence.tools.workbench.utility.ManifestInterrogator.Defaults#defaultLibraryDesignation()
	 */
	public String defaultLibraryDesignation() {
		return "Workbench";
	}

	/**
	 * @see org.eclipse.persistence.tools.workbench.utility.ManifestInterrogator.Defaults#defaultSpecificationVersion()
	 */
	public String defaultSpecificationVersion() {
		return "1.0.0";
	}

	/**
	 * @see org.eclipse.persistence.tools.workbench.utility.ManifestInterrogator.Defaults#defaultImplementationVersion()
	 */
	public String defaultImplementationVersion() {
		return this.defaultSpecificationVersion();
	}


	// ********** nested classes **********

	/**
	 * This is the root application context that all other application contexts
	 * and workbench contexts extend. All methods delegate to the application
	 */
	private class RootApplicationContext
		extends AbstractApplicationContext
	{
		public Application getApplication() {
			return FrameworkApplication.this;
		}
		public HelpManager getHelpManager() {
			return FrameworkApplication.this.getHelpManager();
		}
		public NodeManager getNodeManager() {
			return FrameworkApplication.this.getNodeManager();
		}
		public Preferences getPreferences() {
			return FrameworkApplication.this.getRootPreferences();
		}
		public ResourceRepository getResourceRepository() {
			return FrameworkApplication.this.getResourceRepository();
		}
	}

	/**
	 * This class holds the information needed by the application to acquire
	 * a plug-in factory: the name of a class and the name of a static method
	 * implemented by that class that will return an instance of PluginFactory.
	 */
	private static class PluginFactoryStaticMethodSpec {
		private String className;
		private String staticMethodName;
		PluginFactoryStaticMethodSpec(String className, String staticMethodName) {
			super();
			this.className = className;
			this.staticMethodName = staticMethodName;
		}
		String getClassName() {
			return this.className;
		}
		String getStaticMethodName() {
			return this.staticMethodName;
		}
	}


	/**
	 * The root preferences node should never be visible or selected.
	 */
	private static final class RootPreferencesNode extends AbstractPreferencesNode {
		RootPreferencesNode(PreferencesContext context) {
			super(context);
		}
		protected Component buildPropertiesPage() {
			return null;
		}
		protected String buildDisplayString() {
			return null;
		}
	}

}
