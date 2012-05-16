/*******************************************************************************
 * Copyright (c) 2006, 2012 Oracle and/or its affiliates. All rights reserved.
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.persistence.jpa.jpql.WordParser;

/**
 * The <b>GROUP BY</b> construct enables the aggregation of values according to the properties of an
 * entity class.
 * <p>
 * <div nowrap><b>BNF:</b> <code>groupby_clause ::= GROUP BY groupby_item {, groupby_item}*</code><p>
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
public final class GroupByClause extends AbstractExpression {

	/**
	 * The unique group by item or the collection of group by items.
	 */
	private AbstractExpression groupByItems;

	/**
	 * Determines whether a whitespace was parsed after <b>GROUP BY</b>.
	 */
	private boolean hasSpace;

	/**
	 * The actual <b>GROUP BY</b> identifier found in the string representation of the JPQL query.
	 */
	private String identifier;

	/**
	 * Creates a new <code>GroupByClause</code>.
	 *
	 * @param parent The parent of this expression
	 */
	public GroupByClause(AbstractExpression parent) {
		super(parent, GROUP_BY);
	}

	/**
	 * {@inheritDoc}
	 */
	public void accept(ExpressionVisitor visitor) {
		visitor.visit(this);
	}

	/**
	 * {@inheritDoc}
	 */
	public void acceptChildren(ExpressionVisitor visitor) {
		getGroupByItems().accept(visitor);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void addChildrenTo(Collection<Expression> children) {
		children.add(getGroupByItems());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void addOrderedChildrenTo(List<Expression> children) {

		children.add(buildStringExpression(getText()));

		if (hasSpace) {
			children.add(buildStringExpression(SPACE));
		}

		if (groupByItems != null) {
			children.add(groupByItems);
		}
	}

	/**
	 * Creates a new {@link CollectionExpression} that will wrap the single group by item.
	 *
	 * @return The single group by item represented by a temporary collection
	 */
	public CollectionExpression buildCollectionExpression() {

		List<AbstractExpression> children = new ArrayList<AbstractExpression>(1);
		children.add((AbstractExpression) getGroupByItems());

		List<Boolean> commas = new ArrayList<Boolean>(1);
		commas.add(Boolean.FALSE);

		List<Boolean> spaces = new ArrayList<Boolean>(1);
		spaces.add(Boolean.FALSE);

		return new CollectionExpression(this, children, commas, spaces, true);
	}

	/**
	 * Returns the actual <b>GROUP BY</b> found in the string representation of the JPQL query, which
	 * has the actual case that was used.
	 *
	 * @return The <b>GROUP BY</b> identifier that was actually parsed
	 */
	public String getActualIdentifier() {
		return identifier;
	}

	/**
	 * Returns the {@link Expression} that represents the list of group by items if any was parsed.
	 *
	 * @return The expression that was parsed representing the list of items
	 */
	public Expression getGroupByItems() {
		if (groupByItems == null) {
			groupByItems = buildNullExpression();
		}
		return groupByItems;
	}

	/**
	 * {@inheritDoc}
	 */
	public JPQLQueryBNF getQueryBNF() {
		return getQueryBNF(GroupByClauseBNF.ID);
	}

	/**
	 * Determines whether the list of items was parsed.
	 *
	 * @return <code>true</code> if at least one item was parsed; <code>false</code> otherwise
	 */
	public boolean hasGroupByItems() {
		return groupByItems != null &&
		      !groupByItems.isNull();
	}

	/**
	 * Determines whether a whitespace was found after <b>GROUP BY</b>.
	 *
	 * @return <code>true</code> if there was a whitespace after <b>GROUP BY</b>; <code>false</code>
	 * otherwise
	 */
	public boolean hasSpaceAfterGroupBy() {
		return hasSpace;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void parse(WordParser wordParser, boolean tolerant) {

		// Parse 'GROUP BY'
		identifier = wordParser.moveForward(GROUP_BY);

		hasSpace = wordParser.skipLeadingWhitespace() > 0;

		// Group by items
		groupByItems = parse(wordParser, GroupByItemBNF.ID, tolerant);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void toParsedText(StringBuilder writer, boolean actual) {

		// 'GROUP BY'
		writer.append(actual ? identifier : getText());

		if (hasSpace) {
			writer.append(SPACE);
		}

		// Group by items
		if (groupByItems != null) {
			groupByItems.toParsedText(writer, actual);
		}
	}
}