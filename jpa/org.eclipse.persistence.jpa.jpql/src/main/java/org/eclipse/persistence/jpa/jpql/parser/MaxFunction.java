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
 * One of the aggregate functions. The arguments must correspond to orderable state-field types
 * (i.e., numeric types, string types, character types, or date types). The return type of this
 * function is based on the state-field's type.
 *
 * <div><b>BNF:</b> <code>expression ::= MAX([DISTINCT] state_field_path_expression)</code><p></p></div>
 *
 * @version 2.5
 * @since 2.3
 * @author Pascal Filion
 */
public final class MaxFunction extends AggregateFunction {

    /**
     * Creates a new <code>MaxFunction</code>.
     *
     * @param parent The parent of this expression
     */
    public MaxFunction(AbstractExpression parent) {
        super(parent, MAX);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void accept(ExpressionVisitor visitor) {
        visitor.visit(this);
    }
}
