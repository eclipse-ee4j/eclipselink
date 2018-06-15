/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Blaise Doughan - 2.3.3 - initial implementation
package org.eclipse.persistence.testing.jaxb.annotations.xmlpath.attributecollection;

import javax.xml.bind.annotation.XmlID;

public class XmlPathCollectionRefAttributeChild {

    private String id;

    @XmlID
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object obj) {
        if(null == obj || obj.getClass() != this.getClass()) {
            return false;
        }
        XmlPathCollectionRefAttributeChild test = (XmlPathCollectionRefAttributeChild) obj;
        if(null == id) {
            return null == test.id;
        } else {
            return id.equals(test.id);
        }
    }

}
