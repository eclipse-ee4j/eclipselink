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
package org.eclipse.persistence.tools.workbench.scplugin.model.adapter;

import org.eclipse.persistence.internal.sessions.factories.XMLSessionConfigProject;
import org.eclipse.persistence.internal.sessions.factories.model.platform.ServerPlatformConfig;

/**
 * Session Configuration model adapter class for the 
 * TopLink Foudation Library class ServerPlatformConfig
 * 
 * @see ServerPlatformConfig
 * 
 * @author Tran Le
 */
public abstract class ServerPlatformAdapter extends SCAdapter {
	// property change
	public final static String ENABLE_JTA_PROPERTY = "enableJTA";
	public final static String ENABLE_RUNTIME_SERVICES_PROPERTY = "enableRuntimeServices";

	/**
	 * Default constructor
	 */
	ServerPlatformAdapter() {
		super();
	}
	/**
	 * Creates a new Platform for the specified model object.
	 */
    ServerPlatformAdapter( SCAdapter parent, ServerPlatformConfig scConfig) {
		
		super( parent, scConfig);
	}
	/**
	 * Creates a new Platform.
	 */
	protected ServerPlatformAdapter( SCAdapter parent) {
		
		super( parent);
	}

	public boolean isCustom() {
		
		return false;
	}

	public boolean isNull() {
		
		return false;
	}
	/**
	 * Initializes default values.
	 */
	protected void initializeDefaults() {

		super.initializeDefaults();

		setEnableJTA( XMLSessionConfigProject.ENABLE_RUNTIME_SERVICES_DEFAULT);
		setEnableRuntimeServices( XMLSessionConfigProject.ENABLE_JTA_DEFAULT);
	}

	/**
	 * Returns this Config Model Object.
	 */
	private final ServerPlatformConfig serverPlatform() {
		
		return ( ServerPlatformConfig)this.getModel();
	}
	/**
	 * Returns this config model property.
	 */
	public boolean getEnableJTA() {
		
		return this.serverPlatform().getEnableJTA();
	}
	/**
	 * Sets this config model property.
	 */
	public void setEnableJTA( boolean value) {
		
	    boolean old = this.serverPlatform().getEnableJTA();

		this.serverPlatform().setEnableJTA( value);
		
		(( SessionAdapter)this.getParent()).externalTransactionControllerClassChanged();
		this.firePropertyChanged( ENABLE_JTA_PROPERTY, old, value);
	}
	/**
	 * Returns this config model property.
	 */
	public boolean getEnableRuntimeServices() {
		
		return this.serverPlatform().getEnableRuntimeServices();
	}
	/**
	 * Sets this config model property.
	 */
	public void setEnableRuntimeServices( boolean value) {
		
	    boolean old = this.serverPlatform().getEnableRuntimeServices();

		this.serverPlatform().setEnableRuntimeServices( value);
		this.firePropertyChanged( ENABLE_RUNTIME_SERVICES_PROPERTY, old, value);
	}
	/**
	 * Returns this config model property.
	 */
	public String getServerClassName() {
		
		return this.serverPlatform().getServerClassName();
	}

}
