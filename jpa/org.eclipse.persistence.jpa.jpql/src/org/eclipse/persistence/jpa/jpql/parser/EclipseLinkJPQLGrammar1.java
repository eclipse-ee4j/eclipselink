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

import org.eclipse.persistence.jpa.jpql.spi.JPAVersion;

/**
 * This {@link JPQLGrammar JPQL grammar} provides support for parsing JPQL queries defined in
 * <a href="http://jcp.org/en/jsr/detail?id=220">JSR-220 - Enterprise JavaBeans 3.0</a>. EclipseLink
 * 1.x does not provide additional support.
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class EclipseLinkJPQLGrammar1 extends AbstractJPQLGrammar {

	/**
	 * The singleton instance of this {@link EclipseLinkJPQLGrammar1}.
	 */
	private static final JPQLGrammar INSTANCE = new EclipseLinkJPQLGrammar1();

	/**
	 * The EclipseLink version, which is 1.
	 */
	public static final String VERSION = "1";

	/**
	 * Creates a new <code>EclipseLinkJPQLExtension1</code>.
	 */
	public EclipseLinkJPQLGrammar1() {
		super();
	}

	/**
	 * Returns the singleton instance of this class.
	 *
	 * @return The {@link EclipseLinkJPQLGrammar1}
	 */
	public static JPQLGrammar instance() {
		return INSTANCE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected JPQLGrammar buildBaseGrammar() {
		return new JPQLGrammar1_0();
	}

	/**
	 * {@inheritDoc}
	 */
	public JPAVersion getJPAVersion() {
		return JPAVersion.VERSION_1_0;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getProviderVersion() {
		return VERSION;
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
		return "EclipseLink 1.x";
	}
}