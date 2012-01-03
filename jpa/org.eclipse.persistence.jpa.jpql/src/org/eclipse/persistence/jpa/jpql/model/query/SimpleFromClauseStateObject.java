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

import org.eclipse.persistence.jpa.jpql.parser.InternalSimpleFromClauseBNF;
import org.eclipse.persistence.jpa.jpql.parser.SimpleFromClause;
import org.eclipse.persistence.jpa.jpql.spi.IManagedType;

/**
 * The <code><b>FROM</b></code> clause of a sub-query defines the domain of the sub-query by
 * declaring identification variables. An identification variable is an identifier declared in the
 * <code><b>FROM</b></code> clause of a sub-query. The domain of the sub-query may be constrained by
 * path expressions. Identification variables designate instances of a particular entity abstract
 * schema type. The <code><b>FROM</b></code> clause can contain multiple identification variable
 * declarations separated by a comma (,).
 * <p>
 * <pre><code>BNF: subquery_from_clause ::= FROM subselect_identification_variable_declaration {, subselect_identification_variable_declaration}*</code></pre>
 *
 * @see SimpleFromClause
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public class SimpleFromClauseStateObject extends AbstractFromClauseStateObject {

	/**
	 * Creates a new <code>SimpleFromClauseStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public SimpleFromClauseStateObject(SimpleSelectStatementStateObject parent) {
		super(parent);
	}

	/**
	 * {@inheritDoc}
	 */
	public void accept(StateObjectVisitor visitor) {
		visitor.visit(this);
	}

	/**
	 * Adds a new derived collection declaration to the <code><b>FROM</b></code> clause.
	 *
	 * @return The {@link CollectionMemberDeclarationStateObject} representing the collection
	 * declaration
	 */
	public CollectionMemberDeclarationStateObject addDerivedCollectionDeclaration() {
		CollectionMemberDeclarationStateObject stateObject = new CollectionMemberDeclarationStateObject(this);
		addItem(stateObject);
		return stateObject;
	}

	/**
	 * Adds a new derived collection declaration to the <code><b>FROM</b></code> clause.
	 *
	 * @param collectionValuedPath The collection-valued path expression
	 * @return The {@link CollectionMemberDeclarationStateObject} representing the collection
	 * declaration
	 */
	public CollectionMemberDeclarationStateObject addDerivedCollectionDeclaration(String collectionValuedPath) {

		CollectionMemberDeclarationStateObject stateObject = new CollectionMemberDeclarationStateObject(
			this,
			collectionValuedPath
		);

		addItem(stateObject);
		return stateObject;
	}

	/**
	 * Adds a new derived identification variable declaration to the <code><b>FROM</b></code> clause.
	 *
	 * @return The {@link DerivedPathIdentificationVariableDeclarationStateObject} representing the
	 * path declaration
	 */
	public DerivedPathIdentificationVariableDeclarationStateObject addDerivedPathDeclaration() {
		DerivedPathIdentificationVariableDeclarationStateObject stateObject = new DerivedPathIdentificationVariableDeclarationStateObject(this);
		addItem(stateObject);
		return stateObject;
	}

	/**
	 * Adds a new derived identification variable declaration to the <code><b>FROM</b></code> clause.
	 *
	 * @param path Either the derived singled-valued object field or the collection-valued path expression
	 * @param identificationVariable The identification variable defining the given path
	 * @return The {@link DerivedPathIdentificationVariableDeclarationStateObject} representing the
	 * path declaration
	 */
	public DerivedPathIdentificationVariableDeclarationStateObject addDerivedPathDeclaration(String path,
	                                                                                         String identificationVariable) {

		DerivedPathIdentificationVariableDeclarationStateObject stateObject = new DerivedPathIdentificationVariableDeclarationStateObject(
			this,
			path,
			identificationVariable
		);

		addItem(stateObject);
		return stateObject;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String declarationBNF() {
		return InternalSimpleFromClauseBNF.ID;
	}

	/**
	 * {@inheritDoc}
	 */
	public IManagedType findManagedType(StateObject stateObject) {
		IManagedType managedType = getManagedType(stateObject);
		if (managedType == null) {
			managedType = getParent().getParent().getDeclaration().findManagedType(stateObject);
		}
		return managedType;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SimpleFromClause getExpression() {
		return (SimpleFromClause) super.getExpression();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SimpleSelectStatementStateObject getParent() {
		return (SimpleSelectStatementStateObject) super.getParent();
	}

	/**
	 * Keeps a reference of the {@link SimpleFromClause parsed object} object, which should only be
	 * done when this object is instantiated during the conversion of a parsed JPQL query into
	 * {@link StateObject StateObjects}.
	 *
	 * @param expression The {@link SimpleFromClause parsed object} representing a subquery
	 * <code><b>FROM</b></code clause
	 */
	public void setExpression(SimpleFromClause expression) {
		super.setExpression(expression);
	}
}