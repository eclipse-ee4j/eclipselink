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

import org.eclipse.persistence.internal.sessions.factories.model.transport.discovery.DiscoveryConfig;

/**
 * Session Configuration model adapter class for the 
 * TopLink Foudation Library class DiscoveryConfig
 * 
 * @see DiscoveryConfig
 * 
 * @author Tran Le
 */
final class DiscoveryAdapter extends SCAdapter {

	/**
	 * Creates a new Discovery for the specified model object.
	 */
	DiscoveryAdapter( SCAdapter parent, DiscoveryConfig scConfig) {
		
		super( parent, scConfig);
	}
	/**
	 * Creates a new Discovery.
	 */
	protected DiscoveryAdapter( SCAdapter parent) {
		
		super( parent);
	}
	/**
	 * Returns this Config Model Object.
	 */
	private final DiscoveryConfig discovery() {
		
		return ( DiscoveryConfig)this.getModel();
	}
	/**
	 * Factory method for building this model.
	 */
	protected Object buildModel() {
		return new DiscoveryConfig();
	}
	/**
	 * Returns this config model property.
	 */
	String getMulticastGroupAddress() {
		
		return this.discovery().getMulticastGroupAddress();
	}
	/**
	 * Sets this config model property.
	 */
	void setMulticastGroupAddress( String address) {
		
		this.discovery().setMulticastGroupAddress( address);
	}
	/**
	 * Returns this config model property.
	 */
	int getMulticastPort() {
		
		return this.discovery().getMulticastPort();
	}
	/**
	 * Sets this config model property.
	 */
	void setMulticastPort( int value) {
		
		this.discovery().setMulticastPort( value);
	}
	/**
	 * Returns this config model property.
	 */
	int getAnnouncementDelay() {
		
		return this.discovery().getAnnouncementDelay();
	}
	/**
	 * Sets this config model property.
	 */
	void setAnnouncementDelay( int value) {
		
		this.discovery().setAnnouncementDelay( value);
	}
	/**
	 * Returns this config model property.
	 */
	int getPacketTimeToLive() {
		
		return this.discovery().getPacketTimeToLive();
	}
	/**
	 * Sets this config model property.
	 */
	void setPacketTimeToLive( int value) {
		
		this.discovery().setPacketTimeToLive( value);
	}

}
