/*******************************************************************************
 * Copyright (c) 2006, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.jpa.jpql.parser;

import org.eclipse.persistence.jpa.jpql.ExpressionTools;
import org.eclipse.persistence.jpa.jpql.spi.JPAVersion;

/**
 * This {@link JPQLGrammar JPQL grammar} provides support for parsing JPQL queries defined in the
 * latest JPA functional specification. The current version of the functional specification is
 * <a href="http://jcp.org/en/jsr/detail?id=317">JSR-337 Java Persistence 2.0</a>.
 *
 * Provisional API: This interface is part of an interim API that is still under development and
 * expected to change significantly before reaching stability. It is available at this early stage
 * to solicit feedback from pioneering adopters on the understanding that any code that uses this
 * API will almost certainly be broken (repeatedly) as the API evolves.
 *
 * @see JPQLGrammar2_0
 * @see DefaultJPQLGrammar
 * @see DefaultEclipseLinkJPQLGrammar
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class DefaultJPQLGrammar implements JPQLGrammar {

	/**
	 * Creates a new <code>DefaultJPQLGrammar</code>.
	 */
	private DefaultJPQLGrammar() {
		super();
	}

	/**
	 * Returns the singleton instance of this class.
	 *
	 * @return The singleton instance of this {@link DefaultJPQLGrammar}
	 */
	public static JPQLGrammar instance() {
		return JPQLGrammar2_0.instance();
	}

	/**
	 * {@inheritDoc}
	 */
	public ExpressionRegistry getExpressionRegistry() {
		return instance().getExpressionRegistry();
	}

	/**
	 * {@inheritDoc}
	 */
	public JPAVersion getJPAVersion() {
		return instance().getJPAVersion();
	}

	/**
	 * {@inheritDoc}
	 */
	public String getProviderVersion() {
		return ExpressionTools.EMPTY_STRING;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "JPQLGrammar 2.0 (default)";
	}
}