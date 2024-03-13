/*
 * Copyright (c) 1998, 2024 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
//     05/16/2008-1.0M8 Guy Pelletier
//       - 218084: Implement metadata merging functionality between mapping files
//     03/27/2009-2.0 Guy Pelletier
//       - 241413: JPA 2.0 Add EclipseLink support for Map type attributes
//     03/24/2011-2.3 Guy Pelletier
//       - 337323: Multi-tenant with shared schema support (part 1)
//     06/20/2012-2.5 Guy Pelletier
//       - 350487: JPA 2.1 Specification defined support for Stored Procedure Calls
//     08/11/2012-2.5 Guy Pelletier
//       - 393867: Named queries do not work when using EM level Table Per Tenant Multitenancy.
package org.eclipse.persistence.internal.jpa.metadata.queries;

import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.jpa.JPAQuery;
import org.eclipse.persistence.internal.jpa.metadata.accessors.MetadataAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;
import org.eclipse.persistence.internal.sessions.AbstractSession;

/**
 * INTERNAL:
 * Object to hold onto named native query metadata.
 * <p>
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
 * @since TopLink EJB 3.0 Reference Implementation
 */
public class NamedNativeQueryMetadata extends NamedQueryMetadata {
    private String m_resultSetMapping;

    /**
     * INTERNAL:
     * Used for XML loading.
     */
    public NamedNativeQueryMetadata() {
        super("<named-native-query>");
    }

    /**
     * INTERNAL:
     * Used for annotation loading.
     */
    public NamedNativeQueryMetadata(MetadataAnnotation namedNativeQuery, MetadataAccessor accessor) {
        super(namedNativeQuery, accessor);

        m_resultSetMapping = namedNativeQuery.getAttributeString("resultSetMapping");
    }

    /**
     * INTERNAL:
     *
     */
    protected NamedNativeQueryMetadata(String javaClassName) {
        super(javaClassName);
    }

    /**
     * INTERNAL:
     */
    @Override
    public boolean equals(Object objectToCompare) {
        if (super.equals(objectToCompare) && objectToCompare instanceof NamedNativeQueryMetadata query) {

            return valuesMatch(m_resultSetMapping, query.getResultSetMapping());
        }

        return false;
    }

    @Override
    public int hashCode() {
        return (m_resultSetMapping != null ? m_resultSetMapping.hashCode() : 0);
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
        if (m_resultSetMapping != null && !m_resultSetMapping.isEmpty()) {
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
    public void process(AbstractSession session) {
        // Create a JPA query to store internally on the session.
        JPAQuery query = new JPAQuery(getName(), getQuery(), processQueryHints(session));

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
    public void setResultSetMapping(String resultSetMapping) {
        m_resultSetMapping = resultSetMapping;
    }
}
