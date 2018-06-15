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
import org.eclipse.persistence.internal.jpa.metadata.tables.UniqueConstraintMetadata;
import org.eclipse.persistence.jpa.config.UniqueConstraint;

/**
 * JPA scripting API implementation.
 *
 * @author Guy Pelletier
 * @since EclipseLink 2.5.1
 */
public class UniqueConstraintImpl extends MetadataImpl<UniqueConstraintMetadata> implements UniqueConstraint {

    public UniqueConstraintImpl() {
        super(new UniqueConstraintMetadata());
        getMetadata().setColumnNames(new ArrayList<String>());
    }

    @Override
    public UniqueConstraint addColumnName(String columnName) {
        getMetadata().getColumnNames().add(columnName);
        return this;
    }

    @Override
    public UniqueConstraint setName(String name) {
        getMetadata().setName(name);
        return this;
    }

}
