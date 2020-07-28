/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
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
//
package org.eclipse.persistence.jpa.jpql.tools;

import org.eclipse.persistence.jpa.jpql.AbstractEclipseLinkSemanticValidator;
import org.eclipse.persistence.jpa.jpql.EclipseLinkGrammarValidator;
import org.eclipse.persistence.jpa.jpql.EclipseLinkSemanticValidatorExtension;
import org.eclipse.persistence.jpa.jpql.parser.JPQLGrammar;
import org.eclipse.persistence.jpa.jpql.tools.model.EclipseLinkJPQLQueryBuilder;
import org.eclipse.persistence.jpa.jpql.tools.model.IJPQLQueryBuilder;
import org.eclipse.persistence.jpa.jpql.tools.spi.IQuery;

/**
 * This helper can perform the following operations over a JPQL query:
 * <ul>
 * <li>Calculates the result type of a query: {@link #getResultType()};</li>
 * <li>Calculates the type of an input parameter: {@link #getParameterType(String)}.</li>
 * <li>Calculates the possible choices to complete the query from a given
 *     position (used for content assist): {@link #buildContentAssistProposals(int)}.</li>
 * <li>Validates the query by introspecting it grammatically and semantically:
 *     <ul>
 *     <li>{@link #validate()},</li>
 *     <li>{@link #validateGrammar()},</li>
 *     <li>{@link #validateSemantic()}.</li>
 *     </ul>
 * </li>
 * <li>Refactoring support:
 *     <ul>
 *     <li>{@link #buildBasicRefactoringTool()} provides support for generating the delta of the
 *     refactoring operation through a collection of {@link TextEdit} objects.</li>
 *     <li>{@link #buildRefactoringTool()} provides support for refactoring the JPQL query through
 *     the editable {@link org.eclipse.persistence.jpa.jpql.tools.model.query.StateObject StateObject} and
 *     once all refactoring operations have been executed, the {@link
 *     org.eclipse.persistence.jpa.jpql.tools.model.IJPQLQueryFormatter IJPQLQueryFormatter} will generate
 *     a new string representation of the JPQL query.</li>
 *     </ul>
 * </li>
 * </ul>
 *
 * This helper should be used when the JPQL query is written using the JPQL grammar defined in the
 * Java Persistence functional specification 2.1 and it contains the additional support provided by
 * EclipseLink.
 * <p>
 * Provisional API: This interface is part of an interim API that is still under development and
 * expected to change significantly before reaching stability. It is available at this early stage
 * to solicit feedback from pioneering adopters on the understanding that any code that uses this
 * API will almost certainly be broken (repeatedly) as the API evolves.
 *
 * @version 2.5
 * @since 2.4
 * @author Pascal Filion
 */
public class EclipseLinkJPQLQueryHelper extends AbstractJPQLQueryHelper {

    /**
     * Creates a new <code>EclipseLinkJPQLQueryHelper</code>.
     *
     * @param jpqlGrammar The {@link JPQLGrammar} that will determine how to parse JPQL queries
     */
    public EclipseLinkJPQLQueryHelper(JPQLGrammar jpqlGrammar) {
        super(jpqlGrammar);
    }

    /**
     * Creates a new <code>EclipseLinkJPQLQueryHelper</code>.
     *
     * @param queryContext The context used to query information about the JPQL query
     */
    public EclipseLinkJPQLQueryHelper(JPQLQueryContext queryContext) {
        super(queryContext);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BasicRefactoringTool buildBasicRefactoringTool() {
        return new EclipseLinkBasicRefactoringTool(
            getQuery().getExpression(),
            getGrammar(),
            getProvider()
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected AbstractContentAssistVisitor buildContentAssistVisitor(JPQLQueryContext queryContext) {
        return new EclipseLinkContentAssistVisitor(queryContext);
    }

    /**
     * Creates a new {@link EclipseLinkSemanticValidatorExtension}, which will provide additional
     * support for non-JPA related information.
     *
     * @return Either a new concrete instance of {@link EclipseLinkSemanticValidatorExtension} or
     * {@link EclipseLinkSemanticValidatorExtension#NULL_EXTENSION} if none is required
     */
    protected EclipseLinkSemanticValidatorExtension buildEclipseLinkSemanticValidatorExtension() {
        return EclipseLinkSemanticValidatorExtension.NULL_EXTENSION;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected EclipseLinkGrammarValidator buildGrammarValidator(JPQLGrammar jpqlGrammar) {
        return new EclipseLinkGrammarValidator(jpqlGrammar);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected JPQLQueryContext buildJPQLQueryContext(JPQLGrammar jpqlGrammar) {
        return new EclipseLinkJPQLQueryContext(jpqlGrammar);
    }

    /**
     * Creates the right {@link IJPQLQueryBuilder} based on the JPQL grammar.
     *
     * @return A new concrete instance of {@link IJPQLQueryBuilder}
     */
    protected IJPQLQueryBuilder buildQueryBuilder() {
        return new EclipseLinkJPQLQueryBuilder(getGrammar());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RefactoringTool buildRefactoringTool() {

        IQuery query = getQuery();

        return new DefaultRefactoringTool(
            query.getProvider(),
            buildQueryBuilder(),
            query.getExpression()
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected AbstractEclipseLinkSemanticValidator buildSemanticValidator(JPQLQueryContext queryContext) {
        return new EclipseLinkSemanticValidator(
            queryContext,
            buildEclipseLinkSemanticValidatorExtension()
        );
    }
}
