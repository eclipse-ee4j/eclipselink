/*
 * Copyright (c) 2013, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Guy Pelletier - initial API and implementation
package org.eclipse.persistence.internal.jpa.config.tables;

import java.util.ArrayList;

import org.eclipse.persistence.internal.jpa.config.columns.ForeignKeyImpl;
import org.eclipse.persistence.internal.jpa.config.columns.JoinColumnImpl;
import org.eclipse.persistence.internal.jpa.metadata.columns.JoinColumnMetadata;
import org.eclipse.persistence.internal.jpa.metadata.tables.JoinTableMetadata;
import org.eclipse.persistence.jpa.config.ForeignKey;
import org.eclipse.persistence.jpa.config.JoinColumn;
import org.eclipse.persistence.jpa.config.JoinTable;

/**
 * JPA scripting API implementation.
 *
 * @author Guy Pelletier
 * @since EclipseLink 2.5.1
 */
public class JoinTableImpl extends AbstractTableImpl<JoinTableMetadata, JoinTable> implements JoinTable {

    public JoinTableImpl() {
        super(new JoinTableMetadata());

        getMetadata().setJoinColumns(new ArrayList<JoinColumnMetadata>());
        getMetadata().setInverseJoinColumns(new ArrayList<JoinColumnMetadata>());
    }

    @Override
    public JoinColumn addInverseJoinColumn() {
        JoinColumnImpl joinColumn = new JoinColumnImpl();
        getMetadata().getInverseJoinColumns().add(joinColumn.getMetadata());
        return joinColumn;
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

    @Override
    public ForeignKey setInverseForeignKey() {
        ForeignKeyImpl foreignKey = new ForeignKeyImpl();
        getMetadata().setInverseForeignKey(foreignKey.getMetadata());
        return foreignKey;
    }

}
