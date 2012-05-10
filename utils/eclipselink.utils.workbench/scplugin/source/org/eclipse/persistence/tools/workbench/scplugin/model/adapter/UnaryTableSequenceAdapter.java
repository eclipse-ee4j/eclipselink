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

import org.eclipse.persistence.internal.sessions.factories.model.sequencing.UnaryTableSequenceConfig;
import org.eclipse.persistence.tools.workbench.scplugin.model.SequenceType;

/**
 * Session Configuration model adapter class for the 
 * TopLink Foudation Library class UnaryTableSequenceConfig
 * 
 * @see UnaryTableSequenceConfig
 * 
 * @author Tran Le
 */
public class UnaryTableSequenceAdapter extends SequenceAdapter {
	
	public static final String COUNTER_FIELD_PROPERTY = "counterField";
	
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

	public String getSequenceCounterField() {
		return this.config().getCounterField();
	}

	public void setSequenceCounterField( String value) {
		String old = this.config().getCounterField();
		this.config().setCounterField( value);
		firePropertyChanged(COUNTER_FIELD_PROPERTY, old, value);
	}
	
	@Override
	public SequenceType getType() {
		return SequenceType.UNARY_TABLE;
	}

}
