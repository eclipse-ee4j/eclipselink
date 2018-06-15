/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.jaxb.xmlelementref.nills2;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

@XmlAccessorType(XmlAccessType.FIELD)
public class Bar {

    @XmlAttribute
    private String data;

    public Bar() {
    }

    public Bar(final String data) {
        this.data = data;
    }

    public String getData() {
        return this.data;
    }

    public void setData(final String data) {
        this.data = data;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Bar)) {
            return false;
        }
        Bar a = (Bar) obj;
        if (a.getData() == null & this.getData() == null) {
            return true;
        } else if (a.getData() != null && this.getData() != null && a.getData().equals(this.getData())) {
            return true;
        } else {
            return false;
        }
    }

}
