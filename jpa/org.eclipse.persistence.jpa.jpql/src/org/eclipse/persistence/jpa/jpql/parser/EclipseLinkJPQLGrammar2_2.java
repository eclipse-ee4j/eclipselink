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
package org.eclipse.persistence.jpa.jpql.parser;

import org.eclipse.persistence.jpa.jpql.spi.JPAVersion;

/**
 * This {@link JPQLGrammar} provides support for parsing JPQL queries defined in <a href="http://jcp.org/en/jsr/detail?id=317">JSR-337
 * - Java Persistence 2.0</a>. EclipseLink 2.3 did not add extended the grammar.
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public final class EclipseLinkJPQLGrammar2_2 extends AbstractJPQLGrammar {

	/**
	 * The singleton instance of this {@link EclipseLinkJPQLGrammar2_2}.
	 */
	private static final JPQLGrammar INSTANCE = new EclipseLinkJPQLGrammar2_2();

	/**
	 * Creates a new <code>EclipseLinkJPQLGrammar2_2</code>.
	 */
	public EclipseLinkJPQLGrammar2_2() {
		super();
	}

	/**
	 * Returns the singleton instance of this class.
	 *
	 * @return The {@link EclipseLinkJPQLGrammar2_2}
	 */
	public static JPQLGrammar instance() {
		return INSTANCE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected JPQLGrammar buildBaseGrammar() {
		return new EclipseLinkJPQLGrammar2_1();
	}

	/**
	 * {@inheritDoc}
	 */
	public JPAVersion getJPAVersion() {
		return JPAVersion.VERSION_2_0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initializeBNFs() {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initializeExpressionFactories() {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initializeIdentifiers() {
	}
}