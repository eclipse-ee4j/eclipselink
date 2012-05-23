/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation
 *     02/08/2012-2.4 Guy Pelletier 
 *       - 350487: JPA 2.1 Specification defined support for Stored Procedure Calls
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.queries;

import java.util.Map;

import org.eclipse.persistence.internal.jpa.StoredProcedureQueryImpl;
import org.eclipse.persistence.internal.jpa.metadata.accessors.MetadataAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappings;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.platform.database.oracle.plsql.PLSQLStoredFunctionCall;

/**
 * INTERNAL:
 * Object to hold onto a named PLSQL stored function query.
 * 
 * Key notes:
 * - any metadata mapped from XML to this class must be compared in the
 *   equals method.
 * - all metadata mapped from XML must be initialized in the initXMLObject 
 *   method.
 * - when loading from annotations, the constructor accepts the metadata
 *   accessor this metadata was loaded from. Used it to look up any 
 *   'companion' annotation needed for processing.
 * - methods should be preserved in alphabetical order.
 * 
 * @author James Sutherland
 * @since EclipseLink 2.3
 */
public class NamedPLSQLStoredFunctionQueryMetadata extends NamedPLSQLStoredProcedureQueryMetadata {
    private PLSQLParameterMetadata returnParameter;

    /**
     * INTERNAL:
     * Used for XML loading.
     */
    public NamedPLSQLStoredFunctionQueryMetadata() {
        super("<named-plsql-stored-function-query>");
    }

    /**
     * INTERNAL:
     */
    public NamedPLSQLStoredFunctionQueryMetadata(MetadataAnnotation namedStoredProcedureQuery, MetadataAccessor accessor) {
        super(namedStoredProcedureQuery, accessor);
         
        this.returnParameter = new PLSQLParameterMetadata((MetadataAnnotation)namedStoredProcedureQuery.getAttribute("returnParameter"), accessor);
        
        setProcedureName((String) namedStoredProcedureQuery.getAttribute("functionName"));
    }

    /**
     * INTERNAL:
     */
    @Override
    public boolean equals(Object objectToCompare) {
        if (super.equals(objectToCompare) && objectToCompare instanceof NamedPLSQLStoredFunctionQueryMetadata) {
            NamedPLSQLStoredFunctionQueryMetadata query = (NamedPLSQLStoredFunctionQueryMetadata) objectToCompare;                        
            
            return valuesMatch(returnParameter, query.getReturnParameter());
        }
        
        return false;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public PLSQLParameterMetadata getReturnParameter() {
        return returnParameter;
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public void initXMLObject(MetadataAccessibleObject accessibleObject, XMLEntityMappings entityMappings) {
        super.initXMLObject(accessibleObject, entityMappings);
        
        // Initialize the return parameter.
        initXMLObject(returnParameter, accessibleObject);
    }

    /**
     * INTERNAL:
     */
    @Override
    public void process(AbstractSession session, ClassLoader loader) {
        // Build the stored procedure call.
        PLSQLStoredFunctionCall call = new PLSQLStoredFunctionCall();
        
        // Process the stored procedure parameters.
        for (PLSQLParameterMetadata parameter : getParameters()) {
            parameter.process(call, false);
        }
        
        // Process the return parameter.
        if (getReturnParameter() != null) {
            getReturnParameter().process(call, true);
        }
        
        // Process the procedure name.
        call.setProcedureName(getProcedureName());
                
        // Process the query hints.
        Map<String, Object> hints = processQueryHints(session);
        
        // Process the result class.
        if (getResultClass().isVoid()) {
            if (hasResultSetMapping(session)) {
                session.addQuery(getName(), StoredProcedureQueryImpl.buildStoredProcedureQuery(getResultSetMapping(), call, hints, loader, session));
            } else {
                // Neither a resultClass or resultSetMapping is specified so place in a temp query on the session
                session.addQuery(getName(), StoredProcedureQueryImpl.buildStoredProcedureQuery(call, hints, loader, session));
            }
        } else {
            session.addQuery(getName(), StoredProcedureQueryImpl.buildStoredProcedureQuery(MetadataHelper.getClassForName(getResultClass().getName(), loader), call, hints, loader, session));
        }
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setReturnParameter(PLSQLParameterMetadata returnParameter) {
        this.returnParameter = returnParameter;
    }
}
