/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.jpa.jpql;

import org.eclipse.persistence.jpa.jpql.parser.EclipseLinkExpressionVisitor;
import org.eclipse.persistence.jpa.jpql.parser.FuncExpression;
import org.eclipse.persistence.jpa.jpql.parser.TreatExpression;

/**
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public class EclipseLinkSemanticValidator extends AbstractSemanticValidator
                                          implements EclipseLinkExpressionVisitor {

	/**
	 * Creates a new <code>EclipseLinkSemanticValidator</code>.
	 *
	 * @param context The context used to query information about the JPQL query
	 * @exception AssertException The {@link JPQLQueryContext} cannot be <code>null</code>
	 */
	public EclipseLinkSemanticValidator(JPQLQueryContext context) {
		super(context);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(FuncExpression expression) {
		// Nothing to validate semantically
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(TreatExpression expression) {
		// Nothing to validate semantically
		super.visit(expression);
	}
}