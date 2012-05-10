/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
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
import org.eclipse.persistence.jpa.jpql.parser.ConcatExpression;

import static org.eclipse.persistence.jpa.jpql.parser.AbstractExpression.*;
import static org.eclipse.persistence.jpa.jpql.parser.Expression.*;

/**
 * The <code><b>CONCAT</b></code> function returns a string that is a concatenation of its arguments.
 * <p>
 * JPA 1.0:
 * <div nowrap><b>BNF:</b> <code>expression ::= CONCAT(string_primary, string_primary)</code><p>
 * JPA 2.0
 * <div nowrap><b>BNF:</b> <code>expression ::= CONCAT(string_primary, string_primary {, string_primary}*)</code><p>
 *
 * @see ConcatExpression
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings({"nls", "unused"}) // unused used for the import statement: see bug 330740
public class ConcatExpressionStateObject extends AbstractListHolderStateObject<StateObject> {

	/**
	 * Notifies the content of the list of {@link StateObject} representing the string primaries
	 * has changed.
	 */
	public static final String STRING_PRIMARY_STATE_OBJECT_LIST = "stringPrimary";

	/**
	 * Creates a new <code>ConcatExpressionStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public ConcatExpressionStateObject(StateObject parent) {
		super(parent);
	}

	/**
	 * Creates a new <code>ConcatExpressionStateObject</code>.
	 *
	 * @param stateObjects The list of {@link StateObject} representing the encapsulated expressions
	 */
	public ConcatExpressionStateObject(StateObject parent, List<? extends StateObject> stateObjects) {
		super(parent, stateObjects);
	}

	/**
	 * Creates a new <code>ConcatExpressionStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @param stateObjects The list of {@link StateObject} representing the encapsulated expressions
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public ConcatExpressionStateObject(StateObject parent, StateObject... stateObjects) {
		super(parent, stateObjects);
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
	public ConcatExpression getExpression() {
		return (ConcatExpression) super.getExpression();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isEquivalent(StateObject stateObject) {
		return super.isEquivalent(stateObject) &&
		       areChildrenEquivalent((ConcatExpressionStateObject) stateObject);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String listName() {
		return STRING_PRIMARY_STATE_OBJECT_LIST;
	}

	/**
	 * Keeps a reference of the {@link ConcatExpression parsed object} object, which should only be
	 * done when this object is instantiated during the conversion of a parsed JPQL query into
	 * {@link StateObject StateObjects}.
	 *
	 * @param expression The {@link ConcatExpression parsed object} representing a <code><b>CONCAT</b></code>
	 * expression
	 */
	public void setExpression(ConcatExpression expression) {
		super.setExpression(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void toTextInternal(Appendable writer) throws IOException {
		writer.append(CONCAT);
		writer.append(LEFT_PARENTHESIS);
		toStringItems(writer, true);
		writer.append(RIGHT_PARENTHESIS);
	}
}