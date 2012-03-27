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
import org.eclipse.persistence.jpa.jpql.model.query.SelectClauseStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.SimpleSelectClauseStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.UpdateItemStateObject;
import org.eclipse.persistence.jpa.jpql.parser.JPQLGrammar;
import org.eclipse.persistence.jpa.jpql.parser.JPQLGrammar2_1;

/**
 * An implementation of {@link IJPQLQueryBuilder} that provides support based on the Java Persistence
 * functional specification defined in <a href="http://jcp.org/en/jsr/detail?id=338">JSR-338 - Java
 * Persistence 2.1</a>.
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class JPQLQueryBuilder2_1 extends AbstractJPQLQueryBuilder {

	/**
	 * Creates a new <code>JPQLQueryBuilder2_1</code>.
	 */
	public JPQLQueryBuilder2_1() {
		super();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected DefaultStateObjectBuilder buildStateObjectBuilder() {
		return new DefaultStateObjectBuilder();
	}

	/**
	 * {@inheritDoc}
	 */
	public DefaultConditionalExpressionStateObjectBuilder buildStateObjectBuilder(AbstractConditionalClauseStateObject stateObject) {
		return new DefaultConditionalExpressionStateObjectBuilder(stateObject);
	}

	/**
	 * {@inheritDoc}
	 */
	public DefaultSelectExpressionStateObjectBuilder buildStateObjectBuilder(SelectClauseStateObject stateObject) {
		return new DefaultSelectExpressionStateObjectBuilder(stateObject);
	}

	/**
	 * {@inheritDoc}
	 */
	public DefaultSimpleSelectExpressionStateObjectBuilder buildStateObjectBuilder(SimpleSelectClauseStateObject stateObject) {
		return new DefaultSimpleSelectExpressionStateObjectBuilder(stateObject);
	}

	/**
	 * {@inheritDoc}
	 */
	public INewValueStateObjectBuilder buildStateObjectBuilder(UpdateItemStateObject stateObject) {
		return new DefaultNewValueStateObjectBuilder(stateObject);
	}

	/**
	 * {@inheritDoc}
	 */
	public JPQLGrammar getGrammar() {
		return JPQLGrammar2_1.instance();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "JPQLQueryBuilder2_1 using " + getGrammar().toString();
	}
}