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
 * Used by various "pluggable" classes to filter objects.
 */
public interface Filter {

    /**
     * Return whether the specified object is "accepted" by the
     * filter. The semantics of "accept" is determined by the
     * contract between the client and the server.
     */
    boolean accept(Object o);


    Filter NULL_INSTANCE =
        new Filter() {
            // nothing is filtered - everything is accepted
            public boolean accept(Object next) {
                return true;
            }
            public String toString() {
                return "NullFilter";
            }
        };

}
