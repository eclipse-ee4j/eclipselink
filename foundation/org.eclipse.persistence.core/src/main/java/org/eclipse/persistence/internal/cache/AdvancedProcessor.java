/*
 * Copyright (c) 2015, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//      Marcel Valovy - initial API and implementation
package org.eclipse.persistence.internal.cache;

/**
 * Processor for computable tasks.
 *
 * Concurrent.
 *
 * @author Marcel Valovy - marcel.valovy@oracle.com
 * @since 2.6
 */
public class AdvancedProcessor implements Processor, Clearable {

    private Memoizer<Object, Object> memoizer = new Memoizer<>();

    @Override
    public <A, V> V compute(ComputableTask<A, V> task, A taskArgument) {
        while (true) {
            try {
                //noinspection unchecked
                return (V) memoizer.compute((ComputableTask<Object, Object>) task, taskArgument);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    @Override
    public void clear() {
        memoizer.forgetAll();
    }
}
