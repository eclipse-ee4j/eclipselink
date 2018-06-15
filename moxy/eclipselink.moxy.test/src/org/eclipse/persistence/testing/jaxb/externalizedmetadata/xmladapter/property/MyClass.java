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
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmladapter.property;

import java.util.Calendar;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="myClass")
public class MyClass {
    public int id;
    public Calendar cal;

    public MyClass() {}

    public boolean equals(Object obj) {
        if (!(obj instanceof MyClass)) {
            return false;
        }
        MyClass sc = (MyClass) obj;
        if (this.id != sc.id) {
            return false;
        }
        //return (this.cal.YEAR == sc.cal.YEAR && this.cal.MONTH == sc.cal.MONTH && this.cal.DATE == sc.cal.DATE);
        return (this.cal.get(cal.YEAR) == sc.cal.get(cal.YEAR) && this.cal.get(cal.MONTH) == sc.cal.get(cal.MONTH) && cal.get(cal.DATE) == sc.cal.get(cal.DATE));
    }
}
