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
import org.eclipse.persistence.jpa.jpql.Assert;
import org.eclipse.persistence.jpa.jpql.parser.OrderByItem;
import org.eclipse.persistence.jpa.jpql.parser.OrderByItem.Ordering;
import org.eclipse.persistence.jpa.jpql.parser.OrderByItemBNF;

import static org.eclipse.persistence.jpa.jpql.parser.AbstractExpression.*;

/**
 * An <code><b>ORDER BY</b></code> item must be one of the following:
 * <ol>
 * <li> A {@link StateFieldPathExpression state_field_path_expression} that evaluates to an orderable
 * state field of an entity or embeddable class abstract schema type designated in the
 * <code><b>SELECT</b></code> clause by one of the following:
 *   <ul>
 *   <li>A general_identification_variable
 *   <li>A single_valued_object_path_expression
 *   </ul>
 * <li>A {@link StateFieldPathExpression state_field_path_expression} that evaluates to the same
 * state field of the same entity or embeddable abstract schema type as a {@link StateFieldPathExpression
 * state_field_path_expression} in the <b>SELECT</b> clause
 * <li>A {@link ResultVariable result_variable} that refers to an orderable item in the
 * <code><b>SELECT</b></code> clause for which the same {@link ResultVariableStateObject
 * result_variable} has been specified. This may be the result of an aggregate_expression, a {@link
 * ScalarExpression scalar_expression}, or a {@link StateFieldPathExpression state_field_path_expression}
 * in the <code><b>SELECT</b></code> clause.
 * </ol>
 * <p>
 * The keyword <code><b>ASC</b></code> specifies that ascending ordering be used for the associated
 * <code><b>ORDER BY</b></code> item; the keyword <code><b>DESC</b></code> specifies that descending
 * ordering be used. Ascending ordering is the default.
 * <p>
 * JPA 1.0: <div nowrap><b>BNF:</b> <code>orderby_item ::= state_field_path_expression [ ASC | DESC ]</code>
 * <p>
 * JPA 2.0 <div nowrap><b>BNF:</b> <code>orderby_item ::= state_field_path_expression | result_variable [ ASC | DESC ]</code><p>
 *
 * @see OrderByItem
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public class OrderByItemStateObject extends AbstractStateObject {

	/**
	 * The ascending ordering.
	 */
	private Ordering ordering;

	/**
	 * The {@link StateObject} representing the order by item.
	 */
	private StateObject stateObject;

	/**
	 * Notifies the ordering property has changed.
	 */
	public static final String ORDERING_PROPERTY = "ordering";

	/**
	 * Notifies the ordering property has changed.
	 */
	public static final String STATE_OBJECT_PROPERTY = "stateObject";

	/**
	 * Creates a new <code>OrderByItemStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public OrderByItemStateObject(OrderByClauseStateObject parent) {
		super(parent);
	}

	/**
	 * Creates a new <code>OrderByItemStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public OrderByItemStateObject(OrderByClauseStateObject parent, Ordering ordering) {
		super(parent);
		validateOrdering(ordering);
		this.ordering = ordering;
	}

	/**
	 * Creates a new <code>OrderByItemStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @param stateObject The {@link StateObject} of the item
	 * @param ordering One of the possible {@link Ordering} choice
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public OrderByItemStateObject(OrderByClauseStateObject parent,
	                              StateObject stateObject,
	                              Ordering ordering) {

		this(parent, ordering);
		this.stateObject = parent(stateObject);
	}

	/**
	 * Creates a new <code>OrderByItemStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @param stateObject The {@link StateObject} of the item
	 * @param ordering One of the possible {@link Ordering} choice
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public OrderByItemStateObject(OrderByClauseStateObject parent,
	                              String jpqlFragment,
	                              Ordering ordering) {

		this(parent, ordering);
		parse(jpqlFragment);
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
	 * {@inheritDoc}
	 */
	@Override
	public OrderByItem getExpression() {
		return (OrderByItem) super.getExpression();
	}

	/**
	 * Returns the enum constant representing the ordering type.
	 *
	 * @return The constant representing the ordering, in the case the ordering was not parsed, then
	 * {@link Ordering#DEFAULT} is returned
	 */
	public Ordering getOrdering() {
		return ordering;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public OrderByClauseStateObject getParent() {
		return (OrderByClauseStateObject) super.getParent();
	}

	/**
	 * Returns the {@link StateObject} representing the value used for ordering.
	 *
	 * @return The {@link StateObject} representing the value used for ordering
	 */
	public StateObject getStateObject() {
		return stateObject;
	}

	/**
	 * Determines whether the {@link StateObject} representing the value used for ordering has been
	 * defined.
	 *
	 * @return <code>true</code> if the ordering value is defined; <code>false</code> otherwise
	 */
	public boolean hasStateObject() {
		return stateObject != null;
	}

	/**
	 * Parses the given JPQL fragment, which represents either a state-field pathe expression or a
	 * result variable, and creates the {@link StateObject}.
	 *
	 * @param jpqlFragment The portion of the query representing a state-field path expression or
	 * result variable
	 */
	public void parse(String jpqlFragment) {
		StateObject stateObject = buildStateObject(jpqlFragment, OrderByItemBNF.ID);
		setStateObject(stateObject);
	}

	/**
	 * Removes the ordering if it's specified, otherwise do nothing.
	 */
	public void removeOrdering() {
		setOrdering(Ordering.DEFAULT);
	}

	/**
	 * Keeps a reference of the {@link OrderByItem parsed object} object, which should only be
	 * done when this object is instantiated during the conversion of a parsed JPQL query into
	 * {@link StateObject StateObjects}.
	 *
	 * @param expression The {@link OrderByItem parsed object} representing an order by item
	 */
	public void setExpression(OrderByItem expression) {
		super.setExpression(expression);
	}

	/**
	 * Sets the enum constant representing the ordering type.
	 *
	 * @param ordering The constant representing the ordering, in the case the ordering was not
	 * parsed, then {@link Ordering#DEFAULT} should be used
	 */
	public void setOrdering(Ordering ordering) {

		validateOrdering(ordering);

		Ordering oldOrdering = this.ordering;
		this.ordering = ordering;
		firePropertyChanged(ORDERING_PROPERTY, oldOrdering, ordering);
	}

	/**
	 * Sets the {@link StateObject} representing the value used for ordering.
	 *
	 * @param stateObject The {@link StateObject} representing the value used for ordering
	 */
	public void setStateObject(StateObject stateObject) {
		StateObject oldStateObject = this.stateObject;
		this.stateObject = parent(stateObject);
		firePropertyChanged(STATE_OBJECT_PROPERTY, oldStateObject, stateObject);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void toTextInternal(Appendable writer) throws IOException {

		if (stateObject != null) {
			stateObject.toString(writer);
		}

		if (ordering != Ordering.DEFAULT) {
			writer.append(SPACE);
			writer.append(ordering.name());
		}
	}

	private void validateOrdering(Ordering ordering) {
		Assert.isNotNull(ordering, "The Ordering cannot be null");
	}
}