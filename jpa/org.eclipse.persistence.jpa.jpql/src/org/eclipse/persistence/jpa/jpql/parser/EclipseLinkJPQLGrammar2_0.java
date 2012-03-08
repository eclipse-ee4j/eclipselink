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
package org.eclipse.persistence.jpa.jpql.parser;

import org.eclipse.persistence.jpa.jpql.spi.JPAVersion;

/**
 * This {@link JPQLGrammar} provides support for parsing JPQL queries defined in <a href="http://jcp.org/en/jsr/detail?id=317">JSR-337
 * - Java Persistence 2.0</a>. EclipseLink 2.0 extended support to some BNFs.
 * <p>
 * The following BNFs is the EclipseLink's extension over the standard JPQL BNF.
 *
 * <pre><code>orderby_item ::= state_field_path_expression | result_variable | scalar_expression [ ASC | DESC ]
 *
 * groupby_item ::= single_valued_path_expression | identification_variable | scalar_expression
 *
 * aggregate_expression ::= { AVG | MAX | MIN | SUM | COUNT } ([DISTINCT] scalar_expression)
 *
 * in_item ::= literal | single_valued_input_parameter | scalar_expression
 *
 * </code></pre>
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class EclipseLinkJPQLGrammar2_0 extends AbstractJPQLGrammar {

	/**
	 * The singleton instance of this {@link EclipseLinkJPQLGrammar2_0}.
	 */
	private static final JPQLGrammar INSTANCE = new EclipseLinkJPQLGrammar2_0();

	/**
	 * The EclipseLink version, which is 2.0.
	 */
	public static final String VERSION = "2.0";

	/**
	 * Creates a new <code>EclipseLinkJPQLGrammar2_0</code>.
	 */
	public EclipseLinkJPQLGrammar2_0() {
		super();
	}

	/**
	 * Returns the singleton instance of this class.
	 *
	 * @return The {@link EclipseLinkJPQLGrammar2_0}
	 */
	public static JPQLGrammar instance() {
		return INSTANCE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected JPQLGrammar buildBaseGrammar() {
		return new JPQLGrammar2_0();
	}

	/**
	 * {@inheritDoc}
	 */
	public JPAVersion getJPAVersion() {
		return JPAVersion.VERSION_2_0;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getProviderVersion() {
		return VERSION;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initializeBNFs() {
		addChildBNF(InternalOrderByItemBNF.ID, ScalarExpressionBNF.ID);
		addChildBNF(GroupByItemBNF.ID,         ScalarExpressionBNF.ID);
		addChildBNF(InExpressionItemBNF.ID,    IdentificationVariableBNF.ID);
		addChildBNF(InExpressionItemBNF.ID,    InputParameterBNF.ID);
		addChildBNF(InItemBNF.ID,              ScalarExpressionBNF.ID);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initializeExpressionFactories() {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initializeIdentifiers() {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "EclipseLink 2.0";
	}
}