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

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Vector;
import java.util.prefs.Preferences;

import org.eclipse.persistence.internal.sessions.factories.model.SessionConfigs;
import org.eclipse.persistence.tools.workbench.scplugin.model.meta.SCSessionsProperties;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.iterators.CloneIterator;
import org.eclipse.persistence.tools.workbench.utility.iterators.CompositeIterator;
import org.eclipse.persistence.tools.workbench.utility.iterators.FilteringIterator;
import org.eclipse.persistence.tools.workbench.utility.iterators.TransformationIterator;

import org.eclipse.persistence.Version;

/**
 * Session Configuration model adapter class for the 
 * TopLink Foudation Library class TopLinkSessions
 * 
 * @see TopLinkSessions
 * 
 * @author Tran Le
 */
public final class TopLinkSessionsAdapter extends RootSCAdapter {

	// property change
	public final static String VERSION_PROPERTY = "version";
	private Collection sessions;
	public final static String SESSIONS_COLLECTION = "sessions";
	
	public static final String VERSION_9_0_4 = "9.0.4";
	public static final String VERSION_10g = "10g";
	// internal
	private Collection managedSessions;

	/**
	 * Constructor for TopLinkSessionsAdapter and load the config from file.
	 * @param buildModel 
	 * 				true - creates a new config model
	 * 				false - load config model from file
	 */
	public TopLinkSessionsAdapter( SCSessionsProperties properties, Preferences preferences, boolean buildModel) {
		super( properties, preferences, buildModel);	
	}
	/**
	 * Subclasses should override this method to add their children
	 * to the specified collection.
	 * @see #children()
	 */
	protected void addChildrenTo( List children) {
		super.addChildrenTo( children);
		
		CollectionTools.addAll( children, sessionBrokers());
		CollectionTools.addAll( children, databaseSessions());
	}
	/**
	 * Factory method for building this model.
	 */
	protected Object buildModel() {
		return new SessionConfigs();
	}
	/**
	 * Initializes this adapter.
	 */
	protected void initialize() {
		super.initialize();
	
		this.sessions = new Vector();
		this.managedSessions = new Vector();
	}
	/**
	* Initializes this new model inst. var. and aggregates.
	*/
   protected void initialize( Object newConfig) {
	   super.initialize( newConfig);

	   topLinkSessions().setVersion( Version.getVersion());
   }
	/**
	 * Initializes this adapter from the config model.
	 */
	protected void initializeFromModel( Object scConfig) {
		super.initializeFromModel( scConfig);

		this.sessions.addAll( this.adaptAll( this.getSessionConfigs()));
		this.initializeBroker();
	}
	/**
	 * Initializes all brokers from the config model.
	 */
	private void initializeBroker() {
		for( Iterator i = this.sessionBrokers(); i.hasNext();) {
			SessionBrokerAdapter broker = ( SessionBrokerAdapter)i.next();
			for( Iterator j = broker.sessionNames(); j.hasNext();) {
				broker.internalAddSessionNamed(( String)j.next());
			}
		}
	}
	/**
	 * Post Initialization: allows validation, handle legacy model...
	 */
	protected void postInitializationFromModel() {
		
		for( Iterator i = allSessions(); i.hasNext(); ) {
			(( SCAdapter)i.next()).postInitializationFromModel();
		}
	}	
	/**
	 * Pre Saving: Clean up all Sessions config model before writing.
	 */
	protected void preSaving() {
		
		for( Iterator i = allSessions(); i.hasNext(); ) {
		    (( SCAdapter)i.next()).preSaving();
		}
	}
	/**
	 * Post Saving: Re-initialize all Sessions config model after writing.
	 */
	protected void postSaving() {
		
		for( Iterator i = allSessions(); i.hasNext(); ) {
		    (( SCAdapter)i.next()).postSaving();
		}
	}
	/**
	 * Returns this version.
	 */
	public String getVersion() {
		
		return this.topLinkSessions().getVersion();
	}
	/**
	 * Sets this version and the config model.
	 */
	void setVersion( String version) {

		Object old = this.topLinkSessions().getVersion();

		this.topLinkSessions().setVersion( version);
		this.firePropertyChanged( VERSION_PROPERTY, old, version);
	}
	/**
	 * Returns a collection of names from the sessions collection.
	 */
	public Collection getSessionsNames() {
		return getSessionsNames( this.sessions());
	}
	/**
	 * Returns a collection of names from the managed sessions collection.
	 */
	public Collection getManagedSessionsNames() {
		return getSessionsNames( this.managedSessions());
	}

	public Collection getAllSessionsNames() {
		return getSessionsNames( this.allSessions());
	}
		
	public Collection getDatabaseSessionsNames() {
		return getSessionsNames( this.databaseSessions());
	}
	
	Collection getSessionsNames( Iterator sessions) {

		return CollectionTools.collection( new TransformationIterator( sessions) {
			protected Object transform( Object next) {
				return (( SessionAdapter)next).getName();
			}
		});
	}
	
	Iterator getNames( Iterator sessions) {
		return new TransformationIterator( sessions) {
			protected Object transform( Object next) {
				return (( SessionAdapter)next).getName();
			}
		};
	}
	/**
	 * Returns an iterator on the collection of names of the sessions not managed
	 * by a broker.
	 */
	public Iterator sessionNames() {
		return getNames( this.sessions());
	}
	
	public Iterator managedSessionNames() {
		return getNames( this.managedSessions());
	}
	
	public Iterator databaseSessionNames() {
		return getNames( this.databaseSessions());
	}
	
	public Iterator allSessionNames() {
		return getNames( this.allSessions());
	}
	/**
	 * Returns an iterator on the collection of sessions not managed by a broker.
	 * i.e. non managed sessions + broker 
	 */
	public Iterator sessions() {
		
		return new CloneIterator(this.sessions);
	}
	
	public int sessionsSize() {
		
		return this.sessions.size();
	}
	/**
	 * Returns an iterator on the collection of sessions managed by a broker.
	 * i.e. managed database + server sessions
	 */
	public Iterator managedSessions() {
		
		return new CloneIterator(this.managedSessions);
	}
	
	/**
	 * Returns an iterator on the collection of session brokers.
	 */
	public Iterator sessionBrokers() {
		
		return new FilteringIterator( sessions()) {
			protected boolean accept( Object next) {
				return (( SessionAdapter)next).isBroker();
			}
		};
	}
	/**
	 * Returns an iterator on the collection of the name of the session brokers.
	 */
	public Iterator sessionBrokerNames() {

		return getNames( sessionBrokers());
	}
	/**
	 * Returns an iterator on the collection of sessions managed by the given
	 * broker. i.e. managed database + server sessions
	 */
	Iterator managedSessions( final SessionBrokerAdapter broker) {
		
		return new FilteringIterator( managedSessions()) {
			protected boolean accept( Object next) {
				return (( DatabaseSessionAdapter)next).getBroker() == broker;
			}
		};
	}
	
	public int managedSessionsSize() {
		
		return this.managedSessions.size();
	}
	/**
	 * Returns an iterator on the collection of database sessions not managed by a broker.
	 * i.e. non managed database + server sessions
	 */
	public Iterator databaseSessions() {
		
		return new FilteringIterator( this.sessions()) {
		   protected boolean accept( Object next) {
			  return !(( SessionAdapter)next).isBroker();
		   }
		};
	}
	
	public int databaseSessionsSize() {
		
		return CollectionTools.collection( databaseSessions()).size();
	}
	/**
	 * Returns all the sessions and managed session.
	 * i.e. sessions() + managedSessions()
	 */
	public Iterator allSessions() {
		return new CompositeIterator( this.sessions(), this.managedSessions());
	}
	/**
	 * Returns the collection of sessions from the config model.
	 */
	private Collection getSessionConfigs() {
		
		return this.topLinkSessions().getSessionConfigs();
	}
	/**
	 * Returns a session that not manage by a broker.
	 */
	public SessionAdapter sessionNamed( String name) {

		for( Iterator i = sessions(); i.hasNext();) {
			SessionAdapter session = ( SessionAdapter) i.next();
			if( name.equals( session.getName())) return session;
		}	
		return null;	
	}
	/**
	 * Returns a session that not manage by a broker.
	 */
	public SessionAdapter databaseSessionNamed( String name) {
		SessionAdapter session = this.sessionNamed( name);
		if( session.isBroker()) return null;
		return session;
	}
	/**
	 * Returns a session that not manage by a broker.
	 */
	public DatabaseSessionAdapter managedSessionNamed( String name) {

		for( Iterator i = managedSessions(); i.hasNext();) {
			DatabaseSessionAdapter session = ( DatabaseSessionAdapter) i.next();
			if( name.equals( session.getName()))
				return session;
		}	
		return null;	
	}
	/**
	 * Adds to the sessions collection (none managed) and to the config model collection.
	 */
	private SessionAdapter addSession( SessionAdapter sessionAdapter) {

		sessionAdapter.markEntireConfigurationClean();

		this.getSessionConfigs().add( sessionAdapter.getModel());

		this.addItemToCollection( sessionAdapter, this.sessions, SESSIONS_COLLECTION);
		return sessionAdapter;
	}
	/**
	 * Removes the given session and fire notification.
	 */
	public void removeSession( SessionAdapter sessionAdapter) {	
		
		this.getSessionConfigs().remove( sessionAdapter.getModel());

		this.removeItemFromCollection( sessionAdapter, this.sessions, SESSIONS_COLLECTION);
	}
	/**
	 * Removes the given sessions and fire notification.
	 */
	public void removeSessions( Collection sessionAdapters) {
		
		this.getSessionConfigs().removeAll( modelObjectsFrom( sessionAdapters));
		
		this.removeItemsFromCollection( sessionAdapters, this.sessions, SESSIONS_COLLECTION);
	}

	/**
	 * Factory method for adding DatabaseSession.
	 */
	public DatabaseSessionAdapter addDatabaseSessionNamed( String name, ServerPlatform sp, DataSource ds) {
		
		DatabaseSessionAdapter session = new DatabaseSessionAdapter( this, name, sp, ds);
		
		return ( DatabaseSessionAdapter)this.addSession( session);
	}
	/**
	 * Factory method for adding ServerSession.
	 */
	public ServerSessionAdapter addServerSessionNamed( String name, ServerPlatform sp, DataSource ds) {
		
		ServerSessionAdapter session = new ServerSessionAdapter( this, name, sp, ds);
		
		return ( ServerSessionAdapter)this.addSession( session);
	}
	/**
	 * Factory method for adding SessionBroker.
	 */
	public SessionBrokerAdapter addSessionBrokerNamed( String name, ServerPlatform sp) {
		
		SessionBrokerAdapter broker = new SessionBrokerAdapter( this, name, sp);
		
		return ( SessionBrokerAdapter)this.addSession( broker);
	}
	/**
	 * Remove the session with the given name.
	 */
	public void removeSessionNamed( String name) {
		
		this.removeSession( this.sessionNamed( name));
	}
	/**
	 * Returns this Config Model Object.
	 */
	private final SessionConfigs topLinkSessions() {
		return (SessionConfigs)this.getModel();
	}
	
	private static final String CR = System.getProperty("line.separator");
	public void toString( StringBuffer sb) {
		super.toString( sb);
		
		sb.append(" [").append(CR);
		for( Iterator i = this.allSessions(); i.hasNext(); ) {
			SessionAdapter session = ( SessionAdapter)i.next();
			sb.append( "\t\t");
			session.toString( sb);
			if( i.hasNext()) {
				sb.append(',').append(CR);
			}
		}
		sb.append(" ] ");
	}
	/**
	 * Moves session from the sessions collection to the managed sessions collection
	 * Returns null if session is already entered or is a broker.
	 */
	DatabaseSessionAdapter addManagedSessionNamed( String name) {
		SessionAdapter session = this.sessionNamed( name);
		if( session == null)
			throw new NoSuchElementException();
		if( session.isBroker()) 
			throw new IllegalArgumentException( session.toString());
		
		this.removeItemFromCollection( session, this.sessions, SESSIONS_COLLECTION);
		this.managedSessions.add( session);
		return ( DatabaseSessionAdapter)session;
	}
	/**
	 * Moves session from the managed sessions collection to the sessions collection.
	 */
	DatabaseSessionAdapter removeManagedSessionNamed( String name) {
		DatabaseSessionAdapter session = this.managedSessionNamed( name);

		this.managedSessions.remove( session);
		this.addItemToCollection( session, this.sessions, SESSIONS_COLLECTION);
		return session;
	}
	/**
	 * Moves sessions from the managed sessions collection to the sessions collection.
	 */
	Collection removeManagedSessions( Collection sessions) {

		this.managedSessions.removeAll( sessions);
		this.addItemsToCollection( sessions, this.sessions, SESSIONS_COLLECTION);
		return sessions;
	}
}
