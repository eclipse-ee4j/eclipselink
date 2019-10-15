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
// rbarkhouse - 2011 May 09 - 2.3 - Initial implementation
package org.eclipse.persistence.oxm.annotations;

public enum XmlVirtualAccessMethodsSchema {
    /**
     * Virtual properties are written to the schema as individual nodes (default).
     */
    NODES,

    /**
     * An XML {@literal <any>} element will be written to the schema to represent all
     * of the defined virtual properties.
     */
    ANY;

    public String value() {
        return name();
    }

    public static XmlVirtualAccessMethodsSchema fromValue(String v) {
        return valueOf(v);
    }

}
