/*
 * Copyright (c) 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation
package org.eclipse.persistence.testing.jaxb.annotations.xmlvalue;

import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlValue;

@XmlRootElement(name="root")
public class Child extends Parent {

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
        Child test = (Child) obj;
        if(null == childProperty) {
            return null == test.getChildProperty();
        } else {
            return childProperty.equals(test.getChildProperty());
        }
    }
}
