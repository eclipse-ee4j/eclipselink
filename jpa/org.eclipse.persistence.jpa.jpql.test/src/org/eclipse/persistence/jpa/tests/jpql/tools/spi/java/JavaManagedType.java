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

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.Access;
import javax.persistence.AccessType;
import org.eclipse.persistence.jpa.jpql.tools.spi.IManagedType;
import org.eclipse.persistence.jpa.jpql.tools.spi.IManagedTypeProvider;
import org.eclipse.persistence.jpa.jpql.tools.spi.IMapping;
import org.eclipse.persistence.jpa.jpql.tools.spi.IMappingBuilder;
import org.eclipse.persistence.jpa.jpql.tools.utility.iterable.SnapshotCloneIterable;

/**
 * The abstract definition of {@link IManagedType} defined for wrapping the runtime mapped class object.
 *
 * @version 2.5
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public abstract class JavaManagedType implements IManagedType {

    /**
     * The builder that is responsible to create the {@link IMapping} wrapping a persistent attribute
     * or property.
     */
    private IMappingBuilder<Member> mappingBuilder;

    /**
     * The cached {@link IMapping mappings} wrapping the persistent attributes and properties.
     */
    private Map<String, IMapping> mappings;

    /**
     * The provider of JPA managed types.
     */
    private final IManagedTypeProvider provider;

    /**
     * The cached {@link JavaType} representing the managed type.
     */
    private final JavaType type;

    /**
     * Creates a new <code>JavaManagedType</code>.
     *
     * @param provider The provider of JPA managed types
     * @param type The {@link org.eclipse.persistence.jpa.jpql.tools.spi.IType IType} wrapping the Java type
     * @param mappingBuilder The builder that is responsible to create the {@link IMapping} wrapping
     * a persistent attribute or property
     */
    public JavaManagedType(IManagedTypeProvider provider,
                           JavaType type,
                           IMappingBuilder<Member> mappingBuilder) {

        super();
        this.type           = type;
        this.provider       = provider;
        this.mappingBuilder = mappingBuilder;
    }

    protected IMapping buildMapping(Member member) {
        return mappingBuilder.buildMapping(this, member);
    }

    /**
     * {@inheritDoc}
     */
    public int compareTo(IManagedType managedType) {
        return getType().getName().compareTo(managedType.getType().getName());
    }

    protected AccessType getAccessType() {
        Access accessType = type.getType().getAnnotation(Access.class);
        if (accessType == null) {
            return AccessType.FIELD;
        }
        return accessType.value();
    }

    /**
     * {@inheritDoc}
     */
    public IMapping getMappingNamed(String name) {
        initializeMappings();
        return mappings.get(name);
    }

    /**
     * {@inheritDoc}
     */
    public IManagedTypeProvider getProvider() {
        return provider;
    }

    /**
     * {@inheritDoc}
     */
    public JavaType getType() {
        return type;
    }

    protected void initializeMappings() {
        if (mappings == null) {
            mappings = new HashMap<String, IMapping>();
            initializeMappings(type.getType(), getAccessType());
        }
    }

    protected void initializeMappings(Class<?> type, AccessType accessType) {

        if (type == Object.class) {
            return;
        }

        if (accessType == AccessType.FIELD) {
            for (Field field : type.getDeclaredFields()) {
                if (isPersistentAttribute(field)) {
                    mappings.put(field.getName(), buildMapping(field));
                }
            }
        }
        else {
            for (Method method : type.getDeclaredMethods()) {
                if (isProperty(method)) {
                    mappings.put(method.getName(), buildMapping(method));
                }
            }
        }

        initializeMappings(type.getSuperclass(), accessType);
    }

    protected boolean isPersistentAttribute(Field field) {

        int modifiers = field.getModifiers();

        return !Modifier.isStatic(modifiers) &&
               !Modifier.isPublic(modifiers);
    }

    protected boolean isProperty(Method method) {

        int modifiers = method.getModifiers();
        String name = method.getName();

        return // Name convention
               (name.startsWith("get") ||
                name.startsWith("is")) &&

                // Return type and parameter
                method.getReturnType() != Void.class &&
                method.getParameterTypes().length == 0 &&

                // Modifiers
               !Modifier.isStatic(modifiers) &&
               !Modifier.isNative(modifiers) &&
               (Modifier.isPublic(modifiers) ||
                Modifier.isProtected(modifiers));
    }

    /**
     * {@inheritDoc}
     */
    public final Iterable<IMapping> mappings() {
        initializeMappings();
        return new SnapshotCloneIterable<IMapping>(mappings.values());
    }
}
