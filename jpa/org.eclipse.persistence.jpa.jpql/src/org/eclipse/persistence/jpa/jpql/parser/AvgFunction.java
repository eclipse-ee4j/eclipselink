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
package org.eclipse.persistence.jpa.jpql.parser;

/**
 * One of the aggregate functions. The arguments must be numeric. <b>AVG</b> returns <code>Double</code>.
 *
 * <div><b>BNF:</b> <code>expression ::= AVG([DISTINCT] state_field_path_expression)</code><p></div>
 *
 * @version 2.5
 * @since 2.3
 * @author Pascal Filion
 */
public final class AvgFunction extends AggregateFunction {

    /**
     * Creates a new <code>AvgFunction</code>.
     *
     * @param parent The parent of this expression
     */
    public AvgFunction(AbstractExpression parent) {
        super(parent, AVG);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void accept(ExpressionVisitor visitor) {
        visitor.visit(this);
    }
}
