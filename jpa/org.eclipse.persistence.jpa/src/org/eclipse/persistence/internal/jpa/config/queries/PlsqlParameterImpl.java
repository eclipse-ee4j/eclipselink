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

    public PlsqlParameter setDatabaseType(String databaseType) {
        getMetadata().setDatabaseType(databaseType);
        return this;
    }

    public PlsqlParameter setDirection(String direction) {
        getMetadata().setDirection(direction);
        return this;
    }

    public PlsqlParameter setLength(Integer length) {
        getMetadata().setLength(length);
        return this;
    }

    public PlsqlParameter setName(String name) {
        getMetadata().setName(name);
        return this;
    }

    public PlsqlParameter setOptional(Boolean optional) {
        getMetadata().setOptional(optional);
        return this;
    }

    public PlsqlParameter setPrecision(Integer precision) {
        getMetadata().setPrecision(precision);
        return this;
    }

    public PlsqlParameter setQueryParameter(String queryParameter) {
        getMetadata().setQueryParameter(queryParameter);
        return this;
    }

    public PlsqlParameter setScale(Integer scale) {
        getMetadata().setScale(scale);
        return this;
    }

}
