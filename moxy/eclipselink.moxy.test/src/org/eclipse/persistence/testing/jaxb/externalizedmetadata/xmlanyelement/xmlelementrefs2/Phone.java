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
//     Matt MacIvor - 2.3.1 - initial implementation
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlanyelement.xmlelementrefs2;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAttribute;

public class Phone {

    @XmlAttribute
    public int id;

    public String number;

    public Phone() {
    }

    public Phone(int i, String s) {
        id = i;
        number = s;
    }

    @Override
    public String toString() {
        return "Phone [" + id + "] " + number;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;

        if (!(obj instanceof Phone)) return false;

        Phone p = (Phone) obj;

        if (this.id != p.id) return false;

        if (!(this.number.equals(p.number))) return false;

        return true;
    }

}
