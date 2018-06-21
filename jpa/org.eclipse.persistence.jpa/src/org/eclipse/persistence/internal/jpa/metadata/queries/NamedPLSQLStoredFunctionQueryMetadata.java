/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation
//     02/08/2012-2.4 Guy Pelletier
//       - 350487: JPA 2.1 Specification defined support for Stored Procedure Calls
//     06/20/2012-2.5 Guy Pelletier
//       - 350487: JPA 2.1 Specification defined support for Stored Procedure Calls
//     08/24/2012-2.5 Guy Pelletier
//       - 350487: JPA 2.1 Specification defined support for Stored Procedure Calls
//     08/11/2012-2.5 Guy Pelletier
//       - 393867: Named queries do not work when using EM level Table Per Tenant Multitenancy.
package org.eclipse.persistence.internal.jpa.metadata.queries;

import org.eclipse.persistence.internal.jpa.JPAQuery;
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

        this.returnParameter = new PLSQLParameterMetadata(namedStoredProcedureQuery.getAttributeAnnotation("returnParameter"), accessor);

        setProcedureName(namedStoredProcedureQuery.getAttributeString("functionName"));
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

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (returnParameter != null ? returnParameter.hashCode() : 0);
        return result;
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
    public void process(AbstractSession session) {
        // Build the stored procedure call.
        PLSQLStoredFunctionCall call = new PLSQLStoredFunctionCall();

        // Process the return parameter - the return arg must be processed before
        // the rest of the args to ensure that it is set on the call first
        if (getReturnParameter() != null) {
            getReturnParameter().process(call, true);
        }

        // Process the stored procedure parameters.
        for (PLSQLParameterMetadata parameter : getParameters()) {
            parameter.process(call, false);
        }

        // Process the procedure name.
        call.setProcedureName(getProcedureName());

        // Create a JPA query to store internally on the session.
        JPAQuery query = new JPAQuery(getName(), call, processQueryHints(session));

        // Process the result class.
        if (!getResultClass().isVoid()) {
            query.setResultClassName(getJavaClassName(getResultClass()));
        } else if (hasResultSetMapping(session)) {
            query.addResultSetMapping(getResultSetMapping());
        }

        addJPAQuery(query, session);
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setReturnParameter(PLSQLParameterMetadata returnParameter) {
        this.returnParameter = returnParameter;
    }
}
