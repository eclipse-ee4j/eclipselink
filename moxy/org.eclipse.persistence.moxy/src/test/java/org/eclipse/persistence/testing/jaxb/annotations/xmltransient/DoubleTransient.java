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
//     Rick Barkhouse - 2.3.3 - initial implementation
package org.eclipse.persistence.testing.jaxb.annotations.xmltransient;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;

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
