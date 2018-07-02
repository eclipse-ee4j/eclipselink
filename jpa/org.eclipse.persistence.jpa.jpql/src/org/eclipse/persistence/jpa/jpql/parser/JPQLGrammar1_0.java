/*
 * Copyright (c) 2006, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation
//
package org.eclipse.persistence.jpa.jpql.parser;

import org.eclipse.persistence.jpa.jpql.ExpressionTools;
import org.eclipse.persistence.jpa.jpql.JPAVersion;
import static org.eclipse.persistence.jpa.jpql.parser.Expression.*;

/**
 * <p>This {@link JPQLGrammar} provides support for parsing JPQL queries defined in <a
 * href="http://jcp.org/en/jsr/detail?id=220">JSR-220 - Enterprise JavaBeans 3.0</a>.</p>
 *
 * The following is the JPQL grammar defined in JPA version 1.0.
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
 *                           boolean_expression { = | {@literal <>} } {boolean_expression | all_or_any_expression} |
 *                           enum_expression { = | {@literal <>} } {enum_expression | all_or_any_expression} |
 *                           datetime_expression comparison_operator {datetime_expression | all_or_any_expression} |
 *                           entity_expression { = | {@literal <>} } {entity_expression | all_or_any_expression} |
 *                           arithmetic_expression comparison_operator {arithmetic_expression | all_or_any_expression}
 *
 * comparison_operator ::= = | {@literal > | >= | < | <= | <>}
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
 * TIME_STRING ::= [0-9] ([0-9])? ':' [0-9] [0-9] ':' [0-9] [0-9] '.' [0-9]*</code></pre>
 *
 * <p>Provisional API: This interface is part of an interim API that is still under development and
 * expected to change significantly before reaching stability. It is available at this early stage
 * to solicit feedback from pioneering adopters on the understanding that any code that uses this
 * API will almost certainly be broken (repeatedly) as the API evolves.</p>
 *
 * @version 2.5
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
    public String getProvider() {
        return DefaultJPQLGrammar.PROVIDER_NAME;
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
        registerFactory(new ObjectExpressionFactory());
        registerFactory(new OrderByClauseFactory());
        registerFactory(new OrderByItemFactory());
        registerFactory(new OrExpressionFactory());
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

        registerIdentifierRole(ABS,                   IdentifierRole.FUNCTION);           // ABS(x)
        registerIdentifierRole(ALL,                   IdentifierRole.FUNCTION);           // ALL(x)
        registerIdentifierRole(AND,                   IdentifierRole.AGGREGATE);          // x AND y
        registerIdentifierRole(ANY,                   IdentifierRole.FUNCTION);           // ANY(x)
        registerIdentifierRole(AS,                    IdentifierRole.COMPLEMENT);
        registerIdentifierRole(ASC,                   IdentifierRole.COMPLEMENT);
        registerIdentifierRole(AVG,                   IdentifierRole.FUNCTION);           // AVG(x)
        registerIdentifierRole(BETWEEN,               IdentifierRole.COMPOUND_FUNCTION);  // x BETWEEN y AND z
        registerIdentifierRole(BIT_LENGTH,            IdentifierRole.UNUSED);
        registerIdentifierRole(BOTH,                  IdentifierRole.COMPLEMENT);
        registerIdentifierRole(CHAR_LENGTH,           IdentifierRole.UNUSED);
        registerIdentifierRole(CHARACTER_LENGTH,      IdentifierRole.UNUSED);
        registerIdentifierRole(CLASS,                 IdentifierRole.UNUSED);
        registerIdentifierRole(CONCAT,                IdentifierRole.FUNCTION);           // CONCAT(x, y)
        registerIdentifierRole(COUNT,                 IdentifierRole.FUNCTION);           // COUNT(x)
        registerIdentifierRole(CURRENT_DATE,          IdentifierRole.FUNCTION);
        registerIdentifierRole(CURRENT_TIME,          IdentifierRole.FUNCTION);
        registerIdentifierRole(CURRENT_TIMESTAMP,     IdentifierRole.FUNCTION);
        registerIdentifierRole(DELETE,                IdentifierRole.CLAUSE);
        registerIdentifierRole(DELETE_FROM,           IdentifierRole.CLAUSE);
        registerIdentifierRole(DESC,                  IdentifierRole.COMPLEMENT);
        registerIdentifierRole(DISTINCT,              IdentifierRole.COMPLEMENT);
        registerIdentifierRole(EMPTY,                 IdentifierRole.COMPOUND_FUNCTION);  // x IS EMPTY
        registerIdentifierRole(ESCAPE,                IdentifierRole.COMPLEMENT);
        registerIdentifierRole(EXISTS,                IdentifierRole.FUNCTION);           // EXISTS(x)
        registerIdentifierRole(FALSE,                 IdentifierRole.FUNCTION);
        registerIdentifierRole(FETCH,                 IdentifierRole.COMPOUND_FUNCTION);
        registerIdentifierRole(FROM,                  IdentifierRole.CLAUSE);
        registerIdentifierRole(HAVING,                IdentifierRole.CLAUSE);
        registerIdentifierRole(IN,                    IdentifierRole.COMPOUND_FUNCTION);  // x IN { (y {, z}* | (s) | t }
        registerIdentifierRole(INNER,                 IdentifierRole.COMPOUND_FUNCTION);  // Part of JOIN
        registerIdentifierRole(IS,                    IdentifierRole.COMPOUND_FUNCTION);
        registerIdentifierRole(JOIN,                  IdentifierRole.COMPOUND_FUNCTION);
        registerIdentifierRole(LEADING,               IdentifierRole.COMPLEMENT);
        registerIdentifierRole(LEFT,                  IdentifierRole.COMPLEMENT);
        registerIdentifierRole(LENGTH,                IdentifierRole.FUNCTION);           // LENGTH(x)
        registerIdentifierRole(LIKE,                  IdentifierRole.COMPOUND_FUNCTION);  // x LIKE y [ESCAPE z]
        registerIdentifierRole(LOCATE,                IdentifierRole.FUNCTION);           // LOCATE(x, y [, z]))
        registerIdentifierRole(LOWER,                 IdentifierRole.FUNCTION);           // LOWER(x)
        registerIdentifierRole(MAX,                   IdentifierRole.FUNCTION);           // MAX(x)
        registerIdentifierRole(MEMBER,                IdentifierRole.COMPOUND_FUNCTION);  // x MEMBER y
        registerIdentifierRole(MIN,                   IdentifierRole.FUNCTION);           // MIN(x)
        registerIdentifierRole(MOD,                   IdentifierRole.FUNCTION);           // MOD(x, y)
        registerIdentifierRole(NEW,                   IdentifierRole.FUNCTION);           // NEW x (y {, z}*)
        registerIdentifierRole(NOT,                   IdentifierRole.COMPLEMENT);
        registerIdentifierRole(NULL,                  IdentifierRole.FUNCTION);
        registerIdentifierRole(OBJECT,                IdentifierRole.FUNCTION);           // OBJECT(x)
        registerIdentifierRole(OF,                    IdentifierRole.COMPOUND_FUNCTION);  // Part of MEMBER [OF]
        registerIdentifierRole(OR,                    IdentifierRole.AGGREGATE);          // x OR y
        registerIdentifierRole(OUTER,                 IdentifierRole.COMPLEMENT);       // Part of JOIN
        registerIdentifierRole(POSITION,              IdentifierRole.UNUSED);
        registerIdentifierRole(SELECT,                IdentifierRole.CLAUSE);
        registerIdentifierRole(SET,                   IdentifierRole.CLAUSE);
        registerIdentifierRole(SIZE,                  IdentifierRole.FUNCTION);           // SIZE(x)
        registerIdentifierRole(SOME,                  IdentifierRole.FUNCTION);           // SOME(x)
        registerIdentifierRole(SQRT,                  IdentifierRole.FUNCTION);           // SQRT(x)
        registerIdentifierRole(SUBSTRING,             IdentifierRole.FUNCTION);           // SUBSTRING(x, y {, z})
        registerIdentifierRole(SUM,                   IdentifierRole.FUNCTION);
        registerIdentifierRole(TRAILING,              IdentifierRole.COMPLEMENT);
        registerIdentifierRole(TRIM,                  IdentifierRole.FUNCTION);           // TRIM([[x [c] FROM] y)
        registerIdentifierRole(TRUE,                  IdentifierRole.FUNCTION);
        registerIdentifierRole(UNKNOWN,               IdentifierRole.UNUSED);
        registerIdentifierRole(UPDATE,                IdentifierRole.CLAUSE);
        registerIdentifierRole(UPPER,                 IdentifierRole.FUNCTION);           // UPPER(x)
        registerIdentifierRole(WHERE,                 IdentifierRole.CLAUSE);
        registerIdentifierRole(PLUS,                  IdentifierRole.AGGREGATE);          // x + y
        registerIdentifierRole(MINUS,                 IdentifierRole.AGGREGATE);          // x - y
        registerIdentifierRole(MULTIPLICATION,        IdentifierRole.AGGREGATE);          // x * y
        registerIdentifierRole(DIVISION,              IdentifierRole.AGGREGATE);          // x / y
        registerIdentifierRole(LOWER_THAN,            IdentifierRole.AGGREGATE);          // x < y
        registerIdentifierRole(LOWER_THAN_OR_EQUAL,   IdentifierRole.AGGREGATE);          // x <= y
        registerIdentifierRole(GREATER_THAN,          IdentifierRole.AGGREGATE);          // x >  y
        registerIdentifierRole(GREATER_THAN_OR_EQUAL, IdentifierRole.AGGREGATE);          // x >= y
        registerIdentifierRole(DIFFERENT,             IdentifierRole.AGGREGATE);          // x <> y
        registerIdentifierRole(EQUAL,                 IdentifierRole.AGGREGATE);          // x =  y

        // Composite Identifiers
        registerIdentifierRole(GROUP_BY,              IdentifierRole.CLAUSE);
        registerIdentifierRole(LEFT_JOIN,             IdentifierRole.COMPOUND_FUNCTION);
        registerIdentifierRole(LEFT_JOIN_FETCH,       IdentifierRole.COMPOUND_FUNCTION);
        registerIdentifierRole(LEFT_OUTER_JOIN,       IdentifierRole.COMPOUND_FUNCTION);
        registerIdentifierRole(LEFT_OUTER_JOIN_FETCH, IdentifierRole.COMPOUND_FUNCTION);
        registerIdentifierRole(INNER_JOIN,            IdentifierRole.COMPOUND_FUNCTION);
        registerIdentifierRole(INNER_JOIN_FETCH,      IdentifierRole.COMPOUND_FUNCTION);
        registerIdentifierRole(IS_EMPTY,              IdentifierRole.COMPOUND_FUNCTION);
        registerIdentifierRole(IS_NOT_EMPTY,          IdentifierRole.COMPOUND_FUNCTION);
        registerIdentifierRole(IS_NOT_NULL,           IdentifierRole.COMPOUND_FUNCTION);
        registerIdentifierRole(IS_NULL,               IdentifierRole.COMPOUND_FUNCTION);
        registerIdentifierRole(JOIN_FETCH,            IdentifierRole.COMPOUND_FUNCTION);
        registerIdentifierRole(MEMBER_OF,             IdentifierRole.COMPOUND_FUNCTION);  // x NOT MEMBER OF y
        registerIdentifierRole(NOT_BETWEEN,           IdentifierRole.COMPOUND_FUNCTION);  // x NOT BETWEEN y AND z
        registerIdentifierRole(NOT_EXISTS,            IdentifierRole.FUNCTION);           // NOT EXISTS(x)
        registerIdentifierRole(NOT_IN,                IdentifierRole.COMPOUND_FUNCTION);  // x NOT IN { (y {, z}* | (s) | t }
        registerIdentifierRole(NOT_LIKE,              IdentifierRole.COMPOUND_FUNCTION);  // x NOT LIKE y
        registerIdentifierRole(NOT_MEMBER,            IdentifierRole.COMPOUND_FUNCTION);  // x NOT MEMBER y
        registerIdentifierRole(NOT_MEMBER_OF,         IdentifierRole.COMPOUND_FUNCTION);  // x NOT MEMBER OF y
        registerIdentifierRole(ORDER_BY,              IdentifierRole.CLAUSE);

        // Partial Identifiers
        registerIdentifierRole("BY",                  IdentifierRole.CLAUSE);             // Part of GROUP BY, ORDER BY
        registerIdentifierRole("DELETE",              IdentifierRole.CLAUSE);             // Part of DELETE FROM
        registerIdentifierRole("GROUP",               IdentifierRole.CLAUSE);             // Part of GROUP BY
        registerIdentifierRole("ORDER",               IdentifierRole.CLAUSE);             // Part of ORDERY BY
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "JPQLGrammar 1.0";
    }
}
