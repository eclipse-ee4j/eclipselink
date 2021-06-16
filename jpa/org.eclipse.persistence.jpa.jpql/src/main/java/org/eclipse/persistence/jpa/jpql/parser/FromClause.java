/*
 * Copyright (c) 2006, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Oracle - initial API and implementation
//
package org.eclipse.persistence.jpa.jpql.parser;

/**
 * The <b>FROM</b> clause of a query defines the domain of the query by declaring identification
 * variables. An identification variable is an identifier declared in the <b>FROM</b> clause of a
 * query. The domain of the query may be constrained by path expressions. Identification variables
 * designate instances of a particular entity abstract schema type. The <b>FROM</b> clause can
 * contain multiple identification variable declarations separated by a comma (,).
 * <p>
 * JPA 1.0, 2.0, 2.1, EclipseLink 1.0 - 2.4:
 * <div><b>BNF:</b> <code>from_clause ::= FROM identification_variable_declaration {, {identification_variable_declaration | collection_member_declaration}}*</code><p></p></div>
 *
 * EclipseLink 2.5:
 * <div><b>BNF:</b> <code>from_clause ::= FROM identification_variable_declaration {, {identification_variable_declaration | collection_member_declaration}}* [hierarchical_query_clause] [asof_clause]</code><p></p></div>
 *
 * @version 2.5
 * @since 2.3
 * @author Pascal Filion
 */
public final class FromClause extends AbstractFromClause {

    /**
     * Creates a new <code>FromClause</code>.
     *
     * @param parent The parent of this expression
     */
    public FromClause(AbstractExpression parent) {
        super(parent);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void accept(ExpressionVisitor visitor) {
        visitor.visit(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDeclarationQueryBNFId() {
        return InternalFromClauseBNF.ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JPQLQueryBNF getQueryBNF() {
        return getQueryBNF(FromClauseBNF.ID);
    }
}
