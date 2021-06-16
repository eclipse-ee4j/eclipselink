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

    @Override
    public ColumnResult addColumnResult() {
        ColumnResultImpl columnResult = new ColumnResultImpl();
        getMetadata().getColumnResults().add(columnResult.getMetadata());
        return columnResult;
    }

    @Override
    public ConstructorResult addConstructorResult() {
        ConstructorResultImpl constructorResult = new ConstructorResultImpl();
        getMetadata().getConstructorResults().add(constructorResult.getMetadata());
        return constructorResult;
    }

    @Override
    public EntityResult addEntityResult() {
        EntityResultImpl entityResult = new EntityResultImpl();
        getMetadata().getEntityResults().add(entityResult.getMetadata());
        return entityResult;
    }

    @Override
    public SqlResultSetMapping setName(String name) {
        getMetadata().setName(name);
        return this;
    }

}
