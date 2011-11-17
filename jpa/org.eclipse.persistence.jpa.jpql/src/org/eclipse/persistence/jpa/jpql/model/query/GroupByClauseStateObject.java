/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.jpa.jpql.model.query;

import java.io.IOException;
import java.util.List;
import org.eclipse.persistence.jpa.jpql.parser.GroupByClause;
import org.eclipse.persistence.jpa.jpql.parser.GroupByItemBNF;

import static org.eclipse.persistence.jpa.jpql.parser.AbstractExpression.*;
import static org.eclipse.persistence.jpa.jpql.parser.Expression.*;

/**
 * The <code><b>GROUP BY</b></code> construct enables the aggregation of values according to the
 * properties of an entity class.
 * <p>
 * <div nowrap><b>BNF:</b> <code>groupby_clause ::= GROUP BY groupby_item {, groupby_item}*</code><p>
 *
 * @see GroupByClause
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings({"nls", "unused"}) // unused used for the import statement: see bug 330740
public class GroupByClauseStateObject extends AbstractListHolderStateObject<StateObject> {

	/**
	 * Notify the list of {@link StateObject StateObjects} representing the <code><b>GROUP BY</b></code>
	 * items.
	 */
	public static final String GROUP_BY_ITEMS_LIST = "groupByItems";

	/**
	 * Creates a new <code>GroupByClauseStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public GroupByClauseStateObject(AbstractSelectStatementStateObject parent) {
		super(parent);
	}

	/**
	 * {@inheritDoc}
	 */
	public void accept(StateObjectVisitor visitor) {
		visitor.visit(this);
	}

	/**
	 * Adds the given path as a select item, which can either be an identification variable or a
	 * state-field path expression.
	 *
	 * @param path Either an identification variable or a state-field path expression
	 * @return The {@link StateObject} encapsulating the given path
	 */
	public StateObject addGroupByItem(String path) {
		StateObject stateObject = buildStateObject(path, GroupByItemBNF.ID);
		addItem(stateObject);
		return stateObject;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public GroupByClause getExpression() {
		return (GroupByClause) super.getExpression();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AbstractSelectStatementStateObject getParent() {
		return (AbstractSelectStatementStateObject) super.getParent();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String listName() {
		return GROUP_BY_ITEMS_LIST;
	}

	/**
	 * Parses the given JPQL fragment, which will represent the group by items. The fragment cannot
	 * start with the <code><b>GROUP BY</b></code>.
	 *
	 * @param jpqlFragment The string representation of the group by items
	 */
	public void parse(String jpqlFragment) {
		List<StateObject> stateObjects = buildStateObjects(jpqlFragment, GroupByItemBNF.ID);
		addItems(stateObjects);
	}

	/**
	 * Keeps a reference of the {@link GroupByClause parsed object} object, which should only be
	 * done when this object is instantiated during the conversion of a parsed JPQL query into
	 * {@link StateObject StateObjects}.
	 *
	 * @param expression The {@link GroupByClause parsed object} representing a <code><b>GROUP
	 * BY</b></code> clause
	 */
	public void setExpression(GroupByClause expression) {
		super.setExpression(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void toTextInternal(Appendable writer) throws IOException {
		writer.append(GROUP_BY);
		if (hasItems()) {
			writer.append(SPACE);
			toStringItems(writer, true);
		}
	}
}