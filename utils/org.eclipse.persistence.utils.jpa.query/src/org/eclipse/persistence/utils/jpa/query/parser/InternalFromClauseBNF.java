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
 * The query BNF for the from declaration.
 *
 * <div nowrap><b>BNF:</b> <code>from_clause ::= FROM identification_variable_declaration {, {identification_variable_declaration | collection_member_declaration}}*</code><p>
 *
 * @version 11.2.0
 * @since 11.2.0
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
final class InternalFromClauseBNF extends AbstractCompoundBNF
{
	/**
	 * The unique identifier of this BNF rule.
	 */
	static final String ID = "internal_from_clause";

	/**
	 * Creates a new <code>InternalFromClauseBNF</code>.
	 */
	InternalFromClauseBNF()
	{
		super(ID);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	String getFallbackBNFId()
	{
		return IdentificationVariableDeclarationBNF.ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	boolean handleAggregate()
	{
		// To support invalid queries
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	boolean handleCollection()
	{
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void initialize()
	{
		super.initialize();

		registerExpressionFactory(CollectionMemberDeclarationBNF.ID);

		registerChild(IdentificationVariableDeclarationBNF.ID);
	}
}