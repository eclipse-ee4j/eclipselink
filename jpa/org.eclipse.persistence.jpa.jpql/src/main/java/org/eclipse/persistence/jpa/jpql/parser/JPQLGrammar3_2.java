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

import org.eclipse.persistence.jpa.jpql.ExpressionTools;
import org.eclipse.persistence.jpa.jpql.JPAVersion;

import static org.eclipse.persistence.jpa.jpql.parser.Expression.CONCAT_PIPES;
import static org.eclipse.persistence.jpa.jpql.parser.Expression.REPLACE;

/**
 * This {@link JPQLGrammar} provides support for parsing JPQL queries defined in Jakarta Persistence 3.2.
 * <pre><code>
 *
 * string_expression ::= string_expression || string_term
 *
 * functions_returning_strings ::= REPLACE(string_primary, string_primary, string_primary)
 *
 * </code></pre>
 */
public class JPQLGrammar3_2 extends AbstractJPQLGrammar {

    /**
     * The singleton instance of this {@link JPQLGrammar3_2}.
     */
    private static final JPQLGrammar INSTANCE = new JPQLGrammar3_2();

    /**
     * Creates an insance of Jakarta Persistence 3.1 JPQL grammar.
     */
    public JPQLGrammar3_2() {
        super();
    }

    /**
     * Creates an instance of Jakarta Persistence 3.2 JPQL grammar.
     *
     * @param jpqlGrammar The {@link JPQLGrammar} to extend with the content of this one without
     * instantiating the base {@link JPQLGrammar}
     */
    private JPQLGrammar3_2(AbstractJPQLGrammar jpqlGrammar) {
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
        new JPQLGrammar3_2(jpqlGrammar);
    }

    /**
     * Returns the singleton instance of the default implementation of {@link JPQLGrammar} which
     * provides support for the JPQL grammar defined in the Jakarta Persistence 3.2 functional specification.
     *
     * @return The {@link JPQLGrammar} that only has support for Jakarta Persistence 3.2
     */
    public static JPQLGrammar instance() {
        return INSTANCE;
    }

    @Override
    protected JPQLGrammar buildBaseGrammar() {
        return new JPQLGrammar3_1();
    }

    @Override
    public JPAVersion getJPAVersion() {
        return JPAVersion.VERSION_3_2;
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
        registerBNF(new StringFactorBNF());
        registerBNF(new StringTermBNF());
        registerBNF(new SimpleStringExpressionBNF());
        registerBNF(new InternalReplacePositionExpressionBNF());
        registerBNF(new InternalReplaceStringExpressionBNF());

        // Extend some query BNFs
        addChildBNF(StringPrimaryBNF.ID,   SimpleStringExpressionBNF.ID);
        addChildFactory(FunctionsReturningStringsBNF.ID, ReplaceExpressionFactory.ID);
    }

    @Override
    protected void initializeExpressionFactories() {
        registerFactory(new StringExpressionFactory());
        registerFactory(new ReplaceExpressionFactory());
    }

    @Override
    protected void initializeIdentifiers() {
        registerIdentifierRole(CONCAT_PIPES,                  IdentifierRole.AGGREGATE);          // x || y
        registerIdentifierVersion(CONCAT_PIPES, JPAVersion.VERSION_3_2);
        registerIdentifierRole(REPLACE,             IdentifierRole.FUNCTION);           // REPLACE(x, y, z)
        registerIdentifierVersion(REPLACE, JPAVersion.VERSION_3_2);
    }

    @Override
    public String toString() {
        return "JPQLGrammar 3.2";
    }
}
