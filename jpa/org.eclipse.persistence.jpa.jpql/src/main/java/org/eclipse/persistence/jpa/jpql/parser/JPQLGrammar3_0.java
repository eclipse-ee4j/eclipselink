/*
 * Copyright (c) 2022 Oracle and/or its affiliates. All rights reserved.
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
//     04/21/2022: Tomas Kraus
//       - Issue 1474: Update JPQL Grammar for JPA 2.2, 3.0 and 3.1
package org.eclipse.persistence.jpa.jpql.parser;

import org.eclipse.persistence.jpa.jpql.ExpressionTools;
import org.eclipse.persistence.jpa.jpql.JPAVersion;

/**
 * This {@link JPQLGrammar} provides support for parsing JPQL queries defined in Java Persistence 3.0.
 */
public class JPQLGrammar3_0 extends AbstractJPQLGrammar {

    /**
     * The singleton instance of this {@link JPQLGrammar3_0}.
     */
    private static final JPQLGrammar INSTANCE = new JPQLGrammar3_0();

    /**
     * Creates an insance of Java Persistence 3.0 JPQL grammar.
     */
    public JPQLGrammar3_0() {
        super();
    }

    /**
     * Creates an insance of Java Persistence 3.0 JPQL grammar.
     *
     * @param jpqlGrammar The {@link JPQLGrammar} to extend with the content of this one without
     * instantiating the base {@link JPQLGrammar}
     */
    private JPQLGrammar3_0(AbstractJPQLGrammar jpqlGrammar) {
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
        new JPQLGrammar3_0(jpqlGrammar);
    }

    /**
     * Returns the singleton instance of the default implementation of {@link JPQLGrammar} which
     * provides support for the JPQL grammar defined in the JPA 3.0 functional specification.
     *
     * @return The {@link JPQLGrammar} that only has support for JPA 3.0
     */
    public static JPQLGrammar instance() {
        return INSTANCE;
    }

    @Override
    protected JPQLGrammar buildBaseGrammar() {
        return new JPQLGrammar2_2();
    }

    @Override
    public JPAVersion getJPAVersion() {
        return JPAVersion.VERSION_3_0;
    }

    @Override
    public String getProvider() {
        return DefaultJPQLGrammar.PROVIDER_NAME;
    }

    @Override
    public String getProviderVersion() {
        return ExpressionTools.EMPTY_STRING;
    }

    @Override
    protected void initializeBNFs() {
    }

    @Override
    protected void initializeExpressionFactories() {
    }

    @Override
    protected void initializeIdentifiers() {
    }

    @Override
    public String toString() {
        return "JPQLGrammar 3.0";
    }

}
