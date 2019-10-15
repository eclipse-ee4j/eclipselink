/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation
//
package org.eclipse.persistence.jpa.jpql.tools.model;

/**
 * This builder can be used to easily create a select expression without having to create each
 * object manually. The builder is associated with {@link
 * org.eclipse.persistence.jpa.jpql.tools.model.query.SelectClauseStateObject SelectClauseStateObject}.
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public interface ISelectExpressionStateObjectBuilder extends IScalarExpressionStateObjectBuilder<ISelectExpressionStateObjectBuilder> {

    /**
     * Appends the previously created select item and starts a new stack to create a new select item.
     *
     * @return This builder
     */
    ISelectExpressionStateObjectBuilder append();

    /**
     * Pushes the changes created by this builder to the state object.
     */
    void commit();

    /**
     * Creates the expression <code><b>NEW constructor_name(identification_variable)</b></code>.
     *
     * @param className The fully qualified class name
     * @param parameters The parameters of the constructor
     * @return This builder
     */
    ISelectExpressionStateObjectBuilder new_(String className, ISelectExpressionStateObjectBuilder... parameters);

    /**
     * Creates the expression <code><b>OBJECT(identification_variable)</b></code>.
     *
     * @param identificationVariable The identification variable
     * @return This builder
     */
    ISelectExpressionStateObjectBuilder object(String identificationVariable);

    /**
     * Defines the current state object with the given result variable.
     *
     * @param resultVariable The variable identifying the current select expression
     * @return This builder
     */
    ISelectExpressionStateObjectBuilder resultVariable(String resultVariable);

    /**
     * Defines the current state object with the given result variable.
     *
     * @param resultVariable The variable identifying the current select expression
     * @return This builder
     */
    ISelectExpressionStateObjectBuilder resultVariableAs(String resultVariable);

    /**
     * Creates the expression representing an identification variable.
     *
     * @param variable The identification variable
     * @return This {@link ISelectExpressionStateObjectBuilder builder}
     */
    ISelectExpressionStateObjectBuilder variable(String variable);
}
