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
import org.eclipse.persistence.internal.jpa.metadata.queries.PLSQLComplexTypeMetadata;

/**
 * JPA scripting API implementation.
 *
 * @author Guy Pelletier
 * @since EclipseLink 2.5.1
 */
@SuppressWarnings("unchecked")
public abstract class AbstractPlsqlTypeImpl<T extends PLSQLComplexTypeMetadata, R> extends MetadataImpl<T> {

    public AbstractPlsqlTypeImpl(T t) {
        super(t);
    }

    public R setCompatibleType(String compatibleType) {
        getMetadata().setCompatibleType(compatibleType);
        return (R) this;
    }

    public R setJavaType(String javaType) {
        getMetadata().setJavaType(javaType);
        return (R) this;
    }

    public R setName(String name) {
        getMetadata().setName(name);
        return (R) this;
    }

}
