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
//     06/20/2012-2.5 Guy Pelletier
//       - 350487: JPA 2.1 Specification defined support for Stored Procedure Calls
//     08/11/2012-2.5 Guy Pelletier
//       - 393867: Named queries do not work when using EM level Table Per Tenant Multitenancy.
//     11/19/2012-2.5 Guy Pelletier
//       - 389090: JPA 2.1 DDL Generation Support (foreign key metadata support)
package org.eclipse.persistence.internal.jpa.metadata.queries;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.jpa.JPAQuery;
import org.eclipse.persistence.internal.jpa.QueryHintsHandler;
import org.eclipse.persistence.internal.jpa.jpql.JPQLQueryHelper;
import org.eclipse.persistence.internal.jpa.metadata.ORMetadata;
import org.eclipse.persistence.internal.jpa.metadata.accessors.MetadataAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;
import org.eclipse.persistence.internal.sessions.AbstractSession;

/**
 * INTERNAL:
 * Object to hold onto a named query metadata.
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
 * @since TopLink EJB 3.0 Reference Implementation
 */
public class NamedQueryMetadata extends ORMetadata {
    private List<QueryHintMetadata> m_hints = new ArrayList<QueryHintMetadata>();

    private String m_lockMode;
    private String m_name;
    private String m_query;

    /**
     * INTERNAL:
     * Used for XML loading.
     */
    public NamedQueryMetadata() {
        super("<named-query>");
    }

    /**
     * INTERNAL:
     * Used for annotation loading.
     */
    public NamedQueryMetadata(MetadataAnnotation namedQuery, MetadataAccessor accessor) {
        super(namedQuery, accessor);

        m_name = namedQuery.getAttributeString("name");
        m_query = namedQuery.getAttributeString("query");
        m_lockMode = namedQuery.getAttributeString("lockMode");

        for (Object hint : namedQuery.getAttributeArray("hints")) {
            m_hints.add(new QueryHintMetadata((MetadataAnnotation)hint, accessor));
        }
    }

    /**
     * INTERNAL:
     * Used for XML loading.
     */
    protected NamedQueryMetadata(String xmlElement) {
        super(xmlElement);
    }

    /**
     * INTERNAL:
     * Add the query the session. Table per tenant queries should not be added
     * to the regular query list as these queries may need to be initialized
     * per EM.
     */
    protected void addJPAQuery(JPAQuery query, AbstractSession session) {
        if (query.isJPQLQuery()) {
            List<ClassDescriptor> descriptors = new JPQLQueryHelper().getClassDescriptors(query.getJPQLString(), session);

            for (ClassDescriptor descriptor : descriptors) {
                // If we find one descriptor that has table per tenant multitenancy,
                // then add it to the multitenant query list. These queries may
                // need to be initialized per EM rather than straight up at the
                // EMF level.
                if (descriptor.hasMultitenantPolicy() && descriptor.getMultitenantPolicy().isTablePerMultitenantPolicy()) {
                    // Store the descriptors so we don't have to parse them again.
                    query.setDescriptors(descriptors);
                    session.addJPATablePerTenantQuery(query);
                    return;
                }
            }
        }

        session.addJPAQuery(query);
    }

    /**
     * INTERNAL:
     */
    @Override
    public boolean equals(Object objectToCompare) {
        if (objectToCompare instanceof NamedQueryMetadata) {
            NamedQueryMetadata query = (NamedQueryMetadata) objectToCompare;

            if (! valuesMatch(m_name, query.getName())) {
                return false;
            }

            if (! valuesMatch(m_query, query.getQuery())) {
                return false;
            }

            return valuesMatch(m_hints, query.getHints());
        }

        return false;
    }

    @Override
    public int hashCode() {
        int result = m_hints != null ? m_hints.hashCode() : 0;
        result = 31 * result + (m_name != null ? m_name.hashCode() : 0);
        result = 31 * result + (m_query != null ? m_query.hashCode() : 0);
        return result;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public List<QueryHintMetadata> getHints() {
        return m_hints;
    }

    /**
     * INTERNAL:
     * To satisfy the abstract getIdentifier() method from ORMetadata.
     */
    @Override
    public String getIdentifier() {
        return m_name;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getLockMode() {
        return m_lockMode;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getName() {
        return m_name;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getQuery() {
        return m_query;
    }

    /**
     * INTERNAL:
     */
    public void process(AbstractSession session) {
        try {
            addJPAQuery(new JPAQuery(getName(), getQuery(), getLockMode(), processQueryHints(session)), session);
        } catch (Exception exception) {
            throw ValidationException.errorProcessingNamedQuery(getClass(), getName(), exception);
        }
    }

    /**
     * INTERNAL:
     */
    protected Map<String, Object> processQueryHints(AbstractSession session) {
        Map<String, Object> hints = new HashMap<String, Object>();

        for (QueryHintMetadata hint : getHints()) {
            QueryHintsHandler.verify(hint.getName(), hint.getValue(), getName(), session);
            Object value = hints.get(hint.getName());

            if (value != null) {
                Object[] values = null;

                if (value instanceof Object[]) {
                    List list = new ArrayList(Arrays.asList((Object[])value));
                    list.add(hint.getValue());
                    values = list.toArray();
                } else {
                    values = new Object[2];
                    values[0] = value;
                    values[1] = hint.getValue();
                }

                hints.put(hint.getName(), values);
            } else {
                hints.put(hint.getName(), hint.getValue());
            }
        }

        return hints;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setHints(List<QueryHintMetadata> hints) {
        m_hints = hints;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setLockMode(String lockMode) {
        m_lockMode = lockMode;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setName(String name) {
        m_name = name;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setQuery(String query) {
        m_query = query;
    }
}
