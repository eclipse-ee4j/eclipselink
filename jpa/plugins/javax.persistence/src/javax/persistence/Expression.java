/*******************************************************************************
 * Copyright (c) 1998, 2009 Oracle. All rights reserved. 
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 * 
 * The API for this class and its comments are derived from the JPA 2.0 specification 
 * which is developed under the Java Community Process (JSR 317) and is copyright 
 * Sun Microsystems, Inc. 
 *
 * Contributors:
 *     dclarke - Java Persistence API 2.0 Public Draft
 *     			 Specification and licensing terms available from
 *     		   	 http://jcp.org/en/jsr/detail?id=317
 *     
 * IMPORTANT: The Criteria API is defined as per the public draft specification
 * but is not implemented in the EclipseLink's early access.
 *
 * EARLY ACCESS - PUBLIC DRAFT
 * This is an implementation of an early-draft specification developed under the 
 * Java Community Process (JCP) and is made available for testing and evaluation 
 * purposes only. The code is not compatible with any specification of the JCP.
 ******************************************************************************/

package javax.persistence;

/**
 * Instances of this interface can be used either as select list items or as
 * predicate operands.
 * 
 * @since Java Persistence API 2.0
 */
public interface Expression {
	/*
	 * Conditional predicates over expression items
	 */
	/**
	 * Create a predicate for testing whether the expression is a member of the
	 * association or element collection denoted by the path expression. The
	 * argument must correspond to a collection-valued association or element
	 * collection of like type.
	 * 
	 * @param arg
	 *            - a path expression that specifies a collection-valued
	 *            association or an element collection
	 * @return conditional predicate
	 */
	Predicate member(PathExpression arg);

	/**
	 * Create a predicate for testing whether the value of the expression is
	 * null.
	 * 
	 * @return conditional predicate
	 */
	Predicate isNull();

	/**
	 * Create a predicate for testing whether the expression value is a member
	 * of the argument list.
	 * 
	 * @param strings
	 * @return conditional predicate
	 */
	Predicate in(String... strings);

	/**
	 * Create a predicate for testing whether the expression value is a member
	 * of the argument list.
	 * 
	 * @param nums
	 * @return conditional predicate
	 */
	Predicate in(Number... nums);

	/**
	 * Create a predicate for testing whether the expression value is a member
	 * of the argument list.
	 * 
	 * @param enums
	 * @return conditional predicate
	 */
	Predicate in(Enum<?>... enums);

	/**
	 * Create a predicate for testing whether the expression value is a member
	 * of the argument list.
	 * 
	 * @param classes
	 * @return conditional predicate
	 */
	Predicate in(Class... classes);

	/**
	 * Create a predicate for testing whether the expression value is a member
	 * of the argument list.
	 * 
	 * @param params
	 * @return conditional predicate
	 */
	Predicate in(Expression... params);

	/**
	 * Create a predicate for testing whether the expression value is a member
	 * of a subquery result.
	 * 
	 * @param subquery
	 * @return conditional predicate
	 */
	Predicate in(Subquery subquery);

	/*
	 * Operations on strings
	 */
	/**
	 * String length This method must be invoked on an expression corresponding
	 * to a string.
	 * 
	 * @return expression denoting the length of the string.
	 */
	Expression length();

	/**
	 * Concatenate a string with other string(s). This method must be invoked on
	 * an expression corresponding to a string.
	 * 
	 * @param str
	 *            - string(s)
	 * @return expression denoting the concatenation of the strings, starting
	 *         with the string corresponding to the expression on which the
	 *         method was invoked.
	 */
	Expression concat(String... str);

	/**
	 * Concatenate a string with other string(s). This method must be invoked on
	 * an expression corresponding to a string.
	 * 
	 * @param str
	 *            - expression(s) corresponding to string(s)
	 * @return expression denoting the concatenation of the strings, starting
	 *         with the string corresponding to the expression on which the
	 *         method was invoked.
	 */
	Expression concat(Expression... str);

	/**
	 * Extract a substring starting at specified position through to the end of
	 * the string. This method must be invoked on an expression corresponding to
	 * a string.
	 * 
	 * @param start
	 *            - start position (1 indicates first position)
	 * @return expression denoting the extracted substring
	 */
	Expression substring(int start);

	/**
	 * Extract a substring starting at specified position through to the end of
	 * the string. This method must be invoked on an expression corresponding to
	 * a string.
	 * 
	 * @param start
	 *            - expression denoting start position (1 indicates first
	 *            position)
	 * @return expression denoting the extracted substring
	 */
	Expression substring(Expression start);

	/**
	 * Extract a substring. This method must be invoked on an expression
	 * corresponding to a string.
	 * 
	 * @param start
	 *            - start position (1 indicates first position)
	 * @param len
	 *            - length of the substring to be returned
	 * @return expression denoting the extracted substring
	 */
	Expression substring(int start, int len);

	/**
	 * Extract a substring. This method must be invoked on an expression
	 * corresponding to a string.
	 * 
	 * @param start
	 *            - start position (1 indicates first position)
	 * @param len
	 *            - expression denoting length of the substring to return
	 * @return expression denoting the extracted substring
	 */
	Expression substring(int start, Expression len);

	/**
	 * Extract a substring. This method must be invoked on an expression
	 * corresponding to a string.
	 * 
	 * @param start
	 *            - expression denoting start position (1 indicates first
	 *            position)
	 * @param len
	 *            - length of the substring to return
	 * @return expression denoting the extracted substring
	 */
	Expression substring(Expression start, int len);

	/**
	 * Extract a substring. This method must be invoked on an expression
	 * corresponding to a string.
	 * 
	 * @param start
	 *            - expression denoting start position (1 indicates first
	 *            position)
	 * @param len
	 *            - expression denoting length of the substring to return
	 * @return expression denoting the extracted substring
	 */
	Expression substring(Expression start, Expression len);

	/**
	 * Convert string to lowercase. This method must be invoked on an expression
	 * corresponding to a string.
	 * 
	 * @return expression denoting the string in lowercase
	 */
	Expression lower();

	/**
	 * Convert string to uppercase. This method must be invoked on an expression
	 * corresponding to a string.
	 * 
	 * @return expression denoting the string in uppercase
	 */
	Expression upper();

	/**
	 * Trim leading and trailing blanks. This method must be invoked on an
	 * expression corresponding to a string.
	 * 
	 * @return expression denoting trimmed string
	 */
	Expression trim();

	/**
	 * Trim leading, trailing blanks (or both) as specified by trim spec. This
	 * method must be invoked on an expression corresponding to a string.
	 * 
	 * @param spec
	 *            - trim specification
	 * @return expression denoting trimmed string
	 */
	Expression trim(TrimSpec spec);

	/**
	 * Trim leading and trailing occurrences of character from the string. This
	 * method must be invoked on an expression corresponding to a string.
	 * 
	 * @param c
	 *            - character to be trimmed
	 * @return expression denoting trimmed string
	 */
	Expression trim(char c);

	/**
	 * Trim occurrences of the character from leading or trailing (or both)
	 * positions of the string, as specified by trim spec. This method must be
	 * invoked on an expression corresponding to a string.
	 * 
	 * @param c
	 *            - character to be trimmed
	 * @param spec
	 *            - trim specification
	 * @return expression denoting trimmed string
	 */
	Expression trim(char c, TrimSpec spec);

	/**
	 * Trim leading and trailing occurrences of character specified by the
	 * expression argument from the string. This method must be invoked on an
	 * expression corresponding to a string.
	 * 
	 * @param expr
	 *            - expression corresponding to the character to be trimmed
	 * @return expression denoting trimmed string
	 */
	Expression trim(Expression expr);

	/**
	 * Trim occurrences of the character specified by the expression argument
	 * from leading or trailing (or both) positions of the string, as specified
	 * by trim spec. This method must be invoked on an expression corresponding
	 * to a string.
	 * 
	 * @param expr
	 *            - expression corresponding to the character to be trimmed
	 * @param spec
	 *            - trim specification
	 * @return expression denoting trimmed string
	 */
	Expression trim(Expression expr, TrimSpec spec);

	/**
	 * Locate a string contained within the string corresponding to the
	 * expression on which the method was invoked. The search is started at
	 * position 1 (first string position). This method must be invoked on an
	 * expression corresponding to a string.
	 * 
	 * @param str
	 *            - string to be located
	 * @return expression denoting the first position at which the string was
	 *         found or expression denoting 0 if the string was not found
	 */
	Expression locate(String str);

	/**
	 * Locate a string contained within the string corresponding to the
	 * expression on which the method was invoked. The search is started at
	 * position 1 (first string position). This method must be invoked on an
	 * expression corresponding to a string.
	 * 
	 * @param str
	 *            - expression corresponding to the string to be located
	 * @return expression denoting the first position at which the string was
	 *         found or expression denoting 0 if the string was not found
	 */
	Expression locate(Expression str);

	/**
	 * Locate a string contained within the string corresponding to the
	 * expression on which the method was invoked, starting at a specified
	 * search position. This method must be invoked on an expression
	 * corresponding to a string.
	 * 
	 * @param str
	 *            - string to be located
	 * @param position
	 *            - position at which to start the search
	 * @return expression denoting the first position at which the string was
	 *         found or expression denoting 0 if the string was not found
	 */
	Expression locate(String str, int position);

	/**
	 * Locate a string contained within the string corresponding to the
	 * expression on which the method was invoked, starting at a specified
	 * search position. This method must be invoked on an expression
	 * corresponding to a string.
	 * 
	 * @param str
	 *            - string to be located
	 * @param position
	 *            - expression corresponding to position at which to start the
	 *            search
	 * @return expression denoting the first position at which the string was
	 *         found or expression denoting 0 if the string was not found
	 */
	Expression locate(String str, Expression position);

	/**
	 * Locate a string contained within the string corresponding to the
	 * expression on which the method was invoked, starting at a specified
	 * search position. This method must be invoked on an expression
	 * corresponding to a string.
	 * 
	 * @param str
	 *            - expression corresponding to the string to be located
	 * @param position
	 *            - position at which to start the search
	 * @return expression denoting the first position at which the string was
	 *         found or expression denoting 0 if the string was not found
	 */
	Expression locate(Expression str, int position);

	/**
	 * Locate a string contained within the string corresponding to the
	 * expression on which the method was invoked, starting at a specified
	 * search position. This method must be invoked on an expression
	 * corresponding to a string.
	 * 
	 * @param str
	 *            - expression corresponding to the string to be located
	 * @param position
	 *            - expression corresponding to position at which to start the
	 *            search
	 * @return expression denoting the first position at which the string was
	 *         found or expression denoting 0 if the string was not found
	 */
	Expression locate(Expression str, Expression position);

	/*
	 * Arithmetic operations
	 */
	/**
	 * Addition. This method must be invoked on an expression corresponding to a
	 * number.
	 * 
	 * @param num
	 *            - number to be added
	 * @return expression denoting the sum
	 */
	Expression plus(Number num);

	/**
	 * Addition. This method must be invoked on an expression corresponding to a
	 * number.
	 * 
	 * @param expr
	 *            - expression corresponding to number to be added
	 * @return expression denoting the sum
	 */
	Expression plus(Expression expr);

	/**
	 * Unary minus. This method must be invoked on an expression corresponding
	 * to a number.
	 * 
	 * @return expression denoting the unary minus of the expression
	 */
	Expression minus();

	/**
	 * Subtraction. This method must be invoked on an expression corresponding
	 * to a number.
	 * 
	 * @param num
	 *            - subtrahend
	 * @return expression denoting the result of subtracting the argument from
	 *         the number corresponding to the expression on which the method
	 *         was invoked.
	 */
	Expression minus(Number num);

	/**
	 * Subtraction. This method must be invoked on an expression corresponding
	 * to a number.
	 * 
	 * @param expr
	 *            - expression corresponding to subtrahend
	 * @return expression denoting the result of subtracting the number denoted
	 *         by the argument from the number corresponding to the expression
	 *         on which the method was invoked.
	 */
	Expression minus(Expression expr);

	/**
	 * Division. This method must be invoked on an expression corresponding to a
	 * number.
	 * 
	 * @param num
	 *            - divisor
	 * @return expression denoting the result of dividing the number
	 *         corresponding to the expression on which the method was invoked
	 *         by the argument
	 */
	Expression dividedBy(Number num);

	/**
	 * Division. This method must be invoked on an expression corresponding to a
	 * number.
	 * 
	 * @param expr
	 *            - expression corresponding to the divisor
	 * @return expression denoting the result of dividing the number
	 *         corresponding to the expression on which the method was invoked
	 *         by the number denoted by the argument
	 */
	Expression dividedBy(Expression expr);

	/**
	 * Multiplication. This method must be invoked on an expression
	 * corresponding to a number.
	 * 
	 * @param num
	 *            - multiplier
	 * @return expression denoting the result of multiplying the argument with
	 *         the number corresponding to the expression on which the method
	 *         was invoked.
	 */
	Expression times(Number num);

	/**
	 * Multiplication. This method must be invoked on an expression
	 * corresponding to a number.
	 * 
	 * @param expr
	 *            - expression corresponding to the multiplier
	 * @return expression denoting the result of multiplying the number denoted
	 *         by the argument with the number corresponding to the expression
	 *         on which the method was invoked.
	 */
	Expression times(Expression expr);

	/**
	 * Absolute value. This method must be invoked on an expression
	 * corresponding to a number.
	 * 
	 * @return expression corresponding to the absolute value
	 */
	Expression abs();

	/**
	 * Square root. This method must be invoked on an expression corresponding
	 * to a number.
	 * 
	 * @return expression corresponding to the square root
	 */
	Expression sqrt();

	/**
	 * Modulo operation. This must be invoked on an expression corresponding to
	 * an integer value
	 * 
	 * @param num
	 *            - integer divisor
	 * @return expression corresponding to the integer remainder of the division
	 *         of the integer corresponding to the expression on which the
	 *         method was invoked by the argument.
	 */
	Expression mod(int num);

	/**
	 * Modulo operation. This must be invoked on an expression corresponding to
	 * an integer value
	 * 
	 * @param expr
	 *            - expression corresponding to integer divisor
	 * @return expression corresponding to the integer remainder of the division
	 *         of the integer corresponding to the expression on which the
	 *         method was invoked by the argument.
	 */
	Expression mod(Expression expr);
}
