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

import org.eclipse.persistence.internal.jpa.config.MetadataImpl;
import org.eclipse.persistence.internal.jpa.metadata.queries.PLSQLParameterMetadata;
import org.eclipse.persistence.jpa.config.PlsqlParameter;

/**
 * JPA scripting API implementation.
 *
 * @author Guy Pelletier
 * @since EclipseLink 2.5.1
 */
public class PlsqlParameterImpl extends MetadataImpl<PLSQLParameterMetadata> implements PlsqlParameter {

    public PlsqlParameterImpl() {
        super(new PLSQLParameterMetadata());
    }

    @Override
    public PlsqlParameter setDatabaseType(String databaseType) {
        getMetadata().setDatabaseType(databaseType);
        return this;
    }

    @Override
    public PlsqlParameter setDirection(String direction) {
        getMetadata().setDirection(direction);
        return this;
    }

    @Override
    public PlsqlParameter setLength(Integer length) {
        getMetadata().setLength(length);
        return this;
    }

    @Override
    public PlsqlParameter setName(String name) {
        getMetadata().setName(name);
        return this;
    }

    @Override
    public PlsqlParameter setOptional(Boolean optional) {
        getMetadata().setOptional(optional);
        return this;
    }

    @Override
    public PlsqlParameter setPrecision(Integer precision) {
        getMetadata().setPrecision(precision);
        return this;
    }

    @Override
    public PlsqlParameter setQueryParameter(String queryParameter) {
        getMetadata().setQueryParameter(queryParameter);
        return this;
    }

    @Override
    public PlsqlParameter setScale(Integer scale) {
        getMetadata().setScale(scale);
        return this;
    }

}
