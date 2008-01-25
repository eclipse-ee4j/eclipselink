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

import org.eclipse.persistence.internal.sessions.factories.model.log.ServerLogConfig;

/**
 * Session Configuration model adapter class for the 
 * TopLink Foudation Library class ServerLogConfig
 * 
 * @see ServerLogConfig
 * 
 * @author Tran Le
 */
public final class ServerLogAdapter extends LogAdapter {

	/**
	 * Creates a new ServerLogAdapter for the specified model object.
	 */
	ServerLogAdapter( SCAdapter parent, ServerLogConfig scConfig) {
		
		super( parent, scConfig);
	}
	/**
	 * Creates a new JavaLogAdapter.
	 */
	protected ServerLogAdapter( SCAdapter parent) {
		
		super( parent);
	}
	/**
	 * Factory method for building this model.
	 */
	protected Object buildModel() {
		return new ServerLogConfig();
	}
	
	public void toString( StringBuffer sb) {
		
		sb.append( "TODO: toString()");
	}
	
	/**
	 * Returns this Config Model Object.
	 */
	private final ServerLogConfig log() {
		
		return ( ServerLogConfig)this.getModel();
	}
	/**
	 * Returns true if options are used.
	 */
	public boolean optionsIsEnable() {
		return false;
	}	
	
	public void enableOptions() {
		// do nothing
	}	
	
	public void disableOptions() {
		// do nothing
	}	
}
