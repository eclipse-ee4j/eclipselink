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

/**
 * A utility class used to help parsing a JPA query.
 *
 * @version 11.2.0
 * @since 11.0.0
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class ExpressionTools
{
	/**
	 * ExpressionTools cannot be instantiated.
	 */
	private ExpressionTools()
	{
		super();
		throw new IllegalAccessError("ExpressionTools cannot be instantiated");
	}

	/**
	 * Determines whether the specified string is <code>null</code>, empty, or
	 * contains only whitespace characters.
	 *
	 * @param text The sequence of character to test if it is <code>null</code>
	 * or only contains whitespace
	 * @return <code>true</code> if the given string is <code>null</code> or only
	 * contains whitespace; <code>false</code> otherwise
	 */
	public static boolean stringIsEmpty(CharSequence text)
	{
		if ((text == null) || (text.length() == 0))
		{
			return true;
		}

		for (int i = text.length(); i-- > 0;)
		{
			if (!Character.isWhitespace(text.charAt(i)))
			{
				return false;
			}
		}

		return true;
	}

	/**
	 * Determines whether the specified string is NOT <code>null</code>, NOT
	 * empty, or contains at least one non-whitespace character.
	 *
	 * @param text The sequence of character to test if it is NOT <code>null</code>
	 * or does not only contain whitespace
	 * @return <code>true</code> if the given string is NOT <code>null</code> or
	 * has at least one non-whitespace character; <code>false</code> otherwise
	 */
	public static boolean stringIsNotEmpty(CharSequence text)
	{
		return !stringIsEmpty(text);
	}

	/**
	 * Determines whether the values are different, with the appropriate
	 * <code>null</code> checks.
	 *
	 * @param value1 The first value to check for equality and equivalency
	 * @param value2 The second value to check for equality and equivalency
	 * @return <code>true</code> if both values are different; <code>true</code>
	 * if they are both <code>null</code>, equal or equivalent
	 */
	public static boolean valuesAreDifferent(Object value1, Object value2)
	{
		return !valuesAreEqual(value1, value2);
	}

	/**
	 * Determines whether the values are equal or equivalent, with the
	 * appropriate <code>null</code> checks.
	 *
	 * @param value1 The first value to check for equality and equivalency
	 * @param value2 The second value to check for equality and equivalency
	 * @return <code>true</code> if both values are <code>null</code>, equal or
	 * equivalent; <code>false</code> otherwise
	 */
	public static boolean valuesAreEqual(Object value1, Object value2)
	{
		// Both are equal or both are null
		if ((value1 == value2) || (value1 == null) && (value2 == null))
		{
			return true;
		}

		// One is null but the other is not
		if ((value1 == null) || (value2 == null))
		{
			return false;
		}

		return value1.equals(value2);
	}
}