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
 *     05/16/2008-1.0M8 Guy Pelletier 
 *       - 218084: Implement metadata merging functionality between mapping files
 *     04/27/2010-2.1 Guy Pelletier 
 *       - 309856: MappedSuperclasses from XML are not being initialized properly
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.queries;

import org.eclipse.persistence.internal.jpa.metadata.ORMetadata;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;

/**
 * INTERNAL:
 * Object to hold onto an field result metadata.
 * 
 * @author Guy Pelletier
 * @since TopLink EJB 3.0 Reference Implementation
 */
public class FieldResultMetadata extends ORMetadata {
    // Note: Any metadata mapped from XML to this class must be compared in the equals method.

    // Both the name and column are required in XML and annotations.
    private String m_name;
    private String m_column;
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public FieldResultMetadata() {
        super("<field-result>");
    }

    /**
     * INTERNAL:
     */
    public FieldResultMetadata(MetadataAnnotation fieldResult, MetadataAccessibleObject accessibleObject) {
        super(fieldResult, accessibleObject);
        
        m_name = (String) fieldResult.getAttribute("name");
        m_column = (String) fieldResult.getAttribute("column");
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public boolean equals(Object objectToCompare) {
        if (objectToCompare instanceof FieldResultMetadata) {
            FieldResultMetadata fieldResult = (FieldResultMetadata) objectToCompare;
            
            if (! valuesMatch(m_name, fieldResult.getName())) {
                return false;
            }

            return valuesMatch(m_column, fieldResult.getColumn());
        }
        
        return false;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getColumn() {
        return m_column;
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
     * Used for OX mapping.
     */
    public void setColumn(String column) {
        m_column = column;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setName(String name) {
        m_name = name;
    }
}
