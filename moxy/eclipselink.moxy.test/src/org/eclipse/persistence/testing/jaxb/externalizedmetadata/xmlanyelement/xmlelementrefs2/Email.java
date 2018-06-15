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

public class Email {

    @XmlAttribute
    public int id;

    public String address;

    public Email() {
    }

    public Email(int i, String s) {
        id = i;
        address = s;
    }

    @Override
    public String toString() {
        return "Email [" + id + "] " + address;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;

        if (!(obj instanceof Email)) return false;

        Email e = (Email) obj;

        if (this.id != e.id) return false;

        if (!(this.address.equals(e.address))) return false;

        return true;
    }

}
