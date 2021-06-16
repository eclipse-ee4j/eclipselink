/*
 * Copyright (c) 2018, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.jaxb.xmlelementref.nills2;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;

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
