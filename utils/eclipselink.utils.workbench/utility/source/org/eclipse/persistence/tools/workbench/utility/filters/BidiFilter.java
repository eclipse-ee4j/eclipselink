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
package org.eclipse.persistence.tools.workbench.utility.filters;

/**
 * Used by various "pluggable" classes to filter objects
 * in both directions.
 *
 * If anyone can come up with a better class name
 * and/or method name, I would love to hear it.  -bjv
 */
public interface BidiFilter extends Filter {

    /**
     * Return whether the specified object is "accepted" by the
     * "reverse" filter. What that means is determined by the client.
     */
    boolean reverseAccept(Object o);


    BidiFilter NULL_INSTANCE =
        new BidiFilter() {
            // nothing is filtered - everything is accepted
            public boolean accept(Object o) {
                return true;
            }
            // nothing is "reverse-filtered" - everything is accepted
            public boolean reverseAccept(Object o) {
                return true;
            }
            public String toString() {
                return "NullBidiFilter";
            }
        };

}
