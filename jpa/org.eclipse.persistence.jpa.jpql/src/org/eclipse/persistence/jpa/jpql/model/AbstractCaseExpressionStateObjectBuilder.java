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

import org.eclipse.persistence.jpa.jpql.model.query.CaseExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.StateObject;
import org.eclipse.persistence.jpa.jpql.model.query.WhenClauseStateObject;

/**
 * This abstract implementation of {@link ICaseExpressionStateObjectBuilder} adds support for
 * creating a <code><b>CASE</b></code> expression.
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public abstract class AbstractCaseExpressionStateObjectBuilder extends AbstractConditionalExpressionStateObjectBuilder<ICaseExpressionStateObjectBuilder>
                                                               implements ICaseExpressionStateObjectBuilder {

	/**
	 * Keeps track of the actual {@link CaseExpressionStateObject} since it is needed when creating
	 * new {@link WhenClauseStateObject} due to strongly typed parent.
	 */
	private CaseExpressionStateObject caseExpressionStateObject;

	/**
	 * Creates a new <code>AbstractCaseExpressionStateObjectBuilder</code>.
	 *
	 * @param parent The parent of the <code><b>CASE</b></code> expression to build, which is only
	 * required when a JPQL fragment needs to be parsed
	 */
	protected AbstractCaseExpressionStateObjectBuilder(StateObject parent) {
		super(parent);
		caseExpressionStateObject = new CaseExpressionStateObject(parent);
	}

	/**
	 * {@inheritDoc}
	 */
	public CaseExpressionStateObject buildStateObject() {
		caseExpressionStateObject.setElse(pop());
		if (hasStateObjects()) {
			caseExpressionStateObject.setCaseOperand(pop());
		}
		return caseExpressionStateObject;
	}

	/**
	 * {@inheritDoc}
	 */
	public ICaseExpressionStateObjectBuilder when(ICaseExpressionStateObjectBuilder when,
	                                              ICaseExpressionStateObjectBuilder then) {

		checkBuilders(when, then);

		StateObject thenStateObject = pop();
		StateObject whenStateObject = pop();

		WhenClauseStateObject stateObject = new WhenClauseStateObject(
			caseExpressionStateObject,
			whenStateObject,
			thenStateObject
		);

		caseExpressionStateObject.addItem(stateObject);
		return this;
	}
}