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

import java.util.List;

import org.eclipse.persistence.internal.sessions.factories.XMLSessionConfigProject;
import org.eclipse.persistence.internal.sessions.factories.model.clustering.ClusteringServiceConfig;
import org.eclipse.persistence.internal.sessions.factories.model.csm.CacheSynchronizationManagerConfig;

/**
 * Session Configuration model adapter class for the 
 * TopLink Foudation Library class CacheSynchronizationManagerConfig
 * 
 * @see CacheSynchronizationManagerConfig
 * 
 * @author Tran Le
 */
public final class CacheSynchronizationManagerAdapter extends SCAdapter {
	// property change
	private volatile ClusteringServiceAdapter clusteringService;
	public static final String CLUSTERING_SERVICE_CONFIG_PROPERTY = "clusteringServiceConfig";
	public static final String CACHE_SYNC_PROPERTY = "cacheSyncConfig";
	public static final String ON_CONNECTION_ERROR_PROPERTY = "onConnectionErrorConfig";

	/**
	 * Creates a new CacheSynchronizationManagerAdapter for the specified model object.
	 */
	CacheSynchronizationManagerAdapter( SCAdapter parent, CacheSynchronizationManagerConfig scConfig) {
		
		super( parent, scConfig);
	}
	/**
	 * Creates a new CacheSynchronizationManagerAdapter.
	 */
	protected CacheSynchronizationManagerAdapter( SCAdapter parent) {
		
		super( parent);
	}
	/**
	 * Returns this Config Model Object.
	 */
	private final CacheSynchronizationManagerConfig manager() {
		
		return ( CacheSynchronizationManagerConfig)this.getModel();
	}
	/**
	 * Factory method for building this model.
	 */
	protected Object buildModel() {
		return new CacheSynchronizationManagerConfig();
	}
	/**
	 * Factory method for building a child default ClusteringService.
	 */
	private ClusteringServiceAdapter buildDefaultClusteringService() {
		return buildJMSClustering();
	}
	/**
	 * Factory method for building a child default ClusteringService.
	 */
	private JMSClusteringAdapter buildJMSClustering() {

		return new JMSClusteringAdapter( this);
	}
	/**
	 * Factory method for building a child default ClusteringService.
	 */
	private RMIClusteringAdapter buildRMIClustering() {

		return new RMIClusteringAdapter( this);
	}
	/**
	 * Factory method for building a child default ClusteringService.
	 */
	private RMIJNDIClusteringAdapter buildRMIJNDIClustering() {

		return new RMIJNDIClusteringAdapter( this);
	}
	/**
	 * Factory method for building a child default ClusteringService.
	 */
	private RMIIIOPJNDIClusteringAdapter buildRMIIIOPJNDIClustering() {

		return new RMIIIOPJNDIClusteringAdapter( this);
	}
	/**
	 * Factory method for building a child default ClusteringService.
	 */
	private SunCORBAJNDIClusteringAdapter buildSunCORBAJNDIClustering() {

		return new SunCORBAJNDIClusteringAdapter( this);
	}
	/**
	 * Returns this clusteringService adapter.
	 */
	public ClusteringServiceAdapter getClusteringService() {
		
		return this.clusteringService;
	}
	/**
	 * Adds the children of this adapter to the given list.
	 * @param children The list of children
	 */
	protected void addChildrenTo(List children) {
		super.addChildrenTo(children);

		if( getClusteringService() != null)
			children.add( getClusteringService());
	}
	/**
	 * Initializes this adapter from the config model.
	 */
	protected void initializeFromModel( Object scConfig) {
		
		super.initializeFromModel( scConfig);

		this.clusteringService = ( ClusteringServiceAdapter)this.adapt(
				this.manager().getClusteringServiceConfig());
	}
	/**
	 * Initializes this new model.
	 */
	protected void initialize( Object newConfig) {
		
		super.initialize( newConfig);

		this.setClusteringService( buildDefaultClusteringService());
	}
	protected void initializeDefaults() {
		super.initializeDefaults();

		setAsynchronous( XMLSessionConfigProject.IS_ASYNCHRONOUS_DEFAULT);
	}
	/**
	 * gets isAsynchronous property.
	 */
	public boolean isAsynchronous() {
		
		return this.manager().getIsAsynchronous();
	}
	/**
	 * sets isAsynchronous property.
	 */
	public void setAsynchronous( boolean value) {
		
		boolean old = this.manager().getIsAsynchronous();

		this.manager().setIsAsynchronous( value);
		this.firePropertyChanged( CACHE_SYNC_PROPERTY, old, value);
	}
	/**
	 * gets removeConnectionOnError property.
	 */
	public boolean removeConnectionOnError() {
		
		return this.manager().getRemoveConnectionOnError();
	}
	/**
	 * sets removeConnectionOnError property.
	 */
	public void setRemoveConnectionOnError( boolean value) {
		
		boolean old = this.manager().getRemoveConnectionOnError();

		this.manager().setRemoveConnectionOnError( value);
		this.firePropertyChanged( ON_CONNECTION_ERROR_PROPERTY, old, value);
	}
	/**
	 *  Sets this clusteringService.
	 */
	private void setClusteringService( ClusteringServiceAdapter clusteringService) {
		
		Object old = this.clusteringService;

		this.clusteringService = clusteringService;
		this.manager().setClusteringServiceConfig(( ClusteringServiceConfig)clusteringService.getModel());
		this.firePropertyChanged( CLUSTERING_SERVICE_CONFIG_PROPERTY, old, clusteringService);
	}
	/**
	 * Sets this clusteringService as JMSClusteringAdapter.
	 */
	public JMSClusteringAdapter setClusteringAsJMS() {
		
		this.setClusteringService( buildJMSClustering());

		return ( JMSClusteringAdapter)this.clusteringService;	
	}
	/**
	 * Sets this clusteringService as RMIClusteringAdapter.
	 */
	public RMIClusteringAdapter setClusteringAsRMI() {
		
		this.setClusteringService( buildRMIClustering());

		return ( RMIClusteringAdapter)this.clusteringService;	
	}

	/**
	 * Sets this clusteringService as RMIJNDIClusteringAdapter.
	 */
	public RMIJNDIClusteringAdapter setClusteringAsRMIJNDI() {
		
		this.setClusteringService( buildRMIJNDIClustering());

		return ( RMIJNDIClusteringAdapter)this.clusteringService;	
	}

	/**
	 * Sets this clusteringService as RMIClusteringAdapter.
	 */
	public RMIIIOPJNDIClusteringAdapter setClusteringAsRMIIIOPJNDI() {
		
		this.setClusteringService( buildRMIIIOPJNDIClustering());

		return ( RMIIIOPJNDIClusteringAdapter)this.clusteringService;	
	}

	/**
	 * Sets this clusteringService as SunCORBAJNDIClusteringAdapter.
	 */
	public SunCORBAJNDIClusteringAdapter setClusteringAsSunCORBAJNDI() {
		
		this.setClusteringService( buildSunCORBAJNDIClustering());

		return ( SunCORBAJNDIClusteringAdapter)this.clusteringService;	
	}
}
