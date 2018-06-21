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
// Denise Smith- February 2010 - 2.1
package org.eclipse.persistence.testing.jaxb.xmlinlinebinary;

import java.util.Arrays;


@javax.xml.bind.annotation.XmlRootElement(name="my-data")
public class MyDataPropertyAnnotation {

    @javax.xml.bind.annotation.XmlInlineBinaryData
    public byte[] bytes;
    public Byte[] bigBytes;

    @javax.xml.bind.annotation.XmlInlineBinaryData
    @javax.xml.bind.annotation.XmlAttribute
    public byte[] bytesAttr;
    public boolean equals(Object obj) {
        MyDataPropertyAnnotation mdObj;
        try {
            mdObj = (MyDataPropertyAnnotation) obj;
        } catch (ClassCastException cce) {
            return false;
        }
        if(!Arrays.equals(bytes, mdObj.bytes)){
            return false;
        }
        if(!Arrays.equals(bigBytes, mdObj.bigBytes)){
            return false;
        }
        if(!Arrays.equals(bytesAttr, mdObj.bytesAttr)){
            return false;
        }
        return true;
    }
}
