/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     rbarkhouse - 2009-04-14 - 2.0 - Initial implementation

package org.eclipse.persistence.testing.oxm.mappings.compositeobject.keepaselement;

import org.w3c.dom.Element;

public class Elem {

    protected Object elem;
    protected Object elem1;

    public Object getElem() {
        return elem;
    }

    public void setElem(Object value) {
        this.elem = value;
    }

    public Object getElem1() {
        return elem1;
    }

    public void setElem1(Object value) {
        this.elem1 = value;
    }

    public boolean equals(Object object) {
        if (object instanceof Elem) {
            Elem anotherElem = (Elem) object;
            if (elem == null && anotherElem.getElem() != null) {
                return false;
            }
            if (elem != null && anotherElem.getElem() == null) {
                return false;
            }
            if (elem1 == null && anotherElem.getElem1() != null) {
                return false;
            }
            if (elem1 != null && anotherElem.getElem1() == null) {
                return false;
            }

            Object obj1 = elem;
            Object obj2 = anotherElem.getElem();
            if ((obj1 instanceof Element) && (obj2 instanceof Element)) {
                Element elem1 = (Element )obj1;
                Element elem2 = (Element) obj2;

                if (!(elem1.getLocalName().equals(elem2.getLocalName()))) {
                    return false;
                }
            } else {
                return false;
            }

            Object obj3 = elem1;
            Object obj4 = anotherElem.getElem1();
            if ((obj3 instanceof Element) && (obj4 instanceof Element)) {
                Element elem1 = (Element )obj3;
                Element elem2 = (Element) obj4;

                if (!(elem1.getLocalName().equals(elem2.getLocalName()))) {
                    return false;
                }
            } else {
                return false;
            }

            // Passed all equality tests
            return true;
        } else {
            return false;
        }
    }

}
