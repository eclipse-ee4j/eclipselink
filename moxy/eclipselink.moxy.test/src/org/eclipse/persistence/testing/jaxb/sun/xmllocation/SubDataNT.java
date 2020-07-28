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
//  - rbarkhouse - 04 October 2011 - 2.4 - Initial implementation
package org.eclipse.persistence.testing.jaxb.sun.xmllocation;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import com.sun.xml.bind.annotation.XmlLocation;
import org.xml.sax.Locator;

public class SubDataNT {

    @XmlElement
    public String info;

    @XmlLocation
    public Locator locator;

    @Override
    public String toString() {
        String loc = " noLoc";
        if (locator != null) {
            loc = " L" + locator.getLineNumber() + " C" + locator.getColumnNumber() + " " + locator.getSystemId();
        }

        return "SubData(" + info + ")" + loc;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof SubDataNT)) {
            return false;
        }

        SubDataNT d = (SubDataNT) obj;

        if (!(d.info.equals(this.info))) {
            return false;
        }

        if (d.locator == null && this.locator != null) {
            return false;
        }
        if (d.locator != null && this.locator == null) {
            return false;
        }

        if (this.locator != null && d.locator != null) {
            if (!(this.locator.equals(d.locator))) {
                return false;
            }
        }

        return true;
    }

}
