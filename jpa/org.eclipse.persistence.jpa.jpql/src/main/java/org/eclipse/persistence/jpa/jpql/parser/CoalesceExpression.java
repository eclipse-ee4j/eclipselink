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
 * A <b>COALESCE</b> expression returns <code>null</code> if all its arguments evaluate to
 * <code>null</code>, and the value of the first non-<code>null</code> argument otherwise.
 * <p>
 * The return type is the type returned by the arguments if they are all of the same type, otherwise
 * it is undetermined.
 *
 * <div><b>BNF:</b> <code>coalesce_expression::= COALESCE(scalar_expression {, scalar_expression}+)</code></div>
 *
 * @version 2.5
 * @since 2.3
 * @author Pascal Filion
 */
public final class CoalesceExpression extends AbstractSingleEncapsulatedExpression {

    /**
     * Creates a new <code>CoalesceExpression</code>.
     *
     * @param parent The parent of this expression
     */
    public CoalesceExpression(AbstractExpression parent) {
        super(parent, COALESCE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void accept(ExpressionVisitor visitor) {
        visitor.visit(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getEncapsulatedExpressionQueryBNFId() {
        return InternalCoalesceExpressionBNF.ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JPQLQueryBNF getQueryBNF() {
        return getQueryBNF(CoalesceExpressionBNF.ID);
    }
}
