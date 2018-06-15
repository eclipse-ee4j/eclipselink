/*
 * Copyright (c) 2014, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Martin Vojtek - 2.6.0 - initial implementation
package org.eclipse.persistence.testing.perf.largexml.bigpo.operator_code;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for OperatorCodeContentType.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="OperatorCodeContentType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}normalizedString">
 *     &lt;enumeration value="Divide"/>
 *     &lt;enumeration value="Multiply"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 *
 */
@XmlType(name = "OperatorCodeContentType")
@XmlEnum
public enum OperatorCodeContentType {


    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:OperatorCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;Divide&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    @XmlEnumValue("Divide")
    DIVIDE("Divide"),

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:OperatorCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;Multiply&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    @XmlEnumValue("Multiply")
    MULTIPLY("Multiply");
    private final String value;

    OperatorCodeContentType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static OperatorCodeContentType fromValue(String v) {
        for (OperatorCodeContentType c: OperatorCodeContentType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
