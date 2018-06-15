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
// David McCann = 2.1 - Initial contribution
package org.eclipse.persistence.jaxb.xmlmodel;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for xml-marshal-null-representation.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;simpleType name="xml-marshal-null-representation"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="XSI_NIL"/&gt;
 *     &lt;enumeration value="ABSENT_NODE"/&gt;
 *     &lt;enumeration value="EMPTY_NODE"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 *
 */
@XmlType(name = "xml-marshal-null-representation")
@XmlEnum
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
