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

import org.eclipse.persistence.internal.sessions.factories.model.pool.ReadConnectionPoolConfig;

/**
 * Session Configuration model adapter class for the 
 * TopLink Foudation Library class ReadConnectionPoolConfig
 * 
 * @see ReadConnectionPoolConfig
 * 
 * @author Tran Le
 */
public class ReadConnectionPoolAdapter extends ConnectionPoolAdapter {
	// Property change
	public static final String EXCLUSIVE_PROPERTY = "exclusive";
	public static final String USE_NON_TRANSACTIONAL_READ_LOGIN_PROPERTY = "useNonTransactionalReadLogin";
	private volatile boolean useNonTransactionalReadLogin;

	/**
	 * Creates a new ReadConnectionPool for the specified model object.
	 */
	ReadConnectionPoolAdapter( SCAdapter parent, ReadConnectionPoolConfig scConfig) {
		
		super( parent, scConfig);
	}
	/**
	 * Creates a new ReadConnectionPool.
	 */
	protected ReadConnectionPoolAdapter( SCAdapter parent) {
		
		super( parent, READ_CONNECTION_POOL_NAME);
	}
	/**
	 * Returns this Config Model Object.
	 */
	private final ReadConnectionPoolConfig readPool() {
		
		return ( ReadConnectionPoolConfig)this.getModel();
	}
	/**
	 * Factory method for building this model.
	 */
	protected Object buildModel() {
		return new ReadConnectionPoolConfig();
	}
	
	protected void initialize() {
		super.initialize( );

		this.setConfigRequired( true);
	}
    
    protected void initializeDefaults() {
        super.initializeDefaults();
        this.setExclusive( true);
    }
	/**
	 * Initializes this adapter.
	 * This does not have a Login at creation.
	 */
	protected void initialize( Object newConfig) {
		super.initialize( newConfig);

		this.setLogin( NullLoginAdapter.instance());
	}
	/**
	 * Initializes this adapter from the config model.
	 */
	protected void initializeFromModel( Object scConfig) {
		
		super.initializeFromModel( scConfig);

		this.useNonTransactionalReadLogin = ( readPool().getLoginConfig() != null);
	}
	
	protected void postInitializationFromModel() {
		super.postInitializationFromModel();
		
		if( this.getName().equals( "unamed"))
		    this.setName( ConnectionPoolAdapter.READ_CONNECTION_POOL_NAME);
		return;
	}	

	public boolean isExclusive() {
		return this.readPool().getExclusive();
	}

	/**
	 * Returns this config model property.
	 */
	public boolean isReadConnectionPool() {

		return true;
	}

	public void setExclusive(boolean exclusive) {

		boolean old = isExclusive();
		this.readPool().setExclusive( exclusive);
		this.firePropertyChanged( EXCLUSIVE_PROPERTY, old, exclusive);
	}

	public boolean usesNonTransactionalReadLogin() {

		return this.useNonTransactionalReadLogin;
	}

	public void setUseNonTransactionalReadLogin(boolean useNonTransactionalReadLogin) {

		boolean old = this.useNonTransactionalReadLogin;

		if( old == useNonTransactionalReadLogin) return;

		this.useNonTransactionalReadLogin = useNonTransactionalReadLogin;
		if( useNonTransactionalReadLogin)
			setLogin( buildLogin());
		else
			removeLogin();

		this.firePropertyChanged( USE_NON_TRANSACTIONAL_READ_LOGIN_PROPERTY, old, useNonTransactionalReadLogin);
	}
}
