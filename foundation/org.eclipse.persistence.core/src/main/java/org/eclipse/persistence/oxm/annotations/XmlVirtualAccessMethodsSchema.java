/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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
