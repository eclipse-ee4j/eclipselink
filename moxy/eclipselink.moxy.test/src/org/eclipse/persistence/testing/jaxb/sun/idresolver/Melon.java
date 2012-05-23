/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 *
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *  - rbarkhouse - 27 February - 2.3.3 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.sun.idresolver;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.eclipse.persistence.oxm.annotations.XmlKey;

@XmlRootElement
class Melon {

    @XmlID
    @XmlAttribute
    String id;

    @XmlElement
    String type;

    @XmlTransient
    boolean processed = false;

    @Override
    public String toString() {
        return "M(" + id + ")" + type + "|" + (processed ? "Ok" : "XX");
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Melon)) {
            return false;
        }
        Melon a = (Melon) obj;

        if (!this.id.equals(a.id)) {
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
