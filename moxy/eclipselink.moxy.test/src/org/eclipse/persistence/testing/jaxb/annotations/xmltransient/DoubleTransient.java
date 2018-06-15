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
//     Rick Barkhouse - 2.3.3 - initial implementation
package org.eclipse.persistence.testing.jaxb.annotations.xmltransient;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement
@XmlAccessorType(XmlAccessType.PROPERTY)
public class DoubleTransient {

    @XmlTransient
    public String bar = "FOO";

    public String getBar() {
        return "BAZ";
    }

    @XmlTransient
    public void setBar(String bar) {
        this.bar = bar;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof DoubleTransient) {
            return true;
        }
        return false;
    }

}
