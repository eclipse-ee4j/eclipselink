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
package org.eclipse.persistence.jpa.jpql;

import org.eclipse.persistence.jpa.jpql.parser.AbstractTraverseParentVisitor;
import org.eclipse.persistence.jpa.jpql.parser.BadExpression;
import org.eclipse.persistence.jpa.jpql.parser.CollectionExpression;
import org.eclipse.persistence.jpa.jpql.parser.DeleteClause;
import org.eclipse.persistence.jpa.jpql.parser.DeleteStatement;
import org.eclipse.persistence.jpa.jpql.parser.FromClause;
import org.eclipse.persistence.jpa.jpql.parser.IdentificationVariable;
import org.eclipse.persistence.jpa.jpql.parser.IdentificationVariableDeclaration;
import org.eclipse.persistence.jpa.jpql.parser.JPQLExpression;
import org.eclipse.persistence.jpa.jpql.parser.NullExpression;
import org.eclipse.persistence.jpa.jpql.parser.RangeVariableDeclaration;
import org.eclipse.persistence.jpa.jpql.parser.SelectStatement;
import org.eclipse.persistence.jpa.jpql.parser.UnknownExpression;
import org.eclipse.persistence.jpa.jpql.parser.UpdateClause;
import org.eclipse.persistence.jpa.jpql.parser.UpdateStatement;

/**
 * This visitor traverses the parsed tree and retrieves the {@link IdentificationVariable}
 * defined in the base range variable declaration for the top-level statement if and only if the
 * query is a <code><b>DELETE</b></code> or <code><b>UPDATE</b></code> query.
 *
 * @since 2.5
 * @version 2.5
 * @author Pascal Filion
 */
public class BaseDeclarationIdentificationVariableFinder extends AbstractTraverseParentVisitor {

    /**
     * The {@link IdentificationVariable} used to define the abstract schema name from either the
     * <b>UPDATE</b> or <b>DELETE</b> clause.
     */
    public IdentificationVariable expression;

    /**
     * Determines if the {@link RangeVariableDeclaration} should traverse its identification
     * variable expression or simply visit the parent hierarchy.
     */
    protected boolean traverse;

    /**
     * Creates a new <code>BaseDeclarationIdentificationVariableFinder</code>.
     */
    public BaseDeclarationIdentificationVariableFinder() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(BadExpression expression) {
        // Incomplete/invalid query, stop here
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(CollectionExpression expression) {
        if (traverse) {
            // Invalid query, scan the first expression only
            expression.getChild(0).accept(this);
        }
        else {
            super.visit(expression);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(DeleteClause expression) {
        try {
            traverse = true;
            expression.getRangeVariableDeclaration().accept(this);
        }
        finally {
            traverse = false;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(DeleteStatement expression) {
        expression.getDeleteClause().accept(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(FromClause expression) {
        if (traverse) {
            expression.getDeclaration().accept(this);
        }
        else {
            super.visit(expression);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(IdentificationVariable expression) {
        if (traverse) {
            this.expression = expression;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(IdentificationVariableDeclaration expression) {
        if (traverse) {
            expression.getRangeVariableDeclaration().accept(this);
        }
        else {
            super.visit(expression);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(JPQLExpression expression) {
        expression.getQueryStatement().accept(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(NullExpression expression) {
        // Incomplete/invalid query, stop here
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(RangeVariableDeclaration expression) {
        if (traverse) {
            expression.getIdentificationVariable().accept(this);
        }
        else {
            super.visit(expression);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(SelectStatement expression) {
        // Nothing to do because this visitor is only meant for DELETE or UPDATE queries
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(UnknownExpression expression) {
        // Incomplete/invalid query, stop here
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(UpdateClause expression) {
        try {
            traverse = true;
            expression.getRangeVariableDeclaration().accept(this);
        }
        finally {
            traverse = false;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(UpdateStatement expression) {
        expression.getUpdateClause().accept(this);
    }
}
