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

import javax.xml.bind.annotation.XmlAttribute;

public class MyCalendar {
    @XmlAttribute
    public int year;
    @XmlAttribute
    public int month;
    @XmlAttribute
    public int day;

    public boolean equals(Object obj) {
        if (!(obj instanceof MyCalendar)) {
            return false;
        }
        MyCalendar mcType = (MyCalendar) obj;
        if (mcType.day != day || mcType.month != month || mcType.year != year) {
            return false;
        }
        return true;
    }

}
