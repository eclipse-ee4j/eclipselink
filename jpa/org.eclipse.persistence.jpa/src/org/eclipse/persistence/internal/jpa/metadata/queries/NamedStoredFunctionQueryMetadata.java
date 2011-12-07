/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.queries;

import java.util.Map;

import org.eclipse.persistence.internal.jpa.EJBQueryImpl;
import org.eclipse.persistence.internal.jpa.metadata.MetadataProject;
import org.eclipse.persistence.internal.jpa.metadata.accessors.MetadataAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappings;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.queries.StoredFunctionCall;

/**
 * INTERNAL:
 * Object to hold onto a named stored function query.
 * 
 * Key notes:
 * - any metadata mapped from XML to this class must be compared in the
 *   matches method.
 * - when loading from annotations, the constructor accepts the metadata
 *   accessor this metadata was loaded from. Used it to look up any 
 *   'companion' annotation needed for processing.
 * - methods should be preserved in alphabetical order.
 * 
 * @author James
 * @since EclipseLink 2.3
 */
public class NamedStoredFunctionQueryMetadata extends NamedStoredProcedureQueryMetadata {
    private StoredProcedureParameterMetadata returnParameter;

    public NamedStoredFunctionQueryMetadata() {
        super("<named-stored-function-query>");
    }

    public NamedStoredFunctionQueryMetadata(MetadataAnnotation namedStoredProcedureQuery, MetadataAccessor accessor) {
        super(namedStoredProcedureQuery, accessor);
         
        this.returnParameter = new StoredProcedureParameterMetadata((MetadataAnnotation)namedStoredProcedureQuery.getAttribute("returnParameter"), accessor);
        
        setProcedureName((String) namedStoredProcedureQuery.getAttribute("functionName"));
    }
    
    /**
     * INTERNAL:
     * For merging and overriding to work properly, all ORMetadata must be able 
     * to compare themselves for metadata equality.
     */
    @Override
    public boolean equals(Object objectToCompare) {
        if (super.equals(objectToCompare) && objectToCompare instanceof NamedStoredFunctionQueryMetadata) {
            NamedStoredFunctionQueryMetadata query = (NamedStoredFunctionQueryMetadata) objectToCompare;                        
            
            return valuesMatch(this.returnParameter, query.getReturnParameter());
        }
        
        return false;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public StoredProcedureParameterMetadata getReturnParameter() {
        return returnParameter;
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public void initXMLObject(MetadataAccessibleObject accessibleObject, XMLEntityMappings entityMappings) {
        super.initXMLObject(accessibleObject, entityMappings);
        
        // Initialize parameters ...
        initXMLObject(this.returnParameter, accessibleObject);
    }

    /**
     * INTERNAL:
     */
    @Override
    public void process(AbstractSession session, ClassLoader loader, MetadataProject project) {
        // Build the stored procedure call.
        StoredFunctionCall call = new StoredFunctionCall();
        
        // Process the stored procedure parameters.
        boolean callByIndex = (m_callByIndex == null) ? false : m_callByIndex;
        for (StoredProcedureParameterMetadata parameter : getParameters()) {
            parameter.process(call, project, callByIndex, false);
        }
        
        if (getReturnParameter() != null) {
            getReturnParameter().process(call, project, callByIndex, true);
        }
        
        // Process the procedure name.
        call.setProcedureName(getProcedureName());
                
        // Process the query hints.
        Map<String, Object> hints = processQueryHints(session);
        
        // Process the result class.
        if (getResultClass().isVoid()) {
            if (hasResultSetMapping(session)) {
                session.addQuery(getName(), EJBQueryImpl.buildStoredProcedureQuery(getResultSetMapping(), call, hints, loader, session));
            } else {
                // Neither a resultClass or resultSetMapping is specified so place in a temp query on the session
                session.addQuery(getName(), EJBQueryImpl.buildStoredProcedureQuery(call, hints, loader, session));
            }
        } else {
            session.addQuery(getName(), EJBQueryImpl.buildStoredProcedureQuery(MetadataHelper.getClassForName(getResultClass().getName(), loader), call, hints, loader, session));
        }
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setReturnParameter(StoredProcedureParameterMetadata returnParameter) {
        this.returnParameter = returnParameter;
    }
}
