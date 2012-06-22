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
import java.util.Vector;

import org.eclipse.persistence.internal.sessions.factories.model.event.SessionEventManagerConfig;

/**
 * Session Configuration model adapter class for the 
 * TopLink Foudation Library class SessionEventManagerConfig
 * 
 * @see SessionEventManagerConfig
 * 
 * @author Tran Le
 */
final class SessionEventManagerAdapter extends SCAdapter {

	/**
	 * Creates a new SessionEventManagerAdapter for the specified model object.
	 */
	SessionEventManagerAdapter( SCAdapter parent, SessionEventManagerConfig scConfig) {
		
		super( parent, scConfig);
	}
	/**
	 * Creates a new SessionEventManagerAdapter.
	 */
	protected SessionEventManagerAdapter( SCAdapter parent) {
		
		super( parent);
	}
	/**
	 * Factory method for building this model.
	 */
	protected Object buildModel() {
		return new SessionEventManagerConfig();
	}
	/**
	 * Returns this Config Model Object.
	 */
	private final SessionEventManagerConfig manager() {
		
		return ( SessionEventManagerConfig)this.getModel();
	}
	/**
	 * Returns an iterator on this collection of SessionEventListeners.
	 */
	ListIterator sessionEventListenerConfigs() {
		
		return this.getSessionEventListenerConfigs().listIterator();
	}
	/**
	 * Returns the collection of SessionEventListeners from the config model.
	 */
	List getSessionEventListenerConfigs() {
		
		return this.manager().getSessionEventListeners();
	}
	/**
	 * Adds the given SessionEventListeners and fire notification.
	 */
	int addSessionEventListenerConfigNamed( String name) {

		int i = this.getSessionEventListenerConfigs().size();
		this.getSessionEventListenerConfigs().add( i, name);
		return( i);
	}
	/**
	 * Removes the given SessionEventListeners and fire notification.
	 */
	String removeSessionEventListenerConfigNamed( String name) {
		
		String removedItem = null;
		int i = this.getSessionEventListenerConfigs().indexOf( name);
		if( i != -1) {
			removedItem = ( String)this.getSessionEventListenerConfigs().remove( i);
		}
		return removedItem;
	}

	List removeAllSessionEventListenerConfigNamed() {
		Vector copy = new Vector( this.getSessionEventListenerConfigs());
		this.getSessionEventListenerConfigs().clear();
		return copy;
	}
}
