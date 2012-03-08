/*******************************************************************************
 * Copyright (c) 2006, 2011 Oracle. All rights reserved.
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

import java.util.HashMap;
import java.util.Map;

/**
 * This object contains the cursor position within the parsed tree and within each of the {@link
 * Expression} from the root to the deepest leaf.
 *
 * @version 2.4
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
	 * The table containing the position of each of the {@link Expression} up to
	 * the deepest leaf.
	 */
	private Map<Expression, Integer> positions;

	/**
	 * Creates a new <code>QueryPosition</code>.
	 *
	 * @param position The position of the cursor in the query
	 */
	QueryPosition(int position) {
		super();

		this.position  = position;
		this.positions = new HashMap<Expression, Integer>();
	}

	/**
	 * Adds the position of the cursor within the given {@link Expression}
	 *
	 * @param expression An {@link Expression} in which the cursor is located
	 * @param The position of the cursor within the given {@link Expression}
	 */
	void addPosition(Expression expression, int position) {
		positions.put(expression, position);
	}

	void adjustPosition(AbstractExpression parent, Expression expression, int adjustedPosition) {

		if (parent == null) {
			addPosition(expression, adjustedPosition);
			return;
		}

		int length = 0;

		for (Expression child : parent.orderedChildren()) {

			if (child == expression) {
				length += adjustedPosition;
				break;
			}
			else {
				length += child.toParsedText().length();
			}
		}

		addPosition(parent, length);
		adjustPosition(parent.getParent(), parent, length);
	}

	QueryPosition adjustPosition(Expression expression, int newPosition) {
		QueryPosition queryPosition = new QueryPosition(newPosition);
		queryPosition.expression = expression;
		queryPosition.adjustPosition((AbstractExpression) expression.getParent(), expression, newPosition);
		return queryPosition;
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
	 * Removes the given {@link Expression} from the cached positions.
	 *
	 * @param expression The {@link Expression} that was registered with this object
	 */
	void removePosition(Expression expression) {
		positions.remove(expression);
	}

	/**
	 * Sets the deepest leaf where the cursor is located.
	 *
	 * @param expression The {@link Expression} that is the deepest leaf within the parsed tree
	 */
	void setExpression(Expression expression) {
		this.expression = expression;
	}

	QueryPosition transform(Expression leafExpression) {
		QueryPosition queryPosition = new QueryPosition(this.position + 1);
		queryPosition.expression = leafExpression;
		transform(queryPosition, leafExpression);
		return queryPosition;
	}

	private void transform(QueryPosition position, Expression expression) {
		if (expression != null) {
			position.addPosition(expression, getPosition(expression) + 1);
			transform(position, expression.getParent());
		}
	}
}