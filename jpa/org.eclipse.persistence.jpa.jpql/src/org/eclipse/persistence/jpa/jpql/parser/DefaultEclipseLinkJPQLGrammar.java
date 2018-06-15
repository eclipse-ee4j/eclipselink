/*
 * Copyright (c) 2006, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation
//
package org.eclipse.persistence.jpa.jpql.parser;

import org.eclipse.persistence.jpa.jpql.JPAVersion;

/**
 * This {@link JPQLGrammar JPQL grammar} provides support for parsing JPQL queries defined by the
 * JPA 2.1 functional specification and the EclipseLink 2.6.
 * <p>
 * Provisional API: This interface is part of an interim API that is still under development and
 * expected to change significantly before reaching stability. It is available at this early stage
 * to solicit feedback from pioneering adopters on the understanding that any code that uses this
 * API will almost certainly be broken (repeatedly) as the API evolves.
 *
 * @see JPQLGrammar2_1
 * @see EclipseLinkJPQLGrammar2_6
 *
 * @version 2.6
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
        return EclipseLinkJPQLGrammar2_6.instance();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ExpressionRegistry getExpressionRegistry() {
        return instance().getExpressionRegistry();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JPAVersion getJPAVersion() {
        return instance().getJPAVersion();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getProvider() {
        return instance().getProvider();
    }

    /**
     * {@inheritDoc}
     */
    @Override
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
