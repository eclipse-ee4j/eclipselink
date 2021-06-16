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
package org.eclipse.persistence.testing.jaxb.xmlvalue.none;

import java.util.Map;

import jakarta.xml.bind.annotation.XmlAnyAttribute;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlTransient;
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
