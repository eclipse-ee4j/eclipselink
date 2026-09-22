/*
 * Copyright (c) 1998, 2025 Oracle and/or its affiliates. All rights reserved.
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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.jpa.JPAQuery;
import org.eclipse.persistence.internal.jpa.metadata.accessors.MetadataAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.queries.SQLResultSetMapping;

import static org.eclipse.persistence.internal.helper.CollectionUtils.isEmpty;

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

    private String resultSetMapping;
    private List<EntityResultMetadata> entityResults = new ArrayList<>();
    private List<ConstructorResultMetadata> constructorResults = new ArrayList<>();
    private List<ColumnResultMetadata> columnResults = new ArrayList<>();

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

        resultSetMapping = namedNativeQuery.getAttributeString("resultSetMapping");

        for (Object entityResult : namedNativeQuery.getAttributeArray("entities")) {
            entityResults.add(new EntityResultMetadata((MetadataAnnotation) entityResult, accessor));
        }

        for (Object constructorResult : namedNativeQuery.getAttributeArray("classes")) {
            constructorResults.add(new ConstructorResultMetadata((MetadataAnnotation) constructorResult, accessor));
        }

        for (Object columnResult : namedNativeQuery.getAttributeArray("columns")) {
            columnResults.add(new ColumnResultMetadata((MetadataAnnotation) columnResult, accessor));
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
     * Used for OX mapping.
     */
    public List<ColumnResultMetadata> getColumnResults() {
        return columnResults;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public List<ConstructorResultMetadata> getConstructorResults() {
        return constructorResults;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public List<EntityResultMetadata> getEntityResults() {
        return entityResults;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getResultSetMapping() {
        return resultSetMapping;
    }

    /**
     * INTERNAL:
     */
    @Override
    public void process(AbstractSession session) {
        if (hasResultSetMapping(session) && hasInlineResultSetMapping()) {
            throw ValidationException.duplicitResultSetMappingInNativeQuery(getAccessibleObjectName(), getName());
        }

        // Create a Jakarta Persistence query to store internally on the session.
        JPAQuery query = new JPAQuery(getName(), getQuery(), processQueryHints(session));

        // The entities, classes and columns elements are a way of declaring a result set mapping on
        // the query itself rather than referring to a named one, so they are processed ahead of the
        // result class: they describe the shape of each row, whereas a result class only names the
        // type a row is read into, and must agree with what the mapping says.
        //
        // Were the result class taken first, a result class naming anything other than an entity (a basic
        // type or a constructor's class), would be read as an entity result and fail for want of a descriptor,
        // and the mapping the caller declared would be discarded unused.
        if (hasInlineResultSetMapping()) {
            query.setLocalResultSetMapping(processInlineResultSetMapping());
        } else if (!getResultClass().isVoid()) {
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
        this.columnResults = columnResults;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setConstructorResults(List<ConstructorResultMetadata> constructorResults) {
        this.constructorResults = constructorResults;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setEntityResults(List<EntityResultMetadata> entityResults) {
        this.entityResults = entityResults;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setResultSetMapping(String resultSetMapping) {
        this.resultSetMapping = resultSetMapping;
    }

    /**
     * INTERNAL:
     */
    @Override
    public boolean equals(Object objectToCompare) {
        if (super.equals(objectToCompare) && objectToCompare instanceof NamedNativeQueryMetadata query) {

            if (!valuesMatch(entityResults, query.getEntityResults())) {
                return false;
            }

            if (!valuesMatch(columnResults, query.getColumnResults())) {
                return false;
            }

            if (!valuesMatch(constructorResults, query.getConstructorResults())) {
                return false;
            }

            return valuesMatch(resultSetMapping, query.getResultSetMapping());
        }

        return false;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (resultSetMapping != null ? resultSetMapping.hashCode() : 0);
        result = 31 * result + (entityResults != null ? entityResults.hashCode() : 0);
        result = 31 * result + (columnResults != null ? columnResults.hashCode() : 0);
        result = 31 * result + (constructorResults != null ? constructorResults.hashCode() : 0);
        return result;
    }

    /**
     * INTERNAL:
     * Return true is a result set mapping has been specified.
     */
    protected boolean hasResultSetMapping(AbstractSession session) {
        if (isEmpty(resultSetMapping)) {
            return false;
        }

        // User has specified a result set mapping. Since all the result
        // set mappings are processed and placed on the session before named
        // queries, let's validate that the sql result set mapping specified
        // on this query actually exists.
        if (!session.getProject().hasSQLResultSetMapping(resultSetMapping)) {
            throw ValidationException.invalidSQLResultSetMapping(resultSetMapping, getName(), getLocation());
        }

        return true;
    }

    /**
     * INTERNAL:
     * Return whether this query declares its own result set mapping through the entities, classes
     * or columns elements, rather than naming one declared elsewhere.
     */
    protected boolean hasInlineResultSetMapping() {
        return !entityResults.isEmpty() || !constructorResults.isEmpty() || !columnResults.isEmpty();
    }

    /**
     * INTERNAL:
     * Build the result set mapping this query declares through its entities, classes and columns
     * elements. It is named after the query, since it belongs to the query alone and is never
     * looked up by name.
     */
    protected SQLResultSetMapping processInlineResultSetMapping() {
        SQLResultSetMapping sqlResultSetMapping = new SQLResultSetMapping(getName());

        // Process the entity results first.
        for (EntityResultMetadata entityResult : entityResults) {
            sqlResultSetMapping.addResult(entityResult.process());
        }
        // Process the constructor results second.
        for (ConstructorResultMetadata constructorResult : constructorResults) {
            sqlResultSetMapping.addResult(constructorResult.process());
        }
        // Process the column results third.
        for (ColumnResultMetadata columnResult : columnResults) {
            sqlResultSetMapping.addResult(columnResult.process());
        }

        return sqlResultSetMapping;
    }
}
