/*******************************************************************************
 * Copyright (c) 2012 Oracle. All rights reserved.
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
 * Returns an expression that allows a join ON clause to be defined.
 * <p>
 * New to EclipseLink 2.4.
 *
 * <div nowrap><b>BNF:</b> <code>join_on ::= ON conditional_expression</code>
 * <p>
 * Example: <code>SELECT e FROM Employee e LEFT JOIN e.projects p ON p.budget > 10000</code>
 *
 * @version 2.4
 * @since 2.4
 * @author James
 */
public final class OnClause extends AbstractConditionalClause {

	/**
	 * Creates a new <code>TreatExpression</code>.
	 *
	 * @param parent The parent of this expression
	 */
	public OnClause(AbstractExpression parent) {
		super(parent, ON);
	}

        /**
         * {@inheritDoc}
         */
        public void accept(ExpressionVisitor visitor) {
                visitor.visit(this);
        }

	/**
	 * {@inheritDoc}
	 */
	@Override
	public JPQLQueryBNF getQueryBNF() {
		return getQueryBNF(OnClauseBNF.ID);
	}
}