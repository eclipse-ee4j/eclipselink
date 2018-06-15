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
//     Blaise Doughan - 2.3 - initial implementation
package org.eclipse.persistence.testing.jaxb.xmlvalue.none;

import javax.xml.bind.annotation.XmlValue;

public class InvalidChild extends InvalidParent {

    private String childProperty;

    @XmlValue
    public String getChildProperty() {
        return childProperty;
    }

    public void setChildProperty(String childProperty) {
        this.childProperty = childProperty;
    }

    @Override
    public boolean equals(Object obj) {
        if(!super.equals(obj)) {
            return false;
        }
        ValidChild test = (ValidChild) obj;
        if(null == childProperty) {
            return null == test.getChildProperty();
        } else {
            return childProperty.equals(test.getChildProperty());
        }
    }

}
