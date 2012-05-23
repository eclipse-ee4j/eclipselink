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

import org.eclipse.persistence.jpa.jpql.WordParser;

/**
 * The </b>AND</b> logical operator chains multiple criteria together. A valid operand of an
 * </b>AND</b> operator must be one of: </b>TRUE</b>, </b>FALSE</b>, and </b>NULL</b>. The
 * </b>AND</b> operator has a higher precedence than the </b>OR</b> operator.
 * <p>
 * </b>NULL</b> represents unknown. Therefore, if one operand is </b>NULL</b> and the other operand
 * is </b>FALSE</b> the result is </b>FALSE</b>, because one </b>FALSE</b> operand is sufficient for
 * a </b>FALSE</b> result. If one operand is </b>NULL</b> and the other operand is either
 * </b>TRUE</b> or </b>NULL</b>, the result is </b>NULL</b> (unknown).
 * <p>
 * The following table shows how the <b>AND</b> operator is evaluated based on its two operands:
 * <p>
 * <code>
 * <style type="text/css">td {border-top:solid 1px; border-bottom:solid 0px; border-left:solid 1px; border-right:solid 0px}</style>
 * <table cellspacing="0" cellpadding="2" border="1" width="250" style="border:1px outset darkgrey;">
 * <tr><td>            </td><td style="border-top="0""><b>TRUE</b></td><td style="border-top="0""><b>FALSE</b><td  style="border-top="0""><b>NULL</b></td></tr>
 * <tr><td style="border-left="0""><b>TRUE</b> </td><td>   TRUE    </td><td>   FALSE    <td>   NULL    </td></tr>
 * <tr><td style="border-left="0""><b>FALSE</b></td><td>   FALSE   </td><td>   FALSE    <td>   FALSE   </td></tr>
 * <tr><td style="border-left="0""><b>NULL</b> </td><td>   NULL    </td><td>   FALSE    <td>   NULL    </td></tr>
 * </table>
 * </code>
 *
 * <div nowrap><b>BNF:</b> <code>conditional_term ::= conditional_term AND conditional_factor</code><p>
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
public final class AndExpression extends LogicalExpression {

	/**
	 * Creates a new <code>AndExpression</code>.
	 *
	 * @param parent The parent of this expression
	 */
	public AndExpression(AbstractExpression parent) {
		super(parent, AND);
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
	protected boolean isParsingComplete(WordParser wordParser, String word, Expression expression) {
		return word.equalsIgnoreCase(AND) ||
		       word.equalsIgnoreCase(OR)  ||
		       super.isParsingComplete(wordParser, word, expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String rightExpressionBNF() {
		return ConditionalFactorBNF.ID;
	}
}