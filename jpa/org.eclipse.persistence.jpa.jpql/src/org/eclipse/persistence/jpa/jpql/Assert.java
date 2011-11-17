/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.jpa.jpql;

/**
 * An utility class that provides various checks and when the condition fails, then an {@link
 * AssertException} is thrown.
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public final class Assert {

	/**
	 * Prevents instantiation.
	 */
	private Assert() {
		super();
	}

	/**
	 * Throws an {@link AssertException} immediately.
	 *
	 * @param message The message to display
	 */
	public static void fail(String message) {
		throw new AssertException(message);
	}

	/**
	 * Determines whether the values are different, with the appropriate <code>null</code> checks.
	 *
	 * @param value1 The first value to check for equality and equivalency
	 * @param value2 The second value to check for equality and equivalency
	 * @return <code>true</code> if both values are different; <code>true</code> if they are both
	 * <code>null</code>, equal or equivalent
	 */
	public static boolean isDifferent(Object object1, Object object2) {
		return !isSame(object1, object2);
	}

	/**
	 * Determines whether the given two objects are equal (identity). If the two objects are not
	 * identical, then an {@link AssertException} is thrown
	 *
	 * @param object1 The first object used to be compared to the second object
	 * @param object2 The second object used to be compared to the first object
	 * @param message The expression's message that will describe the reason why the check failed
	 */
	public static void isEqual(Object object1, Object object2, String message) {
		if (object1 != object2) {
			fail(message);
		}
	}

	/**
	 * Determines whether the given condition if <code>false</code> or <code>true</code> and if it is
	 * <code>true</code> then an {@link AssertException} is thrown.
	 *
	 * @param condition The condition to verify it is <code>false</code>
	 * @param message The expression's message that will describe the reason why the check failed
	 */
	public static void isFalse(boolean condition, String message) {
		if (condition) {
			fail(message);
		}
	}

	/**
	 * Determines whether the given object is not <code>null</code>. If the object is <code>null</code>,
	 * then an {@link NullPointerException} is thrown
	 *
	 * @param object The value to check to not be <code>null</code>
	 * @param message The expression's message that will describe the reason why the check failed
	 */
	public static void isNotNull(Object object, String message) {
		if (object == null) {
			throw new NullPointerException(message);
		}
	}

	/**
	 * Determines whether the given object is <code>null</code>. If the object is not <code>null</code>,
	 * then an {@link AssertException} is thrown
	 *
	 * @param object The value to check to be <code>null</code>
	 * @param message The expression's message that will describe the reason why the check failed
	 */
	public static void isNull(Object object, String message) {
		if (object != null) {
			fail(message);
		}
	}

	/**
	 * Determines whether the values are equal or equivalent, with the appropriate <code>null</code>
	 * checks. No exception is thrown when the two values are not equal or equivalent.
	 *
	 * @param value1 The first value to check for equality and equivalency
	 * @param value2 The second value to check for equality and equivalency
	 * @return <code>true</code> if both values are <code>null</code>, equal or equivalent;
	 * <code>false</code> otherwise
	 */
	public static boolean isSame(Object object1, Object object2) {

		// Both are equal or both are null
		if ((object1 == object2) || (object1 == null) && (object2 == null)) {
			return true;
		}

		// One is null but the other is not
		if ((object1 == null) || (object2 == null)) {
			return false;
		}

		return object1.equals(object2);
	}

	/**
	 * Determines whether the given condition if <code>true</code> or <code>false</code> and if it is
	 * <code>false</code> then an {@link AssertException} is thrown.
	 *
	 * @param condition The condition to verify it is <code>true</code>
	 * @param message The expression's message that will describe the reason why the check failed
	 */
	public static void isTrue(boolean condition, String message) {
		if (!condition) {
			fail(message);
		}
	}

	/**
	 * Determines whether the given object is one of the given choices using identity check.
	 *
	 * @param object The object to find in the list of choices
	 * @param message The expression's message that will describe the reason why the check failed
	 * @param choices The list of valid choices
	 */
	public static void isValid(Object object, String message, Object... choices) {
		for (Object choice : choices) {
			if (object == choice) {
				return;
			}
		}
		fail(message);
	}

	/**
	 * The exception thrown when the condition is not met.
	 */
	public static class AssertException extends RuntimeException {

		/**
		 * Creates a new <code>AssertException</code>.
		 *
		 * @param message The message describing the reason why this exception is thrown
		 */
		public AssertException(String message) {
			super(message);
		}
	}
}