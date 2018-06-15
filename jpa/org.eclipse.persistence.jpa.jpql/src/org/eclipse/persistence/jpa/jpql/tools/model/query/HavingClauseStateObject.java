/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation
//
package org.eclipse.persistence.jpa.jpql.tools.model.query;

import org.eclipse.persistence.jpa.jpql.parser.HavingClause;
import static org.eclipse.persistence.jpa.jpql.parser.Expression.*;

/**
 * The <code><b>HAVING</b></code> construct enables conditions to be specified that further restrict
 * the query result as restrictions upon the groups.
 *
 * <div><b>BNF:</b> <code>having_clause ::= HAVING conditional_expression</code><p></div>
 *
 * @see HavingClause
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public class HavingClauseStateObject extends AbstractConditionalClauseStateObject {

    /**
     * Creates a new <code>HavingClauseStateObject</code>.
     *
     * @param parent The parent of this state object, which cannot be <code>null</code>
     * @exception NullPointerException The given parent cannot be <code>null</code>
     */
    public HavingClauseStateObject(AbstractSelectStatementStateObject parent) {
        super(parent);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void accept(StateObjectVisitor visitor) {
        visitor.visit(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HavingClause getExpression() {
        return (HavingClause) super.getExpression();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getIdentifier() {
        return HAVING;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AbstractSelectStatementStateObject getParent() {
        return (AbstractSelectStatementStateObject) super.getParent();
    }

    /**
     * Keeps a reference of the {@link HavingClause parsed object} object, which should only be
     * done when this object is instantiated during the conversion of a parsed JPQL query into
     * {@link StateObject StateObjects}.
     *
     * @param expression The {@link HavingClause parsed object} representing a <code><b>HAVING</b></code>
     * expression
     */
    public void setExpression(HavingClause expression) {
        super.setExpression(expression);
    }
}
