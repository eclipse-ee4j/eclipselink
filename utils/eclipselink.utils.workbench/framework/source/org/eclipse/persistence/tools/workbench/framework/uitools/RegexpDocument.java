/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
******************************************************************************/
package org.eclipse.persistence.tools.workbench.framework.uitools;

// JDK
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

/**
 * This class allows the document to use a regular expression. With the regular
 * expression, only specific string will be inserted or deleted. Plain text area
 * and text field can use this document to store the text. This document also
 * has a back pointer to the owner.
 *
 * @version 10.0.3
 * @author Pascal Filion
 */
public class RegexpDocument extends PlainDocument
{
	/**
	 *	The regular expression used to check the insertion or deletion of string.
	 */
	private Pattern regularExpression;

	/**
	 * Caches the created <code>Pattern</code>s for a regular expressions.
	 */
	private static final Hashtable cachedRegularExpressions = new Hashtable();

	/** Regular expression defined for class name without the package. */
	public static final int RE_CLASS = 2;

	/** Regular expression defined for field name. */
	public static final int RE_FIELD = 6;

	/** Regular expression defined for class with package name. */
	public static final int RE_FULLY_QUALIFIED_CLASS_NAME = 3;

	/** Regular expression defined for interface name. */
	public static final int RE_INTERFACE = 4;

	/** Regular expression defined for method name. */
	public static final int RE_METHOD = 5;

	/** If no regular expression is needed for this document. */
	public static final int RE_NONE = 0;

	/** Regular expression defined for integer [-infinite.0, +infinite.0]. */
	public static final int RE_NUMERIC_DECIMAL = 10;

	/** Regular expression defined for integer [-infinite.0, -1.0]. */
	public static final int RE_NUMERIC_DECIMAL_NEGATIVE = 12;

	/** Regular expression defined for integer [0.0, +infinite.0]. */
	public static final int RE_NUMERIC_DECIMAL_POSITIVE = 11;

	/** Regular expression defined for integer [0.0, +infinite.0]. */
	public static final int RE_NUMERIC_DECIMAL_STRICT = 15;

	/** Regular expression defined for integer [-infinite, +infinite]. */
	public static final int RE_NUMERIC_INTEGER = 7;

	/** Regular expression defined for integer [-infinite, -1]. */
	public static final int RE_NUMERIC_INTEGER_NEGATIVE = 9;

	/** Regular expression defined for integer [0, +infinite]. */
	public static final int RE_NUMERIC_INTEGER_POSITIVE = 8;

	/** Regular expression defined for integer [0, +infinite]. */
	public static final int RE_NUMERIC_INTEGER_STRICT = 16;

	/** Regular expression defined by the user. */
	public static final int RE_OTHER = 14;

	/** Regular expression defined for package. */
	public static final int RE_PACKAGE = 1;

	/** Regular expression defined for SQL related string. */
	public static final int RE_SQL_RELATED = 13;

	/**
	 * Regular expression that accepts only short class name. A class name is a
	 * string begining with a letter or with '_' and followed by zero or more
	 * alphanumeric character (including '_'). For inner classes, it is possible
	 * to use '$'. If '.' needs to be used, then <code>RE_SYNTAX_FULLY_QUALIFIED_CLASS_NAME</code>
	 * will have to be used instead.
	 */
	public static final String RE_SYNTAX_CLASS =
	"([\\p{L}_][\\p{L}\\p{N}_]*(\\$[\\p{L}_][\\p{L}\\p{N}_]+)*(\\$\\p{N}+)?(\\$\\z)?)*";

	/**
	 * Regular expression that accepts only package name and fully qualified
	 * class name. A package name is an identifier that can be followed zero or
	 * more identifier separator by '.'. For inner classes, it is possible to use
	 * '$'.
	 */
	public static final String RE_SYNTAX_FULLY_QUALIFIED_CLASS_NAME =
		"([\\p{L}_][\\p{L}\\p{N}_]*(\\.[\\p{L}_][\\p{L}\\p{N}_]*)*(\\$[\\p{L}_][\\p{L}\\p{N}_]+)*(\\$\\p{N}+)?((\\.\\z)|(\\$\\z))?)*";

	/**
	 * Regular expression that accepts only identifier. An identifier is a string
	 * begining with a letter or '_' and followed by zero or more alphanumeric
	 * character (including '_').
	 */
	public static final String RE_SYNTAX_IDENTIFIER = "([\\p{L}_][\\p{L}\\p{N}_]*)*";

	/**
	 * Regular expression that accepts any type of expression.
	 */
	public static final String RE_SYNTAX_NONE = ".*";

	/**
	 * Regular expression that accepts only positive decimal value. An decimal
	 * value is a positive or negative number that can have a decimal part.
	 */
	public static final String RE_SYNTAX_NUMERIC_DECIMAL = "[\\+-]?\\d*\\.?\\d*";

	/**
	 * Regular expression that accepts only negative decimal value.
	 */
	public static final String RE_SYNTAX_NUMERIC_DECIMAL_NEGATIVE = "-?\\d*\\.?\\d*";

	/**
	 * Regular expression that accepts only positive decimal value.
	 */
	public static final String RE_SYNTAX_NUMERIC_DECIMAL_POSITIVE = "\\+?\\d*\\.?\\d*";

	/**
	 * Regular expression that accepts only positive decimal value. An decimal
	 * value is a positive or negative number that can have a decimal part.
	 */
	public static final String RE_SYNTAX_NUMERIC_DECIMAL_STRICT = "\\d*\\.?\\d*";

	/**
	 * Regular expression that accepts only integer value. An integer value is a
	 * positive or negative number that does not have a decimal part.
	 */
	public static final String RE_SYNTAX_NUMERIC_INTEGER = "[\\+-]?\\d*";

	/**
	 * Regular expression that accepts only negative integer value.
	 */
	public static final String RE_SYNTAX_NUMERIC_INTEGER_NEGATIVE = "-?\\d*";

	/**
	 * Regular expression that accepts only positive integer value.
	 */
	public static final String RE_SYNTAX_NUMERIC_INTEGER_POSITIVE = "\\+?\\d*";

	/**
	 * Regular expression that accepts only integer value without + or -.
	 */
	public static final String RE_SYNTAX_NUMERIC_INTEGER_STRICT = "\\d*";

	/**
	 * Regular expression that accepts only package name and fully qualified
	 * class name. A package name is an identifier that can be followed zero or
	 * more identifier separator by '.'.
	 */
	public static final String RE_SYNTAX_PACKAGE =
		"([\\p{L}_][\\p{L}\\p{N}_]*(\\.[\\p{L}_][\\p{L}\\p{N}_]*)*(\\.\\z)?)*";

	/**
	 * Creates a new <code>RegexpDocument</code> with no regular expression.
	 */
	public RegexpDocument()
	{
		this(RE_NONE);
	}

	/**
	 * Creates a new <code>RegexpDocument</code> and use a predefined regular
	 * expression.
	 *
	 * @param regularExpressionType One of the predefined regular expression type
	 */
	public RegexpDocument(int regularExpressionType)
	{
		this(buildRegularExpression(regularExpressionType));
	}

	/**
	 * Creates a new <code>RegexpDocument</code> and set the regular expression.
	 *
	 * @param regularExpression The regular expression use to parse the text
	 */
	public RegexpDocument(Pattern regularExpression)
	{
		super();
		initialize(regularExpression);
	}

	/**
	 * Creates a new <code>RegexpDocument</code> and set the regular expression.
	 *
	 * @param regularExpression The regular expression use to parse the text
	 */
	public RegexpDocument(String regularExpression)
	{
		this(buildRegularExpression(regularExpression));
	}

	/**
	 * Creates a new <code>RegexpDocument</code> and use a predefined regular
	 * expression.
	 *
	 * @param regularExpressionType One of the predefined regular expression type
	 * @return A new <code>RegexpDocument</code>
	 */
	public static Document buildDocument(int regularExpressionType)
	{
		return new RegexpDocument(regularExpressionType);
	}

	/**
	 * Creates a new <code>RegexpDocument</code> and set the regular expression.
	 *
	 * @param regularExpression The regular expression use to parse the text
	 * @return A new <code>RegexpDocument</code>
	 */
	public static Document buildDocument(Pattern regularExpression)
	{
		return new RegexpDocument(regularExpression);
	}

	/**
	 * Creates a new <code>RegexpDocument</code> and set the regular expression.
	 *
	 * @param regularExpression The regular expression use to parse the text
	 * @return A new <code>RegexpDocument</code>
	 */
	public static Document buildDocument(String regularExpression)
	{
		return new RegexpDocument(regularExpression);
	}

	/**
	 * Returns the predefined regular expression associated with the
	 * regular expression type. If the given value is not a predefined
	 * type, then the regular expression returned is RE_SYNTAX_NONE.
	 * If an exception is thrown when creating the regular expression,
	 * <code>null</code> is returned.
	 *
	 * @param expressionExpressionType A predefined type of regular expression
	 * @return The regular expression object that represents the regular
	 * expression type
	 */
	public static Pattern buildRegularExpression(int expressionExpressionType)
	{
		switch (expressionExpressionType)
		{
			case RE_CLASS:
				return buildRegularExpression(RE_SYNTAX_CLASS);

			case RE_FULLY_QUALIFIED_CLASS_NAME:
				return buildRegularExpression(RE_SYNTAX_FULLY_QUALIFIED_CLASS_NAME);

			case RE_FIELD:
			case RE_INTERFACE:
			case RE_METHOD:
			case RE_SQL_RELATED:
				return buildRegularExpression(RE_SYNTAX_IDENTIFIER);

			case RE_NUMERIC_INTEGER:
				return buildRegularExpression(RE_SYNTAX_NUMERIC_INTEGER);

			case RE_NUMERIC_INTEGER_NEGATIVE:
				return buildRegularExpression(RE_SYNTAX_NUMERIC_INTEGER_NEGATIVE);

			case RE_NUMERIC_INTEGER_POSITIVE:
				return buildRegularExpression(RE_SYNTAX_NUMERIC_INTEGER_POSITIVE);

			case RE_NUMERIC_DECIMAL:
				return buildRegularExpression(RE_SYNTAX_NUMERIC_DECIMAL);

			case RE_NUMERIC_DECIMAL_NEGATIVE:
				return buildRegularExpression(RE_SYNTAX_NUMERIC_DECIMAL_NEGATIVE);

			case RE_NUMERIC_DECIMAL_POSITIVE:
				return buildRegularExpression(RE_SYNTAX_NUMERIC_DECIMAL_POSITIVE);

			case RE_PACKAGE:
				return buildRegularExpression(RE_SYNTAX_PACKAGE);

			default:
				return null;
		}
	}

	/**
	 * Creates the compiled <code>Pattern</code> for the given regular expression.
	 *
	 * @param regularExpression The pattern used to create a new regular expression
	 */
	public static Pattern buildRegularExpression(String regularExpression)
	{	
		return buildRegularExpression(regularExpression, Pattern.UNICODE_CASE);
	}

	/**
	 * Creates the compiled <code>Pattern</code> for the given regular expression.
	 *
	 * @param regularExpression The pattern used to create a new regular expression
	 * @param flags The flags to be used with the regular expression
	 */
	public static Pattern buildRegularExpression(String regularExpression, int flags)
	{	
		Pattern pattern = (Pattern) cachedRegularExpressions.get(regularExpression);

		if (pattern == null)
		{
			pattern = Pattern.compile(regularExpression, flags);
			cachedRegularExpressions.put(regularExpression, pattern);
		}

		return pattern;
	}

	/**
	 * Returns the regular expression used for this document.
	 *
	 * @return The regular expression used for this document
	 * @see #setRegularExpression
	 */
	protected Pattern getRegularExpression()
	{
		return regularExpression;
	}

	/**
	 * Initializes this <code>RegexpDocument</code>.
	 *
	 * @param regularExpression The regular expression use to parse the text or
	 * <code>null</code> if anything is accepted
	 */
	protected void initialize(Pattern regularExpression)
	{
		this.regularExpression = regularExpression;
	}

	/**
	 * Inserts a string of content. This will cause a DocumentEvent of type
	 * DocumentEvent.EventType.INSERT to be sent to the registered
	 * DocumentListers, unless an exception is thrown. The DocumentEvent will be
	 * delivered by calling the insertUpdate method on the DocumentListener. The
	 * offset and length of the generated DocumentEvent will indicate what change
	 * was actually made to the Document.
	 * <p>
	 * If the Document structure changed as result of the insertion, the details
	 * of what Elements were inserted and removed in response to the change will
	 * also be contained in the generated DocumentEvent. It is up to the
	 * implementation of a Document to decide how the structure should change in
	 * response to an insertion.
	 * <p>
	 * If the Document supports undo/redo, an UndoableEditEvent will also be
	 * generated.
	 *
	 * @param startingOffset The offset into the document to insert the content
	 * >= 0. All positions that track change at or after the given location will
	 * move
	 * @param text The string to insert
	 * @param attributeSet The attributes to associate with the inserted content.
	 * This may be null if there are no attributes
	 * @exception BadLocationException the given insert position is not a valid
	 * position within the document
	 */
	public void insertString(int startingOffset,
									 String text,
									 AttributeSet attributeSet) throws BadLocationException
	{
		if (regularExpression != null)
		{
			StringBuffer upcomingString = new StringBuffer(getText(0, getLength()));
			upcomingString.insert(startingOffset, text);
			String upcoming = upcomingString.toString();

			Matcher matcher = regularExpression.matcher(upcoming);

			if (!matcher.matches())
				return;
		}

		super.insertString(startingOffset, text, attributeSet);
	}

	/**
	 * Removes a portion of the content of the document. This will cause a
	 * DocumentEvent of type DocumentEvent.EventType.REMOVE to be sent to the
	 * registered DocumentListeners, unless an exception is thrown. The
	 * notification will be sent to the listeners by calling the removeUpdate
	 * method on the DocumentListeners.
	 * <p>
	 * To ensure reasonable behavior in the face of concurrency, the event is
	 * dispatched after the mutation has occurred. This means that by the time a
	 * notification of removal is dispatched, the document has already been
	 * updated and any marks created by createPosition have already changed.
	 * <p>
	 * For a removal, the end of the removal range is collapsed down to the start
	 * of the range, and any marks in the removal range are collapsed down to the
	 * start of the range.
	 * <p>
	 * If the Document structure changed as result of the removal, the details of
	 * what Elements were inserted and removed in response to the change will also
	 * be contained in the generated DocumentEvent. It is up to the implementation
	 * of a Document to decide how the structure should change in response to a
	 * remove.
	 * <p>
	 * If the Document supports undo/redo, an UndoableEditEvent will also be
	 * generated.
	 * 
	 * @param startingOffset The offset from the begining >= 0
	 * @param length The number of characters to remove >= 0
	 * @exception BadLocationException some portion of the removal range was not a
	 * valid part of the document. The location in the exception is the first bad
	 * position encountered
	 */ 
	public void remove(int startingOffset, int length) throws BadLocationException
	{
		if (regularExpression != null)
		{
			StringBuffer upcomingString = new StringBuffer(getText(0, getLength()));
			upcomingString = upcomingString.delete(startingOffset, startingOffset + length);
			String upcoming = upcomingString.toString();

			Matcher matcher = regularExpression.matcher(upcoming);

			if (!matcher.matches())
				return;
		}

		super.remove(startingOffset, length);
	}
}
