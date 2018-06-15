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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.jaxb.xmladapter.direct;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="date")
public class MyCalendarType {
    @XmlAttribute
    public int year;
    @XmlAttribute
    public int month;
    @XmlAttribute
    public int day;

    public boolean equals(Object obj) {
        if (!(obj instanceof MyCalendarType)) {
            return false;
        }
        MyCalendarType mcType = (MyCalendarType) obj;
        if (mcType.day != day || mcType.month != month || mcType.year != year) {
            return false;
        }
        return true;
    }
}
