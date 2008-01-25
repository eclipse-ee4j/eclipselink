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

import org.eclipse.persistence.internal.sessions.factories.model.rcm.command.CommandsConfig;

/**
 * Session Configuration model adapter class for the 
 * TopLink Foudation Library class CommandsConfig
 * 
 * @see CommandsConfig
 * 
 * @author Tran Le
 */
final class CommandsAdapter extends SCAdapter {

	/**
	 * Creates a new Discovery for the specified model object.
	 */
	CommandsAdapter( SCAdapter parent, CommandsConfig scConfig) {
		
		super( parent, scConfig);
	}
	/**
	 * Creates a new Discovery.
	 */
	protected CommandsAdapter( SCAdapter parent) {
		
		super( parent);
	}
	/**
	 * Returns this Config Model Object.
	 */
	private final CommandsConfig commands() {
		
		return ( CommandsConfig)this.getModel();
	}
	/**
	 * Factory method for building this model.
	 */
	protected Object buildModel() {
		return new CommandsConfig();
	}
	/**
	 * Returns this config model property..
	 */
	boolean usesCacheSync() {
		
		return this.commands().getCacheSync();
	}
	/**
	 * Sets this config model property.
	 */
	void setCacheSync( boolean value) {
		
		this.commands().setCacheSync( value);
	}
}
