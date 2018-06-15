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
//     Blaise Doughan - 2.3.2 - initial implementation
package org.eclipse.persistence.testing.jaxb.xmladapter.bytearray;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlRootElement
public class Root {

    private BinaryData binaryData;

    @XmlJavaTypeAdapter(BinaryDataAdapter.class)
    public BinaryData getBinaryData() {
        return binaryData;
    }

    public void setBinaryData(BinaryData binaryData) {
        this.binaryData = binaryData;
    }

    @Override
    public boolean equals(Object obj) {
        if(null == obj || obj.getClass() != this.getClass()) {
            return false;
        }
        Root test = (Root) obj;
        if(null == binaryData) {
            return null == test.getBinaryData();
        }
        return binaryData.equals(test.getBinaryData());
    }

}
