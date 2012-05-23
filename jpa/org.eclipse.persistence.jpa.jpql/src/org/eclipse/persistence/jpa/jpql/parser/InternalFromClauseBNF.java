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

/**
 * The query BNF for the <code><b>FROM</b></code> declaration, basically what follows the
 * <code><b>FROM</b></code> identifier.
 * <p>
 * JPA:
 * <div nowrap><b>BNF:</b> <code>from_clause ::= FROM identification_variable_declaration {, {identification_variable_declaration | collection_member_declaration}}*</code>
 * <p>
 * EclipseLink 2.4:
 * <div nowrap><b>BNF:</b> <code>from_clause ::= FROM identification_variable_declaration {, {identification_variable_declaration |
 *                                                                                            collection_member_declaration |
 *                                                                                            (subquery) |
 *                                                                                            table_declaration }}*</code>
 * <p>
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class InternalFromClauseBNF extends JPQLQueryBNF {

	/**
	 * The unique identifier of this BNF rule.
	 */
	public static final String ID = "internal_from_clause";

	/**
	 * Creates a new <code>InternalFromClauseBNF</code>.
	 */
	public InternalFromClauseBNF() {
		super(ID);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initialize() {
		super.initialize();
		setHandleAggregate(true); // To support invalid queries
		setHandleCollection(true);
		setFallbackBNFId(IdentificationVariableDeclarationBNF.ID);
		registerExpressionFactory(CollectionMemberDeclarationBNF.ID);
		registerChild(IdentificationVariableDeclarationBNF.ID);
	}
}