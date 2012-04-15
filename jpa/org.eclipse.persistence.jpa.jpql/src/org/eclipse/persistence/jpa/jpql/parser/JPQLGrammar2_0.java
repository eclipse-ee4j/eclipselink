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
 * href="http://jcp.org/en/jsr/detail?id=317">JSR-337 - Java Persistence 2.0</a>.
 * <p>
 * The following is the BNF for the JPQL query version 2.0.
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
 * join ::= join_spec join_association_path_expression [AS] identification_variable
 *
 * fetch_join ::= join_spec FETCH join_association_path_expression
 *
 * join_spec ::= [ LEFT [OUTER] | INNER ] JOIN
 *
 * join_association_path_expression ::= join_collection_valued_path_expression |
 *                                      join_single_valued_path_expression
 *
 * join_collection_valued_path_expression ::= identification_variable.{single_valued_embeddable_object_field.}*collection_valued_field
 *
 * join_single_valued_path_expression ::= identification_variable.{single_valued_embeddable_object_field.}*single_valued_object_field
 *
 * collection_member_declaration ::= IN (collection_valued_path_expression) [AS] identification_variable
 *
 * qualified_identification_variable ::= KEY(identification_variable) |
 *                                       VALUE(identification_variable) |
 *                                       ENTRY(identification_variable)
 *
 * single_valued_path_expression ::= qualified_identification_variable |
 *                                   state_field_path_expression |
 *                                   single_valued_object_path_expression
 *
 * general_identification_variable ::= identification_variable |
 *                                     KEY(identification_variable) |
 *                                     VALUE(identification_variable)
 *
 * state_field_path_expression ::= general_identification_variable.{single_valued_object_field.}*state_field
 *
 * single_valued_object_path_expression ::= general_identification_variable.{single_valued_object_field.}*single_valued_object_field
 *
 * collection_valued_path_expression ::= general_identification_variable.{single_valued_object_field.}*collection_valued_field
 *
 * update_clause ::= UPDATE entity_name [[AS] identification_variable] SET update_item {, update_item}*
 *
 * update_item ::= [identification_variable.]{state_field | single_valued_object_field} = new_value
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
 *                                            single_valued_object_path_expression)
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
 * derived_path_expression ::= superquery_identification_variable.{single_valued_object_field.}*collection_valued_field |
 *                             superquery_identification_variable.{single_valued_object_field.}*single_valued_object_field
 *
 * derived_collection_member_declaration ::= IN superquery_identification_variable.{single_valued_object_field.}*collection_valued_field
 *
 * simple_select_clause ::= SELECT [DISTINCT] simple_select_expression
 *
 * simple_select_expression ::= single_valued_path_expression |
 *                              scalar_expression |
 *                              aggregate_expression |
 *                              identification_variable
 *
 * scalar_expression ::= simple_arithmetic_expression |
 *                       string_primary |
 *                       enum_primary |
 *                       datetime_primary |
 *                       boolean_primary |
 *                       case_expression |
 *                       entity_type_expression
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
 *                           boolean_expression { = | <> } {boolean_expression | all_or_any_expression} |
 *                           enum_expression { = | <> } {enum_expression | all_or_any_expression} |
 *                           datetime_expression comparison_operator {datetime_expression | all_or_any_expression} |
 *                           entity_expression { = | <> } {entity_expression | all_or_any_expression} |
 *                           arithmetic_expression comparison_operator {arithmetic_expression | all_or_any_expression} |
 *                           entity_type_expression { = | <> } entity_type_expression}
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
 *                        aggregate_expression |
 *                        case_expression
 *
 * string_expression ::= string_primary | (subquery)
 *
 * string_primary ::= state_field_path_expression |
 *                    string_literal |
 *                    input_parameter |
 *                    functions_returning_strings |
 *                    aggregate_expression |
 *                    case_expression
 *
 * datetime_primary ::= state_field_path_expression |
 *                      input_parameter |
 *                      functions_returning_datetime |
 *                      aggregate_expression |
 *                      case_expression |
 *                      date_time_timestamp_literal
 *
 * datetime_expression ::= datetime_primary | (subquery)
 *
 * boolean_expression ::= boolean_primary | (subquery)
 *
 * boolean_primary ::= state_field_path_expression |
 *                     boolean_literal |
 *                     input_parameter |
 *                     case_expression
 *
 * enum_expression ::= enum_primary | (subquery)
 *
 * enum_primary ::= state_field_path_expression |
 *                  enum_literal |
 *                  input_parameter |
 *                  case_expression
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
 * functions_returning_numerics ::= LENGTH(string_primary) |
 *                                  LOCATE(string_primary, string_primary[, simple_arithmetic_expression]) |
 *                                  ABS(simple_arithmetic_expression) |
 *                                  SQRT(simple_arithmetic_expression) |
 *                                  MOD(simple_arithmetic_expression, simple_arithmetic_expression) |
 *                                  SIZE(collection_valued_path_expression) |
 *                                  INDEX(identification_variable)
 *
 * functions_returning_datetime ::= CURRENT_DATE | CURRENT_TIME | CURRENT_TIMESTAMP | literal_temporal
 *
 * functions_returning_strings ::= CONCAT(string_primary, string_primary {, string_primary}*) |
 *                                 SUBSTRING(string_primary, simple_arithmetic_expression [, simple_arithmetic_expression]) |
 *                                 TRIM([[trim_specification] [trim_character] FROM] string_primary) |
 *                                 LOWER(string_primary) |
 *                                 UPPER(string_primary)
 *
 * trim_specification ::= LEADING | TRAILING | BOTH
 *
 * trim_character ::= string_literal | input_parameter
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
 * trim_string ::= [0-9] ([0-9])? ':' [0-9] [0-9] ':' [0-9] [0-9] '.' [0-9]*
 * </pre></code>
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class JPQLGrammar2_0 extends AbstractJPQLGrammar {

	/**
	 * The singleton instance of this {@link JPQLGrammar2_0}.
	 */
	private static final JPQLGrammar INSTANCE = new JPQLGrammar2_0();

	/**
	 * Creates a new <code>JPQLExtension2_0</code>.
	 */
	public JPQLGrammar2_0() {
		super();
	}

	/**
	 * Creates a new <code>JPQLGrammar2_0</code>.
	 *
	 * @param jpqlGrammar The {@link JPQLGrammar} to extend with the content of this one without
	 * instantiating the base {@link JPQLGrammar}
	 */
	private JPQLGrammar2_0(AbstractJPQLGrammar jpqlGrammar) {
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
		new JPQLGrammar2_0(jpqlGrammar);
	}

	/**
	 * Returns the singleton instance of the default implementation of {@link JPQLGrammar} which
	 * provides support for the JPQL grammar defined in the JPA 2.0 functional specification.
	 *
	 * @return The {@link JPQLGrammar} that only has support for JPA 2.0
	 */
	public static JPQLGrammar instance() {
		return INSTANCE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected JPQLGrammar buildBaseGrammar() {
		return new JPQLGrammar1_0();
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
		return ExpressionTools.EMPTY_STRING;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initializeBNFs() {

		registerBNF(new CaseExpressionBNF());
		registerBNF(new CaseOperandBNF());
		registerBNF(new CoalesceExpressionBNF());
		registerBNF(new ElseExpressionBNF());
		registerBNF(new EntityTypeExpressionBNF());
		registerBNF(new EntityTypeLiteralBNF());
		registerBNF(new GeneralCaseExpressionBNF());
		registerBNF(new InternalCoalesceExpressionBNF());
		registerBNF(new InternalEntityTypeExpressionBNF());
		registerBNF(new InternalWhenClauseBNF());
		registerBNF(new NullIfExpressionBNF());
		registerBNF(new QualifiedIdentificationVariableBNF());
		registerBNF(new ResultVariableBNF());
		registerBNF(new SingleValuedObjectPathExpressionBNF());
		registerBNF(new TypeExpressionBNF());
		registerBNF(new WhenClauseBNF());

		// Extend some query BNFs
		addChildBNF(ArithmeticPrimaryBNF.ID,              CaseExpressionBNF.ID);
		addChildBNF(BooleanPrimaryBNF.ID,                 CaseExpressionBNF.ID);
		addChildBNF(CollectionValuedPathExpressionBNF.ID, GeneralIdentificationVariableBNF.ID);
		addChildBNF(ComparisonExpressionBNF.ID,           EntityTypeExpressionBNF.ID);
		addChildBNF(DateTimePrimaryBNF.ID,                CaseExpressionBNF.ID);
		addChildBNF(DateTimePrimaryBNF.ID,                DateTimeTimestampLiteralBNF.ID);
		addChildBNF(EntityExpressionBNF.ID,               SingleValuedObjectPathExpressionBNF.ID);
		addChildBNF(EnumPrimaryBNF.ID,                    CaseExpressionBNF.ID);
		addChildBNF(InExpressionExpressionBNF.ID,         TypeExpressionBNF.ID);
		addChildBNF(InternalCountBNF.ID,                  SingleValuedObjectPathExpressionBNF.ID);
		addChildBNF(LiteralBNF.ID,                        EntityTypeLiteralBNF.ID);
		addChildBNF(ScalarExpressionBNF.ID,               CaseExpressionBNF.ID);
		addChildBNF(ScalarExpressionBNF.ID,               EntityTypeExpressionBNF.ID);
		addChildBNF(SelectExpressionBNF.ID,               ScalarExpressionBNF.ID);
		addChildBNF(SingleValuedPathExpressionBNF.ID,     QualifiedIdentificationVariableBNF.ID);
		addChildBNF(SingleValuedPathExpressionBNF.ID,     SingleValuedObjectPathExpressionBNF.ID);
		addChildBNF(StateFieldPathExpressionBNF.ID,       GeneralIdentificationVariableBNF.ID);
		addChildBNF(StringPrimaryBNF.ID,                  CaseExpressionBNF.ID);

		// Override (internal) select_expression to add support for result variable
		registerBNF(new ResultVariableBNF());

		// Add support for entity type literal
		setFallbackExpressionFactoryId(InExpressionItemBNF.ID, EntityTypeLiteralFactory.ID);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initializeExpressionFactories() {

		registerFactory(new CaseExpressionFactory());
		registerFactory(new CoalesceExpressionFactory());
		registerFactory(new EntityTypeLiteralFactory());
		registerFactory(new EntryExpressionFactory());
		registerFactory(new KeyExpressionFactory());
		registerFactory(new IndexExpressionFactory());
		registerFactory(new NullIfExpressionFactory());
		registerFactory(new ResultVariableFactory());
		registerFactory(new TypeExpressionFactory());
		registerFactory(new ValueExpressionFactory());
		registerFactory(new WhenClauseFactory());

		addChildFactory(FunctionsReturningNumericsBNF.ID, IndexExpressionFactory.ID);
		addChildFactory(InExpressionItemBNF.ID,           EntityTypeLiteralFactory.ID);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initializeIdentifiers() {

		registerIdentifierRole(Expression.CASE,     IdentifierRole.FUNCTION);           // ???
		registerIdentifierRole(Expression.COALESCE, IdentifierRole.FUNCTION);           // COALLESCE(x {, y}+)
		registerIdentifierRole(Expression.ELSE,     IdentifierRole.COMPOUND_FUNCTION);
		registerIdentifierRole(Expression.END,      IdentifierRole.COMPLETEMENT);
		registerIdentifierRole(Expression.ENTRY,    IdentifierRole.FUNCTION);           // ENTRY(x)
		registerIdentifierRole(Expression.INDEX,    IdentifierRole.FUNCTION);           // INDEX(x)
		registerIdentifierRole(Expression.KEY,      IdentifierRole.FUNCTION);           // KEY(x)
		registerIdentifierRole(Expression.NULLIF,   IdentifierRole.FUNCTION);           // NULLIF(x, y)
		registerIdentifierRole(Expression.THEN,     IdentifierRole.COMPOUND_FUNCTION);
		registerIdentifierRole(Expression.TYPE,     IdentifierRole.FUNCTION);           // TYPE(x)
		registerIdentifierRole(Expression.VALUE,    IdentifierRole.FUNCTION);           // VALUE(x)
		registerIdentifierRole(Expression.WHEN,     IdentifierRole.COMPOUND_FUNCTION);  // Part of CASE WHEN ELSE END

		registerIdentifierVersion(Expression.CASE,     JPAVersion.VERSION_2_0);
		registerIdentifierVersion(Expression.COALESCE, JPAVersion.VERSION_2_0);
		registerIdentifierVersion(Expression.ELSE,     JPAVersion.VERSION_2_0);
		registerIdentifierVersion(Expression.END,      JPAVersion.VERSION_2_0);
		registerIdentifierVersion(Expression.ENTRY,    JPAVersion.VERSION_2_0);
		registerIdentifierVersion(Expression.INDEX,    JPAVersion.VERSION_2_0);
		registerIdentifierVersion(Expression.KEY,      JPAVersion.VERSION_2_0);
		registerIdentifierVersion(Expression.NULLIF,   JPAVersion.VERSION_2_0);
		registerIdentifierVersion(Expression.THEN,     JPAVersion.VERSION_2_0);
		registerIdentifierVersion(Expression.TYPE,     JPAVersion.VERSION_2_0);
		registerIdentifierVersion(Expression.VALUE,    JPAVersion.VERSION_2_0);
		registerIdentifierVersion(Expression.WHEN,     JPAVersion.VERSION_2_0);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "JPQLGrammar 2.0";
	}
}