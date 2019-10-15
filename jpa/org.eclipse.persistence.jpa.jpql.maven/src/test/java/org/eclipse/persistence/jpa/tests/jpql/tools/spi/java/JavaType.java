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
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import org.eclipse.persistence.jpa.jpql.ExpressionTools;
import org.eclipse.persistence.jpa.jpql.tools.TypeHelper;
import org.eclipse.persistence.jpa.jpql.tools.spi.IConstructor;
import org.eclipse.persistence.jpa.jpql.tools.spi.IType;
import org.eclipse.persistence.jpa.jpql.tools.spi.ITypeDeclaration;
import org.eclipse.persistence.jpa.jpql.tools.spi.ITypeRepository;
import org.eclipse.persistence.jpa.jpql.tools.utility.iterable.SnapshotCloneIterable;

/**
 * The concrete implementation of {@link IType} that is wrapping a Java type.
 *
 * @version 2.5
 * @since 2.3
 * @author Pascal Filion
 */
public class JavaType implements IType {

    /**
     * The cached {@link IConstructor IConstructors}.
     */
    private Collection<IConstructor> constructors;

    /**
     * The list of names for the {@link Enum}'s constants otherwise an empty array.
     */
    private String[] enumConstants;

    /**
     * The actual Java type.
     */
    private Class<?> type;

    /**
     * The cached {@link ITypeDeclaration} for this {@link IType}.
     */
    private ITypeDeclaration typeDeclaration;

    /**
     * The fully qualified name of the Java type.
     */
    private String typeName;

    /**
     * The external form of a type repository.
     */
    private ITypeRepository typeRepository;

    /**
     * Creates a new <code>JavaType</code>.
     *
     * @param typeRepository The external form of a type repository
     * @param type The actual Java type wrapped by this class
     */
    public JavaType(ITypeRepository typeRepository, Class<?> type) {
        this(typeRepository, type.getName());
        this.type = type;
    }

    /**
     * Creates a new <code>JavaType</code>.
     *
     * @param typeRepository The external form of a type repository
     * @param typeName The fully qualified name of the Java type
     */
    public JavaType(ITypeRepository typeRepository, String typeName) {
        super();
        this.typeName       = typeName;
        this.typeRepository = typeRepository;
    }

    protected IConstructor buildConstructor(Constructor<?> constructor) {
        return new JavaConstructor(this, constructor);
    }

    protected Collection<IConstructor> buildConstructors() {

        if (type == null) {
            return Collections.emptyList();
        }

        Constructor<?>[] javaConstructors = type.getDeclaredConstructors();
        Collection<IConstructor> constructors = new ArrayList<IConstructor>(javaConstructors.length);

        for (Constructor<?> javaConstructor : javaConstructors) {
            constructors.add(buildConstructor(javaConstructor));
        }

        return constructors;
    }

    protected String[] buildEnumConstants() {

        if ((type == null) || !type.isEnum()) {
            return ExpressionTools.EMPTY_STRING_ARRAY;
        }

        Object[] enumConstants = type.getEnumConstants();
        String[] names = new String[enumConstants.length];

        for (int index = enumConstants.length; --index >= 0; ) {
            names[index] = ((Enum<?>) enumConstants[index]).name();
        }

        return names;
    }

    /**
     * {@inheritDoc}
     */
    public Iterable<IConstructor> constructors() {
        if (constructors == null) {
            constructors = buildConstructors();
        }
        return new SnapshotCloneIterable<IConstructor>(constructors);
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(IType type) {
        return (this == type) || typeName.equals(type.getName());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object object) {
        return equals((IType) object);
    }

    /**
     * {@inheritDoc}
     */
    public String[] getEnumConstants() {
        if (enumConstants == null) {
            enumConstants = buildEnumConstants();
        }
        return enumConstants;
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return typeName;
    }

    /**
     * Returns the encapsulated {@link Class}, which is the actual type.
     *
     * @return The actual Java type, if <code>null</code> is returned; then the class could not be resolved
     */
    public Class<?> getType() {
        return type;
    }

    /**
     * {@inheritDoc}
     */
    public ITypeDeclaration getTypeDeclaration() {
        if (typeDeclaration == null) {
            typeDeclaration = new JavaTypeDeclaration(
                typeRepository,
                this,
                null,
                (type != null) ? type.isArray() : false
            );
        }
        return typeDeclaration;
    }

    /**
     * Returns the type repository for the application.
     *
     * @return The repository of {@link IType ITypes}
     */
    protected ITypeRepository getTypeRepository() {
        return typeRepository;
    }

    /**
     * {@inheritDoc}
     */
    public boolean hasAnnotation(Class<? extends Annotation> annotationType) {
        return (type != null) && type.isAnnotationPresent(annotationType);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return typeName.hashCode();
    }

    /**
     * {@inheritDoc}
     */
    public boolean isAssignableTo(IType otherType) {

        if (equals(otherType)) {
            return true;
        }

        if (!isResolvable() || !otherType.isResolvable()) {
            return false;
        }

        // Make sure both types are not primitives since isAssignableFrom() does not work.
        // For instance long and Long can't be compared but they are valid for JPQL query
        TypeHelper typeHelper = typeRepository.getTypeHelper();
        IType thisType = typeHelper.convertPrimitive(this);
        otherType = typeHelper.convertPrimitive(otherType);

        Class<?> thisClass  = ((JavaType) thisType) .type;
        Class<?> otherClass = ((JavaType) otherType).type;

        return otherClass.isAssignableFrom(thisClass);
    }

    /**
     * {@inheritDoc}
     */
    public boolean isEnum() {
        return (type != null) && type.isEnum();
    }

    /**
     * {@inheritDoc}
     */
    public boolean isResolvable() {
        return (type != null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return typeName;
    }
}
