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

/**
 * The query BNF for an enum primary expression.
 *
 * <div nowrap><b>BNF:</b> <code>enum_primary ::= state_field_path_expression |
 * enum_literal | input_parameter | case_expression</code><p>
 *
 * @version 2.3
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class EnumPrimaryBNF extends JPQLQueryBNF {

	/**
	 * The unique identifier of this BNF rule.
	 */
	public static final String ID = "enum_primary";

	/**
	 * Creates a new <code>EnumPrimaryBNF</code>.
	 */
	EnumPrimaryBNF() {
		super(ID);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void initialize() {
		super.initialize();

		registerChild(StateFieldPathExpressionBNF.ID);
		registerChild(EnumLiteralBNF.ID);
		registerChild(InputParameterBNF.ID);
		registerChild(CaseExpressionBNF.ID);
	}
}