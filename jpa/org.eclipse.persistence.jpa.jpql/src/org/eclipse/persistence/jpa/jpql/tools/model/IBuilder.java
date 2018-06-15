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

import org.eclipse.persistence.jpa.jpql.parser.Expression;
import org.eclipse.persistence.jpa.jpql.tools.model.query.StateObject;

/**
 * This builder is used by {@link BasicStateObjectBuilder}, which allows subclasses to easily change
 * any internal builders used for properly creating the state model representation of a JPQL query.
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public interface IBuilder<T extends StateObject, S extends StateObject> {

    /**
     * Creates the {@link StateObject} representation of the given {@link Expression}.
     *
     * @param parent The parent of the new {@link StateObject} to create
     * @param expression The parsed {@link Expression} to convert into a {@link StateObject}
     * @return The {@link StateObject} representation of the given {@link Expression}
     */
    T buildStateObject(S parent, Expression expression);
}
