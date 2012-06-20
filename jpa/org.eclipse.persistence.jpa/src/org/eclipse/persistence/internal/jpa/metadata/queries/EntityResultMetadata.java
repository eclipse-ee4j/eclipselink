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

import java.util.List;
import java.util.ArrayList;

import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.jpa.metadata.ORMetadata;
import org.eclipse.persistence.internal.jpa.metadata.accessors.MetadataAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataClass;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappings;

import org.eclipse.persistence.queries.EntityResult;

/**
 * INTERNAL:
 * Object to hold onto an entity result metadata.
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
public class EntityResultMetadata extends ORMetadata {
    private MetadataClass m_entityClass; // Required in both XML and annotations.
    private List<FieldResultMetadata> m_fieldResults = new ArrayList<FieldResultMetadata>();
    private String m_discriminatorColumn;
    private String m_entityClassName;
    
    /**
     * INTERNAL:
     * Used for XML loading.
     */
    public EntityResultMetadata() {
        super("<entity-result>");
    }
    
    /**
     * INTERNAL:
     * Used for annotation loading.
     */
    public EntityResultMetadata(MetadataAnnotation entityResult, MetadataAccessor accessor) {
        super(entityResult, accessor);
        
        m_entityClass = getMetadataClass((String) entityResult.getAttribute("entityClass")); 
        m_discriminatorColumn = (String) entityResult.getAttribute("discriminatorColumn");
        
        for (Object fieldResult : (Object[]) entityResult.getAttributeArray("fields")) {
            m_fieldResults.add(new FieldResultMetadata((MetadataAnnotation) fieldResult, accessor));
        }
    }
    
    /**
     * INTERNAL:
     * Used for result class processing.
     */
    public EntityResultMetadata(MetadataClass entityClass, MetadataAccessibleObject accessibleObject) {
        m_entityClass = entityClass;
        setAccessibleObject(accessibleObject);
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public boolean equals(Object objectToCompare) {
        if (objectToCompare instanceof EntityResultMetadata) {
            EntityResultMetadata entityResult = (EntityResultMetadata) objectToCompare;
            
            if (! valuesMatch(m_entityClass, entityResult.getEntityClass())) {
                return false;
            }
            
            if (! valuesMatch(m_fieldResults, entityResult.getFieldResults())) {
                return false;
            }
            
            return valuesMatch(m_discriminatorColumn, entityResult.getDiscriminatorColumn());
        }
        
        return false;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getDiscriminatorColumn() {
        return m_discriminatorColumn;
    }
    
    /**
     * INTERNAL:
     */
    public MetadataClass getEntityClass() {
        return m_entityClass;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getEntityClassName() {
        return m_entityClassName;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public List<FieldResultMetadata> getFieldResults() {
        return m_fieldResults;
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public void initXMLObject(MetadataAccessibleObject accessibleObject, XMLEntityMappings entityMappings) {
        super.initXMLObject(accessibleObject, entityMappings);
        
        // Initialize lists of ORMetadata objects.
        initXMLObjects(m_fieldResults, accessibleObject);
        
        m_entityClass = initXMLClassName(m_entityClassName);
    }
    
    /**
     * INTERNAL:
     * Process the entity result for the given sql result set mapping.
     */
    public EntityResult process() {
        // Create a new entity result.
        EntityResult entityResult = new EntityResult(getJavaClass(getEntityClass()));
    
        // Process the field results.
        for (FieldResultMetadata fieldResult : getFieldResults()) {
            entityResult.addFieldResult(fieldResult.process());
        }
    
        // Process the discriminator value;
        if (getDiscriminatorColumn() != null) {
            DatabaseField discriminatorField = new DatabaseField();
            
            // Process the name (taking into consideration delimiters etc.)
            setFieldName(discriminatorField, getDiscriminatorColumn());
            
            // Set the discriminator column on the entity result.
            entityResult.setDiscriminatorColumn(discriminatorField);
        }
    
        // Return the entity result to the caller.
        return entityResult;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setDiscriminatorColumn(String discriminatorColumn) {
        m_discriminatorColumn = discriminatorColumn;
    }
    
    /**
     * INTERNAL:
     */
    public void setEntityClass(MetadataClass entityClass) {
        m_entityClass = entityClass;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setEntityClassName(String entityClassName) {
        m_entityClassName = entityClassName;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setFieldResults(List<FieldResultMetadata> fieldResults) {
        m_fieldResults = fieldResults; 
    }
}
