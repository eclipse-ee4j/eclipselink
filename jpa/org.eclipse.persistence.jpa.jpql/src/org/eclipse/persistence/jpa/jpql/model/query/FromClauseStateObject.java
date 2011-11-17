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

import org.eclipse.persistence.jpa.jpql.parser.FromClause;

/**
 * The <code><b>FROM</b></code> clause of a query defines the domain of the query by declaring
 * identification variables. An identification variable is an identifier declared in the
 * <code><b>FROM</b></code> clause of a query. The domain of the query may be constrained by path
 * expressions. Identification variables designate instances of a particular entity abstract schema
 * type. The <code><b>FROM</b></code> clause can contain multiple identification variable
 * declarations separated by a comma (,).
 * <p>
 * <pre><code>BNF: from_clause ::= FROM identification_variable_declaration {, {identification_variable_declaration | collection_member_declaration}}*</code></pre>
 *
 * @see CollectionMemberVariableDeclarationStateObject
 * @see IdentificationVariableDeclaration
 * @see JoinStateObject
 * @see SelectStatementStateObject
 *
 * @see FromClause
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public class FromClauseStateObject extends AbstractFromClauseStateObject {

	/**
	 * Creates a new <code>FromClauseStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public FromClauseStateObject(SelectStatementStateObject parent) {
		super(parent);
	}

	/**
	 * {@inheritDoc}
	 */
	public void accept(StateObjectVisitor visitor) {
		visitor.visit(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public FromClause getExpression() {
		return (FromClause) super.getExpression();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SelectStatementStateObject getParent() {
		return (SelectStatementStateObject) super.getParent();
	}

	/**
	 * Keeps a reference of the {@link FromClause parsed object} object, which should only be done
	 * when this object is instantiated during the conversion of a parsed JPQL query into {@link
	 * StateObject StateObjects}.
	 *
	 * @param expression The {@link FromClause parsed object} representing a <code><b>FROM</b></code>
	 * expression
	 */
	public void setExpression(FromClause expression) {
		super.setExpression(expression);
	}
}