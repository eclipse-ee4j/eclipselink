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
package org.eclipse.persistence.internal.jpa.config.structures;

import org.eclipse.persistence.internal.jpa.config.columns.ColumnImpl;
import org.eclipse.persistence.internal.jpa.config.mappings.AbstractDirectMappingImpl;
import org.eclipse.persistence.internal.jpa.metadata.structures.ArrayAccessor;
import org.eclipse.persistence.jpa.config.Array;
import org.eclipse.persistence.jpa.config.Column;

/**
 * JPA scripting API implementation.
 *
 * @author Guy Pelletier
 * @since EclipseLink 2.5.1
 */
public class ArrayImpl extends AbstractDirectMappingImpl<ArrayAccessor, Array> implements Array {

    public ArrayImpl() {
        super(new ArrayAccessor());
    }

    @Override
    public Column setColumn() {
        ColumnImpl column = new ColumnImpl();
        getMetadata().setColumn(column.getMetadata());
        return column;
    }

    @Override
    public Array setDatabaseType(String databaseType) {
        getMetadata().setDatabaseType(databaseType);
        return this;
    }

    @Override
    public Array setTargetClass(String targetClass) {
        getMetadata().setTargetClassName(targetClass);
        return this;
    }

}
