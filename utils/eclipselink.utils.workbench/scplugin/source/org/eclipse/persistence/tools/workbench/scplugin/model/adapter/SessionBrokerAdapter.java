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

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eclipse.persistence.tools.workbench.scplugin.SCProblemsConstants;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;


import org.eclipse.persistence.internal.sessions.factories.model.session.SessionBrokerConfig;

/**
 * Session Configuration model adapter class for the 
 * TopLink Foudation Library class SessionBrokerConfig
 * 
 * @see SessionBrokerConfig
 * 
 * @author Tran Le
 */
public final class SessionBrokerAdapter extends SessionAdapter {
	// property change
	public final static String SESSIONS_COLLECTION = "sessions";

	/**
	 * Creates a new SessionBrokerAdapter for the specified model object.
	 */
	SessionBrokerAdapter( SCAdapter parent, SessionBrokerConfig scConfig) {
		
		super( parent, scConfig);
	}
	/**
	 * Creates a new SessionBroker.
	 */
	protected SessionBrokerAdapter( SCAdapter parent, String name, ServerPlatform sp) {
		
		super( parent, name);
		
	    this.initializeServerPlatform( sp);
	}
	/**
	 * Factory method for building this model.
	 */
	protected Object buildModel() {
		return new SessionBrokerConfig();
	}
	/**
	 * Returns this Config Model Object.
	 */
	private final SessionBrokerConfig sessionBroker() {
		
		return ( SessionBrokerConfig)this.getModel();
	}
	/**
	 * Returns this Config Model Object.
	 */
	private TopLinkSessionsAdapter topLinkSessions() {
		
		return ( TopLinkSessionsAdapter)this.getParent();
	}
	
	public boolean isBroker() {
		return true;
	}
	/**
	 * Returns the appropriate session.
	 */
	public SessionAdapter sessionNamed( String name) {
		
		return topLinkSessions().managedSessionNamed( name);
	}
	/**
	 * Returns the sessions managed by this broker.
	 * Helper method to access TopLinkSessionsAdapter collection
	 */
	public Iterator sessions() {
		
		return topLinkSessions().managedSessions( this);
	}
	/**
	 * Returns the number of session managed by this broker.
	 */
	public int sessionsSize() {
		
		return this.getSessionNameConfigs().size();
	}
	/**
	 * Returns an iterator on this collection of sessionNames.
	 */
	Iterator sessionNames() {
		
		return this.getSessionNameConfigs().iterator();
	}
	/**
	 * Returns the collection of sessionNames from the config model.
	 */
	private Collection getSessionNameConfigs() {
		
		return this.sessionBroker().getSessionNames();
	}
	/**
	 * Manage the session with the given name.
	 */
	public DatabaseSessionAdapter manage( String name) {
		
		DatabaseSessionAdapter session = topLinkSessions().addManagedSessionNamed( name);
		session.setBroker( this);
		
		this.getSessionNameConfigs().add( name);
		this.fireItemAdded( SESSIONS_COLLECTION, session);
		return session;
	}
	/**
	 * Internal: Used when loading a config ( add to ManagedSession collection only).
	 */
	void internalAddSessionNamed( String name) {
		
		DatabaseSessionAdapter session = topLinkSessions().addManagedSessionNamed( name);
		session.setBroker( this);
	}
	/**
	 * UnManage the session with the given name.
	 */
	public DatabaseSessionAdapter unManage( String name) {
		
		DatabaseSessionAdapter session = topLinkSessions().removeManagedSessionNamed( name);
		session.setBroker( null);
		
		this.getSessionNameConfigs().remove( name);
		this.fireCollectionChanged( SESSIONS_COLLECTION);	
		return session;
	}	
	/**
	 * UnManage the given collection of sessions.
	 */
	public Collection unManage( Collection sessions) {
		
		Collection removedSessions = topLinkSessions().removeManagedSessions( sessions);

		for( Iterator i = removedSessions.iterator(); i.hasNext(); ) {
			(( DatabaseSessionAdapter)i.next()).setBroker( null);
		}
		Collection removedSessionsNames = topLinkSessions().getSessionsNames( removedSessions.iterator());
		this.getSessionNameConfigs().removeAll( removedSessionsNames);
		this.fireCollectionChanged( SESSIONS_COLLECTION);
		return removedSessions;
	}	

	void externalTransactionControllerClassChanged() {
		
		for( Iterator iter = sessions(); iter.hasNext(); ) {

			DatabaseSessionAdapter session = ( DatabaseSessionAdapter)iter.next();
			    
			session.externalTransactionControllerClassChanged();
		}
	}

	protected void setManaged( boolean managed) {
		
	    throw new IllegalStateException();
	}
	/**
	 * Adds the children of this adapter to the given list.
	 * @param children The list of children
	 */
	protected void addChildrenTo(List children) {
		super.addChildrenTo(children);
		CollectionTools.addAll( children, sessions());
	}
	/**
	 * Add any problems from this adapter to the given set.
	 */
	protected void addProblemsTo( List branchProblems) {

		super.addProblemsTo(branchProblems);

		verifyProblemSessionCount(branchProblems);
	}

	private void verifyProblemSessionCount( List branchProblems) {

		if( sessionsSize() == 0) {

			branchProblems.add(buildProblem( SCProblemsConstants.SESSION_BROKER_SESSION_COUNT, displayString()));
		}
	}

	void sessionRenamed( String oldName, String newName) {

		this.getSessionNameConfigs().remove( oldName);
		this.getSessionNameConfigs().add( newName);
	}
}
