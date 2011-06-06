/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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

import org.eclipse.persistence.internal.sessions.factories.model.pool.WriteConnectionPoolConfig;

/**
 * Session Configuration model adapter class for the 
 * TopLink Foudation Library class WriteConnectionPoolConfig
 * 
 * @see WriteConnectionPoolConfig
 * 
 * @author Tran Le
 */
public class WriteConnectionPoolAdapter extends ConnectionPoolAdapter {
	/**
	 * Creates a new ReadConnectionPool for the specified model object.
	 */
	WriteConnectionPoolAdapter( SCAdapter parent, WriteConnectionPoolConfig scConfig) {
		
		super( parent, scConfig);
	}
	/**
	 * Creates a new ReadConnectionPool.
	 */
	protected WriteConnectionPoolAdapter( SCAdapter parent) {
		
		super( parent, "");
	}
	/**
	 * Returns this Config Model Object.
	 */
	private final WriteConnectionPoolConfig writePool() {
		
		return ( WriteConnectionPoolConfig)this.getModel();
	}
	/**
	 * Factory method for building this model.
	 */
	protected Object buildModel() {
		return new WriteConnectionPoolConfig();
	}
	/**
	 * This does not have a Login (independently of the platform, i.e. rdb or eis)
	 */
	protected LoginAdapter buildLogin() {
	    
	    return NullLoginAdapter.instance();
	}
	
	public boolean isWriteConnectionPool() {
		return true;
	}
	/**
	 * Returns the Login adapter for this config model.
	 */	
	protected LoginAdapter getLoginFromModel() {
		
		return NullLoginAdapter.instance();
	}
	
	protected void initializeTypeDefaults() {
	    // Don't call super; override behavior only

		if( writePool().getMinConnections() == null)
			setMinConnections( 5);

		if( writePool().getMaxConnections() == null)
			setMaxConnections( 10);
	}
}
