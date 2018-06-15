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
// mmacivor - 2.3 - Initial implementation
package org.eclipse.persistence.testing.jaxb.jaxbelement.enumeration;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;

@XmlEnum
public enum Coin {
    @XmlEnumValue("25")QUARTER,
    @XmlEnumValue("10")DIME,
    @XmlEnumValue("5")NICKEL,
    @XmlEnumValue("1")PENNY
}
