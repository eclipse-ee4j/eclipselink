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

import org.eclipse.persistence.jpa.jpql.parser.FunctionExpressionFactory.ParameterCount;
import org.eclipse.persistence.jpa.jpql.spi.JPAVersion;

import static org.eclipse.persistence.jpa.jpql.parser.Expression.*;

/**
 * This {@link JPQLGrammar JPQL grammar} provides support for parsing JPQL queries defined in
 * <a href="http://jcp.org/en/jsr/detail?id=317">JSR-338 - Java Persistence 2.1</a> with EclipseLink
 * 2.4 additional support.
 * <p>
 * The following BNFs is the EclipseLink 2.4 additional support added on top of EclipseLink 2.3.
 *
 * TODO
 *
 * </code></pre>
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class EclipseLinkJPQLGrammar2_4 extends AbstractJPQLGrammar {

	/**
	 * The singleton instance of this {@link EclipseLinkJPQLGrammar2_4}.
	 */
	private static final JPQLGrammar INSTANCE = new EclipseLinkJPQLGrammar2_4();

	/**
	 * The EclipseLink version, which is 2.4.
	 */
	public static final String VERSION = "2.4";

	/**
	 * Creates a new <code>EclipseLinkJPQLGrammar2_4</code>.
	 */
	public EclipseLinkJPQLGrammar2_4() {
		super();
	}

	/**
	 * Returns the singleton instance of this class.
	 *
	 * @return The singleton instance of {@link EclipseLinkJPQLGrammar2_4}
	 */
	public static JPQLGrammar instance() {
		return INSTANCE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected JPQLGrammar buildBaseGrammar() {
		// IMPORTANT: Because EclipseLink 2.2 and 2.3 did not added new functionality,
		// we'll skip creating an instance of those grammars and go directly to 2.1
		// since that one added functionality
		return new EclipseLinkJPQLGrammar2_1();
	}

	/**
	 * {@inheritDoc}
	 */
	public JPAVersion getJPAVersion() {
		return JPAVersion.VERSION_2_1;
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

		registerBNF(new InternalColumnExpressionBNF());
		registerBNF(new OnClauseBNF());

		// Extend the query BNF to add support for ON
		addChildBNF(JoinAssociationPathExpressionBNF.ID, OnClauseBNF.ID);

		// Extend the query BNF
		addChildBNF(ArithmeticPrimaryBNF.ID, SubqueryBNF.ID);

		// Extend the query BNF to add support for COLUMN
		addChildBNF(FunctionsReturningDatetimeBNF.ID, FunctionExpressionBNF.ID);
		addChildBNF(FunctionsReturningNumericsBNF.ID, FunctionExpressionBNF.ID);
		addChildBNF(FunctionsReturningStringsBNF.ID,  FunctionExpressionBNF.ID);

		// Note: This should only support SQL expression
		addChildBNF(SimpleConditionalExpressionBNF.ID, FunctionExpressionBNF.ID);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initializeExpressionFactories() {

		registerFactory(new OnClauseFactory());

		// Add a new FunctionExpression for 'COLUMN' since it has different rules
		FunctionExpressionFactory columnExpressionFactory = new FunctionExpressionFactory(COLUMN, COLUMN);
		columnExpressionFactory.setParameterCount(ParameterCount.ONE);
		columnExpressionFactory.setParameterQueryBNFId(InternalColumnExpressionBNF.ID);
		registerFactory(columnExpressionFactory);

		// Add COLUMN ExpressionFactory to FunctionExpressionBNF
		addChildFactory(FunctionExpressionBNF.ID, COLUMN);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initializeIdentifiers() {

		// Expand FunctionExpression to support 'FUNCTION', 'OPERATOR' and 'SQL'
		addIdentifiers(FunctionExpressionFactory.ID, FUNCTION, OPERATOR, SQL);

		registerIdentifierRole(COLUMN,   IdentifierRole.FUNCTION);          // FUNCTION(n, x1, ..., x2)
		registerIdentifierRole(FUNCTION, IdentifierRole.FUNCTION);          // FUNCTION(n, x1, ..., x2)
		registerIdentifierRole(ON,       IdentifierRole.COMPOUND_FUNCTION); // ON x
		registerIdentifierRole(OPERATOR, IdentifierRole.FUNCTION);          // FUNCTION(n, x1, ..., x2)
		registerIdentifierRole(SQL,      IdentifierRole.FUNCTION);          // FUNCTION(n, x1, ..., x2)

		registerIdentifierVersion(COLUMN,   JPAVersion.VERSION_2_1);
		registerIdentifierVersion(FUNCTION, JPAVersion.VERSION_2_1);
		registerIdentifierVersion(ON,       JPAVersion.VERSION_2_1);
		registerIdentifierVersion(OPERATOR, JPAVersion.VERSION_2_1);
		registerIdentifierVersion(SQL,      JPAVersion.VERSION_2_1);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "EclipseLink 2.4";
	}
}