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
package org.eclipse.persistence.jpa.jpql.spi.java;

import org.eclipse.persistence.jpa.jpql.ExpressionTools;
import org.eclipse.persistence.jpa.jpql.spi.IManagedTypeProvider;
import org.eclipse.persistence.jpa.jpql.spi.IQuery;

/**
 * The concrete implementation of {@link IQuery} that is wrapping the design-time representation
 * of a JPQL query.
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public class JavaQuery implements IQuery {

	/**
	 * The string representation of the JPQL query.
	 */
	private String jpqlQuery;

	/**
	 * The provider of JPA managed types.
	 */
	private IManagedTypeProvider provider;

	/**
	 * Creates a new <code>JavaQuery</code>.
	 *
	 * @param provider The provider of JPA managed types
	 * @param jpqlQuery The string representation of the JPQL query
	 */
	public JavaQuery(IManagedTypeProvider provider, CharSequence jpqlQuery) {
		super();
		this.provider = provider;
		setExpression(jpqlQuery);
	}

	/**
	 * {@inheritDoc}
	 */
	public String getExpression() {
		return jpqlQuery;
	}

	/**
	 * {@inheritDoc}
	 */
	public IManagedTypeProvider getProvider() {
		return provider;
	}

	/**
	 * Sets the string representation of the JPQL query.
	 *
	 * @param jpqlQuery The string representation of the JPQL query
	 */
	public void setExpression(CharSequence jpqlQuery) {
		this.jpqlQuery = (jpqlQuery != null) ? jpqlQuery.toString() : ExpressionTools.EMPTY_STRING;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("JPQL query=[");
		sb.append(jpqlQuery);
		sb.append("]");
		return sb.toString();
	}
}