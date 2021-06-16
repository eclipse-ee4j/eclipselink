/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     bdoughan - April 14/2010 - 2.1 - Initial implementation
package org.eclipse.persistence.testing.jaxb.xmlidrefs.object;

import java.util.List;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="root")
public class Root {

    @XmlElement(name="employee")
    public Employee employee;

    @XmlElement(name="phone-number")
    public List<PhoneNumber> phoneNumbers;

    public boolean equals(Object object) {
        try {
            if(null == object) {
                return false;
            }
            Root testRoot = (Root) object;
            if(null == employee && null != testRoot.employee) {
                return false;
            }
            if(!employee.equals(testRoot.employee)) {
                return false;
            }
            List testRootPhoneNumbers = testRoot.phoneNumbers;
            if(phoneNumbers == null) {
                return testRootPhoneNumbers == null;
            }
            int phonesSize = phoneNumbers.size();
            if(phonesSize != testRootPhoneNumbers.size()) {
                return false;
            }
            for(int x=0; x<phonesSize; x++) {
                if(!phoneNumbers.get(x).equals(testRootPhoneNumbers.get(x))) {
                    return false;
                }
            }
            return true;
        } catch(Exception e) {
            return false;
        }
    }

}
