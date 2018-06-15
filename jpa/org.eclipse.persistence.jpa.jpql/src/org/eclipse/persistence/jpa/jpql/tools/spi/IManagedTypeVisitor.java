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
package org.eclipse.persistence.jpa.jpql.tools.spi;

/**
 * The interface is used to visit a {@link IManagedType}.
 * <p>
 * Provisional API: This interface is part of an interim API that is still under development and
 * expected to change significantly before reaching stability. It is available at this early stage
 * to solicit feedback from pioneering adopters on the understanding that any code that uses this
 * API will almost certainly be broken (repeatedly) as the API evolves.
 *
 * @version 2.3
 * @since 2.3
 * @author Pascal Filion
 */
public interface IManagedTypeVisitor extends IExternalForm {

    /**
     * Visits the given {@link IEmbeddable} object.
     *
     * @param embeddable The embeddable object to visit
     */
    void visit(IEmbeddable embeddable);

    /**
     * Visits the given {@link IEntity} object.
     *
     * @param entity The entity object to visit
     */
    void visit(IEntity entity);

    /**
     * Visits the given {@link IMappedSuperclass} object.
     *
     * @param mappedSuperclass The mapped superclass object to visit
     */
    void visit(IMappedSuperclass mappedSuperclass);
}
