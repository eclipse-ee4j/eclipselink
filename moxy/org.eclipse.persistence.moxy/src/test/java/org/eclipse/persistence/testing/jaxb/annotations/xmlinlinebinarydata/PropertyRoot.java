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
//     Blaise Doughan - 2.3 - initial implementation
package org.eclipse.persistence.testing.jaxb.annotations.xmlinlinebinarydata;

import java.util.Arrays;

import jakarta.xml.bind.annotation.XmlInlineBinaryData;
import jakarta.xml.bind.annotation.XmlRootElement;

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
