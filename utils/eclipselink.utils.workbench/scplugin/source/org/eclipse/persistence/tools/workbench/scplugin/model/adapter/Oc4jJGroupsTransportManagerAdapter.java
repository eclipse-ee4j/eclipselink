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

import org.eclipse.persistence.internal.sessions.factories.model.transport.Oc4jJGroupsTransportManagerConfig;
import org.eclipse.persistence.internal.sessions.factories.model.transport.SunCORBATransportManagerConfig;

public final class Oc4jJGroupsTransportManagerAdapter extends TransportManagerAdapter {

	public final static String USE_SINGLE_THREADED_NOTIFICATION_PROPERTY = "useSingleThreadedNotification";

	public final static String TOPIC_NAME_PROPERTY = "topicName";

	/**
	 * Creates a new Discovery for the specified model object.
	 */
	Oc4jJGroupsTransportManagerAdapter( SCAdapter parent, Oc4jJGroupsTransportManagerConfig ojConfig) {
	
		super( parent, ojConfig);
	}
	/**
	 * Creates a new Discovery.
	 */
	protected Oc4jJGroupsTransportManagerAdapter( SCAdapter parent) {
	
		super( parent);
	}
	/**
	 * Factory method for building this model.
	 */
	protected Object buildModel() {

		return new Oc4jJGroupsTransportManagerConfig();
	}
	
	public boolean usesSingleThreadedNotification() {		
		return  this.oc4jManager().useSingleThreadedNotification();
	}

	public void setUseSingleThreadedNotification(boolean newValue) {
		
		boolean oldValue = this.oc4jManager().useSingleThreadedNotification();
		this.oc4jManager().setUseSingleThreadedNotification(newValue);
		this.firePropertyChanged( USE_SINGLE_THREADED_NOTIFICATION_PROPERTY, oldValue, newValue);
	}	

	/**
	 * Returns this Config Model Object.
	 */
	private final Oc4jJGroupsTransportManagerConfig oc4jManager() {
	
		return (Oc4jJGroupsTransportManagerConfig)this.getModel();
	}

	/**
	 * Returns this TopicName.
	 */
	public String getTopicName() {
		
		return this.oc4jManager().getTopicName();
	}

	/**
	 * Sets config model.
	 */
	public void setTopicName(String name) {
		
		Object oldValue = this.oc4jManager().getTopicName();
		this.oc4jManager().setTopicName(name);
		this.firePropertyChanged(TOPIC_NAME_PROPERTY, oldValue, name);
	}
}
