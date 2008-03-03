/*******************************************************************************
 * Copyright (c) 1998, 2008 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.queries;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.internal.jpa.metadata.MetadataProject;

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
public class SQLResultSetMappingMetadata {
	private List<String> m_columnResults;
    private List<EntityResultMetadata> m_entityResults;
    private String m_name;
    
    /**
     * INTERNAL:
     */
    public SQLResultSetMappingMetadata() {}

    /**
     * INTERNAL:
     */
    public SQLResultSetMappingMetadata(Annotation sqlResultSetMapping) {
        setName((String) MetadataHelper.invokeMethod("name", sqlResultSetMapping));
        setEntityResults((Annotation[]) MetadataHelper.invokeMethod("entities", sqlResultSetMapping));
        setColumnResults((Annotation[]) MetadataHelper.invokeMethod("columns", sqlResultSetMapping));
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
     * Used for OX mapping.
     */
    public String getName() {
        return m_name;
    }
    
    /**
     * INTERNAL:
     * Process an sql result set mapping metadata into a EclipseLink 
     * SqlResultSetMapping and store it on the session.
     */
    public void process(MetadataProject project) {        
        // Initialize a new SqlResultSetMapping (with the metadata name)
        SQLResultSetMapping mapping = new SQLResultSetMapping(getName());
        
        // Process the entity results.
        for (EntityResultMetadata eResult : m_entityResults) {
            EntityResult entityResult = new EntityResult(eResult.getEntityClass().getName());
        
            // Process the field results.
            if (eResult.hasFieldResults()) {
                for (FieldResultMetadata fResult : eResult.getFieldResults()) {
                    entityResult.addFieldResult(new FieldResult(fResult.getName(), fResult.getColumn()));
                }
            }
        
            // Process the discriminator value;
            entityResult.setDiscriminatorColumn(eResult.getDiscriminatorColumn());
        
            // Add the result to the SqlResultSetMapping.
            mapping.addResult(entityResult);
        }
        
        // Process the column results.
        for (String columnResult : m_columnResults) {
            mapping.addResult(new ColumnResult(columnResult));
        }
            
        project.getSession().getProject().addSQLResultSetMapping(mapping);
    }
    
    /**
     * INTERNAL:
     * Called from annotation population.
     */
    protected void setColumnResults(Object[] columnResults) {
        m_columnResults = new ArrayList<String>();
        
        for (Object columnResult : columnResults) {
            m_columnResults.add((String)MetadataHelper.invokeMethod("name", columnResult));
        }
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
     * Called from annotation population.
     */
    public void setEntityResults(Annotation[] entityResults) {
        m_entityResults = new ArrayList<EntityResultMetadata>();
        
        for (Annotation entityResult : entityResults) {
            m_entityResults.add(new EntityResultMetadata(entityResult));
        }
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
