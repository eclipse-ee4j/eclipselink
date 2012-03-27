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
import org.eclipse.persistence.jpa.jpql.parser.CollectionExpression;

/**
 * This {@link StateObject} is a temporary object used to store a list of {@link StateObject
 * StateObjects}. Those objects are not parented. {@link StateObjectVisitor} is not aware of this
 * object either, to visit it, the visitor will be notified via reflection, see {@link
 * AbstractStateObject#acceptUnknownVisitor(StateObjectVisitor)}.
 *
 * @see CollectionExpression
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public final class CollectionExpressionStateObject extends AbstractStateObject {

	/**
	 * The list of children owned by this state object.
	 */
	private List<? extends StateObject> items;

	/**
	 * Creates a new <code>CollectionExpressionStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @param items The list of children owned by this state object
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public CollectionExpressionStateObject(StateObject parent, List<? extends StateObject> items) {
		super(parent);
		this.items = items;
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
		children.addAll(items);
	}

	/**
	 * Determines whether the children of this {@link StateObject} are equivalent to the children
	 * of the given one, i.e. the information of the {@link StateObject StateObjects} is the same.
	 *
	 * @param stateObject The {@link StateObject} to compare its children to this one's children
	 * @return <code>true</code> if both have equivalent children; <code>false</code> otherwise
	 */
	protected boolean areChildrenEquivalent(CollectionExpressionStateObject stateObject) {

		int size = items.size();

		if (size != stateObject.items.size()) {
			return false;
		}

		for (int index = size; --index >= 0; ) {

			StateObject child1 = items.get(index);
			StateObject child2 = stateObject.items.get(index);

			if (!child1.isEquivalent(child2)) {
				return false;
			}
		}

		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CollectionExpression getExpression() {
		return (CollectionExpression) super.getExpression();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isEquivalent(StateObject stateObject) {

		if (super.isEquivalent(stateObject)) {
			CollectionExpressionStateObject collection = (CollectionExpressionStateObject) stateObject;
			return areChildrenEquivalent(collection);
		}

		return false;
	}

	/**
	 * Keeps a reference of the {@link CollectionExpression parsed object} object, which should only be
	 * done when this object is instantiated during the conversion of a parsed JPQL query into
	 * {@link StateObject StateObjects}.
	 *
	 * @param expression The {@link CollectionExpression parsed object} representing a collection
	 * expression
	 */
	public void setExpression(CollectionExpression expression) {
		super.setExpression(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void toTextInternal(Appendable writer) throws IOException {
		toStringItems(writer, items, true);
	}
}