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
 *     03/27/2009-2.0 Guy Pelletier 
 *       - 241413: JPA 2.0 Add EclipseLink support for Map type attributes
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.queries;

import java.util.Map;

import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.jpa.EJBQueryImpl;
import org.eclipse.persistence.internal.jpa.metadata.MetadataProject;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataClass;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappings;
import org.eclipse.persistence.internal.sessions.AbstractSession;

/**
 * INTERNAL:
 * Object to hold onto named native query metadata.
 * 
 * @author Guy Pelletier
 * @since TopLink EJB 3.0 Reference Implementation
 */
public class NamedNativeQueryMetadata extends NamedQueryMetadata {
    private MetadataClass m_resultClass;
    private String m_resultClassName;
    private String m_resultSetMapping;
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public NamedNativeQueryMetadata() {
        super("<named-native-query>");
    }
    
    /**
     * INTERNAL:
     */
    protected NamedNativeQueryMetadata(String javaClassName) {
        super(javaClassName);
    }
    
    /**
     * INTERNAL:
     */
    public NamedNativeQueryMetadata(MetadataAnnotation namedNativeQuery, MetadataAccessibleObject accessibleObject) {
        super(namedNativeQuery, accessibleObject);
        
        m_resultClass = getMetadataClass((String) namedNativeQuery.getAttributeString("resultClass")); 
        m_resultSetMapping = (String) namedNativeQuery.getAttributeString("resultSetMapping");
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public boolean equals(Object objectToCompare) {
        if (super.equals(objectToCompare) && objectToCompare instanceof NamedNativeQueryMetadata) {
            NamedNativeQueryMetadata query = (NamedNativeQueryMetadata) objectToCompare;
            
            if (! valuesMatch(m_resultClass, query.getResultClass())) {
                return false;
            }
            
            return valuesMatch(m_resultSetMapping, query.getResultSetMapping());
        }
        
        return false;
    }
    
    /**
     * INTERNAL:
     */
    public MetadataClass getResultClass() {
        return m_resultClass;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getResultClassName() {
        return m_resultClassName;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getResultSetMapping() {
        return m_resultSetMapping;
    }
    
    /**
     * INTERNAL:
     * Return true is a result set mapping has been specified.
     */
    protected boolean hasResultSetMapping(AbstractSession session) {
        if (m_resultSetMapping != null && ! m_resultSetMapping.equals("")) {
            // User has specified a result set mapping. Since all the result
            // set mappings are processed and placed on the session before named
            // queries, let's validate that the sql result set mapping specified
            // on this query actually exists.
            if (session.getProject().hasSQLResultSetMapping(m_resultSetMapping)) {
                return true;
            } else {
                throw ValidationException.invalidSQLResultSetMapping(m_resultSetMapping, getName(), getLocation());
            }
        }
        
        return false;
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public void initXMLObject(MetadataAccessibleObject accessibleObject, XMLEntityMappings entityMappings) {
        super.initXMLObject(accessibleObject, entityMappings);
        
        m_resultClass = initXMLClassName(m_resultClassName);
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public void process(AbstractSession session, ClassLoader loader, MetadataProject project) {
        Map<String, Object> hints = processQueryHints(session);

        if (m_resultClass.isVoid()) {
            if (hasResultSetMapping(session)) {
                session.addQuery(getName(), EJBQueryImpl.buildSQLDatabaseQuery(m_resultSetMapping, getQuery(), hints, loader, session));
            } else {
                // Neither a resultClass or resultSetMapping is specified so place in a temp query on the session
                session.addQuery(getName(), EJBQueryImpl.buildSQLDatabaseQuery(getQuery(), hints, loader, session));  
            }
        } else { 
            session.addQuery(getName(), EJBQueryImpl.buildSQLDatabaseQuery(MetadataHelper.getClassForName(m_resultClass.getName(), loader), getQuery(), hints, loader, session));
        }
    }
    
    /**
     * INTERNAL:
     */
    public void setResultClass(MetadataClass resultClass) {
        m_resultClass = resultClass;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setResultClassName(String resultClassName) {
        m_resultClassName = resultClassName;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    protected void setResultSetMapping(String resultSetMapping) {
        m_resultSetMapping = resultSetMapping;
    }
}
