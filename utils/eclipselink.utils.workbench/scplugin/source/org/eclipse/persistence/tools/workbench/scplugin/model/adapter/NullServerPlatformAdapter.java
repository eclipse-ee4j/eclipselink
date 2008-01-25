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

import org.eclipse.persistence.tools.workbench.utility.node.Node;

import org.eclipse.persistence.platform.server.NoServerPlatform;


/**
 * Session Configuration model adapter class for the 
 * TopLink Foudation Library class PlatformConfig
 * 
 * @author Tran Le
 */
public final class NullServerPlatformAdapter extends ServerPlatformAdapter
{
	// singleton
	private static ServerPlatformAdapter INSTANCE;

	/**
	 * Return the singleton.
	 */
	public static synchronized ServerPlatformAdapter instance() {
		if( INSTANCE == null) {
			INSTANCE = new NullServerPlatformAdapter();
		}
		return INSTANCE;
	}

	private NullServerPlatformAdapter() {
		super( null);
	}

	protected Object buildModel() {

		return null;
	}

	protected void initializeDefaults() {
	}

	protected void checkParent( Node parent) {
	}

	public String getServerClassName() {

		return NoServerPlatform.class.getName();
	}

	public boolean isNull() {
		
		return true;
	}
	/**
	 * Returns this config model property.
	 */
	public boolean getEnableJTA() {
		
		return false;
	}
	/**
	 * Sets this config model property.
	 */
	public void setEnableJTA( boolean value) {
		
	    throw new IllegalStateException();
	}
	/**
	 * Returns this config model property.
	 */
	public boolean getEnableRuntimeServices() {
		
		return false;
	}
	/**
	 * Sets this config model property.
	 */
	public void setEnableRuntimeServices( boolean value) {
		
	    throw new IllegalStateException();
	}
}