/*
 * Copyright (c) 2006, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation
//     04/21/2022: Tomas Kraus
//       - Issue 1474: Update JPQL Grammar for JPA 2.2, 3.0 and 3.1
package org.eclipse.persistence.jpa.jpql.parser;

import org.eclipse.persistence.jpa.jpql.JPAVersion;

/**
 * This {@link JPQLGrammar JPQL grammar} provides support for parsing JPQL queries defined by the
 * JPA 3.1 functional specification and the EclipseLink 4.0.
 *
 * @see JPQLGrammar3_1
 * @see EclipseLinkJPQLGrammar4_0
 *
 * @version 4.0
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
        return EclipseLinkJPQLGrammar4_0.instance();
    }

    @Override
    public ExpressionRegistry getExpressionRegistry() {
        return instance().getExpressionRegistry();
    }

    @Override
    public JPAVersion getJPAVersion() {
        return instance().getJPAVersion();
    }

    @Override
    public String getProvider() {
        return instance().getProvider();
    }

    @Override
    public String getProviderVersion() {
        return instance().getProviderVersion();
    }

    @Override
    public String toString() {
        return instance().toString();
    }
}
