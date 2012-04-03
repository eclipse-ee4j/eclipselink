/*******************************************************************************
 * Copyright (c) 2006, 2012 Oracle. All rights reserved.
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

import org.eclipse.persistence.jpa.jpql.model.IJPQLQueryBuilder;
import org.eclipse.persistence.jpa.jpql.model.JPQLQueryBuilder1_0;
import org.eclipse.persistence.jpa.jpql.model.JPQLQueryBuilder2_0;
import org.eclipse.persistence.jpa.jpql.model.JPQLQueryBuilder2_1;
import org.eclipse.persistence.jpa.jpql.parser.JPQLGrammar;
import org.eclipse.persistence.jpa.jpql.spi.IQuery;

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
 *     </ul></li>
 * </ul>
 *
 * This helper should be used when the JPQL query is written using the JPQL grammar defined in the
 * Java Persistence functional specification 1.0 or 2.x.<p>
 *
 * Provisional API: This interface is part of an interim API that is still under development and
 * expected to change significantly before reaching stability. It is available at this early stage
 * to solicit feedback from pioneering adopters on the understanding that any code that uses this
 * API will almost certainly be broken (repeatedly) as the API evolves.<p>
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
public class DefaultJPQLQueryHelper extends AbstractJPQLQueryHelper {

	/**
	 * Creates a new <code>DefaultJPQLQueryHelper</code>.
	 *
	 * @param jpqlGrammar The {@link JPQLGrammar} that will determine how to parse JPQL queries
	 */
	public DefaultJPQLQueryHelper(JPQLGrammar jpqlGrammar) {
		super(jpqlGrammar);
	}

	/**
	 * Creates a new <code>JPQLQueryHelper</code>.
	 *
	 * @param queryContext The context used to query information about the JPQL query
	 */
	public DefaultJPQLQueryHelper(JPQLQueryContext queryContext) {
		super(queryContext);
	}

	/**
	 * {@inheritDoc}
	 */
	public BasicRefactoringTool buildBasicRefactoringTool() {
		return new DefaultBasicRefactoringTool(
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
		return new DefaultContentAssistVisitor(queryContext);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected DefaultGrammarValidator buildGrammarValidator(JPQLQueryContext queryContext) {
		return new DefaultGrammarValidator(queryContext.getGrammar());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected JPQLQueryContext buildJPQLQueryContext(JPQLGrammar jpqlGrammar) {
		return new DefaultJPQLQueryContext(jpqlGrammar);
	}

	/**
	 * Creates the right {@link IJPQLQueryBuilder} based on the JPQL grammar.
	 *
	 * @return A new concrete instance of {@link IJPQLQueryBuilder}
	 */
	protected IJPQLQueryBuilder buildQueryBuilder() {
		switch (getGrammar().getJPAVersion()) {
			case VERSION_1_0: return new JPQLQueryBuilder1_0();
			case VERSION_2_0: return new JPQLQueryBuilder2_0();
			default:          return new JPQLQueryBuilder2_1();
		}
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
	protected DefaultSemanticValidator buildSemanticValidator(JPQLQueryContext queryContext) {
		return new DefaultSemanticValidator(queryContext);
	}
}