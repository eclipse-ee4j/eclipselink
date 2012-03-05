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
 * - Java Persistence 2.0</a>. EclipseLink 2.4 provides additional support for one additional JPQL
 * identifier: <code><b>ON</b></code>.
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
	 * @return The {@link EclipseLinkJPQLGrammar2_4}
	 */
	public static JPQLGrammar instance() {
		return INSTANCE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected JPQLGrammar buildBaseGrammar() {
		return new EclipseLinkJPQLGrammar2_3();
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

                registerBNF(new FunctionExpressionBNF());
                registerBNF(new FunctionItemBNF());
                registerBNF(new SQLExpressionBNF());
                registerBNF(new OperatorExpressionBNF());
                registerBNF(new SQLItemBNF());
		registerBNF(new OnClauseBNF());
                registerBNF(new ColumnExpressionBNF());

		// Extend the query BNF to add support for ON
		addChildBNF(JoinAssociationPathExpressionBNF.ID, OnClauseBNF.ID);

                // Extend the query BNF to add support for FUNCTION
                addChildBNF(FunctionsReturningDatetimeBNF.ID, FunctionExpressionBNF.ID);
                addChildBNF(FunctionsReturningNumericsBNF.ID, FunctionExpressionBNF.ID);
                addChildBNF(FunctionsReturningStringsBNF.ID,  FunctionExpressionBNF.ID);

                // Extend the query BNF to add support for SQL
                addChildBNF(FunctionsReturningDatetimeBNF.ID, SQLExpressionBNF.ID);
                addChildBNF(FunctionsReturningNumericsBNF.ID, SQLExpressionBNF.ID);
                addChildBNF(FunctionsReturningStringsBNF.ID,  SQLExpressionBNF.ID);
                addChildBNF(SimpleConditionalExpressionBNF.ID,  SQLExpressionBNF.ID);
                
                // Extend the query BNF to add support for SQL
                addChildBNF(FunctionsReturningDatetimeBNF.ID, OperatorExpressionBNF.ID);
                addChildBNF(FunctionsReturningNumericsBNF.ID, OperatorExpressionBNF.ID);
                addChildBNF(FunctionsReturningStringsBNF.ID,  OperatorExpressionBNF.ID);
                
                // Extend the query BNF to add support for COLUMN
                addChildBNF(FunctionsReturningDatetimeBNF.ID, ColumnExpressionBNF.ID);
                addChildBNF(FunctionsReturningNumericsBNF.ID, ColumnExpressionBNF.ID);
                addChildBNF(FunctionsReturningStringsBNF.ID,  ColumnExpressionBNF.ID);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initializeExpressionFactories() {
                registerFactory(new FunctionExpressionFactory());
                registerFactory(new SQLExpressionFactory());
                registerFactory(new ColumnExpressionFactory());
                registerFactory(new OperatorExpressionFactory());
		registerFactory(new OnClauseFactory());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initializeIdentifiers() {

                registerIdentifierRole(Expression.COLUMN,  IdentifierRole.FUNCTION); // FUNCTION(n, x1, ..., x2)
                registerIdentifierRole(Expression.OPERATOR,  IdentifierRole.FUNCTION); // FUNCTION(n, x1, ..., x2)
                registerIdentifierRole(Expression.SQL,  IdentifierRole.FUNCTION); // FUNCTION(n, x1, ..., x2)
	        registerIdentifierRole(Expression.FUNCTION,  IdentifierRole.FUNCTION); // FUNCTION(n, x1, ..., x2)
		registerIdentifierRole(Expression.ON, IdentifierRole.COMPOUND_FUNCTION); // ON x

                registerIdentifierVersion(Expression.SQL, JPAVersion.VERSION_2_1);
                registerIdentifierVersion(Expression.COLUMN, JPAVersion.VERSION_2_1);
                registerIdentifierVersion(Expression.OPERATOR, JPAVersion.VERSION_2_1);
                registerIdentifierVersion(Expression.FUNCTION, JPAVersion.VERSION_2_1);
		registerIdentifierVersion(Expression.ON, JPAVersion.VERSION_2_1);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "JPQL grammar for EclipseLink 2.4";
	}
}