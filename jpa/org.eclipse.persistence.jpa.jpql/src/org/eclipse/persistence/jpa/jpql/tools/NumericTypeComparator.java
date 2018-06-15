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
package org.eclipse.persistence.jpa.jpql.tools;

import java.util.Comparator;
import org.eclipse.persistence.jpa.jpql.tools.spi.IType;

/**
 * This {@link Comparator} is used to sort {@link IType ITypes} based on the numerical priority.
 *
 * @version 2.5
 * @since 2.3
 * @author Pascal Filion
 */
public final class NumericTypeComparator implements Comparator<IType> {

    /**
     * The helper that gives access to the most common {@link IType types}.
     */
    private final TypeHelper typeHelper;

    /**
     * Creates a new <code>NumericTypeComparator</code>.
     *
     * @param typeHelper The helper that gives access to the most common {@link IType types}
     */
    public NumericTypeComparator(TypeHelper typeHelper) {
        super();
        this.typeHelper = typeHelper;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compare(IType type1, IType type2) {

        // Same type
        if (type1.equals(type2)) {
            return 0;
        }

        // Object type
        IType type = typeHelper.objectType();
        if (type1.equals(type)) return -1;
        if (type2.equals(type)) return  1;

        // Double
        type = typeHelper.doubleType();
        if (type1.equals(type)) return -1;
        if (type2.equals(type)) return  1;

        // Float
        type = typeHelper.floatType();
        if (type1.equals(type)) return -1;
        if (type2.equals(type)) return  1;

        // BigDecimal
        type = typeHelper.bigDecimal();
        if (type1.equals(type)) return -1;
        if (type2.equals(type)) return  1;

        // BigInteger
        type = typeHelper.bigInteger();
        if (type1.equals(type)) return -1;
        if (type2.equals(type)) return  1;

        // Long
        type = typeHelper.longType();
        if (type1.equals(type)) return -1;
        if (type2.equals(type)) return  1;

        // Integer
        type = typeHelper.integerType();
        if (type1.equals(type)) return -1;
        if (type2.equals(type)) return  1;

        return 1;
    }
}
