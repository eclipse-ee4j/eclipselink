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
 * This builder can be used to easily create a conditional expression without having to create each
 * object manually. The builder is associated with {@link
 * org.eclipse.persistence.jpa.jpql.tools.model.query.AbstractConditionalClauseStateObject
 * AbstractConditionalClauseStateObject}.
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public interface IConditionalExpressionStateObjectBuilder extends IAbstractConditionalExpressionStateObjectBuilder<IConditionalExpressionStateObjectBuilder> {

    /**
     * Pushes the changes created by this builder to the state object.
     */
    void commit();
}
