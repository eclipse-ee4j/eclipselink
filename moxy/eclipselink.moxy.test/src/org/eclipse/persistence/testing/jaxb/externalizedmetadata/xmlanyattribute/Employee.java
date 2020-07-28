/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
// dmccann - November 05/2009 - 2.0 - Initial implementation
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlanyattribute;

import javax.xml.namespace.QName;

@javax.xml.bind.annotation.XmlRootElement
public class Employee {
    public int a;
    public String b;

    public java.util.Map<QName, Object> stuff;

    @javax.xml.bind.annotation.XmlAnyAttribute
    public java.util.Map<QName, Object> stuffs;

    public boolean equals(Object obj) {
        Employee eObj;
        try {
            eObj = (Employee) obj;
        } catch (ClassCastException cce) {
            return false;
        }

        if (this.a != eObj.a) { return false; }
        if (!this.b.equals(eObj.b)) { return false; }
        if (this.stuff.size() != eObj.stuff.size()) { return false; }

        for (QName key : this.stuff.keySet()) {
            if (!eObj.stuff.containsKey(key)) {
                return false;
            }
            Object val1 = this.stuff.get(key);
            Object val2 = eObj.stuff.get(key);
            if (!val1.equals(val2)) {
                return false;
            }
        }
        return true;
    }
}
