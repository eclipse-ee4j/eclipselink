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
package org.eclipse.persistence.internal.jpa.config.columns;

import org.eclipse.persistence.internal.jpa.metadata.columns.ColumnMetadata;
import org.eclipse.persistence.jpa.config.Column;

/**
 * JPA scripting API implementation.
 *
 * @author Guy Pelletier
 * @since EclipseLink 2.5.1
 */
public class ColumnImpl extends AbstractDirectColumnImpl<ColumnMetadata, Column> implements Column {

    public ColumnImpl() {
        super(new ColumnMetadata());
    }

    public Column setLength(Integer length) {
        getMetadata().setLength(length);
        return this;
    }

    public Column setPrecision(Integer precision) {
        getMetadata().setPrecision(precision);
        return this;
    }

    public Column setScale(Integer scale) {
        getMetadata().setScale(scale);
        return this;
    }

    public Column setTable(String table) {
        getMetadata().setTable(table);
        return this;
    }

    public Column setUnique(Boolean unique) {
        getMetadata().setUnique(unique);
        return this;
    }

}
