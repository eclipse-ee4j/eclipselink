/*
 * Copyright (c) 2013, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Blaise Doughan - 2.4.2 - initial implementation
package org.eclipse.persistence.testing.jaxb.json.wrapper;

import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlIDREF;

public class Employee {

    @XmlElementWrapper(namespace="urn:BAR")
    @XmlElement(name="phoneNumber")
    @XmlIDREF
    public List<PhoneNumber> phoneNumbers = new ArrayList<PhoneNumber>();

    @Override
    public boolean equals(Object obj) {
        if(null == obj || obj.getClass() != this.getClass()) {
            return false;
        }

        Employee test = (Employee) obj;
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
