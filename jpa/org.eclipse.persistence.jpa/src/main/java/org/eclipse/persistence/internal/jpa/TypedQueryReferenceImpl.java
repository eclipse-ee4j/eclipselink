/*
 * Copyright (c) 1998, 2023 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 1998, 2023 IBM Corporation. All rights reserved.
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
//     12/21/2023: Tomas Kraus
//       - New Jakarta Persistence 3.2 Features
package org.eclipse.persistence.internal.jpa;

import java.util.Collections;
import java.util.Map;

import jakarta.persistence.TypedQueryReference;

/**
 * EclipseLink implementation of {@link TypedQueryReference} interface.
 * @param <R> an upper bound on the result type of the query
 */
class TypedQueryReferenceImpl<R> implements TypedQueryReference<R> {

    private final String name;
    private final Class<? extends R> resultType;
    private final Map<String, Object> hints;

    TypedQueryReferenceImpl(String name, Class<? extends R> resultType, Map<String, Object> hints) {
        this.name = name;
        this.resultType = resultType;
        // TypedQueryReference javadoc says nothing about null, but let's be safe
        this.hints = hints != null ? hints : Collections.emptyMap();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Class<? extends R> getResultType() {
        return resultType;
    }

    @Override
    public Map<String, Object> getHints() {
        return hints;
    }

}
