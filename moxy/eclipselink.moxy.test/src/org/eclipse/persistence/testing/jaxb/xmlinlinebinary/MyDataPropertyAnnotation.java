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
 * Denise Smith- February 2010 - 2.1 
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.xmlinlinebinary;

import java.util.Arrays;


@javax.xml.bind.annotation.XmlRootElement(name="my-data")
public class MyDataPropertyAnnotation {

    @javax.xml.bind.annotation.XmlInlineBinaryData    
    public byte[] bytes;
    
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
        if(!Arrays.equals(bytesAttr, mdObj.bytesAttr)){
        	return false;
        }
        return true;
    }
}
