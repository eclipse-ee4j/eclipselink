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

import java.util.Calendar;
import java.util.Date;

/**
 * Interface for constructing where-clause and having-clause conditions.
 * Instances of PredicateOperand are used in constructing predicates passed to
 * the where or having methods.
 * 
 * @since Java Persistence API
 */
public interface PredicateOperand {
	/**
	 * Create a predicate for testing equality with the specified argument.
	 * 
	 * @param arg
	 *            - PredicateOperand instance or parameter
	 * @return conditional predicate
	 */
	Predicate equal(PredicateOperand arg);

	/**
	 * Create a predicate for testing equality with the specified argument.
	 * 
	 * @param cls
	 *            - entity class
	 * @return conditional predicate
	 */
	Predicate equal(Class cls);

	/**
	 * Create a predicate for testing equality with the specified argument.
	 * 
	 * @param arg
	 *            - numeric
	 * @return conditional predicate
	 */
	Predicate equal(Number arg);

	/**
	 * Create a predicate for testing equality with the specified argument.
	 * 
	 * @param arg
	 *            - string value
	 * @return conditional predicate
	 */
	Predicate equal(String arg);

	/**
	 * Create a predicate for testing equality with the specified argument.
	 * 
	 * @param arg
	 *            - boolean value
	 * @return conditional predicate
	 */
	Predicate equal(boolean arg);

	/**
	 * Create a predicate for testing equality with the specified argument.
	 * 
	 * @param arg date
	 * @return conditional predicate
	 */
	Predicate equal(Date arg);

	/**
	 * Create a predicate for testing equality with the specified argument.
	 * 
	 * @param arg  calendar
	 * @return conditional predicate
	 */
	Predicate equal(Calendar arg);

	/**
	 * Create a predicate for testing equality with the specified argument.
	 * 
	 * @param e  enum
	 * @return conditional predicate
	 */
	Predicate equal(Enum<?> e);

	/**
	 * Create a predicate for testing inequality with the specified argument.
	 * 
	 * @param arg
	 *            - PredicateOperand instance or parameter
	 * @return conditional predicate
	 */
	Predicate notEqual(PredicateOperand arg);

	/**
	 * Create a predicate for testing inequality with the specified argument.
	 * 
	 * @param cls
	 *            - entity class
	 * @return conditional predicate
	 */
	Predicate notEqual(Class cls);

	/**
	 * Create a predicate for testing inequality with the specified argument.
	 * 
	 * @param arg
	 *            - numberic value
	 * @return conditional predicate
	 */
	Predicate notEqual(Number arg);

	/**
	 * Create a predicate for testing inequality with the specified argument.
	 * 
	 * @param arg
	 *            - string value
	 * @return conditional predicate
	 */
	Predicate notEqual(String arg);

	/**
	 * Create a predicate for testing inequality with the specified argument.
	 * 
	 * @param arg
	 *            - boolean value
	 * @return conditional predicate
	 */
	Predicate notEqual(boolean arg);

	/**
	 * Create a predicate for testing inequality with the specified argument.
	 * 
	 * @param arg
	 *            - date
	 * @return conditional predicate
	 */
	Predicate notEqual(Date arg);

	/**
	 * Create a predicate for testing inequality with the specified argument.
	 * 
	 * @param arg
	 *            - calendar
	 * @return conditional predicate
	 */
	Predicate notEqual(Calendar arg);

	/**
	 * Create a predicate for testing inequality with the specified argument.
	 * 
	 * @param e
	 *            - enum
	 * @return conditional predicate
	 */
	Predicate notEqual(Enum<?> e);

	/**
	 * Create a predicate for testing whether the PredicateOperand is greater
	 * than the argument.
	 * 
	 * @param arg
	 *            - PredicateOperand instance or parameter
	 * @return conditional predicate
	 */
	Predicate greaterThan(PredicateOperand arg);

	/**
	 * Create a predicate for testing whether the PredicateOperand is greater
	 * than the argument.
	 * 
	 * @param arg
	 *            - numeric
	 * @return conditional predicate
	 */
	Predicate greaterThan(Number arg);

	/**
	 * Create a predicate for testing whether the PredicateOperand is greater
	 * than the argument.
	 * 
	 * @param arg
	 *            - string
	 * @return conditional predicate
	 */
	Predicate greaterThan(String arg);

	/**
	 * Create a predicate for testing whether the PredicateOperand is greater
	 * than the argument.
	 * 
	 * @param arg
	 *            - date
	 * @return conditional predicate
	 */
	Predicate greaterThan(Date arg);

	/**
	 * Create a predicate for testing whether the PredicateOperand is greater
	 * than the argument.
	 * 
	 * @param arg
	 *            - calendar
	 * @return conditional predicate
	 */
	Predicate greaterThan(Calendar arg);

	/**
	 * Create a predicate for testing whether the PredicateOperand is greater
	 * than or equal to the argument.
	 * 
	 * @param arg
	 *            - PredicateOperand instance or parameter
	 * @return conditional predicate
	 */
	Predicate greaterEqual(PredicateOperand arg);

	/**
	 * Create a predicate for testing whether the PredicateOperand is greater
	 * than or equal to the argument.
	 * 
	 * @param arg
	 *            - numeric
	 * @return conditional predicate
	 */
	Predicate greaterEqual(Number arg);

	/**
	 * Create a predicate for testing whether the PredicateOperand is greater
	 * than or equal to the argument.
	 * 
	 * @param arg
	 *            - string
	 * @return conditional predicate
	 */
	Predicate greaterEqual(String arg);

	/**
	 * Create a predicate for testing whether the PredicateOperand is greater
	 * than or equal to the argument.
	 * 
	 * @param arg
	 *            - date
	 * @return conditional predicate
	 */
	Predicate greaterEqual(Date arg);

	/**
	 * Create a predicate for testing whether the PredicateOperand is greater
	 * than or equal to the argument.
	 * 
	 * @param arg
	 *            - calendar
	 * @return conditional predicate
	 */
	Predicate greaterEqual(Calendar arg);

	/**
	 * Create a predicate for testing whether the PredicateOperand is less than
	 * the argument.
	 * 
	 * @param arg
	 *            - PredicateOperand instance or parameter
	 * @return conditional predicate
	 */
	Predicate lessThan(PredicateOperand arg);

	/**
	 * Create a predicate for testing whether the PredicateOperand is less than
	 * the argument.
	 * 
	 * @param arg
	 *            - numeric
	 * @return conditional predicate
	 */
	Predicate lessThan(Number arg);

	/**
	 * Create a predicate for testing whether the PredicateOperand is less than
	 * the argument.
	 * 
	 * @param arg
	 *            - string
	 * @return conditional predicate
	 */
	Predicate lessThan(String arg);

	/**
	 * Create a predicate for testing whether the PredicateOperand is less than
	 * the argument.
	 * 
	 * @param arg
	 *            - date
	 * @return conditional predicate
	 */
	Predicate lessThan(Date arg);

	/**
	 * Create a predicate for testing whether the PredicateOperand is less than
	 * the argument.
	 * 
	 * @param arg
	 *            - calendar
	 * @return conditional predicate
	 */
	Predicate lessThan(Calendar arg);

	/**
	 * Create a predicate for testing whether the PredicateOperand is less than
	 * or equal to the argument.
	 * 
	 * @param arg
	 *            - PredicateOperand instance or parameter
	 * @return conditional predicate
	 */
	Predicate lessEqual(PredicateOperand arg);

	/**
	 * Create a predicate for testing whether the PredicateOperand is less than
	 * or equal to the argument.
	 * 
	 * @param arg
	 *            - numeric
	 * @return conditional predicate
	 */
	Predicate lessEqual(Number arg);

	/**
	 * Create a predicate for testing whether the PredicateOperand is less than
	 * or equal to the argument.
	 * 
	 * @param arg
	 *            - string
	 * @return conditional predicate
	 */
	Predicate lessEqual(String arg);

	/**
	 * Create a predicate for testing whether the PredicateOperand is less than
	 * or equal to the argument.
	 * 
	 * @param arg
	 *            - date
	 * @return conditional predicate
	 */
	Predicate lessEqual(Date arg);

	/**
	 * Create a predicate for testing whether the PredicateOperand is less than
	 * or equal to the argument.
	 * 
	 * @param arg
	 *            - calendar
	 * @return conditional predicate
	 */
	Predicate lessEqual(Calendar arg);

	/**
	 * Create a predicate for testing whether the PredicateOperand lies between
	 * (inclusive) the two arguments.
	 * 
	 * @param arg1
	 *            - PredicateOperand instance or parameter
	 * @param arg2
	 *            - PredicateOperand instance or parameter
	 * @return conditional predicate
	 */
	Predicate between(PredicateOperand arg1, PredicateOperand arg2);

	/**
	 * Create a predicate for testing whether the PredicateOperand lies between
	 * (inclusive) the two arguments.
	 * 
	 * @param arg1
	 *            - PredicateOperand instance or parameter
	 * @param arg2
	 *            - numeric
	 * @return conditional predicate
	 */
	Predicate between(PredicateOperand arg1, Number arg2);

	/**
	 * Create a predicate for testing whether the PredicateOperand lies between
	 * (inclusive) the two arguments.
	 * 
	 * @param arg1
	 *            - numeric
	 * @param arg2
	 *            - PredicateOperand instance or parameter
	 * @return conditional predicate
	 */
	Predicate between(Number arg1, PredicateOperand arg2);

	/**
	 * Create a predicate for testing whether the PredicateOperand lies between
	 * (inclusive) the two arguments.
	 * 
	 * @param arg1
	 *            - numeric
	 * @param arg2
	 *            - numeric
	 * @return conditional predicate
	 */
	Predicate between(Number arg1, Number arg2);

	/**
	 * Create a predicate for testing whether the PredicateOperand lies between
	 * (inclusive) the two arguments.
	 * 
	 * @param arg1
	 *            - PredicateOperand instance or parameter
	 * @param arg2
	 *            - string
	 * @return conditional predicate
	 */
	Predicate between(PredicateOperand arg1, String arg2);

	/**
	 * Create a predicate for testing whether the PredicateOperand lies between
	 * (inclusive) the two arguments.
	 * 
	 * @param arg1
	 *            - string
	 * @param arg2
	 *            - PredicateOperand instance or parameter
	 * @return conditional predicate
	 */
	Predicate between(String arg1, PredicateOperand arg2);

	/**
	 * Create a predicate for testing whether the PredicateOperand lies between
	 * (inclusive) the two arguments.
	 * 
	 * @param arg1
	 *            - string
	 * @param arg2
	 *            - string
	 * @return conditional predicate
	 */
	Predicate between(String arg1, String arg2);

	/**
	 * Create a predicate for testing whether the PredicateOperand lies between
	 * (inclusive) the two arguments.
	 * 
	 * @param arg1
	 *            - PredicateOperand instance or parameter
	 * @param arg2
	 *            - date
	 * @return conditional predicate
	 */
	Predicate between(PredicateOperand arg1, Date arg2);

	/**
	 * Create a predicate for testing whether the PredicateOperand lies between
	 * (inclusive) the two arguments.
	 * 
	 * @param arg1
	 *            - date
	 * @param arg2
	 *            - PredicateOperand instance or parameter
	 * @return conditional predicate
	 */
	Predicate between(Date arg1, PredicateOperand arg2);

	/**
	 * Create a predicate for testing whether the PredicateOperand lies between
	 * (inclusive) the two arguments.
	 * 
	 * @param arg1
	 *            - date
	 * @param arg2
	 *            - date
	 * @return conditional predicate
	 */
	Predicate between(Date arg1, Date arg2);

	/**
	 * Create a predicate for testing whether the PredicateOperand lies between
	 * (inclusive) the two arguments.
	 * 
	 * @param arg1
	 *            - PredicateOperand instance or parameter
	 * @param arg2
	 *            - calendar
	 * @return conditional predicate
	 */
	Predicate between(PredicateOperand arg1, Calendar arg2);

	/**
	 * Create a predicate for testing whether the PredicateOperand lies between
	 * (inclusive) the two arguments.
	 * 
	 * @param arg1
	 *            - calendar
	 * @param arg2
	 *            - PredicateOperand instance or parameter
	 * @return conditional predicate
	 */
	Predicate between(Calendar arg1, PredicateOperand arg2);

	/**
	 * Create a predicate for testing whether the PredicateOperand lies between
	 * (inclusive) the two arguments.
	 * 
	 * @param arg1
	 *            - calendar
	 * @param arg2
	 *            - calendar
	 * @return conditional predicate
	 */
	Predicate between(Calendar arg1, Calendar arg2);

	/**
	 * Create a predicate for testing whether the PredicateOperand satisfies the
	 * given pattern.
	 * 
	 * @param pattern
	 * @return conditional predicate
	 */
	Predicate like(PredicateOperand pattern);

	/**
	 * Create a predicate for testing whether the PredicateOperand satisfies the
	 * given pattern.
	 * 
	 * @param pattern
	 * @param escapeChar
	 * @return conditional predicate
	 */
	Predicate like(PredicateOperand pattern, PredicateOperand escapeChar);

	/**
	 * Create a predicate for testing whether the PredicateOperand satisfies the
	 * given pattern.
	 * 
	 * @param pattern
	 * @param escapeChar
	 * @return conditional predicate
	 */
	Predicate like(PredicateOperand pattern, char escapeChar);

	/**
	 * Create a predicate for testing whether the PredicateOperand satisfies the
	 * given pattern.
	 * 
	 * @param pattern
	 * @return conditional predicate
	 */
	Predicate like(String pattern);

	/**
	 * Create a predicate for testing whether the PredicateOperand satisfies the
	 * given pattern.
	 * 
	 * @param pattern
	 * @param escapeChar
	 * @return conditional predicate
	 */
	Predicate like(String pattern, PredicateOperand escapeChar);

	/**
	 * Create a predicate for testing whether the PredicateOperand satisfies the
	 * given pattern.
	 * 
	 * @param pattern
	 * @param escapeChar
	 * @return conditional predicate
	 */
	Predicate like(String pattern, char escapeChar);
}
