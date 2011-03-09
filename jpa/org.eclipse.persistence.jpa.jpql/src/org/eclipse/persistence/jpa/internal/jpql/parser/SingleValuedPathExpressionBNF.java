/*******************************************************************************
 * Copyright (c) 2006, 2011 Oracle. All rights reserved.
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
 * The query BNF for a single valued path expression.
 *
 * <div nowrap><b>BNF:</b> <code>single_valued_path_expression ::= qualified_identification_variable |
 * state_field_path_expression | single_valued_object_path_expression</code><p>
 *
 * @version 2.3
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
final class SingleValuedPathExpressionBNF extends JPQLQueryBNF {

	/**
	 * The unique identifier of this BNF rule.
	 */
	static final String ID = "single_valued_path_expression";

	/**
	 * Creates a new <code>SingleValuedPathExpressionBNF</code>.
	 */
	SingleValuedPathExpressionBNF() {
		super(ID);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void initialize() {
		super.initialize();

		registerChild(QualifiedIdentificationVariableBNF.ID);
		registerChild(StateFieldPathExpressionBNF.ID);
		registerChild(SingleValuedObjectPathExpressionBNF.ID);
	}
}