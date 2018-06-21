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
//     Gordon Yorke - initial implementation
package org.eclipse.persistence.internal.indirection;

import org.eclipse.persistence.indirection.ValueHolderInterface;

/**
 * WrappingValueHolder is an interface type that implementors use when they will be
 * wrapping another ValueHolder that has the original value.
 *
 * @see UnitOfWorkValueHolder, ProtectedValueHolder
 * @author    Gordon Yorke
 */
public interface WrappingValueHolder {

    /**
     * Returns the valueholder that is wrapped by this ValueHolder
     */
    public ValueHolderInterface getWrappedValueHolder();


}
