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

import org.eclipse.persistence.internal.jpa.config.columns.ForeignKeyImpl;
import org.eclipse.persistence.internal.jpa.config.columns.PrimaryKeyJoinColumnImpl;
import org.eclipse.persistence.internal.jpa.metadata.columns.PrimaryKeyForeignKeyMetadata;
import org.eclipse.persistence.internal.jpa.metadata.columns.PrimaryKeyJoinColumnMetadata;
import org.eclipse.persistence.internal.jpa.metadata.tables.SecondaryTableMetadata;
import org.eclipse.persistence.jpa.config.ForeignKey;
import org.eclipse.persistence.jpa.config.PrimaryKeyJoinColumn;
import org.eclipse.persistence.jpa.config.SecondaryTable;

/**
 * JPA scripting API implementation.
 *
 * @author Guy Pelletier
 * @since EclipseLink 2.5.1
 */
public class SecondaryTableImpl extends AbstractTableImpl<SecondaryTableMetadata, SecondaryTable> implements SecondaryTable {

    public SecondaryTableImpl() {
        super(new SecondaryTableMetadata());

        getMetadata().setPrimaryKeyJoinColumns(new ArrayList<PrimaryKeyJoinColumnMetadata>());
    }

    @Override
    public PrimaryKeyJoinColumn addPrimaryKeyJoinColumn() {
        PrimaryKeyJoinColumnImpl primaryKeyJoinColumn = new PrimaryKeyJoinColumnImpl();
        getMetadata().getPrimaryKeyJoinColumns().add(primaryKeyJoinColumn.getMetadata());
        return primaryKeyJoinColumn;
    }

    @Override
    public ForeignKey setPrimaryKeyForeignKey() {
        ForeignKeyImpl foreignKey = new ForeignKeyImpl();
        getMetadata().setPrimaryKeyForeignKey(new PrimaryKeyForeignKeyMetadata(foreignKey.getMetadata()));
        return foreignKey;
    }

}
