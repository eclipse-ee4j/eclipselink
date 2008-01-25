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

import org.eclipse.persistence.internal.sessions.factories.model.transport.SunCORBATransportManagerConfig;

/**
 * Session Configuration model adapter class for the 
 * TopLink Foudation Library class SunCORBATransportManagerConfig
 * 
 * @see SunCORBATransportManagerConfig
 * 
 * @author Tran Le
 */
public final class SunCORBATransportManagerAdapter extends TransportManagerAdapter {

	/**
	 * Creates a new Discovery for the specified model object.
	 */
	SunCORBATransportManagerAdapter( SCAdapter parent, SunCORBATransportManagerConfig scConfig) {
	
		super( parent, scConfig);
	}
	/**
	 * Creates a new Discovery.
	 */
	protected SunCORBATransportManagerAdapter( SCAdapter parent) {
	
		super( parent);
	}
	/**
	 * Returns this Config Model Object.
	 */
	private final SunCORBATransportManagerConfig corbaManager() {
	
		return ( SunCORBATransportManagerConfig)this.getModel();
	}
	/**
	 * Factory method for building this model.
	 */
	protected Object buildModel() {

		return new SunCORBATransportManagerConfig();
	}
}