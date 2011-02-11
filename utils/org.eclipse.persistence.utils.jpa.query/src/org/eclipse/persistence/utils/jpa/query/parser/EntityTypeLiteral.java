/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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

/**
 * This {@link Expression} wraps the name of an entity type.
 *
 * @version 11.2.0
 * @since 11.2.0
 * @author Pascal Filion
 */
public final class EntityTypeLiteral extends AbstractExpression
{
	/**
	 * Creates a new <code>EntityTypeLiteral</code>.
	 *
	 * @param parent The parent of this expression
	 * @param entityTypeName
	 */
	EntityTypeLiteral(AbstractExpression parent, String entityTypeName)
	{
		super(parent, entityTypeName);
	}

	/**
	 * Returns the name of the entity type.
	 *
	 * @return The name of the entity that was parsed
	 */
	public String getEntityTypeName()
	{
		return getText();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void accept(ExpressionVisitor visitor)
	{
		visitor.visit(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	JPQLQueryBNF getQueryBNF()
	{
		return queryBNF(EntityTypeExpressionBNF.ID);
	}

	/** {@inheritDoc}
	 */
	@Override
	void parse(WordParser wordParser, boolean tolerant)
	{
		wordParser.moveForward(getText());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void toParsedText(StringBuilder writer)
	{
		writer.append(getText());
	}
}