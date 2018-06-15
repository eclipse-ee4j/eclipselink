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
package org.eclipse.persistence.jpa.jpql.tools.model.query;

import org.eclipse.persistence.jpa.jpql.tools.spi.IManagedType;
import org.eclipse.persistence.jpa.jpql.utility.iterable.ListIterable;

/**
 * This defines the declaration portion of a query, which is the <code><b>FROM</b></code> clause
 * of a query.
 *
 * @see FromClauseStateObject
 * @see SimpleFromClauseStateObject
 * @see DeleteClauseStateObject
 * @see UpdateClauseStateObject
 *
 * @version 2.5
 * @since 2.4
 * @author Pascal Filion
 */
public interface DeclarationStateObject extends StateObject {

    /**
     * Returns the list of declarations declared in the declaration clause of the current query.
     *
     * @return The list of {@link VariableDeclarationStateObject} that define the domain of the query
     */
    ListIterable<? extends VariableDeclarationStateObject> declarations();

    /**
     * Returns the {@link IManagedType} for the given identification variable. If the declaration is
     * for a subquery and there is no managed type associated with the identification then the search
     * will traverse up the query hierarchy.
     *
     * <pre><code>SELECT e FROM Department d JOIN KEY(d.employees).addresses a</code></pre>
     * In the above query, the managed type associated with the identification variable:
     * <ul>
     * <li>d is "Department"
     * <li>a is "Address"
     * </ul>
     *
     * @param stateObject The {@link StateObject} that should be an simple identification variable or
     * an encapsulated identification variable with the identifier <code><b>KEY</b></code> or
     * <code><b>VALUE</b></code>
     * @return The {@link IManagedType} representing the domain object declared by the given
     * identification variable
     * @see #getManagedType(StateObject)
     */
    IManagedType findManagedType(StateObject stateObject);

    /**
     * Returns the {@link IManagedType} for the given identification variable. The search does not
     * traverse up the query hierarchy if this declaration is for a subquery.
     *
     * <pre><code>SELECT e FROM Department d JOIN KEY(d.employees).addresses a</code></pre>
     * In the above query, the managed type associated with the identification variable:
     * <ul>
     * <li>d is "Department"
     * <li>a is "Address"
     * </ul>
     *
     * @param stateObject The {@link StateObject} that should be an simple identification variable or
     * an encapsulated identification variable with the identifier <code><b>KEY</b></code> or
     * <code><b>VALUE</b></code>
     * @return The {@link IManagedType} representing the domain object declared by the given
     * identification variable
     * @see #findManagedType(StateObject)
     */
    IManagedType getManagedType(StateObject stateObject);
}
