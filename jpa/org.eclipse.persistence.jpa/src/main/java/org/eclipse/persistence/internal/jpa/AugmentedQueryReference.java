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

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.queries.DatabaseQuery;

/** Factory ownership and independent registration for augmented JPQL references. */
public final class AugmentedQueryReference {
    private static final String JPQL_SOURCE = "eclipselink.criteria.augmented-jpql";

    private AugmentedQueryReference() {
    }

    public static <T> TypedQueryReference<T> owned(TypedQueryReference<T> reference, AbstractSession owner) {
        return new Typed<>(reference.getName(), reference.getResultType(), reference, owner);
    }

    public static StatementReference owned(StatementReference reference, AbstractSession owner) {
        return new StatementRef(reference.getName(), reference, owner);
    }

    public static DatabaseQuery source(Object reference, AbstractSession owner) {
        Objects.requireNonNull(reference);
        if (reference instanceof Owned owned && owned.owner() != owner) {
            throw new IllegalArgumentException("The query reference belongs to a different persistence factory");
        }
        String name = reference instanceof TypedQueryReference<?> typed ? typed.getName()
                : ((StatementReference) reference).getName();
        DatabaseQuery source = owner.getQuery(name);
        if (source == null || (source.getEJBQLString() == null && !Boolean.TRUE.equals(source.getProperty(JPQL_SOURCE)))) {
            throw new IllegalArgumentException("The reference must identify a named JPQL query in this factory");
        }
        return source;
    }

    public static <T> TypedQueryReference<T> register(TypedQueryReference<?> original, Class<T> resultType,
            DatabaseQuery query, AbstractSession owner) {
        String name = register(query, owner);
        return new Typed<>(name, resultType, original, owner);
    }

    public static StatementReference register(StatementReference original, DatabaseQuery query, AbstractSession owner) {
        return new StatementRef(register(query, owner), original, owner);
    }

    private static String register(DatabaseQuery query, AbstractSession owner) {
        String name = "eclipselink.augmented." + UUID.randomUUID();
        query.setProperty(JPQL_SOURCE, Boolean.TRUE);
        owner.addQuery(name, query, true);
        return name;
    }

    private interface Owned {
        AbstractSession owner();
    }

    private static final class Typed<T> extends TypedQueryReferenceImpl<T> implements Owned {
        private final AbstractSession owner;

        Typed(String name, Class<? extends T> type, TypedQueryReference<?> source, AbstractSession owner) {
            super(name, type, source.getHints(), source.getOptions(), source.getEntityGraphName(),
                    source.getParameterTypes(), source.getParameterNames(), source.getArguments());
            this.owner = owner;
        }

        @Override
        public AbstractSession owner() { return owner; }
    }

    private static final class StatementRef implements StatementReference, Owned {
        private final String name;
        private final StatementReference source;
        private final AbstractSession owner;

        StatementRef(String name, StatementReference source, AbstractSession owner) {
            this.name = name;
            this.source = source;
            this.owner = owner;
        }

        @Override
        public AbstractSession owner() { return owner; }
        @Override
        public String getName() { return name; }
        @Override
        public Map<String, Object> getHints() { return source.getHints(); }
        @Override
        public Set<Statement.Option> getOptions() { return source.getOptions(); }
        @Override
        public List<Class<?>> getParameterTypes() { return source.getParameterTypes(); }
        @Override
        public List<String> getParameterNames() { return source.getParameterNames(); }
        @Override
        public List<Object> getArguments() { return source.getArguments(); }
    }
}
