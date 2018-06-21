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
//     Praba Vijayaratnam - 2.4 - initial implementation
package org.eclipse.persistence.testing.jaxb.javadoc.xmltype;

import javax.xml.bind.annotation.*;

@XmlRootElement
public class Invoice {
    @XmlElement
    Address4 addr;

    public boolean equals(Object obj) {
        Invoice invObj = (Invoice) obj;
        Address4 addrObj = invObj.addr;
        return addr.name.equals(addrObj.name) && addr.city.equals(addrObj.city)
                && addr.street.equals(addrObj.street)
                && addr.state.equals(addrObj.state);
    }
}
