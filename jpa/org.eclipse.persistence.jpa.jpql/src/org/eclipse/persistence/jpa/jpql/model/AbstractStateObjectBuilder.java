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
package org.eclipse.persistence.jpa.jpql.model;

import java.util.Stack;
import org.eclipse.persistence.jpa.jpql.Assert;
import org.eclipse.persistence.jpa.jpql.model.query.StateObject;

/**
 * The abstract definition of a builder of a {@link StateObject} hierarchy based on a JPQL fragment
 * that is manually created.
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public abstract class AbstractStateObjectBuilder {

	/**
	 * The stack is used to store the {@link StateObject StateObjects} that were created, which
	 * will be used to properly create the expression.
	 */
	private final Stack<StateObject> stateObjects;

	/**
	 * Creates a new <code>AbstractStateObjectBuilder</code>.
	 */
	protected AbstractStateObjectBuilder() {
		super();
		stateObjects = new Stack<StateObject>();
	}

	/**
	 * Adds the given {@link StateObject} to the stack for future use, which will be removed from
	 * the stack to complete the creation of another {@link StateObject}.
	 *
	 * @param stateObject The newly created {@link StateObject}
	 */
	protected void add(StateObject stateObject) {
		stateObjects.add(stateObject);
	}

	/**
	 * Makes sure the given {@link IScalarExpressionStateObjectBuilder<?> builder} is this one.
	 *
	 * @param builder The builder that was passed as an argument, which is only meant to create the
	 * stack of {@link StateObject StateObjects} in the right order
	 */
	protected final void checkBuilder(IScalarExpressionStateObjectBuilder<?> builder) {
		Assert.isEqual(this, builder, "Both builders have to be the same");
	}

	/**
	 * Makes sure the given {@link IScalarExpressionStateObjectBuilder<?> builder} is this one.
	 *
	 * @param builders The builders that were passed as arguments, which is only meant to create the
	 * stack of {@link StateObject StateObjects} in the right order
	 */
	protected final <T extends IScalarExpressionStateObjectBuilder<?>> void checkBuilders(T... builders) {
		for (IScalarExpressionStateObjectBuilder<?> builder : builders) {
			checkBuilder(builder);
		}
	}

	/**
	 * Determines whether the stack of {@link StateObject StateObjects} is not empty.
	 *
	 * @return <code>true</code> if the stack is not empty; <code>false</code> otherwise
	 */
	protected boolean hasStateObjects() {
		return !stateObjects.isEmpty();
	}

	/**
	 * Retrieves the {@link StateObject} that is on the stack.
	 *
	 * @return The last {@link StateObject} that was added on the stack
	 */
	protected final StateObject pop() {
		return stateObjects.pop();
	}
}