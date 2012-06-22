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
 *       - 218084: Implement metadata merging functionality between mapping file
 *     02/06/2009-2.0 Guy Pelletier 
 *       - 248293: JPA 2.0 Element Collections (part 2)
 *     03/24/2011-2.3 Guy Pelletier 
 *       - 337323: Multi-tenant with shared schema support (part 1)
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.tables;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.internal.jpa.metadata.MetadataLogger;
import org.eclipse.persistence.internal.jpa.metadata.accessors.MetadataAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;
import org.eclipse.persistence.internal.jpa.metadata.columns.JoinColumnMetadata;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappings;

/**
 * INTERNAL:
 * Object to hold onto table metadata in a TopLink database table.
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
 * @since TopLink EJB 3.0 Reference Implementation
 */
public class JoinTableMetadata extends TableMetadata {
    private List<JoinColumnMetadata> m_joinColumns = new ArrayList<JoinColumnMetadata>();
    private List<JoinColumnMetadata> m_inverseJoinColumns = new ArrayList<JoinColumnMetadata>();
    
    /**
     * INTERNAL:
     * Used for XML loading.
     */
    public JoinTableMetadata() {
        super("<join-table>");
    }
    
    /**
     * INTERNAL:
     * Used for defaulting.
     */
    public JoinTableMetadata(MetadataAccessor accessor) {
        super(null, accessor);
    }
    
    /**
     * INTERNAL:
     * Used for annotation loading.
     */
    public JoinTableMetadata(MetadataAnnotation joinTable, MetadataAccessor accessor) {
        super(joinTable, accessor);
        
        if (joinTable != null) {
            for (Object joinColumn : (Object[]) joinTable.getAttributeArray("joinColumns")) {
                m_joinColumns.add(new JoinColumnMetadata((MetadataAnnotation)joinColumn, accessor));
            }  
        
            for (Object inverseJoinColumn : (Object[]) joinTable.getAttributeArray("inverseJoinColumns")) {
                m_inverseJoinColumns.add(new JoinColumnMetadata((MetadataAnnotation)inverseJoinColumn, accessor));
            }
        }
    }

    /**
     * INTERNAL:
     * For merging and overriding to work properly, all ORMetadata must be able 
     * to compare themselves for metadata equality.
     */
    @Override
    public boolean equals(Object objectToCompare) {
        if (super.equals(objectToCompare) && objectToCompare instanceof JoinTableMetadata) {
            JoinTableMetadata joinTable = (JoinTableMetadata) objectToCompare;
            
            if (! valuesMatch(m_joinColumns, joinTable.getJoinColumns())) {
                return false;
            }
            
            return valuesMatch(m_inverseJoinColumns, joinTable.getInverseJoinColumns());
        }
        
        return false;
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public String getCatalogContext() {
        return MetadataLogger.JOIN_TABLE_CATALOG;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public List<JoinColumnMetadata> getInverseJoinColumns() {
        return m_inverseJoinColumns;
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
     */
    @Override
    public String getNameContext() {
        return MetadataLogger.JOIN_TABLE_NAME;
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public String getSchemaContext() {
        return MetadataLogger.JOIN_TABLE_SCHEMA;
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public void initXMLObject(MetadataAccessibleObject accessibleObject, XMLEntityMappings entityMappings) {
        super.initXMLObject(accessibleObject, entityMappings);

        for (JoinColumnMetadata jcm : m_inverseJoinColumns){
            // Initialize single objects.
            initXMLObject(jcm, accessibleObject);
        }

        for (JoinColumnMetadata jcm : m_joinColumns){
            // Initialize single objects.
            initXMLObject(jcm, accessibleObject);
        }
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setInverseJoinColumns(List<JoinColumnMetadata> inverseJoinColumns) {
        m_inverseJoinColumns = inverseJoinColumns;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setJoinColumns(List<JoinColumnMetadata> joinColumns) {
        m_joinColumns = joinColumns;
    }
}
