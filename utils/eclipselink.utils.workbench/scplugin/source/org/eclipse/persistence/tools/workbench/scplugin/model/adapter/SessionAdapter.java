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
import java.util.ListIterator;

import org.eclipse.persistence.tools.workbench.scplugin.SCProblemsConstants;
import org.eclipse.persistence.tools.workbench.utility.ClassTools;
import org.eclipse.persistence.tools.workbench.utility.TriStateBoolean;

import org.eclipse.persistence.internal.sessions.factories.model.event.SessionEventManagerConfig;
import org.eclipse.persistence.internal.sessions.factories.model.log.LogConfig;
import org.eclipse.persistence.internal.sessions.factories.model.platform.ServerPlatformConfig;
import org.eclipse.persistence.internal.sessions.factories.model.rcm.RemoteCommandManagerConfig;
import org.eclipse.persistence.internal.sessions.factories.model.session.SessionConfig;

/**
 * Session Configuration model adapter class for the 
 * TopLink Foundation Library class SessionConfig
 * 
 * @see SessionConfig
 * 
 * @author Tran Le
 */
public abstract class SessionAdapter extends SCAdapter implements Nominative {
	// property change
	private volatile ServerPlatformAdapter serverPlatform;
	public final static String SERVER_PLATFORM_CONFIG_PROPERTY = "serverPlatform";

	public final static String PROFILER_PROPERTY = "profiler";
	public final static String EXCEPTION_HANDLER_CLASS_PROPERTY = "exceptionHandlerClass";
	public final static String SESSION_CUSTOMIZER_CLASS_PROPERTY = "sessionCustomizerClass";
	private volatile LogAdapter log;
	public final static String LOG_CONFIG_PROPERTY = "log";
	public final static String LOG_PROFILER_TOPLINK = "toplink";
	public final static String LOG_PROFILER_DMS = "dms";
	private volatile RemoteCommandManagerAdapter remoteCommandManager;
	public final static String REMOTE_COMMAND_MANAGER_CONFIG_PROPERTY = "remoteCommandManager";
	private volatile SessionEventManagerAdapter sessionEventManager;
	public final static String SESSION_EVENT_LISTENERS_CONFIGS_LIST = "sessionEventListeners";
	
	private volatile boolean managedByBroker;
	public final static String MANAGED_BY_BROKER = "managedByBroker";
	
	/**
	 * Determines whether Cache Synchronization can be used as the Clustering
	 * type. To be allowed, it has to be present when reading in the sessions.xml.
	 */
	private boolean cacheSynchronizationManagerAllowed;

	/**
	 * <code>TriStateBoolean.TRUE</code> means the clustering is Remote Command
	 * Manager, <code>TriStateBoolean.FALSE</code> means it's Cache Synchronization
	 * Manager and <code>TriStateBoolean.UNDEFINED</code> means neither of them
	 * is used.
	 */
	private TriStateBoolean clusteringService;

	/**
	 * Creates a new SessionAdapter for the specified model object.
	 */
	SessionAdapter( SCAdapter parent, SessionConfig scConfig) {
		
		super( parent, scConfig);
	}
	/**
	 * Creates a new Session.
	 */
	protected SessionAdapter( SCAdapter parent, String name) {
		
		super( parent);
		
		this.setName( name);
	}
	/**
	 * Factory method for building a child default SessionEventManagerAdapter.
	 */
	private SessionEventManagerAdapter buildSessionEventManager() {

	    SessionEventManagerAdapter sessionEventManager = new SessionEventManagerAdapter( this);
		
		this.session().setSessionEventManagerConfig(( SessionEventManagerConfig)sessionEventManager.getModel());

		return sessionEventManager;
	}
	/**
	 * Adds the children of this adapter to the given list.
	 * @param children The list of children
	 */
	protected void addChildrenTo(List children) {
		super.addChildrenTo(children);

		if( getLog() != null)
			children.add( getLog());

		if( getRemoteCommandManager() != null)
			children.add( getRemoteCommandManager());

		if( getServerPlatform() != null)
			children.add( getServerPlatform());
	}
	/**
	 * Add any problems from this adapter to the given set.
	 */
	protected void addProblemsTo( List branchProblems) {
	
		super.addProblemsTo( branchProblems);
	
		verifyDeprecatedFeatures( branchProblems);
	}
	/**
	 * Initializes this new model.
	 */
	protected void initialize( Object newConfig) {
		super.initialize( newConfig);

		this.sessionEventManager = buildSessionEventManager();
		//TOREVIEW - server-platform
		// this.session().setExternalTransactionControllerClass( "");
		
		this.clusteringService = TriStateBoolean.UNDEFINED;
		this.setDefaultLogging();;
		this.managedByBroker = false;
	}
	/**
	 * Initializes this adapter from the config model.
	 */
	protected void initializeFromModel( Object scConfig) {
		super.initializeFromModel( scConfig);
		
		LogAdapter adapter = ( LogAdapter)this.adapt( this.session().getLogConfig());
		if (adapter == null) {
			setNoLogging();
		} else {
			this.log = adapter;
		}

		this.remoteCommandManager = 
			( RemoteCommandManagerAdapter)this.adapt( this.session().getRemoteCommandManagerConfig());
		this.sessionEventManager = 
			( SessionEventManagerAdapter)this.adapt( this.session().getSessionEventManagerConfig());
		//TOREVIEW - Bug SessionEventManagerConfig is not initialized
		if( this.sessionEventManager == null)
			this.sessionEventManager = buildSessionEventManager();
		if( this.session().getServerPlatformConfig() == null)
		    initializeServerPlatform( new ServerPlatform( ClassTools.shortNameForClassNamed( NullServerPlatformAdapter.instance().getServerClassName())));
		else
			this.serverPlatform =
				( ServerPlatformAdapter)this.adapt( this.session().getServerPlatformConfig());

		if( this.remoteCommandManager != null) {
			this.clusteringService = TriStateBoolean.TRUE;
		}
		else {
			this.clusteringService = TriStateBoolean.UNDEFINED;
		}
		this.managedByBroker = false;		    
	}
	
	protected void postInitializationFromModel() {
	    super.postInitializationFromModel();
		if( configVersionIsPre10g()) {
			if( this.serverPlatform.isCustom()) {
//TOREVIEW DO NOT TRY TO CONVERT
//			    String jtaClass = (( CustomServerPlatformAdapter)serverPlatform).getExternalTransactionControllerClass();
//			    if( ServerPlatformManager.instance().serverPlatformIsSupported( jtaClass)) {
//				    initializeServerPlatform( new ServerPlatform( ClassTools.shortNameForClassNamed( jtaClass)));
			}
		}
		return;
	}

	private void verifyDeprecatedFeatures( List branchProblems) {

		if( this.configVersionIsPre10g()) {
		    if( this.serverPlatform.isCustom()) {
				branchProblems.add( buildProblem( SCProblemsConstants.EXTERNAL_TRANSACTION_CONTROLLER_904_DEPRECATED, getParent().displayString()));
			}
		}
	}
	/**
	 * Initializes Server platform of this new model..
	 */
	protected void initializeServerPlatform( ServerPlatform sp) {
	    
		this.setServerPlatform( sp.buildAdapter( this));
	}
	/**
	 * Determines whether {@link CacheSynchronizationManagerAdapter} is an
	 * allowed Clustering type or not.
	 *
	 * @return <code>true<code> if the sessions.xml was read with an existing
	 * Cache Synchronization Manager, <code>false<code> in any other case
	 */
	public boolean isCacheSynchronizationManagerAllowed() {
		return this.cacheSynchronizationManagerAllowed;
	}
	/**
	 * Returns this config model property.
	 */
	public String getName() {
		
		return this.session().getName();
	}
	/**
	 * Sets this config model property.
	 */
	public void setName( String name) {
		
		Object old = this.session().getName();

		this.session().setName( name);
		this.firePropertyChanged( NAME_PROPERTY, old, name);
	}
	
	public String displayString() {
		
		return this.getName();
	}
	/**
	 * Returns true when the enableJTA attribute of the ServerPlatform is true.
	 * If this session is managed, sets with the broker's attribute. 
	 */
	public boolean hasJTA() {
	    
	    SessionAdapter session = ( this.isManaged()) ? (( DatabaseSessionAdapter)this).getBroker() : this;
	    
		return ( session.getServerPlatform() == null) ? false : session.getServerPlatform().getEnableJTA();
	}
	/**
	 * Returns true when the Clustering service is a CacheSynchronizationManager.
	 */
	public boolean hasCacheSynchronizationManager() {
		
		return (this.clusteringService == TriStateBoolean.FALSE);
	}
	/**
	 * Returns true when the Clustering service is not set.
	 */
	public boolean hasNoClusteringService() {
		
		return (this.clusteringService == TriStateBoolean.UNDEFINED);
	}
	/**
	 * Returns true when the Clustering service is a RemoteCommandManager.
	 */
	public boolean hasRemoteCommandManager() {
		
		return (this.clusteringService == TriStateBoolean.TRUE);
	}
		
	public boolean platformIsRdbms() {
		return false;
	}
	
	public boolean platformIsEis() {
		return false;
	}
	
	public boolean platformIsXml() {
		return false;
	}
	
	public boolean isManaged() {
		return this.managedByBroker;
	}
	
	public boolean isBroker() {
		return false;
	}
	/**
	 * Convenience method to get the datasource platform.
	 */
	public String getDataSourceName() {
		return "";
	}
	/**
	 * Returns this remoteCommandManager adapter.
	 */
	public RemoteCommandManagerAdapter getRemoteCommandManager() {
		return this.remoteCommandManager;
	}
	/**
	 * Returns this remoteCommandManager adapter.
	 */
	public RemoteCommandManagerAdapter setClusteringToRemoteCommandManager() {
		
		if( hasRemoteCommandManager()) return this.remoteCommandManager;	

		this.clusteringService = TriStateBoolean.TRUE;
		this.setRemoteCommandManager( buildRemoteCommandManager());
		
		return this.remoteCommandManager;	
	}
	public void setClusteringToNothing() {
		this.clusteringService = TriStateBoolean.UNDEFINED;
		this.setRemoteCommandManager( null);
	}
	/**
	 * Sets this log adapter and the config model.
	 */
	private void setRemoteCommandManager( RemoteCommandManagerAdapter manager) {
		
		Object old = this.remoteCommandManager;
		
		this.remoteCommandManager = manager;

		if (manager == null)
			this.session().setRemoteCommandManagerConfig(null);
		else
			this.session().setRemoteCommandManagerConfig(( RemoteCommandManagerConfig)manager.getModel());

		this.firePropertyChanged( REMOTE_COMMAND_MANAGER_CONFIG_PROPERTY, old, manager);
	}
	
	private RemoteCommandManagerAdapter buildRemoteCommandManager() {

		return new RemoteCommandManagerAdapter( this);
	}
		
	/**
	 * Convinience method to access this transportManager adapter.
	 */
	public TransportManagerAdapter getTransportManager() {
		
		return this.remoteCommandManager.getTransportManager();
	}
	/**
	 * Sets this session server platform.
	 */
	public ServerPlatformAdapter setServerPlatform( ServerPlatform sp) {
		
	    this.setServerPlatform( sp.buildAdapter( this));
		
	    this.externalTransactionControllerClassChanged();
		return this.serverPlatform;
	}
	/**
	 * Returns this log adapter.
	 */
	public LogAdapter getLog() {
		
		return this.log;
	}
	/**
	 * Sets this session loggin type.
	 */
	private LogAdapter setNoLogging() {

		setLog( new NoLogAdapter(this));
		return this.log;
	}
	/**
	 * Sets this session logging type.
	 */
	public JavaLogAdapter setJavaLogging() {
		
		setLog( new JavaLogAdapter( this));
		
		return ( JavaLogAdapter)this.log;
	}
	/**
	 * Sets this session logging type.
	 */
	public ServerLogAdapter setServerLogging() {
		
		setLog( new ServerLogAdapter( this));
		
		return ( ServerLogAdapter)this.log;
	}
	/**
	 * Sets this session logging type.
	 */
	public DefaultSessionLogAdapter setDefaultLogging() {
		return this.setTopLinkLogging( 
			DefaultSessionLogAdapter.DEFAULT_LOG_FILE, DefaultSessionLogAdapter.DEFAULT_LOG_LEVEL);
	}
	/**
	 * Sets this session logging type.
	 */
	public DefaultSessionLogAdapter setTopLinkLogging( String fileName, String logLevel) {
		
		setLog( new DefaultSessionLogAdapter( this, fileName, logLevel));

		return ( DefaultSessionLogAdapter)this.log;
	}
	/**
	 * Returns this serverPlatform adapter.
	 */
	public ServerPlatformAdapter getServerPlatform() {
	    
	    return ( this.isManaged()) ? (( DatabaseSessionAdapter)this).getBroker().getServerPlatform() : this.serverPlatform;
	}
	/**
	 * Sets this ServerPlatform adapter and the config model.
	 */
	private void setServerPlatform( ServerPlatformAdapter platform) {
		
	    ServerPlatformAdapter old = this.serverPlatform;
		this.serverPlatform = platform;
		this.session().setServerPlatformConfig(( ServerPlatformConfig)platform.getModel());
		
		if( old != null && old.isCustom()) externalTransactionControllerClassChanged();
		this.firePropertyChanged( SERVER_PLATFORM_CONFIG_PROPERTY, old, platform);
	}
	/**
	 * Sets this log adapter and the config model.
	 */
	private void setLog( LogAdapter log) {
		
		Object old = this.log;
		this.log = log;
		this.session().setLogConfig(( LogConfig)log.getModel());
		this.firePropertyChanged( LOG_CONFIG_PROPERTY, old, log);
	}

	protected void setManaged( boolean managed) {
		
		boolean old = this.managedByBroker;
		this.managedByBroker = managed;
		this.firePropertyChanged( MANAGED_BY_BROKER, old, managed);
	}
	/**
	 * Returns this Config Model Object.
	 */
	private final SessionConfig session() {
		
		return ( SessionConfig)this.getModel();
	}

	public void toString( StringBuffer sb) {
		
		sb.append( this.getName());
	}
	/**
	 * Returns an iterator on this collection of sessionEventListener.
	 */
	public ListIterator sessionEventListenerConfigs() {
		
		return this.sessionEventManager.sessionEventListenerConfigs();
	}
	/**
	 * Returns the collection of sessionEventListener from the config model.
	 */
	private List getSessionEventListenerConfigs() {
		
		return this.sessionEventManager.getSessionEventListenerConfigs();
	}
	/**
	 * Adds the given sessionEventListener and fire notification.
	 */
	public void addPropertyConfigNamed( String name) {

		int i = this.sessionEventManager.addSessionEventListenerConfigNamed( name);
		
		this.fireItemAdded( SESSION_EVENT_LISTENERS_CONFIGS_LIST, i, name);
	}
	/**
	 * Removes the given sessionEventListener and fire notification.
	 */
	public String removePropertyConfigNamed( String name) {
		
		String removedItem = null;
		int i = this.getSessionEventListenerConfigs().indexOf( name);
		if( i != -1) {
			removedItem = this.sessionEventManager.removeSessionEventListenerConfigNamed( name);
			if( removedItem != null)
				this.fireItemRemoved( SESSION_EVENT_LISTENERS_CONFIGS_LIST, i, removedItem);
		}
		return removedItem;
	}
	private void removeAllPropertyConfigNamed() {
		List items = this.sessionEventManager.removeAllSessionEventListenerConfigNamed();

		if (items.size() > 0)
			this.fireItemsRemoved( SESSION_EVENT_LISTENERS_CONFIGS_LIST, 0, items);
	}
	/**
	 * Returns this config model property.
	 */
	public String getProfiler() {
		
		return this.session().getProfiler();
	}
	/**
	 * Sets this config model property.
	 */
	public void setProfiler( String name) {
		
		Object old = this.session().getProfiler();

		this.session().setProfiler( name);
		this.firePropertyChanged( PROFILER_PROPERTY, old, name);
	}

	abstract void externalTransactionControllerClassChanged();
	/**
	 * Returns this config model property.
	 */
	public String getExceptionHandlerClass() {
		
		return this.session().getExceptionHandlerClass();
	}
	/**
	 * Sets this config model property.
	 */
	public void setExceptionHandlerClass( String name) {
		
		Object old = this.session().getExceptionHandlerClass();

		this.session().setExceptionHandlerClass( name);
		this.firePropertyChanged( EXCEPTION_HANDLER_CLASS_PROPERTY, old, name);
	}
	/**
	 * Returns this config model property.
	 */
	public String getSessionCustomizerClass() {
		
		return this.session().getSessionCustomizerClass();
	}
	/**
	 * Sets this config model property.
	 */
	public void setSessionCustomizerClass( String name) {
		
		Object old = this.session().getSessionCustomizerClass();

		this.session().setSessionCustomizerClass( name);
		this.firePropertyChanged( SESSION_CUSTOMIZER_CLASS_PROPERTY, old, name);
	}

	protected void initializeDefaults() {

		super.initializeDefaults();

		this.setDefaultLogging();

		if( this.getProfiler() != null)
			this.setProfiler( null);

		if( this.getExceptionHandlerClass() != null)
			this.setExceptionHandlerClass( null);

		if( this.getSessionCustomizerClass() != null)
			this.setSessionCustomizerClass( null);

		if( ! this.hasNoClusteringService())
			this.setClusteringToNothing();

		removeAllPropertyConfigNamed();
	}
}
