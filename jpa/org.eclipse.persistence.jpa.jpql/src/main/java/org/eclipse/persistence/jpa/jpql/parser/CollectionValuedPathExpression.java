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
 * A <code>collection_valued_field</code> is designated by the name of an association field in a
 * one-to-many or a many-to-many relationship or by the name of an element collection field. The
 * type of a <code>collection_valued_field</code> is a collection of values of the abstract schema
 * type of the related entity or element type.
 *
 * <div><b>BNF:</b> <code>collection_valued_path_expression ::= general_identification_variable.{single_valued_object_field.}*collection_valued_field</code><p></p></div>
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
public final class CollectionValuedPathExpression extends AbstractPathExpression {

    /**
     * Creates a new <code>CollectionValuedPathExpression</code>.
     *
     * @param parent The parent of this expression
     * @param expression The identification variable that was already parsed, which means the
     * beginning of the parsing should start with a dot
     */
    public CollectionValuedPathExpression(AbstractExpression parent, AbstractExpression expression) {
        super(parent, expression);
    }

    /**
     * Creates a new <code>CollectionValuedPathExpression</code>.
     *
     * @param parent The parent of this expression
     * @param expression The identification variable that was already parsed, which means the
     * beginning of the parsing should start with a dot
     * @param paths The path expression that is following the identification variable
     */
    public CollectionValuedPathExpression(AbstractExpression parent,
                                          AbstractExpression expression,
                                          String paths) {

        super(parent, expression, paths);
    }

    /**
     * Creates a new <code>CollectionValuedPathExpression</code>.
     *
     * @param parent The parent of this expression
     * @param paths The path expression
     */
    public CollectionValuedPathExpression(AbstractExpression parent, String paths) {
        super(parent, paths);
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
    public JPQLQueryBNF getQueryBNF() {
        return getQueryBNF(CollectionValuedPathExpressionBNF.ID);
    }
}
