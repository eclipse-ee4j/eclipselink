/*
 * Copyright (c) 2011, 2024 Oracle and/or its affiliates. All rights reserved.
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
//  - rbarkhouse - 08 September 2011 - 2.4 - Initial implementation
package org.eclipse.persistence.testing.jaxb.sun.xmllocation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import org.glassfish.jaxb.core.annotation.XmlLocation;
import org.xml.sax.Locator;

@XmlRootElement(name = "data")
public class DataNT {

    @XmlElement
    public String key;

    @XmlElement
    public String data1;
    @XmlElement
    public String data2;
    @XmlElement
    public String data3;

    @XmlElement
    public List<SubDataNT> subData = new ArrayList<SubDataNT>();

    @XmlLocation
    public Locator locator;

    @Override
    public String toString() {
        String loc = " noLoc";
        if (locator != null) {
            loc = " L" + locator.getLineNumber() + " C" + locator.getColumnNumber() + " " + locator.getSystemId();
        }

        StringBuilder subDataS = new StringBuilder("\n");
        for (Iterator<SubDataNT> iterator = subData.iterator(); iterator.hasNext();) {
            SubDataNT type = iterator.next();
            subDataS.append("\t").append(type.toString()).append("\n");
        }

        return "\nData(" + key + ")" + loc + subDataS;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof DataNT d)) {
            return false;
        }

        if (!(d.key.equals(this.key))) {
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

        if (!(this.subData.equals(d.subData))) {
            return false;
        }

        return true;
    }

}
