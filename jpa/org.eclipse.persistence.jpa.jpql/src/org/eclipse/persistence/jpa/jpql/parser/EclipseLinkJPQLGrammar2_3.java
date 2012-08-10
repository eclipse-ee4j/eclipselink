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

import org.eclipse.persistence.jpa.jpql.EclipseLinkVersion;
import org.eclipse.persistence.jpa.jpql.spi.JPAVersion;

/**
 * This {@link JPQLGrammar} provides support for parsing JPQL queries defined in <a
 * href="http://jcp.org/en/jsr/detail?id=317">JSR-337 - Java Persistence 2.0</a>. EclipseLink 2.3
 * does not add any additional support over EclipseLink 2.2.
 * <p>
 * Provisional API: This interface is part of an interim API that is still under development and
 * expected to change significantly before reaching stability. It is available at this early stage
 * to solicit feedback from pioneering adopters on the understanding that any code that uses this
 * API will almost certainly be broken (repeatedly) as the API evolves.
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class EclipseLinkJPQLGrammar2_3 extends AbstractJPQLGrammar {

	/**
	 * The singleton instance of this {@link EclipseLinkJPQLGrammar2_3}.
	 */
	private static final JPQLGrammar INSTANCE = new EclipseLinkJPQLGrammar2_3();

	/**
	 * The EclipseLink version, which is 2.3.
	 */
	public static final EclipseLinkVersion VERSION = EclipseLinkVersion.VERSION_2_3;

	/**
	 * Creates a new <code>EclipseLinkJPQLGrammar2_3</code>.
	 */
	public EclipseLinkJPQLGrammar2_3() {
		super();
	}

	/**
	 * Creates a new <code>EclipseLinkJPQLGrammar2_3</code>.
	 *
	 * @param jpqlGrammar The {@link JPQLGrammar} to extend with the content of this one without
	 * instantiating the base {@link JPQLGrammar}
	 */
	private EclipseLinkJPQLGrammar2_3(AbstractJPQLGrammar jpqlGrammar) {
		super(jpqlGrammar);
	}

	/**
	 * Extends the given {@link JPQLGrammar} with the information of this one without instantiating
	 * the base {@link JPQLGrammar}.
	 *
	 * @param jpqlGrammar The {@link JPQLGrammar} to extend with the content of this one without
	 * instantiating the base {@link JPQLGrammar}
	 */
	public static void extend(AbstractJPQLGrammar jpqlGrammar) {
		new EclipseLinkJPQLGrammar2_3(jpqlGrammar);
	}

	/**
	 * Returns the singleton instance of this class.
	 *
	 * @return The {@link EclipseLinkJPQLGrammar2_3}
	 */
	public static JPQLGrammar instance() {
		return INSTANCE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected JPQLGrammar buildBaseGrammar() {
		return new EclipseLinkJPQLGrammar2_2();
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
	public String getProvider() {
		return DefaultEclipseLinkJPQLGrammar.PROVIDER_NAME;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getProviderVersion() {
		return VERSION.getVersion();
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "EclipseLink 2.3";
	}
}