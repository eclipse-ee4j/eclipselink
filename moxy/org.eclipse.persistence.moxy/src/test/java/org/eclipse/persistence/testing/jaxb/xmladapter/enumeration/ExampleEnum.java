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
//     Matt MacIvor - 2.4.1 - initial implementation
package org.eclipse.persistence.testing.jaxb.xmladapter.enumeration;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;
import jakarta.xml.bind.annotation.XmlType;

@XmlEnum()
@XmlType(name = "EXAMLE_ENUM")
public enum ExampleEnum {

    @XmlEnumValue("1")
    VALUE1(1),
    @XmlEnumValue("2")
    VALUE2(2),
    @XmlEnumValue("3")
    VALUE3(3);

    int value;

    ExampleEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
