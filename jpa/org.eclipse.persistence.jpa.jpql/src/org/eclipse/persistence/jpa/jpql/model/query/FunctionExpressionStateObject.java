/*******************************************************************************
 * Copyright (c) 2012 Oracle and/or its affiliates. All rights reserved.
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
import java.util.Collection;
import java.util.List;
import org.eclipse.persistence.jpa.jpql.ExpressionTools;
import org.eclipse.persistence.jpa.jpql.model.IListChangeListener;
import org.eclipse.persistence.jpa.jpql.parser.FunctionExpression;
import org.eclipse.persistence.jpa.jpql.util.iterator.CloneListIterator;
import org.eclipse.persistence.jpa.jpql.util.iterator.IterableListIterator;

import static org.eclipse.persistence.jpa.jpql.parser.AbstractExpression.*;

/**
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public class FunctionExpressionStateObject extends AbstractStateObject
                                           implements ListHolderStateObject<StateObject> {

	/**
	 * The name of the native database function.
	 */
	private String functionName;

	/**
	 * The JPQL identifier of the SQL expression.
	 */
	private String identifier;

	/**
	 * The list of {@link StateObject} representing the arguments to pass to the native database function.
	 */
	private List<StateObject> items;

	/**
	 * Notifies the list of arguments has changed.
	 */
	public static final String ARGUMENTS_LIST = "arguments";

	/**
	 * Notifies the function name property has changed.
	 */
	public static final String FUNCTION_NAME_PROPERTY = "functionName";

	/**
	 * Creates a new <code>AbstractFunctionExpressionStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public FunctionExpressionStateObject(StateObject parent) {
		super(parent);
	}

	/**
	 * Creates a new <code>AbstractFunctionExpressionStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @param identifier The JPQL identifier of the SQL expression
	 * @param functionName The name of the native database function
	 * @param arguments The list of {@link StateObject} representing the arguments to pass to the
	 * native database function
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public FunctionExpressionStateObject(StateObject parent,
	                                     String identifier,
	                                     String functionName,
	                                     List<? extends StateObject> arguments) {

		super(parent);
		this.identifier = identifier;
		this.functionName = ExpressionTools.unquote(functionName);
		this.items.addAll(parent(arguments));
	}

	/**
	 * {@inheritDoc}
	 */
	public void accept(StateObjectVisitor visitor) {
		acceptUnknownVisitor(visitor);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void addChildren(List<StateObject> children) {
		super.addChildren(children);
		children.addAll(items);
	}

	/**
	 * {@inheritDoc}
	 */
	public <S extends StateObject> S addItem(S item) {
		getChangeSupport().addItem(this, items, ARGUMENTS_LIST, parent(item));
		return item;
	}

	/**
	 * {@inheritDoc}
	 */
	public void addItems(List<? extends StateObject> items) {
		getChangeSupport().addItems(this, this.items, ARGUMENTS_LIST, parent(items));
	}

	/**
	 * {@inheritDoc}
	 */
	public void addListChangeListener(String listName, IListChangeListener<StateObject> listener) {
		getChangeSupport().addListChangeListener(listName, listener);
	}

	/**
	 * Determines whether the children of this {@link StateObject} are equivalent to the children
	 * of the given one, i.e. the information of the {@link StateObject StateObjects} is the same.
	 *
	 * @param stateObject The {@link StateObject} to compare its children to this one's children
	 * @return <code>true</code> if both have equivalent children; <code>false</code> otherwise
	 */
	protected boolean areChildrenEquivalent(FunctionExpressionStateObject stateObject) {

		int size = itemsSize();

		if (size != stateObject.itemsSize()) {
			return false;
		}

		for (int index = size; --index >= 0; ) {

			StateObject child1 = getItem(index);
			StateObject child2 = stateObject.getItem(index);

			if (!child1.isEquivalent(child2)) {
				return false;
			}
		}

		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean canMoveDown(StateObject item) {
		return getChangeSupport().canMoveDown(items, item);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean canMoveUp(StateObject item) {
		return getChangeSupport().canMoveUp(items, item);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public FunctionExpression getExpression() {
		return (FunctionExpression) super.getExpression();
	}

	/**
	 * Returns the name of the native database function.
	 *
	 * @return The new name of the native database function
	 */
	public String getFunctionName() {
		return functionName;
	}

	/**
	 * Returns the JPQL identifier of this expression.
	 *
	 * @return The JPQL identifier
	 */
	public String getIdentifier() {
		return identifier;
	}

	/**
	 * {@inheritDoc}
	 */
	public StateObject getItem(int index) {
		return items.get(index);
	}

	/**
	 * Returns the quoted name of the native database function.
	 *
	 * @return The new name of the native database function, which is quoted
	 */
	public String getQuotedFunctionName() {
		return ExpressionTools.quote(functionName);
	}

	/**
	 * Determines whether the name of the native SQL function has been specified.
	 *
	 * @return <code>true</code> if the function name has been specified; <code>false</code>
	 * otherwise
	 */
	public boolean hasFunctionName() {
		return functionName != null;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean hasItems() {
		return !items.isEmpty();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initialize() {
		super.initialize();
		items = new ArrayList<StateObject>();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isEquivalent(StateObject stateObject) {

		if (super.isEquivalent(stateObject)) {
			FunctionExpressionStateObject function = (FunctionExpressionStateObject) stateObject;
			return ExpressionTools.valuesAreEqual(functionName, function.functionName) &&
			       areChildrenEquivalent(function);
		}

		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	public IterableListIterator<StateObject> items() {
		return new CloneListIterator<StateObject>(items);
	}

	/**
	 * {@inheritDoc}
	 */
	public int itemsSize() {
		return items.size();
	}

	/**
	 * {@inheritDoc}
	 */
	public StateObject moveDown(StateObject item) {
		getChangeSupport().moveDown(this, items, ARGUMENTS_LIST, item);
		return item;
	}

	/**
	 * {@inheritDoc}
	 */
	public StateObject moveUp(StateObject item) {
		getChangeSupport().moveUp(this, items, ARGUMENTS_LIST, item);
		return item;
	}

	/**
	 * {@inheritDoc}
	 */
	public void removeItem(StateObject item) {
		getChangeSupport().removeItem(this, items, ARGUMENTS_LIST, item);
	}

	/**
	 * {@inheritDoc}
	 */
	public void removeItems(Collection<StateObject> items) {
		getChangeSupport().removeItems(this, this.items, ARGUMENTS_LIST, items);
	}

	/**
	 * {@inheritDoc}
	 */
	public void removeListChangeListener(String listName, IListChangeListener<StateObject> listener) {
		getChangeSupport().removeListChangeListener(listName, listener);
	}

	/**
	 * Keeps a reference of the {@link FunctionExpression parsed object} object, which should only be
	 * done when this object is instantiated during the conversion of a parsed JPQL query into
	 * {@link StateObject StateObjects}.
	 *
	 * @param expression The {@link FunctionExpression parsed object} representing a SQL expression
	 */
	public void setExpression(FunctionExpression expression) {
		super.setExpression(expression);
	}

	/**
	 * Sets the unquoted name of the native database function.
	 *
	 * @param functionName The new name of the native database function, which should be unquoted
	 */
	public void setFunctionName(String functionName) {
		String oldFunctionName = this.functionName;
		this.functionName = functionName;
		firePropertyChanged(FUNCTION_NAME_PROPERTY, oldFunctionName, functionName);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void toTextInternal(Appendable writer) throws IOException {

		// Function name
		if (functionName != null) {
			writer.append(SINGLE_QUOTE);
			writer.append(functionName);
			writer.append(SINGLE_QUOTE);
		}

		if (hasItems()) {
			writer.append(COMMA);
			writer.append(SPACE);
			toStringItems(writer, items, true);
		}
	}
}