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
//     Praba Vijayaratnam - 2.3 - initial implementation
package org.eclipse.persistence.testing.jaxb.javadoc.xmlanyelement;

import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlAnyElement;
import java.util.Collection;
import java.util.Iterator;
//import java.util.List;

import org.w3c.dom.Element;
import org.eclipse.persistence.platform.xml.XMLComparer;

@XmlRootElement(name="xmlanyelementcollectionmodel")
public class XmlAnyElementCollectionModel {
    public int a;
    public int b;

    @XmlAnyElement
    public Collection any;


    public boolean equals(Object obj) {
        if(!(obj instanceof XmlAnyElementCollectionModel example)) {
            return false;
        }

        if(!(a == example.a)) {
            return false;
        }
        if(!(b == example.b)) {
            return false;
        }
        if(!(any.size() == example.any.size())) {
            return false;
        }

        Iterator any1 = this.any.iterator();
        Iterator any2 = example.any.iterator();
        XMLComparer comparer = new XMLComparer();

        while(any1.hasNext()) {
            Object next1 = any1.next();
            Object next2 = any2.next();

            if((next1 instanceof Element nextElem1) && (next2 instanceof Element nextElem2)) {
                if(!(comparer.isNodeEqual(nextElem1, nextElem2))) {
                    return false;
                }
            } else {
                if(!(next1.equals(next2))) {
                    return false;
                }
            }
        }
        return true;
    }

}
