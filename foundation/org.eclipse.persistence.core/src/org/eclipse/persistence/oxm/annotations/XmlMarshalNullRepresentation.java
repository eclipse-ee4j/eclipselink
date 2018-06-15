/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
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
