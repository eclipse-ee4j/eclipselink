/*******************************************************************************
 * Copyright (c) 2011, 2015 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation
 *
 ******************************************************************************/
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
