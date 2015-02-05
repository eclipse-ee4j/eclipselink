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
 * Processor for computable tasks.
 */
public interface Processor {

    /**
     * Waits if necessary for the computation to complete, and then
     * retrieves its result.
     *
     * @param taskArg argument for computation
     */
    <A, V> V compute(ComputableTask<A, V> task, A taskArg);

}
