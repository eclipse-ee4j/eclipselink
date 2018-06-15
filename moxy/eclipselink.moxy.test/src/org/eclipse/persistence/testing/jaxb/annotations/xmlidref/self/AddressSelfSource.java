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
//     Blaise Doughan - 2.4.3 - initial implementation
package org.eclipse.persistence.testing.jaxb.annotations.xmlidref.self;

import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.persistence.oxm.annotations.XmlPath;

@XmlRootElement(name="address")
public class AddressSelfSource {

    @XmlPath(".")
    public AddressSelfTarget address;

    @Override
    public boolean equals(Object obj) {
        if(null == obj || obj.getClass() != this.getClass()) {
            return false;
        }
        AddressSelfSource test = (AddressSelfSource) obj;
        if(null == address) {
            return null == test.address;
        } else {
            return address.equals(test.address);
        }
    }

}
