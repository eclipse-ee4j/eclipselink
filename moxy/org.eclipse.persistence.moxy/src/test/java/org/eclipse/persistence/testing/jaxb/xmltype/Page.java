/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
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
// dmccann - 2.3 - Initial implementation
package org.eclipse.persistence.testing.jaxb.xmltype;

import java.io.Serializable;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlRootElement(name="Page", namespace="##default")
@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType (factoryMethod="", name="", propOrder={}, factoryClass=jakarta.xml.bind.annotation.XmlType.DEFAULT.class, namespace="##default")
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
