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

import org.eclipse.persistence.jpa.jpql.model.query.SelectClauseStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.StateObject;

/**
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public abstract class AbstractEclipseLinkSelectExpressionStateObjectBuilder extends AbstractSelectExpressionStateObjectBuilder
                                                                            implements IEclipseLinkSelectExpressionStateObjectBuilder {

	/**
	 * Creates a new <code>AbstractEclipseLinkSelectExpressionStateObjectBuilder</code>.
	 *
	 * @param parent The select clause for which this builder can create a select expression
	 */
	protected AbstractEclipseLinkSelectExpressionStateObjectBuilder(SelectClauseStateObject parent) {
		super(parent);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IEclipseLinkSelectExpressionStateObjectBuilder append() {
		return (IEclipseLinkSelectExpressionStateObjectBuilder) super.append();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IEclipseLinkSelectExpressionStateObjectBuilder new_(String className,
	                                                           ISelectExpressionStateObjectBuilder... parameters) {

		return (IEclipseLinkSelectExpressionStateObjectBuilder) super.new_(className, parameters);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IEclipseLinkSelectExpressionStateObjectBuilder object(String identificationVariable) {
		return (IEclipseLinkSelectExpressionStateObjectBuilder) super.object(identificationVariable);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IEclipseLinkSelectExpressionStateObjectBuilder resultVariable(String resultVariable) {
		return (IEclipseLinkSelectExpressionStateObjectBuilder) super.resultVariable(resultVariable);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IEclipseLinkSelectExpressionStateObjectBuilder resultVariableAs(String resultVariable) {
		return (IEclipseLinkSelectExpressionStateObjectBuilder) super.resultVariableAs(resultVariable);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IEclipseLinkSelectExpressionStateObjectBuilder variable(String variable) {
		StateObject stateObject = buildIdentificationVariable(variable);
		add(stateObject);
		return this;
	}
}