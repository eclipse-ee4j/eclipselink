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
package org.eclipse.persistence.tools.workbench.framework.context;

import java.awt.Component;
import java.awt.Window;
import java.util.prefs.Preferences;

import org.eclipse.persistence.tools.workbench.framework.Application;
import org.eclipse.persistence.tools.workbench.framework.NodeManager;
import org.eclipse.persistence.tools.workbench.framework.action.ActionRepository;
import org.eclipse.persistence.tools.workbench.framework.app.NavigatorSelectionModel;
import org.eclipse.persistence.tools.workbench.framework.help.HelpManager;
import org.eclipse.persistence.tools.workbench.framework.resources.ResourceRepository;


/**
 * Straightforward implementation of WorkbenchContext:
 * the implementation state matches the specified state.
 */
public class SimpleWorkbenchContext
	extends AbstractWorkbenchContext
{
	private ApplicationContext applicationContext;
	
	Application application;
	Preferences preferences;
	ResourceRepository resourceRepository;
	NodeManager nodeManager;
	HelpManager helpManager;

	private Window currentWindow;
	private NavigatorSelectionModel navigatorSelectionModel;
    private ActionRepository actionRepository;
    private Component propertiesPage;


	// ********** Constructor **********

	/** Use this when creating an workbench context from scratch */
	public SimpleWorkbenchContext(
		Application application,
		Preferences preferences,
		ResourceRepository resourceRepository,
		NodeManager nodeManager,
		HelpManager helpManager,
		Window currentWindow,
		NavigatorSelectionModel navigatorSelectionModel,
		ActionRepository actionRepository,
        Component propertiesPage
	) {
		super();

		this.application = application;
		this.preferences = preferences;
		this.resourceRepository = resourceRepository;
		this.nodeManager = nodeManager;
		this.helpManager = helpManager;
		
		this.applicationContext = buildApplicationContext();

		this.currentWindow = currentWindow;
		this.navigatorSelectionModel = navigatorSelectionModel;
		this.actionRepository = actionRepository;
        this.propertiesPage = propertiesPage;
	}

	protected ApplicationContext buildApplicationContext() {
		return new AbstractApplicationContext() {
			
			public Application getApplication() {
				return SimpleWorkbenchContext.this.application;
			}
			
			public Preferences getPreferences() {
				return SimpleWorkbenchContext.this.preferences;
			}
			
			public ResourceRepository getResourceRepository() {
				return SimpleWorkbenchContext.this.resourceRepository;
			}

			public NodeManager getNodeManager() {
				return SimpleWorkbenchContext.this.nodeManager;
			}

			public HelpManager getHelpManager() {
				return SimpleWorkbenchContext.this.helpManager;
			}
		};	
	}

	// ********** ApplicationContext implementation **********

	/**
	 * @see ApplicationContext#getApplication()
	 */
	public Application getApplication() {
		return this.application;
	}

	/**
	 * @see ApplicationContext#getPreferences()
	 */
	public Preferences getPreferences() {
		return this.preferences;
	}

	/**
	 * @see ApplicationContext#getResourceRepository()
	 */
	public ResourceRepository getResourceRepository() {
		return this.resourceRepository;
	}

	/**
	 * @see ApplicationContext#getNodeManager()
	 */
	public NodeManager getNodeManager() {
		return this.nodeManager;
	}

	/**
	 * @see ApplicationContext#getHelpManager()
	 */
	public HelpManager getHelpManager() {
		return this.helpManager;
	}


	// ********** WorkbenchContext implementation **********

	/**
	 * @see WorkbenchContext#getApplicationContext()
	 */
	public ApplicationContext getApplicationContext() {
		return this.applicationContext;
	}
	
	/**
	 * @see WorkbenchContext#getCurrentWindow()
	 */
	public Window getCurrentWindow() {
		return this.currentWindow;
	}

	/**
	 * @see WorkbenchContext#getNavigatorSelectionModel()
	 */
	public NavigatorSelectionModel getNavigatorSelectionModel() {
		return this.navigatorSelectionModel;
	}

	/**
	 * @see WorkbenchContext#getActionRepository()
	 */
	public ActionRepository getActionRepository() {
		return this.actionRepository;
	}
    
    /**
     * @see WorkbenchContext#getPropertiesPage()
     */
    public Component getPropertiesPage() {
        return this.propertiesPage;
    }

}
