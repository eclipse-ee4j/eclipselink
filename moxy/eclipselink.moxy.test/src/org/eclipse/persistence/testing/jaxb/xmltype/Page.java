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
// dmccann - 2.3 - Initial implementation
package org.eclipse.persistence.testing.jaxb.xmltype;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name="Page", namespace="##default")
@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType (factoryMethod="", name="", propOrder={}, factoryClass=javax.xml.bind.annotation.XmlType.DEFAULT.class, namespace="##default")
public class Page implements Serializable {
    protected String isEmailLinkRequired;

    public Page() {
    }

    public String getIsEmailLinkRequired() {
        return isEmailLinkRequired;
    }

    public void setIsEmailLinkRequired(String value) {
        isEmailLinkRequired = value;
    }

    public boolean equals(Object o) {
        Page p;
        try {
            p = (Page) o;
        } catch (ClassCastException cce) {
            return false;
        }
        return this.isEmailLinkRequired.equals(p.getIsEmailLinkRequired());
    }
}
