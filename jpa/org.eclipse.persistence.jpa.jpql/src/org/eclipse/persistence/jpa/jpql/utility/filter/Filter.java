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
package org.eclipse.persistence.jpa.jpql.utility.filter;

/**
 * A filter is used to determine if a value can be "accepted" or "rejected".
 *
 * @version 2.3
 * @since 2.3
 */
public interface Filter<T> {

    /**
     * Determines whether the specified object is "accepted" by the filter. The semantics of "accept"
     * is determined by the contract between the client and the server.
     *
     * @param value The value to filter
     * @return <code>true</code> if the given value is "accepted" by this filter; <code>false</code>
     * if it was "rejected"
     */
    boolean accept(T value);
}
