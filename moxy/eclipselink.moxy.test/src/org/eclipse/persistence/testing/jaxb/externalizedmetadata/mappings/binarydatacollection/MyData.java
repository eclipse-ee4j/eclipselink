/*******************************************************************************
 * Copyright (c) 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * dmccann - March 31/2010 - 2.1 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.binarydatacollection;

import java.util.Arrays;
import java.util.List;

public class MyData {
    public List<byte[]> bytes;
    public List<byte[]> readOnlyBytes;
    public List<byte[]> writeOnlyBytes;

    @javax.xml.bind.annotation.XmlTransient
    public boolean wasGetCalled;
    @javax.xml.bind.annotation.XmlTransient
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
