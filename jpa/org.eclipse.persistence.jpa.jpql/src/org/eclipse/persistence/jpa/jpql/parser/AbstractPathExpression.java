/*******************************************************************************
 * Copyright (c) 2006, 2012 Oracle and/or its affiliates. All rights reserved.
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.persistence.jpa.jpql.WordParser;
import org.eclipse.persistence.jpa.jpql.util.iterator.CloneListIterator;
import org.eclipse.persistence.jpa.jpql.util.iterator.IterableListIterator;

/**
 * An identification variable followed by the navigation operator (.) and a state field or
 * association field is a path expression. The type of the path expression is the type computed as
 * the result of navigation; that is, the type of the state field or association field to which the
 * expression navigates.
 *
 * @see CollectionValuedPathExpression
 * @see IdentificationVariable
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public abstract class AbstractPathExpression extends AbstractExpression {

	/**
	 * Determines whether the path ends with a dot or not.
	 */
	private Boolean endsWithDot;

	/**
	 * The identification variable that starts the path expression, which can be a sample {@link
	 * IdentificationVariable identification variable}, an {@link EntryExpression entry expression},
	 * a {@link ValueExpression value expression} or a {@link KeyExpression key expression}.
	 */
	private AbstractExpression identificationVariable;

	/**
	 * The state field path in a ordered list of string segments.
	 */
	private List<String> paths;

	/**
	 * The cached number of segments representing the path expression.
	 */
	private int pathSize;

	/**
	 * Determines whether the path starts with a dot or not.
	 */
	private boolean startsWithDot;

	/**
	 * Determines whether the identification variable is virtual, meaning it's not part of the query
	 * but is required for proper navigability.
	 */
	private boolean virtualIdentificationVariable;

	/**
	 * Creates a new <code>AbstractPathExpression</code>.
	 *
	 * @param parent The parent of this expression
	 * @param expression The identification variable that was already parsed, which means the
	 * beginning of the parsing should start with a dot
	 */
	protected AbstractPathExpression(AbstractExpression parent, AbstractExpression expression) {
		super(parent);
		this.pathSize = -1;
		this.identificationVariable = expression;
		this.identificationVariable.setParent(this);
	}

	/**
	 * Creates a new <code>AbstractPathExpression</code>.
	 *
	 * @param parent The parent of this expression
	 * @param expression The identification variable that was already parsed, which means the
	 * beginning of the parsing should start with a dot
	 * @param paths The path expression that is following the identification variable
	 */
	public AbstractPathExpression(AbstractExpression parent,
	                              AbstractExpression expression,
	                              String paths) {

		super(parent, paths);
		this.pathSize = -1;
		this.identificationVariable = expression;
		this.identificationVariable.setParent(this);
	}

	/**
	 * Creates a new <code>AbstractPathExpression</code>.
	 *
	 * @param parent The parent of this expression
	 * @param paths The path expression
	 */
	protected AbstractPathExpression(AbstractExpression parent, String paths) {
		super(parent, paths);
		this.pathSize = -1;
	}

	/**
	 * {@inheritDoc}
	 */
	public void acceptChildren(ExpressionVisitor visitor) {
		getIdentificationVariable().accept(visitor);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void addChildrenTo(Collection<Expression> children) {
		checkPaths();
		children.add(identificationVariable);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected final void addOrderedChildrenTo(List<Expression> children) {
		checkPaths();
		children.add(identificationVariable);
		children.add(buildStringExpression(getText()));
	}

	private void checkPaths() {

		if (paths == null) {

			paths = new ArrayList<String>();
			String text = getText();
			StringBuilder word = new StringBuilder();
			boolean hasDot = text.indexOf(DOT) > -1;

			if ((identificationVariable != null) && !virtualIdentificationVariable) {
				paths.add(identificationVariable.toParsedText());
			}

			// Extract each path from the word
			for (int index = 0, count = text.length(); index < count; index++) {
				char character = text.charAt(index);

				// Skip the first '.' so an empty path isn't added
				if ((index == 0)       &&
				    (character == DOT) &&
				    (identificationVariable != null))
				{
					continue;
				}

				// Append the character and continue to the next character
				if (character != DOT) {
					word.append(character);
					continue;
				}

				if (hasDot && (identificationVariable == null)) {
					if (word.length() == 0) {
						identificationVariable = buildNullExpression();
					}
					else {
						identificationVariable = new IdentificationVariable(this, word.toString());
					}
				}

				paths.add(word.toString());
				word.delete(0, word.length());
			}

			if (identificationVariable == null) {
				if (hasDot) {
					identificationVariable = new IdentificationVariable(this, word.toString());
				}
				else {
					identificationVariable = buildNullExpression();
				}
			}

			if (word.length() > 0) {
				paths.add(word.toString());
			}
		}
	}

	/**
	 * Determines whether the path ends with a dot or not.
	 *
	 * @return <code>true</code> if the path ends with a dot; <code>false</code> otherwise
	 */
	public final boolean endsWithDot() {
		if (endsWithDot == null) {
			String text = getText();
			endsWithDot = text.charAt(text.length() - 1) == DOT;
		}
		return endsWithDot;
	}

	/**
	 * Returns the identification variable that starts the path expression, which can be a sample
	 * identification variable, a map value, map key or map entry expression.
	 *
	 * @return The root of the path expression
	 */
	public final Expression getIdentificationVariable() {
		checkPaths();
		return identificationVariable;
	}

	/**
	 * Returns the specified segment of the state field path.
	 *
	 * @param index The 0-based segment index
	 * @return The specified segment
	 */
	public final String getPath(int index) {
		checkPaths();
		return paths.get(index);
	}

	/**
	 * Determines whether the identification variable was parsed.
	 *
	 * @return <code>true</code> the identification variable was parsed; <code>false</code> otherwise
	 */
	public final boolean hasIdentificationVariable() {
		checkPaths();
		return !identificationVariable.isNull() &&
		       !identificationVariable.isVirtual();
	}

	/**
	 * Determines whether this identification variable is virtual, meaning it's not part of the query
	 * but is required for proper navigability.
	 *
	 * @return <code>true</code> if this identification variable was virtually created to fully
	 * qualify path expression; <code>false</code> if it was parsed
	 */
	public final boolean hasVirtualIdentificationVariable() {
		return virtualIdentificationVariable;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected final void parse(WordParser wordParser, boolean tolerant) {

		String word = getText();

		if (!hasIdentificationVariable()) {
			startsWithDot = word.startsWith(".");
		}

		// A null WordParser happens in a unique case
		wordParser.moveForward(word);
	}

	/**
	 * Returns the segments in the state field path in order.
	 *
	 * @return An <code>Iterator</code> over the segments of the state field path
	 */
	public final IterableListIterator<String> paths() {
		checkPaths();
		return new CloneListIterator<String>(paths);
	}

	/**
	 * Returns the number of segments in the state field path.
	 *
	 * @return The number of segments
	 */
	public final int pathSize() {
		if (pathSize == -1) {
			checkPaths();
			pathSize = paths.size();
		}
		return pathSize;
	}

	/**
	 * Sets a virtual identification variable because the abstract schema name was parsed without
	 * one. This is valid in an <b>UPDATE</b> and <b>DELETE</b> queries.
	 *
	 * @param variableName The identification variable that was generated to identify the abstract
	 * schema name
	 */
	protected final void setVirtualIdentificationVariable(String variableName) {

		paths = null;
		virtualIdentificationVariable = true;
		identificationVariable = new IdentificationVariable(this, variableName, true);

		rebuildActualText();
		rebuildParsedText();
	}

	/**
	 * Determines whether the path starts with a dot or not.
	 *
	 * @return <code>true</code> if the path starts with a dot; <code>false</code> otherwise
	 */
	public final boolean startsWithDot() {
		return startsWithDot;
	}

	/**
	 * Returns a string representation from the given range.
	 *
	 * @param startIndex The beginning of the range to create the string representation
	 * @param stopIndex When to stop creating the string representation, which is exclusive
	 * @return The string representation of this path expression contained in the given range
	 * @since 2.4
	 */
	public String toParsedText(int startIndex, int stopIndex) {

		checkPaths();
		StringBuilder writer = new StringBuilder();

		for (int index = startIndex; index < stopIndex; index++) {
			writer.append(paths.get(index));

			if (index < stopIndex - 1) {
				writer.append(DOT);
			}
		}

		return writer.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected final void toParsedText(StringBuilder writer, boolean actual) {

		int pathSize = pathSize();

		if (startsWithDot) {
			writer.append(DOT);
		}
		else if (!virtualIdentificationVariable) {
			identificationVariable.toParsedText(writer, actual);
			if (pathSize > 1) {
				writer.append(DOT);
			}
		}

		for (int index = (virtualIdentificationVariable ? 0 : 1); index < pathSize; index++) {

			writer.append(paths.get(index));

			if (index < pathSize - 1) {
				writer.append(DOT);
			}
		}

		if (endsWithDot()) {
			writer.append(DOT);
		}
	}
}