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
 * This {@link JPQLGrammar JPQL grammar} provides support for parsing JPQL queries defined by the
 * JPA 2.1 functional specification and the EclipseLink 2.5.
 * <p>
 * Provisional API: This interface is part of an interim API that is still under development and
 * expected to change significantly before reaching stability. It is available at this early stage
 * to solicit feedback from pioneering adopters on the understanding that any code that uses this
 * API will almost certainly be broken (repeatedly) as the API evolves.
 *
 * @see JPQLGrammar2_1
 * @see EclipseLinkJPQLGrammar2_5
 *
 * @version 2.5
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class DefaultEclipseLinkJPQLGrammar implements JPQLGrammar {

	/**
	 * The persistence provider name: EclipseLink.
	 */
	public static final String PROVIDER_NAME = "EclipseLink";

	/**
	 * Creates a new <code>DefaultEclipseLinkJPQLGrammar</code>.
	 */
	private DefaultEclipseLinkJPQLGrammar() {
		super();
	}

	/**
	 * Returns the singleton instance of this class.
	 *
	 * @return The latest {@link JPQLGrammar} that supports EclipseLink
	 */
	public static JPQLGrammar instance() {
		return EclipseLinkJPQLGrammar2_5.instance();
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
	public String getProvider() {
		return instance().getProvider();
	}

	/**
	 * {@inheritDoc}
	 */
	public String getProviderVersion() {
		return instance().getProviderVersion();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return instance().toString();
	}
}