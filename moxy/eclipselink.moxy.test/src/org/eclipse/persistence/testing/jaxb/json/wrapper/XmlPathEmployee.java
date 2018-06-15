/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Blaise Doughan - 2.4.2 - initial implementation
package org.eclipse.persistence.testing.jaxb.json.wrapper;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlIDREF;

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
