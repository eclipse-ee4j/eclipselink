/*
 * Copyright (c) 2022 Oracle and/or its affiliates. All rights reserved.
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
//     04/21/2022: Tomas Kraus
//       - Issue 1474: Update JPQL Grammar for Jakarta Persistence 2.2, 3.0 and 3.1
package org.eclipse.persistence.jpa.jpql.tools.resolver;

import org.eclipse.persistence.jpa.jpql.tools.spi.IType;
import org.eclipse.persistence.jpa.jpql.tools.spi.ITypeDeclaration;

/**
 * This {@link Resolver} is responsible to calculate the {@link IType} based on the type of the
 * math function expression. The valid type is a <code>Number</code> type.
 * <p>
 * The math function takes a numeric argument and returns a number (integer, float, or double)
 * of the same type as the argument to the function.
 */
public abstract class MathFunctionResolver extends Resolver {

    /**
     * The <code>CEILING</code> math function expression resolver.
     */
    public static final class Ceiling extends MathFunctionResolver {

        /**
         * Creates a new instance of <code>CEILING</code> function expression resolver.
         *
         * @param parent The parent {@link Resolver}, which is never <code>null</code>
         */
        public Ceiling(Resolver parent) {
            super(parent);
        }

    }

    /**
     * The <code>FLOOR</code> math function expression resolver.
     */
    public static final class Floor extends MathFunctionResolver {

        /**
         * Creates a new instance of <code>FLOOR</code> function expression resolver.
         *
         * @param parent The parent {@link Resolver}, which is never <code>null</code>
         */
        public Floor(Resolver parent) {
            super(parent);
        }

    }

    /**
     * The <code>ROUND</code> math function expression resolver.
     */
    public static final class Round extends MathFunctionResolver {

        /**
         * Creates a new instance of <code>ROUND</code> function expression resolver.
         *
         * @param parent The parent {@link Resolver}, which is never <code>null</code>
         */
        public Round(Resolver parent) {
            super(parent);
        }

    }

    /**
     * Creates a new instance of math function expression resolver.
     *
     * @param parent The parent {@link Resolver}, which is never <code>null</code>
     */
    public MathFunctionResolver(Resolver parent) {
        super(parent);
    }

    @Override
    protected IType buildType() {

        IType type = getParentType();

        // Anything else is an invalid type
        if (!getTypeHelper().isNumericType(type)) {
            type = getTypeHelper().objectType();
        }

        return type;
    }

    @Override
    protected ITypeDeclaration buildTypeDeclaration() {
        return getParentTypeDeclaration();
    }

}
