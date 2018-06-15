/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
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
 * <p>A JPQL grammar defines how a JPQL query can be parsed. The {@link ExpressionRegistry} contains
 * the {@link JPQLQueryBNF BNFs} and the {@link ExpressionFactory expression factories} used to
 * create the tree representation of the JPQL query.</p>
 *
 * <p>The supported JPQL grammars are:</p>
 *
 * <ul>
 * <li>{@link JPQLGrammar1_0} defines the JPQL grammar based on JPA 1.0;</li>
 * <li>{@link JPQLGrammar2_0} defines the JPQL grammar based on JPA 2.0;</li>
 * <li>{@link JPQLGrammar2_1} defines the JPQL grammar based on JPA 2.1;</li>
 * <li>{@link EclipseLinkJPQLGrammar1} defines the JPQL grammar based on JPA 1.0 and EclipseLink 1.x;</li>
 * <li>{@link EclipseLinkJPQLGrammar2_0} defines the JPQL grammar based on JPA 2.0 and the additional EclipseLink 2.0 support;</li>
 * <li>{@link EclipseLinkJPQLGrammar2_1} defines the JPQL grammar based on JPA 2.0 and the additional EclipseLink 2.1 support.</li>
 * <li>{@link EclipseLinkJPQLGrammar2_2} defines the JPQL grammar based on JPA 2.0 and the additional EclipseLink 2.2 support.</li>
 * <li>{@link EclipseLinkJPQLGrammar2_3} defines the JPQL grammar based on JPA 2.0 and the additional EclipseLink 2.3 support.</li>
 * <li>{@link EclipseLinkJPQLGrammar2_4} defines the JPQL grammar based on JPA 2.1 and the additional EclipseLink 2.4 support.</li>
 * <li>{@link EclipseLinkJPQLGrammar2_5} defines the JPQL grammar based on JPA 2.1 and the additional EclipseLink 2.5 support.</li>
 * <li>{@link DefaultJPQLGrammar} defines the JPQL grammar based on the latest JPA version;</li>
 * <li>{@link DefaultEclipseLinkJPQLGrammar} defines the JPQL grammar based on the latest JPA and
 * the latest EclipseLink;</li>
 * </ul>
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
public interface JPQLGrammar {

    /**
     * Returns the registry containing the {@link JPQLQueryBNF JPQLQueryBNFs} and the {@link
     * org.eclipse.persistence.jpa.jpql.parser.ExpressionFactory ExpressionFactories} that are used
     * to properly parse a JPQL query.
     *
     * @return The registry containing the information related to the JPQL grammar
     */
    ExpressionRegistry getExpressionRegistry();

    /**
     * Returns the {@link JPAVersion} of the Java Persistence supported by this grammar.
     *
     * @return The {@link JPAVersion JPA version} supported by this grammar
     */
    JPAVersion getJPAVersion();

    /**
     * Returns the persistence provider name.
     *
     * @return The name of the persistence provider, <code>null</code> should never be returned
     * @since 2.5
     */
    String getProvider();

    /**
     * Returns the version of the persistence provider.
     *
     * @return The version of the persistence provider, if one is extending the default JPQL grammar
     * defined in the Java Persistence specification, otherwise returns an empty string
     * @since 2.4
     */
    String getProviderVersion();
}
