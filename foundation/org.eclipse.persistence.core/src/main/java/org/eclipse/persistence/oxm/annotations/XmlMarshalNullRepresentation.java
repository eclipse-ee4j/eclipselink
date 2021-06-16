/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
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
// Oracle = 2.1 - Initial contribution
package org.eclipse.persistence.oxm.annotations;

/**
 * <b>Purpose:</b> This enumeration provides a means of specifying how a null value in a
 * java object should be marshalled to XML. The possible options are:<br>
 * <ul>
 * <li>XSI_NIL - This means that the element should be written out with an xsi:nil attribute. Example:
 * <code>&lt;element xsi:nil="true"/&gt;</code></li>
 * <li>EMPTY_NODE - This indicates that the element should be written out to xml with no contents
 * Example: <code>&lt;element /&gt;</code></li>
 * <li>ABSENT_NODE - Absent node means that no element should be written to XML if null is encountered
 * </ul>
 */
public enum XmlMarshalNullRepresentation {

    XSI_NIL,
    ABSENT_NODE,
    EMPTY_NODE;

    public String value() {
        return name();
    }

    public static XmlMarshalNullRepresentation fromValue(String v) {
        return valueOf(v);
    }

}
