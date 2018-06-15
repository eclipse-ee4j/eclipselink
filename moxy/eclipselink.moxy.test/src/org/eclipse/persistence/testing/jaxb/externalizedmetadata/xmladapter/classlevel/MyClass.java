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
// dmccann - July 21/2009 - 2.0 - Initial implementation
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmladapter.classlevel;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="myClass")
public class MyClass {
    public int id;
    public MyCalendar myCal;

    public MyClass() {}

    public boolean equals(Object obj) {
        if (!(obj instanceof MyClass)) {
            return false;
        }
        MyClass sc = (MyClass) obj;
        if (sc.id != this.id) {
            return false;
        }
        return this.myCal.equals(sc.myCal);
    }
}
