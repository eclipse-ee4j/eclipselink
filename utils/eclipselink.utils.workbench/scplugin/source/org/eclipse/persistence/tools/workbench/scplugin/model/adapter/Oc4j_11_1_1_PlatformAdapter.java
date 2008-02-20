/*******************************************************************************
 * Copyright (c) 1998, 2008 Oracle. All rights reserved.
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

import org.eclipse.persistence.internal.sessions.factories.model.platform.oc4j.Oc4j_11_1_1_PlatformConfig;

/**
 * Session Configuration model adapter class for the 
 * TopLink Foudation Library class Oc4j_11_1_1_PlatformConfig
 * 
 * @see Oc4j_11_1_1_PlatformConfig
 * 
 * @author Les Davis
 */
public final class Oc4j_11_1_1_PlatformAdapter extends ServerPlatformAdapter {

	/**
	 * Default constructor
	*/
	Oc4j_11_1_1_PlatformAdapter() {
		super();
	}
	
	/**
	* Creates a new Platform for the specified model object.
	*/
	Oc4j_11_1_1_PlatformAdapter( SCAdapter parent, Oc4j_11_1_1_PlatformConfig scConfig) {
		super( parent, scConfig);
	}
		
	/**
	* Creates a new Platform.
	*/
	protected Oc4j_11_1_1_PlatformAdapter( SCAdapter parent) {
		super( parent);
	}
	
	/**
	* Returns this Config Model Object.
	*/
	private final Oc4j_11_1_1_PlatformConfig platformConfig() {
		return ( Oc4j_11_1_1_PlatformConfig)this.getModel();
	}
	
	/**
	* Factory method for building this model.
	*/
	protected Object buildModel() {
		return new Oc4j_11_1_1_PlatformConfig();
	}

}
