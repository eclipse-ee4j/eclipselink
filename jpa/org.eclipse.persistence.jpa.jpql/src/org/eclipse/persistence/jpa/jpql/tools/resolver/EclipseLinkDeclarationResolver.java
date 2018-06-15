/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.jpa.jpql.tools.resolver;

import org.eclipse.persistence.jpa.jpql.ExpressionTools;
import org.eclipse.persistence.jpa.jpql.parser.Expression;
import org.eclipse.persistence.jpa.jpql.parser.IdentificationVariable;
import org.eclipse.persistence.jpa.jpql.parser.SimpleSelectStatement;
import org.eclipse.persistence.jpa.jpql.parser.SubExpression;
import org.eclipse.persistence.jpa.jpql.parser.TableVariableDeclaration;
import org.eclipse.persistence.jpa.jpql.tools.JPQLQueryContext;

/**
 * The EclipseLink implementation of {@link DeclarationResolver} that adds support for its
 * additional support.
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public class EclipseLinkDeclarationResolver extends DeclarationResolver {

    /**
     * Creates a new <code>EclipseLinkDeclarationResolver</code>.
     *
     * @param parent The parent resolver if this is used for a subquery or null if it's used for the
     * top-level query
     * @param queryContext The context used to query information about the query
     */
    public EclipseLinkDeclarationResolver(DeclarationResolver parent, JPQLQueryContext queryContext) {
        super(parent, queryContext);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected DeclarationVisitor buildDeclarationVisitor() {
        return new DeclarationVisitor();
    }

    protected class DeclarationVisitor extends DeclarationResolver.DeclarationVisitor {

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(SimpleSelectStatement expression) {

            // The parent query is using a subquery in the FROM clause
            if (buildingDeclaration) {
                currentDeclaration = new SubqueryDeclaration();
                currentDeclaration.rootPath       = ExpressionTools.EMPTY_STRING;
                currentDeclaration.baseExpression = expression;
                addDeclaration(currentDeclaration);
            }
            // Simply traversing the tree to create the declarations
            else {
                super.visit(expression);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(SubExpression expression) {
            expression.getExpression().accept(this);
        }

        /**
         * {@inheritDoc}
         */
        public void visit(TableVariableDeclaration expression) {

            currentDeclaration = new TableDeclaration();
            currentDeclaration.declarationExpression  = expression;
            currentDeclaration.baseExpression         = expression.getTableExpression();
            currentDeclaration.rootPath               = expression.getTableExpression().getExpression().toParsedText();

            Expression identificationVariable = expression.getIdentificationVariable();
            String variableName = visitDeclaration(expression, identificationVariable);

            if (variableName != ExpressionTools.EMPTY_STRING) {
                currentDeclaration.identificationVariable = (IdentificationVariable) identificationVariable;
            }

            addDeclaration(currentDeclaration);
        }
    }
}
