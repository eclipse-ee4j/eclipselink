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

import org.eclipse.persistence.internal.sessions.factories.model.sequencing.SequenceConfig;
import org.eclipse.persistence.tools.workbench.scplugin.model.SequenceType;

/**
 * Session Configuration model adapter class for the 
 * TopLink Foudation Library class SequenceConfig
 * 
 * @see SequenceConfig
 * 
 * @author Tran Le
 */
public abstract class SequenceAdapter extends SCAdapter implements Nominative {
	
	public static final String PREALLOCATION_SIZE_PROPERTY = "preAllocationSize";
	public static final String NAME_PROPERTY = "name";
	
	private boolean isTheDefaultSequence;
	
	/**
	 * Creates a new SequenceAdapter for the specified model object.
	 */
	SequenceAdapter( SCAdapter parent, SequenceConfig scConfig) {
			
		super( parent, scConfig);
	}
	/**
	 * Creates a new SequenceAdapter.
	 */
	protected SequenceAdapter( SCAdapter parent, String name, int preallocationSize) {
			
		super( parent);	
		
		this.setName( name);	
		this.setPreallocationSize( new Integer(preallocationSize));
	}
	/**
	 * Returns this Config Model Object.
	 */
	private final SequenceConfig sequence() {
			
		return ( SequenceConfig)this.getModel();
	}
	/**
	 * Factory method for building this model.
	 */
	protected Object buildModel() {
	
		return new SequenceConfig();
	}
	/**
	 * Returns this config model property.
	 */
	public String getName() {
			
		return this.sequence().getName();
	}
	/**
	 * Sets this config model property.
	 */
	public void setName( String name) {
		String old = this.sequence().getName();
		this.sequence().setName( name);
		firePropertyChanged(NAME_PROPERTY, old, name);
	}
	/**
	 * Returns this config model property.
	 */
	public int getPreallocationSize() {
			
		Integer size = this.sequence().getPreallocationSize();
		return (size != null) ? size.intValue() : 0;
	}
	/**
	 * Sets this config model property.
	 */
	public void setPreallocationSize(Integer size) {
		Integer old = this.sequence().getPreallocationSize();	
		this.sequence().setPreallocationSize( new Integer( size));
		firePropertyChanged(PREALLOCATION_SIZE_PROPERTY, old, size);
	}
		
	public String displayString() {
			
		return this.getName();
	}
	
	public void toString( StringBuffer sb) {
			
		sb.append( this.getName());
	}
	/**
	 * Temporary support for Multiple Sequencing schema.
	 */
	public boolean isDefault() {
			
		return false;
	}
	/**
	 * Temporary support for Multiple Sequencing schema.
	 */
	public boolean isNative() {
			
		return false;
	}	
	/**
	 * Temporary support for Multiple Sequencing schema.
	 */
	public boolean isXMLFile() {
		return false;
	}
	
	/**
	 * Temporary support for Multiple Sequencing schema.
	 */
	public boolean isCustom() {
			
		return false;
	}
	
	public abstract SequenceType getType();
	public boolean isTheDefaultSequence() {
		return isTheDefaultSequence;
	}
	public void setTheDefaultSequence(boolean isTheDefaultSequence) {
		this.isTheDefaultSequence = isTheDefaultSequence;
	}
}
