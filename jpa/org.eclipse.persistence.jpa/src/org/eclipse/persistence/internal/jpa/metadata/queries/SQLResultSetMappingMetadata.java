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
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.queries;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.jpa.metadata.MetadataProject;
import org.eclipse.persistence.internal.jpa.metadata.ORMetadata;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappings;
import org.eclipse.persistence.internal.sessions.AbstractSession;

import org.eclipse.persistence.queries.ColumnResult;
import org.eclipse.persistence.queries.EntityResult;
import org.eclipse.persistence.queries.FieldResult;
import org.eclipse.persistence.queries.SQLResultSetMapping;

/**
 * INTERNAL:
 * Object to hold onto an sql result mapping metadata.
 * 
 * @author Guy Pelletier
 * @since TopLink EJB 3.0 Reference Implementation
 */
public class SQLResultSetMappingMetadata extends ORMetadata {
    private List<String> m_columnResults = new ArrayList<String>();
    private List<EntityResultMetadata> m_entityResults = new ArrayList<EntityResultMetadata>();
    private String m_name;
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public SQLResultSetMappingMetadata() {
        super("<sql-result-set-mapping>");
    }

    /**
     * INTERNAL:
     */
    public SQLResultSetMappingMetadata(MetadataAnnotation sqlResultSetMapping, MetadataAccessibleObject accessibleObject) {
        super(sqlResultSetMapping, accessibleObject);
        
        m_name = (String) sqlResultSetMapping.getAttribute("name");
        
        for (Object entityResult : (Object[]) sqlResultSetMapping.getAttributeArray("entities")) {
            m_entityResults.add(new EntityResultMetadata((MetadataAnnotation)entityResult, accessibleObject));
        }
        
        for (Object columnResult : (Object[]) sqlResultSetMapping.getAttributeArray("columns")) {
            m_columnResults.add((String)((MetadataAnnotation)columnResult).getAttribute("name"));
        }
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
            
            return valuesMatch(m_entityResults, sqlResultSetMapping.getEntityResults());
        }
        
        return false;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public List<String> getColumnResults() {
        return m_columnResults;
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
        
        initXMLObjects(m_entityResults, accessibleObject);
    }
    
    /**
     * INTERNAL:
     * Process an sql result set mapping metadata into a EclipseLink 
     * SqlResultSetMapping and store it on the session.
     */
    public void process(AbstractSession session, ClassLoader loader, MetadataProject project) {        
        // Initialize a new SqlResultSetMapping (with the metadata name)
        SQLResultSetMapping mapping = new SQLResultSetMapping(getName());
        
        // Process the entity results.
        for (EntityResultMetadata eResult : m_entityResults) {
            EntityResult entityResult = new EntityResult(MetadataHelper.getClassForName(eResult.getEntityClass().getName(), loader));
        
            // Process the field results.
            if (eResult.hasFieldResults()) {
                for (FieldResultMetadata fResult : eResult.getFieldResults()) {
                    FieldResult fieldResult = new FieldResult(fResult.getName(), fResult.getColumn());
                    if (project.useDelimitedIdentifier()) {
                        fieldResult.getColumn().setUseDelimiters(true);
                    } else if (project.getShouldForceFieldNamesToUpperCase() && !fieldResult.getColumn().shouldUseDelimiters()) {
                        fieldResult.getColumn().useUpperCaseForComparisons(true);
                    }
                    entityResult.addFieldResult(fieldResult);
                }
            }
        
            // Process the discriminator value;
            if (eResult.getDiscriminatorColumn() !=null){
                DatabaseField descriminatorField = new DatabaseField(eResult.getDiscriminatorColumn());
                if (project.useDelimitedIdentifier()) {
                    descriminatorField.setUseDelimiters(true);
                } else if (project.getShouldForceFieldNamesToUpperCase() && !descriminatorField.shouldUseDelimiters()){
                    descriminatorField.useUpperCaseForComparisons(true);
                }
                entityResult.setDiscriminatorColumn(descriminatorField);
            }
        
            // Add the result to the SqlResultSetMapping.
            mapping.addResult(entityResult);
        }
        
        // Process the column results.
        for (String columnResult : m_columnResults) {
            ColumnResult result = new ColumnResult(columnResult);
            if (project.useDelimitedIdentifier()) {
                result.getColumn().setUseDelimiters(true);
            }
            if (project.getShouldForceFieldNamesToUpperCase()) {
                result.getColumn().useUpperCaseForComparisons(true);
            }
            mapping.addResult(result);
        }
            
        session.getProject().addSQLResultSetMapping(mapping);
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    protected void setColumnResults(List<String> columnResults) {            
        m_columnResults = columnResults; 
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
