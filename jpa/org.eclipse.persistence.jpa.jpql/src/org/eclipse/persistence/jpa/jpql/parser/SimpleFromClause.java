/*
 * Copyright (c) 2006, 2018 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.jpa.jpql.parser;

/**
 * The <b>FROM</b> clause of a query defines the domain of the query by declaring identification
 * variables. An identification variable is an identifier declared in the <b>FROM</b> clause of a
 * query. The domain of the query may be constrained by path expressions. Identification variables
 * designate instances of a particular entity abstract schema type. The <b>FROM</b> clause can
 * contain multiple identification variable declarations separated by a comma (,).
 *
 * <div><b>BNF:</b> <code>subquery_from_clause ::= FROM subselect_identification_variable_declaration {, subselect_identification_variable_declaration}*</code><p></div>
 *
 * @version 2.5
 * @since 2.3
 * @author Pascal Filion
 */
public final class SimpleFromClause extends AbstractFromClause {

    /**
     * Creates a new <code>SimpleFromClause</code>.
     *
     * @param parent The parent of this expression
     */
    public SimpleFromClause(AbstractExpression parent) {
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
        return InternalSimpleFromClauseBNF.ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JPQLQueryBNF getQueryBNF() {
        return getQueryBNF(SubqueryFromClauseBNF.ID);
    }
}
