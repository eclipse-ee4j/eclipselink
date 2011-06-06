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
 *     Blaise Doughan - 2.3 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.annotations.xmlpath.predicate.ns;

import java.awt.ItemSelectable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.persistence.oxm.annotations.XmlPath;
import org.eclipse.persistence.oxm.annotations.XmlPaths;

@XmlRootElement(name="root")
@XmlType(propOrder={"item", "items"})
public class XPathsRoot {

    @XmlElements({
        @XmlElement(type=Integer.class),
        @XmlElement(type=Address.class)
    })
    @XmlPaths({
        @XmlPath("item[@type='integer']/text()"),
        @XmlPath("item[@type='address']")
    })
    private Object item;

    @XmlElements({
        @XmlElement(type=String.class),
        @XmlElement(type=PhoneNumber.class)
    })
    @XmlPaths({
        @XmlPath("item[@type='string']/text()"),
        @XmlPath("item[@type='phone-number']")
    })
    private List<Object> items;

    public XPathsRoot() {
        items = new ArrayList<Object>(2);
    }

    public Object getItem() {
        return item;
    }

    public void setItem(Object item) {
        this.item = item;
    }

    public List<Object> getItems() {
        return items;
    }

    public void setItems(List<Object> items) {
        this.items = items;
    }

    @Override
    public boolean equals(Object obj) {
        if(null == obj || obj.getClass() != this.getClass()) {
            return false;
        }
        XPathsRoot test = (XPathsRoot) obj;

        if(null == item && null != test.getItem()) {
            return false;
        } else if(!item.equals(test.getItem())) {
            return false;
        }

        List testItems = test.getItems();
        int itemsSize = items.size();
        if(itemsSize != testItems.size()) {
            return false;
        }
        for(int x=0; x<itemsSize; x++) {
            if(!items.get(x).equals(testItems.get(x))) {
                return false;
            }
        }
        return true;
    }

}