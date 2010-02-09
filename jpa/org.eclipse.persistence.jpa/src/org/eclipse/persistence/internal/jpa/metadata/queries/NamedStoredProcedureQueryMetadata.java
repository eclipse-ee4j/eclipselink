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
import java.util.List;
import java.util.Map;

import org.eclipse.persistence.internal.jpa.EJBQueryImpl;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappings;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.queries.StoredProcedureCall;

/**
 * INTERNAL:
 * Object to hold onto a named stored procedure query.
 * 
 * @author Guy Pelletier
 * @since TopLink 11g
 */
public class NamedStoredProcedureQueryMetadata extends NamedNativeQueryMetadata {
    private Boolean m_returnsResultSet;
    private List<StoredProcedureParameterMetadata> m_parameters = new ArrayList<StoredProcedureParameterMetadata>();
    private String m_procedureName;
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public NamedStoredProcedureQueryMetadata() {
        super("<named-stored-procedure_query>");
    }
    
    /**
     * INTERNAL:
     */
    public NamedStoredProcedureQueryMetadata(MetadataAnnotation namedStoredProcedureQuery, MetadataAccessibleObject accessibleObject) {
        super(namedStoredProcedureQuery, accessibleObject);
         
        for (Object storedProcedureParameter : (Object[]) namedStoredProcedureQuery.getAttributeArray("parameters")) {
           m_parameters.add(new StoredProcedureParameterMetadata((MetadataAnnotation)storedProcedureParameter, accessibleObject));
        }
        
        m_procedureName = (String) namedStoredProcedureQuery.getAttribute("procedureName");
        m_returnsResultSet = (Boolean) namedStoredProcedureQuery.getAttribute("returnsResultSet");
    }
   
    /**
     * INTERNAL:
     */
    @Override
    public boolean equals(Object objectToCompare) {
        if (super.equals(objectToCompare) && objectToCompare instanceof NamedStoredProcedureQueryMetadata) {
            NamedStoredProcedureQueryMetadata query = (NamedStoredProcedureQueryMetadata) objectToCompare;
            
            if (! valuesMatch(m_returnsResultSet, query.getReturnsResultSet())) {
                return false;
            }
            
            if (! valuesMatch(m_parameters, query.getParameters())) {
                return false;
            }
            
            return valuesMatch(m_procedureName, query.getProcedureName());
        }
        
        return false;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getProcedureName() {
        return m_procedureName;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public List<StoredProcedureParameterMetadata> getParameters() {
        return m_parameters;
    }
    
    /**
     * INTERNAL:
     */
    public Boolean getReturnsResultSet() {
        return m_returnsResultSet;
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public void initXMLObject(MetadataAccessibleObject accessibleObject, XMLEntityMappings entityMappings) {
        super.initXMLObject(accessibleObject, entityMappings);
        
        // Initialize parameters ...
        initXMLObjects(m_parameters, accessibleObject);
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public void process(AbstractSession session, ClassLoader loader) {
        // Build the stored procedure call.
        StoredProcedureCall call = new StoredProcedureCall();
        
        // Process the stored procedure parameters.
        List<String> queryArguments = new ArrayList<String>();
        for (StoredProcedureParameterMetadata parameter : m_parameters) {
            queryArguments.addAll(parameter.process(call));
        }
        
        // Process the procedure name.
        call.setProcedureName(m_procedureName);
        
        // Process the returns result set.
        call.setReturnsResultSet((m_returnsResultSet == null) ? false : m_returnsResultSet);
        
        // Process the query hints.
        Map<String, Object> hints = processQueryHints(session);
        
        // Process the result class.
        if (getResultClass().isVoid()) {
            if (getResultSetMapping().equals("")) {
                // Neither a resultClass or resultSetMapping is specified so place in a temp query on the session
                session.addQuery(getName(), EJBQueryImpl.buildStoredProcedureQuery(call, queryArguments, hints, loader, session));
            } else {
                session.addQuery(getName(), EJBQueryImpl.buildStoredProcedureQuery(getResultSetMapping(), call, queryArguments, hints, loader, session));
            }
        } else {
            session.addQuery(getName(), EJBQueryImpl.buildStoredProcedureQuery(MetadataHelper.getClassForName(getResultClass().getName(), loader), call, queryArguments, hints, loader, session));
        }
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setProcedureName(String procedureName) {
        m_procedureName = procedureName;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setParameters(List<StoredProcedureParameterMetadata> parameters) {
        m_parameters = parameters;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setReturnsResultSet(Boolean returnsResultSet) {
        m_returnsResultSet = returnsResultSet;
    }
}
