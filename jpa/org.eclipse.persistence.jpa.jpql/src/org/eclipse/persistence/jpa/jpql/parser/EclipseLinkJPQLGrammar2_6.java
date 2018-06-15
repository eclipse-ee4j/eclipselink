/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
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
 * <p>This {@link JPQLGrammar} provides support for parsing JPQL queries defined in <a
 * href="http://jcp.org/en/jsr/detail?id=317">JSR-338 - Java Persistence 2.1</a> and the additional
 * support provided by EclipseLink 2.6.</p>
 *
 * The BNFs of the additional support are the following:
 * <pre><code> ...</code></pre>
 *
 * <p>Provisional API: This interface is part of an interim API that is still under development and
 * expected to change significantly before reaching stability. It is available at this early stage
 * to solicit feedback from pioneering adopters on the understanding that any code that uses this
 * API will almost certainly be broken (repeatedly) as the API evolves.</p>
 *
 * @version 2.6
 * @since 2.6
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class EclipseLinkJPQLGrammar2_6 extends AbstractJPQLGrammar {

    /**
     * The singleton instance of this {@link EclipseLinkJPQLGrammar2_6}.
     */
    private static final JPQLGrammar INSTANCE = new EclipseLinkJPQLGrammar2_6();

    /**
     * The EclipseLink version, which is 2.6.
     */
    public static final EclipseLinkVersion VERSION = EclipseLinkVersion.VERSION_2_6;

    /**
     * Creates a new <code>EclipseLinkJPQLGrammar2_6</code>.
     */
    public EclipseLinkJPQLGrammar2_6() {
        super();
    }

    /**
     * Creates a new <code>EclipseLinkJPQLGrammar2_6</code>.
     *
     * @param jpqlGrammar The {@link JPQLGrammar} to extend with the content of this one without
     * instantiating the base {@link JPQLGrammar}
     */
    public EclipseLinkJPQLGrammar2_6(AbstractJPQLGrammar jpqlGrammar) {
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
        new EclipseLinkJPQLGrammar2_6(jpqlGrammar);
    }

    /**
     * Returns the singleton instance of this class.
     *
     * @return The singleton instance of {@link EclipseLinkJPQLGrammar2_6}
     */
    public static JPQLGrammar instance() {
        return INSTANCE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected JPQLGrammar buildBaseGrammar() {
        return new EclipseLinkJPQLGrammar2_5();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JPAVersion getJPAVersion() {
        return JPAVersion.VERSION_2_1;
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
        return "EclipseLink 2.6";
    }
}
