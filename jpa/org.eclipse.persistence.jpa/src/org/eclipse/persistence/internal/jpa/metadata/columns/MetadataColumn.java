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
 *     06/09/2009-2.0 Guy Pelletier 
 *       - 249037: JPA 2.0 persisting list item index
 ******************************************************************************/ 
package org.eclipse.persistence.internal.jpa.metadata.columns;

import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.internal.jpa.metadata.ORMetadata;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;

/**
 * INTERNAL:
 * Object to process JPA column type into EclipseLink database fields.
 * 
 * @author Guy Pelletier
 * @since EclipseLink 1.2
 */
public abstract class MetadataColumn extends ORMetadata {
    private String m_name;
    private String m_columnDefinition;
    
    /**
     * INTERNAL:
     */
    protected MetadataColumn(MetadataAnnotation column, MetadataAccessibleObject accessibleObject) {
        super(column, accessibleObject);
        
        if (column != null) {
            m_name = (String) column.getAttribute("name");
            m_columnDefinition =  (String) column.getAttribute("columnDefinition");
        }
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    protected MetadataColumn(String xmlElement) {
        super(xmlElement);
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public boolean equals(Object objectToCompare) {
        if (objectToCompare instanceof MetadataColumn) {
            MetadataColumn column = (MetadataColumn) objectToCompare;
            
            if (! valuesMatch(m_columnDefinition, column.getColumnDefinition())) {
                return false;
            }
            
            return valuesMatch(m_name, column.getName());
        }
        
        return false;
    }
   
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getColumnDefinition() {
        return m_columnDefinition;
    }
    
    /**
     * INTERNAL:
     * Those objects that need/want to initialize more meta data should 
     * override this method.
     */
    public DatabaseField getDatabaseField() {
        // Initialize the DatabaseField with values and defaults.
        DatabaseField databaseField = new DatabaseField();
            
        databaseField.setName(m_name == null ? "" : m_name, Helper.getDefaultStartDatabaseDelimiter(), Helper.getDefaultEndDatabaseDelimiter());
        databaseField.setColumnDefinition(m_columnDefinition == null ? "" : m_columnDefinition);
        
        return databaseField;
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
    public void setColumnDefinition(String columnDefinition) {
        m_columnDefinition = columnDefinition;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setName(String name) {
        m_name = name;
    }

}
