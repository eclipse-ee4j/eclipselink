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

import org.eclipse.persistence.internal.sessions.factories.XMLSessionConfigProject;
import org.eclipse.persistence.internal.sessions.factories.model.clustering.ClusteringServiceConfig;

/**
 * Session Configuration model adapter class for the 
 * TopLink Foudation Library class ClusteringServiceConfig
 * 
 * @see ClusteringServiceConfig
 * 
 * @author Tran Le
 */
public abstract class ClusteringServiceAdapter extends SCAdapter {
	// property change
	public final static String MULTICAST_PORT_PROPERTY = "multicastPort";
	public final static String PACKET_TIME_TO_LIVE_PROPERTY = "packetTimeToLive";
	public final static String MULTICAST_GROUP_ADDRESS_PROPERTY = "multicastGroupAddress";
	public final static String NAMING_SERVICE_URL_PROPERTY = "namingServiceURL";

	/**
	 * Creates a new ClusteringServiceAdapter for the specified model object.
	 */
	ClusteringServiceAdapter( SCAdapter parent, ClusteringServiceConfig scConfig) {
		
		super( parent, scConfig);
	}
	/**
	 * Creates a new ClusteringServiceAdapter.
	 */
	protected ClusteringServiceAdapter( SCAdapter parent) {
		
		super( parent);
	}
	
	protected void initializeDefaults() {
		super.initializeDefaults();

		if( clusteringConfig().getMulticastGroupAddress() == null)
			setMulticastGroupAddress( XMLSessionConfigProject.MULTICAST_GROUP_ADDRESS_RMI_CLUSTERING);

		if( clusteringConfig().getMulticastPort() == null)
			setMulticastPort( XMLSessionConfigProject.MULTICAST_PORT_RMI_CLUSTERING_DEFAULT);

		if( clusteringConfig().getPacketTimeToLive() == null)
			setPacketTimeToLive( XMLSessionConfigProject.PACKET_TIME_TO_LIVE_DEFAULT);
	}
	/**
	 * Returns config.
	 */
	public String getMulticastGroupAddress() {
		
		return this.clusteringConfig().getMulticastGroupAddress();
	}
	/**
	 * Sets config model.
	 */
	public void setMulticastGroupAddress( String address) {
		
		Object old = this.clusteringConfig().getMulticastGroupAddress();

		this.clusteringConfig().setMulticastGroupAddress( address);
		this.firePropertyChanged( NAMING_SERVICE_URL_PROPERTY, old, address);
	}
	/**
	 * Returns config.
	 */
	public int getMulticastPort() {
		
		Integer port = this.clusteringConfig().getMulticastPort();
		return ( port == null) ? 0 : port.intValue();
	}
	/**
	 * Sets config model.
	 */
	public void setMulticastPort( int port) {

		int old = getMulticastPort();
		this.clusteringConfig().setMulticastPort( new Integer( port));
		firePropertyChanged( MULTICAST_PORT_PROPERTY, old, port);
	}
	/**
	 * Returns config.
	 */
	public String getNamingServiceURL() {
		
		return this.clusteringConfig().getNamingServiceURL();
	}
	/**
	 * Sets config model.
	 */
	public void setNamingServiceURL( String url) {
		
		Object old = this.clusteringConfig().getNamingServiceURL();

		this.clusteringConfig().setNamingServiceURL( url);
		this.firePropertyChanged( NAMING_SERVICE_URL_PROPERTY, old, url);
	}
	/**
	 * Returns config.
	 */
	public int getPacketTimeToLive() {
		
		Integer packet = this.clusteringConfig().getPacketTimeToLive();
		return ( packet == null) ? 0 : packet.intValue();
	}
	/**
	 * Sets config model.
	 */
	public void setPacketTimeToLive( int packet) {

		int old = getPacketTimeToLive();
		this.clusteringConfig().setPacketTimeToLive( new Integer( packet));
		firePropertyChanged( PACKET_TIME_TO_LIVE_PROPERTY, old, packet);
	}
	/**
	 * Returns this Config Model Object.
	 */
	private final ClusteringServiceConfig clusteringConfig() {
		
		return ( ClusteringServiceConfig)this.getModel();
	}
}
