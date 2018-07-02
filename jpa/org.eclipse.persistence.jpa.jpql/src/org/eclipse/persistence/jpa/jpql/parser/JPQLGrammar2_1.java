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
 * <p>This {@link JPQLGrammar} provides support for parsing JPQL queries defined in
 * <a href="http://jcp.org/en/jsr/detail?id=317">JSR-338 - Java Persistence 2.1</a>.</p>
 *
 * The following is the JPQL grammar defined in JPA version 2.1.
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
 * range_variable_declaration ::= entity_name [AS] identification_variable
 *
 * join ::= join_spec join_association_path_expression [AS] identification_variable [join_condition]
 *
 * fetch_join ::= join_spec FETCH join_association_path_expression [join_condition]
 *
 * join_spec ::= [ LEFT [OUTER] | INNER ] JOIN
 *
 * join_condition ::= ON conditional_expression
 *
 * join_association_path_expression ::= join_collection_valued_path_expression |
 *                                      join_single_valued_path_expression |
 *                                      TREAT(join_collection_valued_path_expression AS subtype) |
 *                                      TREAT(join_single_valued_path_expression AS subtype)
 *
 * join_collection_valued_path_expression ::= identification_variable.{single_valued_embeddable_object_field.}*collection_valued_field
 *
 * join_single_valued_path_expression ::= identification_variable.{single_valued_embeddable_object_field.}*single_valued_object_field
 *
 * collection_member_declaration ::= IN (collection_valued_path_expression) [AS] identification_variable
 *
 * qualified_identification_variable ::= composable_qualified_identification_variable |
 *                                       ENTRY(identification_variable)
 *
 * composable_qualified_identification_variable ::= KEY(identification_variable) |
 *                                                  VALUE(identification_variable)
 *
 * single_valued_path_expression ::= qualified_identification_variable |
 *                                   TREAT(qualified_identification_variable AS subtype) |
 *                                   state_field_path_expression |
 *                                   single_valued_object_path_expression
 *
 * general_identification_variable ::= identification_variable |
 *                                     composable_qualified_identification_variable
 *
 * general_subpath ::= simple_subpath | treated_subpath{.single_valued_object_field}*
 *
 * simple_subpath ::= general_identification_variable |
 *                    general_identification_variable{.single_valued_object_field}*
 *
 * treated_subpath ::= TREAT(general_subpath AS subtype)
 *
 * state_field_path_expression ::= general_subpath.state_field
 *
 * single_valued_object_path_expression ::= general_subpath.single_valued_object_field
 *
 * collection_valued_path_expression ::= general_subpath.{collection_valued_field}
 *
 * update_clause ::= UPDATE entity_name [[AS] identification_variable] SET update_item {, update_item}*
 *
 * update_item ::= [identification_variable.]{single_valued_embeddable_object_field.}*{state_field | single_valued_object_field} = new_value
 *
 * new_value ::= scalar_expression |
 *               simple_entity_expression |
 *               NULL
 *
 * delete_clause ::= DELETE FROM entity_name [[AS] identification_variable]
 *
 * select_clause ::= SELECT [DISTINCT] select_item {, select_item}*
 *
 * select_item ::= select_expression [[AS] result_variable]
 *
 * select_expression ::= single_valued_path_expression |
 *                       scalar_expression |
 *                       aggregate_expression |
 *                       identification_variable |
 *                       OBJECT(identification_variable) |
 *                       constructor_expression
 *
 * constructor_expression ::= NEW constructor_name ( constructor_item {, constructor_item}* )
 *
 * constructor_item ::= single_valued_path_expression |
 *                      scalar_expression |
 *                      aggregate_expression |
 *                      identification_variable
 *
 * aggregate_expression ::= { AVG | MAX | MIN | SUM } ([DISTINCT] state_field_path_expression) |
 *                          COUNT ([DISTINCT] identification_variable |
 *                                            state_field_path_expression |
 *                                            single_valued_object_path_expression) |
 *                          function_invocation
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
 * orderby_item ::= state_field_path_expression | result_variable [ ASC | DESC ]
 *
 * subquery ::= simple_select_clause subquery_from_clause [where_clause] [groupby_clause] [having_clause]
 *
 * subquery_from_clause ::= FROM subselect_identification_variable_declaration
 *                               {, subselect_identification_variable_declaration | collection_member_declaration}*
 *
 * subselect_identification_variable_declaration ::= identification_variable_declaration |
 *                                                   derived_path_expression [AS] identification_variable {join}* |
 *                                                   derived_collection_member_declaration
 *
 * derived_path_expression ::= general_derived_path.single_valued_object_field |
 *                             general_derived_path.collection_valued_field
 *
 * general_derived_path ::= simple_derived_path |
 *                          treated_derived_path{.single_valued_object_field}*
 *
 * simple_derived_path ::= superquery_identification_variable{.single_valued_object_field}*
 *
 * treated_derived_path ::= TREAT(general_derived_path AS subtype)
 *
 * derived_collection_member_declaration ::= IN superquery_identification_variable.{single_valued_object_field.}*collection_valued_field
 *
 * simple_select_clause ::= SELECT [DISTINCT] simple_select_expression
 *
 * simple_select_expression::= single_valued_path_expression |
 *                             scalar_expression |
 *                             aggregate_expression |
 *                             identification_variable
 *
 * scalar_expression ::= arithmetic_expression |
 *                       string_expression |
 *                       enum_expression |
 *                       datetime_expression |
 *                       boolean_expression |
 *                       case_expression |
 *                       entity_type_expression
 *
 * conditional_expression ::= conditional_term | conditional_expression OR conditional_term
 *
 * conditional_term ::= conditional_factor | conditional_term AND conditional_factor
 *
 * conditional_factor ::= [NOT] conditional_primary
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
 * in_expression ::= {state_field_path_expression | type_discriminator} [NOT] IN { ( in_item {, in_item}* ) | (subquery) | collection_valued_input_parameter }
 *
 * in_item ::= literal | single_valued_input_parameter
 *
 * like_expression ::= string_expression [NOT] LIKE pattern_value [ESCAPE escape_character]
 *
 * escape_character ::= single_character_string_literal | character_valued_input_parameter
 *
 * null_comparison_expression ::= {single_valued_path_expression | input_parameter} IS [NOT] NULL
 *
 * empty_collection_comparison_expression ::= collection_valued_path_expression IS [NOT] EMPTY
 *
 * collection_member_expression ::= entity_or_value_expression [NOT] MEMBER [OF] collection_valued_path_expression
 *
 * entity_or_value_expression ::= single_valued_object_path_expression |
 *                                state_field_path_expression |
 *                                simple_entity_or_value_expression
 *
 * simple_entity_or_value_expression ::= identification_variable |
 *                                       input_parameter |
 *                                       literal
 *
 * exists_expression ::= [NOT] EXISTS (subquery)
 *
 * all_or_any_expression ::= { ALL | ANY | SOME} (subquery)
 *
 * comparison_expression ::= string_expression comparison_operator {string_expression | all_or_any_expression} |
 *                           boolean_expression { = | {@literal <>} } {boolean_expression | all_or_any_expression} |
 *                           enum_expression { = | {@literal <>} } {enum_expression | all_or_any_expression} |
 *                           datetime_expression comparison_operator {datetime_expression | all_or_any_expression} |
 *                           entity_expression { = | {@literal <>} } {entity_expression | all_or_any_expression} |
 *                           arithmetic_expression comparison_operator {arithmetic_expression | all_or_any_expression} |
 *                           entity_type_expression { = | {@literal <>} } entity_type_expression}
 *
 * comparison_operator ::= = | {@literal > | >= | < | <= | <>}
 *
 * arithmetic_expression ::= arithmetic_term | arithmetic_expression { + | - } arithmetic_term
 *
 * arithmetic_term ::= arithmetic_factor | arithmetic_term { * | / } arithmetic_factor
 *
 * arithmetic_factor ::= [{ + | - }] arithmetic_primary
 *
 * arithmetic_primary ::= state_field_path_expression |
 *                        numeric_literal |
 *                        (arithmetic_expression) |
 *                        input_parameter |
 *                        functions_returning_numerics |
 *                        aggregate_expression |
 *                        case_expression |
 *                        function_invocation |
 *                        (subquery)
 *
 * string_expression ::= state_field_path_expression |
 *                       string_literal |
 *                       input_parameter |
 *                       functions_returning_strings |
 *                       aggregate_expression |
 *                       case_expression |
 *                       function_invocation |
 *                       (subquery)
 *
 * datetime_expression ::= state_field_path_expression |
 *                         input_parameter |
 *                         functions_returning_datetime |
 *                         aggregate_expression |
 *                         case_expression |
 *                         function_invocation |
 *                         date_time_timestamp_literal |
 *                         (subquery)
 *
 * boolean_expression ::= state_field_path_expression |
 *                        boolean_literal |
 *                        input_parameter |
 *                        case_expression |
 *                        function_invocation |
 *                        (subquery)
 *
 * enum_expression ::= state_field_path_expression |
 *                     enum_literal |
 *                     input_parameter |
 *                     case_expression |
 *                     (subquery)
 *
 * entity_expression ::= single_valued_object_path_expression | simple_entity_expression
 *
 * simple_entity_expression ::= identification_variable |
 *                              input_parameter
 *
 * entity_type_expression ::= type_discriminator |
 *                            entity_type_literal |
 *                            input_parameter
 *
 * type_discriminator ::= TYPE(identification_variable | single_valued_object_path_expression | input_parameter)
 *
 * functions_returning_numerics ::= LENGTH(string_expression) |
 *                                  LOCATE(string_expression, string_expression[, arithmetic_expression]) |
 *                                  ABS(arithmetic_expression) |
 *                                  SQRT(arithmetic_expression) |
 *                                  MOD(arithmetic_expression, arithmetic_expression) |
 *                                  SIZE(collection_valued_path_expression) |
 *                                  INDEX(identification_variable)
 *
 * functions_returning_datetime ::= CURRENT_DATE | CURRENT_TIME | CURRENT_TIMESTAMP
 *
 * functions_returning_strings ::= CONCAT(string_expression, string_expression {, string_expression}*) |
 *                                 SUBSTRING(string_expression, arithmetic_expression [, arithmetic_expression]) |
 *                                 TRIM([[trim_specification] [trim_character] FROM] string_expression) |
 *                                 LOWER(string_expression) |
 *                                 UPPER(string_expression)
 *
 * trim_specification ::= LEADING | TRAILING | BOTH
 *
 * function_invocation ::= FUNCTION(string_literal {, function_arg}*)
 *
 * function_arg ::= literal |
 *                  state_field_path_expression |
 *                  input_parameter |
 *                  scalar_expression
 *
 * case_expression ::= general_case_expression |
 *                     simple_case_expression |
 *                     coalesce_expression |
 *                     nullif_expression
 *
 * general_case_expression ::= CASE when_clause {when_clause}* ELSE scalar_expression END
 *
 * when_clause ::= WHEN conditional_expression THEN scalar_expression
 *
 * simple_case_expression ::= CASE case_operand simple_when_clause {simple_when_clause}* ELSE scalar_expression END
 *
 * case_operand ::= state_field_path_expression | type_discriminator
 *
 * simple_when_clause ::= WHEN scalar_expression THEN scalar_expression
 *
 * coalesce_expression ::= COALESCE(scalar_expression {, scalar_expression}+)
 *
 * nullif_expression ::= NULLIF(scalar_expression, scalar_expression)
 *
 * literal ::= boolean_literal |
 *             enum_literal
 *             numeric_literal |
 *             string_literal |
 *
 * boolean_literal ::= TRUE | FALSE
 *
 * string_literal ::= '''{identifier}*'''
 *
 * enum_literal ::= {identifier'.'}+identifier
 *
 * identifier ::= Character.isJavaIdentifierStart(char){Character.isJavaIdentifierPart(char)}*
 *
 * literal_temporal ::= date_literal | time_literal | timestamp_literal
 *
 * input_parameter ::= ':'{identifier}+ | '?'{'1'-'9'}{'0'-'9'}*
 *
 * date_literal ::= '{' ''d'' (' ' | '\t')+ '\'' date_string '\'' (' ' | '\t')* '}'
 *
 * time_literal ::= '{' ''t'' (' ' | '\t')+ '\'' trim_string '\'' (' ' | '\t')* '}'
 *
 * timestamp_literal ::= '{' ('ts') (' ' | '\t')+ '\'' date_string ' ' trim_string '\'' (' ' | '\t')* '}'
 *
 * date_string ::= [0-9] [0-9] [0-9] [0-9] '-' [0-9] [0-9] '-' [0-9] [0-9]
 *
 * trim_string ::= [0-9] ([0-9])? ':' [0-9] [0-9] ':' [0-9] [0-9] '.' [0-9]*</code></pre>
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
public final class JPQLGrammar2_1 extends AbstractJPQLGrammar {

    /**
     * The singleton instance of this {@link JPQLGrammar2_1}.
     */
    private static final JPQLGrammar INSTANCE = new JPQLGrammar2_1();

    /**
     * Creates a new <code>JPQLExtension2_1</code>.
     */
    public JPQLGrammar2_1() {
        super();
    }

    /**
     * Creates a new <code>JPQLGrammar2_1</code>.
     *
     * @param jpqlGrammar The {@link JPQLGrammar} to extend with the content of this one without
     * instantiating the base {@link JPQLGrammar}
     */
    private JPQLGrammar2_1(AbstractJPQLGrammar jpqlGrammar) {
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
        new JPQLGrammar2_1(jpqlGrammar);
    }

    /**
     * Returns the singleton instance of the default implementation of {@link JPQLGrammar} which
     * provides support for the JPQL grammar defined in the JPA 2.1 functional specification.
     *
     * @return The {@link JPQLGrammar} that only has support for JPA 2.1
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
        return JPAVersion.VERSION_2_1;
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

        registerBNF(new FunctionExpressionBNF());
        registerBNF(new FunctionItemBNF());
        registerBNF(new OnClauseBNF());
        registerBNF(new TreatExpressionBNF());

        // Extend the query BNF to add support for TREAT
        addChildBNF(CollectionValuedPathExpressionBNF.ID, TreatExpressionBNF.ID);
        addChildBNF(JoinAssociationPathExpressionBNF.ID,  TreatExpressionBNF.ID);
        addChildBNF(SingleValuedPathExpressionBNF.ID,     TreatExpressionBNF.ID);
        addChildBNF(StateFieldPathExpressionBNF.ID,       TreatExpressionBNF.ID);

        // Extends the query BNF to add support for FUNCTION
        addChildBNF(AggregateExpressionBNF.ID, FunctionExpressionBNF.ID);

        // string_primary becomes string_expression in CONCAT
        addChildBNF(InternalConcatExpressionBNF.ID, StringExpressionBNF.ID);

        // string_primary becomes string_expression, simply add the new query BNFs
        addChildBNF(StringPrimaryBNF.ID,    FunctionExpressionBNF.ID);
        addChildBNF(StringPrimaryBNF.ID,    SubqueryBNF.ID);
        addChildBNF(StringExpressionBNF.ID, FunctionExpressionBNF.ID);
        addChildBNF(StringExpressionBNF.ID, SubqueryBNF.ID);

        // datetime_primary becomes datetime_expression, simply add the new query BNFs
        addChildBNF(DateTimePrimaryBNF.ID, FunctionExpressionBNF.ID);
        addChildBNF(DateTimePrimaryBNF.ID, SubqueryBNF.ID);

        // boolean_primary becomes boolean_expression, simply add the new query BNFs
        addChildBNF(BooleanPrimaryBNF.ID, FunctionExpressionBNF.ID);
        addChildBNF(BooleanPrimaryBNF.ID, SubqueryBNF.ID);

        // enum_primary becomes enum_expression, simply add the new query BNFs
        addChildBNF(EnumLiteralBNF.ID, SubqueryBNF.ID);

        // arithmetic_primary becomes arithmetic_expression, simply add the new query BNFs
        addChildBNF(ArithmeticPrimaryBNF.ID, FunctionExpressionBNF.ID);
        addChildBNF(ArithmeticPrimaryBNF.ID, SubqueryBNF.ID);

        // datetime_expression
        addChildBNF(DatetimeExpressionBNF.ID, FunctionExpressionBNF.ID);
        addChildBNF(DatetimeExpressionBNF.ID, SubqueryBNF.ID);

        // boolean_expression
        addChildBNF(BooleanExpressionBNF.ID, FunctionExpressionBNF.ID);
        addChildBNF(BooleanExpressionBNF.ID, SubqueryBNF.ID);

        // enum_expression
        addChildBNF(EnumExpressionBNF.ID, SubqueryBNF.ID);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initializeExpressionFactories() {

        registerFactory(new FunctionExpressionFactory(FunctionExpressionFactory.ID, FUNCTION));
        registerFactory(new OnClauseFactory());
        registerFactory(new TreatExpressionFactory());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initializeIdentifiers() {

        registerIdentifierRole(FUNCTION,    IdentifierRole.FUNCTION); // FUNCTION(n, x1, ..., x2)
        registerIdentifierRole(ON,          IdentifierRole.CLAUSE);   // ON x
        registerIdentifierRole(TREAT,       IdentifierRole.FUNCTION); // TREAT(x AS y)

        registerIdentifierVersion(FUNCTION, JPAVersion.VERSION_2_1);
        registerIdentifierVersion(ON,       JPAVersion.VERSION_2_1);
        registerIdentifierVersion(TREAT,    JPAVersion.VERSION_2_1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "JPQLGrammar 2.1";
    }
}
