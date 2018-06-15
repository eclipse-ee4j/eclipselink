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
package org.eclipse.persistence.jpa.jpql.tools;

import org.eclipse.persistence.jpa.jpql.tools.model.DefaultActualJPQLQueryFormatter;
import org.eclipse.persistence.jpa.jpql.tools.model.IJPQLQueryBuilder;
import org.eclipse.persistence.jpa.jpql.tools.model.IJPQLQueryFormatter;
import org.eclipse.persistence.jpa.jpql.tools.spi.IManagedTypeProvider;

/**
 * This utility class provides basic refactoring support.
 * <p>
 * Provided functionality:
 * <ul>
 * <li>Renaming an identification variable;</li>
 * <li>Renaming a state field or collection-valued field;</li>
 * <li>Renaming an entity name;</li>
 * <li>Renaming a {@link Class} name (e.g.: in constructor expression);</li>
 * <li>Renaming an {@link Enum} constant.</li>
 * </ul>
 * <p>
 * Provisional API: This interface is part of an interim API that is still under development and
 * expected to change significantly before reaching stability. It is available at this early stage
 * to solicit feedback from pioneering adopters on the understanding that any code that uses this
 * API will almost certainly be broken (repeatedly) as the API evolves.
 *
 * @see EclipseLinkRefactoringTool
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public class DefaultRefactoringTool extends RefactoringTool {

    /**
     * Creates a new <code>RefactoringTool</code>.
     *
     * @param managedTypeProvider The external form of a provider that gives access to the JPA metadata
     * @param jpqlQueryBuilder The builder that creates the {@link
     * org.eclipse.persistence.jpa.jpql.tools.model.query.StateObject StateObject} representation of the JPQL query
     * @param jpqlQuery The JPQL query to manipulate
     */
    public DefaultRefactoringTool(IManagedTypeProvider managedTypeProvider,
                                  IJPQLQueryBuilder jpqlQueryBuilder,
                                  CharSequence jpqlQuery) {

        super(managedTypeProvider, jpqlQueryBuilder, jpqlQuery);
    }

    /**
     * Creates a new <code>RefactoringTool</code>.
     *
     * @param managedTypeProvider The external form of a provider that gives access to the JPA metadata
     * @param jpqlQueryBuilder The builder that creates the {@link
     * org.eclipse.persistence.jpa.jpql.tools.model.query.StateObject} representation of the JPQL query
     * @param jpqlFragment The JPQL query to manipulate or a single JPQL fragment, which is parsed
     * using the JPQL query BNF identifier by the given ID
     * @param jpqlQueryBNFId The unique identifier of the {@link
     * org.eclipse.persistence.jpa.jpql.parser.JPQLQueryBNF JPQLQueryBNF} that determines how to parse the JPQL fragment
     */
    public DefaultRefactoringTool(IManagedTypeProvider managedTypeProvider,
                                  IJPQLQueryBuilder jpqlQueryBuilder,
                                  CharSequence jpqlFragment,
                                  String jpqlQueryBNFId) {

        super(managedTypeProvider, jpqlQueryBuilder, jpqlFragment, jpqlQueryBNFId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected IJPQLQueryFormatter buildFormatter() {
        return new DefaultActualJPQLQueryFormatter(true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected JPQLQueryContext buildJPQLQueryContext() {
        return new DefaultJPQLQueryContext(getGrammar());
    }
}
