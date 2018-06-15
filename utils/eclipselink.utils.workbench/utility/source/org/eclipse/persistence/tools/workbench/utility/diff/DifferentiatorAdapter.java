/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.tools.workbench.utility.diff;

/**
 * Pluggable adapter that allows the same code to be used for both "normal"
 * and "key" diffs.
 */
public interface DifferentiatorAdapter {

    /**
     * Adapt the specified differentiator to "diff" the specified objects.
     */
    Diff diff(Differentiator differentiator, Object object1, Object object2);


    DifferentiatorAdapter NORMAL = new DifferentiatorAdapter() {
        public Diff diff(Differentiator differentiator, Object object1, Object object2) {
            return differentiator.diff(object1, object2);
        }
    };

    DifferentiatorAdapter KEY = new DifferentiatorAdapter() {
        public Diff diff(Differentiator differentiator, Object object1, Object object2) {
            return differentiator.keyDiff(object1, object2);
        }
    };

}
