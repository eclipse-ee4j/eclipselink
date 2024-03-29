/*
 * Copyright (c) 2013, 2024 Oracle and/or its affiliates. All rights reserved.
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
//     Blaise Doughan - 2.4.2 - initial implementation
package org.eclipse.persistence.testing.jaxb.unmarshaller.validation;

import jakarta.xml.bind.annotation.XmlType;

@XmlType(propOrder={"province", "postalCode"})
public class CanadianAddress extends Address {

    public String province;
    public String postalCode;

    @Override
    public boolean equals(Object obj) {
        if(null == obj.getClass() || obj.getClass() != this.getClass()) {
            return false;
        }
        if(!super.equals(obj)) {
            return false;
        }
        CanadianAddress test = (CanadianAddress) obj;
        if(!province.equals(test.province)) {
            return false;
        }
        if(!postalCode.equals(test.postalCode)) {
            return false;
        }
        return true;
    }

}
