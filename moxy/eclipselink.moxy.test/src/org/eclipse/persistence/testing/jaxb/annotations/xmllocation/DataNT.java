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
 *  - rbarkhouse - 08 September 2011 - 2.4 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.annotations.xmllocation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.eclipse.persistence.oxm.annotations.XmlLocation;
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

        String subDataS = "\n";
        for (Iterator iterator = subData.iterator(); iterator.hasNext();) {
            SubDataNT type = (SubDataNT) iterator.next();
            subDataS += "\t" + type.toString() + "\n";
        }

        return "\nData(" + key + ")" + loc + subDataS;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof DataNT)) {
            return false;
        }

        DataNT d = (DataNT) obj;

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
            boolean lines = this.locator.getLineNumber() == d.locator.getLineNumber();
            boolean columns = this.locator.getColumnNumber() == d.locator.getColumnNumber();

            String sysId1 = this.locator.getSystemId() == null ? "" : this.locator.getSystemId();
            String sysId2 = d.locator.getSystemId() == null ? "" : d.locator.getSystemId();

            boolean sysIds = sysId1.equals(sysId2);

            if (!(lines && columns && sysIds)) {
                return false;
            }
        }

        if (!(this.subData.equals(d.subData))) {
            return false;
        }

        return true;
    }

}
