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
import org.eclipse.persistence.jpa.jpql.parser.NullComparisonExpression;
import org.eclipse.persistence.jpa.jpql.parser.PreLiteralExpressionBNF;

import static org.eclipse.persistence.jpa.jpql.parser.AbstractExpression.*;
import static org.eclipse.persistence.jpa.jpql.parser.Expression.*;

/**
 * A null comparison tests whether or not the single-valued path expression or input parameter is a
 * <code><b>NULL</b></code> value.
 * <p>
 * <div nowrap><b>BNF:</b> <code>null_comparison_expression ::= {single_valued_path_expression | input_parameter} IS [NOT] NULL</code><p>
 *
 * @see NullComparisonExpression
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings({"nls", "unused"}) // unused used for the import statement: see bug 330740
public class NullComparisonExpressionStateObject extends AbstractStateObject {

	/**
	 * Determines whether the <code><b>NOT</b></code> identifier is part of the expression or not.
	 */
	private boolean not;

	/**
	 * The {@link StateObject} representing the collection-valued path expression.
	 */
	private StateObject stateObject;

	/**
	 * Notifies the visibility of the <code><b>NOT</b></code> identifier has changed.
	 */
	public static String NOT_PROPERTY = "not";

	/**
	 * Notifies the state object property has changed.
	 */
	public static final String STATE_OBJECT_PROPERTY = "stateObject";

	/**
	 * Creates a new <code>NullComparisonExpressionStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public NullComparisonExpressionStateObject(StateObject parent) {
		super(parent);
	}

	/**
	 * Creates a new <code>NullComparisonExpressionStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @param not Determines whether the <code><b>NOT</b></code> identifier is part of the expression
	 * or not
	 * @param stateObject The {@link StateObject} representing the collection-valued path expression
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public NullComparisonExpressionStateObject(StateObject parent,
	                                           boolean not,
	                                           StateObject stateObject) {

		super(parent);
		this.not         = not;
		this.stateObject = parent(stateObject);
	}

	/**
	 * Creates a new <code>NullComparisonExpressionStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @param not Determines whether the <code><b>NOT</b></code> identifier is part of the expression
	 * or not
	 * @param path Either the singled-valued path expression or the input parameter
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public NullComparisonExpressionStateObject(StateObject parent,
	                                           boolean not,
	                                           String path) {

		super(parent);
		this.not = not;
		parse(path);
	}

	/**
	 * Creates a new <code>NullComparisonExpressionStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @param stateObject The {@link StateObject} representing the collection-valued path expression
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public NullComparisonExpressionStateObject(StateObject parent, StateObject stateObject) {
		this(parent, false, stateObject);
	}

	/**
	 * Creates a new <code>NullComparisonExpressionStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @param path Either the singled-valued path expression or the input parameter
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public NullComparisonExpressionStateObject(StateObject parent, String path) {
		super(parent);
		parse(path);
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
		if (stateObject != null) {
			children.add(stateObject);
		}
	}

	/**
	 * Makes sure the <code><b>NOT</b></code> identifier is specified.
	 *
	 * @return This object
	 */
	public NullComparisonExpressionStateObject addNot() {
		if (!not) {
			setNot(true);
		}
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public NullComparisonExpression getExpression() {
		return (NullComparisonExpression) super.getExpression();
	}

	/**
	 * Returns the {@link StateObject} representing the collection-valued path expression.
	 *
	 * @return The {@link StateObject} representing the collection-valued path expression
	 */
	public StateObject getStateObject() {
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
	 * Determines whether there is a {@link StateObject} representing the collection-valued path
	 * expression.
	 *
	 * @return <code>true</code> if the {@link StateObject} is not <code>null</code>; <code>false</code>
	 * otherwise
	 */
	public boolean hasStateObject() {
		return stateObject != null;
	}

	/**
	 * Parses the given singled-valued path expression or input parameter.
	 *
	 * @param path Either a singled-valued path expression or an input parameter
	 */
	private void parse(String path) {
		StateObject stateObject = buildStateObject(path, PreLiteralExpressionBNF.ID);
		setStateObject(stateObject);
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
	 * Keeps a reference of the {@link NullComparisonExpression parsed object} object, which should only be
	 * done when this object is instantiated during the conversion of a parsed JPQL query into
	 * {@link StateObject StateObjects}.
	 *
	 * @param expression The {@link NullComparisonExpression parsed object} representing the null
	 * expression
	 */
	public void setExpression(NullComparisonExpression expression) {
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
	 * Sets the given {@link StateObject} as the collection-valued path expression.
	 *
	 * @param stateObject The {@link StateObject} that represents the collection-valued path expression
	 */
	public void setStateObject(StateObject stateObject) {
		StateObject oldStateObject = this.stateObject;
		this.stateObject = parent(stateObject);
		firePropertyChanged(STATE_OBJECT_PROPERTY, oldStateObject, stateObject);
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

		if (stateObject != null)  {
			stateObject.toString(writer);
			writer.append(SPACE);
		}

		if (not) {
			writer.append(IS_NOT_NULL);
		}
		else {
			writer.append(IS_NULL);
		}
	}
}