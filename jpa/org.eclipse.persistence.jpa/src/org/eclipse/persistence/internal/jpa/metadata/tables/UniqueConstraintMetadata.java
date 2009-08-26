/*******************************************************************************
 * Copyright (c) 1998, 2009 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 *     05/16/2008-1.0M8 Guy Pelletier 
 *       - 218084: Implement metadata merging functionality between mapping file
 *******************************************************************************/
package org.eclipse.persistence.internal.jpa.metadata.tables;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.internal.jpa.metadata.ORMetadata;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;

/**
 * INTERNAL:
 * Object to hold onto a unique constraint metadata.
 */
public class UniqueConstraintMetadata extends ORMetadata {
    private List<String> m_columnNames;
    
    /**
     * INTERNAL:
     */
    public UniqueConstraintMetadata() {
        super("<unique-constraint>");
    }
    
    /**
     * INTERNAL:
     */
    public UniqueConstraintMetadata(MetadataAnnotation uniqueConstraint, MetadataAccessibleObject accessibleObject) {
        super(uniqueConstraint, accessibleObject);
        
        m_columnNames = new ArrayList<String>();
        
        for (Object columnName : (Object[]) uniqueConstraint.getAttributeArray("columnNames")) { 
            m_columnNames.add((String)columnName);
        }
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public boolean equals(Object objectToCompare) {
        if (objectToCompare instanceof UniqueConstraintMetadata) {
            return valuesMatch(m_columnNames, ((UniqueConstraintMetadata) objectToCompare).getColumnNames());
        }
        
        return false;
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

