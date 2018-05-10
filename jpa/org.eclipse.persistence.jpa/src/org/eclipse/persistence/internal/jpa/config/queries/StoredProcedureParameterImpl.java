/*******************************************************************************
 * Copyright (c) 2013, 2018  Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Guy Pelletier - initial API and implementation
 ******************************************************************************/
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

    @Override
    public StoredProcedureParameter setJdbcType(Integer jdbcType) {
        getMetadata().setJdbcType(jdbcType);
        return this;
    }

    @Override
    public StoredProcedureParameter setJdbcTypeName(String jdbcTypeName) {
        getMetadata().setJdbcTypeName(jdbcTypeName);
        return this;
    }

    @Override
    public StoredProcedureParameter setMode(String mode) {
        getMetadata().setMode(mode);
        return this;
    }

    @Override
    public StoredProcedureParameter setName(String name) {
        getMetadata().setName(name);
        return this;
    }

    @Override
    public StoredProcedureParameter setOptional(Boolean optional) {
        getMetadata().setOptional(optional);
        return this;
    }

    @Override
    public StoredProcedureParameter setQueryParameter(String queryParameter) {
        getMetadata().setQueryParameter(queryParameter);
        return this;
    }

    @Override
    public StoredProcedureParameter setType(String type) {
        getMetadata().setTypeName(type);
        return this;
    }

}
