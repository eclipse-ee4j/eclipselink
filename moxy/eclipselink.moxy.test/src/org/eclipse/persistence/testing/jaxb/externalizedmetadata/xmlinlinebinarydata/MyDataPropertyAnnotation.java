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
 * dmccann - January 13/2010 - 2.0 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlinlinebinarydata;

import java.util.Arrays;

import javax.activation.DataHandler;

@javax.xml.bind.annotation.XmlRootElement(name="my-data")
public class MyDataPropertyAnnotation {
    private DataHandler myDataHandler;

    @javax.xml.bind.annotation.XmlInlineBinaryData
    public byte[] bytes;
    
    @javax.xml.bind.annotation.XmlInlineBinaryData
    public DataHandler getData() { return myDataHandler; }
    public void setData(DataHandler data) { myDataHandler = data; }

    public boolean equals(Object obj) {
        MyDataPropertyAnnotation mdObj;
        try {
            mdObj = (MyDataPropertyAnnotation) obj;
        } catch (ClassCastException cce) {
            return false;
        }
        return Arrays.equals(bytes, mdObj.bytes);
    }
}
