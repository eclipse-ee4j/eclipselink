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

import org.eclipse.persistence.internal.jpa.config.columns.ForeignKeyImpl;
import org.eclipse.persistence.internal.jpa.config.columns.PrimaryKeyJoinColumnImpl;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.ObjectAccessor;
import org.eclipse.persistence.internal.jpa.metadata.columns.PrimaryKeyJoinColumnMetadata;
import org.eclipse.persistence.jpa.config.ForeignKey;
import org.eclipse.persistence.jpa.config.PrimaryKeyJoinColumn;

/**
 * JPA scripting API implementation.
 *
 * @author Guy Pelletier
 * @since EclipseLink 2.5.1
 */
@SuppressWarnings("unchecked")
public class AbstractObjectMappingImpl<T extends ObjectAccessor, R> extends AbstractRelationshipMappingImpl<T, R> {

    public AbstractObjectMappingImpl(T t) {
        super(t);

        getMetadata().setPrimaryKeyJoinColumns(new ArrayList<PrimaryKeyJoinColumnMetadata>());
    }

    public PrimaryKeyJoinColumn addPrimaryKeyJoinColumn() {
        PrimaryKeyJoinColumnImpl primaryKeyJoinColumn = new PrimaryKeyJoinColumnImpl();
        getMetadata().getPrimaryKeyJoinColumns().add(primaryKeyJoinColumn.getMetadata());
        return primaryKeyJoinColumn;
    }

    public ForeignKey setForeignKey() {
        ForeignKeyImpl foreignKey = new ForeignKeyImpl();
        getMetadata().setForeignKey(foreignKey.getMetadata());
        return foreignKey;
    }

    public R setId(Boolean id) {
        getMetadata().setId(id);
        return (R) this;
    }

    public R setMapsId(String mapsId) {
        getMetadata().setMapsId(mapsId);
        return (R) this;
    }

    public R setOptional(Boolean optional) {
        getMetadata().setOptional(optional);
        return (R) this;
    }
}
