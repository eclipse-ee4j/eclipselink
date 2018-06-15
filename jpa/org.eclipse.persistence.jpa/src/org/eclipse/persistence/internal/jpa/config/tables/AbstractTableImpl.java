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
package org.eclipse.persistence.internal.jpa.config.tables;

import java.util.ArrayList;

import org.eclipse.persistence.internal.jpa.config.MetadataImpl;
import org.eclipse.persistence.internal.jpa.metadata.tables.IndexMetadata;
import org.eclipse.persistence.internal.jpa.metadata.tables.TableMetadata;
import org.eclipse.persistence.jpa.config.Index;
import org.eclipse.persistence.jpa.config.UniqueConstraint;

/**
 * JPA scripting API implementation.
 *
 * @author Guy Pelletier
 * @since EclipseLink 2.5.1
 */
@SuppressWarnings("unchecked")
public abstract class AbstractTableImpl<T extends TableMetadata, R> extends MetadataImpl<T> {

    public AbstractTableImpl(T t) {
        super(t);

        getMetadata().setIndexes(new ArrayList<IndexMetadata>());
    }

    public Index addIndex() {
        IndexImpl index = new IndexImpl();
        getMetadata().getIndexes().add(index.getMetadata());
        return index;
    }

    public UniqueConstraint addUniqueConstraint() {
        UniqueConstraintImpl uniqueConstraint = new UniqueConstraintImpl();
        getMetadata().getUniqueConstraints().add(uniqueConstraint.getMetadata());
        return uniqueConstraint;
    }

    public R setCatalog(String catalog) {
        getMetadata().setCatalog(catalog);
        return (R) this;
    }

    public R setCreationSuffix(String creationSuffix) {
        getMetadata().setCreationSuffix(creationSuffix);
        return (R) this;
    }

    public R setName(String name) {
        getMetadata().setName(name);
        return (R) this;
    }

    public R setSchema(String schema) {
        getMetadata().setSchema(schema);
        return (R) this;
    }

}
