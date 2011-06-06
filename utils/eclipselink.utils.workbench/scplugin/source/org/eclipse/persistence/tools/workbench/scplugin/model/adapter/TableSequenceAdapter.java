/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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

import org.eclipse.persistence.internal.sessions.factories.model.sequencing.TableSequenceConfig;
import org.eclipse.persistence.tools.workbench.scplugin.model.SequenceType;

/**
 * Session Configuration model adapter class for the 
 * TopLink Foudation Library class TableSequenceConfig
 * 
 * @see TableSequenceConfig
 * 
 * @author Tran Le
 */
public class TableSequenceAdapter extends SequenceAdapter {
	
	public static final String TABLE_PROPERTY = "sequenceTable";
	public static final String NAME_FIELD_PROPERTY = "nameField";
	public static final String COUNTER_FIELD_PROPERTY = "counterField";
	
	/**
	 * Creates a new TableSequence for the specified model object.
	 */
	TableSequenceAdapter( SCAdapter parent, TableSequenceConfig scConfig) {
			
		super( parent, scConfig);
	}
	/**
	 * Creates a new TableSequence.
	 */
	protected TableSequenceAdapter( SCAdapter parent, String name, int preallocationSize) {
			
		super( parent, name, preallocationSize);
	}
	/**
	 * Returns this Config Model Object.
	 */
	private final TableSequenceConfig config() {
			
		return ( TableSequenceConfig)this.getModel();
	}
	/**
	 * Factory method for building this model.
	 */
	protected Object buildModel() {
		return new TableSequenceConfig();
	}

	public String getSequenceTable() {
		return this.config().getTable();
	}

	public void setSequenceTable( String value) {
		String old = this.config().getTable();
		this.config().setTable( value);
		firePropertyChanged(TABLE_PROPERTY, old, value);
	}

	public String getSequenceNameField() {
		return this.config().getNameField();
	}

	public void setSequenceNameField( String value) {
		String old = this.config().getNameField();
		this.config().setNameField( value);
		firePropertyChanged(NAME_FIELD_PROPERTY, old, value);
	}

	public String getSequenceCounterField() {
		return this.config().getCounterField();
	}

	public void setSequenceCounterField( String value) {
		String old = this.config().getCounterField();
		this.config().setCounterField( value);
		firePropertyChanged(COUNTER_FIELD_PROPERTY, old, value);
	}
	
	public boolean isCustom() {

		return (config().getName().equals("Custom"));
	}
	
	public boolean isDefault() {
		
	    return (config().getName().equals("Default"));
	}
	
	@Override
	public SequenceType getType() {
		return SequenceType.TABLE;
	}
}
