/*******************************************************************************
 * Copyright (c) 2006, 2012 Oracle. All rights reserved.
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
import org.eclipse.persistence.jpa.jpql.Assert;
import org.eclipse.persistence.jpa.jpql.WordParser;

/**
 * This expression handles parsing a JPQL identifier followed by an expression encapsulated within
 * parenthesis.
 * <p>
 * <div nowrap><b>BNF:</b> <code>expression ::= &lt;identifier&gt;(expression)</code><p>
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public abstract class AbstractSingleEncapsulatedExpression extends AbstractEncapsulatedExpression {

	/**
	 * The sub-expression encapsulated within parenthesis.
	 */
	private AbstractExpression expression;

	/**
	 * Creates a new <code>EncapsulatedExpression</code>.
	 *
	 * @param parent The parent of this expression
	 */
	protected AbstractSingleEncapsulatedExpression(AbstractExpression parent) {
		super(parent);
	}

	/**
	 * {@inheritDoc}
	 */
	public void acceptChildren(ExpressionVisitor visitor) {
		getExpression().accept(visitor);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected final void addChildrenTo(Collection<Expression> children) {
		children.add(getExpression());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void addOrderedEncapsulatedExpressionTo(List<Expression> children) {
		if (expression != null) {
			children.add(expression);
		}
	}

	/**
	 * Returns the BNF used to parse the encapsulated expression.
	 *
	 * @return The BNF used to parse the encapsulated expression
	 */
	public abstract String encapsulatedExpressionBNF();

	/**
	 * Returns the {@link Expression} that is encapsulated within parenthesis.
	 *
	 * @return The {@link Expression} that is encapsulated within parenthesis
	 */
	public final Expression getExpression() {
		if (expression == null) {
			expression = buildNullExpression();
		}
		return expression;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean hasEncapsulatedExpression() {
		return hasExpression();
	}

	/**
	 * Determines whether the encapsulated expression of the query was parsed.
	 *
	 * @return <code>true</code> if the encapsulated expression was parsed; <code>false</code> if it
	 * was not parsed
	 */
	public final boolean hasExpression() {
		return expression != null &&
		      !expression.isNull();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void parseEncapsulatedExpression(WordParser wordParser,
	                                           int whitespaceCount,
	                                           boolean tolerant) {

		expression = parse(wordParser, encapsulatedExpressionBNF(), tolerant);
	}

	/**
	 * Manually sets the encapsulated {@link Expression} to become the given one.
	 *
	 * @param expression The new encapsulated {@link Expression}, which cannot be <code>null</code>
	 * @exception NullPointerException The given {@link AbstractExpression} cannot be <code>null</code>
	 */
	public final void setExpression(AbstractExpression expression) {
		Assert.isNotNull(expression, "The Expression cannot be null");
		this.expression = expression;
		this.expression.setParent(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void toParsedTextEncapsulatedExpression(StringBuilder writer, boolean actual) {
		if (expression != null) {
			expression.toParsedText(writer, actual);
		}
	}
}