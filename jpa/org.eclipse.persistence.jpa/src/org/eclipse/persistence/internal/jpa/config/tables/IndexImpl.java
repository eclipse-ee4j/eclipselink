/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Guy Pelletier - initial API and implementation
package org.eclipse.persistence.internal.jpa.config.tables;

import java.util.ArrayList;

import org.eclipse.persistence.internal.jpa.config.MetadataImpl;
import org.eclipse.persistence.internal.jpa.metadata.tables.IndexMetadata;
import org.eclipse.persistence.jpa.config.Index;

/**
 * JPA scripting API implementation.
 *
 * @author Guy Pelletier
 * @since EclipseLink 2.5.1
 */
public class IndexImpl extends MetadataImpl<IndexMetadata> implements Index {

    public IndexImpl() {
        super(new IndexMetadata());
        getMetadata().setColumnNames(new ArrayList<String>());
    }

    public Index addColumnName(String columnName) {
        getMetadata().getColumnNames().add(columnName);
        return this;
    }

    public Index setCatalog(String catalog) {
        getMetadata().setCatalog(catalog);
        return this;
    }

    public Index setColumnList(String columnList) {
        getMetadata().setColumnList(columnList);
        return this;
    }

    public Index setName(String name) {
        getMetadata().setName(name);
        return this;
    }

    public Index setSchema(String schema) {
        getMetadata().setSchema(schema);
        return this;
    }

    public Index setTable(String table) {
        getMetadata().setTable(table);
        return this;
    }

    public Index setUnique(Boolean unique) {
        getMetadata().setUnique(unique);
        return this;
    }

}
