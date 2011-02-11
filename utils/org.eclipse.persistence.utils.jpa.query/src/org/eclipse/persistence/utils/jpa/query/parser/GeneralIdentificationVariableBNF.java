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
 * The query BNF for a general identification variable expression.
 *
 * <div nowrap><b>BNF:</b> <code>general_identification_variable ::= identification_variable |
 * KEY(identification_variable) | VALUE(identification_variable)</code><p>
 *
 * @version 11.2.0
 * @since 11.2.0
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
final class GeneralIdentificationVariableBNF extends JPQLQueryBNF
{
	/**
	 * The unique identifier of this BNF rule.
	 */
	static final String ID = "general_identification_variable";

	/**
	 * Creates a new <code>GeneralIdentificationVariableBNF</code>.
	 */
	GeneralIdentificationVariableBNF()
	{
		super(ID);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	String getFallbackBNFId()
	{
		return PreLiteralExpressionBNF.ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void initialize()
	{
		registerExpressionFactory(IdentificationVariableFactory.ID);
		registerExpressionFactory(KeyExpressionFactory.ID);
		registerExpressionFactory(ValueExpressionFactory.ID);
	}
}