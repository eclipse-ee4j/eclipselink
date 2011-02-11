/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available athttp://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle
 *
 ******************************************************************************/
package org.eclipse.persistence.utils.jpa.query.parser;

import java.util.Iterator;

/**
 * This is the root interface of the Java Persistence query parsed tree. The
 * parser supports the latest release of the JPQL functional specification,
 * which is <a href="http://jcp.org/en/jsr/detail?id=317">JSR 317: Java&trade; Persistence 2.0</a>
 * <p>
 * The BNF for the Java Persistence query language, version 2.0:
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
 * join_spec::= [ LEFT [OUTER] | INNER ] JOIN
 *
 * join_association_path_expression ::= join_collection_valued_path_expression |
 *                                      join_single_valued_path_expression
 *
 * join_collection_valued_path_expression::= identification_variable.{single_valued_embeddable_object_field.}*collection_valued_field
 *
 * join_single_valued_path_expression::= identification_variable.{single_valued_embeddable_object_field.}*single_valued_object_field
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
 * single_valued_object_path_expression ::= general_identification_variable.{single_valued_object_field.}* single_valued_object_field
 *
 * collection_valued_path_expression ::= general_identification_variable.{single_valued_object_field.}*collection_valued_field
 *
 * update_clause ::= UPDATE entity_name [[AS] identification_variable] SET update_item {, update_item}*
 *
 * update_item ::= [identification_variable.]{state_field | single_valued_object_field} = new_value
 *
 * new_value ::= scalar_expression | simple_entity_expression | NULL
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
 * subquery_from_clause ::= FROM subselect_identification_variable_declaration {, subselect_identification_variable_declaration | collection_member_declaration}*
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
 *                            in_expression |
 *                            like_expression |
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
 * exists_expression::= [NOT] EXISTS (subquery)
 *
 * all_or_any_expression ::= { ALL | ANY | SOME} (subquery)
 *
 * comparison_expression ::= string_expression comparison_operator {string_expression | all_or_any_expression} |
 *                           boolean_expression { = | <> } {boolean_expression | all_or_any_expression} |
 *                           enum_expression { =|<> } {enum_expression | all_or_any_expression} |
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
 * functions_returning_datetime ::= CURRENT_DATE | CURRENT_TIME | CURRENT_TIMESTAMP
 *
 * functions_returning_strings ::= CONCAT(string_primary, string_primary {, string_primary}*) |
 *                                 SUBSTRING(string_primary, simple_arithmetic_expression [, simple_arithmetic_expression]) |
 *                                 TRIM([[trim_specification] [trim_character] FROM] string_primary) |
 *                                 LOWER(string_primary) |
 *                                 UPPER(string_primary)
 *
 * trim_specification ::= LEADING | TRAILING | BOTH
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
 * boolean_literal ::= TRUE | FALSE
 *
 * string_literal ::= 'string'
 *
 * enum_literal ::= EnumType.CONSTANT
 * </pre></code>
 * <p>
 * The following BNF is the EclipseLink's extension over the standard JPQL BNF.
 * <pre><code> functions_returning_strings ::= ... | func_expression
 *
 * functions_returning_numerics ::= ... | func_expression
 *
 * functions_returning_datetime ::= ... | func_expression
 *
 * func_expression ::= FUNC (func_name {, func_item}*)
 *
 * func_item ::= state_field_path_expression | input_parameter | string_literal (NOT SURE)
 * </code></pre>
 *
 * @version 11.2.0
 * @since 11.0.0
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public interface Expression
{
	/**
	 * The constant for 'ABS'.
	 */
	String ABS = "ABS";

	/**
	 * The constant for 'ALL'.
	 */
	String ALL = "ALL";

	/**
	 * The constant for 'AND'.
	 */
	String AND = "AND";

	/**
	 * The constant for 'ANY'.
	 */
	String ANY = "ANY";

	/**
	 * The constant for 'AS'.
	 */
	String AS = "AS";

	/**
	 * The constant for 'ASC'.
	 */
	String ASC = "ASC";

	/**
	 * The constant for 'AVG'.
	 */
	String AVG = "AVG";

	/**
	 * The constant for 'BETWEEN'.
	 */
	String BETWEEN = "BETWEEN";

	/**
	 * The constant for 'BIT_LENGTH', which is an unused keyword.
	 */
	String BIT_LENGTH = "BIT_LENGTH";

	/**
	 * The constant for 'BOTH'.
	 */
	String BOTH = "BOTH";

	/**
	 * The constant for the identifier 'CASE'.
	 */
	String CASE = "CASE";

	/**
	 * The constant for 'CHAR_LENGTH', which is an unused keyword.
	 */
	String CHAR_LENGTH = "CHAR_LENGTH";

	/**
	 * The constant for 'CHARACTER_LENGTH', which is an unused keyword.
	 */
	String CHARACTER_LENGTH = "CHARACTER_LENGTH";

	/**
	 * The constant for 'CLASS', which is an unused keyword.
	 */
	String CLASS = "CLASS";

	/**
	 * The constant for 'COALESCE'.
	 */
	String COALESCE = "COALESCE";

	/**
	 * The constant for 'CONCAT'.
	 */
	String CONCAT = "CONCAT";

	/**
	 * The constant for 'COUNT'.
	 */
	String COUNT = "COUNT";

	/**
	 * The constant for 'CURRENT_DATE'.
	 */
	String CURRENT_DATE = "CURRENT_DATE";

	/**
	 * The constant for 'CURRENT_DATE'.
	 */
	String CURRENT_TIME = "CURRENT_TIME";

	/**
	 * The constant for 'CURRENT_TIMESTAMP'.
	 */
	String CURRENT_TIMESTAMP = "CURRENT_TIMESTAMP";

	/**
	 * The constant for 'DELETE'.
	 */
	String DELETE = "DELETE";

	/**
	 * The constant for 'DELETE FROM'.
	 */
	String DELETE_FROM = "DELETE FROM";

	/**
	 * The constant for 'DESC'.
	 */
	String DESC = "DESC";

	/**
	 * The constant for '<>'.
	 */
	String DIFFERENT = "<>";

	/**
	 * The constant for 'DISTINCT'.
	 */
	String DISTINCT = "DISTINCT";

	/**
	 * The constant for the division sign '/'.
	 */
	String DIVISION = "/";

	/**
	 * The constant for the identifier 'ELSE'.
	 */
	String ELSE = "ELSE";

	/**
	 * The constant for 'EMPTY'.
	 */
	String EMPTY = "EMPTY";

	/**
	 * The constant for the identifier 'END'.
	 */
	String END = "END";

	/**
	 * The constant for 'ENTRY'.
	 */
	String ENTRY = "ENTRY";

	/**
	 * The constant for '='.
	 */
	String EQUAL = "=";

	/**
	 * The constant for 'ESCAPE'.
	 */
	String ESCAPE = "ESCAPE";

	/**
	 * The constant for 'EXISTS'.
	 */
	String EXISTS = "EXISTS";

	/**
	 * The constant for 'FALSE'.
	 */
	String FALSE = "FALSE";

	/**
	 * A constant for 'FETCH'.
	 */
	String FETCH = "FETCH";

	/**
	 * The constant for 'FROM'.
	 */
	String FROM = "FROM";

	/**
	 * The constant 'FUNC'.
	 */
	String FUNC = "FUNC";

	/**
	 * The constant for '>'.
	 */
	String GREATER_THAN = ">";

	/**
	 * The constant for '>='.
	 */
	String GREATER_THAN_OR_EQUAL = ">=";

	/**
	 * The constant for 'GROUP BY'.
	 */
	String GROUP_BY = "GROUP BY";

	/**
	 * The constant for 'HAVING'.
	 */
	String HAVING = "HAVING";

	/**
	 * The constant for 'IN'.
	 */
	String IN = "IN";

	/**
	 * The constant for 'INDEX'.
	 */
	String INDEX = "INDEX";

	/**
	 * The constant for 'INNER'.
	 */
	String INNER = "INNER";

	/**
	 * The constant for 'INNER JOIN'.
	 */
	String INNER_JOIN = "INNER JOIN";

	/**
	 * The constant for 'INNER JOIN FETCH'.
	 */
	String INNER_JOIN_FETCH = "INNER JOIN FETCH";

	/**
	 * The constant for 'IS'.
	 */
	String IS = "IS";

	/**
	 * The constant for 'IS EMPTY'.
	 */
	String IS_EMPTY = "IS EMPTY";

	/**
	 * The constant for 'IS NOT EMPTY'.
	 */
	String IS_NOT_EMPTY = "IS NOT EMPTY";

	/**
	 * The constant for 'IS NOT NULL'.
	 */
	String IS_NOT_NULL = "IS NOT NULL";

	/**
	 * The constant for 'IS NULL'.
	 */
	String IS_NULL = "IS NULL";

	/**
	 * The constant for 'JOIN'.
	 */
	String JOIN = "JOIN";

	/**
	 * The constant for 'JOIN FETCH'.
	 */
	String JOIN_FETCH = "JOIN FETCH";

	/**
	 * The constant for 'KEY'.
	 */
	String KEY = "KEY";

	/**
	 * The constant for 'LEADING'.
	 */
	String LEADING = "LEADING";

	/**
	 * The constant for 'LEFT'.
	 */
	String LEFT = "LEFT";

	/**
	 * The constant for 'LEFT JOIN'.
	 */
	String LEFT_JOIN = "LEFT JOIN";

	/**
	 * The constant for 'LEFT JOIN FETCH'.
	 */
	String LEFT_JOIN_FETCH = "LEFT JOIN FETCH";

	/**
	 * The constant for 'LEFT OUTER JOIN'.
	 */
	String LEFT_OUTER_JOIN = "LEFT OUTER JOIN";

	/**
	 * The constant for 'LEFT OUTER JOIN FETCH'.
	 */
	String LEFT_OUTER_JOIN_FETCH = "LEFT OUTER JOIN FETCH";

	/**
	 * The constant for 'LENGTH'.
	 */
	String LENGTH = "LENGTH";

	/**
	 * The constant for 'LIKE'.
	 */
	String LIKE = "LIKE";

	/**
	 * The constant for 'LOCATE'.
	 */
	String LOCATE = "LOCATE";

	/**
	 * The constant for 'LOWER'.
	 */
	String LOWER = "LOWER";

	/**
	 * The constant for '<'.
	 */
	String LOWER_THAN = "<";

	/**
	 * The constant for '<='.
	 */
	String LOWER_THAN_OR_EQUAL = "<=";

	/**
	 * The constant for 'MAX'.
	 */
	String MAX = "MAX";

	/**
	 * The constant for 'MEMBER'.
	 */
	String MEMBER = "MEMBER";

	/**
	 * The constant for 'MEMBER OF'.
	 */
	String MEMBER_OF = "MEMBER OF";

	/**
	 * The constant for 'MIN'.
	 */
	String MIN = "MIN";

	/**
	 * The constant for the minus sign '-'.
	 */
	String MINUS = "-";

	/**
	 * The constant for 'MOD'.
	 */
	String MOD = "MOD";

	/**
	 * The constant for multiplication sign '*'.
	 */
	String MULTIPLICATION = "*";

	/**
	 * The constant for ':'.
	 */
	String NAMED_PARAMETER = ":";

	/**
	 * The constant for 'NEW'.
	 */
	String NEW = "NEW";

	/**
	 * The constant for 'NOT'.
	 */
	String NOT = "NOT";

	/**
	 * The constant for 'NOT BETWEEN'.
	 */
	String NOT_BETWEEN = "NOT BETWEEN";

	/**
	 * The constant for 'NOT EXISTS'.
	 */
	String NOT_EXISTS = "NOT EXISTS";

	/**
	 * The constant for 'NOT IN'.
	 */
	String NOT_IN = "NOT IN";

	/**
	 * The constant for 'NOT LIKE'.
	 */
	String NOT_LIKE = "NOT LIKE";

	/**
	 * The constant for 'NOT MEMBER'.
	 */
	String NOT_MEMBER = "NOT MEMBER";

	/**
	 * The constant for 'NOT MEMBER OF'.
	 */
	String NOT_MEMBER_OF = "NOT MEMBER OF";

	/**
	 * The constant for 'NULL'.
	 */
	String NULL = "NULL";

	/**
	 * The constant for 'NULLIF'.
	 */
	String NULLIF = "NULLIF";

	/**
	 * The constant for 'OBJECT'.
	 */
	String OBJECT = "OBJECT";

	/**
	 * The constant for 'OF'.
	 */
	String OF = "OF";

	/**
	 * The constant for 'OR'.
	 */
	String OR = "OR";

	/**
	 * The constant for 'ORDER BY'.
	 */
	String ORDER_BY = "ORDER BY";

	/**
	 * The constant for 'OUTER'.
	 */
	String OUTER = "OUTER";

	/**
	 * The constant for for the plus sign '+'.
	 */
	String PLUS = "+";

	/**
	 * The constant for 'POSITION', which is an unused keyword.
	 */
	String POSITION = "POSITION";

	/**
	 * The constant for '?'.
	 */
	String POSITIONAL_PARAMETER = "?";

	/**
	 * The constant for single quote.
	 */
	String QUOTE = "'";

	/**
	 * The constant for 'SELECT'.
	 */
	String SELECT = "SELECT";

	/**
	 * The constant for 'SET'.
	 */
	String SET = "SET";

	/**
	 * The constant for 'SIZE'.
	 */
	String SIZE = "SIZE";

	/**
	 * The constant for 'SOME'.
	 */
	String SOME = "SOME";

	/**
	 * The constant for 'SQRT'.
	 */
	String SQRT = "SQRT";

	/**
	 * The constant for 'SUBSTRING'.
	 */
	String SUBSTRING = "SUBSTRING";

	/**
	 * The constant for 'SUM'.
	 */
	String SUM = "SUM";

	/**
	 * The constant for 'THEN'.
	 */
	String THEN = "THEN";

	/**
	 * The constant for 'TRAILING'.
	 */
	String TRAILING = "TRAILING";

	/**
	 * The constant for 'TRIM'.
	 */
	String TRIM = "TRIM";

	/**
	 * The constant for 'TRUE'.
	 */
	String TRUE = "TRUE";

	/**
	 * The constant for 'TYPE'.
	 */
	String TYPE = "TYPE";

	/**
	 * The constant for 'UNKNOWN', which is an unused keyword.
	 */
	String UNKNOWN = "UNKNOWN";

	/**
	 * The constant for 'UPDATE'.
	 */
	String UPDATE = "UPDATE";

	/**
	 * The constant for 'UPPER'.
	 */
	String UPPER = "UPPER";

	/**
	 * The constant for 'VALUE'.
	 */
	String VALUE = "VALUE";

	/**
	 * The constant for the identifier 'WHEN'.
	 */
	String WHEN = "WHEN";

	/**
	 * The constant for 'WHERE'.
	 */
	String WHERE = "WHERE";

	/**
	 * Visits this {@link Expression} by the given {@link ExpressionVisitor visitor}.
	 *
	 * @param visitor The {@link ExpressionVisitor visitor} to visit this object
	 */
	void accept(ExpressionVisitor visitor);

	/**
	 * Returns the children of this {@link Expression}.
	 *
	 * @return The children of this {@link Expression} or an empty list if this
	 * {@link Expression} does not have children
	 */
	Iterator<Expression> children();

	/**
	 * Returns the children of this {@link Expression}.
	 *
	 * @return The children of this {@link Expression} or an empty list if this
	 * {@link Expression} does not have children
	 */
	Expression[] getChildren();

	/**
	 * Returns the parent of this {@link Expression}.
	 *
	 * @return The parent of this {@link Expression, which is never <code>null</code>
	 * except for the root of the tree
	 */
	Expression getParent();

	/**
	 * Retrieves the root node of the parsed tree hierarchy.
	 *
	 * @return The root of the {@link Expression} tree
	 */
	JPQLExpression getRoot();

	/**
	 * Determines whether this {@link Expression} is a parent of the given
	 * {@link Expression}.
	 *
	 * @param expression The {@link Expression} to verify its paternity with
	 * this {@link Expression}
	 * @return <code>true</code> if this {@link Expression} is the same as
	 * the given {@link Expression} or one of its parent; <code>false</code>
	 * otherwise
	 */
	boolean isAncestor(Expression expression);

	/**
	 * Returns a string representation of this {@link Expression} and its children.
	 * The expression should contain whitespace even if the beautified version
	 * would not have any.
	 *
	 * @return The portion of the query represented by this {@link Expression}
	 */
	String toParsedText();
}