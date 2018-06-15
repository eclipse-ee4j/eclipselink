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

import org.eclipse.persistence.jpa.jpql.EclipseLinkVersion;
import org.eclipse.persistence.jpa.jpql.JPAVersion;

/**
 * <p>This {@link JPQLGrammar JPQL grammar} provides support for parsing JPQL queries defined in
 * <a href="http://jcp.org/en/jsr/detail?id=220">JSR-220 - Enterprise JavaBeans 3.0</a>. EclipseLink
 * 1.x does not provide additional support.</p>
 *
 * <p>Provisional API: This interface is part of an interim API that is still under development and
 * expected to change significantly before reaching stability. It is available at this early stage
 * to solicit feedback from pioneering adopters on the understanding that any code that uses this
 * API will almost certainly be broken (repeatedly) as the API evolves.</p>
 *
 * @version 2.5
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
     * The EclipseLink version, which is 1.x.
     */
    public static final EclipseLinkVersion VERSION = EclipseLinkVersion.VERSION_1_x;

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
    @Override
    public JPAVersion getJPAVersion() {
        return JPAVersion.VERSION_1_0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getProvider() {
        return DefaultEclipseLinkJPQLGrammar.PROVIDER_NAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
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
        return "EclipseLink 1.x";
    }
}
