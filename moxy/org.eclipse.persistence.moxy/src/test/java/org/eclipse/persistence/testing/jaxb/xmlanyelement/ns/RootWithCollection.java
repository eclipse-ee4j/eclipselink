/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Blaise Doughan - 2.2 - initial implementation
package org.eclipse.persistence.testing.jaxb.xmlanyelement.ns;

import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAnyElement;
import jakarta.xml.bind.annotation.XmlRootElement;

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
