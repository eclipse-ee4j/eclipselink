/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Matt MacIvor - 2.3.1 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlanyelement.xmlelementrefs;

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
