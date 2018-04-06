/*******************************************************************************
 * Copyright (c) 2006, 2018 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation
 *
 ******************************************************************************/
package org.eclipse.persistence.jpa.jpql.parser;

/**
 * The <b>ORDER BY</b> clause allows the objects or values that are returned by the query to be ordered.
 *
 * <div><b>BNF:</b> <code>orderby_clause ::= <b>ORDER BY</b> {@link OrderByItem orderby_item} {, {@link OrderByItem orderby_item}}*</code><p></div>
 *
 * @version 2.5
 * @since 2.3
 * @author Pascal Filion
 */
public final class OrderByClause extends AbstractOrderByClause {

    /**
     * Creates a new <code>OrderByClause</code>.
     *
     * @param parent The parent of this expression
     */
    public OrderByClause(AbstractExpression parent) {
        super(parent, ORDER_BY);
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
    public JPQLQueryBNF getQueryBNF() {
        return getQueryBNF(OrderByClauseBNF.ID);
    }
}
