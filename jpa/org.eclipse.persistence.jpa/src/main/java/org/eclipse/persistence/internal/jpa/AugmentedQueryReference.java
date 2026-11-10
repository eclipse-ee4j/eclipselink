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
import jakarta.persistence.TypedQueryReference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.queries.DatabaseQuery;

/** Factory ownership and reference-local templates for augmented JPQL queries. */
public final class AugmentedQueryReference {
    private AugmentedQueryReference() {
    }

    public static <T> TypedQueryReference<T> owned(TypedQueryReference<T> reference, AbstractSession owner) {
        return new Typed<>(reference.getName(), reference.getResultType(), reference, owner, null);
    }

    public static StatementReference owned(StatementReference reference, AbstractSession owner) {
        return new StatementRef(reference.getName(), reference, owner, null);
    }

    /**
     * Return a private executable copy for an augmented reference, or {@code null}
     * when the caller must follow the ordinary named-query path.
     */
    static DatabaseQuery executable(Object reference, AbstractSession owner) {
        Objects.requireNonNull(reference);
        if (reference instanceof Owned owned) {
            if (owned.owner() != owner) {
                throw new IllegalArgumentException("The query reference belongs to a different persistence factory");
            }
            if (owned.template() != null) {
                return copy(owned.template());
            }
        }
        return null;
    }

    public static DatabaseQuery source(Object reference, AbstractSession owner) {
        DatabaseQuery template = executable(reference, owner);
        if (template != null) {
            return template;
        }
        String name = reference instanceof TypedQueryReference<?> typed ? typed.getName()
                : ((StatementReference) reference).getName();
        DatabaseQuery source = name == null ? null : owner.getQuery(name);
        if (source == null || source.getEJBQLString() == null) {
            throw new IllegalArgumentException("The reference must identify a named JPQL query in this factory");
        }
        return copy(source);
    }

    public static <T> TypedQueryReference<T> create(TypedQueryReference<?> original, Class<T> resultType,
            DatabaseQuery query, AbstractSession owner) {
        return new Typed<>(null, resultType, original, owner, copy(query));
    }

    public static StatementReference create(StatementReference original, DatabaseQuery query, AbstractSession owner) {
        return new StatementRef(null, original, owner, copy(query));
    }

    private static DatabaseQuery copy(DatabaseQuery query) {
        DatabaseQuery copy = (DatabaseQuery) query.clone();
        // Preparation and binding must not mutate the reference's parameter lists.
        copy.setArguments(new ArrayList<>(query.getArguments()));
        copy.setArgumentTypes(new ArrayList<>(query.getArgumentTypes()));
        copy.setArgumentValues(new ArrayList<>(query.getArgumentValues()));
        return copy;
    }

    private static <T> List<T> immutableList(List<T> values) {
        return values == null ? null : Collections.unmodifiableList(new ArrayList<>(values));
    }

    private interface Owned {
        AbstractSession owner();
        DatabaseQuery template();
    }

    private static final class Typed<T> extends TypedQueryReferenceImpl<T> implements Owned {
        private final AbstractSession owner;
        private final DatabaseQuery template;

        Typed(String name, Class<? extends T> type, TypedQueryReference<?> source, AbstractSession owner, DatabaseQuery template) {
            super(name, type, source.getHints(), source.getOptions(), source.getEntityGraphName(),
                    source.getParameterTypes(), source.getParameterNames(), source.getArguments());
            this.owner = owner;
            this.template = template;
        }

        @Override
        public AbstractSession owner() { return owner; }
        @Override
        public DatabaseQuery template() { return template; }
    }

    private static final class StatementRef implements StatementReference, Owned {
        private final String name;
        private final Map<String, Object> hints;
        private final Set<Statement.Option> options;
        private final List<Class<?>> parameterTypes;
        private final List<String> parameterNames;
        private final List<Object> arguments;
        private final AbstractSession owner;
        private final DatabaseQuery template;

        StatementRef(String name, StatementReference source, AbstractSession owner, DatabaseQuery template) {
            this.name = name;
            this.hints = source.getHints() == null ? Map.of()
                    : Collections.unmodifiableMap(new LinkedHashMap<>(source.getHints()));
            this.options = source.getOptions() == null ? Set.of()
                    : Collections.unmodifiableSet(new LinkedHashSet<>(source.getOptions()));
            this.parameterTypes = immutableList(source.getParameterTypes());
            this.parameterNames = immutableList(source.getParameterNames());
            this.arguments = immutableList(source.getArguments());
            this.owner = owner;
            this.template = template;
        }

        @Override
        public AbstractSession owner() { return owner; }
        @Override
        public DatabaseQuery template() { return template; }
        @Override
        public String getName() { return name; }
        @Override
        public Map<String, Object> getHints() { return hints; }
        @Override
        public Set<Statement.Option> getOptions() { return options; }
        @Override
        public List<Class<?>> getParameterTypes() { return parameterTypes; }
        @Override
        public List<String> getParameterNames() { return parameterNames; }
        @Override
        public List<Object> getArguments() { return arguments; }
    }
}
