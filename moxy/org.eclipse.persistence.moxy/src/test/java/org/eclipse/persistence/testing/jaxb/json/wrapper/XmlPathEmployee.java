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
package org.eclipse.persistence.testing.jaxb.json.wrapper;

import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.XmlIDREF;

import org.eclipse.persistence.oxm.annotations.XmlPath;

public class XmlPathEmployee {

    @XmlPath("phoneNumbers/phoneNumber/text()")
    @XmlIDREF
    public List<PhoneNumber> phoneNumbers = new ArrayList<PhoneNumber>();

    @Override
    public boolean equals(Object obj) {
        if(null == obj || obj.getClass() != this.getClass()) {
            return false;
        }

        XmlPathEmployee test = (XmlPathEmployee) obj;
        if(null == phoneNumbers) {
            if(null == test.phoneNumbers) {
                return true;
            } else {
                return false;
            }
        }
        if(phoneNumbers.size() != test.phoneNumbers.size()) {
            return false;
        }
        for(int x=0; x<phoneNumbers.size(); x++) {
            if(!phoneNumbers.get(x).equals(test.phoneNumbers.get(x))) {
                return false;
            }
        }
        return true;
    }

}
