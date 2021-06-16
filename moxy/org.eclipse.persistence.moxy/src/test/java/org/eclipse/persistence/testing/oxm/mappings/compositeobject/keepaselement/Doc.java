/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     rbarkhouse - 2009-04-14 - 2.0 - Initial implementation

package org.eclipse.persistence.testing.oxm.mappings.compositeobject.keepaselement;

import org.eclipse.persistence.testing.oxm.mappings.anyobject.withoutgroupingelement.RootKeepAsElement;
import org.w3c.dom.Element;

public class Doc {

    protected Elem elem;
    protected Object elem1;

    public Elem getElem() {
        return elem;
    }

    public void setElem(Elem value) {
        this.elem = value;
    }

    public Object getElem1() {
        return elem1;
    }

    public void setElem1(Object value) {
        this.elem1 = value;
    }

    public boolean equals(Object object) {
        if (object instanceof Doc) {
            Doc anotherDoc = (Doc) object;
            if (elem == null && anotherDoc.getElem() != null) {
                return false;
            }
            if (elem != null && anotherDoc.getElem() == null) {
                return false;
            }
            if (elem1 == null && anotherDoc.getElem1() != null) {
                return false;
            }
            if (elem1 != null && anotherDoc.getElem1() == null) {
                return false;
            }

            Object obj1 = elem1;
            Object obj2 = anotherDoc.getElem1();
            if ((obj1 instanceof Element) && (obj2 instanceof Element)) {
                Element elem1 = (Element )obj1;
                Element elem2 = (Element) obj2;

                if (!(elem1.getLocalName().equals(elem2.getLocalName()))) {
                    return false;
                }
            } else {
                return false;
            }

            if (!elem.equals(anotherDoc.getElem())) {
                return false;
            }

            // Passed all equality tests
            return true;
        } else {
            return false;
        }
    }

}
