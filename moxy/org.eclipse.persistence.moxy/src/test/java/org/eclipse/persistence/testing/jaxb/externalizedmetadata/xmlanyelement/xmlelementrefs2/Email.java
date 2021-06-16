/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Matt MacIvor - 2.3.1 - initial implementation
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlanyelement.xmlelementrefs2;

import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlAttribute;

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
