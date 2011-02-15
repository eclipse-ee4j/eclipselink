/*******************************************************************************
 * Copyright (c) 2006, 2011 Oracle. All rights reserved.
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

/**
 * An identification variable followed by the navigation operator (.) and a
 * state field or association field is a path expression. The type of the path
 * expression is the type computed as the result of navigation; that is, the
 * type of the state field or association field to which the expression
 * navigates.
 *
 * @see CollectionValuedPathExpression
 * @see IdentificationVariable
 *
 * @version 11.2.0
 * @since 11.2.0
 * @author Pascal Filion
 */
public abstract class AbstractPathExpression extends AbstractExpression
{
	/**
	 * Determines whether the path ends with a dot or not.
	 */
	private Boolean endsWithDot;

	/**
	 * The identification variable that starts the path expression, which can be
	 * a sample {@link IdentificationVariable identification variable}, an
	 * {@link EntryExpression entry expression}, a {@link ValueExpression value
	 * expression} or a {@link KeyExpression key expression}.
	 */
	private AbstractExpression identificationVariable;

	/**
	 * The state field path in a ordered list of string segments.
	 */
	private List<String> paths;

	/**
	 * Determines whether the path starts with a dot or not.
	 */
	private Boolean startsWithDot;

	/**
	 * Creates a new <code>AbstractPathExpression</code>.
	 *
	 * @param parent The parent of this expression
	 * @param expression The identification variable that was already parsed,
	 * which means the beginning of the parsing should start with a dot
	 */
	AbstractPathExpression(AbstractExpression parent,
	                       AbstractExpression expression)
	{
		super(parent);

		this.identificationVariable = expression;
		this.identificationVariable.setParent(this);
	}

	/**
	 * Creates a new <code>AbstractPathExpression</code>.
	 *
	 * @param parent The parent of this expression
	 * @param paths The path expression
	 */
	AbstractPathExpression(AbstractExpression parent, String paths)
	{
		super(parent, paths);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void addChildrenTo(Collection<Expression> children)
	{
		children.add(getIdentificationVariable());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	final void addOrderedChildrenTo(List<StringExpression> children)
	{
		if (identificationVariable != null)
		{
			children.add(identificationVariable);
		}

		children.add(buildStringExpression(getText()));
	}

	private void checkPaths()
	{
		if (paths == null)
		{
			paths = new ArrayList<String>();

			String text = getText();
			StringBuilder word = new StringBuilder();

			// Extract each path from the word
			for (int index = 0, count = text.length(); index < count; index++)
			{
				char character = text.charAt(index);

				// Append the character and continue to the next character
				if (character != DOT)
				{
					word.append(character);
					continue;
				}

				if (identificationVariable == null)
				{
					if (word.length() == 0)
					{
						identificationVariable = buildNullExpression();
					}
					else
					{
						identificationVariable = new IdentificationVariable(this, word.toString());
					}
				}

				paths.add(word.toString());
				word.delete(0, word.length());
			}

			if (identificationVariable == null)
			{
				identificationVariable = new IdentificationVariable(this, word.toString());
			}

			if (word.length() > 0)
			{
				paths.add(word.toString());
			}
		}
	}

	/**
	 * Determines whether the path ends with a dot or not.
	 *
	 * @return <code>true</code> if the path ends with a dot; <code>false</code>
	 * otherwise
	 */
	public final boolean endsWithDot()
	{
		if (endsWithDot == null)
		{
			String text = getText();
			endsWithDot = text.charAt(text.length() - 1) == DOT;
		}

		return endsWithDot;
	}

	/**
	 * Returns the identification variable that starts the path expression, which
	 * can be a sample {@link IdentificationVariable identification variable}, an
	 * {@link EntryExpression entry expression}, a {@link ValueExpression value
	 * expression} or a {@link KeyExpression key expression}.
	 *
	 * @return The root of the path expression
	 */
	public final Expression getIdentificationVariable()
	{
		checkPaths();
		return identificationVariable;
	}

	/**
	 * Returns the specified segment of the state field path.
	 *
	 * @param index The 0-based segment index
	 * @return The specified segment
	 */
	public final String getPath(int index)
	{
		checkPaths();
		return paths.get(index);
	}

	/**
	 * Determines whether the identification variable was parsed.
	 *
	 * @return <code>true</code> the identification variable was parsed;
	 * <code>false</code> otherwise
	 */
	public final boolean hasIdentificationVariable()
	{
		checkPaths();
		return !identificationVariable.isNull();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	final void parse(WordParser wordParser, boolean tolerant)
	{
		String word;

		// Parse the state field path expression after the identification variable
		if (identificationVariable != null)
		{
			word = wordParser.word();
			setText(word);
		}
		else
		{
			word = getText();
		}

		// WordParser happens in a unique case
		if (wordParser != null)
		{
			wordParser.moveForward(word);
		}
	}

	/**
	 * Returns the segments in the state field path in order.
	 *
	 * @return An <code>Iterator</code> over the segments of the state field path
	 */
	public final ListIterator<String> paths()
	{
		checkPaths();
		return Collections.unmodifiableList(paths).listIterator();
	}

	/**
	 * Returns the number of segments in the state field path.
	 *
	 * @return The number of segments
	 */
	public final int pathSize()
	{
		checkPaths();
		return paths.size();
	}

	/**
	 * Determines whether the path starts with a dot or not.
	 *
	 * @return <code>true</code> if the path starts with a dot; <code>false</code>
	 * otherwise
	 */
	public final boolean startsWithDot()
	{
		if (startsWithDot == null)
		{
			startsWithDot = getIdentificationVariable().toParsedText().length() == 0;
		}

		return startsWithDot;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	final void toParsedText(StringBuilder writer)
	{
		checkPaths();

		for (int index = 0, count = paths.size(); index < count; index++)
		{
			writer.append(paths.get(index));

			if (index < count - 1)
			{
				writer.append(DOT);
			}
		}

		if (endsWithDot())
		{
			writer.append(DOT);
		}
	}
}