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
package org.eclipse.persistence.jpa.tests.jpql;

import org.eclipse.persistence.jpa.jpql.tools.spi.IManagedTypeProvider;
import org.eclipse.persistence.jpa.jpql.tools.spi.IQuery;

/**
 * The external representation of an ORM configuration.
 *
 * @version 2.3
 * @since 2.3
 * @author Pascal Filion
 */
public interface IORMConfiguration extends IManagedTypeProvider {

    /**
     * Returns the external form of the given named JPQL query;
     *
     * @param queryName The name of the JPQL query to retrieve
     * @return The {@link IQuery} representing the JPQL query named with the given name; or
     * <code>null</code> if none could be found
     */
    IQuery getNamedQuery(String queryName);
}
