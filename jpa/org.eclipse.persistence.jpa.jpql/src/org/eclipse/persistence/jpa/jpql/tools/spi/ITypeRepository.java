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

import org.eclipse.persistence.jpa.jpql.tools.TypeHelper;

/**
 * The external representation of the repository of Java types, which gives access to the
 * application's classes.
 * <p>
 * <b>Important</b>: {@link IType#UNRESOLVABLE_TYPE} has to be handled by the implementer, which
 * has to be done in {@link #getType(String)}. It indicates the type is unresolvable or simply
 * unknown.
 * <p>
 * Provisional API: This interface is part of an interim API that is still under development and
 * expected to change significantly before reaching stability. It is available at this early stage
 * to solicit feedback from pioneering adopters on the understanding that any code that uses this
 * API will almost certainly be broken (repeatedly) as the API evolves.
 *
 * @version 2.3
 * @since 2.3
 * @author Pascal Filion
 */
public interface ITypeRepository extends IExternalForm {

    /**
     * Returns the {@link IType} representing the possible given enum constant. If the given value
     * does not represent an enum constant, then <code>null</code> is returned.
     *
     * @param enumTypeName The fully qualified enum type with the constant
     * @return The external form for the given Enum type or <code>null</code> if none exists
     */
    IType getEnumType(String enumTypeName);

    /**
     * Retrieves the external type for the given Java type.
     *
     * @param type The Java type to wrap with an external form
     * @return The external form of the given type
     */
    IType getType(Class<?> type);

    /**
     * Retrieves the external class for the given fully qualified class name.
     *
     * @param typeName The fully qualified class name of the class to retrieve
     * @return The external form of the class to retrieve
     */
    IType getType(String typeName);

    /**
     * Returns a helper that gives access to the most common {@link IType types}.
     *
     * @return A helper containing a collection of methods related to {@link IType}
     */
    TypeHelper getTypeHelper();
}
