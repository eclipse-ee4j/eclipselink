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
package org.eclipse.persistence.internal.jpa.config.queries;

import java.util.ArrayList;

import org.eclipse.persistence.internal.jpa.config.MetadataImpl;
import org.eclipse.persistence.internal.jpa.metadata.queries.ColumnResultMetadata;
import org.eclipse.persistence.internal.jpa.metadata.queries.ConstructorResultMetadata;
import org.eclipse.persistence.internal.jpa.metadata.queries.EntityResultMetadata;
import org.eclipse.persistence.internal.jpa.metadata.queries.SQLResultSetMappingMetadata;
import org.eclipse.persistence.jpa.config.ColumnResult;
import org.eclipse.persistence.jpa.config.ConstructorResult;
import org.eclipse.persistence.jpa.config.EntityResult;
import org.eclipse.persistence.jpa.config.SqlResultSetMapping;

/**
 * JPA scripting API implementation.
 *
 * @author Guy Pelletier
 * @since EclipseLink 2.5.1
 */
public class SqlResultSetMappingImpl extends MetadataImpl<SQLResultSetMappingMetadata> implements SqlResultSetMapping {

    public SqlResultSetMappingImpl() {
        super(new SQLResultSetMappingMetadata());

        getMetadata().setColumnResults(new ArrayList<ColumnResultMetadata>());
        getMetadata().setConstructorResults(new ArrayList<ConstructorResultMetadata>());
        getMetadata().setEntityResults(new ArrayList<EntityResultMetadata>());
    }

    public ColumnResult addColumnResult() {
        ColumnResultImpl columnResult = new ColumnResultImpl();
        getMetadata().getColumnResults().add(columnResult.getMetadata());
        return columnResult;
    }

    public ConstructorResult addConstructorResult() {
        ConstructorResultImpl constructorResult = new ConstructorResultImpl();
        getMetadata().getConstructorResults().add(constructorResult.getMetadata());
        return constructorResult;
    }

    public EntityResult addEntityResult() {
        EntityResultImpl entityResult = new EntityResultImpl();
        getMetadata().getEntityResults().add(entityResult.getMetadata());
        return entityResult;
    }

    public SqlResultSetMapping setName(String name) {
        getMetadata().setName(name);
        return this;
    }

}
