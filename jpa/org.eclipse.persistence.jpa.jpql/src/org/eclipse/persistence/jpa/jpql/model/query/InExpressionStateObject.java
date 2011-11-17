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
import org.eclipse.persistence.jpa.jpql.parser.InExpression;
import org.eclipse.persistence.jpa.jpql.parser.InItemBNF;

import static org.eclipse.persistence.jpa.jpql.parser.AbstractExpression.*;
import static org.eclipse.persistence.jpa.jpql.parser.Expression.*;

/**
 * The state field path expression must have a string, numeric, or enum value. The literal and/or
 * input parameter values must be like the same abstract schema type of the state field path
 * expression in type.
 * <p>
 * The results of the subquery must be like the same abstract schema type of the state field path
 * expression in type.
 * <p>
 * There must be at least one element in the comma separated list that defines the set of values for
 * the <code><b>IN</b></code> expression. If the value of a state field path expression in an
 * <code><b>IN</b></code> or <code><b>NOT IN</b></code> expression is <code><b>NULL</b></code> or
 * unknown, the value of the expression is unknown.
 * <p>
 * JPA 1.0:
 * <div nowrap><b>BNF:</b> <code>in_expression ::= state_field_path_expression [NOT] IN(in_item {, in_item}* | subquery)</code><p>
 * JPA 2.0
 * <div nowrap><b>BNF:</b> <code>in_expression ::= {state_field_path_expression | type_discriminator} [NOT] IN { ( in_item {, in_item}* ) | (subquery) | collection_valued_input_parameter }</code><p>
 *
 * @see InExpression
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings({"nls", "unused"}) // unused used for the import statement: see bug 330740
public class InExpressionStateObject extends AbstractListHolderStateObject<StateObject> {

	/**
	 * Determines whether the <code><b>NOT</b></code> identifier is part of the expression or not.
	 */
	private boolean not;

	/**
	 * Determines whether what was parsed after the <code>IN</code> identifier is a single input
	 * parameter.
	 */
	private boolean singleInputParameter;

	/**
	 * The {@link StateObject} representing either the state field path expression or the type
	 * discriminator.
	 */
	private StateObject stateObject;

	/**
	 * Notifies the list of items has changed.
	 */
	public static String ITEMS_LIST = "items";

	/**
	 * Notifies the visibility of the <code><b>NOT</b></code> identifier has changed.
	 */
	public static final String NOT_PROPERTY = "not";

	/**
	 * Notifies the {@link StateObject} representing the state field path expression or the input
	 * parameter has changed.
	 */
	public static final String STATE_OBJECT_PROPERTY = "stateObject";

	/**
	 * Creates a new <code>InExpressionStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public InExpressionStateObject(StateObject parent) {
		super(parent);
	}

	/**
	 * Creates a new <code>InExpressionStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @param stateFieldPath
	 * @param not Determines whether the <code><b>NOT</b></code> identifier is part of the expression
	 * or not
	 * @param inItems The list of JPQL fragments that will be parsed and converted into {@link
	 * StateObject}
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public InExpressionStateObject(StateObject parent,
	                               boolean not,
	                               String path,
	                               List<String> items) {

		super(parent);
		// TODO
	}

	/**
	 * Creates a new <code>InExpressionStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @param path
	 * @param not Determines whether the <code><b>NOT</b></code> identifier is part of the expression
	 * or not
	 * @param parameter The
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public InExpressionStateObject(StateObject parent,
	                               boolean not,
	                               String path,
	                               String parameter) {

		super(parent);
		this.not = not;
		parse(path);
		addItem(new InputParameterStateObject(this, parameter));
	}

	/**
	 * Creates a new <code>InExpressionStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @param stateFieldPath
	 * @param not Determines whether the <code><b>NOT</b></code> identifier is part of the expression
	 * or not
	 * @param items
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public InExpressionStateObject(StateObject parent,
	                               StateObject stateObject,
	                               boolean not,
	                               List<? extends StateObject> items) {

		super(parent, items);
		this.not         = not;
		this.stateObject = parent(stateObject);
	}

	/**
	 * Creates a new <code>InExpressionStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @param stateFieldPath
	 * @param items
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public InExpressionStateObject(StateObject parent,
	                               StateObject stateFieldPath,
	                               List<? extends StateObject> items) {

		this(parent, stateFieldPath, false, items);
	}

	/**
	 * Creates a new <code>InExpressionStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @param path
	 * @param inItems The list of JPQL fragments that will be parsed and converted into {@link
	 * StateObject}
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public InExpressionStateObject(StateObject parent,
	                               String path,
	                               List<String> items) {

		this(parent, false, path, items);
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
		if (stateObject != null) {
			children.add(stateObject);
		}
		super.addChildren(children);
	}

	/**
	 * Makes sure the <code><b>NOT</b></code> identifier is specified.
	 *
	 * @return This object
	 */
	public InExpressionStateObject addNot() {
		if (!not) {
			setNot(true);
		}
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public InExpression getExpression() {
		return (InExpression) super.getExpression();
	}

	/**
	 * Returns
	 *
	 * @return
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
	 * Determines whether
	 *
	 * @return
	 */
	public boolean hasStateObject() {
		return stateObject != null;
	}

	/**
	 * Determines whether what was parsed after the <code>IN</code> identifier is a single input
	 * parameter.
	 *
	 * @return <code>true</code> if the only item is an input parameter; <code>false</code> otherwise
	 */
	public boolean isSingleInputParameter() {
		return singleInputParameter;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String listName() {
		return ITEMS_LIST;
	}

	/**
	 * Parses the given JPQL fragment, which represents either a single or many items, the fragment
	 * will be parsed and converted into {@link StateObject}.
	 *
	 * @param jpqlFragment The portion of the query to parse
	 */
	public void parse(String jpqlFragment) {
		StateObject stateObject = buildStateObject(jpqlFragment, InItemBNF.ID);
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
	 * Keeps a reference of the {@link InExpression parsed object} object, which should only be
	 * done when this object is instantiated during the conversion of a parsed JPQL query into
	 * {@link StateObject StateObjects}.
	 *
	 * @param expression The {@link InExpression parsed object} representing an <code><b>IN</b></code>
	 * expression
	 */
	public void setExpression(InExpression expression) {
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
	 * Sets whether what was parsed after the <code>IN</code> identifier is a single input parameter.
	 *
	 * @param singleInputParameter <code>true</code> if the only item is an input parameter;
	 * <code>false</code> otherwise
	 */
	public void setSingleInputParameter(boolean singleInputParameter) {
		this.singleInputParameter = singleInputParameter;
	}

	/**
	 * Sets
	 *
	 * @param stateObject
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

		if (stateObject != null) {
			stateObject.toString(writer);
			writer.append(SPACE);
		}

		writer.append(not ? NOT_IN : IN);
		writer.append(SPACE);

		if (singleInputParameter) {
			writer.append(LEFT_PARENTHESIS);
		}

		toStringItems(writer, true);

		if (singleInputParameter) {
			writer.append(RIGHT_PARENTHESIS);
		}
	}
}