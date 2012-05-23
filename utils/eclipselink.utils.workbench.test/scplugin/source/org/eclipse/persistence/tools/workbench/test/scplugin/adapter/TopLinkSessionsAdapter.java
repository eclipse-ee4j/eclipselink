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
package org.eclipse.persistence.tools.workbench.test.scplugin.adapter;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;

import org.eclipse.persistence.Version;
import org.eclipse.persistence.internal.sessions.factories.model.SessionConfigs;
import org.eclipse.persistence.internal.sessions.factories.model.session.SessionConfig;
/**
 * Session Configuration model adapter class for the 
 * TopLink Foudation Library class TopLinkSessions
 * 
 * @see TopLinkSessions
 * 
 * @author Tran Le
 */
public class TopLinkSessionsAdapter extends AbstractSessionsXmlTest {

	/**
	 * Constructor for TopLinkSessionsAdapter and load the config from file.
	 * @param buildModel 
	 * 				true - creates a new config model
	 * 				false - load config model from file
	 */
	public TopLinkSessionsAdapter( String xmlFile, boolean buildModel) {
		super( new File( xmlFile), buildModel);	
	}
	/**
	 * Adds the given session and fire notification.
	 */
	public void addSessionConfig( SessionConfig sessionConfig) {

		this.getSessionConfigs().add( sessionConfig);
	}
	
	public void removeSessionConfig( SessionConfig sessionConfig) {

		this.getSessionConfigs().remove( sessionConfig);
	}
	/**
	 * Factory method for building this model.
	 */
	protected Object buildModel() {
		return new SessionConfigs();
	}
	/**
	 * Returns this version.
	 */
	public String getVersion() {
		
		return this.topLinkSessions().getVersion();
	}
	/**
	 * Returns the collection of sessions from the config model.
	 */
	private Collection getSessionConfigs() {
		
		return this.topLinkSessions().getSessionConfigs();
	}
	/**
	 * Initializes this adapter from the config model.
	 */
	protected void initializeFromModel( Object scConfig) {
		
		super.initializeFromModel( scConfig);
		
		SessionConfigs topLinkSessions = ( SessionConfigs)this.getModel();
		if( topLinkSessions.getVersion() == null)
			topLinkSessions.setVersion( Version.getVersion());
	}
	/**
	 * Returns an iterator on a collection of session adapters.
	 */
	public Iterator sessionConfigs() {
		
		return this.getSessionConfigs().iterator();
	}
	/**
	 * Returns the appropriate session.
	 */
	public SessionConfig sessionConfigNamed( String name) {

		for( Iterator i = sessionConfigs(); i.hasNext();) {
			SessionConfig session = ( SessionConfig) i.next();
			if( name.equals( session.getName()))
				return session;
		}	
		return null;	
	}
	/**
	 * Returns this Config Model Object.
	 */
	private final SessionConfigs topLinkSessions() {
		return ( SessionConfigs)this.getModel();
	}
	
	public void toString( StringBuffer sb) {
		super.toString( sb);
		
		sb.append(" [\n");
		for( Iterator i = sessionConfigs(); i.hasNext(); ) {
			SessionConfig session = ( SessionConfig)i.next();
			sb.append( "\t\t" + session.getName());
			if( i.hasNext()) {
				sb.append( ",\n");
			}
		}
		sb.append(" ] ");
	}
}
