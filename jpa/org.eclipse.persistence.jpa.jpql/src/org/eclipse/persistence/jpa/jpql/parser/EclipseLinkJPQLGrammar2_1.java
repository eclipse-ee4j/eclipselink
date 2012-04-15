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

import static org.eclipse.persistence.jpa.jpql.parser.Expression.*;

/**
 * This {@link JPQLGrammar} provides support for parsing JPQL queries defined in <a
 * href="http://jcp.org/en/jsr/detail?id=317">JSR-337 - Java Persistence 2.0</a> and the additional
 * support provided by EclipseLink 2.1.
 * <p>
 * The BNFs of the additional support are the following:
 *
 * <pre><code> join ::= join_spec { join_association_path_expression | join_treat_association_path_expression } [AS] identification_variable
 *
 * join_treat_association_path_expression ::= TREAT(join_association_path_expression AS entity_type_literal)
 *
 * func_expression ::= FUNC(string_literal {, func_item}*)
 *
 * func_item ::= new_value
 *
 * between_expression ::= scalarOrSubSelectExpression [NOT] BETWEEN scalarOrSubSelectExpression AND scalarOrSubSelectExpression
 *
 * in_item ::= literal | single_valued_input_parameter | scalarOrSubSelectExpression
 *
 * scalarOrSubSelectExpression ::= arithmetic_expression | scalar_expression
 *
 * pattern_value ::= scalarOrSubSelectExpression
 *
 * escape_character ::= scalarExpression
 *
 * functions_returning_numerics ::= LENGTH(scalarExpression) |
 *                                  LOCATE(scalarExpression, scalarExpression[, scalarExpression]) |
 *                                  MOD(scalarExpression, scalarExpression) |
 *                                  SQRT(scalarExpression)
 *                                  func_expression |
 *                                  ...
 *
 * functions_returning_strings ::= CONCAT(scalarExpression {, scalarExpression }+) |
 *                                 SUBSTRING(scalarExpression, scalarExpression [, scalarExpression]) |
 *                                 LOWER(scalarExpression) |
 *                                 UPPER(scalarExpression) |
 *                                 func_expression |
 *                                 ...
 *
 * orderby_item ::= state_field_path_expression | result_variable | scalar_expression [ ASC | DESC ]
 *
 * groupby_item ::= single_valued_path_expression | identification_variable | scalar_expression
 *
 * aggregate_expression ::= { AVG | MAX | MIN | SUM | COUNT } ([DISTINCT] scalar_expression)
 *
 * </code></pre>
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class EclipseLinkJPQLGrammar2_1 extends AbstractJPQLGrammar {

	/**
	 * The singleton instance of this {@link EclipseLinkJPQLGrammar2_1}.
	 */
	private static final JPQLGrammar INSTANCE = new EclipseLinkJPQLGrammar2_1();

	/**
	 * The EclipseLink version, which is 2.1.
	 */
	public static final String VERSION = "2.1";

	/**
	 * Creates a new <code>EclipseLinkJPQLGrammar2_1</code>.
	 */
	public EclipseLinkJPQLGrammar2_1() {
		super();
	}

	/**
	 * Creates a new <code>EclipseLinkJPQLGrammar2_1</code>.
	 *
	 * @param jpqlGrammar The {@link JPQLGrammar} to extend with the content of this one without
	 * instantiating the base {@link JPQLGrammar}
	 */
	private EclipseLinkJPQLGrammar2_1(AbstractJPQLGrammar jpqlGrammar) {
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
		new EclipseLinkJPQLGrammar2_1(jpqlGrammar);
	}

	/**
	 * Returns the singleton instance of this class.
	 *
	 * @return The {@link EclipseLinkJPQLGrammar2_1}
	 */
	public static JPQLGrammar instance() {
		return INSTANCE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected JPQLGrammar buildBaseGrammar() {

		// First build the JPQL 2.0 grammar
		JPQLGrammar2_0 jpqlGrammar = new JPQLGrammar2_0();

		// Extend it by adding the EclipseLink 2.0 additional support
		EclipseLinkJPQLGrammar2_0.extend(jpqlGrammar);

		return jpqlGrammar;
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

		// Add new query BNF
		registerBNF(new FunctionExpressionBNF());
		registerBNF(new FunctionItemBNF());
		registerBNF(new TreatExpressionBNF());

		// Extend the query BNF to add support new JPQL identifiers
		addChildBNF(FunctionsReturningNumericsBNF.ID,          FunctionExpressionBNF.ID);
		addChildBNF(JoinAssociationPathExpressionBNF.ID,       TreatExpressionBNF.ID);

		// Extend to support scalarExpression
		addChildBNF(GroupByItemBNF.ID,                         ScalarExpressionBNF.ID);
		addChildBNF(LikeExpressionEscapeCharacterBNF.ID,       ScalarExpressionBNF.ID);
		addChildBNF(InternalAggregateFunctionBNF.ID,           ScalarExpressionBNF.ID);
		addChildBNF(InternalConcatExpressionBNF.ID,            ScalarExpressionBNF.ID);
		addChildBNF(InternalLengthExpressionBNF.ID,            ScalarExpressionBNF.ID);
		addChildBNF(InternalLocateStringExpressionBNF.ID,      ScalarExpressionBNF.ID);
		addChildBNF(InternalLocateThirdExpressionBNF.ID,       ScalarExpressionBNF.ID);
		addChildBNF(InternalLowerExpressionBNF.ID,             ScalarExpressionBNF.ID);
		addChildBNF(InternalModExpressionBNF.ID,               ScalarExpressionBNF.ID);
		addChildBNF(InternalOrderByItemBNF.ID,                 ScalarExpressionBNF.ID);
		addChildBNF(InternalSqrtExpressionBNF.ID,              ScalarExpressionBNF.ID);
		addChildBNF(InternalSubstringPositionExpressionBNF.ID, ScalarExpressionBNF.ID);
		addChildBNF(InternalSubstringStringExpressionBNF.ID,   ScalarExpressionBNF.ID);
		addChildBNF(InternalUpperExpressionBNF.ID,             ScalarExpressionBNF.ID);

		// Extend to support scalarOrSubSelectExpression
		addChildBNF(InternalBetweenExpressionBNF.ID,           ScalarExpressionBNF.ID);
		addChildBNF(InternalBetweenExpressionBNF.ID,           ArithmeticExpressionBNF.ID);
		addChildBNF(InExpressionItemBNF.ID,                    ScalarExpressionBNF.ID);
		addChildBNF(InExpressionItemBNF.ID,                    ArithmeticExpressionBNF.ID);
		addChildBNF(PatternValueBNF.ID,                        ScalarExpressionBNF.ID);
		addChildBNF(PatternValueBNF.ID,                        ArithmeticExpressionBNF.ID);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initializeExpressionFactories() {

		registerFactory(new FunctionExpressionFactory(FunctionExpressionFactory.ID, FUNC));
		registerFactory(new TreatExpressionFactory());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initializeIdentifiers() {

		// Expand ComparisonExpression to support !=
		addIdentifiers(ComparisonExpressionFactory.ID, NOT_EQUAL);

		registerIdentifierRole(FUNC,      IdentifierRole.FUNCTION);  // FUNC(n, x1, ..., x2)
		registerIdentifierRole(NOT_EQUAL, IdentifierRole.AGGREGATE); // x != y
		registerIdentifierRole(TREAT,     IdentifierRole.FUNCTION);  // TREAT(x AS y)

		registerIdentifierVersion(FUNC,      JPAVersion.VERSION_2_0);
		registerIdentifierVersion(NOT_EQUAL, JPAVersion.VERSION_2_0);
		registerIdentifierVersion(TREAT,     JPAVersion.VERSION_2_0);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "EclipseLink 2.1";
	}
}