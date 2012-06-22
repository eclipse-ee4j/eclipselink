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

import org.eclipse.persistence.internal.sessions.factories.XMLSessionConfigProject;
import org.eclipse.persistence.internal.sessions.factories.model.rcm.RemoteCommandManagerConfig;
import org.eclipse.persistence.internal.sessions.factories.model.rcm.command.CommandsConfig;
import org.eclipse.persistence.internal.sessions.factories.model.transport.TransportManagerConfig;

/**
 * Session Configuration model adapter class for the 
 * TopLink Foudation Library class RemoteCommandManagerConfig
 * 
 * @see RemoteCommandManagerConfig
 * 
 * @author Tran Le
 */
public class RemoteCommandManagerAdapter extends SCAdapter {
	// property change
	public final static String CHANNEL_PROPERTY = "channelConfig";
	private volatile CommandsAdapter commands;
	public final static String COMMANDS_PROPERTY = "commandsConfig";
	private volatile TransportManagerAdapter transportManager;
	public final static String TRANSPORT_MANAGER_PROPERTY = "transportManagerConfig";
	public final static String CACHE_SYNC_PROPERTY = "cacheSyncConfig";

	/**
	 * Creates a new Discovery for the specified model object.
	 */
	RemoteCommandManagerAdapter( SCAdapter parent, RemoteCommandManagerConfig scConfig) {
		
		super( parent, scConfig);
	}
	/**
	 * Creates a new Discovery.
	 */
	protected RemoteCommandManagerAdapter( SCAdapter parent) {
		
		super( parent);
	}
	/**
	 * Returns this Config Model Object.
	 */
	private final RemoteCommandManagerConfig rcm() {
		
		return ( RemoteCommandManagerConfig)this.getModel();
	}
	/**
	 * Factory method for building this model.
	 */
	protected Object buildModel() {
		return new RemoteCommandManagerConfig();
	}
	/**
	 * Adds the children of this adapter to the given list.
	 * @param children The list of children
	 */
	protected void addChildrenTo(List children) {
		super.addChildrenTo(children);

		if( this.transportManager != null)
			children.add( getTransportManager());

		if( this.commands != null)
			children.add( getCommands());
	}
	/**
	 * Factory method for building a child default Commands.
	 */
	private CommandsAdapter buildCommands() {
		
		CommandsConfig commands = new CommandsConfig(); 
		
		this.rcm().setCommandsConfig( commands);
		
		return new CommandsAdapter( this, commands);
	}
	/**
	 * Factory method for building a child default TransportManager.
	 */
	private TransportManagerAdapter buildDefaultTransportManager() {
		return buildJMSTransportManager();
	}
	/**
	 * Factory method for building a child default TransportManager.
	 */
	private JMSTopicTransportManagerAdapter buildJMSTransportManager() {

		return new JMSTopicTransportManagerAdapter( this);
	}
	/**
	 * Factory method for building a child default TransportManager.
	 */
	private RMITransportManagerAdapter buildRMITransportManager() {

		return new RMITransportManagerAdapter( this);
	}
	/**
	 * Factory method for building a child default TransportManager.
	 */
	private RMIIIOPTransportManagerAdapter buildRMIIIOPTransportManager() {

		return new RMIIIOPTransportManagerAdapter( this);
	}
	/**
	 * Factory method for building a child default TransportManager.
	 */
	private UserDefinedTransportManagerAdapter buildUserDefinedTransportManager() {

		return new UserDefinedTransportManagerAdapter( this);
	}
	/**
	 * Factory method for building a child default TransportManager.
	 */
	private SunCORBATransportManagerAdapter buildSunCORBATransportManager() {

		return new SunCORBATransportManagerAdapter( this);
	}
	/**
	 * Initializes this adapter from the config model.
	 */
	protected void initializeFromModel( Object scConfig) {
		
		super.initializeFromModel( scConfig);

		this.commands = ( CommandsAdapter)this.adapt( this.rcm().getCommandsConfig());
		this.transportManager = ( TransportManagerAdapter)this.adapt( this.rcm().getTransportManagerConfig());
	}
	/**
	 * Initializes this new model.
	 */
	protected void initialize( Object newConfig) {
		
		super.initialize( newConfig);

		this.commands = null;			// optional
		this.transportManager = null;	// optional
	}
	protected void initializeDefaults() {
		super.initializeDefaults();

		setCacheSync(true);
		if( rcm().getChannel() == null)
			this.setChannel( XMLSessionConfigProject.CHANNEL_DEFAULT);
	}
	/**
	 * Returns this transportManager adapter.
	 */
	public TransportManagerAdapter getTransportManager() {
		
		if( this.transportManager == null) {
			setTransportManager( buildDefaultTransportManager());
		}
		return this.transportManager;
	}
	/**
	 *  Sets this TransportManager.
	 */
	private void setTransportManager( TransportManagerAdapter manager) {
		
		Object old = this.transportManager;

		this.transportManager = manager;
		this.rcm().setTransportManagerConfig(( TransportManagerConfig)manager.getModel());
		this.firePropertyChanged( TRANSPORT_MANAGER_PROPERTY, old, manager);
	}
	/**
	 * Returns this config model property.
	 */
	public String getChannel() {
		
		return this.rcm().getChannel();
	}
	/**
	 * Sets this config model property.
	 */
	public void setChannel( String value) {
		
		Object old = this.rcm().getChannel();

		this.rcm().setChannel( value);
		this.firePropertyChanged( CHANNEL_PROPERTY, old, value);
	}
	/**
	 * Convenience method to get the commands cacheSync.
	 */
	public boolean usesCacheSync() {
		
		return this.getCommands().usesCacheSync();
	}
	/**
	 * Convenience method to set the commands cacheSync.
	 */
	private void setCacheSync( boolean cacheSync) {
		
		boolean old = this.getCommands().usesCacheSync();

		this.commands.setCacheSync( cacheSync);
		this.firePropertyChanged( CACHE_SYNC_PROPERTY, old, cacheSync);
	}
	/**
	 * Returns this Commands adapter.
	 */
	CommandsAdapter getCommands() {
		
		if( this.commands == null) {
			setCommands( buildCommands());
		}
		return this.commands;
	}
	/**
	 *  Sets this CommandsAdapter.
	 */
	private void setCommands( CommandsAdapter commands) {
		
		Object old = this.commands;

		this.commands = commands;
		this.rcm().setCommandsConfig(( CommandsConfig)commands.getModel());
		this.firePropertyChanged( COMMANDS_PROPERTY, old, commands);
	}
	/**
	 * Sets this transportManager as JMSTopicTransportManagerAdapter.
	 */
	public JMSTopicTransportManagerAdapter setTransportAsJMSTopic() {
		if (!(transportManager instanceof JMSTopicTransportManagerAdapter))
			this.setTransportManager( buildJMSTransportManager());

		setChannel( XMLSessionConfigProject.CHANNEL_DEFAULT);
		return ( JMSTopicTransportManagerAdapter)this.transportManager;	
	}
	/**
	 * Returns this remoteCommandManager adapter.
	 */
	public RMITransportManagerAdapter setTransportAsRMI() {
		
		this.setTransportManager( buildRMITransportManager());

		return ( RMITransportManagerAdapter)this.transportManager;	
	}
	/**
	 * Returns this remoteCommandManager adapter.
	 */
	public RMIIIOPTransportManagerAdapter setTransportAsRMIIIOP() {
		
		this.setTransportManager( buildRMIIIOPTransportManager());

		return ( RMIIIOPTransportManagerAdapter)this.transportManager;	
	}
	/**
	 * Returns this remoteCommandManager adapter.
	 */
	public UserDefinedTransportManagerAdapter setTransportAsUserDefined() {
		
		this.setTransportManager( buildUserDefinedTransportManager());

		return ( UserDefinedTransportManagerAdapter)this.transportManager;	
	}
	/**
	 * Returns this remoteCommandManager adapter.
	 */
	public SunCORBATransportManagerAdapter setTransportAsSunCORBA() {
		
		this.setTransportManager( buildSunCORBATransportManager());

		return ( SunCORBATransportManagerAdapter)this.transportManager;	
	}

}
