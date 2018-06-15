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
package org.eclipse.persistence.testing.perf.largexml.bigpo.document_status_code;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for DocumentStatusCodeContentType.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="DocumentStatusCodeContentType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}normalizedString">
 *     &lt;enumeration value="Cancelled"/>
 *     &lt;enumeration value="Disputed"/>
 *     &lt;enumeration value="NoStatus"/>
 *     &lt;enumeration value="Revised"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 *
 */
@XmlType(name = "DocumentStatusCodeContentType")
@XmlEnum
public enum DocumentStatusCodeContentType {


    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:DocumentStatusCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;Document has been cancelled&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    @XmlEnumValue("Cancelled")
    CANCELLED("Cancelled"),

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:DocumentStatusCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;Document is disputed&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    @XmlEnumValue("Disputed")
    DISPUTED("Disputed"),

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:DocumentStatusCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;No status on document&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    @XmlEnumValue("NoStatus")
    NO_STATUS("NoStatus"),

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;CodeName xmlns="urn:oasis:names:specification:ubl:schema:xsd:DocumentStatusCode-1.0" xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;Document has been revised&lt;/CodeName&gt;
     * </pre>
     *
     *
     */
    @XmlEnumValue("Revised")
    REVISED("Revised");
    private final String value;

    DocumentStatusCodeContentType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static DocumentStatusCodeContentType fromValue(String v) {
        for (DocumentStatusCodeContentType c: DocumentStatusCodeContentType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
