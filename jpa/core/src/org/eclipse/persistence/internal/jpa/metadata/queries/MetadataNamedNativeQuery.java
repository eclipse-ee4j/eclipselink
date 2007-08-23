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

import javax.persistence.QueryHint;
import javax.persistence.NamedNativeQuery;

import org.eclipse.persistence.internal.jpa.metadata.MetadataLogger;

/**
 * Object to hold onto named native query metadata.
 * 
 * @author Guy Pelletier
 * @since TopLink EJB 3.0 Reference Implementation
 */
public class MetadataNamedNativeQuery extends MetadataQuery {
    private Class m_resultClass;
    private String m_resultSetMapping;
    
    /**
     * INTERNAL:
     */
    protected MetadataNamedNativeQuery() {}
    
    /**
     * INTERNAL:
     */
    public MetadataNamedNativeQuery(NamedNativeQuery namedNativeQuery, Class javaClass) {
        // Set the location where we found this query.
        setLocation(javaClass.getName());
        
        // Process the name
        setName(namedNativeQuery.name());
        
        // Process the query string.
        setEJBQLString(namedNativeQuery.query());
        
        // Process the query hints.
        for (QueryHint hint : namedNativeQuery.hints()) {
            addHint(new MetadataQueryHint(hint.name(), hint.value()));
        }   
        
        // Process the result class.
        setResultClass(namedNativeQuery.resultClass());
        
        // Process the result set mapping.
        setResultSetMapping(namedNativeQuery.resultSetMapping());
    }
    
    /**
     * INTERNAL:
     */
    public String getIgnoreLogMessageContext() {
        return MetadataLogger.IGNORE_NAMED_NATIVE_QUERY_ANNOTATION;
    }
    
    /**
     * INTERNAL:
     */
    public Class getResultClass() {
        return m_resultClass;
    }
    
    /**
     * INTERNAL:
     */
    public String getResultSetMapping() {
        return m_resultSetMapping;
    }
    
    /**
     * INTERNAL:
     */
    protected void setResultClass(Class resultClass) {
        m_resultClass = resultClass;
    }
    
    /**
     * INTERNAL:
     */
    protected void setResultSetMapping(String resultSetMapping) {
        m_resultSetMapping = resultSetMapping;
    }
}
