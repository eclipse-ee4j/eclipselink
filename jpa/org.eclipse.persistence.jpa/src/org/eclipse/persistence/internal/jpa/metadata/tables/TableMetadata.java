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
 *     11/06/2009-2.0 Guy Pelletier 
 *       - 286317: UniqueConstraint xml element is changing (plus couple other fixes, see bug)
 *     03/24/2011-2.3 Guy Pelletier 
 *       - 337323: Multi-tenant with shared schema support (part 1)
 *     03/24/2011-2.3 Guy Pelletier 
 *       - 337323: Multi-tenant with shared schema support (part 8)
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.tables;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.jpa.metadata.MetadataLogger;
import org.eclipse.persistence.internal.jpa.metadata.ORMetadata;
import org.eclipse.persistence.internal.jpa.metadata.accessors.MetadataAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappings;
import org.eclipse.persistence.internal.helper.DatabaseTable;
import org.eclipse.persistence.internal.helper.Helper;

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
public class TableMetadata extends ORMetadata {
    private DatabaseTable m_databaseTable = new DatabaseTable();
    private List<UniqueConstraintMetadata> m_uniqueConstraints = new ArrayList<UniqueConstraintMetadata>();
    
    private String m_name;
    private String m_schema;
    private String m_catalog;
    private String m_creationSuffix;

    /**
     * INTERNAL:
     * Used for XML loading.
     */
    public TableMetadata() {
        super("<table>");
    }

    /**
     * INTERNAL:
     * Used for annotatation loading.
     */
    public TableMetadata(MetadataAnnotation table, MetadataAccessor accessor) {
        super(table, accessor);
        
        if (table != null) {
            m_name = (String) table.getAttribute("name"); 
            m_schema = (String) table.getAttribute("schema"); 
            m_catalog = (String) table.getAttribute("catalog");

            for (Object uniqueConstraint : (Object[]) table.getAttributeArray("uniqueConstraints")) {
                m_uniqueConstraints.add(new UniqueConstraintMetadata((MetadataAnnotation) uniqueConstraint, accessor));
            }
        }
    }
    
    /**
     * INTERNAL:
     * Used for XML loading.
     */
    protected TableMetadata(String xmlElement) {
        super(xmlElement);
    }

    /**
     * INTERNAL:
     */
    @Override
    public boolean equals(Object objectToCompare) {
        if (objectToCompare instanceof TableMetadata) {
            TableMetadata table = (TableMetadata) objectToCompare;
            
            if (! valuesMatch(m_name, table.getName())) {
                return false;
            }

            if (! valuesMatch(m_schema, table.getSchema())) {
                return false;
            }

            if (! valuesMatch(m_catalog, table.getCatalog())) {
                return false;
            }

            if (! valuesMatch(m_creationSuffix, table.getCreationSuffix())) {
                return false;
            }

            return valuesMatch(m_uniqueConstraints, table.getUniqueConstraints());
        }
        
        return false;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getCatalog() {
        return m_catalog;
    }

    /**
     * INTERNAL:
     */
    public String getCatalogContext() {
        return MetadataLogger.TABLE_CATALOG;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getCreationSuffix() {
        return m_creationSuffix;
    }

    /**
     * INTERNAL:
     */
    public DatabaseTable getDatabaseTable() {
        return m_databaseTable;
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
     */
    public String getNameContext() {
        return MetadataLogger.TABLE_NAME;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getSchema() {
        return m_schema;
    }

    /**
     * INTERNAL:
     */
    public String getSchemaContext() {
        return MetadataLogger.TABLE_SCHEMA;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public List<UniqueConstraintMetadata> getUniqueConstraints() {
        return m_uniqueConstraints;
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public void initXMLObject(MetadataAccessibleObject accessibleObject, XMLEntityMappings entityMappings) {
        super.initXMLObject(accessibleObject, entityMappings);

        for (UniqueConstraintMetadata jcm : m_uniqueConstraints){
            // Initialize single objects.
            initXMLObject(jcm, accessibleObject);
        }
    }

    /**
     * INTERNAL:
     * Add the unique constraints to the database table.
     */
    public void processUniqueConstraints() {
        if (m_uniqueConstraints != null) {
            for (UniqueConstraintMetadata uniqueConstraint : m_uniqueConstraints) {
                if (uniqueConstraint.hasName() && m_databaseTable.getUniqueConstraints().containsKey(uniqueConstraint.getName())) {
                    throw ValidationException.multipleUniqueConstraintsWithSameNameSpecified(uniqueConstraint.getName(), getName(), getLocation());
                } else {
                    m_databaseTable.addUniqueConstraints(uniqueConstraint.getName(), uniqueConstraint.getColumnNames());
                }
            }
        }
    }

    /**
     * INTERNAL:
     */
    public void setFullyQualifiedTableName(String fullyQualifiedTableName) {
        m_databaseTable.setPossiblyQualifiedName(fullyQualifiedTableName, Helper.getDefaultStartDatabaseDelimiter(), Helper.getDefaultEndDatabaseDelimiter());  
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setCatalog(String catalog) {
        m_catalog = catalog;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setCreationSuffix(String creationSuffix) {
        m_creationSuffix = creationSuffix;
    }

    /**
     * INTERNAL:
     */
    public void setDatabaseTable(DatabaseTable databaseTable) {
        m_databaseTable = databaseTable;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setName(String name) {
        m_name = name;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setSchema(String schema) {
        m_schema = schema;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setUniqueConstraints(List<UniqueConstraintMetadata> uniqueConstraints) {
        m_uniqueConstraints = uniqueConstraints;
    }

    /**
     * INTERNAL:
     */
    public void setUseDelimiters(boolean useDelimiters){
        m_databaseTable.setUseDelimiters(useDelimiters);
    }
}
