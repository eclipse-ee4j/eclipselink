/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.scplugin.model.adapter;
import org.eclipse.persistence.internal.sessions.factories.model.platform.WebLogic_9_PlatformConfig;

/**
 * Session Configuration model adapter class for the 
 * TopLink Foudation Library class WebLogic_9_PlatformConfig
 * 
 * @see WebLogic_9_PlatformConfig
 * 
 * @author Les Davis
 */
public class WebLogic_9_PlatformAdapter extends ServerPlatformAdapter {

	/**
	 * Default constructor
	 */
	WebLogic_9_PlatformAdapter() {
		super();
	}
	/**
	 * Creates a new Platform for the specified model object.
	 */
	WebLogic_9_PlatformAdapter( SCAdapter parent, WebLogic_9_PlatformConfig scConfig) {
		
		super( parent, scConfig);
	}
	/**
	 * Creates a new Platform.
	 */
	protected WebLogic_9_PlatformAdapter( SCAdapter parent) {
		
		super( parent);
	}
	/**
	 * Returns this Config Model Object.
	 */
	private final WebLogic_9_PlatformConfig platformConfig() {
		
		return ( WebLogic_9_PlatformConfig)this.getModel();
	}
	/**
	 * Factory method for building this model.
	 */
	protected Object buildModel() {
		return new WebLogic_9_PlatformConfig();
	}
	
}

