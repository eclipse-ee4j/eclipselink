/*
 * Copyright (c) 1998, 2026 Oracle and/or its affiliates. All rights reserved.
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

import jakarta.persistence.TypedQuery.Option;
import jakarta.persistence.TypedQueryReference;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import static java.util.Collections.emptyMap;

/**
 * EclipseLink implementation of {@link TypedQueryReference} interface.
 * @param <R> an upper bound on the result type of the query
 */
class TypedQueryReferenceImpl<R> implements TypedQueryReference<R> {

    private final String name;
    private final Class<? extends R> resultType;
    private final Map<String, Object> hints;
    private final List<Class<?>> parameterTypes;
    private final List<String> parameterNames;
    private final List<Object> arguments;
    private final Set<Option> options;
    private final String entityGraphName;

    TypedQueryReferenceImpl(String name, Class<? extends R> resultType, Map<String, Object> hints) {
        this(name, resultType, hints, Set.of(), null);
    }

    TypedQueryReferenceImpl(String name, Class<? extends R> resultType, Map<String, Object> hints,
            Set<Option> options, String entityGraphName) {
        this(name, resultType, hints, options, entityGraphName, null, null, null);
    }

    TypedQueryReferenceImpl(String name, Class<? extends R> resultType, Map<String, Object> hints,
            Set<Option> options, String entityGraphName, List<Class<?>> parameterTypes,
            List<String> parameterNames, List<Object> arguments) {
        this.name = name;
        this.resultType = resultType;
        this.hints = hints == null ? emptyMap() : Collections.unmodifiableMap(new LinkedHashMap<>(hints));
        this.options = options == null ? Set.of() : Collections.unmodifiableSet(new LinkedHashSet<>(options));
        this.entityGraphName = entityGraphName;
        this.parameterTypes = immutableList(parameterTypes);
        this.parameterNames = immutableList(parameterNames);
        this.arguments = immutableList(arguments);
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

    @Override
    public List<Class<?>> getParameterTypes() {
        return parameterTypes;
    }

    @Override
    public List<String> getParameterNames() {
        return parameterNames;
    }

    @Override
    public List<Object> getArguments() {
        return arguments;
    }

    @Override
    public Set<Option> getOptions() {
        return options;
    }

    @Override
    public String getEntityGraphName() {
        return entityGraphName;
    }

    private static <T> List<T> immutableList(List<T> values) {
        return values == null ? null : Collections.unmodifiableList(new ArrayList<>(values));
    }
}
