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
//     Blaise Doughan - 2.3 - Initial implementation
package org.eclipse.persistence.testing.jaxb.annotations.xmlpath.enumtype;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.persistence.oxm.annotations.XmlPath;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Customer {

    @XmlPath("status/level/text()")
    private StatusEnum level;

    public StatusEnum getLevel() {
        return level;
    }

    public void setLevel(StatusEnum level) {
        this.level = level;
    }

    @Override
    public boolean equals(Object obj) {
        if(null == obj || obj.getClass() != Customer.class) {
            return false;
        }
        Customer test = (Customer) obj;
        return level == test.getLevel();
    }

}
