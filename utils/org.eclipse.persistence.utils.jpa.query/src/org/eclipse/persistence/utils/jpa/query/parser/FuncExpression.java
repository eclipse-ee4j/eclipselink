/*******************************************************************************
 * Copyright (c) 2006, 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.utils.jpa.query.parser;

import java.util.List;

/**
 * This expression adds support to call native database functions.
 * <p>
 * New to EclipseLink 2.1.0.
 *
 * <div nowrap><b>BNF:</b> <code>func_expression ::= FUNC('function_name' {, func_item}*)</code><p>
 *
 * @version 11.2.0
 * @since 11.2.0
 * @author Pascal Filion
 */
public final class FuncExpression extends AbstractSingleEncapsulatedExpression {

	/**
	 * The name of the SQL function.
	 */
	private String functionName;

	/**
	 *
	 */
	private boolean hasComma;

	/**
	 *
	 */
	private boolean hasSpaceAFterComma;

	/**
	 * Creates a new <code>FuncExpression</code>.
	 *
	 * @param parent The parent of this expression
	 */
	FuncExpression(AbstractExpression parent) {
		super(parent);
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
	void addOrderedEncapsulatedExpressionTo(List<StringExpression> children) {

		if (functionName != null) {
			children.add(buildStringExpression(functionName));
		}

		if (hasComma) {
			children.add(buildStringExpression(COMMA));
		}

		if (hasSpaceAFterComma) {
			children.add(buildStringExpression(SPACE));
		}

		super.addOrderedEncapsulatedExpressionTo(children);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	JPQLQueryBNF encapsulatedExpressionBNF() {
		return queryBNF(FuncItemBNF.ID);
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
	JPQLQueryBNF getQueryBNF() {
		return queryBNF(FuncExpressionBNF.ID);
	}

	/**
	 * Returns the name of the SQL function.
	 *
	 * @return The name of the SQL function
	 */
	public String getUnquotedFunctionName() {
		return ExpressionTools.unquotedText(functionName);
	}

	public boolean hasComma() {
		return hasComma;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	boolean hasEncapsulatedExpression() {
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

	public boolean hasSpaceAFterComma() {
		return hasSpaceAFterComma;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void parseEncapsulatedExpression(WordParser wordParser, boolean tolerant) {

		// Parse the function name outside of a CollectionExpression so we can retrieve it
		// with getFunctionName()
		if (wordParser.startsWith(SINGLE_QUOTE)) {
			functionName = ExpressionTools.parseLiteral(wordParser);
			wordParser.moveForward(functionName);
			wordParser.skipLeadingWhitespace();
		}
		else {
			functionName = EMPTY_STRING;
		}

		hasComma = wordParser.startsWith(COMMA);

		if (hasComma) {
			wordParser.moveForward(1);
		}

		hasSpaceAFterComma = wordParser.skipLeadingWhitespace() > 0;

		super.parseEncapsulatedExpression(wordParser, tolerant);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	String parseIdentifier(WordParser wordParser) {
		return FUNC;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void toParsedTextEncapsulatedExpression(StringBuilder writer) {

		if (functionName != null) {
			writer.append(functionName);
		}

		if (hasComma) {
			writer.append(COMMA);
		}

		if (hasSpaceAFterComma) {
			writer.append(SPACE);
		}

		super.toParsedTextEncapsulatedExpression(writer);
	}
}