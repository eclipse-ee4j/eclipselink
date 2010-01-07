/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
package org.eclipse.persistence.tools.workbench.test.scplugin.app.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.prefs.Preferences;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.eclipse.persistence.tools.workbench.test.scplugin.AllSCTests;

import org.eclipse.persistence.tools.workbench.framework.context.SimpleWorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.help.HelpManager;
import org.eclipse.persistence.tools.workbench.framework.internal.FrameworkIconResourceFileNameMap;
import org.eclipse.persistence.tools.workbench.framework.internal.FrameworkResourceBundle;
import org.eclipse.persistence.tools.workbench.framework.resources.DefaultResourceRepository;
import org.eclipse.persistence.tools.workbench.framework.resources.ResourceRepository;
import org.eclipse.persistence.tools.workbench.framework.resources.ResourceRepositoryWrapper;
import org.eclipse.persistence.tools.workbench.scplugin.SCPluginIconResourceFileNameMap;
import org.eclipse.persistence.tools.workbench.scplugin.SCPluginResourceBundle;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.ServerPlatform;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.TopLinkSessionsAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.meta.SCSessionsProperties;
import org.eclipse.persistence.tools.workbench.scplugin.model.meta.SCSessionsPropertiesManager;
import org.eclipse.persistence.tools.workbench.utility.ClassTools;

/**
 * Based class for testing Session Configuration properties.
 * Loads the testing sessions.xml. And encapsulates all the
 * knowledge to build the main test window.
 */
public abstract class SCAbstractUITest {

	private TopLinkSessionsAdapter topLinkSessions;
	private WorkbenchContext workbenchContext;
	protected String filename;
	protected JFrame window;
	protected int windowW, windowH, windowX;
	
	protected SCAbstractUITest() {
		super();
		initialize();
	}

	protected void setUp() {

		Preferences preferences = Preferences.userNodeForPackage(AllSCTests.class);

		SCSessionsPropertiesManager manager = new SCSessionsPropertiesManager(preferences);
		SCSessionsProperties properties = manager.getSessionsProperties(new File(filename));

		topLinkSessions = new TopLinkSessionsAdapter( properties, preferences, true);
		this.window = new JFrame( this.windowTitle());
		this.workbenchContext = new SimpleWorkbenchContext(null, null, buildResourceRepository(), null, this.buildHelpManager(), window, null, null, null);

	}

	protected void openWindow() {
		
		window.setDefaultCloseOperation( JFrame.DO_NOTHING_ON_CLOSE);
		window.addWindowListener( this.buildWindowListener());
		window.getContentPane().add( this.buildMainPanel(), "Center");
		window.setLocation( windowX, 0);
		window.setSize( windowW, windowH);
		window.setVisible( true);
	}
	
	private ResourceRepository buildResourceRepository() {

		ResourceRepository frameworkRepository = new DefaultResourceRepository( FrameworkResourceBundle.class, new FrameworkIconResourceFileNameMap());
		return new ResourceRepositoryWrapper( frameworkRepository, SCPluginResourceBundle.class, new SCPluginIconResourceFileNameMap());
	}
	
	protected final WorkbenchContext workbenchContext()
	{
		return this.workbenchContext;
	}
	protected abstract String windowTitle();
	
	protected WindowListener buildWindowListener() {
		
		return new WindowAdapter() {
			public void windowClosing( WindowEvent e) {
				e.getWindow().setVisible( false);
				System.exit( 0);
			}
		};
	}

	protected Component buildMainPanel() {
		
		JPanel mainPanel = new JPanel( new BorderLayout());
		mainPanel.add( this.buildPropertyTestingPanel(), BorderLayout.NORTH);
		mainPanel.add( this.buildControlPanel(), BorderLayout.SOUTH);
		return mainPanel;
	}

	protected abstract Component buildPropertyTestingPanel();

	protected Component buildControlPanel() {
		
		JPanel controlPanel = new JPanel( new GridLayout( 1, 0));
		controlPanel.add( this.buildResetPropertyButton());
		controlPanel.add( this.buildClearModelButton());
		controlPanel.add( this.buildRestoreModelButton());
		controlPanel.add( this.buildPrintModelButton());
		return controlPanel;
	}
	
	protected JButton buildResetPropertyButton() {
		return new JButton(this.buildResetPropertyAction());
	}
	
	private Action buildResetPropertyAction() {
		Action action = new AbstractAction( "reset property") {
			public void actionPerformed (ActionEvent event) {
				SCAbstractUITest.this.resetProperty();
			}
		};
		action.setEnabled( true);
		return action;
	}
	
	protected abstract void resetProperty();

	protected JButton buildClearModelButton() {
		return new JButton( this.buildClearModelAction());
	}

	private Action buildClearModelAction() {
		Action action = new AbstractAction("clear model") {
			public void actionPerformed(ActionEvent event) {
				SCAbstractUITest.this.clearModel();
			}
		};
		action.setEnabled(true);
		return action;
	}

	protected abstract void clearModel();
	
	protected TopLinkSessionsAdapter getTopLinkSessions() {
		return topLinkSessions;
	}
	
	protected String getFileName() {
		return filename;
	}
	
	protected JButton buildRestoreModelButton() {
		return new JButton( this.buildRestoreModelAction());
	}

	private Action buildRestoreModelAction() {
		Action action = new AbstractAction("restore model") {
			public void actionPerformed(ActionEvent event) {
				SCAbstractUITest.this.restoreModel();
			}
		};
		action.setEnabled(true);
		return action;
	}

	protected abstract void restoreModel();

	protected ServerPlatform noServerPlatform() {

		return new ServerPlatform( "NoServerPlatform");
	}

	protected JButton buildPrintModelButton() {
		return new JButton( this.buildPrintModelAction());
	}
	
	private Action buildPrintModelAction() {
		Action action = new AbstractAction( "print model") {
			public void actionPerformed( ActionEvent event) {
				SCAbstractUITest.this.printModel();
			}
		};
		action.setEnabled( true);
		return action;
	}

	protected abstract void printModel();

	protected TopLinkSessionsAdapter topLinkSessions() {
		return this.topLinkSessions;
	}
	
	protected void initialize() {
		this.filename = "scplugin/resource/SessionsXMLTestModel/XMLSchemaSessions.xml";
		windowW = 400;
		windowH = 100;
		try {
			InetAddress address = InetAddress.getLocalHost();

			if ("138.2.91.83".equals(address.getHostAddress()))
				windowX = 1600;
		}
		catch( UnknownHostException e) {}
	}
	
	protected HelpManager buildHelpManager() {
		try {
			return (HelpManager) ClassTools.newInstance("org.eclipse.persistence.tools.workbench.framework.help.NullHelpManager");
		} catch (ClassNotFoundException ex) {
			throw new RuntimeException(ex);
		}
	}
	
}
