/*******************************************************************************
 * Copyright (c) 2012 Oracle. All rights reserved.
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

import java.util.List;
import org.eclipse.persistence.jpa.jpql.ExpressionTools;
import org.eclipse.persistence.jpa.jpql.WordParser;

/**
 * This expression adds support to call native database functions.
 * <p>
 * New to JPA 2.1.
 *
 * <div nowrap><b>BNF:</b> <code>func_expression ::= &lt;identifier&gt;('function_name' {, func_item}*)</code><p>
 *
 * @version 2.4
 * @since 2.4
 * @author James
 */
public final class FunctionExpression extends AbstractSingleEncapsulatedExpression {

	/**
	 * The name of the SQL function.
	 */
	private String functionName;

	/**
	 * Determines whether the comma separating the function name and the first expression.
	 */
	private boolean hasComma;

	/**
	 * Determines whether a whitespace is following the comma.
	 */
	private boolean hasSpaceAfterComma;

	/**
	 * Creates a new <code>FuncExpression</code>.
	 *
	 * @param parent The parent of this expression
	 * @param identifier The JPQL identifier
	 */
	public FunctionExpression(AbstractExpression parent, String identifier) {
		super(parent);
		setText(identifier);
	}

	/**
	 * {@inheritDoc}
	 */
	public void accept(ExpressionVisitor visitor) {
		visitor.visit(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void addOrderedEncapsulatedExpressionTo(List<Expression> children) {

		children.add(buildStringExpression(functionName));

		if (hasComma) {
			children.add(buildStringExpression(COMMA));
		}

		if (hasSpaceAfterComma) {
			children.add(buildStringExpression(SPACE));
		}

		super.addOrderedEncapsulatedExpressionTo(children);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String encapsulatedExpressionBNF() {
		return FunctionItemBNF.ID;
	}

	/**
	 * Returns the name of the SQL function.
	 *
	 * @return The name of the SQL function
	 */
	public String getFunctionName() {
		return functionName;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public JPQLQueryBNF getQueryBNF() {
		return getQueryBNF(FunctionExpressionBNF.ID);
	}

	/**
	 * Returns the name of the SQL function.
	 *
	 * @return The name of the SQL function
	 */
	public String getUnquotedFunctionName() {
		return ExpressionTools.unquote(functionName);
	}

	/**
	 * Determines whether the comma was parsed after the function name.
	 *
	 * @return <code>true</code> if a comma was parsed after the function name and the first
	 * expression; <code>false</code> otherwise
	 */
	public boolean hasComma() {
		return hasComma;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean hasEncapsulatedExpression() {
		return hasFunctionName() || hasComma || super.hasEncapsulatedExpression();
	}

	/**
	 * Determines whether the function name was parsed.
	 *
	 * @return <code>true</code> if the function name was parsed; <code>false</code> otherwise
	 */
	public boolean hasFunctionName() {
		return ExpressionTools.stringIsNotEmpty(functionName);
	}

	/**
	 * Determines whether a whitespace was parsed after the comma.
	 *
	 * @return <code>true</code> if there was a whitespace after the comma; <code>false</code> otherwise
	 */
	public boolean hasSpaceAfterComma() {
		return hasSpaceAfterComma;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void parseEncapsulatedExpression(WordParser wordParser, boolean tolerant) {

		int count = 0;

		// Parse the function name outside of a CollectionExpression so we can retrieve it
		// with getFunctionName()
		if (wordParser.startsWith(SINGLE_QUOTE)) {
			functionName = ExpressionTools.parseLiteral(wordParser);
			wordParser.moveForward(functionName);
			count = wordParser.skipLeadingWhitespace();
		}
		else {
			functionName = ExpressionTools.EMPTY_STRING;
		}

		hasComma = wordParser.startsWith(COMMA);

		if (hasComma) {
			wordParser.moveForward(1);
			count = wordParser.skipLeadingWhitespace();
			hasSpaceAfterComma = count > 0;
		}

		// Parse the arguments
		if (!wordParser.isTail() &&
		     wordParser.character() != RIGHT_PARENTHESIS) {

			super.parseEncapsulatedExpression(wordParser, tolerant);
		}

		// A space was parsed but no expression, let the space belong to the parent
		if (count > 0           &&
		   !hasExpression()     &&
		   !wordParser.isTail() &&
		    wordParser.character() != RIGHT_PARENTHESIS) {

			hasSpaceAfterComma = false;
			wordParser.moveBackward(count);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String parseIdentifier(WordParser wordParser) {
		return getText();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void toParsedTextEncapsulatedExpression(StringBuilder writer, boolean actual) {

		writer.append(functionName);

		if (hasComma) {
			writer.append(COMMA);
		}

		if (hasSpaceAfterComma) {
			writer.append(SPACE);
		}

		super.toParsedTextEncapsulatedExpression(writer, actual);
	}
}