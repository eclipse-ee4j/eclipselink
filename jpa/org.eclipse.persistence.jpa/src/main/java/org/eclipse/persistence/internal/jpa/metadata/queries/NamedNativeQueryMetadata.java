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
import org.eclipse.persistence.internal.jpa.metadata.columns.ColumnMetadata;
import org.eclipse.persistence.internal.jpa.metadata.columns.JoinColumnMetadata;
import org.eclipse.persistence.internal.sessions.AbstractSession;

import java.util.ArrayList;
import java.util.List;

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
    private List<EntityResultMetadata> m_entityResults = new ArrayList<>();
    private List<ConstructorResultMetadata> m_constructorResults = new ArrayList<>();
    private List<ColumnResultMetadata> m_columnResults = new ArrayList<>();

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

        for (Object entityResult : namedNativeQuery.getAttributeArray("entities")) {
            m_entityResults.add(new EntityResultMetadata((MetadataAnnotation) entityResult, accessor));
        }

        for (Object constructorResult : namedNativeQuery.getAttributeArray("classes")) {
            m_constructorResults.add(new ConstructorResultMetadata((MetadataAnnotation) constructorResult, accessor));
        }

        for (Object columnResult : namedNativeQuery.getAttributeArray("columns")) {
            m_columnResults.add(new ColumnResultMetadata((MetadataAnnotation) columnResult, accessor));
        }

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

            if (! valuesMatch(m_entityResults, query.getEntityResults())) {
                return false;
            }

            if (! valuesMatch(m_columnResults, query.getColumnResults())) {
                return false;
            }

            if (! valuesMatch(m_constructorResults, query.getConstructorResults())) {
                return false;
            }

            return valuesMatch(m_resultSetMapping, query.getResultSetMapping());
        }

        return false;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (m_resultSetMapping != null ? m_resultSetMapping.hashCode() : 0);
        result = 31 * result + (m_entityResults != null ? m_entityResults.hashCode() : 0);
        result = 31 * result + (m_columnResults != null ? m_columnResults.hashCode() : 0);
        result = 31 * result + (m_constructorResults != null ? m_constructorResults.hashCode() : 0);
        return result;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public List<ColumnResultMetadata> getColumnResults() {
        return m_columnResults;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public List<ConstructorResultMetadata> getConstructorResults() {
        return m_constructorResults;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public List<EntityResultMetadata> getEntityResults() {
        return m_entityResults;
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
    public void setColumnResults(List<ColumnResultMetadata> columnResults) {
        m_columnResults = columnResults;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setConstructorResults(List<ConstructorResultMetadata> constructorResults) {
        m_constructorResults = constructorResults;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setEntityResults(List<EntityResultMetadata> entityResults) {
        m_entityResults = entityResults;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setResultSetMapping(String resultSetMapping) {
        m_resultSetMapping = resultSetMapping;
    }
}
