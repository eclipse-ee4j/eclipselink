/*******************************************************************************
 * Copyright (c) 2011, 2015 Oracle and/or its affiliates. All rights reserved.
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Apple apple = (Apple) o;

        if (processed != apple.processed) return false;
        if (appleChar != null ? !appleChar.equals(apple.appleChar) : apple.appleChar != null) return false;
        if (id != null ? !id.equals(apple.id) : apple.id != null) return false;
        if (type != null ? !type.equals(apple.type) : apple.type != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (appleChar != null ? appleChar.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (processed ? 1 : 0);
        return result;
    }
}
