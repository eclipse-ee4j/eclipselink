/*******************************************************************************
 * Copyright (c) 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Blaise Doughan - 2.4.2 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.json.any;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Root {

    private List<JAXBElement> items = new ArrayList<JAXBElement>(1);

    @XmlAnyElement(lax = true)
    public List<JAXBElement> getItems() {
        return items;
    }

    public void setItems(List<JAXBElement> items) {
        this.items = items;
    }

    @Override
    public boolean equals(Object obj) {
        if(null == obj || obj.getClass() != this.getClass()) {
            return false;
        }
        Root test = (Root) obj;
        return equals(items, test.getItems());
    }

    private boolean equals(List<?> control, List<?> test) {
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
            if(!control.get(x).equals(test.get(x))) {
                return false;
            }
        }
        return true;
    }
}
