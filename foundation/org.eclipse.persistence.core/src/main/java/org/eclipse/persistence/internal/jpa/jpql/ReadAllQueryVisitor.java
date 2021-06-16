/*
 * Copyright (c) 2012, 2021 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.internal.jpa.jpql;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.jpa.jpql.parser.CollectionExpression;
import org.eclipse.persistence.jpa.jpql.parser.EclipseLinkAnonymousExpressionVisitor;
import org.eclipse.persistence.jpa.jpql.parser.HierarchicalQueryClause;
import org.eclipse.persistence.jpa.jpql.parser.OrderSiblingsByClause;
import org.eclipse.persistence.queries.ReadAllQuery;

/**
 * This visitor is responsible to populate a {@link ReadAllQuery}.
 *
 * @version 2.5
 * @since 2.5
 * @author Pascal Filion
 */
class ReadAllQueryVisitor extends AbstractObjectLevelReadQueryVisitor {

    /**
     * Creates a new <code>ReadAllQueryVisitor</code>.
     *
     * @param queryContext The context used to query information about the application metadata and
     * cached information
     * @param query The {@link ReportQuery} to populate by using this visitor to visit the parsed
     * tree representation of the JPQL query
     */
    ReadAllQueryVisitor(JPQLQueryContext queryContext, ReadAllQuery query) {
        super(queryContext, query);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(HierarchicalQueryClause expression) {

        // START WITH clause
        Expression startWithClause = null;

        if (expression.hasStartWithClause()) {
            startWithClause = queryContext.buildExpression(expression.getStartWithClause());
        }

        // CONNECT BY clause
        Expression connectByClause = queryContext.buildExpression(expression.getConnectByClause());

        // ORDER SIBLINGS BY clause
        List<Expression> orderItems = Collections.<Expression>emptyList();

        if (expression.hasOrderSiblingsByClause()) {
            OrderSiblingsByClauseVisitor visitor = new OrderSiblingsByClauseVisitor();
            expression.getOrderSiblingsByClause().accept(visitor);
            orderItems = visitor.orderByItems;
        }

        // Now add the hierarchical query clause
        ReadAllQuery query = (ReadAllQuery) this.query;
        query.setHierarchicalQueryClause(startWithClause, connectByClause, orderItems);
    }

    private class OrderSiblingsByClauseVisitor extends EclipseLinkAnonymousExpressionVisitor {

        /**
         * The ordered list of items.
         */
        List<Expression> orderByItems;

        /**
         * Creates a new <code>OrderSiblingsByClauseVisitor</code>.
         */
        OrderSiblingsByClauseVisitor() {
            super();
            orderByItems = new ArrayList<>();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(CollectionExpression expression) {
            expression.acceptChildren(this);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(OrderSiblingsByClause expression) {
            expression.getOrderByItems().accept(this);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(org.eclipse.persistence.jpa.jpql.parser.Expression expression) {
            orderByItems.add(queryContext.buildExpression(expression));
        }
    }
}
