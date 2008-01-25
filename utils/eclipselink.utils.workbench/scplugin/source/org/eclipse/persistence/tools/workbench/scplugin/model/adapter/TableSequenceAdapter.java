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

import org.eclipse.persistence.internal.sessions.factories.XMLSessionConfigProject;
import org.eclipse.persistence.internal.sessions.factories.model.sequencing.TableSequenceConfig;

/**
 * Session Configuration model adapter class for the 
 * TopLink Foudation Library class TableSequenceConfig
 * 
 * @see TableSequenceConfig
 * 
 * @author Tran Le
 */
class TableSequenceAdapter extends SequenceAdapter {
	
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

	String getSequenceTable() {
		return this.config().getTable();
	}

	void setSequenceTable( String value) {

		this.config().setTable( value);
	}

	String getSequenceNameField() {
		return this.config().getNameField();
	}

	void setSequenceNameField( String value) {

		this.config().setNameField( value);
	}

	String getSequenceCounterField() {
		return this.config().getCounterField();
	}

	void setSequenceCounterField( String value) {

		this.config().setCounterField( value);
	}
	
	public boolean isCustom() {

		return !this.isDefault();
	}
	
	public boolean isDefault() {
		
	    if( getSequenceTable() == null ||  getSequenceTable().length() == 0) 
	        return true;
	    else if( getSequenceTable().equals( XMLSessionConfigProject.SEQUENCE_TABLE_DEFAULT)
	    		&& getSequenceNameField().equals(XMLSessionConfigProject.SEQUENCE_NAME_FIELD_DEFAULT)
	    		&& getSequenceCounterField().equals(XMLSessionConfigProject.SEQUENCE_COUNTER_FIELD_DEFAULT)) 
	        return true;

		return false;
		}
}
