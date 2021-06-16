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
package org.eclipse.persistence.testing.jaxb.xmladapter.compositecollection;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="dates")
public class MyDates {
    @XmlElement(name="date")
    @XmlJavaTypeAdapter(MyDateAdapter.class)
    public ArrayList<Date> dateList;

    public boolean equals(Object obj) {
        if (!(obj instanceof MyDates)) {
            return false;
        }
        ArrayList<Date> dates = ((MyDates) obj).dateList;
        for (Iterator<Date> dateIt = dates.iterator(); dateIt.hasNext(); ) {
            Date date = dateIt.next();
            if (!this.dateList.contains(date)) {
                return false;
            }
        }
        return true;
    }
}
