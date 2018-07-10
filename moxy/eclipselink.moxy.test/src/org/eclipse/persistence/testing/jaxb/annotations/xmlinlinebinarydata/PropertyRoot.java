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
//     Blaise Doughan - 2.3 - initial implementation
package org.eclipse.persistence.testing.jaxb.annotations.xmlinlinebinarydata;

import java.util.Arrays;

import javax.xml.bind.annotation.XmlInlineBinaryData;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class PropertyRoot {

    private byte[] a;

    private byte[] b;

    private byte[] c;

    public byte[] getA() {
        return a;
    }

    public void setA(byte[] a) {
        this.a = a;
    }

    @XmlInlineBinaryData
    public byte[] getB() {
        return b;
    }

    public void setB(byte[] b) {
        this.b = b;
    }

    public byte[] getC() {
        return c;
    }

    public void setC(byte[] c) {
        this.c = c;
    }

    public boolean equals(Object obj){
        if(obj instanceof PropertyRoot){
            PropertyRoot pr = ((PropertyRoot)obj);
            if(!Arrays.equals(a, pr.getA())){
                return false;
            }
            if(!Arrays.equals(b, pr.getB())){
                return false;
            }
            if(!Arrays.equals(c, pr.getC())){
                return false;
            }
            return true;
        }
        return false;
    }
}
