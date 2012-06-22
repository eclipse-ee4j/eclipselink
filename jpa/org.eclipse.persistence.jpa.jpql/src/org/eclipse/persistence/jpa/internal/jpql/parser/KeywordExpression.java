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
package org.eclipse.persistence.jpa.internal.jpql.parser;

import java.util.List;
import org.eclipse.persistence.jpa.internal.jpql.WordParser;

/**
 * The expression representing some keywords: <code>TRUE</code>, <code>FALSE</code> or <code>NULL</code>.
 *
 * @version 2.3
 * @since 2.3
 * @author Pascal Filion
 */
public final class KeywordExpression extends AbstractExpression {

	/**
	 * Creates a new <code>KeywordExpression</code>.
	 *
	 * @param parent The parent of this expression
	 */
	KeywordExpression(AbstractExpression parent) {
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
	public void acceptChildren(ExpressionVisitor visitor) {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void addOrderedChildrenTo(List<StringExpression> children) {
		children.add(buildStringExpression(getText()));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public JPQLQueryBNF getQueryBNF() {
		return queryBNF(BooleanLiteralBNF.ID);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getText() {
		return super.getText();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void parse(WordParser wordParser, boolean tolerant) {
		String word = parseIdentifier(wordParser);
		setText(word);
		wordParser.moveForward(word);
	}

	private String parseIdentifier(WordParser wordParser) {
		switch (wordParser.character()) {
			case 'T': return TRUE;
			case 'F': return FALSE;
			default:  return NULL;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void toParsedText(StringBuilder writer, boolean includeVirtual) {
		writer.append(getText());
	}
}