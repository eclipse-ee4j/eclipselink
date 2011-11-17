/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
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

/**
 * This helper can perform the following operations over a JPQL query:
 * <ul>
 * <li>Calculates the result type of a query: {@link #getResultType()};</li>
 * <li>Calculates the type of an input parameter: {@link #getParameterType(String)}.</li>
 * <li>Calculates the possible choices to complete the query from a given
 *     position (used for content assist): {@link #buildContentAssistItems(int)}.</li>
 * <li>Validates the query by introspecting it grammatically and semantically:
 *     <ul>
 *     <li>{@link #validate()},</li>
 *     <li>{@link #validateGrammar()},</li>
 *     <li>{@link #validateSemantic()}.</li>
 *     </ul></li>
 * </ul>
 *
 * This helper should be used when the JPQL query is written using the JPQL grammar defined in the
 * Java Persistence functional specification 2.0 and it contains the additional support provided by
 * EclipseLink.<p>
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public class EclipseLinkJPQLQueryHelper extends AbstractJPQLQueryHelper {

	/**
	 * Creates a new <code>EclipseLinkJPQLQueryHelper</code>.
	 *
	 * @param jpqlGrammar
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
	protected AbstractContentAssistVisitor buildContentAssistVisitor(JPQLQueryContext queryContext) {
		return new EclipseLinkContentAssistVisitor(queryContext);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected EclipseLinkGrammarValidator buildGrammarValidator(JPQLQueryContext queryContext) {
		return new EclipseLinkGrammarValidator(queryContext);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected JPQLQueryContext buildJPQLQueryContext(JPQLGrammar jpqlGrammar) {
		return new EclipseLinkJPQLQueryContext(jpqlGrammar);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected EclipseLinkSemanticValidator buildSemanticValidator(JPQLQueryContext queryContext) {
		return new EclipseLinkSemanticValidator(queryContext);
	}
}