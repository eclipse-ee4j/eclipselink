/*
 * Copyright (c) 1998, 2024 Oracle and/or its affiliates. All rights reserved.
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
// dmccann - July 21/2009 - 2.0 - Initial implementation
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmladapter.property;

import java.util.Calendar;

import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="myClass")
public class MyClass {
    public int id;
    public Calendar cal;

    public MyClass() {}

    public boolean equals(Object obj) {
        if (!(obj instanceof MyClass sc)) {
            return false;
        }
        if (this.id != sc.id) {
            return false;
        }
        //return (this.cal.YEAR == sc.cal.YEAR && this.cal.MONTH == sc.cal.MONTH && this.cal.DATE == sc.cal.DATE);
        return (this.cal.get(Calendar.YEAR) == sc.cal.get(Calendar.YEAR) && this.cal.get(Calendar.MONTH) == sc.cal.get(Calendar.MONTH) && cal.get(Calendar.DATE) == sc.cal.get(Calendar.DATE));
    }
}
