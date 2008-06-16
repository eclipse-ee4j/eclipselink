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
 *     05/16/2008-1.0M8 Guy Pelletier 
 *       - 218084: Implement metadata merging functionality between mapping files
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.queries;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.jpa.QueryHintsHandler;
import org.eclipse.persistence.internal.jpa.metadata.ORMetadata;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.queries.JPAQuery;

/**
 * INTERNAL:
 * Object to hold onto a named query metadata.
 * 
 * @author Guy Pelletier
 * @since TopLink EJB 3.0 Reference Implementation
 */
public class NamedQueryMetadata extends ORMetadata {
    private List<QueryHintMetadata> m_hints = new ArrayList<QueryHintMetadata>();
    private String m_name;
    private String m_query;
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public NamedQueryMetadata() {
        super("<named-query>");
    }

    /**
     * INTERNAL:
     */
    protected NamedQueryMetadata(String xmlElement) {
        super(xmlElement);
    }
    
    /**
     * INTERNAL:
     */
    public NamedQueryMetadata(Annotation namedQuery, MetadataAccessibleObject accessibleObject) {
        super(namedQuery, accessibleObject);
        
        m_name = (String) MetadataHelper.invokeMethod("name", namedQuery);
        m_query = (String) MetadataHelper.invokeMethod("query", namedQuery);

        for (Annotation hint : (Annotation[]) MetadataHelper.invokeMethod("hints", namedQuery)) {
            m_hints.add(new QueryHintMetadata(hint, accessibleObject));
        }
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public boolean equals(Object objectToCompare) {
        if (objectToCompare instanceof NamedQueryMetadata) {
            NamedQueryMetadata query = (NamedQueryMetadata) objectToCompare;
            
            if (! valuesMatch(m_name, query.getName())) {
                return false;
            }
            
            if (! valuesMatch(m_query, query.getQuery())) {
                return false;
            }
            
            return valuesMatch(m_hints, query.getHints());
        }
        
        return false;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public List<QueryHintMetadata> getHints() {
        return m_hints; 
    }
    
    /**
     * INTERNAL:
     * To satisfy the abstract getIdentifier() method from ORMetadata.
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
     * Used for OX mapping.
     */
    public String getQuery() {
        return m_query;
    }
    
    /**
     * INTERNAL:
     */
    public void process(AbstractSession session, ClassLoader loader) {
        try {
            HashMap<String, String> hints = processQueryHints(session);
            session.addJPAQuery(new JPAQuery(getName(), getQuery(), hints));
        } catch (Exception exception) {
            throw ValidationException.errorProcessingNamedQuery(getClass(), getName(), exception);
        }
    }
    
    /**
     * INTERNAL:
     */ 
    protected HashMap<String, String> processQueryHints(AbstractSession session) {
        HashMap<String, String> hints = new HashMap<String, String>();
        
        for (QueryHintMetadata hint : m_hints) {
            QueryHintsHandler.verify(hint.getName(), hint.getValue(), m_name, session);
            hints.put(hint.getName(), hint.getValue());
        }
        
        return hints;
    } 
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setHints(List<QueryHintMetadata> hints) {
        m_hints = hints;
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
    public void setQuery(String query) {
        m_query = query;
    }
}
