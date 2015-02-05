/**
 * ****************************************************************************
 * Copyright (c) 2015 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 * <p/>
 * Contributors:
 *      Marcel Valovy - initial API and implementation
 * ****************************************************************************
 */
package org.eclipse.persistence.internal.cache;

/**
 * Able to process computable tasks.
 *
 * Use {@link org.eclipse.persistence.internal.cache.Processor} for higher-level processors that do not propagate
 * InterruptedException.
 */
public interface LowLevelProcessor<A, V> {

    /**
     * Waits if necessary for the computation to complete, and then
     * retrieves its result.
     *
     * @param taskArg argument for computation
     * @throws InterruptedException if the current thread was interrupted
     * while waiting
     */
    V compute(ComputableTask<A, V> c, A taskArg) throws InterruptedException;
}
