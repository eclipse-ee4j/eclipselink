/*******************************************************************************
 * Copyright (c) 1998, 2008 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * @author Guy Pelletier
 * @since EclipseLink 1.0
 *******************************************************************************/
package org.eclipse.persistence.internal.jpa.metadata.tables;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.UniqueConstraint;

/**
 * INTERNAL:
 * Object to hold onto a unique constraint metadata.
 */
public class UniqueConstraintMetadata {
	private List<String> m_columnNames;
	
	/**
     * INTERNAL:
     */
	public UniqueConstraintMetadata() {}
	
	/**
     * INTERNAL:
     */
	public UniqueConstraintMetadata(UniqueConstraint uniqueConstraint) {
		m_columnNames = new ArrayList<String>();
		
		for (String columnName : uniqueConstraint.columnNames()) {
			m_columnNames.add(columnName);
		}
	}
	
	/**
     * INTERNAL:
     */
	public List<String> getColumnNames() {
		return m_columnNames;
	}
	
	/**
     * INTERNAL:
     */
	public void setColumnNames(List<String> columnNames) {
		m_columnNames = columnNames;
	}
}

