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
package org.eclipse.persistence.jpa.jpql.model;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.persistence.jpa.jpql.model.query.ConstructorExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.ObjectExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.ResultVariableStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.SelectClauseStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.StateObject;

/**
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public abstract class AbstractSelectExpressionStateObjectBuilder extends AbstractScalarExpressionStateObjectBuilder<ISelectExpressionStateObjectBuilder>
                                                                 implements ISelectExpressionStateObjectBuilder {

	/**
	 * The list of select items, which were added by invoking {@link #append(ISelectExpressionStateObjectBuilder)}.
	 */
	protected List<StateObject> stateObjectList;

	/**
	 * Creates a new <code>AbstractSelectExpressionStateObjectBuilder</code>.
	 *
	 * @param parent The select clause for which this builder can create a select expression
	 */
	protected AbstractSelectExpressionStateObjectBuilder(SelectClauseStateObject parent) {
		super(parent);
		stateObjectList = new ArrayList<StateObject>();
	}

	/**
	 * {@inheritDoc}
	 */
	public ISelectExpressionStateObjectBuilder append() {
		stateObjectList.add(pop());
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	public void commit() {
		if (hasStateObjects()) {
			append();
		}
		getParent().addItems(stateObjectList);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected SelectClauseStateObject getParent() {
		return (SelectClauseStateObject) super.getParent();
	}

	/**
	 * {@inheritDoc}
	 */
	public ISelectExpressionStateObjectBuilder new_(String className,
	                                                ISelectExpressionStateObjectBuilder... parameters) {

		checkBuilders(parameters);

		StateObject stateObject = new ConstructorExpressionStateObject(
			getParent(),
			className,
			stateObjects(parameters)
		);
		add(stateObject);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	public ISelectExpressionStateObjectBuilder object(String identificationVariable) {
		StateObject stateObject = new ObjectExpressionStateObject(getParent(), identificationVariable);
		add(stateObject);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	public ISelectExpressionStateObjectBuilder resultVariable(String resultVariable) {
		resultVariable(resultVariable, false);
		return this;
	}

	protected void resultVariable(String resultVariable, boolean as) {

		StateObject stateObject = new ResultVariableStateObject(
			getParent(),
			pop(),
			as,
			resultVariable
		);

		add(stateObject);
	}

	/**
	 * {@inheritDoc}
	 */
	public ISelectExpressionStateObjectBuilder resultVariableAs(String resultVariable) {
		resultVariable(resultVariable, true);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	public ISelectExpressionStateObjectBuilder variable(String variable) {
		StateObject stateObject = buildIdentificationVariable(variable);
		add(stateObject);
		return this;
	}
}