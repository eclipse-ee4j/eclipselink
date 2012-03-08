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

import org.eclipse.persistence.jpa.jpql.model.query.AbstractConditionalClauseStateObject;

/**
 * The default implementation of {@link IConditionalExpressionStateObjectBuilder}.
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public class DefaultConditionalExpressionStateObjectBuilder extends AbstractConditionalExpressionStateObjectBuilder<IConditionalExpressionStateObjectBuilder>
                                                            implements IConditionalExpressionStateObjectBuilder {

	/**
	 * Creates a new <code>DefaultConditionalExpressionStateObjectBuilder</code>.
	 *
	 * @param parent The conditional clause for which this builder can create a conditional expression
	 */
	public DefaultConditionalExpressionStateObjectBuilder(AbstractConditionalClauseStateObject parent) {
		super(parent);
	}

	/**
	 * {@inheritDoc}
	 */
	public void commit() {
		getParent().setConditional(pop());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected AbstractConditionalClauseStateObject getParent() {
		return (AbstractConditionalClauseStateObject) super.getParent();
	}
}