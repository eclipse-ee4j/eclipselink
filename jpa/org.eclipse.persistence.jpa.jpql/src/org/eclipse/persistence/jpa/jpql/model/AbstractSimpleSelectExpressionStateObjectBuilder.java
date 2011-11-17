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

import org.eclipse.persistence.jpa.jpql.model.query.AbstractSelectClauseStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.StateObject;

/**
 * The abstract implementation of {@link ISimpleSelectExpressionStateObjectBuilder} that supports
 * the creation of the select expression based on the JPQL grammar defined in JPA 2.0.
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public abstract class AbstractSimpleSelectExpressionStateObjectBuilder extends AbstractScalarExpressionStateObjectBuilder<ISimpleSelectExpressionStateObjectBuilder>
                                                                       implements ISimpleSelectExpressionStateObjectBuilder {

	/**
	 * Creates a new <code>AbstractSimpleSelectExpressionStateObjectBuilder</code>.
	 *
	 * @param parent The select clause for which this builder can create a select expression
	 */
	protected AbstractSimpleSelectExpressionStateObjectBuilder(AbstractSelectClauseStateObject parent) {
		super(parent);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected AbstractSelectClauseStateObject getParent() {
		return (AbstractSelectClauseStateObject) super.getParent();
	}

	/**
	 * {@inheritDoc}
	 */
	public ISimpleSelectExpressionStateObjectBuilder variable(String variable) {
		StateObject stateObject = buildIdentificationVariable(variable);
		add(stateObject);
		return this;
	}
}