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

import javax.persistence.FieldResult;
import javax.persistence.EntityResult;

/**
 * Object to hold onto an entity result metadata.
 * 
 * @author Guy Pelletier
 * @since TopLink EJB 3.0 Reference Implementation
 */
public class MetadataEntityResult {
    protected EntityResult m_entityResult;
    protected List<MetadataFieldResult> m_fieldResults;
    
    /**
     * INTERNAL:
     */
    protected MetadataEntityResult() {}

    /**
     * INTERNAL:
     */
    public MetadataEntityResult(EntityResult entityResult) {
        m_entityResult = entityResult;
    }
    
    /**
     * INTERNAL:
     */
    public String getDiscriminatorColumn() {
        return m_entityResult.discriminatorColumn();
    }
    
    /**
     * INTERNAL:
     */
    public Class getEntityClass() {
        return m_entityResult.entityClass();
    }
    
    /**
     * INTERNAL:
     */
    public List<MetadataFieldResult> getFieldResults() {
        if (m_fieldResults == null) {
            m_fieldResults = new ArrayList<MetadataFieldResult>();
            
            for (FieldResult fieldResult : m_entityResult.fields()) {
                m_fieldResults.add(new MetadataFieldResult(fieldResult));
            } 
        }
        
        return m_fieldResults;
    }
}
