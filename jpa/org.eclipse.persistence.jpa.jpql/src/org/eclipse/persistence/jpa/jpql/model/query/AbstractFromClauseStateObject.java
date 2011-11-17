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
import java.util.ArrayList;
import java.util.List;
import org.eclipse.persistence.jpa.jpql.Assert;
import org.eclipse.persistence.jpa.jpql.model.Problem;
import org.eclipse.persistence.jpa.jpql.parser.AbstractFromClause;
import org.eclipse.persistence.jpa.jpql.spi.IEntity;
import org.eclipse.persistence.jpa.jpql.util.CollectionTools;
import org.eclipse.persistence.jpa.jpql.util.iterator.CloneListIterator;
import org.eclipse.persistence.jpa.jpql.util.iterator.IterableIterator;
import org.eclipse.persistence.jpa.jpql.util.iterator.IterableListIterator;

import static org.eclipse.persistence.jpa.jpql.parser.AbstractExpression.*;
import static org.eclipse.persistence.jpa.jpql.parser.Expression.*;

/**
 * This state object represents the abstract definition of a <code><b>FROM</b></code> clause, which
 * is either the <code>FROM</code> clause of the query or of a sub-query expression.
 *
 * @see AbstractSelectStatementStateObject
 * @see FromClauseStateObject
 * @see SubQueryFromClauseStateObject
 *
 * @see AbstractFromClause
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings({"nls", "unused"}) // unused used for the import statement: see bug 330740
public abstract class AbstractFromClauseStateObject extends AbstractListHolderStateObject<VariableDeclarationStateObject>
                                                    implements DeclarationStateObject {

	/**
	 * Notifies the content of the list of {@link IVariableDeclarationStateObject} has changed.
	 */
	public static final String VARIABLE_DECLARATIONS_LIST = "variableDeclarations";

	/**
	 * Creates a new <code>AbstractFromClauseStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	protected AbstractFromClauseStateObject(AbstractSelectStatementStateObject parent) {
		super(parent);
	}

	/**
	 * Adds a new collection declaration to the <code><b>FROM</b></code> clause.
	 *
	 * @return The {@link CollectionMemberDeclarationStateObject} representing the collection declaration
	 */
	public CollectionMemberDeclarationStateObject addCollectionDeclaration() {
		CollectionMemberDeclarationStateObject stateObject = new CollectionMemberDeclarationStateObject(this);
		addItem(stateObject);
		return stateObject;
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

		CollectionMemberDeclarationStateObject stateObject = new CollectionMemberDeclarationStateObject(this);
		stateObject.setPath(collectionValuedPath);
		stateObject.setIdentificationVariable(identificationVariable);

		addItem(stateObject);
		return stateObject;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void addProblems(List<Problem> currentProblems) {
		super.addProblems(currentProblems);
		// TODO
	}

	/**
	 * Adds a new range variable declaration to the <code><b>FROM</b></code> clause.
	 *
	 * @return The {@link StateObject} representing the range variable declaration
	 */
	public IdentificationVariableDeclarationStateObject addRangeDeclaration() {
		IdentificationVariableDeclarationStateObject stateObject = new IdentificationVariableDeclarationStateObject(this);
		addItem(stateObject);
		return stateObject;
	}

	/**
	 * Adds to this select statement a new range variable declaration.
	 *
	 * @param entity The external form of the entity to add to the declaration list
	 * @param identificationVariable The unique identifier identifying the abstract schema name
	 * @return The state object of the new declaration
	 */
	public IdentificationVariableDeclarationStateObject addRangeDeclaration(IEntity entity,
	                                                                        String identificationVariable) {

		IdentificationVariableDeclarationStateObject stateObject = new IdentificationVariableDeclarationStateObject(
			this,
			entity,
			identificationVariable
		);

		addItem(stateObject);
		return stateObject;
	}

	/**
	 * Adds a new range variable declaration.
	 *
	 * @param entityName The name of the entity name
	 * @param identificationVariable The new identification variable
	 * @return The state object of the new declaration
	 */
	public IdentificationVariableDeclarationStateObject addRangeDeclaration(String entityName,
	                                                                        String identificationVariable) {

		IdentificationVariableDeclarationStateObject stateObject = new IdentificationVariableDeclarationStateObject(
			this,
			entityName,
			identificationVariable
		);

		addItem(stateObject);
		return stateObject;
	}

	/**
	 * {@inheritDoc}
	 */
	public IterableListIterator<? extends VariableDeclarationStateObject> declarations() {
		return items();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IdentificationVariableStateObject findIdentificationVariable(String variable) {

		for (IdentificationVariableStateObject identificationVariable : identificationVariables()) {
			if (Assert.isSame(identificationVariable.getText(), variable)) {
				return identificationVariable;
			}
		}

		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DeclarationStateObject getDeclaration() {
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AbstractFromClause getExpression() {
		return (AbstractFromClause) super.getExpression();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AbstractSelectStatementStateObject getParent() {
		return (AbstractSelectStatementStateObject) super.getParent();
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
	public IterableIterator<IdentificationVariableStateObject> identificationVariables() {
		List<IdentificationVariableStateObject> stateObjects = new ArrayList<IdentificationVariableStateObject>();
		for (VariableDeclarationStateObject stateObject : items()) {
			CollectionTools.addAll(stateObjects, stateObject.identificationVariables());
		}
		return new CloneListIterator<IdentificationVariableStateObject>(stateObjects);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String listName() {
		return VARIABLE_DECLARATIONS_LIST;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void toTextInternal(Appendable writer) throws IOException {
		writer.append(FROM);
		writer.append(SPACE);
		toStringItems(writer, true);
	}
}