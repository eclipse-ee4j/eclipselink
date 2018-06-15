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
package org.eclipse.persistence.jpa.jpql.tools.resolver;

import org.eclipse.persistence.jpa.jpql.tools.TypeHelper;
import org.eclipse.persistence.jpa.jpql.tools.spi.IType;
import org.eclipse.persistence.jpa.jpql.tools.spi.ITypeDeclaration;

/**
 * This {@link Resolver} is responsible to calculate the type based on the type of the state field.
 *
 * @version 2.5
 * @since 2.3
 * @author Pascal Filion
 */
public class SumFunctionResolver extends Resolver {

    /**
     * Creates a new <code>SumFunctionResolver</code>.
     *
     * @param parent The parent {@link Resolver}, which is never <code>null</code>
     */
    public SumFunctionResolver(Resolver parent) {
        super(parent);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected IType buildType() {

        IType type = getTypeDeclaration().getType();
        TypeHelper helper = getTypeHelper();

        // Integral types: int/Integer, long/Long => the result is a Long
        if (helper.isIntegralType(type)) {
            return helper.longType();
        }

        // Floating types: float/Float, double/Double => the result is a Double
        if (helper.isFloatingType(type)) {
            return helper.doubleType();
        }

        // BigInteger, the result is the same
        IType bigIntegerType = helper.bigInteger();

        if (type.equals(bigIntegerType)) {
            return bigIntegerType;
        }

        // BigDecimal, the result is the same
        IType bigDecimalType = helper.bigDecimal();

        if (type.equals(bigDecimalType)) {
            return bigDecimalType;
        }

        // Anything else is an invalid type
        return helper.objectType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ITypeDeclaration buildTypeDeclaration() {
        return getParentTypeDeclaration();
    }
}
