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
import org.eclipse.persistence.internal.jpa.metadata.queries.StoredProcedureParameterMetadata;
import org.eclipse.persistence.jpa.config.StoredProcedureParameter;

/**
 * JPA scripting API implementation.
 *
 * @author Guy Pelletier
 * @since EclipseLink 2.5.1
 */
public class StoredProcedureParameterImpl extends MetadataImpl<StoredProcedureParameterMetadata> implements StoredProcedureParameter {

    public StoredProcedureParameterImpl() {
        super(new StoredProcedureParameterMetadata());
    }

    public StoredProcedureParameter setJdbcType(Integer jdbcType) {
        getMetadata().setJdbcType(jdbcType);
        return this;
    }

    public StoredProcedureParameter setJdbcTypeName(String jdbcTypeName) {
        getMetadata().setJdbcTypeName(jdbcTypeName);
        return this;
    }

    public StoredProcedureParameter setMode(String mode) {
        getMetadata().setMode(mode);
        return this;
    }

    public StoredProcedureParameter setName(String name) {
        getMetadata().setName(name);
        return this;
    }

    public StoredProcedureParameter setOptional(Boolean optional) {
        getMetadata().setOptional(optional);
        return this;
    }

    public StoredProcedureParameter setQueryParameter(String queryParameter) {
        getMetadata().setQueryParameter(queryParameter);
        return this;
    }

    public StoredProcedureParameter setType(String type) {
        getMetadata().setTypeName(type);
        return this;
    }

}
