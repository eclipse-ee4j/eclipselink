/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Blaise Doughan - 2.2 - initial implementation
package org.eclipse.persistence.testing.jaxb.xmladapter.direct.objectlist;

public class PhoneNumber {

    String value;

    public PhoneNumber(String value) {
        this.value = value;
    }

    public boolean equals(Object object) {
        if(null == object || PhoneNumber.class != object.getClass()) {
            return false;
        }
        PhoneNumber test = (PhoneNumber) object;
        if(null == value) {
            return null == test.value;
        } else {
            return value.equals(test.value);
        }
    }

}
