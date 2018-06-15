/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.oxm.mappings.serializedobject;

import java.io.Serializable;

public class SerializableAddress implements Serializable {
    public String theAddress;

    public String getTheAddress() {
        return theAddress;
    }
    public void setTheAddress(String addr) {
        theAddress = addr;
    }

    public boolean equals(Object obj) {
        if(!(obj instanceof SerializableAddress)) {
            return false;
        }
        return getTheAddress().equalsIgnoreCase(((SerializableAddress)obj).getTheAddress());
    }
}
