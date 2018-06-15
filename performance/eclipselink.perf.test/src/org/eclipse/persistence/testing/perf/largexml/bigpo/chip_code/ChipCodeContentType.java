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
package org.eclipse.persistence.testing.perf.largexml.bigpo.chip_code;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ChipCodeContentType.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ChipCodeContentType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}normalizedString">
 *     &lt;enumeration value="Chip"/>
 *     &lt;enumeration value="MagneticStripe"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 *
 */
@XmlType(name = "ChipCodeContentType")
@XmlEnum
public enum ChipCodeContentType {


    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:ChipCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;Chip&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    @XmlEnumValue("Chip")
    CHIP("Chip"),

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:ChipCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;Magnetic Stripe&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    @XmlEnumValue("MagneticStripe")
    MAGNETIC_STRIPE("MagneticStripe");
    private final String value;

    ChipCodeContentType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ChipCodeContentType fromValue(String v) {
        for (ChipCodeContentType c: ChipCodeContentType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
