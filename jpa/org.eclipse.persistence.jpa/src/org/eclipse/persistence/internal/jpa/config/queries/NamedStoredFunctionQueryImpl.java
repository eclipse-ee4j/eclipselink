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

import org.eclipse.persistence.internal.jpa.metadata.queries.NamedStoredFunctionQueryMetadata;
import org.eclipse.persistence.jpa.config.NamedStoredFunctionQuery;
import org.eclipse.persistence.jpa.config.StoredProcedureParameter;

/**
 * JPA scripting API implementation.
 *
 * @author Guy Pelletier
 * @since EclipseLink 2.5.1
 */
public class NamedStoredFunctionQueryImpl extends AbstractStoredQueryImpl<NamedStoredFunctionQueryMetadata, NamedStoredFunctionQuery> implements NamedStoredFunctionQuery {

    public NamedStoredFunctionQueryImpl() {
        super(new NamedStoredFunctionQueryMetadata());
    }

    @Override
    public NamedStoredFunctionQuery setFunctionName(String functionName) {
        getMetadata().setProcedureName(functionName);
        return null;
    }

    @Override
    public NamedStoredFunctionQuery setResultSetMapping(String resultSetMapping) {
        getMetadata().setResultSetMapping(resultSetMapping);
        return this;
    }

    @Override
    public StoredProcedureParameter setReturnParameter() {
        StoredProcedureParameterImpl parameter = new StoredProcedureParameterImpl();
        getMetadata().setReturnParameter(parameter.getMetadata());
        return parameter;
    }

}
