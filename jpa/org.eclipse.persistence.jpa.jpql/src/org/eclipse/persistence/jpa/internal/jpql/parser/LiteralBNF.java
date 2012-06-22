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
 * The query BNF for literals, which is based on the listing defined in section 4.6.1 of the Java
 * Specification document for JPA 2.0.
 *
 * @version 2.3
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class LiteralBNF extends JPQLQueryBNF {

	/**
	 * The unique identifier of this <code>LiteralBNF</code>.
	 */
	public static final String ID = "literal";

	/**
	 * Creates a new <code>LiteralBNF</code>.
	 */
	LiteralBNF() {
		super(ID);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void initialize() {
		super.initialize();

		registerChild(StringLiteralBNF.ID);
		registerChild(NumericLiteralBNF.ID);
		registerChild(EnumLiteralBNF.ID);
		registerChild(BooleanLiteralBNF.ID);
		registerChild(DateTimeTimestampLiteralBNF.ID);
		registerChild(EntityTypeLiteralBNF.ID);
	}
}