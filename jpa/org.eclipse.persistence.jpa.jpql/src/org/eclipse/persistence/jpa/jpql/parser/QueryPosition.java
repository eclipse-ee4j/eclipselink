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

import java.util.HashMap;
import java.util.Map;

/**
 * This object contains the cursor position within the parsed tree and within each of the {@link
 * Expression} from the root to the deepest leaf.
 *
 * @version 2.5
 * @since 2.3
 * @author Pascal Filion
 */
public final class QueryPosition {

    /**
     * The deepest child {@link Expression} where the position of the cursor is.
     */
    private Expression expression;

    /**
     * The position of the cursor in the query.
     */
    private int position;

    /**
     * The table containing the position of each of the {@link Expression} up to the deepest leaf.
     */
    private Map<Expression, Integer> positions;

    /**
     * Creates a new <code>QueryPosition</code>.
     *
     * @param position The position of the cursor in the query
     */
    public QueryPosition(int position) {
        super();
        this.position  = position;
        this.positions = new HashMap<Expression, Integer>();
    }

    /**
     * Adds the position of the cursor within the given {@link Expression}
     *
     * @param expression An {@link Expression} in which the cursor is located
     * @param position The position of the cursor within the given {@link Expression}
     */
    public void addPosition(Expression expression, int position) {
        positions.put(expression, position);
    }

    /**
     * Returns the child {@link Expression} where the position of the cursor is.
     *
     * @return The deepest {@link Expression} child that was retrieving by
     * traversing the parsed tree up to the position of the cursor.
     */
    public Expression getExpression() {
        return expression;
    }

    /**
     * Returns the position of the cursor in the query.
     *
     * @return The position of the cursor in the query
     */
    public int getPosition() {
        return position;
    }

    /**
     * Returns the position of the cursor within the given {@link Expression}
     *
     * @param expression The {@link Expression} for which the position of the cursor is requested
     * @return Either the position of the cursor within the given {@link Expression} or -1 if the
     * cursor is not within it
     */
    public int getPosition(Expression expression) {
        Integer position = positions.get(expression);
        return (position == null) ? -1 : position;
    }

    /**
     * Sets the deepest leaf where the cursor is located.
     *
     * @param expression The {@link Expression} that is the deepest leaf within the parsed tree
     */
    public void setExpression(Expression expression) {
        this.expression = expression;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return positions.toString();
    }
}
