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
package org.eclipse.persistence.jpa.jpql.parser;

import org.eclipse.persistence.jpa.jpql.parser.FunctionExpressionFactory.ParameterCount;
import org.eclipse.persistence.jpa.jpql.spi.JPAVersion;

import static org.eclipse.persistence.jpa.jpql.parser.Expression.*;

/**
 * This {@link JPQLGrammar} provides support for parsing JPQL queries defined in <a
 * href="http://jcp.org/en/jsr/detail?id=317">JSR-338 - Java Persistence 2.1</a> and the additional
 * support provided by EclipseLink 2.4.
 * <p>
 * The BNFs of the additional support are the following:
 *
 * <pre><code> select_statement ::= select_clause from_clause [where_clause] [groupby_clause] [having_clause] [orderby_clause] {union_clause}*
 *
 * union_clause ::= { UNION | INTERSECT | EXCEPT} [ALL] subquery
 *
 * from_clause ::= FROM identification_variable_declaration {, {identification_variable_declaration |
 *                                                              collection_member_declaration |
 *                                                              (subquery) |
 *                                                              table_variable_declaration }}*
 *
 * range_variable_declaration ::= root_object [AS] identification_variable
 *
 * root_object ::= abstract_schema_name | fully_qualified_class_name
 *
 * table_variable_declaration ::= table_expression [AS] identification_variable
 *
 * join ::= join_spec { abstract_schema_name | join_association_path_expression } [AS] identification_variable [join_condition]
 *
 * functions_returning_datetime ::= cast_expression |
 *                                  extract_expression |
 *                                  ...
 *
 * functions_returning_string ::= cast_expression |
 *                                extract_expression |
 *                                ...
 *
 * functions_returning_numeric ::= cast_expression |
 *                                 extract_expression |
 *                                 ...
 *
 * simple_cond_expression ::= regexp_expression |
 *                            ...
 *
 * function_expression ::= { FUNC | FUNCTION | OPERATOR | SQL | COLUMN } (string_literal {, function_arg}*)
 *
 * regexp_expression ::= string_expression REGEXP pattern_value
 *
 * extract_expression ::= EXTRACT(date_part_literal [FROM] scalar_expression)
 *
 * table_expression ::= TABLE(string_literal)
 *
 * date_part_literal ::= { MICROSECOND | SECOND | MINUTE | HOUR | DAY | WEEK | MONTH | QUARTER |
 *                         YEAR | SECOND_MICROSECOND | MINUTE_MICROSECOND | MINUTE_SECOND |
 *                         HOUR_MICROSECOND | HOUR_SECOND | HOUR_MINUTE | DAY_MICROSECOND |
 *                         DAY_SECOND | DAY_MINUTE | DAY_HOUR | YEAR_MONTH, etc }
 *
 * cast_expression ::= CAST(scalar_expression [AS] database_type)
 *
 * database_type ::= data_type_literal [( [numeric_literal [, numeric_literal]] )]
 *
 * data_type_literal ::= [CHAR, VARCHAR, NUMERIC, INTEGER, DATE, TIME, TIMESTAMP, etc]
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
	 * Creates a new <code>EclipseLinkJPQLGrammar2_4</code>.
	 *
	 * @param jpqlGrammar The {@link JPQLGrammar} to extend with the content of this one without
	 * instantiating the base {@link JPQLGrammar}
	 */
	private EclipseLinkJPQLGrammar2_4(AbstractJPQLGrammar jpqlGrammar) {
		super(jpqlGrammar);
	}

	/**
	 * Extends the given {@link JPQLGrammar} with the information of this one without instantiating
	 * the base {@link JPQLGrammar}.
	 *
	 * @param jpqlGrammar The {@link JPQLGrammar} to extend with the content of this one without
	 * instantiating the base {@link JPQLGrammar}
	 */
	public static void extend(AbstractJPQLGrammar jpqlGrammar) {
		new EclipseLinkJPQLGrammar2_4(jpqlGrammar);
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

		// First build the JPQL 2.1 grammar
		JPQLGrammar2_1 jpqlGrammar = new JPQLGrammar2_1();

		// Extend it by adding the EclipseLink 2.0 additional support
		EclipseLinkJPQLGrammar2_0.extend(jpqlGrammar);

		// Extend it by adding the EclipseLink 2.1 additional support
		EclipseLinkJPQLGrammar2_1.extend(jpqlGrammar);

		return jpqlGrammar;
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

		registerBNF(new CastExpressionBNF());
		registerBNF(new DatabaseTypeQueryBNF());
		registerBNF(new ExtractExpressionBNF());
		registerBNF(new InternalColumnExpressionBNF());
		registerBNF(new RegexpExpressionBNF());
		registerBNF(new TableExpressionBNF());
		registerBNF(new TableVariableDeclarationBNF());
		registerBNF(new UnionClauseBNF());

		// This is required to properly validate an entity name used as a join association path
		addChildBNF(JoinAssociationPathExpressionBNF.ID, AbstractSchemaNameBNF.ID);

		// Override (internal) simple_select_expression to add support for result variable
		registerBNF(new SimpleResultVariableBNF());

		// Note: This should only support SQL expression
		addChildBNF(SimpleConditionalExpressionBNF.ID,   FunctionExpressionBNF.ID);

		// CAST
		addChildBNF(FunctionsReturningDatetimeBNF.ID,    CastExpressionBNF.ID);
		addChildBNF(FunctionsReturningNumericsBNF.ID,    CastExpressionBNF.ID);
		addChildBNF(FunctionsReturningStringsBNF.ID,     CastExpressionBNF.ID);

		// EXTRACT
		addChildBNF(FunctionsReturningDatetimeBNF.ID,    ExtractExpressionBNF.ID);
		addChildBNF(FunctionsReturningNumericsBNF.ID,    ExtractExpressionBNF.ID);
		addChildBNF(FunctionsReturningStringsBNF.ID,     ExtractExpressionBNF.ID);

		// REGEXP
		addChildBNF(SimpleConditionalExpressionBNF.ID,   RegexpExpressionBNF.ID);

		// Add subquery support to RangeDeclarationBNF
		addChildBNF(RangeDeclarationBNF.ID,              SubqueryBNF.ID);

		// Add table declaration to the FROM clause's declaration
		addChildBNF(InternalFromClauseBNF.ID,            TableVariableDeclarationBNF.ID);
		addChildBNF(InternalSimpleFromClauseBNF.ID,      TableVariableDeclarationBNF.ID);

		// Trickle down the handling of a sub expression (subquery) to RangeVariableDeclaration
		// and in order to keep the hierarchy intact, otherwise the default behavior would be
		// FromClause would parse the subquery immediately.
		//
		// FromClause
		//  |- IdentificationVariableDeclaration
		//      |- RangeVariableDeclaration [(subquery) AS identification_variable]
		//          |- SubExpression
		//              |- SimpleSelectStatement
		setHandleSubExpression(InternalFromClauseBNF.ID,                true);
		setHandleSubExpression(InternalSimpleFromClauseBNF.ID,          true);
		setHandleSubExpression(IdentificationVariableDeclarationBNF.ID, true);
		setHandleSubExpression(RangeVariableDeclarationBNF.ID,          true);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initializeExpressionFactories() {

		registerFactory(new CastExpressionFactory());
		registerFactory(new DatabaseTypeFactory());
		registerFactory(new ExtractExpressionFactory());
		registerFactory(new JoinCollectionValuedPathExpressionFactory());
		registerFactory(new OnClauseFactory());
		registerFactory(new RegexpExpressionFactory());
		registerFactory(new TableExpressionFactory());
		registerFactory(new TableVariableDeclarationFactory());
		registerFactory(new UnionClauseFactory());

		// Add a new FunctionExpression for 'COLUMN' since it has different rules
		FunctionExpressionFactory columnExpressionFactory = new FunctionExpressionFactory(COLUMN, COLUMN);
		columnExpressionFactory.setParameterCount(ParameterCount.ONE);
		columnExpressionFactory.setParameterQueryBNFId(InternalColumnExpressionBNF.ID);
		registerFactory(columnExpressionFactory);

		// Add COLUMN ExpressionFactory to FunctionExpressionBNF
		addChildFactory(FunctionExpressionBNF.ID, COLUMN);

		// Change the fallback ExpressionFactory to add support for an abstract schema name
		// as a valid join association path expression
		setFallbackExpressionFactoryId(JoinAssociationPathExpressionBNF.ID, JoinCollectionValuedPathExpressionFactory.ID);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initializeIdentifiers() {

		// Expand FunctionExpression to support 'FUNCTION', 'OPERATOR' and 'SQL'
		addIdentifiers(FunctionExpressionFactory.ID, FUNCTION, OPERATOR, SQL);

		registerIdentifierRole(CAST,           IdentifierRole.FUNCTION);          // FUNCTION(n, x1, ..., x2)
		registerIdentifierRole(COLUMN,         IdentifierRole.FUNCTION);          // FUNCTION(n, x1, ..., x2)
		registerIdentifierRole(EXCEPT,         IdentifierRole.CLAUSE);
		registerIdentifierRole(EXTRACT,        IdentifierRole.FUNCTION);          // EXTRACT(x FROM y)
		registerIdentifierRole(FUNCTION,       IdentifierRole.FUNCTION);          // FUNCTION(n, x1, ..., x2)
		registerIdentifierRole(INTERSECT,      IdentifierRole.CLAUSE);
		registerIdentifierRole(NULLS_FIRST,    IdentifierRole.COMPLETEMENT);
		registerIdentifierRole(NULLS_LAST,     IdentifierRole.COMPLETEMENT);
		registerIdentifierRole(ON,             IdentifierRole.COMPOUND_FUNCTION); // ON x
		registerIdentifierRole(OPERATOR,       IdentifierRole.FUNCTION);          // FUNCTION(n, x1, ..., x2)
		registerIdentifierRole(REGEXP,         IdentifierRole.COMPOUND_FUNCTION); // x REGEXP y
		registerIdentifierRole(SQL,            IdentifierRole.FUNCTION);          // FUNCTION(n, x1, ..., x2)
		registerIdentifierRole(TABLE,          IdentifierRole.FUNCTION);          // TABLE('TABLE_NAME')
		registerIdentifierRole(UNION,          IdentifierRole.CLAUSE);

		registerIdentifierVersion(CAST,        JPAVersion.VERSION_2_1);
		registerIdentifierVersion(COLUMN,      JPAVersion.VERSION_2_1);
		registerIdentifierVersion(EXCEPT,      JPAVersion.VERSION_2_1);
		registerIdentifierVersion(EXTRACT,     JPAVersion.VERSION_2_1);
		registerIdentifierVersion(FUNCTION,    JPAVersion.VERSION_2_1);
		registerIdentifierVersion(INTERSECT,   JPAVersion.VERSION_2_1);
		registerIdentifierVersion(NULLS_FIRST, JPAVersion.VERSION_2_1);
		registerIdentifierVersion(NULLS_LAST,  JPAVersion.VERSION_2_1);
		registerIdentifierVersion(ON,          JPAVersion.VERSION_2_1);
		registerIdentifierVersion(OPERATOR,    JPAVersion.VERSION_2_1);
		registerIdentifierVersion(REGEXP,      JPAVersion.VERSION_2_1);
		registerIdentifierVersion(SQL,         JPAVersion.VERSION_2_1);
		registerIdentifierVersion(TABLE,       JPAVersion.VERSION_2_1);
		registerIdentifierVersion(UNION,       JPAVersion.VERSION_2_1);

		// Partial identifiers
		registerIdentifierRole("NULLS",        IdentifierRole.CLAUSE);       // Part of NULLS FIRST, NULLS LAST
		registerIdentifierRole("FIRST",        IdentifierRole.CLAUSE);       // Part of NULLS FIRST
		registerIdentifierRole("LAST",         IdentifierRole.CLAUSE);       // Part of NULLS LAST
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "EclipseLink 2.4";
	}
}