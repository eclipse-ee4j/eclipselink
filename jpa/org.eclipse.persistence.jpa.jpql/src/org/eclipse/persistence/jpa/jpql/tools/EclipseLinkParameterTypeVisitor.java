/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.jpa.jpql.tools;

import org.eclipse.persistence.jpa.jpql.AbstractEclipseLinkParameterTypeVisitor;
import org.eclipse.persistence.jpa.jpql.tools.spi.IType;

/**
 * This visitor calculates the type of an input parameter.
 * <p>
 * Provisional API: This interface is part of an interim API that is still under development and
 * expected to change significantly before reaching stability. It is available at this early stage
 * to solicit feedback from pioneering adopters on the understanding that any code that uses this
 * API will almost certainly be broken (repeatedly) as the API evolves.
 *
 * @version 2.5
 * @since 2.4
 * @author Pascal Filion
 */
public class EclipseLinkParameterTypeVisitor extends AbstractEclipseLinkParameterTypeVisitor {

    /**
     * The context used to query information about the application metadata and cached information.
     */
    private JPQLQueryContext queryContext;

    /**
     * Creates a new <code>EclipseLinkParameterTypeVisitor</code>.
     *
     * @param queryContext The context used to query information about the application metadata and
     * cached information
     */
    public EclipseLinkParameterTypeVisitor(JPQLQueryContext queryContext) {
        super();
        this.queryContext = queryContext;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IType getType() {

        // The type should be ignored, use the special constant
        if (ignoreType) {
            return queryContext.getTypeHelper().unknownType();
        }

        // The type name was set
        if (typeName != null) {
            return queryContext.getType(typeName);
        }

        // The calculation couldn't find an expression with a type
        if (expression == null) {
            if (type == null) {
                type = Object.class;
            }
            return queryContext.getType(type);
        }

        return queryContext.getType(expression);
    }
}
