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
package org.eclipse.persistence.internal.jpa.config.columns;

import org.eclipse.persistence.internal.jpa.metadata.columns.JoinColumnMetadata;
import org.eclipse.persistence.jpa.config.JoinColumn;

/**
 * JPA scripting API implementation.
 *
 * @author Guy Pelletier
 * @since EclipseLink 2.5.1
 */
public class JoinColumnImpl extends AbstractRelationalColumnImpl<JoinColumnMetadata, JoinColumn> implements JoinColumn {

    public JoinColumnImpl() {
        super(new JoinColumnMetadata());
    }

    @Override
    public JoinColumn setInsertable(Boolean insertable) {
        getMetadata().setInsertable(insertable);
        return this;
    }

    @Override
    public JoinColumn setNullable(Boolean nullable) {
        getMetadata().setNullable(nullable);
        return this;
    }

    @Override
    public JoinColumn setTable(String table) {
        getMetadata().setTable(table);
        return this;
    }

    @Override
    public JoinColumn setUpdatable(Boolean updatable) {
        getMetadata().setUpdatable(updatable);
        return this;
    }

    @Override
    public JoinColumn setUnique(Boolean unique) {
        getMetadata().setUnique(unique);
        return this;
    }

}
