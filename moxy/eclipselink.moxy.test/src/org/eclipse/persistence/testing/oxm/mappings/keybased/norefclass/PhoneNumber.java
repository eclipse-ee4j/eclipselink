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
//     bdoughan - April 9/2010 - 2.1 - Initial implementation
package org.eclipse.persistence.testing.oxm.mappings.keybased.norefclass;

import java.math.BigInteger;

public class PhoneNumber {

    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object object) {
        try {
            if(null == object) {
                return false;
            }
            PhoneNumber testPhoneNumber = (PhoneNumber) object;
            if(null == id) {
                return null == testPhoneNumber.getId();
            } else {
                return id.equals(testPhoneNumber.getId());
            }
        } catch(ClassCastException e) {
            return false;
        }
    }

}
