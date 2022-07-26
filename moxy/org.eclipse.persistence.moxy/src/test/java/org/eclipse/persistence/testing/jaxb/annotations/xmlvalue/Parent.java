/*
 * Copyright (c) 2014, 2021 Oracle and/or its affiliates. All rights reserved.
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

import jakarta.xml.bind.annotation.XmlAttribute;

public class Parent {

    private String parentAttributeProperty;

    @XmlAttribute(name="attribute")
    public String getParentAttributeProperty() {
        return parentAttributeProperty;
    }

    public void setParentAttributeProperty(String parentAttributeProperty) {
        this.parentAttributeProperty = parentAttributeProperty;
    }

    @Override
    public boolean equals(Object obj) {
        if(null == obj || obj.getClass() != this.getClass()) {
            return false;
        }
        Parent test = (Parent) obj;
        return equals(parentAttributeProperty, test.getParentAttributeProperty());
    }

    private boolean equals(Object control, Object test) {
        if(null == control) {
            return null == test;
        } else {
            return control.equals(test);
        }
    }
}
