/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
//     05/16/2008-1.0M8 Guy Pelletier
//       - 218084: Implement metadata merging functionality between mapping files
//     03/24/2011-2.3 Guy Pelletier
//       - 337323: Multi-tenant with shared schema support (part 1)
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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.internal.jpa.JPAQuery;
import org.eclipse.persistence.internal.jpa.metadata.accessors.MetadataAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappings;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.platform.database.oracle.plsql.PLSQLStoredProcedureCall;

/**
 * INTERNAL:
 * Object to hold onto a named PLSQL stored procedure query.
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
public class NamedPLSQLStoredProcedureQueryMetadata extends NamedNativeQueryMetadata {

    private List<PLSQLParameterMetadata> m_parameters = new ArrayList<PLSQLParameterMetadata>();
    private String m_procedureName;

    /**
     * INTERNAL:
     * Used for XML loading.
     */
    public NamedPLSQLStoredProcedureQueryMetadata() {
        super("<named-plsql-stored-procedure-query>");
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public NamedPLSQLStoredProcedureQueryMetadata(String elementName) {
        super(elementName);
    }

    /**
     * INTERNAL:
     */
    public NamedPLSQLStoredProcedureQueryMetadata(MetadataAnnotation namedStoredProcedureQuery, MetadataAccessor accessor) {
        super(namedStoredProcedureQuery, accessor);

        for (Object storedProcedureParameter : namedStoredProcedureQuery.getAttributeArray("parameters")) {
           m_parameters.add(new PLSQLParameterMetadata((MetadataAnnotation)storedProcedureParameter, accessor));
        }

        m_procedureName = namedStoredProcedureQuery.getAttributeString("procedureName");
    }

    /**
     * INTERNAL:
     */
    @Override
    public boolean equals(Object objectToCompare) {
        if (super.equals(objectToCompare) && objectToCompare instanceof NamedPLSQLStoredProcedureQueryMetadata) {
            NamedPLSQLStoredProcedureQueryMetadata query = (NamedPLSQLStoredProcedureQueryMetadata) objectToCompare;

            if (! valuesMatch(m_parameters, query.getParameters())) {
                return false;
            }

            return valuesMatch(m_procedureName, query.getProcedureName());
        }

        return false;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (m_parameters != null ? m_parameters.hashCode() : 0);
        result = 31 * result + (m_procedureName != null ? m_procedureName.hashCode() : 0);
        return result;
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
    public List<PLSQLParameterMetadata> getParameters() {
        return m_parameters;
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
    public void process(AbstractSession session) {
        // Build the stored procedure call.
        PLSQLStoredProcedureCall call = new PLSQLStoredProcedureCall();

        // Process the stored procedure parameters.
        for (PLSQLParameterMetadata parameter : m_parameters) {
            parameter.process(call, false);
        }

        // Process the procedure name.
        call.setProcedureName(m_procedureName);

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
    public void setProcedureName(String procedureName) {
        m_procedureName = procedureName;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setParameters(List<PLSQLParameterMetadata> parameters) {
        m_parameters = parameters;
    }
}
