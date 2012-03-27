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
import java.util.Iterator;
import java.util.List;
import org.eclipse.persistence.jpa.jpql.parser.AbstractSelectStatement;
import org.eclipse.persistence.jpa.jpql.spi.IEntity;
import org.eclipse.persistence.jpa.jpql.util.iterator.IterableListIterator;

import static org.eclipse.persistence.jpa.jpql.parser.AbstractExpression.*;

/**
 * This state object represents the select statement, which has at least a <code><b>SELECT</b></code>
 * clause and a <b>FROM</b></code> clause.
 *
 * @see SelectStatementStateObject
 * @see FromClauseStateObject
 * @see GroupByClauseStateObject
 * @see HavingClauseStateObject
 * @see SelectClauseStateObject
 * @see WhereClauseStateObject
 *
 * @see AbstractSelectStatement
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public abstract class AbstractSelectStatementStateObject extends AbstractStateObject {

	/**
	 * The state object representing the <code><b>FROM</b></code> clause.
	 */
	private AbstractFromClauseStateObject fromClause;

	/**
	 * The state object representing the <code><b>GROUP BY</b></code> clause.
	 */
	private GroupByClauseStateObject groupByClause;

	/**
	 * The state object representing the <code><b>HAVING</b></code> clause.
	 */
	private HavingClauseStateObject havingClause;

	/**
	 * The state object representing the <code><b>SELECT</b></code> clause.
	 */
	private AbstractSelectClauseStateObject selectClause;

	/**
	 * The state object representing the <code><b>WHERE</b></code> clause.
	 */
	private WhereClauseStateObject whereClause;

	/**
	 * Notify the state object representing the <code><b>GROUP BY</b></code> clause has changed.
	 */
	public static String GROUP_BY_CLAUSE_PROPERTY = "groupByClause";

	/**
	 * Notify the state object representing the <code><b>HAVING</b></code> clause has changed.
	 */
	public static String HAVING_CLAUSE_PROPERTY = "havingClause";

	/**
	 * Notify the state object representing the <code><b>WHERE</b></code> clause has changed.
	 */
	public static String WHERE_CLAUSE_PROPERTY = "whereClause";

	/**
	 * Creates a new <code>AbstractSelectStatementStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	protected AbstractSelectStatementStateObject(StateObject parent) {
		super(parent);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void addChildren(List<StateObject> children) {

		super.addChildren(children);
		children.add(selectClause);
		children.add(fromClause);

		if (whereClause != null) {
			children.add(whereClause);
		}

		if (groupByClause != null) {
			children.add(groupByClause);
		}

		if (havingClause != null) {
			children.add(havingClause);
		}
	}

	/**
	 * Adds a new collection declaration to the <code><b>FROM</b></code> clause.
	 *
	 * @return The {@link CollectionMemberDeclarationStateObject} representing the collection
	 * declaration
	 */
	public CollectionMemberDeclarationStateObject addCollectionDeclaration() {
		return getFromClause().addCollectionDeclaration();
	}

	/**
	 * Adds a new collection declaration to the <code><b>FROM</b></code> clause.
	 *
	 * @param collectionValuedPath The collection-valued path expression
	 * @param identificationVariable The variable defining the collection-valued path expression
	 * @return The {@link CollectionMemberDeclarationStateObject} representing the collection
	 * declaration
	 */
	public CollectionMemberDeclarationStateObject addCollectionDeclaration(String collectionValuedPath,
	                                                                       String identificationVariable) {

		return getFromClause().addCollectionDeclaration(collectionValuedPath, identificationVariable);
	}

	/**
	 * Adds the <code><b>GROUP BY</b></code> clause. The clause is not added if it's already present.
	 *
	 * @return The {@link GroupByClauseStateObject}
	 */
	public GroupByClauseStateObject addGroupByClause() {
		if (!hasGroupByClause()) {
			setGroupByClause(new GroupByClauseStateObject(this));
		}
		return groupByClause;
	}

	/**
	 * Adds the <code><b>GROUP BY</b></code> clause and parses the given JPQL fragment. The clause is
	 * not added if it's already present.
	 *
	 * @param jpqlFragment The fragment of the JPQL to parse that represents the group by items, the
	 * fragment cannot start with <code><b>GROUP BY</b></code>
	 * @return The {@link GroupByClauseStateObject}
	 */
	public GroupByClauseStateObject addGroupByClause(String jpqlFragment) {
		GroupByClauseStateObject stateObject = addGroupByClause();
		stateObject.parse(jpqlFragment);
		return stateObject;
	}

	/**
	 * Adds the <code><b>HAVING</b></code> clause. The clause is not added if it's already present.
	 *
	 * @return The {@link GroupByClauseStateObject}
	 */
	public HavingClauseStateObject addHavingClause() {
		if (!hasHavingClause()) {
			setHavingClause(new HavingClauseStateObject(this));
		}
		return havingClause;
	}

	/**
	 * Adds the <code><b>HAVING</b></code> clause and parses the given JPQL fragment. The clause is
	 * not added if it's already present.
	 *
	 * @param jpqlFragment The fragment of the JPQL to parse that represents the conditional expression,
	 * the fragment cannot start with <code><b>HAVING</b></code>
	 * @return The {@link HavingClauseStateObject}
	 */
	public HavingClauseStateObject addHavingClause(String jpqlFragment) {
		HavingClauseStateObject stateObject = addHavingClause();
		stateObject.parse(jpqlFragment);
		return stateObject;
	}

	/**
	 * Adds a new range variable declaration to the <code><b>FROM</b></code> clause.
	 *
	 * @return The {@link StateObject} representing the new range variable declaration
	 */
	public IdentificationVariableDeclarationStateObject addRangeDeclaration() {
		return getFromClause().addRangeDeclaration();
	}

	/**
	 * Adds to this select statement a new range variable declaration.
	 *
	 * @param entity The external form of the entity to add to the declaration list
	 * @param identificationVariable The unique identifier identifying the given {@link IEntity}
	 * @return The {@link StateObject} representing the new range variable declaration
	 */
	public IdentificationVariableDeclarationStateObject addRangeDeclaration(IEntity entity,
	                                                                        String identificationVariable) {

		return getFromClause().addRangeDeclaration(entity, identificationVariable);
	}

	/**
	 * Adds to this select statement a new range variable declaration.
	 *
	 * @param entityName The name of the entity
	 * @param identificationVariable The unique identifier identifying the entity
	 * @return The {@link StateObject} representing the range variable declaration
	 */
	public IdentificationVariableDeclarationStateObject addRangeDeclaration(String entityName,
	                                                                        String identificationVariable) {

		return getFromClause().addRangeDeclaration(entityName, identificationVariable);
	}

	/**
	 * Adds the <code><b>WHERE</b></code> clause. The clause is not added if it's already present.
	 *
	 * @return The {@link GroupByClauseStateObject}
	 */
	public WhereClauseStateObject addWhereClause() {
		if (!hasWhereClause()) {
			setWhereClause(new WhereClauseStateObject(this));
		}
		return whereClause;
	}

	/**
	 * Adds the <code><b>WHERE</b></code> clause and parses the given JPQL fragment. The clause is
	 * not added if it's already present.
	 *
	 * @param jpqlFragment The fragment of the JPQL to parse that represents the conditional expression,
	 * the fragment cannot start with <code><b>WHERE</b></code>
	 * @return The {@link WhereClauseStateObject}
	 */
	public WhereClauseStateObject addWhereClause(String jpqlFragment) {
		WhereClauseStateObject stateObject = addWhereClause();
		stateObject.parse(jpqlFragment);
		return stateObject;
	}

	/**
	 * Creates the state object representing the <code><b>FROM</b></code> clause.
	 *
	 * @return A concrete instance of {@link AbstractFromClauseStateObject}
	 */
	protected abstract AbstractFromClauseStateObject buildFromClause();

	/**
	 * Creates the state object representing the <code><b>SELECT</b></code> clause.
	 *
	 * @return A concrete instance of {@link AbstractSelectClauseStateObject}
	 */
	protected abstract AbstractSelectClauseStateObject buildSelectClause();

	/**
	 * Returns the list of {@link VariableDeclarationStateObject} defining the variable declarations,
	 * which are mapping an entity to a variable or a collection-valued member to a variable.
	 * <p>
	 * Example:
	 * <ul>
	 * <li><code>Employee e</code></li>
	 * <li><code>IN (e.employees) AS emps</code></li>
	 * </ul>
	 *
	 * @return The list of {@link VariableDeclarationStateObject}
	 */
	public IterableListIterator<? extends VariableDeclarationStateObject> declarations() {
		return fromClause.items();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IdentificationVariableStateObject findIdentificationVariable(String identificationVariable) {
		return fromClause.findIdentificationVariable(identificationVariable);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DeclarationStateObject getDeclaration() {
		return fromClause;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AbstractSelectStatement getExpression() {
		return (AbstractSelectStatement) super.getExpression();
	}

	/**
	 * Returns the state object representing the <code><b>FROM</b></code> clause.
	 *
	 * @return The state object representing the <code><b>FROM</b></code> clause, which is never
	 * <code>null</code>
	 */
	public AbstractFromClauseStateObject getFromClause() {
		return fromClause;
	}

	/**
	 * Returns the state object representing the <code><b>GROUP BY</b></code> clause.
	 *
	 * @return Either the actual state object representing the <code><b>GROUP BY</b></code> clause or
	 * <code>null</code> if it's not present
	 */
	public GroupByClauseStateObject getGroupByClause() {
		return groupByClause;
	}

	/**
	 * Returns the state object representing the <code><b>HAVING</b></code> clause.
	 *
	 * @return Either the actual state object representing the <code><b>HAVING</b></code> clause or
	 * <code>null</code> if it's not present
	 */
	public HavingClauseStateObject getHavingClause() {
		return havingClause;
	}

	/**
	 * Returns the state object representing the <code><b>SELECT</b></code> clause.
	 *
	 * @return Either the actual state object representing the <code><b>SELECT</b></code> clause,
	 * which is never <code>null</code>
	 */
	public AbstractSelectClauseStateObject getSelectClause() {
		return selectClause;
	}

	/**
	 * Returns the state object representing the <code><b>WHERE</b></code> clause.
	 *
	 * @return Either the actual state object representing the <code><b>WHERE</b></code> clause or
	 * the <code>null</code> state object since <code>null</code> is never returned
	 */
	public WhereClauseStateObject getWhereClause() {
		return whereClause;
	}

	/**
	 * Returns the state object representing the <code><b>GROUP BY</b></code> clause.
	 *
	 * @return Either the actual state object representing the <code><b>GROUP BY</b></code> clause or
	 * <code>null</code> if it's not present
	 */
	public boolean hasGroupByClause() {
		return groupByClause != null;
	}

	/**
	 * Returns the state object representing the <code><b>HAVING</b></code> clause.
	 *
	 * @return Either the actual state object representing the <code><b>HAVING</b></code> clause  or
	 * <code>null</code> if it's not present
	 */
	public boolean hasHavingClause() {
		return havingClause != null;
	}

	/**
	 * Returns the state object representing the <code><b>WHERE</b></code> clause.
	 *
	 * @return Either the actual state object representing the <code><b>WHERE</b></code> clause or
	 * <code>null</code> if it's not present
	 */
	public boolean hasWhereClause() {
		return whereClause != null;
	}

	/**
	 * Returns the {@link IdentificationVariableStateObject IdentificationVariableStateObjects}
	 * holding onto the identification variables, which are the variables defined in the
	 * <code><b>FROM</b></code> clause.
	 * <p>
	 * Example:
	 * <ul>
	 * <li><code>Employee e</code>; <i>e</i> is returned</li>
	 * <li><code>IN (e.employees) AS emps</code></li>; <i>emps</i> is returned</li>
	 * <li><code>Manager m JOIN m.employees emps</code>; <i>m</i> and <i>emps</i> are returned</li>
	 * </ul>
	 *
	 * @return The list of {@link IdentificationVariableStateObject IdentificationVariableStateObjects}
	 */
	public Iterator<IdentificationVariableStateObject> identificationVariables() {
		return fromClause.identificationVariables();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initialize() {
		super.initialize();
		selectClause = buildSelectClause();
		fromClause   = buildFromClause();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isEquivalent(StateObject stateObject) {

		if (super.isEquivalent(stateObject)) {
			AbstractSelectStatementStateObject select = (AbstractSelectStatementStateObject) stateObject;
			return areEquivalent(selectClause,  select.selectClause)  &&
			       areEquivalent(fromClause,    select.fromClause)    &&
			       areEquivalent(fromClause,    select.fromClause)    &&
			       areEquivalent(whereClause,   select.whereClause)   &&
			       areEquivalent(groupByClause, select.groupByClause) &&
			       areEquivalent(havingClause,  select.havingClause);
		}

		return false;
	}

	/**
	 * Parses the given JPQL fragment and create the select item. For the top-level query, the
	 * fragment can contain several select items but for a subquery, it can represent only one.
	 *
	 * @param jpqlFragment The portion of the query representing one or several select items
	 */
	public void parseSelect(String jpqlFragment) {
		getSelectClause().parse(jpqlFragment);
	}

	/**
	 * Removes the <code><b>GROUP BY</b></code> clause.
	 */
	public void removeGroupByClause() {
		setGroupByClause(null);
	}

	/**
	 * Removes the <code><b>HAVING</b></code> clause.
	 */
	public void removeHavingClause() {
		setHavingClause(null);
	}

	/**
	 * Removes the <code><b>WHERE</b></code> clause.
	 */
	public void removeWhereClause() {
		setWhereClause(null);
	}

	private void setGroupByClause(GroupByClauseStateObject groupByClause) {
		GroupByClauseStateObject oldGroupByClause = this.groupByClause;
		this.groupByClause = groupByClause;
		firePropertyChanged(GROUP_BY_CLAUSE_PROPERTY, oldGroupByClause, groupByClause);
	}

	private void setHavingClause(HavingClauseStateObject havingClause) {
		HavingClauseStateObject oldHavingClause = this.havingClause;
		this.havingClause = havingClause;
		firePropertyChanged(HAVING_CLAUSE_PROPERTY, oldHavingClause, havingClause);
	}

	private void setWhereClause(WhereClauseStateObject whereClause) {
		WhereClauseStateObject oldWhereClause = this.whereClause;
		this.whereClause = whereClause;
		firePropertyChanged(WHERE_CLAUSE_PROPERTY, oldWhereClause, whereClause);
	}

	/**
	 * Either adds or removes the state object representing the <code><b>GROUP BY</b></code> clause.
	 */
	public void toggleGroupByClause() {
		if (hasGroupByClause()) {
			removeGroupByClause();
		}
		else {
			addGroupByClause();
		}
	}

	/**
	 * Either adds or removes the state object representing the <code><b>HAVING</b></code> clause.
	 */
	public void toggleHavingClause() {
		if (hasHavingClause()) {
			removeHavingClause();
		}
		else {
			addHavingClause();
		}
	}

	/**
	 * Either adds or removes the state object representing the <code><b>WHERE</b></code> clause.
	 */
	public void toggleWhereClause() {
		if (hasWhereClause()) {
			removeWhereClause();
		}
		else {
			addWhereClause();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void toTextInternal(Appendable writer) throws IOException {

		selectClause.toString(writer);
		writer.append(SPACE);
		fromClause.toString(writer);

		if (whereClause != null) {
			writer.append(SPACE);
			whereClause.toString(writer);
		}

		if (groupByClause != null) {
			writer.append(SPACE);
			groupByClause.toString(writer);
		}

		if (havingClause != null) {
			writer.append(SPACE);
			havingClause.toString(writer);
		}
	}
}