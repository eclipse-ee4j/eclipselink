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

import org.eclipse.persistence.jpa.jpql.tools.model.query.CaseExpressionStateObject;

/**
 * This builder is responsible to create a <code><b>CASE</b></code> expression.
 *
 * @see ISimpleSelectExpressionStateObjectBuilder#getCaseBuilder()
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public interface ICaseExpressionStateObjectBuilder extends IAbstractConditionalExpressionStateObjectBuilder<ICaseExpressionStateObjectBuilder> {

    /**
     * Creates the actual state object based on the information this builder gathered.
     *
     * @return The newly created {@link CaseExpressionStateObject}
     */
    CaseExpressionStateObject buildStateObject();

    /**
     * Creates a single <code><b>WHEN</b></code> expression.
     *
     * @param when The <code><b>WHEN</b></code> expression
     * @param then The <code><b>THEN</b></code> expression
     * @return This {@link ICaseExpressionStateObjectBuilder builder}
     */
    ICaseExpressionStateObjectBuilder when(ICaseExpressionStateObjectBuilder when,
                                           ICaseExpressionStateObjectBuilder then);
}
