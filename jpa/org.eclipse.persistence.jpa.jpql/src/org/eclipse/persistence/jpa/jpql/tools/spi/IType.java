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
 * The external representation of a Java type.
 * <p>
 * Provisional API: This interface is part of an interim API that is still under development and
 * expected to change significantly before reaching stability. It is available at this early stage
 * to solicit feedback from pioneering adopters on the understanding that any code that uses this
 * API will almost certainly be broken (repeatedly) as the API evolves.
 *
 * @version 2.5
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public interface IType extends IExternalForm {

    /**
     * Special constant used to specify this {@link IType} represents an unresolvable type, or simply
     * an unknown type. This has to be handled by {@link ITypeRepository#getType(String)}.
     */
    String UNRESOLVABLE_TYPE = "UNRESOLVABLE_TYPE";

    /**
     * Returns the external representation of the Java class's constructors. All public, protected,
     * default (package) access, and private constructors should be included.
     *
     * @return The declared constructors
     */
    Iterable<IConstructor> constructors();

    /**
     * Determines whether the given type represents the same Java type thank this
     * one.
     * <p>
     * Note: {@link Object#hashCode()} needs to be overridden.
     *
     * @param type The type to compare with this one
     * @return <code>true</code> if the given type and this one represents the
     * same Java type; <code>false</code> otherwise
     */
    boolean equals(IType type);

    /**
     * If this {@link IType} represents an {@link Enum} type, then this method should returns the
     * name of the constants.
     *
     * @return The name of the <code>Enum</code> constant or an empty list if the type is not an
     * <code>Enum</code>
     */
    String[] getEnumConstants();

    /**
     * Returns the fully qualified class name.
     *
     * @return The name of the class represented by this one
     */
    String getName();

    /**
     * Returns the declaration of the Java class, which gives the information about type parameters,
     * dimensionality, etc.
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
     * Determines whether this type is an instance of the given type.
     *
     * @param type The type used to determine if the class represented by this external form is an
     * instance of with one
     * @return <code>true</code> if this type is an instance of the given type; <code>false</code>
     * otherwise
     */
    boolean isAssignableTo(IType type);

    /**
     * Determines whether this {@link IType} represents an {@link Enum}.
     *
     * @return <code>true</code> if this is an {@link Enum}; <code>false</code> otherwise
     */
    boolean isEnum();

    /**
     * Determines whether this Java type actually exists.
     *
     * @return <code>true</code> if the actual Java type can be located on the application's class
     * path; <code>false</code> if it could not be found
     */
    boolean isResolvable();
}
