/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
 *     03/24/2011-2.3 Guy Pelletier 
 *       - 337323: Multi-tenant with shared schema support (part 1)
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.queries;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.persistence.internal.jpa.EJBQueryImpl;
import org.eclipse.persistence.internal.jpa.metadata.MetadataProject;
import org.eclipse.persistence.internal.jpa.metadata.accessors.MetadataAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappings;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.queries.StoredProcedureCall;

/**
 * INTERNAL:
 * Object to hold onto a named stored procedure query.
 * 
 * Key notes:
 * - any metadata mapped from XML to this class must be compared in the
 *   equals method.
 * - when loading from annotations, the constructor accepts the metadata
 *   accessor this metadata was loaded from. Used it to look up any 
 *   'companion' annotation needed for processing.
 * - methods should be preserved in alphabetical order.
 * 
 * @author Guy Pelletier
 * @since TopLink 11g
 */
public class NamedStoredProcedureQueryMetadata extends NamedNativeQueryMetadata {
    private Boolean m_multipleResultSets;
    private Boolean m_returnsResultSet;
    protected Boolean m_callByIndex;

    private List<StoredProcedureParameterMetadata> m_parameters = new ArrayList<StoredProcedureParameterMetadata>();
    private String m_procedureName;
    
    /**
     * INTERNAL:
     * Used for XML loading.
     */
    public NamedStoredProcedureQueryMetadata() {
        super("<named-stored-procedure-query>");
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public NamedStoredProcedureQueryMetadata(String elementName) {
        super(elementName);
    }
    
    /**
     * INTERNAL:
     */
    public NamedStoredProcedureQueryMetadata(MetadataAnnotation namedStoredProcedureQuery, MetadataAccessor accessor) {
        super(namedStoredProcedureQuery, accessor);
         
        for (Object storedProcedureParameter : (Object[]) namedStoredProcedureQuery.getAttributeArray("parameters")) {
           m_parameters.add(new StoredProcedureParameterMetadata((MetadataAnnotation)storedProcedureParameter, accessor));
        }
        
        m_procedureName = (String) namedStoredProcedureQuery.getAttribute("procedureName");
        m_returnsResultSet = (Boolean) namedStoredProcedureQuery.getAttribute("returnsResultSet");
        m_multipleResultSets = (Boolean) namedStoredProcedureQuery.getAttribute("multipleResultSets");
        m_callByIndex = (Boolean) namedStoredProcedureQuery.getAttribute("callByIndex");
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
            if (! valuesMatch(m_callByIndex, query.getCallByIndex())) {
                return false;
            }
            if (! valuesMatch(m_multipleResultSets, query.getMultipleResultSets())) {
                return false;
            }
            
            if (! valuesMatch(m_parameters, query.getParameters())) {
                return false;
            }
            
            return valuesMatch(m_procedureName, query.getProcedureName());
        }
        
        return false;
    }

    public Boolean getCallByIndex() {
        return m_callByIndex;
    }

    public void setCallByIndex(Boolean callByIndex) {
        m_callByIndex = callByIndex;
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
    
    public Boolean getMultipleResultSets() {
        return m_multipleResultSets;
    }

    public void setMultipleResultSets(Boolean multipleResultSets) {
        m_multipleResultSets = multipleResultSets;
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
    public void process(AbstractSession session, ClassLoader loader, MetadataProject project) {
        // Build the stored procedure call.
        StoredProcedureCall call = new StoredProcedureCall();
        
        // Process the stored procedure parameters.
        List<String> queryArguments = new ArrayList<String>();
        boolean callByIndex = (m_callByIndex == null) ? false : m_callByIndex;
        for (StoredProcedureParameterMetadata parameter : m_parameters) {
            queryArguments.addAll(parameter.process(call, project, callByIndex));
        }
        
        // Process the procedure name.
        call.setProcedureName(m_procedureName);
        
        // Process the returns result set.
        call.setReturnsResultSet((m_returnsResultSet == null) ? false : m_returnsResultSet);
        
        call.setHasMultipleResultSets((m_multipleResultSets == null) ? false : m_multipleResultSets);
        
        // Process the query hints.
        Map<String, Object> hints = processQueryHints(session);
        
        // Process the result class.
        if (getResultClass().isVoid()) {
            if (hasResultSetMapping(session)) {
                session.addQuery(getName(), EJBQueryImpl.buildStoredProcedureQuery(getResultSetMapping(), call, queryArguments, hints, loader, session));
            } else {
                // Neither a resultClass or resultSetMapping is specified so place in a temp query on the session
                session.addQuery(getName(), EJBQueryImpl.buildStoredProcedureQuery(call, queryArguments, hints, loader, session));
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
