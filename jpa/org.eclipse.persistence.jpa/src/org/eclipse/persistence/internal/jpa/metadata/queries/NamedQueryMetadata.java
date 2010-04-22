/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.jpa.JPAQuery;
import org.eclipse.persistence.internal.jpa.QueryHintsHandler;
import org.eclipse.persistence.internal.jpa.metadata.MetadataProject;
import org.eclipse.persistence.internal.jpa.metadata.ORMetadata;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;
import org.eclipse.persistence.internal.sessions.AbstractSession;

/**
 * INTERNAL:
 * Object to hold onto a named query metadata.
 * 
 * @author Guy Pelletier
 * @since TopLink EJB 3.0 Reference Implementation
 */
public class NamedQueryMetadata extends ORMetadata {
    private String m_lockMode;
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
    public NamedQueryMetadata(MetadataAnnotation namedQuery, MetadataAccessibleObject accessibleObject) {
        super(namedQuery, accessibleObject);
        
        m_name = (String) namedQuery.getAttribute("name");
        m_query = (String) namedQuery.getAttribute("query");
        m_lockMode = (String) namedQuery.getAttribute("lockMode");
        
        for (Object hint : (Object[]) namedQuery.getAttributeArray("hints")) {
            m_hints.add(new QueryHintMetadata((MetadataAnnotation)hint, accessibleObject));
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
    public String getLockMode() {
        return m_lockMode;
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
    public void process(AbstractSession session, ClassLoader loader, MetadataProject project) {
        try {
            Map<String, Object> hints = processQueryHints(session);
            session.addJPAQuery(new JPAQuery(getName(), getQuery(), getLockMode(), hints));
        } catch (Exception exception) {
            throw ValidationException.errorProcessingNamedQuery(getClass(), getName(), exception);
        }
    }
    
    /**
     * INTERNAL:
     */ 
    protected Map<String, Object> processQueryHints(AbstractSession session) {
        Map<String, Object> hints = new HashMap<String, Object>();
        
        for (QueryHintMetadata hint : this.m_hints) {
            QueryHintsHandler.verify(hint.getName(), hint.getValue(), this.m_name, session);
            Object value = hints.get(hint.getName());
            if (value != null) {
                Object[] values = null;
                if (value instanceof Object[]) {
                    List list = new ArrayList(Arrays.asList((Object[])value));
                    list.add(hint.getValue());
                    values = list.toArray();                
                } else {
                    values = new Object[2];
                    values[0] = value;
                    values[1] = hint.getValue();
                }
                hints.put(hint.getName(), values);
            } else {
                hints.put(hint.getName(), hint.getValue());
            }
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
    public void setLockMode(String lockMode) {
        m_lockMode = lockMode;
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
