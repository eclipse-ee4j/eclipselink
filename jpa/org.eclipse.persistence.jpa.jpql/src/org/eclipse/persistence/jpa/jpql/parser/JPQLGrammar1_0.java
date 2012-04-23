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

import org.eclipse.persistence.jpa.jpql.ExpressionTools;
import org.eclipse.persistence.jpa.jpql.spi.JPAVersion;

/**
 * This {@link JPQLGrammar} provides support for parsing JPQL queries defined in <a
 * href="http://jcp.org/en/jsr/detail?id=220">JSR-220 - Enterprise JavaBeans 3.0</a>.
 * <p>
 * The following is the BNF for the JPQL query version 1.0.
 *
 * <pre><code> QL_statement ::= select_statement | update_statement | delete_statement
 *
 * select_statement ::= select_clause from_clause [where_clause] [groupby_clause] [having_clause] [orderby_clause]
 *
 * update_statement ::= update_clause [where_clause]
 *
 * delete_statement ::= delete_clause [where_clause]
 *
 * from_clause ::= FROM identification_variable_declaration {, {identification_variable_declaration | collection_member_declaration}}*
 *
 * identification_variable_declaration ::= range_variable_declaration { join | fetch_join }*
 *
 * range_variable_declaration ::= abstract_schema_name [AS] identification_variable
 *
 * join ::= join_spec join_association_path_expression [AS] identification_variable
 *
 * fetch_join ::= join_spec FETCH join_association_path_expression
 *
 * association_path_expression ::= collection_valued_path_expression | single_valued_association_path_expression
 *
 * join_spec::= [ LEFT [OUTER] | INNER ] JOIN
 *
 * join_association_path_expression ::= join_collection_valued_path_expression |
 *                                      join_single_valued_association_path_expression
 *
 * join_collection_valued_path_expression::= identification_variable.collection_valued_association_field
 *
 * join_single_valued_association_path_expression::= identification_variable.single_valued_association_field
 *
 * collection_member_declaration ::= IN (collection_valued_path_expression) [AS] identification_variable
 *
 * single_valued_path_expression ::= state_field_path_expression |
 *                                   single_valued_association_path_expression
 *
 * state_field_path_expression ::= {identification_variable | single_valued_association_path_expression}.state_field
 *
 * single_valued_association_path_expression ::= identification_variable.{single_valued_association_field.}* single_valued_association_field
 *
 * collection_valued_path_expression ::= identification_variable.{single_valued_association_field.}*collection_valued_association_field
 *
 * state_field ::= {embedded_class_state_field.}*simple_state_field
 *
 * update_clause ::= UPDATE abstract_schema_name [[AS] identification_variable] SET update_item {, update_item}*
 *
 * update_item ::= [identification_variable.]{state_field | single_valued_association_field} = new_value
 *
 * new_value ::= simple_arithmetic_expression |
 *               string_primary |
 *               datetime_primary |
 *               boolean_primary |
 *               enum_primary |
 *               simple_entity_expression |
 *               NULL
 *
 * delete_clause ::= DELETE FROM abstract_schema_name [[AS] identification_variable]
 *
 * select_clause ::= SELECT [DISTINCT] select_expression {, select_expression}*
 *
 * select_expression ::= single_valued_path_expression |
 *                       aggregate_expression |
 *                       identification_variable |
 *                       OBJECT(identification_variable) |
 *                       constructor_expression
 *
 * constructor_expression ::= NEW constructor_name ( constructor_item {, constructor_item}* )
 *
 * constructor_item ::= single_valued_path_expression | aggregate_expression
 *
 * aggregate_expression ::= { AVG | MAX | MIN | SUM } ([DISTINCT] state_field_path_expression) |
 *                          COUNT ([DISTINCT] identification_variable |
 *                                            state_field_path_expression |
 *                                            single_valued_association_path_expression)
 *
 * where_clause ::= WHERE conditional_expression
 *
 * groupby_clause ::= GROUP BY groupby_item {, groupby_item}*
 *
 * groupby_item ::= single_valued_path_expression | identification_variable
 *
 * having_clause ::= HAVING conditional_expression
 *
 * orderby_clause ::= ORDER BY orderby_item {, orderby_item}*
 *
 * orderby_item ::= state_field_path_expression [ ASC | DESC ]
 *
 * subquery ::= simple_select_clause subquery_from_clause [where_clause] [groupby_clause] [having_clause]
 *
 * subquery_from_clause ::= FROM subselect_identification_variable_declaration {, subselect_identification_variable_declaration}*
 *
 * subselect_identification_variable_declaration ::= identification_variable_declaration |
 *                                                   association_path_expression [AS] identification_variable |
 *                                                   collection_member_declaration
 *
 * simple_select_clause ::= SELECT [DISTINCT] simple_select_expression
 *
 * simple_select_expression::= single_valued_path_expression |
 *                             aggregate_expression |
 *                             identification_variable
 *
 * conditional_expression ::= conditional_term | conditional_expression OR conditional_term
 *
 * conditional_term ::= conditional_factor | conditional_term AND conditional_factor
 *
 * conditional_factor ::= [ NOT ] conditional_primary
 *
 * conditional_primary ::= simple_cond_expression | (conditional_expression)
 *
 * simple_cond_expression ::= comparison_expression |
 *                            between_expression |
 *                            like_expression |
 *                            in_expression |
 *                            null_comparison_expression |
 *                            empty_collection_comparison_expression |
 *                            collection_member_expression |
 *                            exists_expression
 *
 * between_expression ::= arithmetic_expression [NOT] BETWEEN arithmetic_expression AND arithmetic_expression |
 *                        string_expression [NOT] BETWEEN string_expression AND string_expression |
 *                        datetime_expression [NOT] BETWEEN datetime_expression AND datetime_expression
 *
 * in_expression ::= state_field_path_expression [NOT] IN ( in_item {, in_item}* | subquery)
 *
 * in_item ::= literal | input_parameter
 *
 * like_expression ::= string_expression [NOT] LIKE pattern_value [ESCAPE escape_character]
 *
 * escape_character ::= single_character_string_literal | character_valued_input_parameter
 *
 * null_comparison_expression ::= {single_valued_path_expression | input_parameter} IS [NOT] NULL
 *
 * empty_collection_comparison_expression ::= collection_valued_path_expression IS [NOT] EMPTY
 *
 * collection_member_expression ::= entity_expression [NOT] MEMBER [OF] collection_valued_path_expression
 *
 * exists_expression::= [NOT] EXISTS (subquery)
 *
 * all_or_any_expression ::= { ALL | ANY | SOME} (subquery)
 *
 * comparison_expression ::= string_expression comparison_operator {string_expression | all_or_any_expression} |
 *                           boolean_expression { = | <> } {boolean_expression | all_or_any_expression} |
 *                           enum_expression { = | <> } {enum_expression | all_or_any_expression} |
 *                           datetime_expression comparison_operator {datetime_expression | all_or_any_expression} |
 *                           entity_expression { = | <> } {entity_expression | all_or_any_expression} |
 *                           arithmetic_expression comparison_operator {arithmetic_expression | all_or_any_expression}
 *
 * comparison_operator ::= = | > | >= | < | <= | <>
 *
 * arithmetic_expression ::= simple_arithmetic_expression | (subquery)
 *
 * simple_arithmetic_expression ::= arithmetic_term | simple_arithmetic_expression { + | - } arithmetic_term
 *
 * arithmetic_term ::= arithmetic_factor | arithmetic_term { * | / } arithmetic_factor
 *
 * arithmetic_factor ::= [{ + | - }] arithmetic_primary
 *
 * arithmetic_primary ::= state_field_path_expression |
 *                        numeric_literal |
 *                        (simple_arithmetic_expression) |
 *                        input_parameter |
 *                        functions_returning_numerics |
 *                        aggregate_expression
 *
 * string_expression ::= string_primary | (subquery)
 *
 * string_primary ::= state_field_path_expression |
 *                    string_literal |
 *                    input_parameter |
 *                    functions_returning_strings |
 *                    aggregate_expression
 *
 * datetime_expression ::= datetime_primary | (subquery)
 *
 * datetime_primary ::= state_field_path_expression |
 *                      input_parameter |
 *                      functions_returning_datetime |
 *                      aggregate_expression
 *
 * boolean_expression ::= boolean_primary | (subquery)
 *
 * boolean_primary ::= state_field_path_expression | boolean_literal | input_parameter |
 *
 * enum_expression ::= enum_primary | (subquery)
 *
 * enum_primary ::= state_field_path_expression | enum_literal | input_parameter
 *
 * entity_expression ::= single_valued_association_path_expression | simple_entity_expression
 *
 * simple_entity_expression ::= identification_variable | input_parameter
 *
 * functions_returning_numerics::= LENGTH(string_primary) |
 *                                 LOCATE(string_primary, string_primary[, simple_arithmetic_expression]) |
 *                                 ABS(simple_arithmetic_expression) |
 *                                 SQRT(simple_arithmetic_expression) |
 *                                 MOD(simple_arithmetic_expression, simple_arithmetic_expression) |
 *                                 SIZE(collection_valued_path_expression)
 *
 * functions_returning_datetime ::= CURRENT_DATE| CURRENT_TIME | CURRENT_TIMESTAMP
 *
 * functions_returning_strings ::= CONCAT(string_primary, string_primary) |
 *                                 SUBSTRING(string_primary, simple_arithmetic_expression, simple_arithmetic_expression) |
 *                                 TRIM([[trim_specification] [trim_character] FROM] string_primary) |
 *                                 LOWER(string_primary) |
 *                                 UPPER(string_primary)
 *
 * trim_specification ::= LEADING | TRAILING | BOTH
 *
 * boolean_literal ::= TRUE | FALSE
 *
 * string_literal ::= 'string'
 *
 * enum_literal ::= {package_name.}*EnumType.CONSTANT
 *
 * literalTemporal ::= date_literal | TIME_LITERAL | TIMESTAMP_LITERAL
 *
 * input_parameter ::= (':' [a-zA-Z]+) | ('?' [0-9]+);  // TODO: TO COMPLETE
 *
 * date_literal ::= "{" "'d'" (' ' | '\t')+ '\'' DATE_STRING '\'' (' ' | '\t')* "}"
 *
 * TIME_LITERAL ::= "{" "'t'" (' ' | '\t')+ '\'' TIME_STRING '\'' (' ' | '\t')* "}"
 *
 * TIMESTAMP_LITERAL ::= "{" ('ts') (' ' | '\t')+ '\'' DATE_STRING ' ' TIME_STRING '\'' (' ' | '\t')* "}"
 *
 * DATE_STRING ::= [0-9] [0-9] [0-9] [0-9] '-' [0-9] [0-9] '-' [0-9] [0-9]
 *
 * TIME_STRING ::= [0-9] ([0-9])? ':' [0-9] [0-9] ':' [0-9] [0-9] '.' [0-9]*
 * </code></pre>
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class JPQLGrammar1_0 extends AbstractJPQLGrammar {

	/**
	 * The singleton instance of this {@link JPQLGrammar1_0}.
	 */
	private static final JPQLGrammar INSTANCE = new JPQLGrammar1_0();

	/**
	 * Creates a new <code>JPQLGrammar1_0</code>.
	 */
	public JPQLGrammar1_0() {
		super();
	}

	/**
	 * Returns the singleton instance of the default implementation of {@link JPQLGrammar} which
	 * provides support for the JPQL grammar defined in the JPA 1.0 functional specification.
	 *
	 * @return The {@link JPQLGrammar} that only has support for JPA 1.0
	 */
	public static JPQLGrammar instance() {
		return INSTANCE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected JPQLGrammar buildBaseGrammar() {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public JPAVersion getJPAVersion() {
		return JPAVersion.VERSION_1_0;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getProviderVersion() {
		return ExpressionTools.EMPTY_STRING;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initializeBNFs() {

		registerBNF(new AbstractSchemaNameBNF());
		registerBNF(new AggregateExpressionBNF());
		registerBNF(new AllOrAnyExpressionBNF());
		registerBNF(new ArithmeticExpressionBNF());
		registerBNF(new ArithmeticFactorBNF());
		registerBNF(new ArithmeticPrimaryBNF());
		registerBNF(new ArithmeticTermBNF());
		registerBNF(new BadExpressionBNF());
		registerBNF(new BetweenExpressionBNF());
		registerBNF(new BooleanExpressionBNF());
		registerBNF(new BooleanLiteralBNF());
		registerBNF(new BooleanPrimaryBNF());
		registerBNF(new CollectionMemberDeclarationBNF());
		registerBNF(new CollectionMemberExpressionBNF());
		registerBNF(new CollectionValuedPathExpressionBNF());
		registerBNF(new ComparisonExpressionBNF());
		registerBNF(new ConditionalExpressionBNF());
		registerBNF(new ConditionalFactorBNF());
		registerBNF(new ConditionalPrimaryBNF());
		registerBNF(new ConditionalTermBNF());
		registerBNF(new ConstructorExpressionBNF());
		registerBNF(new ConstructorItemBNF());
		registerBNF(new DatetimeExpressionBNF());
		registerBNF(new DateTimePrimaryBNF());
		registerBNF(new DateTimeTimestampLiteralBNF());
		registerBNF(new DeleteClauseBNF());
		registerBNF(new DeleteClauseRangeVariableDeclarationBNF());
		registerBNF(new DeleteStatementBNF());
		registerBNF(new DerivedCollectionMemberDeclarationBNF());
		registerBNF(new EmptyCollectionComparisonExpressionBNF());
		registerBNF(new EntityExpressionBNF());
		registerBNF(new EntityOrValueExpressionBNF());
		registerBNF(new EnumExpressionBNF());
		registerBNF(new EnumLiteralBNF());
		registerBNF(new EnumPrimaryBNF());
		registerBNF(new ExistsExpressionBNF());
		registerBNF(new FromClauseBNF());
		registerBNF(new FunctionsReturningDatetimeBNF());
		registerBNF(new FunctionsReturningNumericsBNF());
		registerBNF(new FunctionsReturningStringsBNF());
		registerBNF(new GeneralIdentificationVariableBNF());
		registerBNF(new GroupByClauseBNF());
		registerBNF(new GroupByItemBNF());
		registerBNF(new HavingClauseBNF());
		registerBNF(new IdentificationVariableBNF());
		registerBNF(new IdentificationVariableDeclarationBNF());
		registerBNF(new InExpressionBNF());
		registerBNF(new InExpressionExpressionBNF());
		registerBNF(new InExpressionItemBNF());
		registerBNF(new InputParameterBNF());
		registerBNF(new InternalAggregateFunctionBNF());
		registerBNF(new InternalBetweenExpressionBNF());
		registerBNF(new InternalConcatExpressionBNF());
		registerBNF(new InternalCountBNF());
		registerBNF(new InternalFromClauseBNF());
		registerBNF(new InternalJoinBNF());
		registerBNF(new InternalLengthExpressionBNF());
		registerBNF(new InternalLocateStringExpressionBNF());
		registerBNF(new InternalLocateThirdExpressionBNF());
		registerBNF(new InternalLowerExpressionBNF());
		registerBNF(new InternalModExpressionBNF());
		registerBNF(new InternalOrderByClauseBNF());
		registerBNF(new InternalOrderByItemBNF());
		registerBNF(new InternalSelectExpressionBNF());
		registerBNF(new InternalSimpleFromClauseBNF());
		registerBNF(new InternalSimpleSelectExpressionBNF());
		registerBNF(new InternalSqrtExpressionBNF());
		registerBNF(new InternalSubstringPositionExpressionBNF());
		registerBNF(new InternalSubstringStringExpressionBNF());
		registerBNF(new InternalUpdateClauseBNF());
		registerBNF(new InternalUpperExpressionBNF());
		registerBNF(new JoinAssociationPathExpressionBNF());
		registerBNF(new JoinBNF());
		registerBNF(new JoinFetchBNF());
		registerBNF(new JPQLStatementBNF());
		registerBNF(new LikeExpressionBNF());
		registerBNF(new LikeExpressionEscapeCharacterBNF());
		registerBNF(new LiteralBNF());
		registerBNF(new NewValueBNF());
		registerBNF(new NullComparisonExpressionBNF());
		registerBNF(new NumericLiteralBNF());
		registerBNF(new ObjectExpressionBNF());
		registerBNF(new OrderByClauseBNF());
		registerBNF(new OrderByItemBNF());
		registerBNF(new PatternValueBNF());
		registerBNF(new PreLiteralExpressionBNF());
		registerBNF(new RangeDeclarationBNF());
		registerBNF(new RangeVariableDeclarationBNF());
		registerBNF(new ScalarExpressionBNF());
		registerBNF(new SelectClauseBNF());
		registerBNF(new SelectExpressionBNF());
		registerBNF(new SelectStatementBNF());
		registerBNF(new SimpleArithmeticExpressionBNF());
		registerBNF(new SimpleCaseExpressionBNF());
		registerBNF(new SimpleConditionalExpressionBNF());
		registerBNF(new SimpleEntityExpressionBNF());
		registerBNF(new SimpleEntityOrValueExpressionBNF());
		registerBNF(new SimpleSelectClauseBNF());
		registerBNF(new SimpleSelectExpressionBNF());
		registerBNF(new SingleValuedPathExpressionBNF());
		registerBNF(new StateFieldPathExpressionBNF());
		registerBNF(new StringExpressionBNF());
		registerBNF(new StringLiteralBNF());
		registerBNF(new StringPrimaryBNF());
		registerBNF(new SubqueryBNF());
		registerBNF(new SubqueryFromClauseBNF());
		registerBNF(new SubSelectIdentificationVariableDeclarationBNF());
		registerBNF(new UpdateClauseBNF());
		registerBNF(new UpdateItemBNF());
		registerBNF(new UpdateItemStateFieldPathExpressionBNF());
		registerBNF(new UpdateStatementBNF());
		registerBNF(new WhereClauseBNF());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initializeExpressionFactories() {

		registerFactory(new AbsExpressionFactory());
		registerFactory(new AbstractSchemaNameFactory());
		registerFactory(new AllOrAnyExpressionFactory());
		registerFactory(new AndExpressionFactory());
		registerFactory(new ArithmeticExpressionFactory());
		registerFactory(new AvgFunctionFactory());
		registerFactory(new BadExpressionFactory());
		registerFactory(new BetweenExpressionFactory());
		registerFactory(new CollectionMemberDeclarationFactory());
		registerFactory(new CollectionMemberExpressionFactory());
		registerFactory(new CollectionValuedPathExpressionFactory());
		registerFactory(new ComparisonExpressionFactory());
		registerFactory(new ConcatExpressionFactory());
		registerFactory(new ConstructorExpressionFactory());
		registerFactory(new CountFunctionFactory());
		registerFactory(new DateTimeFactory());
		registerFactory(new DeleteClauseFactory());
		registerFactory(new DeleteStatementFactory());
		registerFactory(new EmptyCollectionComparisonExpressionFactory());
		registerFactory(new ExistsExpressionFactory());
		registerFactory(new FromClauseFactory());
		registerFactory(new GroupByClauseFactory());
		registerFactory(new GroupByItemFactory());
		registerFactory(new HavingClauseFactory());
		registerFactory(new IdentificationVariableDeclarationFactory());
		registerFactory(new IdentificationVariableFactory());
		registerFactory(new InExpressionFactory());
		registerFactory(new InternalOrderByItemFactory());
		registerFactory(new IsExpressionFactory());
		registerFactory(new JoinFactory());
		registerFactory(new KeywordExpressionFactory());
		registerFactory(new LengthExpressionFactory());
		registerFactory(new LikeExpressionFactory());
		registerFactory(new LiteralExpressionFactory());
		registerFactory(new LocateExpressionFactory());
		registerFactory(new LowerExpressionFactory());
		registerFactory(new MaxFunctionFactory());
		registerFactory(new MinFunctionFactory());
		registerFactory(new ModExpressionFactory());
		registerFactory(new NotExpressionFactory());
		registerFactory(new NullComparisonExpressionFactory());
		registerFactory(new NumericLiteralFactory());
		registerFactory(new ObjectExpressionFactory());
		registerFactory(new OrderByClauseFactory());
		registerFactory(new OrderByItemFactory());
		registerFactory(new OrExpressionFactory());
		registerFactory(new PreLiteralExpressionFactory());
		registerFactory(new RangeDeclarationFactory());
		registerFactory(new RangeVariableDeclarationFactory());
		registerFactory(new SelectClauseFactory());
		registerFactory(new SelectStatementFactory());
		registerFactory(new SimpleSelectStatementFactory());
		registerFactory(new SizeExpressionFactory());
		registerFactory(new SqrtExpressionFactory());
		registerFactory(new StateFieldPathExpressionFactory());
		registerFactory(new StringLiteralFactory());
		registerFactory(new SubstringExpressionFactory());
		registerFactory(new SumFunctionFactory());
		registerFactory(new TrimExpressionFactory());
		registerFactory(new UnknownExpressionFactory());
		registerFactory(new UpdateClauseFactory());
		registerFactory(new UpdateItemFactory());
		registerFactory(new UpdateItemStateFieldPathExpressionFactory());
		registerFactory(new UpdateStatementFactory());
		registerFactory(new UpperExpressionFactory());
		registerFactory(new WhereClauseFactory());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initializeIdentifiers() {

		registerIdentifierRole(Expression.ABS,                   IdentifierRole.FUNCTION);           // ABS(x)
		registerIdentifierRole(Expression.ALL,                   IdentifierRole.FUNCTION);           // ALL(x)
		registerIdentifierRole(Expression.AND,                   IdentifierRole.AGGREGATE);          // x AND y
		registerIdentifierRole(Expression.ANY,                   IdentifierRole.FUNCTION);           // ANY(x)
		registerIdentifierRole(Expression.AS,                    IdentifierRole.COMPLETEMENT);
		registerIdentifierRole(Expression.ASC,                   IdentifierRole.COMPLETEMENT);
		registerIdentifierRole(Expression.AVG,                   IdentifierRole.FUNCTION);           // AVG(x)
		registerIdentifierRole(Expression.BETWEEN,               IdentifierRole.COMPOUND_FUNCTION);  // x BETWEEN y AND z
		registerIdentifierRole(Expression.BIT_LENGTH,            IdentifierRole.UNUSED);
		registerIdentifierRole(Expression.BOTH,                  IdentifierRole.COMPLETEMENT);
		registerIdentifierRole(Expression.CHAR_LENGTH,           IdentifierRole.UNUSED);
		registerIdentifierRole(Expression.CHARACTER_LENGTH,      IdentifierRole.UNUSED);
		registerIdentifierRole(Expression.CLASS,                 IdentifierRole.UNUSED);
		registerIdentifierRole(Expression.CONCAT,                IdentifierRole.FUNCTION);           // CONCAT(x, y)
		registerIdentifierRole(Expression.COUNT,                 IdentifierRole.FUNCTION);           // COUNT(x)
		registerIdentifierRole(Expression.CURRENT_DATE,          IdentifierRole.FUNCTION);
		registerIdentifierRole(Expression.CURRENT_TIME,          IdentifierRole.FUNCTION);
		registerIdentifierRole(Expression.CURRENT_TIMESTAMP,     IdentifierRole.FUNCTION);
		registerIdentifierRole(Expression.DELETE,                IdentifierRole.CLAUSE);
		registerIdentifierRole(Expression.DELETE_FROM,           IdentifierRole.CLAUSE);
		registerIdentifierRole(Expression.DESC,                  IdentifierRole.COMPLETEMENT);
		registerIdentifierRole(Expression.DISTINCT,              IdentifierRole.COMPLETEMENT);
		registerIdentifierRole(Expression.EMPTY,                 IdentifierRole.COMPOUND_FUNCTION);
		registerIdentifierRole(Expression.ESCAPE,                IdentifierRole.COMPLETEMENT);
		registerIdentifierRole(Expression.EXISTS,                IdentifierRole.FUNCTION);           // EXISTS(x)
		registerIdentifierRole(Expression.FALSE,                 IdentifierRole.FUNCTION);
		registerIdentifierRole(Expression.FETCH,                 IdentifierRole.COMPOUND_FUNCTION);
		registerIdentifierRole(Expression.FROM,                  IdentifierRole.CLAUSE);
		registerIdentifierRole(Expression.HAVING,                IdentifierRole.CLAUSE);
		registerIdentifierRole(Expression.IN,                    IdentifierRole.COMPOUND_FUNCTION);  // x IN { (y {, z}* | (s) | t }
		registerIdentifierRole(Expression.INNER,                 IdentifierRole.COMPOUND_FUNCTION);  // Part of JOIN
		registerIdentifierRole(Expression.IS,                    IdentifierRole.COMPOUND_FUNCTION);
		registerIdentifierRole(Expression.JOIN,                  IdentifierRole.COMPOUND_FUNCTION);
		registerIdentifierRole(Expression.LEADING,               IdentifierRole.COMPLETEMENT);
		registerIdentifierRole(Expression.LEFT,                  IdentifierRole.COMPLETEMENT);
		registerIdentifierRole(Expression.LENGTH,                IdentifierRole.FUNCTION);           // LENGTH(x)
		registerIdentifierRole(Expression.LIKE,                  IdentifierRole.COMPOUND_FUNCTION);
		registerIdentifierRole(Expression.LOCATE,                IdentifierRole.FUNCTION);           // LOCATE(x, y [, z]))
		registerIdentifierRole(Expression.LOWER,                 IdentifierRole.FUNCTION);           // LOWER(x)
		registerIdentifierRole(Expression.MAX,                   IdentifierRole.FUNCTION);           // MAX(x)
		registerIdentifierRole(Expression.MEMBER,                IdentifierRole.COMPOUND_FUNCTION);  // x MEMBER y
		registerIdentifierRole(Expression.MIN,                   IdentifierRole.FUNCTION);           // MIN(x)
		registerIdentifierRole(Expression.MOD,                   IdentifierRole.FUNCTION);           // MOD(x, y)
		registerIdentifierRole(Expression.NEW,                   IdentifierRole.FUNCTION);           // NEW x (y {, z}*)
		registerIdentifierRole(Expression.NOT,                   IdentifierRole.COMPLETEMENT);
		registerIdentifierRole(Expression.NULL,                  IdentifierRole.FUNCTION);
		registerIdentifierRole(Expression.OBJECT,                IdentifierRole.FUNCTION);           // OBJECT(x)
		registerIdentifierRole(Expression.OF,                    IdentifierRole.COMPOUND_FUNCTION);  // Part of MEMBER [OF]
		registerIdentifierRole(Expression.OR,                    IdentifierRole.AGGREGATE);          // x OR y
		registerIdentifierRole(Expression.OUTER,                 IdentifierRole.COMPLETEMENT);       // Part of JOIN
		registerIdentifierRole(Expression.POSITION,              IdentifierRole.UNUSED);
		registerIdentifierRole(Expression.SELECT,                IdentifierRole.CLAUSE);
		registerIdentifierRole(Expression.SET,                   IdentifierRole.CLAUSE);
		registerIdentifierRole(Expression.SIZE,                  IdentifierRole.FUNCTION);           // SIZE(x)
		registerIdentifierRole(Expression.SOME,                  IdentifierRole.FUNCTION);           // SOME(x)
		registerIdentifierRole(Expression.SQRT,                  IdentifierRole.FUNCTION);           // SQRT(x)
		registerIdentifierRole(Expression.SUBSTRING,             IdentifierRole.FUNCTION);           // SUBSTRING(x, y {, z})
		registerIdentifierRole(Expression.SUM,                   IdentifierRole.FUNCTION);
		registerIdentifierRole(Expression.TRAILING,              IdentifierRole.COMPLETEMENT);
		registerIdentifierRole(Expression.TRIM,                  IdentifierRole.FUNCTION);           // TRIM([[x [c] FROM] y)
		registerIdentifierRole(Expression.TRUE,                  IdentifierRole.FUNCTION);
		registerIdentifierRole(Expression.UNKNOWN,               IdentifierRole.UNUSED);
		registerIdentifierRole(Expression.UPDATE,                IdentifierRole.CLAUSE);
		registerIdentifierRole(Expression.UPPER,                 IdentifierRole.FUNCTION);           // UPPER(x)
		registerIdentifierRole(Expression.WHERE,                 IdentifierRole.CLAUSE);
		registerIdentifierRole(Expression.PLUS,                  IdentifierRole.AGGREGATE);
		registerIdentifierRole(Expression.MINUS,                 IdentifierRole.AGGREGATE);
		registerIdentifierRole(Expression.MULTIPLICATION,        IdentifierRole.AGGREGATE);
		registerIdentifierRole(Expression.DIVISION,              IdentifierRole.AGGREGATE);
		registerIdentifierRole(Expression.LOWER_THAN,            IdentifierRole.AGGREGATE);
		registerIdentifierRole(Expression.LOWER_THAN_OR_EQUAL,   IdentifierRole.AGGREGATE);
		registerIdentifierRole(Expression.GREATER_THAN,          IdentifierRole.AGGREGATE);
		registerIdentifierRole(Expression.GREATER_THAN_OR_EQUAL, IdentifierRole.AGGREGATE);
		registerIdentifierRole(Expression.DIFFERENT,             IdentifierRole.AGGREGATE);
		registerIdentifierRole(Expression.EQUAL,                 IdentifierRole.AGGREGATE);

		// Composite Identifiers
		registerIdentifierRole(Expression.GROUP_BY,              IdentifierRole.CLAUSE);
		registerIdentifierRole(Expression.LEFT_JOIN,             IdentifierRole.COMPOUND_FUNCTION);
		registerIdentifierRole(Expression.LEFT_JOIN_FETCH,       IdentifierRole.COMPOUND_FUNCTION);
		registerIdentifierRole(Expression.LEFT_OUTER_JOIN,       IdentifierRole.COMPOUND_FUNCTION);
		registerIdentifierRole(Expression.LEFT_OUTER_JOIN_FETCH, IdentifierRole.COMPOUND_FUNCTION);
		registerIdentifierRole(Expression.INNER_JOIN,            IdentifierRole.COMPOUND_FUNCTION);
		registerIdentifierRole(Expression.INNER_JOIN_FETCH,      IdentifierRole.COMPOUND_FUNCTION);
		registerIdentifierRole(Expression.IS_EMPTY,              IdentifierRole.COMPOUND_FUNCTION);
		registerIdentifierRole(Expression.IS_NOT_EMPTY,          IdentifierRole.COMPOUND_FUNCTION);
		registerIdentifierRole(Expression.IS_NOT_NULL,           IdentifierRole.COMPOUND_FUNCTION);
		registerIdentifierRole(Expression.IS_NULL,               IdentifierRole.COMPOUND_FUNCTION);
		registerIdentifierRole(Expression.JOIN_FETCH,            IdentifierRole.COMPOUND_FUNCTION);
		registerIdentifierRole(Expression.MEMBER_OF,             IdentifierRole.COMPOUND_FUNCTION);  // x NOT MEMBER OF y
		registerIdentifierRole(Expression.NOT_BETWEEN,           IdentifierRole.COMPOUND_FUNCTION);  // x NOT BETWEEN y AND z
		registerIdentifierRole(Expression.NOT_EXISTS,            IdentifierRole.FUNCTION);           // NOT EXISTS(x)
		registerIdentifierRole(Expression.NOT_IN,                IdentifierRole.COMPOUND_FUNCTION);  // x NOT IN { (y {, z}* | (s) | t }
		registerIdentifierRole(Expression.NOT_LIKE,              IdentifierRole.COMPOUND_FUNCTION);  // x NOT LIKE y
		registerIdentifierRole(Expression.NOT_MEMBER,            IdentifierRole.COMPOUND_FUNCTION);  // x NOT MEMBER y
		registerIdentifierRole(Expression.NOT_MEMBER_OF,         IdentifierRole.COMPOUND_FUNCTION);  // x NOT MEMBER OF y
		registerIdentifierRole(Expression.ORDER_BY,              IdentifierRole.CLAUSE);

		// Partial Identifiers
		registerIdentifierRole("BY",                             IdentifierRole.CLAUSE);             // Part of GROUP BY, ORDER BY
		registerIdentifierRole("DELETE",                         IdentifierRole.CLAUSE);             // Part of DELETE FROM
		registerIdentifierRole("GROUP",                          IdentifierRole.CLAUSE);             // Part of GROUP BY
		registerIdentifierRole("ORDER",                          IdentifierRole.CLAUSE);             // Part of ORDERY BY
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "JPQLGrammar 1.0";
	}
}