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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.oxm.mappings.anyobject.withoutgroupingelement;

import org.eclipse.persistence.oxm.XMLRoot;
import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class RootKeepAsElement {

    protected Object t1;

    public Object getT1() {
        return t1;
    }

    public void setT1(Object value) {
        this.t1 = value;
    }

    public boolean equals(Object object) {
        if (object instanceof RootKeepAsElement) {
            if (t1 == null && ((RootKeepAsElement) object).getT1() == null) {
                return true;
            }
            if (t1 == null && ((RootKeepAsElement) object).getT1() != null) {
                return false;
            }
            Object value1 = t1;
            Object value2 = ((RootKeepAsElement) object).getT1();
            if ((value1 instanceof Element) && (value2 instanceof Element)) {
                Element elem1 = (Element )value1;
                Element elem2 = (Element) value2;
                if(!(elem1.getLocalName().equals(elem2.getLocalName()))) {
                    return false;
                }
                return true;
            }
            return this.t1.equals(((RootKeepAsElement) object).getT1());
        }
        return false;
    }

}
