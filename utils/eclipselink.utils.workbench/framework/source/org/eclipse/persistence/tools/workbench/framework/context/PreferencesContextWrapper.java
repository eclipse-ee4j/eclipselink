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

import java.util.prefs.Preferences;

import org.eclipse.persistence.tools.workbench.framework.Application;
import org.eclipse.persistence.tools.workbench.framework.NodeManager;
import org.eclipse.persistence.tools.workbench.framework.help.HelpManager;
import org.eclipse.persistence.tools.workbench.framework.resources.ResourceRepository;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;


/**
 * This class wraps another context and delegates most of
 * the behavior to it. (The #build__() methods are NOT delegated -
 * they must return a wrapped version of THIS object, NOT the delegate.)
 * Subclasses can simply override the method(s) that should not be delegated.
 */
public abstract class PreferencesContextWrapper
	extends AbstractPreferencesContext
{
	private PreferencesContext delegate;


	// ********** constructor/initialization **********

	/**
	 * Construct an wrapper for the specified preferences context.
	 */
	public PreferencesContextWrapper(PreferencesContext delegate) {
		super();
		if (delegate == null) {
			throw new NullPointerException();
		}
		this.delegate = delegate;
	}


	// ********** ApplicationContext implementation **********

	/**
	 * @see ApplicationContext#getApplication()
	 */
	public Application getApplication() {
		return this.delegate.getApplication();
	}

	/**
	 * @see ApplicationContext#getPreferences()
	 */
	public Preferences getPreferences() {
		return this.delegate.getPreferences();
	}

	/**
	 * @see ApplicationContext#getResourceRepository()
	 */
	public ResourceRepository getResourceRepository() {
		return this.delegate.getResourceRepository();
	}

	/**
	 * @see ApplicationContext#getNodeManager()
	 */
	public NodeManager getNodeManager() {
		return this.delegate.getNodeManager();
	}

	/**
	 * @see ApplicationContext#getHelpManager()
	 */
	public HelpManager getHelpManager() {
		return this.delegate.getHelpManager();
	}


	// ********** PreferencesContext implementation **********

	/**
	 * @see PreferencesContext#getBufferTrigger()
	 */
	public ValueModel getBufferTrigger() {
		return this.delegate.getBufferTrigger();
	}


	// ********** additional behavior **********

	/**
	 * Allow access to the delegate.
	 */
	public PreferencesContext getDelegate() {
		return this.delegate;
	}

}
