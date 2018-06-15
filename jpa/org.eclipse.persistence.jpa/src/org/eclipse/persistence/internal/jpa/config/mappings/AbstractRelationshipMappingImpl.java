/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Guy Pelletier - initial API and implementation
package org.eclipse.persistence.internal.jpa.config.mappings;

import java.util.ArrayList;

import org.eclipse.persistence.internal.jpa.config.columns.JoinColumnImpl;
import org.eclipse.persistence.internal.jpa.config.columns.JoinFieldImpl;
import org.eclipse.persistence.internal.jpa.config.tables.JoinTableImpl;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.RelationshipAccessor;
import org.eclipse.persistence.internal.jpa.metadata.columns.JoinColumnMetadata;
import org.eclipse.persistence.internal.jpa.metadata.columns.JoinFieldMetadata;
import org.eclipse.persistence.jpa.config.BatchFetch;
import org.eclipse.persistence.jpa.config.Cascade;
import org.eclipse.persistence.jpa.config.JoinColumn;
import org.eclipse.persistence.jpa.config.JoinField;
import org.eclipse.persistence.jpa.config.JoinTable;

/**
 * JPA scripting API implementation.
 *
 * @author Guy Pelletier
 * @since EclipseLink 2.5.1
 */
@SuppressWarnings("unchecked")
public class AbstractRelationshipMappingImpl<T extends RelationshipAccessor, R> extends AbstractMappingImpl<T, R> {

    public AbstractRelationshipMappingImpl(T t) {
        super(t);

        getMetadata().setJoinColumns(new ArrayList<JoinColumnMetadata>());
        getMetadata().setJoinFields(new ArrayList<JoinFieldMetadata>());
    }

    public JoinColumn addJoinColumn() {
        JoinColumnImpl joinColumn = new JoinColumnImpl();
        getMetadata().getJoinColumns().add(joinColumn.getMetadata());
        return joinColumn;
    }

    public JoinField addJoinField() {
        JoinFieldImpl joinField = new JoinFieldImpl();
        getMetadata().getJoinFields().add(joinField.getMetadata());
        return joinField;
    }

    public BatchFetch setBatchFetch() {
        BatchFetchImpl batchFetch = new BatchFetchImpl();
        getMetadata().setBatchFetch(batchFetch.getMetadata());
        return batchFetch;
    }

    public Cascade setCascade() {
        CascadeImpl cascade = new CascadeImpl();
        getMetadata().setCascade(cascade.getMetadata());
        return cascade;
    }

    public R setCascadeOnDelete(Boolean cascadeOnDelete) {
        getMetadata().setCascadeOnDelete(cascadeOnDelete);
        return (R) this;
    }

    public R setFetch(String fetch) {
        getMetadata().setFetch(fetch);
        return (R) this;
    }

    public R setJoinFetch(String joinFetch) {
        getMetadata().setJoinFetch(joinFetch);
        return (R) this;
    }

    public JoinTable setJoinTable() {
        JoinTableImpl joinTable = new JoinTableImpl();
        getMetadata().setJoinTable(joinTable.getMetadata());
        return joinTable;
    }

    public R setMappedBy(String mappedBy) {
        getMetadata().setMappedBy(mappedBy);
        return (R) this;
    }

    public R setNonCacheable(Boolean nonCacheable) {
        getMetadata().setNonCacheable(nonCacheable);
        return (R) this;
    }

    public R setOrphanRemoval(Boolean orphanRemoval) {
        getMetadata().setOrphanRemoval(orphanRemoval);
        return (R) this;
    }

    public R setPrivateOwned(Boolean privateOwned) {
        getMetadata().setPrivateOwned(privateOwned);
        return (R) this;
    }

    public R setTargetEntity(String targetEntity) {
        getMetadata().setTargetEntityName(targetEntity);
        return (R) this;
    }

}
