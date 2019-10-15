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

import org.eclipse.persistence.internal.jpa.metadata.queries.NamedStoredProcedureQueryMetadata;
import org.eclipse.persistence.internal.jpa.metadata.queries.StoredProcedureParameterMetadata;
import org.eclipse.persistence.jpa.config.StoredProcedureParameter;

/**
 * JPA scripting API implementation.
 *
 * @author Guy Pelletier
 * @since EclipseLink 2.5.1
 */
@SuppressWarnings("unchecked")
public abstract class AbstractStoredQueryImpl<T extends NamedStoredProcedureQueryMetadata, R> extends AbstractNamedQueryImpl<T, R> {

    public AbstractStoredQueryImpl(T t) {
        super(t);

        getMetadata().setParameters(new ArrayList<StoredProcedureParameterMetadata>());
    }

    public StoredProcedureParameter addParameter() {
        StoredProcedureParameterImpl parameter = new StoredProcedureParameterImpl();
        getMetadata().getParameters().add(parameter.getMetadata());
        return parameter;
    }

    public R setCallByIndex(Boolean callByIndex) {
        getMetadata().setCallByIndex(callByIndex);
        return (R) this;
    }
}
