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
 *     05/16/2008-1.0M8 Guy Pelletier 
 *       - 218084: Implement metadata merging functionality between mapping files
 *     06/09/2009-2.0 Guy Pelletier 
 *       - 249037: JPA 2.0 persisting list item index
 *     03/24/2011-2.3 Guy Pelletier 
 *       - 337323: Multi-tenant with shared schema support (part 1)
 ******************************************************************************/ 
package org.eclipse.persistence.internal.jpa.metadata.columns;

import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.jpa.metadata.ORMetadata;
import org.eclipse.persistence.internal.jpa.metadata.accessors.MetadataAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;

/**
 * INTERNAL:
 * Object to process JPA column type into EclipseLink database fields.
 * 
 * Key notes:
 * - any metadata mapped from XML to this class must be compared in the
 *   equals method.
 * - when loading from annotations, the constructor accepts the metadata
 *   accessor this metadata was loaded from. Used it to look up any 
 *   'companion' annotation needed for processing.
 * - methods should be preserved in alphabetical order.
 * 
 * @author Guy Pelletier
 * @since EclipseLink 1.2
 */
public abstract class MetadataColumn extends ORMetadata {
    private String m_name;
    private String m_columnDefinition;
    
    /**
     * INTERNAL:
     * Used for defaulting.
     */
    protected MetadataColumn(MetadataAccessor accessor) {
        super(null, accessor);
    }
    
    /**
     * INTERNAL:
     * Used for annotation loading.
     */
    protected MetadataColumn(MetadataAnnotation column, MetadataAccessor accessor) {
        super(column, accessor);
        
        if (column != null) {
            m_name = (String) column.getAttribute("name");
            m_columnDefinition =  (String) column.getAttribute("columnDefinition");
        }
    }
    
    /**
     * INTERNAL:
     * Used for XML loading.
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
        //use the following method to manage delimited or case insensitive defaults
        setFieldName(databaseField, m_name == null ? "" : m_name);
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
