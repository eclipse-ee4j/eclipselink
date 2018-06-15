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
package org.eclipse.persistence.testing.perf.largexml.bigpo.acknowledgement_response_code;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for AcknowledgementResponseCodeContentType.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="AcknowledgementResponseCodeContentType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}normalizedString">
 *     &lt;enumeration value="OrderResponse"/>
 *     &lt;enumeration value="OrderResponseSimple"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 *
 */
@XmlType(name = "AcknowledgementResponseCodeContentType")
@XmlEnum
public enum AcknowledgementResponseCodeContentType {


    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:AcknowledgementResponseCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;UBL OrderResponse document&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    @XmlEnumValue("OrderResponse")
    ORDER_RESPONSE("OrderResponse"),

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:AcknowledgementResponseCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;UBL OrderResponseSimple document&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    @XmlEnumValue("OrderResponseSimple")
    ORDER_RESPONSE_SIMPLE("OrderResponseSimple");
    private final String value;

    AcknowledgementResponseCodeContentType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static AcknowledgementResponseCodeContentType fromValue(String v) {
        for (AcknowledgementResponseCodeContentType c: AcknowledgementResponseCodeContentType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
