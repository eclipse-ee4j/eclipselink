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
