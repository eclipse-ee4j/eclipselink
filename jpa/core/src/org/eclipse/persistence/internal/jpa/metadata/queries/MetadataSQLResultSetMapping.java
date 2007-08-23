/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.queries;

import java.util.List;
import java.util.ArrayList;

import javax.persistence.ColumnResult;
import javax.persistence.EntityResult;
import javax.persistence.SqlResultSetMapping;

/**
 * Object to hold onto an sql result mapping metadata.
 * 
 * @author Guy Pelletier
 * @since TopLink EJB 3.0 Reference Implementation
 */
public class MetadataSQLResultSetMapping {
    protected List<String> m_columnResults;
    protected List<MetadataEntityResult> m_entityResults;
    protected SqlResultSetMapping m_sqlResultSetMapping;
    
    /**
     * INTERNAL:
     */
    protected MetadataSQLResultSetMapping() {}

    /**
     * INTERNAL:
     */
    public MetadataSQLResultSetMapping(SqlResultSetMapping sqlResultSetMapping) {
        m_sqlResultSetMapping = sqlResultSetMapping;
    }
    
    /**
     * INTERNAL:
     */
    public List<MetadataEntityResult> getEntityResults() {
        if (m_entityResults == null) {
            m_entityResults = new ArrayList<MetadataEntityResult>();
            
            for (EntityResult entityResult : m_sqlResultSetMapping.entities()) {
                m_entityResults.add(new MetadataEntityResult(entityResult));
            } 
        }
        
        return m_entityResults;
    }
    
    /**
     * INTERNAL:
     */
    public List<String> getColumnResults() {
        if (m_columnResults == null) {
            m_columnResults = new ArrayList<String>();
            
            for (ColumnResult columnResult : m_sqlResultSetMapping.columns()) {
                m_columnResults.add(columnResult.name());
            } 
        }
        
        return m_columnResults;
    }
    
    /**
     * INTERNAL:
     */
    public String getName() {
        return m_sqlResultSetMapping.name();
    }
}
