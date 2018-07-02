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
package org.eclipse.persistence.testing.jaxb.xmlvalue.none;

import java.util.Map;

import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.namespace.QName;

@XmlTransient
public class ValidParent {

    private String parentProperty;
    private String parentAttributeProperty;
    private Map<QName, Object> anyAttributes;

    public String getParentProperty() {
        return parentProperty;
    }

    public void setParentProperty(String parentProperty) {
        this.parentProperty = parentProperty;
    }

    @XmlAttribute(name="parent-attribute")
    public String getParentAttributeProperty() {
        return parentAttributeProperty;
    }

    public void setParentAttributeProperty(String parentAttributeProperty) {
        this.parentAttributeProperty = parentAttributeProperty;
    }

    @XmlAnyAttribute
    public Map<QName, Object> getAnyAttributes() {
        return anyAttributes;
    }

    public void setAnyAttributes(Map<QName, Object> anyAttributes) {
        this.anyAttributes = anyAttributes;
    }

    @Override
    public boolean equals(Object obj) {
        if(null == obj || obj.getClass() != this.getClass()) {
            return false;
        }
        ValidParent test = (ValidParent) obj;
        if(!equals(parentProperty, test.getParentProperty())) {
            return false;
        }
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
