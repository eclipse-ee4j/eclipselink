/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Blaise Doughan - 2.3.2 - initial implementation
 ******************************************************************************/
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