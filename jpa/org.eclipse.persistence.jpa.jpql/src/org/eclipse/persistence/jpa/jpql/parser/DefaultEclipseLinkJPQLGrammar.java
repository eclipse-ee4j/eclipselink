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
 * This {@link JPQLGrammar JPQL grammar} provides support for parsing JPQL queries defined in the
 * latest JPA functional specification and the latest EclipseLink.
 *
 * @see DefaultJPQLGrammar
 * @see EclipseLinkJPQLGrammar2_4
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public final class DefaultEclipseLinkJPQLGrammar implements JPQLGrammar {

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
		return EclipseLinkJPQLGrammar2_4.instance();
	}

	/**
	 * {@inheritDoc}
	 */
	public ExpressionRegistry getExpressionRegistry() {
		return EclipseLinkJPQLGrammar2_4.instance().getExpressionRegistry();
	}

	/**
	 * {@inheritDoc}
	 */
	public JPAVersion getJPAVersion() {
		return EclipseLinkJPQLGrammar2_4.instance().getJPAVersion();
	}
}