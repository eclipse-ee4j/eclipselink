/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Blaise Doughan - 2.3.2 - initial implementation
package org.eclipse.persistence.testing.jaxb.xmladapter.bytearray;

public class BinaryData {

    private byte[] bytes;

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    @Override
    public boolean equals(Object obj) {
         if(obj == null || obj.getClass() != this.getClass()) {
             return false;
         }
         BinaryData test = (BinaryData) obj;
         byte[] testBytes = test.getBytes();
         if(bytes.length != testBytes.length) {
             return false;
         }
         for(int x=0; x<bytes.length; x++) {
              if(bytes[x] != testBytes[x]) {
                  return false;
              }
         }
         return true;
    }

}
