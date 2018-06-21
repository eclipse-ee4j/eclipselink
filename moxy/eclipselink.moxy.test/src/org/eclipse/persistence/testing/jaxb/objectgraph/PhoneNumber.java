/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Matt MacIvor - 2.5 - initial implementation
package org.eclipse.persistence.testing.jaxb.objectgraph;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

public class PhoneNumber {

    @XmlAttribute
    public String areaCode;

    @XmlValue
    public String number;

    public boolean equals(Object obj) {
        PhoneNumber pn = (PhoneNumber)obj;

        return (areaCode == pn.areaCode || areaCode.equals(pn.areaCode))
                && (number == pn.number || number.equals(pn.number));
    }
}
