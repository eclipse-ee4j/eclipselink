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
//     Blaise Doughan - 2.2 - initial implementation
package org.eclipse.persistence.testing.jaxb.xmlanyelement.ns;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.persistence.testing.oxm.OXTestCase;
import org.w3c.dom.Element;

@XmlRootElement(name="root")
public class RootWithCollection {

    private List<Element> child;

    public RootWithCollection() {
        child = new ArrayList<Element>();
    }

    @XmlAnyElement
    public List<Element> getChild() {
        return child;
    }

    public void setChild(List<Element> child) {
        this.child = child;
    }

    @Override
    public boolean equals(Object obj) {
        if(null == obj || obj.getClass() != RootWithCollection.class) {
            return false;
        }
        RootWithCollection test = (RootWithCollection) obj;
        try {
            return child.size() == test.getChild().size();
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

}
