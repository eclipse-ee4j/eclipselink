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
// Denise Smith- February 2010 - 2.1
package org.eclipse.persistence.testing.jaxb.xmlinlinebinary;

import java.util.Arrays;


@jakarta.xml.bind.annotation.XmlRootElement(name="my-data")
public class MyDataPropertyAnnotation {

    @jakarta.xml.bind.annotation.XmlInlineBinaryData
    public byte[] bytes;
    public Byte[] bigBytes;

    @jakarta.xml.bind.annotation.XmlInlineBinaryData
    @jakarta.xml.bind.annotation.XmlAttribute
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
