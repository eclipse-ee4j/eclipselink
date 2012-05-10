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

import java.util.List;

import org.eclipse.persistence.internal.sessions.factories.model.login.LoginConfig;
import org.eclipse.persistence.internal.sessions.factories.model.pool.ConnectionPoolConfig;

/**
 * Session Configuration model adapter class for the 
 * TopLink Foudation Library class ConnectionPoolConfig
 * 
 * @see ConnectionPoolConfig
 * 
 * @author Tran Le
 */
public class ConnectionPoolAdapter extends SCAdapter implements Nominative, LoginHandler {
	// property change
	public static final String MAX_CONNECTIONS_PROPERTY = "maxConnections";
	public static final String MIN_CONNECTIONS_PROPERTY = "minConnections";
	private volatile LoginAdapter login;
	public final static String LOGIN_CONFIG_PROPERTY = "loginConfig";

	public static final String READ_CONNECTION_POOL_NAME = "ReadConnectionPool";
	public static final String SEQUENCE_CONNECTION_POOL_NAME = "SequenceConnectionPool";
	/**
	 * Creates a new ConnectionPoolAdapter for the specified model object.
	 */
	ConnectionPoolAdapter( SCAdapter parent, ConnectionPoolConfig scConfig) {
		
		super( parent, scConfig);
	}
	/**
	 * Creates a new ConnectionPoolAdapter.
	 */
	protected ConnectionPoolAdapter( SCAdapter parent, String name) {
		
		super( parent);	
			
		this.initializeType( name);
	}
	/**
	 * Returns this Config Model Object.
	 */
	private final ConnectionPoolConfig pool() {
		
		return ( ConnectionPoolConfig)this.getModel();
	}
	/**
	 * Factory method for building this model.
	 */
	protected Object buildModel() {

		return new ConnectionPoolConfig();
	}
	
	protected LoginAdapter buildLogin() {
	    
		if( this.platformIsEis()) {
		    return new EISLoginAdapter( this);
		} else if ( this.platformIsXml() ) {
			return new XMLLoginAdapter(this);
		}
		return new DatabaseLoginAdapter( this);
	}
	/**
	 * Adds the children of this adapter to the given list.
	 * @param children The list of children
	 */
	protected void addChildrenTo( List children) {
		super.addChildrenTo( children);

		if( this.getLogin() !=  NullLoginAdapter.instance())
			children.add( getLogin());
	}
	/**
	 * Initializes this new model.
	 */
	private void initializeType( String name) {

		this.setName( name);
		this.initializeTypeDefaults();
	}
	
	protected void initializeTypeDefaults() {

		if( pool().getMinConnections() == null)
			setMinConnections( 2);

		if( pool().getMaxConnections() == null)
			setMaxConnections( 2);
	}
	/**
	 * Initializes this adapter.
	 */
	protected void initialize( Object newConfig) {
		super.initialize( newConfig);

		this.setLogin( buildLogin());
	}
	/**
	 * Initializes this adapter from the specified config model.
	 */
	protected void initializeFromModel( Object scConfig) {
		
		super.initializeFromModel( scConfig);

		this.login = getLoginFromModel();
	}
	
	protected void postInitializationFromModel() {
		super.postInitializationFromModel();
		
		if( this.getName() == null)
		    this.setName( "unamed");
		return;
	}	
	/**
	 * Returns the Login adapter for this config model.
	 */	
	protected LoginAdapter getLoginFromModel() {
		
	    if( this.pool().getLoginConfig() == null) {
	        return NullLoginAdapter.instance();
	    }
		return ( LoginAdapter)this.adapt( this.pool().getLoginConfig());
	}
	/**
	 * Returns this config model property.
	 */
	public String getName() {
		
		return this.pool().getName();
	}
	/**
	 * Sets this config model property.
	 */
	public void setName( String name) {
		
		Object old = this.pool().getName();

		this.pool().setName( name);
		this.firePropertyChanged( NAME_PROPERTY, old, name);
	}
	
	public String displayString() {
		
		return this.getName();
	}

	public void toString( StringBuffer sb) {
		
		sb.append( this.getName());
	}
	
	public boolean platformIsRdbms() {

		return (( PoolsAdapter)getParent()).platformIsRdbms();
	}
	
	public boolean platformIsEis() {

		return (( PoolsAdapter)getParent()).platformIsEis();
	}
	
	public boolean platformIsXml() {

		return (( PoolsAdapter)getParent()).platformIsXml();
	}
	/**
	 * Returns this config model property.
	 */
	public boolean isReadConnectionPool() {

		return false;
	}
	/**
	 * Returns this config model property.
	 */
	public boolean isSequenceConnectionPool() {
		
		return this.pool().getName().equals( ConnectionPoolAdapter.SEQUENCE_CONNECTION_POOL_NAME);
	}
	/**
	 * Returns this config model property.
	 */
	public boolean isWriteConnectionPool() {
		
		return false;
	}

	public void setMaxConnections( int maxConnections) {

		int oldMaxConnections = getMaxConnections();
		this.pool().setMaxConnections( new Integer(maxConnections));
		firePropertyChanged( MAX_CONNECTIONS_PROPERTY, oldMaxConnections, maxConnections);
	}

	public int getMaxConnections() {

		Integer max = this.pool().getMaxConnections();
		return ( max == null) ? 0 : max.intValue();
	}

	public void setMinConnections( int minConnections) {

		int oldMinConnections = getMinConnections();
		this.pool().setMinConnections( new Integer( minConnections));
		firePropertyChanged( MIN_CONNECTIONS_PROPERTY, oldMinConnections, minConnections);
	}

	public int getMinConnections() {

		Integer min = this.pool().getMinConnections();
		return ( min == null) ? 0 : min.intValue();
	} 
	/**
	 * Returns this login adapter.
	 */
	public LoginAdapter getLogin() {
		
		return this.login;
	}
	/**
	 * Sets the login adapter into this model.
	 */
	protected void setLogin( LoginAdapter login) {

		LoginAdapter old = this.login;
		this.login = login;
		this.pool().setLoginConfig(( LoginConfig)this.login.getModel());
		firePropertyChanged( LOGIN_CONFIG_PROPERTY, old, this.login);
	}
	/**
	 * Removes the login adapter from this model.
	 */
	protected void removeLogin() {

		this.setLogin( NullLoginAdapter.instance());
	}

    public void setExternalConnectionPooling( boolean value) {
        // Don't do anything because this is a ConnectionPool
        return; 
    }

    public boolean usesExternalConnectionPooling() {
		
		return false;
    }
}
