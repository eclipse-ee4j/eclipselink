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
package org.eclipse.persistence.jpa.jpql.tools.model;

/**
 * This builder can be used to easily create a select expression defined for a subquery without
 * having to create each object manually. The builder is associated with {@link
 * org.eclipse.persistence.jpa.jpql.tools.model.query.SimpleSelectClauseStateObject SimpleSelectClauseStateObject}.
 *
 * @see ISelectExpressionStateObjectBuilder
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public interface ISimpleSelectExpressionStateObjectBuilder extends IScalarExpressionStateObjectBuilder<ISimpleSelectExpressionStateObjectBuilder> {

    /**
     * Pushes the changes created by this builder to the state object.
     */
    void commit();

    /**
     * Creates the expression representing an identification variable.
     *
     * @param variable The identification variable
     * @return This {@link ISimpleSelectExpressionStateObjectBuilder builder}
     */
    ISimpleSelectExpressionStateObjectBuilder variable(String variable);
}
