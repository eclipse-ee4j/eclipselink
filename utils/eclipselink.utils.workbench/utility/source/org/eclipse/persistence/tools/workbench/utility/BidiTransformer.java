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
package org.eclipse.persistence.tools.workbench.utility;

/**
 * Used by various "pluggable" classes to transform objects
 * in both directions.
 *
 * If anyone can come up with a better class name
 * and/or method name, I would love to hear it.  -bjv
 */
public interface BidiTransformer extends Transformer {

    /**
     * Return the "reverse-transformed" object.
     * The semantics of "reverse-transform" is determined by the
     * contract between the client and the server.
     */
    Object reverseTransform(Object o);


    BidiTransformer NULL_INSTANCE =
        new BidiTransformer() {
            // simply return the object, unchanged
            public Object transform(Object o) {
                return o;
            }
            // simply return the object, unchanged
            public Object reverseTransform(Object o) {
                return o;
            }
            public String toString() {
                return "NullBidiTransformer";
            }
        };

}
