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
import org.eclipse.persistence.jpa.jpql.parser.EmptyCollectionComparisonExpression;

import static org.eclipse.persistence.jpa.jpql.parser.AbstractExpression.*;
import static org.eclipse.persistence.jpa.jpql.parser.Expression.*;

/**
 * This expression tests whether or not the collection designated by the collection-valued path
 * expression is empty (i.e, has no elements).
 * <p>
 * <div nowrap><b>BNF:</b> <code>empty_collection_comparison_expression ::= collection_valued_path_expression IS [NOT] EMPTY</code><p>
 *
 * @see EmptyCollectionComparisonExpression
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings({"nls", "unused"}) // unused used for the import statement: see bug 330740
public class EmptyCollectionComparisonExpressionStateObject extends AbstractStateObject {

	/**
	 * Determines whether the <code><b>NOT</b></code> identifier is part of the expression or not.
	 */
	private boolean not;

	/**
	 * The {@link StateObject} representing the collection-valued path expression.
	 */
	private CollectionValuedPathExpressionStateObject stateObject;

	/**
	 * Notifies the visibility of the <code><b>NOT</b></code> identifier has changed.
	 */
	public static String NOT_PROPERTY = "not";

	/**
	 * Creates a new <code>EmptyCollectionComparisonExpressionStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public EmptyCollectionComparisonExpressionStateObject(StateObject parent) {
		super(parent);
	}

	/**
	 * Creates a new <code>EmptyCollectionComparisonExpressionStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @param not Determines whether the <code><b>NOT</b></code> identifier is part of the expression
	 * or not
	 * @param path The collection-valued path expression
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public EmptyCollectionComparisonExpressionStateObject(StateObject parent,
	                                                      boolean not,
	                                                      String path) {

		super(parent);
		this.not         = not;
		this.stateObject.setPath(path);
	}

	/**
	 * Creates a new <code>EmptyCollectionComparisonExpressionStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @param path The collection-valued path expression
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public EmptyCollectionComparisonExpressionStateObject(StateObject parent, String path) {
		this(parent, false, path);
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
	protected void addChildren(List<StateObject> children) {
		super.addChildren(children);
		children.add(stateObject);
	}

	/**
	 * Makes sure the <code><b>NOT</b></code> identifier is specified.
	 *
	 * @return This object
	 */
	public EmptyCollectionComparisonExpressionStateObject addNot() {
		if (!not) {
			setNot(true);
		}
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public EmptyCollectionComparisonExpression getExpression() {
		return (EmptyCollectionComparisonExpression) super.getExpression();
	}

	/**
	 * Returns the {@link CollectionValuedPathExpressionStateObject} representing the collection-
	 * valued path expression.
	 *
	 * @return The {@link CollectionValuedPathExpressionStateObject} representing the collection-
	 * valued path expression, which is never <code>null</code>
	 */
	public CollectionValuedPathExpressionStateObject getStateObject() {
		return stateObject;
	}

	/**
	 * Determines whether the <code><b>NOT</b></code> identifier is used or not.
	 *
	 * @return <code>true</code> if the <code><b>NOT</b></code> identifier is part of the expression;
	 * <code>false</code> otherwise
	 */
	public boolean hasNot() {
		return not;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initialize() {
		super.initialize();
		stateObject = new CollectionValuedPathExpressionStateObject(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isEquivalent(StateObject stateObject) {

		if (super.isEquivalent(stateObject)) {
			EmptyCollectionComparisonExpressionStateObject collection = (EmptyCollectionComparisonExpressionStateObject) stateObject;
			return not == collection.not &&
			       stateObject.isEquivalent(collection.stateObject);
		}

		return false;
	}

	/**
	 * Makes sure the <code><b>NOT</b></code> identifier is not specified.
	 */
	public void removeNot() {
		if (not) {
			setNot(false);
		}
	}

	/**
	 * Keeps a reference of the {@link EmptyCollectionComparison parsed object} object, which should
	 * only be done when this object is instantiated during the conversion of a parsed JPQL query
	 * into {@link StateObject StateObjects}.
	 *
	 * @param expression The {@link EmptyCollectionComparison parsed object} representing an
	 * <code><b>EMPTY</b></code expression
	 */
	public void setExpression(EmptyCollectionComparisonExpression expression) {
		super.setExpression(expression);
	}

	/**
	 * Sets whether the <code><b>NOT</b></code> identifier should be part of the expression or not.
	 *
	 * @param not <code>true</code> if the <code><b>NOT</b></code> identifier should be part of the
	 * expression; <code>false</code> otherwise
	 */
	public void setNot(boolean not) {
		boolean oldNot = this.not;
		this.not = not;
		firePropertyChanged(NOT_PROPERTY, oldNot, not);
	}

	/**
	 * Changes the visibility state of the <code><b>NOT</b></code> identifier.
	 */
	public void toggleNot() {
		setNot(!not);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void toTextInternal(Appendable writer) throws IOException {

		stateObject.toString(writer);
		writer.append(SPACE);

		if (not) {
			writer.append(IS_NOT_EMPTY);
		}
		else {
			writer.append(IS_EMPTY);
		}
	}
}