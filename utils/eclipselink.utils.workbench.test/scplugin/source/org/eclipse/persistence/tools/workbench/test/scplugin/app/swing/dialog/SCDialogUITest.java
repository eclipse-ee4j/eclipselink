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
package org.eclipse.persistence.tools.workbench.test.scplugin.app.swing.dialog;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.eclipse.persistence.tools.workbench.test.scplugin.app.SCTestNodeManager;
import org.eclipse.persistence.tools.workbench.test.scplugin.app.swing.SCAbstractUITest;

import org.eclipse.persistence.tools.workbench.framework.NodeManager;
import org.eclipse.persistence.tools.workbench.framework.app.ApplicationNode;
import org.eclipse.persistence.tools.workbench.framework.context.SimpleWorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.internal.FrameworkIconResourceFileNameMap;
import org.eclipse.persistence.tools.workbench.framework.internal.FrameworkResourceBundle;
import org.eclipse.persistence.tools.workbench.framework.resources.DefaultResourceRepository;
import org.eclipse.persistence.tools.workbench.framework.resources.ResourceRepository;
import org.eclipse.persistence.tools.workbench.framework.resources.ResourceRepositoryWrapper;
import org.eclipse.persistence.tools.workbench.scplugin.SCPluginIconResourceFileNameMap;
import org.eclipse.persistence.tools.workbench.scplugin.SCPluginResourceBundle;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.TopLinkSessionsAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.ui.project.ProjectNode;

/**
 * @author Tran Le
 */
public abstract class SCDialogUITest extends SCAbstractUITest {

	private WorkbenchContext wbContext;
	private NodeManager nodeManager;
	private ResourceRepository resourceRepository;
	protected JFrame currentWindow;
	protected int windowW, windowH, windowX;
	
	protected SCDialogUITest() {
		super();
		initialize();
	}

	protected void setUp() {
		super.setUp();
		
		this.resourceRepository = this.buildResourceRepository();
		this.currentWindow = new JFrame( this.windowTitle());
		
		ApplicationNode projectNode = buildProjectNode( this.topLinkSessions(), resourceRepository, currentWindow);

		this.nodeManager = buildNodeManager( projectNode);
	
		this.wbContext = buildWorkbenchContext( this.resourceRepository, this.nodeManager, this.currentWindow);
	}
	
	private NodeManager buildNodeManager( ApplicationNode projectNode) {

		return new SCTestNodeManager( projectNode);
	}

	private ApplicationNode buildProjectNode( TopLinkSessionsAdapter topLinkSessions, ResourceRepository resourceRepository, Window currentWindow) {

//		SCPlugin scPlugin = (SCPlugin) SCPluginFactory.instance().createPlugin();
//		SCSessionsProperties properties = scPlugin.getSessionsProperties( buildWorkbenchContext( this.resourceRepository, null, this.currentWindow),
//																		new File( this.getFileName()));
//		topLinkSessions.setProperties( properties);
		
		return new ProjectNode( topLinkSessions, null, null, buildWorkbenchContext( this.resourceRepository, null, this.currentWindow).getApplicationContext());
	}

	private WorkbenchContext buildWorkbenchContext( ResourceRepository resourceRepository, NodeManager nodeManager, Window currentWindow) {

		return new SimpleWorkbenchContext(null, null, resourceRepository, nodeManager, this.buildHelpManager(), currentWindow, null, null, null);
	}

	private ResourceRepository buildResourceRepository() {

		ResourceRepository frameworkRepository = new DefaultResourceRepository( FrameworkResourceBundle.class, new FrameworkIconResourceFileNameMap());
		return new ResourceRepositoryWrapper( frameworkRepository, getResourceRepositoryClass(), new SCPluginIconResourceFileNameMap());
	}

	protected void openWindow() {
		
		currentWindow.setDefaultCloseOperation( JFrame.DO_NOTHING_ON_CLOSE);
		currentWindow.addWindowListener( this.buildWindowListener());
		currentWindow.getContentPane().add( this.buildMainPanel(), "Center");
		currentWindow.setLocation( windowX, 0);
		currentWindow.setSize( windowW, windowH);
		currentWindow.setVisible( true);
	}
	protected final ResourceRepository resourceRepository() {

		return resourceRepository;
	}
	protected Class getResourceRepositoryClass()
	{
		return SCPluginResourceBundle.class;
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
		mainPanel.add( this.buildTestingPanel(), BorderLayout.NORTH);
		mainPanel.add( this.buildControlPanel(), BorderLayout.SOUTH);
		return mainPanel;
	}

	protected abstract Component buildTestingPanel();

	protected Component buildControlPanel() {
		
		JPanel controlPanel = new JPanel( new GridLayout( 1, 0));
		controlPanel.add( this.buildClearModelButton());
		controlPanel.add( this.buildPrintModelButton());
		return controlPanel;
	}
	
	protected JButton buildClearModelButton() {
		return new JButton( this.buildClearModelAction());
	}

	private Action buildClearModelAction() {
		Action action = new AbstractAction("clear model") {
			public void actionPerformed(ActionEvent event) {
				SCDialogUITest.this.clearModel();
			}
		};
		action.setEnabled(true);
		return action;
	}

	protected JButton buildPrintModelButton() {
		return new JButton( this.buildPrintModelAction());
	}
	
	private Action buildPrintModelAction() {
		Action action = new AbstractAction( "print model") {
			public void actionPerformed( ActionEvent event) {
				SCDialogUITest.this.printModel();
			}
		};
		action.setEnabled( true);
		return action;
	}

	protected WorkbenchContext getWorkbenchContext() {
		return this.wbContext;
	}
	
	protected Window currentWindow() {
		return currentWindow;
	}
	
	protected Component buildPropertyTestingPanel() {
		// TODO Auto-generated method stub
		return null;
	}
	protected void resetProperty() {
		// TODO Auto-generated method stub
	}
	protected void restoreModel() {
		// TODO Auto-generated method stub
	}

	protected void clearModel() {
		// TODO Auto-generated method stub
	}
	
	protected void printModel() {
		System.out.println( this.topLinkSessions().toString());
	}
}
