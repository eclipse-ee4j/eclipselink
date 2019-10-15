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
//     11/19/2012-2.5 Guy Pelletier
//       - 389090: JPA 2.1 DDL Generation Support (foreign key metadata support)
package org.eclipse.persistence.internal.jpa.metadata.queries;

import org.eclipse.persistence.internal.jpa.JPAQuery;
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
 *   equals method.
 * - all metadata mapped from XML should be initialized in the initXMLObject
 *   method.
 * - when loading from annotations, the constructor accepts the metadata
 *   accessor this metadata was loaded from. Used it to look up any
 *   'companion' annotation needed for processing.
 * - methods should be preserved in alphabetical order.
 *
 * @author James Sutherland
 * @since EclipseLink 2.3
 */
public class NamedStoredFunctionQueryMetadata extends NamedStoredProcedureQueryMetadata {
    private StoredProcedureParameterMetadata returnParameter;

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public NamedStoredFunctionQueryMetadata() {
        super("<named-stored-function-query>");
    }

    /**
     * INTERNAL:
     * Used for annotation mapping.
     */
    public NamedStoredFunctionQueryMetadata(MetadataAnnotation namedStoredProcedureQuery, MetadataAccessor accessor) {
        super(namedStoredProcedureQuery, accessor);

        this.returnParameter = new StoredProcedureParameterMetadata(namedStoredProcedureQuery.getAttributeAnnotation("returnParameter"), accessor);

        setProcedureName(namedStoredProcedureQuery.getAttributeString("functionName"));
    }

    /**
     * INTERNAL:
     */
    @Override
    public boolean equals(Object objectToCompare) {
        if (super.equals(objectToCompare) && objectToCompare instanceof NamedStoredFunctionQueryMetadata) {
            NamedStoredFunctionQueryMetadata query = (NamedStoredFunctionQueryMetadata) objectToCompare;

            return valuesMatch(this.returnParameter, query.getReturnParameter());
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
    public void process(AbstractSession session) {
        // Build the stored procedure call.
        StoredFunctionCall call = new StoredFunctionCall();

        // Process the stored procedure parameters.
        boolean callByIndex = callByIndex();
        for (StoredProcedureParameterMetadata parameter : getParameters()) {
            parameter.processArgument(call, callByIndex, -1);
        }

        if (getReturnParameter() != null) {
            getReturnParameter().processResult(call, -1);
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
    public void setReturnParameter(StoredProcedureParameterMetadata returnParameter) {
        this.returnParameter = returnParameter;
    }
}
