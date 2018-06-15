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
//     Matt MacIvor - January 18/2010 - 2.0 - Initial implementation
package org.eclipse.persistence.testing.oxm.mappings.binarydata;

public class MyImage {
    private byte[] myBytes;

    public byte[] getMyBytes() {
        return this.myBytes;
    }

    public void setMyBytes(byte[] myBytes) {
        this.myBytes = myBytes;
    }

    public boolean equals(Object obj) {
        if(obj == null || !(obj instanceof MyImage)) {
            return false;
        }

        MyImage image = (MyImage)obj;
        byte[] bytes1 = this.getMyBytes();
        byte[] bytes2 = image.getMyBytes();

        if(bytes1.length != bytes2.length) {
            for(int i = 0; i < bytes1.length; i++) {
                if(!(bytes1[i] == bytes2[i])) {
                    return false;
                }
            }
        }
        return true;
    }
}
