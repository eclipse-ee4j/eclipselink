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

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.eclipse.persistence.internal.sessions.factories.model.event.SessionEventManagerConfig;
import org.eclipse.persistence.internal.sessions.factories.model.pool.ConnectionPolicyConfig;
import org.eclipse.persistence.internal.sessions.factories.model.pool.PoolsConfig;
import org.eclipse.persistence.internal.sessions.factories.model.session.ServerSessionConfig;

/**
 * Session Configuration model adapter class for the 
 * TopLink Foudation Library class ServerSessionConfig
 * 
 * @see ServerSessionConfig
 * 
 * @author Tran Le
 */
public final class ServerSessionAdapter extends DatabaseSessionAdapter {
	// property change
	private volatile PoolsAdapter pools;
	public final static String POOLS_CONFIG_COLLECTION = "pools";
	private volatile ConnectionPolicyAdapter connectionPolicy;
	public final static String USE_EXCLUSIVE_CONNECTION_PROPERTY = "useExclusiveConnection";	
	public final static String LAZY_CONNECTION_PROPERTY = "lazyConnection";	
	public final static String READ_CONNECTION_POOL_PROPERTY = "readConnectionPool";	
	public final static String WRITE_CONNECTION_POOL_PROPERTY = "writeConnectionPool";	
	public final static String SEQUENCE_CONNECTION_POOL_PROPERTY = "sequenceConnectionPool";	
	/**
	 * Creates a new ServerSessionAdapter for the specified model object.
	 */
	ServerSessionAdapter( SCAdapter parent, ServerSessionConfig scConfig) {
		
		super( parent, scConfig);
		
		this.initializePoolsFromModel();
	}
	/**
	 * Creates a new ServerSession.
	 */
	protected ServerSessionAdapter( SCAdapter parent, String name, ServerPlatform sp, DataSource ds) {
		
		super( parent, name, sp, ds);
		
		this.initializePools();
	}

	/**
	 * Subclasses should override this method to add their children
	 * to the specified collection.
	 * @see #children()
	 */
	protected void addChildrenTo( List children) {

		super.addChildrenTo(children);
		if( getPools() != null)
		    children.add( getPools());
	}

	/**
	 * Factory method for building this model.
	 */
	protected Object buildModel() {

		ServerSessionConfig session = new ServerSessionConfig();
		//TOREVIEW - collection not initialized in model class & SessionEventManagerConfig is not initialized
		//FL_TOREVIEW
		session.setAdditionalProjects( new Vector());
		session.setSessionEventManagerConfig( new SessionEventManagerConfig());
		return session;		
	}
	
	private PoolsAdapter buildPools() {

		PoolsAdapter pools = new PoolsAdapter( this);
		this.serverSession().setPoolsConfig(( PoolsConfig)pools.getModel());

		return pools;
	}
	/**
	 * Factory method for adding a write pool.
	 * WriteConnectionPool is stored in the pool collection and in its corresponding instance variable. 
	 */
	public ConnectionPoolAdapter addWriteConnectionPool() {
		ConnectionPoolAdapter writePool = this.pools.addWriteConnectionPool();
		this.fireItemAdded( POOLS_CONFIG_COLLECTION, writePool);

		firePropertyChanged(WRITE_CONNECTION_POOL_PROPERTY, null, writePool);
		return writePool;
	}
	public ConnectionPoolAdapter addReadConnectionPool() {
		ConnectionPoolAdapter readPool = this.pools.addReadConnectionPool();
		this.fireItemAdded( POOLS_CONFIG_COLLECTION, readPool);

		firePropertyChanged(READ_CONNECTION_POOL_PROPERTY, null, readPool);
		return readPool;
	}
	/**
	 * Factory method for adding a sequence pool.
	 */
	public ConnectionPoolAdapter addSequenceConnectionPool() {
		ConnectionPoolAdapter sequencePool = this.pools.addSequenceConnectionPool();
		this.fireItemAdded( POOLS_CONFIG_COLLECTION, sequencePool);
// TODO Pool node issue when signaling SEQUENCE_CONNECTION_POOL_CONFIG
// @see #ServerSessionNode.buildChildrenNodeAdapter()
		firePropertyChanged(SEQUENCE_CONNECTION_POOL_PROPERTY, null, sequencePool);
		return sequencePool;
	}
	/**
	 * Sets usesExternalConnectionPooling and the config model.
	 */
	public void setExternalConnectionPooling( boolean value) {
		
		super.setExternalConnectionPooling( value);
		
		if( value && this.pools.getReadConnectionPool() != null) {
		    this.pools.getReadConnectionPool().setExclusive( false);
		}
	}

	protected void initializePoolsFromModel() {

		if( this.getPoolConfigs() == null)
			this.pools = buildPools();
		else
			this.pools = ( PoolsAdapter)this.adapt( this.getPoolConfigs());
		
	}
	/**
	 * Initializes the pools of this new model, after that the platform is set.
	 */
	protected void initializePools() {

		this.pools = buildPools();
	}	
	/**
	 * Initializes this new model.
	 */
	protected void initialize( Object newConfig) {
		super.initialize( newConfig);

		this.connectionPolicy = this.buildConnectionPolicy();
	}
	/**
	 * Initializes this adapter from the config model.
	 */
	protected void initializeFromModel( Object scConfig) {
		super.initializeFromModel( scConfig);

		this.connectionPolicy = ( ConnectionPolicyAdapter)this.adapt( this.getConnectionPolicyConfig());
		if( this.connectionPolicy == null) {
		    this.connectionPolicy = this.buildConnectionPolicy();
		}
	}
	/**
	 * Returns this Config Model Object.
	 */
	private final ServerSessionConfig serverSession() {
		
		return ( ServerSessionConfig)this.getModel();
	}

	private PoolsConfig getPoolConfigs() {
		
		return this.serverSession().getPoolsConfig();
	}
	/**
	 * Returns an iterator on a collection of pools adapters.
	 */
	public Iterator pools() {
		
		return this.pools.pools();
	}
	
	public int poolsSize() {
		
		return this.pools.poolsSize();
	}
	/**
	 * Returns this pools adapter.
	 */
	private PoolsAdapter getPools() {
		
		return this.pools;
	}
	/**
	 * Returns an iterator on pool names.
	 */
	public Iterator<String> poolNames() {

		return getPools().getPoolNames().iterator();
	}

	public ReadConnectionPoolAdapter getReadConnectionPool() {
		
		return this.pools.getReadConnectionPool();
	}
	
	public ConnectionPoolAdapter getWriteConnectionPool() {
		
		return this.pools.getWriteConnectionPool();
	}
	
	public ConnectionPoolAdapter getSequenceConnectionPool() {
		
		return this.pools.getSequenceConnectionPool();
	}

	public boolean hasReadPool() {
		
		return this.pools.getReadConnectionPool() != null;
	}

	public boolean hasWritePool() {
		
		return this.pools.getWriteConnectionPool() != null;
	}
	
	public boolean hasAnyConnectionPool() {
		return hasReadPool() || hasWritePool() || hasSequencePool() || poolsSize() > 0;
	}
	
	@Override
	public boolean isServer() {
		return true;
	}

	public boolean hasSequencePool() {
		
		return this.pools.getSequenceConnectionPool() != null;
	}
	/**
	 * Returns the appropriate pool.
	 */
	public ConnectionPoolAdapter poolNamed( String name) {
		
		return this.pools.poolNamed( name);
	}
	/**
	 * Adds the given pool and fire notification.
	 */
	public ConnectionPoolAdapter addConnectionPoolNamed( String name) {
		
		ConnectionPoolAdapter namedPool = this.pools.addConnectionPoolNamed( name);
		
		this.fireItemAdded( POOLS_CONFIG_COLLECTION, namedPool);
		return namedPool;
	}
	/**
	 * Remove the pool with the given name.
	 */
	public void removeConnectionPoolNamed( String name) {
		
		ConnectionPoolAdapter namedPool = this.pools.removeConnectionPoolNamed( name);
		
		if( namedPool != null)
			this.fireItemRemoved( POOLS_CONFIG_COLLECTION, namedPool);
	}
	/**
	 * Remove the write pool.
	 */
	public void removeWriteConnectionPool() {
		
		ConnectionPoolAdapter writePool = this.pools.removeWriteConnectionPool();
		
		if( writePool != null) {
			this.fireItemRemoved( POOLS_CONFIG_COLLECTION, writePool);
		}
	}
	/**
	 * Remove the read pool.
	 */
	public void removeReadConnectionPool() {
		
		ConnectionPoolAdapter readPool = this.pools.removeReadConnectionPool();
		
		if( readPool != null) {
			this.fireItemRemoved( POOLS_CONFIG_COLLECTION, readPool);
		}
	}
	
	/**
	 * Remove all pools
	 */
	public void removeAllConnectionPools() {
		removeReadConnectionPool();
		removeWriteConnectionPool();
		removeSequenceConnectionPool();
		
		Iterator<String> poolNamesIter = poolNames();
		while (poolNamesIter.hasNext()) {
			removeConnectionPoolNamed(poolNamesIter.next());
		}
	}

	/**
	 * Remove the pool with the given name.
	 */
	public void removeSequenceConnectionPool() {
		
		ConnectionPoolAdapter sequencePool = this.pools.removeSequenceConnectionPool();
		
		if( sequencePool != null)
			this.fireItemRemoved( POOLS_CONFIG_COLLECTION, sequencePool);
	}
	
	private ConnectionPolicyAdapter buildConnectionPolicy() {

	    ConnectionPolicyAdapter policy = new ConnectionPolicyAdapter( this);
		this.serverSession().setConnectionPolicyConfig(( ConnectionPolicyConfig)policy.getModel());

		return policy;
	}

	private ConnectionPolicyConfig getConnectionPolicyConfig() {
		
		return this.serverSession().getConnectionPolicyConfig();
	}
	/**
	 * Returns this pools adapter.
	 */
	private ConnectionPolicyAdapter getConnectionPolicy() {
		
		return this.connectionPolicy;
	}
	/**
	 * Convenience method to get the connectionPolicy usesExclusiveConnection.
	 */
	public boolean usesExclusiveConnection() {
		
		return this.getConnectionPolicy().usesExclusiveConnection();
	}
	/**
	 * Convenience method to set the connectionPolicy usesExclusiveConnection.
	 */
	public void setUseExclusiveConnection( boolean value) {
		
		boolean old = this.getConnectionPolicy().usesExclusiveConnection();

		this.getConnectionPolicy().setUseExclusiveConnection( value);
		this.firePropertyChanged( USE_EXCLUSIVE_CONNECTION_PROPERTY, old, value);
	}
	/**
	 * Convenience method to get the connectionPolicy usesLazyConnection.
	 */
	public boolean usesLazyConnection() {
		
		return this.getConnectionPolicy().usesLazyConnection();
	}
	/**
	 * Convenience method to set the connectionPolicy usesLazyConnection.
	 */
	public void setLazyConnection( boolean value) {
		
		boolean old = this.getConnectionPolicy().usesLazyConnection();

		this.getConnectionPolicy().setLazyConnection( value);
		this.firePropertyChanged( LAZY_CONNECTION_PROPERTY, old, value);
	}
}
