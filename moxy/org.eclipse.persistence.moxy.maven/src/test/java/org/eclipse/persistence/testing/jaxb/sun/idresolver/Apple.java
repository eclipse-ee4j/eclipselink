/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
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
//  - rbarkhouse - 21 October 2011 - 2.4 - Initial implementation
package org.eclipse.persistence.testing.jaxb.sun.idresolver;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.eclipse.persistence.oxm.annotations.XmlKey;

@XmlRootElement
class Apple {

    @XmlID
    @XmlAttribute
    String id;

    @XmlKey
    @XmlAttribute
    Character appleChar;

    @XmlElement
    String type;

    @XmlTransient
    boolean processed = false;

    @Override
    public String toString() {
        return "A(" + id + "|" + appleChar + ")" + type + "|" + (processed ? "Ok" : "XX");
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Apple)) {
            return false;
        }
        Apple a = (Apple) obj;

        if (!this.id.equals(a.id)) {
            return false;
        }
        if (!this.appleChar.equals(a.appleChar)) {
            return false;
        }
        if (!this.type.equals(a.type)) {
            return false;
        }
        if (this.processed != a.processed) {
            return false;
        }

        return true;
    }

}
