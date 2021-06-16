/*
 * Copyright (c) 2006, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Oracle - initial API and implementation
//
package org.eclipse.persistence.jpa.jpql.parser;

/**
 * One of the aggregate functions. The arguments must be numeric.
 * <p>
 * <b>SUM</b> returns <code>Long</code> when applied to state-fields of integral types (other than
 * <code>BigInteger</code>); <code>Double</code> when applied to state-fields of floating point
 * types; <code>BigInteger</code> when applied to state-fields of type <code>BigInteger</code>; and
 * <code>BigDecimal</code> when applied to state-fields of type <code>BigDecimal</code>. If
 * <b>SUM</b>, <b>AVG</b>, <b>MAX</b>, or <b>MIN</b> is used, and there are no values to which the
 * aggregate function can be applied, the result of the aggregate function is <code>NULL</code>. If
 * <code>COUNT</code> is used, and there are no values to which <b>COUNT</b> can be applied, the
 * result of the aggregate function is 0.
 *
 * <div><b>BNF:</b> <code>expression ::= SUM([DISTINCT] state_field_path_expression)</code></div>
 *
 * @version 2.5
 * @since 2.3
 * @author Pascal Filion
 */
public final class SumFunction extends AggregateFunction {

    /**
     * Creates a new <code>SumFunction</code>.
     *
     * @param parent The parent of this expression
     */
    public SumFunction(AbstractExpression parent) {
        super(parent, SUM);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void accept(ExpressionVisitor visitor) {
        visitor.visit(this);
    }
}
