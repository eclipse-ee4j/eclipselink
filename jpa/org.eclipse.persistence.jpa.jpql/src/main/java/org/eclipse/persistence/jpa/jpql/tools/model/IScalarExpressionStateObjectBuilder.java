/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.jpa.jpql.tools.model;

/**
 * This builder can be used to easily create a scalar expression without having to create each
 * object manually.
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public interface IScalarExpressionStateObjectBuilder<T extends IScalarExpressionStateObjectBuilder<T>> {

    /**
     * Creates the expression <code><b>ABS(x)</b></code>.
     *
     * @param builder The parameter of the <code><b>ABS</b></code> expression
     * @return This This builder
     */
    T abs(T builder);

    /**
     * Creates the expression <code><b>x + y</b></code>.
     *
     * @param builder The right side of the addition expression
     * @return This builder
     */
    T add(T builder);

    /**
     * Creates the expression <code><b>AVG(path)</b></code>.
     *
     * @param path The state field path expression
     * @return This This builder
     */
    T avg(String path);

    /**
     * Creates the expression <code><b>AVG(DISTINCT path)</b></code>.
     *
     * @param path The state field path expression
     * @return This This builder
     */
    T avgDistinct(String path);

    /**
     * Creates a new <code><b>CASE</b></code> expression.
     *
     * @param builder The {@link ICaseExpressionStateObjectBuilder builder} of a <code><b>CASE</b></code>
     * expression
     * @return This This builder
     */
    T case_(ICaseExpressionStateObjectBuilder builder);

    /**
     * Create the expression <code><b>COALESCE(scalar_expression {, scalar_expression}+)</b></code>.
     *
     * @param builder1 The first scalar expression
     * @param builder2 The second scalar expression
     * @param builders The subsequent scalar expressions
     * @return This This builder
     */
    T coalesce(T builder1,
               T builder2,
               T... builders);

    /**
     * Creates the expression <code><b>CONCAT(string_primary, string_primary {, string_primary}*)</b></code>.
     *
     * @param builder1 The first argument of the expression
     * @param builder2 The second argument of the expression
     * @param builders The subsequence arguments of the expression, which are optional
     * @return This This builder
     */
    T concat(T builder1,
             T builder2,
             T... builders);

    /**
     * Creates the expression <code><b>COUNT(identification_variable |
     *                                       state_field_path_expression |
     *                                       single_valued_object_path_expression)</b></code>.
     *
     * @param path The state field path expression
     * @return This This builder
     */
    T count(String path);

    /**
     * Creates the expression <code><b>COUNT(DISTINCT identification_variable |
     *                                                state_field_path_expression |
     *                                                single_valued_object_path_expression)</b></code>.
     *
     * @param path The state field path expression
     * @return This This builder
     */
    T countDistinct(String path);

    /**
     * Creates the expression representing <code><b>CURRENT_DATE</b></code>.
     *
     * @return This This builder
     */
    T currentDate();

    /**
     * Creates the expression representing <code><b>CURRENT_TIME</b></code>.
     *
     * @return This This builder
     */
    T currentTime();

    /**
     * Creates the expression representing <code><b>CURRENT_TIMESTAMP</b></code>.
     *
     * @return This This builder
     */
    T currentTimestamp();

    /**
     * Creates a new date using the JDBC syntax of a date.
     *
     * @return This This builder
     */
    T date(String jdbcDate);

    /**
     * Creates the expression <code><b>x / y</b></code>.
     *
     * @param builder The right side of the division expression
     * @return This This builder
     */
    T divide(T builder);

    /**
     * Creates a new entity type literal.
     *
     * @param entityTypeName The short name of the entity
     * @return This This builder
     */
    T entityType(String entityTypeName);

    /**
     * Creates a new enum literal.
     *
     * @param enumConstant The enum constant
     * @return This This builder
     */
    T enumLiteral(Enum<? extends Enum<?>> enumConstant);

    /**
     * Creates the expression <code><b>&lt;identifier&gt;('functionName' {, expression}*)</b></code>.
     *
     * @param identifier The JPQL identifier that is used to identify the SQL expression
     * @param functionName The name of the native SQL function
     * @param arguments The arguments of the expression
     * @return This builder
     */
    T function(String identifier, String functionName, String... arguments);

    /**
     * Creates the expression <code><b>&lt;identifier&gt;('functionName' {, expression}*)</b></code>.
     *
     * @param identifier The JPQL identifier that is used to identify the SQL expression
     * @param functionName The name of the native SQL function
     * @param arguments The arguments of the expression
     * @return This builder
     */
    T function(String identifier, String functionName, T... arguments);

    /**
     * Returns the builder that can create a <code><b>CASE</b></code> expression, which requires a
     * {@link IConditionalExpressionStateObjectBuilder} to build the <code><b>WHEN</b></code> clauses.
     *
     * @return The builder of a <code><b>CASE</b></code> expression
     */
    ICaseExpressionStateObjectBuilder getCaseBuilder();

    /**
     * Creates the expression <code><b>INDEX(identification_variable)</b></code>.
     *
     * @param variable The identification variable
     * @return This This builder
     */
    T index(String variable);

    /**
     * Creates the expression <code><b>LENGTH(expression)</b></code>.
     *
     * @param builder The encapsulated expression
     * @return This This builder
     */
    T length(T builder);

    /**
     * Creates the expression <code><b>LOCATE(string_primary, string_primary)</b></code>.
     *
     * @param parameter1 The first string primary
     * @param parameter2 The second string primary
     * @return This This builder
     */
    T locate(T parameter1, T parameter2);

    /**
     * Creates the expression <code><b>LOCATE(string_primary, string_primary [, simple_arithmetic_expression])</b></code>.
     *
     * @param parameter1 The first string primary
     * @param parameter2 The second string primary
     * @param parameter3 The position of the search within the string
     * @return This This builder
     */
    T locate(T parameter1, T parameter2, T parameter3);

    /**
     * Creates the expression <code><b>MAX(path)</b></code>.
     *
     * @param path The state field path expression
     * @return This This builder
     */
    T max(String path);

    /**
     * Creates the expression <code><b>MAX(DISTINCT path)</b></code>.
     *
     * @param path The state field path expression
     * @return This This builder
     */
    T maxDistinct(String path);

    /**
     * Creates the expression <code><b>AVG(path)</b></code>.
     *
     * @param path The state field path expression
     * @return This This builder
     */
    T min(String path);

    /**
     * Creates the expression <code><b>AVG(DISTINCT path)</b></code>.
     *
     * @param path The state field path expression
     * @return This This builder
     */
    T minDistinct(String path);

    /**
     * Creates the expression <code><b>- x</b></code>.
     *
     * @param builder The expression that will have the plus sign prepended
     * @return This This builder
     */
    T minus(T builder);

    /**
     * Creates the expression <code><b>MOD(simple_arithmetic_expression, simple_arithmetic_expression)</b></code>.
     *
     * @param parameter1 The first parameter
     * @param parameter2 The second parameter
     * @return This This builder
     */
    T mod(T parameter1, T parameter2);

    /**
     * Creates the expression <code><b>x * y</b></code>.
     *
     * @param builder The right side of the multiply expression
     * @return This This builder
     */
    T multiply(T builder);

    /**
     * Create the expression <code><b>NULLIF(scalar_expression, scalar_expression)</b></code>.
     *
     * @param builder1 The first scalar expression
     * @param builder2 The second scalar expression
     * @return This This builder
     */
    T nullIf(T builder1, T builder2);

    /**
     * Creates the numeric literal.
     *
     * @param number The numeric literal
     * @return This This builder
     */
    T numeric(Number number);

    /**
     * Creates the numeric literal.
     *
     * @param number The numeric literal
     * @return This This builder
     */
    T numeric(String number);

    /**
     * Creates the input parameter.
     *
     * @param parameter The named or positional input parameter
     * @return This This builder
     */
    T parameter(String parameter);

    /**
     * Creates a new state field path expression.
     *
     * @param path The state field path path expression
     * @return This This builder
     */
    T path(String path);

    /**
     * Creates the expression <code><b>+ x</b></code>.
     *
     * @param builder The expression that will have the plus sign prepended
     * @return This This builder
     */
    T plus(T builder);

    /**
     * Creates the expression <code><b>SIZE(collection_valued_path_expression)</b></code>.
     *
     * @param path The collection-valued path expression
     * @return This This builder
     */
    T size(String path);

    /**
     * Creates the expression <code><b>SQRT(x)</b></code>.
     *
     * @param builder The parameter of the <code><b>ABS</b></code> expression
     * @return This This builder
     */
    T sqrt(T builder);

    /**
     * Creates a new string literal.
     *
     * @param literal The string literal
     * @return This This builder
     */
    T string(String literal);

    /**
     * Creates an encapsulated expression: <code><b>(expression)</b></code>.
     *
     * @param builder The expression that will be encapsulated
     * @return This This builder
     */
    T sub(T builder);

    /**
     * Creates the expression <code><b>x - y</b></code>.
     *
     * @param builder The right side of the addition expression
     * @return This This builder
     */
    T subtract(T builder);

    /**
     * Creates the expression <code><b>SUM(path)</b></code>.
     *
     * @param path The state field path expression
     * @return This This builder
     */
    T sum(String path);

    /**
     * Creates the expression <code><b>SUM(path)</b></code>.
     *
     * @param path The state field path expression
     * @return This This builder
     */
    T sumDistinct(String path);

    /**
     * Creates the expression <code><b>TYPE(identification_variable | single_valued_object_path_expression | input_parameter)</b></code>.
     *
     * @param path The identification variable or the input parameter
     * @return This This builder
     */
    T type(String path);
}
