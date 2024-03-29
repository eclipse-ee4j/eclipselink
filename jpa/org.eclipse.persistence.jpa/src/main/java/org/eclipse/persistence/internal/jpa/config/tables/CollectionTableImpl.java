/*
 * Copyright (c) 2013, 2024 Oracle and/or its affiliates. All rights reserved.
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

import org.eclipse.persistence.internal.jpa.config.columns.ForeignKeyImpl;
import org.eclipse.persistence.internal.jpa.config.columns.JoinColumnImpl;
import org.eclipse.persistence.internal.jpa.metadata.tables.CollectionTableMetadata;
import org.eclipse.persistence.jpa.config.CollectionTable;
import org.eclipse.persistence.jpa.config.ForeignKey;
import org.eclipse.persistence.jpa.config.JoinColumn;

/**
 * JPA scripting API implementation.
 *
 * @author Guy Pelletier
 * @since EclipseLink 2.5.1
 */
public class CollectionTableImpl extends AbstractTableImpl<CollectionTableMetadata, CollectionTable> implements CollectionTable {

    public CollectionTableImpl() {
        super(new CollectionTableMetadata());

        getMetadata().setJoinColumns(new ArrayList<>());
    }

    @Override
    public JoinColumn addJoinColumn() {
        JoinColumnImpl joinColumn = new JoinColumnImpl();
        getMetadata().getJoinColumns().add(joinColumn.getMetadata());
        return joinColumn;
    }

    @Override
    public ForeignKey setForeignKey() {
        ForeignKeyImpl foreignKey = new ForeignKeyImpl();
        getMetadata().setForeignKey(foreignKey.getMetadata());
        return foreignKey;
    }

}
