/*
 * Copyright (c) 2006, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation
//
package org.eclipse.persistence.jpa.tests.jpql.tools.spi.java;

import java.lang.annotation.Annotation;
import java.lang.reflect.Member;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import org.eclipse.persistence.jpa.jpql.ExpressionTools;
import org.eclipse.persistence.jpa.jpql.tools.spi.IEntity;
import org.eclipse.persistence.jpa.jpql.tools.spi.IManagedTypeProvider;
import org.eclipse.persistence.jpa.jpql.tools.spi.IManagedTypeVisitor;
import org.eclipse.persistence.jpa.jpql.tools.spi.IMappingBuilder;
import org.eclipse.persistence.jpa.jpql.tools.spi.IQuery;

/**
 * The concrete implementation of {@link IEntity} that is wrapping the runtime representation of a
 * JPA entity.
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
public class JavaEntity extends JavaManagedType
                        implements IEntity {

    /**
     * The entity name, if it was defined in &#64;Entity.name() or the short class name.
     */
    private String name;

    /**
     * The list of named queries mapped by their name.
     */
    private Map<String, IQuery> queries;

    /**
     * Creates a new <code>JavaEntity</code>.
     *
     * @param provider The provider of JPA managed types
     * @param type The {@link org.eclipse.persistence.jpa.jpql.tools.spi.IType IType} wrapping the Java type
     * @param mappingBuilder The builder that is responsible to create the {@link org.eclipse.
     * persistence.jpa.jpql.spi.IMapping IMapping} wrapping a persistent attribute or property
     */
    public JavaEntity(IManagedTypeProvider provider,
                      JavaType type,
                      IMappingBuilder<Member> mappingBuilder) {

        super(provider, type, mappingBuilder);
    }

    /**
     * {@inheritDoc}
     */
    public void accept(IManagedTypeVisitor visitor) {
        visitor.visit(this);
    }

    protected String buildName() {

        Class<?> type = getType().getType();
        Entity entity = type.getAnnotation(Entity.class);
        String entityName = entity.name();

        if (ExpressionTools.stringIsEmpty(entityName)) {
            name = type.getSimpleName();
        }

        return name;
    }

    protected Map<String, IQuery> buildQueries() {

        Map<String, IQuery> queries = new HashMap<String, IQuery>();

        try {
            Class<?> type = getType().getType();
            Annotation[] annotations = type.getAnnotations();
            NamedQueries namedQueries = getAnnotation(annotations, NamedQueries.class);

            if (namedQueries != null) {
                for (NamedQuery namedQuery : namedQueries.value()) {
                    IQuery query = buildQuery(namedQuery);
                    queries.put(namedQuery.name(), query);
                }
            }
            else {
                NamedQuery namedQuery = getAnnotation(annotations, NamedQuery.class);
                if (namedQuery != null) {
                    IQuery query = buildQuery(namedQuery);
                    queries.put(namedQuery.name(), query);
                }
            }
        }
        catch (Exception e) {
            // Ignore
        }

        return queries;
    }

    protected IQuery buildQuery(NamedQuery namedQuery) {
        return new JavaQuery(getProvider(), namedQuery.query());
    }

    @SuppressWarnings("unchecked")
    protected <T extends Annotation> T getAnnotation(Annotation[] annotations,
                                                     Class<T> annotationType) {

        for (Annotation annotation : annotations) {
            if (annotationType == annotation.annotationType()) {
                return (T) annotation;
            }
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        if (name == null) {
            name = buildName();
        }
        return name;
    }

    /**
     * {@inheritDoc}
     */
    public IQuery getNamedQuery(String queryName) {
        initializeQueries();
        return queries.get(queryName);
    }

    protected boolean hasAnnotation(Annotation[] annotations, String annotationType) {
        for (Annotation annotation : annotations) {
            if (annotationType.equals(annotation.annotationType().getName())) {
                return true;
            }
        }
        return false;
    }

    protected void initializeQueries() {
        if (queries == null) {
            queries = buildQueries();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return getName();
    }
}
