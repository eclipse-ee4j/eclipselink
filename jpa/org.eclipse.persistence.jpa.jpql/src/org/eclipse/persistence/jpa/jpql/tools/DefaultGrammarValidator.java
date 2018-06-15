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
package org.eclipse.persistence.jpa.jpql.tools;

import org.eclipse.persistence.jpa.jpql.AbstractGrammarValidator;
import org.eclipse.persistence.jpa.jpql.JPAVersion;
import org.eclipse.persistence.jpa.jpql.LiteralVisitor;
import org.eclipse.persistence.jpa.jpql.parser.JPQLGrammar;

/**
 * This validator is responsible to validate a JPQL query grammatically purely based on the JPA
 * specification document.
 *
 * @see <a href="http://jcp.org/en/jsr/detail?id=220">JSR 220: Enterprise JavaBeans 3.0</a>
 * @see <a href="http://jcp.org/en/jsr/detail?id=317">JSR 317: Java Persistence 2.0</a>
 * @see <a href="http://jcp.org/en/jsr/detail?id=338">JSR 338: Java Persistence 2.1</a>
 * @see DefaultSemanticValidator
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
public class DefaultGrammarValidator extends AbstractGrammarValidator {

    /**
     * Creates a new <code>DefaultGrammarValidator</code>.
     *
     * @param jpqlGrammar The {@link JPQLGrammar} that defines how the JPQL query was parsed
     */
    public DefaultGrammarValidator(JPQLGrammar jpqlGrammar) {
        super(jpqlGrammar);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected LiteralVisitor buildLiteralVisitor() {
        return new DefaultLiteralVisitor();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected OwningClauseVisitor buildOwningClauseVisitor() {
        return new OwningClauseVisitor();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean isJoinFetchIdentifiable() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean isSubqueryAllowedAnywhere() {
        return getJPAVersion().isNewerThanOrEqual(JPAVersion.VERSION_2_1);
    }
}
