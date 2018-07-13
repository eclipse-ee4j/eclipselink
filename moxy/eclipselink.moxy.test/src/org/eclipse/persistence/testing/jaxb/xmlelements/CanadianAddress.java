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
//     Matt MacIvor - 2.4 - initial implementation
package org.eclipse.persistence.testing.jaxb.xmlelements;

import javax.xml.bind.annotation.XmlType;

@XmlType(name="canadian-address")
public class CanadianAddress extends Address {
    public String postalCode;

    public boolean equals(Object obj) {
        CanadianAddress addr = (CanadianAddress)obj;

        return addr.postalCode.equals(this.postalCode) && super.equals(obj);
    }
}
