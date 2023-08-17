/*
 * Copyright (c) 2023 Oracle and/or its affiliates. All rights reserved.
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
//     06/02/2023: Radek Felcman
//       - Issue 1885: Implement new JPQLGrammar for upcoming Jakarta Persistence 3.2
package org.eclipse.persistence.jpa.jpql.parser;

import org.eclipse.persistence.jpa.jpql.EclipseLinkVersion;
import org.eclipse.persistence.jpa.jpql.JPAVersion;

/**
 * <p>This {@link JPQLGrammar} provides support for parsing JPQL queries defined
 * in Jakarta Persistence 3.2 and the additional support provided by EclipseLink 4.1.</p>
 */
public class EclipseLinkJPQLGrammar4_1 extends AbstractJPQLGrammar {

    /**
     * The singleton instance of this {@link EclipseLinkJPQLGrammar4_1}.
     */
    private static final JPQLGrammar INSTANCE = new EclipseLinkJPQLGrammar4_1();

    /**
     * The EclipseLink version, which is 4.1.
     */
    public static final EclipseLinkVersion VERSION = EclipseLinkVersion.VERSION_4_1;

    /**
     * Creates a new <code>EclipseLinkJPQLGrammar4_0</code>.
     */
    public EclipseLinkJPQLGrammar4_1() {
        super();
    }

    /**
     * Creates a new <code>EclipseLinkJPQLGrammar4_0</code>.
     *
     * @param jpqlGrammar The {@link JPQLGrammar} to extend with the content of this one without
     * instantiating the base {@link JPQLGrammar}
     */
    public EclipseLinkJPQLGrammar4_1(AbstractJPQLGrammar jpqlGrammar) {
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
        new EclipseLinkJPQLGrammar4_1(jpqlGrammar);
    }

    /**
     * Returns the singleton instance of this class.
     *
     * @return The singleton instance of {@link EclipseLinkJPQLGrammar4_1}
     */
    public static JPQLGrammar instance() {
        return INSTANCE;
    }

    @Override
    protected JPQLGrammar buildBaseGrammar() {
        // First build the JPQL 3.2 grammar
        JPQLGrammar3_2 jpqlGrammar = new JPQLGrammar3_2();
        // Extend it by adding the EclipseLink 2.0 - 4.0 additional support
        EclipseLinkJPQLGrammar2_0.extend(jpqlGrammar);
        EclipseLinkJPQLGrammar2_1.extend(jpqlGrammar);
        EclipseLinkJPQLGrammar2_4.extend(jpqlGrammar);
        EclipseLinkJPQLGrammar2_5.extend(jpqlGrammar);
        EclipseLinkJPQLGrammar4_0.extend(jpqlGrammar);
        return jpqlGrammar;
    }

    @Override
    public JPAVersion getJPAVersion() {
        return JPAVersion.VERSION_3_2;
    }

    @Override
    public String getProvider() {
        return DefaultEclipseLinkJPQLGrammar.PROVIDER_NAME;
    }

    @Override
    public String getProviderVersion() {
        return VERSION.getVersion();
    }

    @Override
    protected void initializeBNFs() {
        // Extend to support scalarExpression
        addChildBNF(InternalReplacePositionExpressionBNF.ID, ScalarExpressionBNF.ID);
        addChildBNF(InternalReplaceStringExpressionBNF.ID,   ScalarExpressionBNF.ID);
    }

    @Override
    protected void initializeExpressionFactories() {
    }

    @Override
    protected void initializeIdentifiers() {
    }

    @Override
    public String toString() {
        return "EclipseLink 4.1";
    }

}
