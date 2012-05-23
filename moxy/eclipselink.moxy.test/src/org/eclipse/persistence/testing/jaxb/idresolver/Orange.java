/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 *
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *  - rbarkhouse - 21 October 2011 - 2.4 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.idresolver;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement
class Orange {

    @XmlID
    @XmlAttribute
    String id;

    @XmlAttribute
    Integer orangeCode;

    @XmlElement
    String size;

    @XmlTransient
    boolean processed = false;

    @Override
    public String toString() {
        return "O(" + id + "|" + orangeCode + ")" + size + "|" + (processed ? "Ok" : "XX");
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Orange)) {
            return false;
        }
        Orange a = (Orange) obj;

        if (!this.id.equals(a.id)) {
            return false;
        }
        if (!this.orangeCode.equals(a.orangeCode)) {
            return false;
        }
        if (!this.size.equals(a.size)) {
            return false;
        }
        if (this.processed != a.processed) {
            return false;
        }

        return true;
    }

}