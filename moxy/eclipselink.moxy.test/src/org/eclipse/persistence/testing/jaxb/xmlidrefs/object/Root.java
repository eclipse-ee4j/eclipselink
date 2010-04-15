/*******************************************************************************
* Copyright (c) 1998, 2010 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
* which accompanies this distribution.
* The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
* and the Eclipse Distribution License is available at
* http://www.eclipse.org/org/documents/edl-v10.php.
*
* Contributors:
*     bdoughan - April 14/2010 - 2.1 - Initial implementation
******************************************************************************/
package org.eclipse.persistence.testing.jaxb.xmlidrefs.object;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

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