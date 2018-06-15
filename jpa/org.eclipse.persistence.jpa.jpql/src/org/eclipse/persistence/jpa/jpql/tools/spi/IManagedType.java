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
 * The external representation of a managed type, which is a JPA persistent object.
 * <p>
 * Provisional API: This interface is part of an interim API that is still under development and
 * expected to change significantly before reaching stability. It is available at this early stage
 * to solicit feedback from pioneering adopters on the understanding that any code that uses this
 * API will almost certainly be broken (repeatedly) as the API evolves.
 *
 * @see IEmbeddable
 * @see IEntity
 * @see IMappedSuperclass
 *
 * @version 2.5
 * @since 2.3
 * @author Pascal Filion
 */
public interface IManagedType extends IExternalForm,
                                      Comparable<IManagedType> {

    /**
     * Visits this managed type with the given visitor.
     *
     * @param visitor The visitor to visit this managed type object
     */
    void accept(IManagedTypeVisitor visitor);

    /**
     * Returns the {@link IMapping} with the given name.
     *
     * @param name The name of the mapping to retrieve
     * @return Either the {@link IMapping} or <code>null</code> if it could not be found
     */
    IMapping getMappingNamed(String name);

    /**
     * Retrieves the owner of this managed type.
     *
     * @return The external form holding onto the JPA managed types
     */
    IManagedTypeProvider getProvider();

    /**
     * Returns the external representation of the class used by this managed type.
     *
     * @return The external representation of the class used by this managed type
     */
    IType getType();

    /**
     * Returns the collection of {@link IMapping mappings} defined in this managed type.
     *
     * @return The collection of persistent fields and properties of this managed type
     */
    Iterable<IMapping> mappings();
}
