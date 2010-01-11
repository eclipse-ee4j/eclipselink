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

import java.util.List;

import org.eclipse.persistence.internal.sessions.factories.XMLSessionConfigProject;
import org.eclipse.persistence.internal.sessions.factories.model.transport.RMITransportManagerConfig;
import org.eclipse.persistence.internal.sessions.factories.model.transport.discovery.DiscoveryConfig;
import org.eclipse.persistence.internal.sessions.factories.model.transport.naming.JNDINamingServiceConfig;
import org.eclipse.persistence.internal.sessions.factories.model.transport.naming.RMIRegistryNamingServiceConfig;

/**
 * Session Configuration model adapter class for the 
 * TopLink Foudation Library class RMITransportManagerConfig
 * 
 * @see RMITransportManagerConfig
 * 
 * @author Tran Le
 */
public class RMITransportManagerAdapter extends TransportManagerAdapter {
	// property change
	private volatile JNDINamingServiceAdapter namingService;
	public final static String JNDI_NAMING_SERVICE_PROPERTY = "jndiNamingService";
	private volatile DiscoveryAdapter discovery;
	public final static String DISCOVERY_PROPERTY = "discovery";
	private volatile RMIRegistryNamingServiceAdapter rmiRegistryNamingService;
	public final static String RMI_REGISTRY_NAMING_SERVICE_PROPERTY = "rmiRegistryNamingService";
	// JNDI property change
	public final static String JNDI_ENCRYPTION_CLASS_PROPERTY = "encryptionClass";
	public final static String JNDI_PROPERTIES_COLLECTION = "properties";
	// DISCOVER property change
	public final static String DISCOVER_MULTICAST_GROUP_ADDRESS_PROPERTY = "multicastGroupAddress";
	public final static String DISCOVER_MULTICAST_PORT_PROPERTY = "multicastPort";
	public final static String DISCOVER_ANNOUNCEMENT_DELAY_PROPERTY = "announcementDelay";
	public final static String DISCOVER_PACKET_TIME_TO_LIVE_PROPERTY = "packetTimeToLive";
	// Naming Service property change
	public volatile String namingServiceType;
	public final static String NAMING_SERVICE_TYPE_PROPERTY = "namingServiceType";
	// Send Mode property change
	public final static String SEND_MODE_PROPERTY = "sendMode";

	/**
	 * Creates a new RMITransportManagerAdapter for the specified model object.
	 */
	RMITransportManagerAdapter( SCAdapter parent, RMITransportManagerConfig scConfig) {
		
		super( parent, scConfig);
	}
	/**
	 * Creates a new RMITransportManager.
	 */
	protected RMITransportManagerAdapter( SCAdapter parent) {	
		super( parent);
	}
	/**
	 * Factory method for building this model.
	 */
	protected Object buildModel() {
		return new RMITransportManagerConfig();
	}
	/**
	 * Factory method for building a child default DiscoveryAdapter.
	 */
	private DiscoveryAdapter buildDiscovery() {

		return new DiscoveryAdapter( this);
	}
	/**
	 * Factory method for building a child default RMIRegistryNamingServiceAdapter.
	 */
	private RMIRegistryNamingServiceAdapter buildRegistryNamingService() {

		return new RMIRegistryNamingServiceAdapter( this);
	}
	/**
	 * Returns this Config Model Object.
	 */
	private final RMITransportManagerConfig manager() {
		
		return ( RMITransportManagerConfig)this.getModel();
	}
	/**
	 * Initializes this adapter from the config model.
	 */
	protected void initializeFromModel( Object scConfig) {
		
		super.initializeFromModel( scConfig);

		this.discovery = ( DiscoveryAdapter)this.adapt(
				this.manager().getDiscoveryConfig());
		this.namingService = ( JNDINamingServiceAdapter)this.adapt(
				this.manager().getJNDINamingServiceConfig());
		this.rmiRegistryNamingService = ( RMIRegistryNamingServiceAdapter)this.adapt(
				this.manager().getRMIRegistryNamingServiceConfig());

		this.namingServiceType = (namingService != null) ? JNDI_NAMING_SERVICE_PROPERTY : RMI_REGISTRY_NAMING_SERVICE_PROPERTY;
	}
	/**
	 * Initializes this new model.
	 */
	protected void initialize( Object newConfig) {
		
		super.initialize( newConfig);

		this.setDiscovery( buildDiscovery());
		this.setJNDINamingService( buildJNDINamingService());
		this.namingServiceType = JNDI_NAMING_SERVICE_PROPERTY;
	}
	/**
	 * Initializes default value.
	 */
	protected void initializeDefaults() {
		super.initializeDefaults();

		this.setMulticastGroupAddress( XMLSessionConfigProject.MULTICAST_GROUP_ADDRESS_DEFAULT);
		this.setMulticastPort( XMLSessionConfigProject.MULTICAST_PORT_DEFAULT);
		this.setAnnouncementDelay( XMLSessionConfigProject.ANNOUNCEMENT_DELAY_DEFAULT);
		this.setSynchronous( XMLSessionConfigProject.SEND_MODE_DEFAULT.equals("Asynchronous"));
		this.setPacketTimeToLive( XMLSessionConfigProject.PACKET_TIME_TO_LIVE_DEFAULT);
	}
	
	/**
	 * Returns this Discovery adapter.
	 */
	DiscoveryAdapter getDiscovery() {
		
		return this.discovery;
	}
	/**
	 * Returns this JNDINamingService adapter.
	 */
	public JNDINamingServiceAdapter getJNDINamingService() {
		
		return this.namingService;
	}
	/**
	 * Returns this RMIRegistryNamingService adapter.
	 */
	public RMIRegistryNamingServiceAdapter getRMIRegistryNamingService() {
		
		return this.rmiRegistryNamingService;
	}
	/**
	 *  Sets this DiscoveryAdapter.
	 */
	private void setDiscovery( DiscoveryAdapter discovery) {
		
		Object old = this.namingService;

		this.discovery = discovery;
		this.manager().setDiscoveryConfig(( DiscoveryConfig)discovery.getModel());
		this.firePropertyChanged( DISCOVERY_PROPERTY, old, discovery);
	}
	/**
	 *  Sets this JNDINamingService.
	 */
	private void setJNDINamingService( JNDINamingServiceAdapter service) {
		
		Object old = this.namingService;
		this.namingService = service;
		
		JNDINamingServiceConfig config = ( service != null) ? ( JNDINamingServiceConfig)service.getModel() : null;

		this.manager().setJNDINamingServiceConfig( config);

		this.firePropertyChanged( JNDI_NAMING_SERVICE_PROPERTY, old, service);
	}
	/**
	 *  Sets this RMIRegistryNamingServiceAdapter.
	 */
	private void setRegistryNamingService( RMIRegistryNamingServiceAdapter service) {
		
		Object old = this.namingService;

		this.rmiRegistryNamingService = service;

		if( service != null) {
			this.manager().setRMIRegistryNamingServiceConfig(( RMIRegistryNamingServiceConfig)service.getModel());
		}
		else {
			this.manager().setRMIRegistryNamingServiceConfig( null);
		}

		this.firePropertyChanged( RMI_REGISTRY_NAMING_SERVICE_PROPERTY, old, service);
	}
	// ********** Convinience methods to Discover behaviors **********
	/**
	 * Returns this config model property.
	 */
	public String getMulticastGroupAddress() {
		
		return this.discovery.getMulticastGroupAddress();
	}
	/**
	 * Sets this config model property.
	 */
	public void setMulticastGroupAddress( String address) {
		
		Object old = this.discovery.getMulticastGroupAddress();

		this.discovery.setMulticastGroupAddress( address);
		this.firePropertyChanged( DISCOVER_MULTICAST_GROUP_ADDRESS_PROPERTY, old, address);
	}
	/**
	 * Returns this config model property.
	 */
	public int getMulticastPort() {
		
		return this.discovery.getMulticastPort();
	}
	/**
	 * Sets this config model property.
	 */
	public void setMulticastPort( int value) {
		
		int old = this.discovery.getMulticastPort();

		this.discovery.setMulticastPort( value);
		this.firePropertyChanged( DISCOVER_MULTICAST_PORT_PROPERTY, old, value);
	}
	/**
	 * Returns this config model property.
	 */
	public int getPacketTimeToLive() {
		
		return this.discovery.getPacketTimeToLive();
	}
	/**
	 * Sets this config model property.
	 */
	public void setPacketTimeToLive( int value) {
		
		int old = this.discovery.getPacketTimeToLive();

		this.discovery.setPacketTimeToLive( value);
		this.firePropertyChanged( DISCOVER_PACKET_TIME_TO_LIVE_PROPERTY, old, value);
	}
	/**
	 * Returns this config model property.
	 */
	public int getAnnouncementDelay() {
		
		return this.discovery.getAnnouncementDelay();
	}
	/**
	 * Sets this config model property.
	 */
	public void setAnnouncementDelay( int value) {
		
		int old = this.discovery.getAnnouncementDelay();

		this.discovery.setAnnouncementDelay( value);
		this.firePropertyChanged( DISCOVER_ANNOUNCEMENT_DELAY_PROPERTY, old, value);
	}

	/**
	 * Sets config model.
	 */
	public String getNamingServiceType() {

		return namingServiceType;
	}
	/**
	 * Sets config model.
	 */
	public JNDINamingServiceAdapter setNamingServiceToJNDINamingService() {

		String old = getNamingServiceType();
		this.setJNDINamingService( buildJNDINamingService());
		this.setRegistryNamingService( null);
		this.namingServiceType = JNDI_NAMING_SERVICE_PROPERTY;
		this.firePropertyChanged( NAMING_SERVICE_TYPE_PROPERTY, old, namingServiceType);
		return getJNDINamingService();
	}
	/**
	 * Sets config model.
	 */
	public RMIRegistryNamingServiceAdapter setNamingServiceToRMIRegistryNamingService() {

		String old = getNamingServiceType();
		this.setJNDINamingService( null);
		this.setRegistryNamingService( buildRegistryNamingService());
		this.namingServiceType = RMI_REGISTRY_NAMING_SERVICE_PROPERTY;
		this.firePropertyChanged( NAMING_SERVICE_TYPE_PROPERTY, old, namingServiceType);
		return getRMIRegistryNamingService();
	}
	/**
	 * Returns this EncryptionClass.
	 */
	public String getEncryptionClass() {
		
		return this.namingService.getEncryptionClass();
	}
	/**
	 * Sets config model.
	 */
	public void setEncryptionClass( String name) {
		
		Object old = this.namingService.getEncryptionClass();

		this.namingService.setEncryptionClass( name);
		this.firePropertyChanged( JNDI_ENCRYPTION_CLASS_PROPERTY, old, name);
	}
	/**
	 * Adds the children of this adapter to the given list.
	 * @param children The list of children
	 */
	protected void addChildrenTo(List children) {
		super.addChildrenTo(children);

		if( getJNDINamingService() != null)
			children.add( getJNDINamingService());

		if( getRMIRegistryNamingService() != null)
			children.add( getRMIRegistryNamingService());
	}
	public boolean isSynchronous() {

		String value =  this.manager().getSendMode();
		
		return (( value != null) && value.equalsIgnoreCase( "Synchronous"));
	}

	public void setSynchronous(boolean synchronous) {

		boolean old = this.removeConnectionOnError();

		if( synchronous)
			this.manager().setSendMode( "Synchronous");
		else
			this.manager().setSendMode( "Asynchronous");

		this.firePropertyChanged( SEND_MODE_PROPERTY, old, synchronous);
	}
}
