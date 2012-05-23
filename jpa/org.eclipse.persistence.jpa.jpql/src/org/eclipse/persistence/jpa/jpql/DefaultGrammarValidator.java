/*******************************************************************************
 * Copyright (c) 2006, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation
 *
 ******************************************************************************/
package org.eclipse.persistence.jpa.jpql;

import org.eclipse.persistence.jpa.jpql.parser.JPQLGrammar;
import org.eclipse.persistence.jpa.jpql.spi.JPAVersion;

/**
 * This validator is responsible to validate a JPQL query grammatically purely based on the JPA
 * specification document.
 *
 * @see <a href="http://jcp.org/en/jsr/detail?id=220">JSR 220: Enterprise JavaBeans™ 3.0</a>
 * @see <a href="http://jcp.org/en/jsr/detail?id=317">JSR 317: Java™ Persistence 2.0</a>
 * @see <a href="http://jcp.org/en/jsr/detail?id=338">JSR 338: Java™ Persistence 2.1</a>
 * @see DefaultSemanticValidator
 * <p>
 * Provisional API: This interface is part of an interim API that is still under development and
 * expected to change significantly before reaching stability. It is available at this early stage
 * to solicit feedback from pioneering adopters on the understanding that any code that uses this
 * API will almost certainly be broken (repeatedly) as the API evolves.
 *
 * @version 2.4
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
	 * Creates a new <code>DefaultGrammarValidator</code>.
	 *
	 * @param queryContext The context used to query information about the JPQL query
	 * @deprecated This constructor only exists for backward compatibility. {@link JPQLQueryContext}
	 * is no longer required, only {@link JPQLGrammar}
	 * @see #DefaultGrammarValidator(JPQLGrammar)
	 */
	@Deprecated
	public DefaultGrammarValidator(JPQLQueryContext queryContext) {
		super(queryContext.getGrammar());
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