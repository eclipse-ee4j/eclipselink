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
package org.eclipse.persistence.tools.workbench.scplugin.model.adapter;

import org.eclipse.persistence.internal.sessions.factories.model.platform.Oc4jPlatformConfig;

/**
 * Session Configuration model adapter class for the 
 * EclipseLink Foundation Library class Oc4jPlatformConfig
 * 
 * @see Oc4jPlatformConfig
 * 
 * @author Les Davis
 */
public final class Oc4jPlatformAdapter extends ServerPlatformAdapter {

	/**
	 * Default constructor
	*/
	Oc4jPlatformAdapter() {
		super();
	}
	
	/**
	* Creates a new Platform for the specified model object.
	*/
	Oc4jPlatformAdapter( SCAdapter parent, Oc4jPlatformConfig scConfig) {
		super( parent, scConfig);
	}
		
	/**
	* Creates a new Platform.
	*/
	protected Oc4jPlatformAdapter( SCAdapter parent) {
		super( parent);
	}
	
	/**
	* Returns this Config Model Object.
	*/
	private final Oc4jPlatformConfig platformConfig() {
		return ( Oc4jPlatformConfig)this.getModel();
	}
	
	/**
	* Factory method for building this model.
	*/
	protected Object buildModel() {
		return new Oc4jPlatformConfig();
	}

}
