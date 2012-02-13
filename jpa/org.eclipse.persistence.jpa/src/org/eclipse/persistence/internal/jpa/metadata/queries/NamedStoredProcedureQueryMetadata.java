/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle. All rights reserved.
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
 *     02/08/2012-2.4 Guy Pelletier 
 *       - 350487: JPA 2.1 Specification defined support for Stored Procedure Calls
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.queries;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.persistence.internal.jpa.StoredProcedureQueryImpl;
import org.eclipse.persistence.internal.jpa.metadata.accessors.MetadataAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataClass;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappings;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.queries.SQLResultSetMapping;
import org.eclipse.persistence.queries.StoredProcedureCall;

/**
 * INTERNAL:
 * Object to hold onto a named stored procedure query.
 * 
 * Key notes:
 * - any metadata mapped from XML to this class must be compared in the
 *   equals method.
 * - all metadata mapped from XML should be initialized in the initXMLObject 
 *   method.
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
    private Boolean m_callByIndex;

    private List<MetadataClass> m_resultClasses = new ArrayList<MetadataClass>();
    private List<String> m_resultClassNames = new ArrayList<String>();
    private List<String> m_resultSetMappings = new ArrayList<String>();
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
     */
    public NamedStoredProcedureQueryMetadata(MetadataAnnotation namedStoredProcedureQuery, MetadataAccessor accessor) {
        super(namedStoredProcedureQuery, accessor);
         
        for (Object storedProcedureParameter : (Object[]) namedStoredProcedureQuery.getAttributeArray("parameters")) {
           m_parameters.add(new StoredProcedureParameterMetadata((MetadataAnnotation)storedProcedureParameter, accessor));
        }
        
        // JPA spec allows for multiple result classes.
        for (Object resultClass : (Object[]) namedStoredProcedureQuery.getAttributeArray("resultClasses")) {
            m_resultClasses.add(getMetadataClass((String) resultClass));
        }

        // JPA spec allows for multiple result set mappings.
        for (Object resultSetMapping : (Object[]) namedStoredProcedureQuery.getAttributeArray("resultSetMappings")) {
            m_resultSetMappings.add((String) resultSetMapping);
        }
        
        m_procedureName = (String) namedStoredProcedureQuery.getAttribute("procedureName");
        
        // Don't default these booleans as we want to know if the user has actually set them.
        m_returnsResultSet = (Boolean) namedStoredProcedureQuery.getAttribute("returnsResultSet");
        m_multipleResultSets = (Boolean) namedStoredProcedureQuery.getAttribute("multipleResultSets");
        
        m_callByIndex = (Boolean) namedStoredProcedureQuery.getAttributeBooleanDefaultFalse("callByIndex");
    }
    
    /**
     * INTERNAL:
     * Used for XML loading.
     */
    public NamedStoredProcedureQueryMetadata(String elementName) {
        super(elementName);
    }
   
    /**
     * INTERNAL:
     */
    public boolean callByIndex() {
        return m_callByIndex == null ? false : m_callByIndex; 
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
            
            if (! valuesMatch(m_resultClassNames, query.getResultClassNames())) {
                return false;
            }
            
            if (! valuesMatch(m_resultSetMappings, query.getResultSetMappings())) {
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
    public Boolean getCallByIndex() {
        return m_callByIndex;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping
     */
    public Boolean getMultipleResultSets() {
        return m_multipleResultSets;
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
     * Used for OX mapping.
     */
    public String getProcedureName() {
        return m_procedureName;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public List<String> getResultClassNames() {
        return m_resultClassNames;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public List<String> getResultSetMappings() {
        return m_resultSetMappings;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping
     */
    public Boolean getReturnsResultSet() {
        return m_returnsResultSet;
    }
    
    /**
     * INTERNAL:
     * If there is no user setting and there are not multiple result classes
     * or result set mappings, assume a single result is returned. There is no 
     * way to set this parameter via JPA, only through EclipseLink's metadata.
     */
    public boolean hasMultipleResultSets() {
        return m_multipleResultSets == null ? (m_resultClasses.size() > 1 || m_resultSetMappings.size() > 1) : m_multipleResultSets;
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public void initXMLObject(MetadataAccessibleObject accessibleObject, XMLEntityMappings entityMappings) {
        super.initXMLObject(accessibleObject, entityMappings);
        
        // Initialize parameters ...
        initXMLObjects(m_parameters, accessibleObject);
        
        // Initialize all the result class names into MetadataClass.
        for (String resultClassName : m_resultClassNames) {
            m_resultClasses.add(initXMLClassName(resultClassName));
        }
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public void process(AbstractSession session, ClassLoader loader) {
        // Build the stored procedure call.
        StoredProcedureCall call = new StoredProcedureCall();
        
        // Process the stored procedure parameters.
        int index = 1;
        boolean callByIndex = callByIndex();
        boolean hasOutParameters = false;
        for (StoredProcedureParameterMetadata parameter : m_parameters) {
            parameter.processArgument(call, callByIndex, index);
            index++;
            
            // In JPA, if we have at least one out parameter we assume the
            // procedure does not return a result set.
            if (parameter.isOutParameter()) {
                hasOutParameters = true;
            }
        }
        
        // Process the procedure name.
        call.setProcedureName(m_procedureName);
        
        // Process the returns result set.
        call.setReturnsResultSet(returnsResultSet(hasOutParameters));
        
        // Process the multiple result sets.
        call.setHasMultipleResultSets(hasMultipleResultSets());
        
        // Process the query hints.
        Map<String, Object> hints = processQueryHints(session);
        
        if (! m_resultClasses.isEmpty()) {    
            // Process the multiple result classes.
            List<SQLResultSetMapping> resultSetMappings = new ArrayList<SQLResultSetMapping>();
            
            for (MetadataClass resultClass : m_resultClasses) {
                resultSetMappings.add(new SQLResultSetMappingMetadata(resultClass).process(loader)); 
            }
            
            session.addQuery(getName(), StoredProcedureQueryImpl.buildResultSetMappingQuery(resultSetMappings, call, hints, loader, session));
        } else if (! m_resultSetMappings.isEmpty()) {
            // Process the multiple result set mapping.
            session.addQuery(getName(), StoredProcedureQueryImpl.buildResultSetMappingNameQuery(m_resultSetMappings, call, hints, loader, session));
        } else {            
            // Legacy support (EclipseLink @NamedStoreProcedureQuery).
            if (getResultClass().isVoid()) {
                // Process the single result set mapping.
                if (hasResultSetMapping(session)) {
                    session.addQuery(getName(), StoredProcedureQueryImpl.buildStoredProcedureQuery(getResultSetMapping(), call, hints, loader, session));
                } else {
                    // Neither a resultClass or resultSetMapping is specified so place in a temp query on the session
                    session.addQuery(getName(), StoredProcedureQueryImpl.buildStoredProcedureQuery(call, hints, loader, session));
                }
            } else {
                // Process the single result class.
                session.addQuery(getName(), StoredProcedureQueryImpl.buildStoredProcedureQuery(MetadataHelper.getClassForName(getResultClass().getName(), loader), call, hints, loader, session));
            }
        }
    }
    
    /**
     * INTERNAL:
     * If there is no user setting and there are no out parameters, assume
     * a result set is returned. There is no way to set this parameter via
     * JPA, only through EclipseLink's metadata.
     */
    public boolean returnsResultSet(boolean hasOutParameters) {
        return m_returnsResultSet == null ? (!hasOutParameters) : m_returnsResultSet; 
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setCallByIndex(Boolean callByIndex) {
        m_callByIndex = callByIndex;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping
     */
    public void setMultipleResultSets(Boolean multipleResultSets) {
        m_multipleResultSets = multipleResultSets;
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
    public void setProcedureName(String procedureName) {
        m_procedureName = procedureName;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setResultClassNames(List<String> resultClassNames) {
        m_resultClassNames = resultClassNames;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setResultSetMappings(List<String> resultSetMappings) {
        m_resultSetMappings = resultSetMappings;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setReturnsResultSet(Boolean returnsResultSet) {
        m_returnsResultSet = returnsResultSet;
    }
}
