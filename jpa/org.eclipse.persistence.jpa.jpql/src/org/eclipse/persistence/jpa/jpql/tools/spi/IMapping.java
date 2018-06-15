/*
 * Copyright (c) 2006, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation
//
package org.eclipse.persistence.jpa.jpql.tools.spi;

import java.lang.annotation.Annotation;

/**
 * The external representation of a mapping, which represents a single persistence property
 * of a managed type.
 * <p>
 * Provisional API: This interface is part of an interim API that is still under development and
 * expected to change significantly before reaching stability. It is available at this early stage
 * to solicit feedback from pioneering adopters on the understanding that any code that uses this
 * API will almost certainly be broken (repeatedly) as the API evolves.
 *
 * @see IManagedType
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
public interface IMapping extends IExternalForm,
                                  Comparable<IMapping> {

    /**
     * Returns the type of this mapping.
     *
     * @return One of the supported mapping type, which is one of the constants defined in {@link
     * org.eclipse.persistence.jpa.jpql.tools.spi.IMappingType IMappingType} when the provider only
     * supports generic JPA
     */
    int getMappingType();

    /**
     * Returns the name of the persistence property represented by this mapping.
     *
     * @return The name of this mapping
     */
    String getName();

    /**
     * Returns the parent managed type owning this mapping.
     *
     * @return The parent of this mapping
     */
    IManagedType getParent();

    /**
     * Returns the type of this mapping. If this mapping is a relationship mapping, the parameter
     * type of the collection is returned.
     * <p>
     * <code>@OneToMany<br>private Collection&lt;Employee&gt; employees;</code>
     * <p>
     * "Employee" is the type. To retrieve {@link java.util.Collection}, {@link #getTypeDeclaration()}
     * needs to be used, its type will be {@link java.util.Collection} and it's generic type will be
     * <code>Employee</code>.
     *
     * @return The external form representing the type of this mapping
     */
    IType getType();

    /**
     * Returns the declaration of the Java class, which gives the information about type parameters,
     * dimensionality, etc.
     * <p>
     * <code>@OneToMany<br>private Collection&lt;Employee&gt; employees;</code>
     * <p>
     * "Collection&lt;Employee&gt;" is the type declaration.
     *
     * @return The external form of the class' type declaration
     */
    ITypeDeclaration getTypeDeclaration();

    /**
     * Determines whether the given annotation is present on this type.
     *
     * @param annotationType The class of the annotation
     * @return <code>true</code> if the annotation is defined on this type; <code>false</code>
     * otherwise
     */
    boolean hasAnnotation(Class<? extends Annotation> annotationType);

    /**
     * Determines whether this {@link IMapping} is a collection type mapping.
     *
     * @return <code>true</code> if this {@link IMapping} is a collection mapping;
     * <code>false</code> otherwise
     * @since 2.4
     */
    boolean isCollection();

    /**
     * Determines whether this {@link IMapping} is a property type mapping.
     *
     * @return <code>true</code> if this {@link IMapping} is a property mapping; <code>false</code>
     * otherwise
     * @since 2.4
     */
    boolean isProperty();

    /**
     * Determines whether this {@link IMapping} is a relationship type mapping.
     *
     * @return <code>true</code> if this {@link IMapping} is a relationship mapping;
     * <code>false</code> otherwise
     * @since 2.4
     */
    boolean isRelationship();

    /**
     * Determines whether this {@link IMapping} is a transient mapping.
     *
     * @return <code>true</code> if this {@link IMapping} is a transient mapping;
     * <code>false</code> otherwise
     * @since 2.4
     */
    boolean isTransient();
}
