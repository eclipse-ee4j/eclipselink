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

import org.eclipse.persistence.internal.jpa.metadata.queries.NamedNativeQueryMetadata;
import org.eclipse.persistence.jpa.config.NamedNativeQuery;

/**
 * JPA scripting API implementation.
 *
 * @author Guy Pelletier
 * @since EclipseLink 2.5.1
 */
public class NamedNativeQueryImpl extends AbstractQueryImpl<NamedNativeQueryMetadata, NamedNativeQuery> implements NamedNativeQuery {

    public NamedNativeQueryImpl() {
        super(new NamedNativeQueryMetadata());
    }

    @Override
    public NamedNativeQuery setResultClass(String resultClass) {
        getMetadata().setResultClassName(resultClass);
        return this;
    }

    @Override
    public NamedNativeQuery setResultSetMapping(String resultSetMapping) {
        getMetadata().setResultSetMapping(resultSetMapping);
        return this;
    }

}
