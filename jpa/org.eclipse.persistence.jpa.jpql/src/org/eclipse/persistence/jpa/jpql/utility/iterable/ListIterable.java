/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.jpa.jpql.utility.iterable;

import java.util.ListIterator;

/**
 * A <code>ListIterable</code> simply forces the returned object to be an instance of {@link ListIterator}.
 *
 * @version 2.5
 * @since 2.5
 * @author Pascal Filion
 */
public interface ListIterable<T> extends Iterable<T> {

    /**
     * {@inheritDoc}
     */
    @Override
    ListIterator<T> iterator();
}
