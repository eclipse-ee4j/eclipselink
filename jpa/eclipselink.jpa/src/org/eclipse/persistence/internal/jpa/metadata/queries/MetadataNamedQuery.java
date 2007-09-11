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

import javax.persistence.QueryHint;
import javax.persistence.NamedQuery;

import org.eclipse.persistence.internal.jpa.metadata.MetadataLogger;

/**
 * Object to hold onto a named query metadata.
 * 
 * @author Guy Pelletier
 * @since TopLink EJB 3.0 Reference Implementation
 */
public class MetadataNamedQuery extends MetadataQuery {
    /**
     * INTERNAL:
     */
    protected MetadataNamedQuery() {}

    /**
     * INTERNAL:
     */
    public MetadataNamedQuery(NamedQuery namedQuery, Class javaClass) {
        // Set the location where we found this query.
        setLocation(javaClass.getName());
        
        // Process the name.
        setName(namedQuery.name());
        
        // Process the query string.
        setEJBQLString(namedQuery.query());
            
        // Process the query hints.
        for (QueryHint hint : namedQuery.hints()) {
            addHint(new MetadataQueryHint(hint.name(), hint.value()));
        }     
    }
    
    /**
     * INTERNAL:
     */
    public String getIgnoreLogMessageContext() {
        return MetadataLogger.IGNORE_NAMED_QUERY_ANNOTATION;
    }
}
