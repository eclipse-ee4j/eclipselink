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
package org.eclipse.persistence.internal.jpa.config.queries;

import java.util.ArrayList;

import org.eclipse.persistence.internal.jpa.config.MetadataImpl;
import org.eclipse.persistence.internal.jpa.metadata.queries.EntityResultMetadata;
import org.eclipse.persistence.internal.jpa.metadata.queries.FieldResultMetadata;
import org.eclipse.persistence.jpa.config.EntityResult;
import org.eclipse.persistence.jpa.config.FieldResult;

/**
 * JPA scripting API implementation.
 *
 * @author Guy Pelletier
 * @since EclipseLink 2.5.1
 */
public class EntityResultImpl extends MetadataImpl<EntityResultMetadata> implements EntityResult {

    public EntityResultImpl() {
        super(new EntityResultMetadata());

        getMetadata().setFieldResults(new ArrayList<FieldResultMetadata>());
    }

    @Override
    public FieldResult addFieldResult() {
        FieldResultImpl fieldResult = new FieldResultImpl();
        getMetadata().getFieldResults().add(fieldResult.getMetadata());
        return fieldResult;
    }

    @Override
    public EntityResult setDiscriminatorColumn(String discriminatorColumn) {
        getMetadata().setDiscriminatorColumn(discriminatorColumn);
        return this;
    }

    @Override
    public EntityResult setEntityClass(String entityClass) {
        getMetadata().setEntityClassName(entityClass);
        return this;
    }

}
