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
//     Matt MacIvor - 2.4.1 - initial implementation
package org.eclipse.persistence.testing.jaxb.xmladapter.enumeration;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

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

    private ExampleEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
