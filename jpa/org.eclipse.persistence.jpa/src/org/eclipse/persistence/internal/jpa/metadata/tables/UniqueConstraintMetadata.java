/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
 *     11/06/2009-2.0 Guy Pelletier 
 *       - 286317: UniqueConstraint xml element is changing (plus couple other fixes, see bug)
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
    private String m_name;
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
        
        m_name = (String) uniqueConstraint.getAttribute("name");
        
        m_columnNames = new ArrayList<String>();
        
        for (Object columnName : (Object[]) uniqueConstraint.getAttributeArray("columnNames")) { 
            m_columnNames.add((String) columnName);
        }
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public boolean equals(Object objectToCompare) {
        if (objectToCompare instanceof UniqueConstraintMetadata) {
            UniqueConstraintMetadata uniqueConstraint = (UniqueConstraintMetadata) objectToCompare;
            
            if (! valuesMatch(m_name, uniqueConstraint.getName())) {
                return false;
            }
            
            return valuesMatch(m_columnNames, uniqueConstraint.getColumnNames());
        }
        
        return false;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public List<String> getColumnNames() {
        return m_columnNames;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getName() {
        return m_name;
    }
    
    /**
     * INTERNAL:
     * Return true if a name has been specified for this unique constraint.
     */
    public boolean hasName() {
        return m_name != null && !m_name.equals("");
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setColumnNames(List<String> columnNames) {
        m_columnNames = columnNames;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setName(String name) {
        m_name = name;
    }
}

