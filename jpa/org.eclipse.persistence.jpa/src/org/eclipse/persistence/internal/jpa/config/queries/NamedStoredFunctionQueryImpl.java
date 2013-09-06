/*******************************************************************************
 * Copyright (c) 2013 Oracle and/or its affiliates. All rights reserved.
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

    public NamedStoredFunctionQuery setFunctionName(String functionName) {
        getMetadata().setProcedureName(functionName);
        return null;
    }

    public NamedStoredFunctionQuery setResultSetMapping(String resultSetMapping) {
        getMetadata().setResultSetMapping(resultSetMapping);
        return this;
    }

    public StoredProcedureParameter setReturnParameter() {
        StoredProcedureParameterImpl parameter = new StoredProcedureParameterImpl();
        getMetadata().setReturnParameter(parameter.getMetadata());
        return parameter;
    }

}
