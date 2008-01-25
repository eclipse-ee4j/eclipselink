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

import org.eclipse.persistence.internal.sessions.factories.model.sequencing.UnaryTableSequenceConfig;

/**
 * Session Configuration model adapter class for the 
 * TopLink Foudation Library class UnaryTableSequenceConfig
 * 
 * @see UnaryTableSequenceConfig
 * 
 * @author Tran Le
 */
class UnaryTableSequenceAdapter extends SequenceAdapter {
	
	/**
	 * Creates a new UnaryTableSequence for the specified model object.
	 */
	UnaryTableSequenceAdapter( SCAdapter parent, UnaryTableSequenceConfig scConfig) {
			
		super( parent, scConfig);
	}
	/**
	 * Creates a new UnaryTableSequence.
	 */
	protected UnaryTableSequenceAdapter( SCAdapter parent, String name, int preallocationSize) {
			
		super( parent, name, preallocationSize);
	}
	/**
	 * Returns this Config Model Object.
	 */
	private final UnaryTableSequenceConfig config() {
			
		return ( UnaryTableSequenceConfig)this.getModel();
	}
	/**
	 * Factory method for building this model.
	 */
	protected Object buildModel() {
		return new UnaryTableSequenceConfig();
	}

	String getSequenceCounterField() {
		return this.config().getCounterField();
	}

	void setSequenceCounterField( String value) {

		this.config().setCounterField( value);
	}

}
