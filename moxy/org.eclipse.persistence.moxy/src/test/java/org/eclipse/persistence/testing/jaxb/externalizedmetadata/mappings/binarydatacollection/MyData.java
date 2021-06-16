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
// dmccann - March 31/2010 - 2.1 - Initial implementation
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.binarydatacollection;

import java.util.Arrays;
import java.util.List;

public class MyData {
    public List<byte[]> bytes;
    public List<byte[]> readOnlyBytes;
    public List<byte[]> writeOnlyBytes;

    @jakarta.xml.bind.annotation.XmlTransient
    public boolean wasGetCalled;
    @jakarta.xml.bind.annotation.XmlTransient
    public boolean wasSetCalled;

    public List<byte[]> getBytes() {
        wasGetCalled = true;
        return bytes;
    }

    public void setBytes(List<byte[]> bytes) {
        wasSetCalled = true;
        this.bytes = bytes;
    }

    public boolean equals(Object obj) {
        MyData mdObj;
        try {
            mdObj = (MyData) obj;
        } catch (ClassCastException cce) {
            return false;
        }
        if (bytes == null) {
            if (mdObj.bytes != null) {
                return false;
            }
        } else {
            if (mdObj.bytes == null) {
                return false;
            }
            for (byte[] b : bytes) {
                if (!bytesExistsInList(b, mdObj.bytes)) {
                    return false;
                }
            }
        }
        if (readOnlyBytes == null) {
            if (mdObj.readOnlyBytes != null) {
                return false;
            }
        } else {
            if (mdObj.readOnlyBytes == null) {
                return false;
            }
            for (byte[] b : readOnlyBytes) {
                if (!bytesExistsInList(b, mdObj.readOnlyBytes)) {
                    return false;
                }
            }
        }
        if (writeOnlyBytes == null) {
            if (mdObj.writeOnlyBytes != null) {
                return false;
            }
        } else {
            if (mdObj.writeOnlyBytes == null) {
                return false;
            }
            for (byte[] b : writeOnlyBytes) {
                if (!bytesExistsInList(b, mdObj.writeOnlyBytes)) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean bytesExistsInList(byte[] bites, List<byte[]> byteList) {
        for (byte[] b : byteList) {
            if (Arrays.equals(b, bites)) {
                return true;
            }
        }
        return false;
    }
}
