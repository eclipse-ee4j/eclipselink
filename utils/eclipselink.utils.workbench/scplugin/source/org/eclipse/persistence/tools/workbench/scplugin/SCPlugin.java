/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.tools.workbench.scplugin;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;
import java.util.prefs.Preferences;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.eclipse.persistence.tools.workbench.framework.OpenException;
import org.eclipse.persistence.tools.workbench.framework.Plugin;
import org.eclipse.persistence.tools.workbench.framework.UnsupportedFileException;
import org.eclipse.persistence.tools.workbench.framework.app.ApplicationNode;
import org.eclipse.persistence.tools.workbench.framework.app.ComponentContainerDescription;
import org.eclipse.persistence.tools.workbench.framework.app.MenuGroupDescription;
import org.eclipse.persistence.tools.workbench.framework.app.PreferencesNode;
import org.eclipse.persistence.tools.workbench.framework.app.ToolBarButtonGroupDescription;
import org.eclipse.persistence.tools.workbench.framework.context.ApplicationContext;
import org.eclipse.persistence.tools.workbench.framework.context.PreferencesContext;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.TopLinkSessionsAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.meta.SCSessionsProperties;
import org.eclipse.persistence.tools.workbench.scplugin.model.meta.SCSessionsPropertiesManager;
import org.eclipse.persistence.tools.workbench.scplugin.ui.AddNewSessionsAction;
import org.eclipse.persistence.tools.workbench.scplugin.ui.preferences.SCPreferencesNode;
import org.eclipse.persistence.tools.workbench.scplugin.ui.project.ProjectNode;
import org.eclipse.persistence.tools.workbench.uitools.LabelArea;
import org.eclipse.persistence.tools.workbench.utility.io.FileTools;
import org.eclipse.persistence.tools.workbench.utility.string.StringTools;

import org.eclipse.persistence.exceptions.SessionLoaderException;

public final class SCPlugin implements Plugin {

	/** Manager responsible to load the class repository information for a sessions.xml. */
	private SCSessionsPropertiesManager propertiesManager;

	/** cache of properties pages, key by node-determined value */
	private Map propertiesPageCache;

	private static final String SC_PREFERENCES_NODE = "sc";

	public static final String DATA_SOURCE_TYPE_PREFERENCE  = "data source type";
	public static final String DEFAULT_CLASSPATH_PREFERENCE = "default classpath";
	public static final String NEW_NAME_BROKER_PREFERENCE   = "new name.broker";
	public static final String NEW_NAME_POOL_PREFERENCE     = "new name.pool";
	public static final String NEW_NAME_SESSION_PREFERENCE  = "new name.session";
	public static final String DATABASE_PLATFORM_PREFERENCE = "platform.database";
	public static final String EIS_PLATFORM_PREFERENCE      = "platform.eis";
	public static final String SERVER_PLATFORM_PREFERENCE   = "platform.server";
	public static final String NEW_NAME_SESSIONS_CONFIGURATION_PREFERENCE = "new name.sessions configuration";

	public static final String DATABASE_PLATFORM_PREFERENCE_DEFAULT = "Oracle11";
	public static final String EIS_PLATFORM_PREFERENCE_DEFAULT      = "AQPlatform";
	public static final String SERVER_PLATFORM_PREFERENCE_DEFAULT   = "WebLogic_10_Platform";

	public static final String DATA_SOURCE_TYPE_PREFERENCE_EIS_CHOICE = "eis";
	public static final String DATA_SOURCE_TYPE_PREFERENCE_RELATIONAL_CHOICE = "relational";
	public static final String DATA_SOURCE_TYPE_PREFERENCE_XML_CHOICE = "xml";

	// ********** constructor **********
	public SCPlugin() {
		super();
		initialize();
	}

	// ********** Initialization **********

	private void initialize() {
		this.propertiesPageCache = new HashMap();
	}

	// ********** Plugin implementation **********
	public JMenuItem[] buildNewMenuItems(WorkbenchContext context) {
		return new JMenuItem[] {buildNewConfigurationMenuItem(context)};
	}

	public JMenuItem[] buildMigrateMenuItems(WorkbenchContext context) {
		return new JMenuItem[0];
	}

	public ApplicationNode open(File file, WorkbenchContext context) throws UnsupportedFileException, OpenException {
		if ( ! FileTools.extension(file).equalsIgnoreCase(".xml")) {
			throw new UnsupportedFileException();
		}

		WorkbenchContext scContext = wrap( context);

		try {
			SCSessionsProperties properties = this.getSessionsProperties( scContext.getApplicationContext(), file);
			TopLinkSessionsAdapter topLinkSessions = new TopLinkSessionsAdapter( properties, scContext.getApplicationContext().getPreferences(), false);
			return new ProjectNode(topLinkSessions, scContext.getApplicationContext().getNodeManager().getRootNode(), this, scContext.getApplicationContext());
		} catch (Throwable t) {
			throw new OpenException(t);
		}
	}

	public ComponentContainerDescription buildToolBarDescription(WorkbenchContext context) {

		return new ToolBarButtonGroupDescription();
	}

	public ComponentContainerDescription buildMenuDescription(WorkbenchContext context) {

		return new MenuGroupDescription();
	}

	public PreferencesNode[] buildPreferencesNodes(PreferencesContext context) {

		return new PreferencesNode[] {new SCPreferencesNode((PreferencesContext) this.wrap(context))};
	}

	// ********** queries **********

	/**
	 * Returns the next untitled file name for a sessions.xml, the name will be
	 * "Sessions1.xml", then "Sessions2.xml" and so on.
	 *
	 * @return A non-fully qualified file name with this format SesssionX.xml
	 * where X is the next number of untitled sessions.xml that was created
	 */
	public File nextUntitledFile( ApplicationContext context) {

		return getPropertiesManager(context).nextUntitledSessionsFile();
	}

	// ********** behavior **********

	public void showWarning( WorkbenchContext context,
									 String message,
									 Throwable exception) {

		StringBuffer exceptionBuffer = new StringBuffer();
		String exceptionMessage = exception.getLocalizedMessage();

		// First start with the exception message
		if( exceptionMessage != null) {
			exceptionBuffer.append(exception.getLocalizedMessage());
		}

		// Remove new line at the beginning of the exception message (TopLink does that)
		if (exceptionBuffer.toString().startsWith(StringTools.CR)) {
			exceptionBuffer.replace(0, StringTools.CR.length(), "");
		}

		// For this type of exception, show all the causes
		if( exception instanceof SessionLoaderException) {
			SessionLoaderException loaderException = (SessionLoaderException) exception;

			Vector exceptions = loaderException.getExceptionList();

			if (exceptions != null) {
				for (Iterator iter = loaderException.getExceptionList().iterator(); iter.hasNext(); ) {
					Throwable topLinkException = (Throwable) iter.next();
					exceptionBuffer.append(StringTools.CR);
					exceptionBuffer.append("***");
					exceptionBuffer.append(topLinkException.getLocalizedMessage());
				}
			}
		}

		// The exception has no message, show the stack trace
		if( exceptionBuffer.length() == 0) {
			StringWriter writer = new StringWriter();
			PrintWriter printWriter = new PrintWriter(writer);
			exception.printStackTrace(printWriter);
			exceptionBuffer.append(writer.toString());
		}

		JOptionPane.showMessageDialog(context.getCurrentWindow(),
												buildErrorPaneArea(message, exceptionBuffer.toString()),
												context.getApplicationContext().getApplication().getShortProductName(),
												JOptionPane.OK_OPTION);
	}

	private Component buildErrorPaneArea(String message, String exception)
	{
		JPanel pane = new JPanel(new BorderLayout(0, 1));

		LabelArea label = new LabelArea(message);
		pane.add(label, BorderLayout.PAGE_START);

		JTextArea textArea = new JTextArea(exception);
		textArea.setFont(label.getFont());
		textArea.setTabSize(2);
		pane.add(new JScrollPane(textArea), BorderLayout.CENTER);

		pane.setPreferredSize(new Dimension(350, Math.min(pane.getPreferredSize().height + 50, 150)));

		return pane;
	}

	private JMenuItem buildNewConfigurationMenuItem( WorkbenchContext context) {
		
		return new JMenuItem( new AddNewSessionsAction(this.wrap( context), this));
	}

	private WorkbenchContext wrap(WorkbenchContext context) {
		return context.buildExpandedApplicationContextWorkbenchContext(wrap(context.getApplicationContext()));
	}

	private ApplicationContext wrap( ApplicationContext context) {
		ApplicationContext expandedContext = context.buildExpandedResourceRepositoryContext( SCPluginResourceBundle.class, new SCPluginIconResourceFileNameMap());
		expandedContext = expandedContext.buildExpandedResourceRepositoryContext( SCProblemsResourceBundle.class);
		return expandedContext.buildRedirectedPreferencesContext(SC_PREFERENCES_NODE);
	}

	// ********** properties page cache **********

	/**
	 * Return the properties page for the specified key.
	 * If the requested page is in the cache remove it and
	 * return it, otherwise return null.
	 */
	public Component getPropertiesPage(Object key) {
		return (Component) this.propertiesPageCache.remove(key);
	}

	/**
	 * Put the specified properties page back into the cache,
	 * using the specified key. For now, replace the existing
	 * properties page, allowing it to be garbage-collected.
	 */
	public void releasePropertiesPage(Object key, Component propertiesPage) {
		this.propertiesPageCache.put(key, propertiesPage);
	}

	// ********* sessions.xml classpath *******

	/**
	 * Retrieves the object containing the classpath information for any
	 * sessions.xml that was opened.
	 *
	 * @return The object containing the collection of sessions.xml where their
	 * classpath is stored, <code>null</code> is never returned
	 */
	private SCSessionsPropertiesManager getPropertiesManager(ApplicationContext context)
	{
		if (this.propertiesManager == null)
			this.propertiesManager = loadSessionsProperties(context);

		return this.propertiesManager;
	}

	/**
	 * Retrieves the object containing the classpath information for the desired
	 * sessions.xml specified with the given path.
	 *
	 * @return The object containing the classpath information for the desired
	 * sessions.xml, <code>null</code> is never returned
	 */
	public SCSessionsProperties getSessionsProperties(ApplicationContext context,
																	  File path)
	{
		return getPropertiesManager(context).getSessionsProperties(path);
	}

	/**
	 * Loads the information contained in the file sc.xml that is located in the
	 * application config directory.
	 *
	 * @return The collection of sessions.xml where their classpath is stored
	 */
	private SCSessionsPropertiesManager loadSessionsProperties(ApplicationContext context)
	{
		Preferences preferences = context.getPreferences();
		return new SCSessionsPropertiesManager(preferences);
	}
}
