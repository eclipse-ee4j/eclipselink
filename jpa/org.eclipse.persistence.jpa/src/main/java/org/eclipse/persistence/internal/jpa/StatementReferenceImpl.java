/*
 * Copyright (c) 2026 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */
package org.eclipse.persistence.internal.jpa;

import jakarta.persistence.Statement;
import jakarta.persistence.StatementReference;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Immutable reference to a named database statement. */
final class StatementReferenceImpl implements StatementReference {
    private final String name;
    private final Map<String, Object> hints;
    private final Set<Statement.Option> options;

    StatementReferenceImpl(String name, Map<String, Object> hints, Set<Statement.Option> options) {
        this.name = name;
        this.hints = hints == null ? Map.of() : Collections.unmodifiableMap(new LinkedHashMap<>(hints));
        this.options = options == null ? Set.of() : Collections.unmodifiableSet(new LinkedHashSet<>(options));
    }

    @Override
    public String getName() { return name; }
    @Override
    public Map<String, Object> getHints() { return hints; }
    @Override
    public Set<Statement.Option> getOptions() { return options; }
    @Override
    public List<Class<?>> getParameterTypes() { return null; }
    @Override
    public List<String> getParameterNames() { return null; }
    @Override
    public List<Object> getArguments() { return null; }
}
