/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
// dmccann - January 13/2010 - 2.0 - Initial implementation
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlinlinebinarydata;

import java.util.Arrays;

import javax.activation.DataHandler;

@javax.xml.bind.annotation.XmlRootElement(name="my-data")
@javax.xml.bind.annotation.XmlInlineBinaryData
public class MyDataClassAnnotation {
    private DataHandler myDataHandler;

    public byte[] bytes;

    public DataHandler getData() {
        return myDataHandler;
    }

    public void setData(DataHandler data) {
        myDataHandler = data;
    }

    public boolean equals(Object obj) {
        MyDataClassAnnotation mdObj;
        try {
            mdObj = (MyDataClassAnnotation) obj;
        } catch (ClassCastException cce) {
            return false;
        }
        return Arrays.equals(bytes, mdObj.bytes);
    }
}
