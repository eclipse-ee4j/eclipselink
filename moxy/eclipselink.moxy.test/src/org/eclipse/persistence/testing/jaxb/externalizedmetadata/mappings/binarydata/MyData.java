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
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.binarydata;

import java.util.Arrays;

public class MyData {
    public byte[] bytes;
    public byte[] readOnlyBytes;
    public byte[] writeOnlyBytes;
    
    @javax.xml.bind.annotation.XmlTransient
    public boolean wasGetCalled;
    @javax.xml.bind.annotation.XmlTransient
    public boolean wasSetCalled;

    public byte[] getBytes() {
        wasGetCalled = true;
        return bytes;
    }

    public void setBytes(byte[] bytes) {
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
        } else if (mdObj.bytes == null) {
            return false;
        }
        if (!Arrays.equals(bytes, mdObj.bytes)) {
            return false;
        }
        if (readOnlyBytes == null) {
            if (mdObj.readOnlyBytes != null) {
                return false;
            }
        } else if (mdObj.readOnlyBytes == null) {
            return false;
        }
        if (!Arrays.equals(readOnlyBytes, mdObj.readOnlyBytes)) {
            return false;
        }
        if (writeOnlyBytes == null) {
            if (mdObj.writeOnlyBytes != null) {
                return false;
            }
        } else if (mdObj.writeOnlyBytes == null) {
            return false;
        }
        if (!Arrays.equals(writeOnlyBytes, mdObj.writeOnlyBytes)) {
            return false;
        }
        return true;
    }
}
