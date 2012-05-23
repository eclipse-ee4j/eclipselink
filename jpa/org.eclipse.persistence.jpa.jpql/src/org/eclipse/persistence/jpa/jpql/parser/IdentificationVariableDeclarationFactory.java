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
 * This <code>IdentificationVariableDeclarationFactory</code> handles parsing the JPQL fragment
 * within the <code><b>FROM</b></code> clause.
 *
 * @see IdentificationVariableDeclaration
 * @see CollectionMemberDeclaration
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class IdentificationVariableDeclarationFactory extends ExpressionFactory {

	/**
	 * The unique identifier of this {@link IdentificationVariableDeclarationFactory}.
	 */
	public static final String ID = "identification-variable-declaration";

	/**
	 * Creates a new <code>IdentificationVariableDeclarationFactory</code>.
	 */
	public IdentificationVariableDeclarationFactory() {
		super(ID);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected AbstractExpression buildExpression(AbstractExpression parent,
	                                             WordParser wordParser,
	                                             String word,
	                                             JPQLQueryBNF queryBNF,
	                                             AbstractExpression expression,
	                                             boolean tolerant) {

		if (word.equalsIgnoreCase(Expression.IN)) {
			expression = new CollectionMemberDeclaration(parent);
		}
		else {
			expression = new IdentificationVariableDeclaration(parent);
		}

		expression.parse(wordParser, tolerant);
		return expression;
	}
}