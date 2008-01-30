/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/
package org.eclipse.persistence.internal.jpa.metadata.columns;

import javax.persistence.AttributeOverride;

/**
 * Object to hold onto an attribute override meta data.
 * 
 * @author Guy Pelletier
 * @since EclipseLink 1.0
 */
public class AttributeOverrideMetadata extends OverrideMetadata {
	private ColumnMetadata m_column;
	
	/**
	 * INTERNAL:
	 * Assumed to be used solely for OX loading.
	 */
	public AttributeOverrideMetadata() {}
	
	/**
	 * INTERNAL:
	 */
	public AttributeOverrideMetadata(AttributeOverride attributeOverride, String className) {
		super(className);
	
		setName(attributeOverride.name());
		m_column = new ColumnMetadata(attributeOverride.column(), attributeOverride.name());
	}
	
	/**
	 * INTERNAL:
	 * Used for OX mapping.
	 */
	public ColumnMetadata getColumn() {
		return m_column;
	}
	
	/**
	 * INTERNAL:
	 * Used for OX mapping.
	 */
	public void setColumn(ColumnMetadata column) {
		m_column = column;
	}
}
