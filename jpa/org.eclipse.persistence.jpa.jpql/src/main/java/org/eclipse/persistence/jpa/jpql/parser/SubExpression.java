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

import org.eclipse.persistence.jpa.jpql.ExpressionTools;

/**
 * This expression wraps a sub-expression within parenthesis.
 *
 * <div><b>BNF:</b> <code>expression ::= (sub_expression)</code><p></p></div>
 *
 * @version 2.5
 * @since 2.3
 * @author Pascal Filion
 */
public final class SubExpression extends AbstractSingleEncapsulatedExpression {

    /**
     * The {@link JPQLQueryBNF} coming from the parent that is used to parse the next portion of the query.
     */
    private JPQLQueryBNF queryBNF;

    /**
     * Creates a new <code>SubExpression</code>.
     *
     * @param parent The parent of this expression
     * @param queryBNF The BNF coming from the parent that is used to parse the next portion of the query
     */
    public SubExpression(AbstractExpression parent, JPQLQueryBNF queryBNF) {
        super(parent, ExpressionTools.EMPTY_STRING);
        this.queryBNF = queryBNF;
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
    protected boolean areLogicalIdentifiersSupported() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getEncapsulatedExpressionQueryBNFId() {
        return queryBNF.getId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JPQLQueryBNF findQueryBNF(Expression expression) {

        if (hasExpression() && (getExpression().isAncestor(expression))) {
            return queryBNF;
        }

        return getParent().findQueryBNF(expression);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JPQLQueryBNF getQueryBNF() {
        return queryBNF;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean handleCollection(JPQLQueryBNF queryBNF) {
        return true;
    }
}
