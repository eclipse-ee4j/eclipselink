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
package org.eclipse.persistence.jpa.jpql.parser;

import java.util.Collection;
import java.util.List;
import org.eclipse.persistence.jpa.jpql.ExpressionTools;
import org.eclipse.persistence.jpa.jpql.WordParser;

/**
 * <div nowrap><b>BNF:</b> <code>general_case_expression ::= CASE when_clause {when_clause}* ELSE scalar_expression END</code>
 * or
 * <div nowrap><b>BNF:</b> <code>simple_case_expression ::= CASE case_operand simple_when_clause {simple_when_clause}* ELSE scalar_expression END</code><p>
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
public final class CaseExpression extends AbstractExpression {

	/**
	 * The actual <b>CASE</b> identifier found in the string representation of the JPQL query.
	 */
	private String caseIdentifier;

	/**
	 * The {@link Expression} representing the case operand.
	 */
	private AbstractExpression caseOperand;

	/**
	 * The {@link Expression} representing the <b>ELSE</b> expression.
	 */
	private AbstractExpression elseExpression;

	/**
	 * The actual <b>ELSE</b> identifier found in the string representation of the JPQL query.
	 */
	private String elseIdentifier;

	/**
	 * The actual <b>END</b> identifier found in the string representation of the JPQL query.
	 */
	private String endIdentifier;

	/**
	 * Determines whether the identifier <b>ELSE</b> was parsed.
	 */
	private boolean hasElse;

	/**
	 * Determines whether the identifier <b>END</b> was parsed.
	 */
	private boolean hasEnd;

	/**
	 * Determines whether a whitespace was parsed after <b>CASE</b>.
	 */
	private boolean hasSpaceAfterCase;

	/**
	 * Determines whether a whitespace was parsed after the case operand.
	 */
	private boolean hasSpaceAfterCaseOperand;

	/**
	 * Determines whether a whitespace was parsed after <b>ELSE</b>.
	 */
	private boolean hasSpaceAfterElse;

	/**
	 * Determines whether a whitespace was parsed after the <b>BETWEEN</b> expression.
	 */
	private boolean hasSpaceAfterElseExpression;

	/**
	 * Determines whether a whitespace was parsed after <b>WHEN</b> clause.
	 */
	private boolean hasSpaceAfterWhenClauses;

	/**
	 * Used to determine how to check if the parsing is complete.
	 */
	private ParsingType parsingType;

	/**
	 * The {@link Expression} representing the <b>WHEN</b> clauses.
	 */
	private AbstractExpression whenClauses;

	/**
	 * Creates a new <code>CaseExpression</code>.
	 *
	 * @param parent The parent of this expression
	 */
	public CaseExpression(AbstractExpression parent) {
		super(parent, CASE);
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
	public void acceptChildren(ExpressionVisitor visitor) {
		getCaseOperand().accept(visitor);
		getWhenClauses().accept(visitor);
		getElseExpression().accept(visitor);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void addChildrenTo(Collection<Expression> children) {
		children.add(getCaseOperand());
		children.add(getWhenClauses());
		children.add(getElseExpression());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void addOrderedChildrenTo(List<StringExpression> children) {

		// 'CASE'
		children.add(buildStringExpression(CASE));

		if (hasSpaceAfterCase) {
			children.add(buildStringExpression(SPACE));
		}

		// Case operand
		if (caseOperand != null) {
			children.add(caseOperand);
		}

		if (hasSpaceAfterCaseOperand) {
			children.add(buildStringExpression(SPACE));
		}

		// WHEN clauses
		if (whenClauses != null) {
			children.add(whenClauses);
		}

		if (hasSpaceAfterWhenClauses) {
			children.add(buildStringExpression(SPACE));
		}

		// 'ELSE'
		if (hasElse) {
			children.add(buildStringExpression(ELSE));
		}

		if (hasSpaceAfterElse) {
			children.add(buildStringExpression(SPACE));
		}

		// Else expression
		if (elseExpression != null) {
			children.add(elseExpression);
		}

		if (hasSpaceAfterElseExpression) {
			children.add(buildStringExpression(SPACE));
		}

		// 'END'
		if (hasEnd) {
			children.add(buildStringExpression(END));
		}
	}

	/**
	 * Returns the actual <b>CASE</b> identifier found in the string representation of the JPQL
	 * query, which has the actual case that was used.
	 *
	 * @return The <b>CASE</b> identifier that was actually parsed
	 */
	public String getActualCaseIdentifier() {
		return caseIdentifier;
	}

	/**
	 * Returns the actual <b>ELSE</b> identifier found in the string representation of the JPQL
	 * query, which has the actual case that was used.
	 *
	 * @return The <b>ELSE</b> identifier that was actually parsed, or an empty string if it was not
	 * parsed
	 */
	public String getActualElseIdentifier() {
		return (elseIdentifier != null) ? elseIdentifier : ExpressionTools.EMPTY_STRING;
	}

	/**
	 * Returns the actual <b>END</b> identifier found in the string representation of the JPQL query,
	 * which has the actual case that was used.
	 *
	 * @return The <b>END</b> identifier that was actually parsed, or an empty string if it was not
	 * parsed
	 */
	public String getActualEndIdentifier() {
		return (endIdentifier != null) ? endIdentifier : ExpressionTools.EMPTY_STRING;
	}

	/**
	 * Returns the {@link Expression} that represents the <b>CASE</b> operand.
	 *
	 * @return The expression that was parsed representing the <b>CASE</b> operand
	 */
	public Expression getCaseOperand() {
		if (caseOperand == null) {
			caseOperand = buildNullExpression();
		}
		return caseOperand;
	}

	/**
	 * Returns the {@link Expression} that represents the <b>ELSE</b> operand.
	 *
	 * @return The expression that was parsed representing the <b>ELSE</b> operand
	 */
	public AbstractExpression getElseExpression() {
		if (elseExpression == null) {
			elseExpression = buildNullExpression();
		}
		return elseExpression;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public JPQLQueryBNF getQueryBNF() {
		return getQueryBNF(GeneralCaseExpressionBNF.ID);
	}

	/**
	 * Returns the {@link Expression} that represents the <b>WHEN</b> clauses.
	 *
	 * @return The expression that was parsed representing the <b>WHEN</b> clauses
	 */
	public AbstractExpression getWhenClauses() {
		if (whenClauses == null) {
			whenClauses = buildNullExpression();
		}
		return whenClauses;
	}

	/**
	 * Determines whether the <b>CASE</b> operand was parsed.
	 *
	 * @return <code>true</code> if the <b>CASE</b> operand was parsed; <code>false</code> otherwise
	 */
	public boolean hasCaseOperand() {
		return caseOperand != null &&
		      !caseOperand.isNull();
	}

	/**
	 * Determines whether the identifier <b>ELSE</b> was parsed.
	 *
	 * @return <code>true</code> if the identifier <b>ELSE</b> was parsed; <code>false</code>
	 * otherwise
	 */
	public boolean hasElse() {
		return hasElse;
	}

	/**
	 * Determines whether the <b>ELSE</b> expression was parsed.
	 *
	 * @return <code>true</code> if the <b>ELSE</b> expression was parsed; <code>false</code>
	 * otherwise
	 */
	public boolean hasElseExpression() {
		return elseExpression != null &&
		      !elseExpression.isNull();
	}

	/**
	 * Determines whether the identifier <b>END</b> was parsed.
	 *
	 * @return <code>true</code> if the identifier <b>END</b> was parsed; <code>false</code> otherwise
	 */
	public boolean hasEnd() {
		return hasEnd;
	}

	/**
	 * Determines whether a whitespace was parsed after <b>CASE</b>.
	 *
	 * @return <code>true</code> if there was a whitespace after <b>CASE</b>; <code>false</code>
	 * otherwise
	 */
	public boolean hasSpaceAfterCase() {
		return hasSpaceAfterCase;
	}

	/**
	 * Determines whether a whitespace was parsed after the case operand.
	 *
	 * @return <code>true</code> if there was a whitespace after the case operand; <code>false</code>
	 * otherwise
	 */
	public boolean hasSpaceAfterCaseOperand() {
		return hasSpaceAfterCaseOperand;
	}

	/**
	 * Determines whether a whitespace was parsed after <b>ELSE</b>.
	 *
	 * @return <code>true</code> if there was a whitespace after <b>ELSE</b>; <code>false</code>
	 * otherwise
	 */
	public boolean hasSpaceAfterElse() {
		return hasSpaceAfterElse;
	}

	/**
	 * Determines whether a whitespace was parsed after the else expression.
	 *
	 * @return <code>true</code> if there was a whitespace after the else expression;
	 * <code>false</code> otherwise
	 */
	public boolean hasSpaceAfterElseExpression() {
		return hasSpaceAfterElseExpression;
	}

	/**
	 * Determines whether a whitespace was parsed after the <b>WHEN</b> clause.
	 *
	 * @return <code>true</code> if there was a whitespace after <the b>WHEN</b>
	 * clause; <code>false</code> otherwise
	 */
	public boolean hasSpaceAfterWhenClauses() {
		return hasSpaceAfterWhenClauses;
	}

	/**
	 * Determines whether the <b>WHEN</b> clauses were parsed.
	 *
	 * @return <code>true</code> if the <b>WHEN</b> clauses were parsed;
	 * <code>false</code> otherwise
	 */
	public boolean hasWhenClauses() {
		return whenClauses != null &&
		      !whenClauses.isNull();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean isParsingComplete(WordParser wordParser, String word, Expression expression) {

		// 'CASE'
		if (parsingType == ParsingType.CASE) {
			return word.equalsIgnoreCase(WHEN) ||
			       word.equalsIgnoreCase(ELSE) ||
			       word.equalsIgnoreCase(END)  ||
			       super.isParsingComplete(wordParser, word, expression);
		}

		// 'WHEN'
		if (parsingType == ParsingType.WHEN) {
			return word.equalsIgnoreCase(ELSE) ||
			       word.equalsIgnoreCase(END)  ||
			       super.isParsingComplete(wordParser, word, expression);
		}

		// 'ELSE'
		return word.equalsIgnoreCase(END) ||
		       super.isParsingComplete(wordParser, word, expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void parse(WordParser wordParser, boolean tolerant) {

		// Parse 'CASE'
		caseIdentifier = wordParser.moveForward(CASE);

		hasSpaceAfterCase = wordParser.skipLeadingWhitespace() > 0;

		// Parse case operand
		parsingType = ParsingType.CASE;

		if (!wordParser.startsWithIdentifier(WHEN)) {
			caseOperand = parse(
				wordParser,
				getQueryBNF(CaseOperandBNF.ID),
				tolerant
			);
		}

		hasSpaceAfterCaseOperand = wordParser.skipLeadingWhitespace() > 0;

		// Parse the WHEN clauses
		parsingType = ParsingType.WHEN;

		whenClauses = parse(
			wordParser,
			getQueryBNF(WhenClauseBNF.ID),
			tolerant
		);

		hasSpaceAfterWhenClauses = wordParser.skipLeadingWhitespace() > 0;

		// Parse 'ELSE'
		hasElse = wordParser.startsWithIdentifier(ELSE);

		if (hasElse) {
			elseIdentifier = wordParser.moveForward(ELSE);
		}

		hasSpaceAfterElse = wordParser.skipLeadingWhitespace() > 0;

		// Parse the ELSE expression
		parsingType = ParsingType.ELSE;

		elseExpression = parse(
			wordParser,
			getQueryBNF(ElseExpressionBNF.ID),
			tolerant
		);

		hasSpaceAfterElseExpression = wordParser.skipLeadingWhitespace() > 0;

		// Parse 'END'
		hasEnd = wordParser.startsWithIdentifier(END);

		if (hasEnd) {
			endIdentifier = wordParser.moveForward(END);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void toParsedText(StringBuilder writer, boolean actual) {

		// 'CASE'
		writer.append(actual ? caseIdentifier : CASE);

		if (hasSpaceAfterCase) {
			writer.append(SPACE);
		}

		// Case Operand
		if (caseOperand != null) {
			caseOperand.toParsedText(writer, actual);
		}

		if (hasSpaceAfterCaseOperand) {
			writer.append(SPACE);
		}

		// When clauses
		if (whenClauses != null) {
			whenClauses.toParsedText(writer, actual);
		}

		if (hasSpaceAfterWhenClauses) {
			writer.append(SPACE);
		}

		// 'ELSE'
		if (hasElse) {
			writer.append(actual ? elseIdentifier : ELSE);
		}

		if (hasSpaceAfterElse) {
			writer.append(SPACE);
		}

		// Else expression
		if (elseExpression != null) {
			elseExpression.toParsedText(writer, actual);
		}

		if (hasSpaceAfterElseExpression) {
			writer.append(SPACE);
		}

		// 'END'
		if (hasEnd) {
			writer.append(actual ? endIdentifier : END);
		}
	}

	/**
	 * A enumeration used to determine how {@link CaseExpression#isParsingComplete(WordParser, String)}
	 * should behaves.
	 */
	private enum ParsingType {
		CASE,
		ELSE,
		WHEN
	}
}