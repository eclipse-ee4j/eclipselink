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
//  - rbarkhouse - 04 October 2011 - 2.4 - Initial implementation
package org.eclipse.persistence.testing.jaxb.annotations.xmllocation;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import org.eclipse.persistence.oxm.annotations.XmlLocation;
import org.xml.sax.Locator;

@XmlAccessorType(XmlAccessType.PROPERTY)
public class SubData {

    @XmlElement
    public String info;

    private Locator loc;

    public Locator getLoc() {
        return loc;
    }

    @XmlLocation
    @XmlTransient
    public void setLoc(Locator locator) {
        this.loc = locator;
    }

    @Override
    public String toString() {
        String sloc = " noLoc";
        if (loc != null) {
            sloc = " L" + loc.getLineNumber() + " C" + loc.getColumnNumber() + " " + loc.getSystemId();
        }

        return "SubData(" + info + ")" + sloc;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof SubData)) {
            return false;
        }

        SubData d = (SubData) obj;

        if (!(d.info.equals(this.info))) {
            return false;
        }

        if (d.loc == null && this.loc != null) {
            return false;
        }
        if (d.loc != null && this.loc == null) {
            return false;
        }

        if (this.loc != null && d.loc != null) {
            if (!(this.loc.equals(d.loc))) {
                return false;
            }
        }

        return true;
    }

}
