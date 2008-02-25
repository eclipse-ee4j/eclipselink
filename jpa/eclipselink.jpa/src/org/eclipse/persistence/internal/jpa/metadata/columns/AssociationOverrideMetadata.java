/*******************************************************************************
 * Copyright (c) 1998, 2008 Oracle. All rights reserved.
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
package org.eclipse.persistence.internal.jpa.metadata.columns;

import java.util.ArrayList;
import java.util.List;

/**
 * Object to hold onto an association override meta data.
 * 
 * @author Guy Pelletier
 * @since EclipseLink 1.0
 */
public class AssociationOverrideMetadata extends OverrideMetadata {
	private List<JoinColumnMetadata> m_joinColumns;
	
	/**
	 * INTERNAL:
	 * Assumed to be used solely for OX loading.
	 */
	public AssociationOverrideMetadata() {}
	
	/**
	 * INTERNAL:
	 */
	public AssociationOverrideMetadata(Object associationOverride, String className) {
		super(className);

		setName((String)MetadataHelper.invokeMethod("name", associationOverride, (Object[])null));
		
		m_joinColumns = new ArrayList<JoinColumnMetadata>();
		for (Object joinColumn : (Object[])MetadataHelper.invokeMethod("joinColumns", associationOverride, (Object[])null)) {
			m_joinColumns.add(new JoinColumnMetadata(joinColumn));
		}
	}
	
	/**
	 * INTERNAL:
	 * Used for OX mapping.
	 */
	public List<JoinColumnMetadata> getJoinColumns() {
		return m_joinColumns;
	}
	
	/**
	 * INTERNAL:
	 * Used for OX mapping.
	 */
	public void setJoinColumns(List<JoinColumnMetadata> joinColumns) {
		m_joinColumns = joinColumns;
	}
}
