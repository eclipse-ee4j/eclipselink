/*
 * Copyright (c) 2015, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//      Marcel Valovy - initial API and implementation
package org.eclipse.persistence.internal.cache;

/**
 * Computable task.
 *
 * Note: If implementation has mutable state, it must override equals and hashCode methods, taking into account all
 * fields that may have any effect on computation result.
 */
public interface ComputableTask<A, V> {

    V compute(A arg) throws InterruptedException;
}
