/*
 * Copyright (c) 2011, 2024 Oracle and/or its affiliates. All rights reserved.
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
// Oracle = 2.1 - Initial contribution
package org.eclipse.persistence.oxm.annotations;

/**
 * This enumeration provides a means of specifying how a null value in a
 * java object should be marshalled to XML. The possible options are:
 * <ul>
 *  <li>{@linkplain #XSI_NIL} - This means that the element should be written out with an xsi:nil attribute.
 * Example: {@code <element xsi:nil="true"/>}.</li>
 *  <li>{@linkplain #EMPTY_NODE} - This indicates that the element should be written out to xml with no contents
 * Example: {@code <element />}.</li>
 *  <li>{@linkplain #ABSENT_NODE} - Absent node means that no element should be written to XML if null is encountered.</li>
 * </ul>
 */
public enum XmlMarshalNullRepresentation {

    /**
     * Null element should be written out as an element with an xsi:nil attribute.
     */
    XSI_NIL,
    /**
     * Null element should not be written out.
     */
    ABSENT_NODE,
    /**
     * Null element should be written out to xml as an element with no contents.
     */
    EMPTY_NODE;

    public String value() {
        return name();
    }

    public static XmlMarshalNullRepresentation fromValue(String v) {
        return valueOf(v);
    }

}
