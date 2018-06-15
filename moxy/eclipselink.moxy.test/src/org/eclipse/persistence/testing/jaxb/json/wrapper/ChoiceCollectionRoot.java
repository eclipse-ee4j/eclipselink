/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Blaise Doughan - 2.4.2 - initial implementation
package org.eclipse.persistence.testing.jaxb.json.wrapper;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.*;

public class ChoiceCollectionRoot {

    private List<Object> wrapperItems = new ArrayList<Object>();

    @XmlElementWrapper
    @XmlElements({
        @XmlElement(name="foo", type=String.class),
        @XmlElement(name="bar", type=Integer.class)
    })
    public List<Object> getWrapperItems() {
        return wrapperItems;
    }

    public void setWrapperItems(List<Object> items) {
        this.wrapperItems = items;
    }

    @Override
    public boolean equals(Object obj) {
        if(null == obj || obj.getClass() != this.getClass()) {
            return false;
        }
        ChoiceCollectionRoot test = (ChoiceCollectionRoot) obj;
        return true;
    }

    private boolean equals(List<?> control, List<?> test) {
        if(control == test) {
            return true;
        }
        if(null == control || null == test) {
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
