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
package org.eclipse.persistence.jpa.jpql.tools.spi;

/**
 * A mapping builder is used to create the appropriate implementation of a {@link IMapping}.
 * <p>
 * Provisional API: This interface is part of an interim API that is still under development and
 * expected to change significantly before reaching stability. It is available at this early stage
 * to solicit feedback from pioneering adopters on the understanding that any code that uses this
 * API will almost certainly be broken (repeatedly) as the API evolves.
 *
 * @see IManagedType
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public interface IMappingBuilder<T> extends IExternalForm {

    /**
     * Creates a new {@link IMapping}.
     *
     * @param parent The parent of the mapping to create
     * @param value The object that represents the mapping
     * @return A new concrete instance of the external form
     */
    IMapping buildMapping(IManagedType parent, T value);
}
