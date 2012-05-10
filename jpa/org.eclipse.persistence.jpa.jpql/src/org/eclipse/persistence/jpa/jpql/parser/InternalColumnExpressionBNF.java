/*******************************************************************************
 * Copyright (c) 2012 Oracle and/or its affiliates. All rights reserved.
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
 * The query BNF for the items of a <b>COLUMN</b> expression.
 *
 * <div nowrap><b>BNF:</b> <code>column_expression ::= COLUMN('function' {, [single_valued_object_path_expression | identification_variable]}+)</code><p>
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class InternalColumnExpressionBNF extends JPQLQueryBNF {

	/**
	 * The unique identifier of this BNF rule.
	 */
	public static final String ID = "column_item";

	/**
	 * Creates a new <code>InternalColumnExpressionBNF</code>.
	 */
	public InternalColumnExpressionBNF() {
		super(ID);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initialize() {
		super.initialize();
		setHandleCollection(true); // For invalid queries
		setFallbackBNFId(PreLiteralExpressionBNF.ID);
		registerChild(SingleValuedObjectPathExpressionBNF.ID);
		registerChild(StateFieldPathExpressionBNF.ID);
		registerChild(IdentificationVariableBNF.ID);
	}
}