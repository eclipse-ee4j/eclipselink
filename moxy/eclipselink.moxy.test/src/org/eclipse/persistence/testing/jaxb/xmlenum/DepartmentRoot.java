/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Matt MacIvor - 2.3.3 - initial implementation
package org.eclipse.persistence.testing.jaxb.xmlenum;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlRootElement;

@XmlEnum(Integer.class)
@XmlRootElement(name="department")
public enum DepartmentRoot {
    @XmlEnumValue("1") J2EE,
    @XmlEnumValue("2") RDBMS,
    @XmlEnumValue("3") SALES,
    @XmlEnumValue("4") SUPPORT
}
