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
// Matt MacIvor - August 2011
package org.eclipse.persistence.testing.jaxb.xmladapter.compositedirectcollection;

import java.util.ArrayList;
import java.util.Iterator;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.eclipse.persistence.testing.jaxb.xmladapter.direct.MyCalendarAdapter;
import org.eclipse.persistence.testing.jaxb.xmladapter.direct.MyCalendarType;

@XmlRootElement(name="mycalendar")
public class MyCalendarWithArray {
    @XmlElement(name="date")
    @XmlJavaTypeAdapter(MyCalendarAdapter.class)
    public MyCalendarType[] date;

    public boolean equals(Object obj) {
        if (!(obj instanceof MyCalendarWithArray)) {
            return false;
        }
        MyCalendarWithArray myCal = (MyCalendarWithArray) obj;
        for (int i = 0; i < date.length; i++) {
            if(!(date[i].equals(myCal.date[i]))) {
                return false;
            }
        }
        return true;
    }
}
