/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Denise Smith   April 2013
package org.eclipse.persistence.testing.jaxb.xmlenum.xmlvalue;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;

@XmlEnum(Price.class)
public enum Coin {
    @XmlEnumValue("1") penny(1),
    @XmlEnumValue("5") nickel(5),
    @XmlEnumValue("10") dime(10),
    @XmlEnumValue("25") quarter(25);

    Coin(int value) { this.value = value; }

    private final int value;

    public int value() { return value; }
}
