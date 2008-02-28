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
     */
    public boolean hasColumnResults() {
        return ! m_columnResults.isEmpty();
    }
    
    /**
     * INTERNAL:
     */
    public boolean hasEntityResults() {
        return ! m_entityResults.isEmpty();
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
