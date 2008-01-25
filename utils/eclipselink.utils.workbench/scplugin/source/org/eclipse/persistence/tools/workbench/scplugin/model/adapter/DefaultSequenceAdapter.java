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

import org.eclipse.persistence.internal.sessions.factories.model.sequencing.DefaultSequenceConfig;

/**
 * Session Configuration model adapter class for the 
 * TopLink Foudation Library class DefaultSequenceConfig
 * 
 * @see DefaultSequenceConfig
 * 
 * @author Tran Le
 */
class DefaultSequenceAdapter extends SequenceAdapter {
	
	/**
	 * Creates a new DefaultSequence for the specified model object.
	 */
	DefaultSequenceAdapter( SCAdapter parent, DefaultSequenceConfig scConfig) {
			
		super( parent, scConfig);
	}
	/**
	 * Creates a new DefaultSequence.
	 */
	protected DefaultSequenceAdapter( SCAdapter parent, String name, int preallocationSize) {
			
		super( parent, name, preallocationSize);
	}
	/**
	 * Returns this Config Model Object.
	 */
	private final DefaultSequenceConfig config() {
			
		return ( DefaultSequenceConfig)this.getModel();
	}
	/**
	 * Factory method for building this model.
	 */
	protected Object buildModel() {
		return new DefaultSequenceConfig();
	}
}
