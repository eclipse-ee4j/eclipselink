/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmltransient.unset.prop;

import javax.xml.bind.annotation.XmlTransient;

public class PhoneNumber {
    private String type;
    private String number;

    @XmlTransient
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getNumber() {
        return number;
    }
    public void setNumber(String number) {
        this.number = number;
    }

    public boolean equals(Object obj) {
        PhoneNumber num = (PhoneNumber)obj;
        return type.equals(num.getType()) && number.equals(num.getNumber());
    }
}
