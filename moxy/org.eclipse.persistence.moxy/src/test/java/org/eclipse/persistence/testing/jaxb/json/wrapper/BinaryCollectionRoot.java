/*
 * Copyright (c) 2013, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Blaise Doughan - 2.4.2 - initial implementation
package org.eclipse.persistence.testing.jaxb.json.wrapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlType;

import org.eclipse.persistence.oxm.annotations.XmlPath;

@XmlType(propOrder={"wrapperItems", "xmlPathItems"})
public class BinaryCollectionRoot {

    private List<byte[]> wrapperItems = new ArrayList<byte[]>(2);
    private List<byte[]> xmlPathItems = new ArrayList<byte[]>(2);

    @XmlElementWrapper
    @XmlElement(name="item")
    public List<byte[]> getWrapperItems() {
        return wrapperItems;
    }

    public void setWrapperItems(List<byte[]> wrapperItems) {
        this.wrapperItems = wrapperItems;
    }

    @XmlPath("xmlPathItems/item")
    public List<byte[]> getXmlPathItems() {
        return xmlPathItems;
    }

    public void setXmlPathItems(List<byte[]> xmlPathItems) {
        this.xmlPathItems = xmlPathItems;
    }

    @Override
    public boolean equals(Object obj) {
        if(null == obj || obj.getClass() != this.getClass()) {
            return false;
        }
        BinaryCollectionRoot test = (BinaryCollectionRoot) obj;

        if(!equals(wrapperItems, test.wrapperItems)) {
            return false;
        }
        if(!equals(xmlPathItems, test.xmlPathItems)) {
            return false;
        }
        return true;
    }

    private boolean equals(List<byte[]> control, List<byte[]> test) {
        if(control == test) {
            return true;
        }
        if(null == control || null == test) {
            return false;
        }
        if(control.size() != test.size()) {
            return false;
        }
        for(int x=0; x<control.size(); x++) {
            if(!Arrays.equals(control.get(x), test.get(x))) {
                return false;
            }
        }
        return true;
    }

}
