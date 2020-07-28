/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.tools.workbench.utility.string;

/**
 * Used by various "pluggable" classes to transform objects
 * into strings.
 */
public interface StringConverter {

    /**
     * Convert the specified object into a string.
     * The semantics of "convert" is determined by the
     * contract between the client and the server.
     */
    String convertToString(Object o);


    StringConverter DEFAULT_INSTANCE =
        new StringConverter() {
            // simply return the object's #toString() result
            public String convertToString(Object o) {
                return (o == null) ? null : o.toString();
            }
            public String toString() {
                return "DefaultStringConverter";
            }
        };

}
