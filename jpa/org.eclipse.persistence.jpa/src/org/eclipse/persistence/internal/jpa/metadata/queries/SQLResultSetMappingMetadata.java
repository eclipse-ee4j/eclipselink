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
 *     03/24/2011-2.3 Guy Pelletier 
 *       - 337323: Multi-tenant with shared schema support (part 1)
 *     02/08/2012-2.4 Guy Pelletier 
 *       - 350487: JPA 2.1 Specification defined support for Stored Procedure Calls
 *     06/20/2012-2.5 Guy Pelletier 
 *       - 350487: JPA 2.1 Specification defined support for Stored Procedure Calls
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.queries;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.internal.jpa.metadata.MetadataProject;
import org.eclipse.persistence.internal.jpa.metadata.ORMetadata;
import org.eclipse.persistence.internal.jpa.metadata.accessors.MetadataAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataClass;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappings;

import org.eclipse.persistence.queries.SQLResultSetMapping;

/**
 * INTERNAL:
 * Object to hold onto an sql result mapping metadata.
 * 
 * Key notes:
 * - any metadata mapped from XML to this class must be compared in the
 *   equals method.
 * - all metadata mapped from XML must be initialized in the initXMLObject 
 *   method.
 * - when loading from annotations, the constructor accepts the metadata
 *   accessor this metadata was loaded from. Used it to look up any 
 *   'companion' annotation needed for processing.
 * - methods should be preserved in alphabetical order.
 * 
 * @author Guy Pelletier
 * @since TopLink EJB 3.0 Reference Implementation
 */
public class SQLResultSetMappingMetadata extends ORMetadata {
    private List<ColumnResultMetadata> m_columnResults = new ArrayList<ColumnResultMetadata>();
    private List<ConstructorResultMetadata> m_constructorResults = new ArrayList<ConstructorResultMetadata>();
    private List<EntityResultMetadata> m_entityResults = new ArrayList<EntityResultMetadata>();
    private String m_name;
    
    /**
     * INTERNAL:
     * Used for XML loading.
     */
    public SQLResultSetMappingMetadata() {
        super("<sql-result-set-mapping>");
    }

    /**
     * INTERNAL:
     * Used for annotation loading.
     */
    public SQLResultSetMappingMetadata(MetadataAnnotation sqlResultSetMapping, MetadataAccessor accessor) {
        super(sqlResultSetMapping, accessor);
        
        m_name = (String) sqlResultSetMapping.getAttribute("name");
        
        for (Object entityResult : (Object[]) sqlResultSetMapping.getAttributeArray("entities")) {
            m_entityResults.add(new EntityResultMetadata((MetadataAnnotation) entityResult, accessor));
        }
        
        for (Object constructorResult : (Object[]) sqlResultSetMapping.getAttributeArray("classes")) {
            m_constructorResults.add(new ConstructorResultMetadata((MetadataAnnotation) constructorResult, accessor));
        }
        
        for (Object columnResult : (Object[]) sqlResultSetMapping.getAttributeArray("columns")) {
            m_columnResults.add(new ColumnResultMetadata((MetadataAnnotation) columnResult, accessor));
        }
    }
    
    /**
     * INTERNAL:
     * Used for result class processing.
     */
    public SQLResultSetMappingMetadata(MetadataClass entityClass, MetadataAccessibleObject accessibleObject, MetadataProject project, Object location) {
        super(accessibleObject, project, location);
        
        // Since a result set mapping requires a name, set it to the entity 
        // class name.
        m_name = entityClass.getName();
        m_entityResults.add(new EntityResultMetadata(entityClass, accessibleObject));
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public boolean equals(Object objectToCompare) {
        if (objectToCompare instanceof SQLResultSetMappingMetadata) {
            SQLResultSetMappingMetadata sqlResultSetMapping = (SQLResultSetMappingMetadata) objectToCompare;
            
            if (! valuesMatch(m_name, sqlResultSetMapping.getName())) {
                return false;
            }
            
            if (! valuesMatch(m_columnResults, sqlResultSetMapping.getColumnResults())) {
                return false;
            }
            
            if (! valuesMatch(m_constructorResults, sqlResultSetMapping.getConstructorResults())) {
                return false;
            }
            
            return valuesMatch(m_entityResults, sqlResultSetMapping.getEntityResults());
        }
        
        return false;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public List<ColumnResultMetadata> getColumnResults() {
        return m_columnResults;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public List<ConstructorResultMetadata> getConstructorResults() {
        return m_constructorResults;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public List<EntityResultMetadata> getEntityResults() {
        return m_entityResults;
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public String getIdentifier() {
        return m_name;
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
    @Override
    public void initXMLObject(MetadataAccessibleObject accessibleObject, XMLEntityMappings entityMappings) {
        super.initXMLObject(accessibleObject, entityMappings);
        
        // Initialize lists of objects.
        initXMLObjects(m_entityResults, accessibleObject);
        initXMLObjects(m_constructorResults, accessibleObject);
        initXMLObjects(m_columnResults, accessibleObject);
    }
    
    /**
     * INTERNAL:
     * Process an sql result set mapping metadata into a EclipseLink 
     * SqlResultSetMapping and store it on the session. The order of processing
     * the results and adding them is important to be spec compliant.
     */
    public SQLResultSetMapping process() {        
        // Initialize a new SqlResultSetMapping (with the metadata name)
        SQLResultSetMapping mapping = new SQLResultSetMapping(getName());
        
        // Process the entity results first.
        for (EntityResultMetadata entityResult : m_entityResults) {
            mapping.addResult(entityResult.process());
        }
            
        // Process the constructor results second.
        for (ConstructorResultMetadata constructorResult : m_constructorResults) {
            mapping.addResult(constructorResult.process());
        }
        
        // Process the column results third.
        for (ColumnResultMetadata columnResult : m_columnResults) {
            mapping.addResult(columnResult.process());
        }
        
        return mapping;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setColumnResults(List<ColumnResultMetadata> columnResults) {            
        m_columnResults = columnResults; 
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setConstructorResults(List<ConstructorResultMetadata> constructorResults) {
        m_constructorResults = constructorResults;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setEntityResults(List<EntityResultMetadata> entityResults) {
        m_entityResults = entityResults;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setName(String name) {
        m_name = name;
    }
}
