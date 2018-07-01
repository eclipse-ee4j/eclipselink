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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.jaxb.xmladapter.direct;

import java.util.Calendar;
import javax.xml.bind.annotation.adapters.XmlAdapter;

public final class MyCalendarExceptionAdapter extends XmlAdapter<Calendar, MyCalendarType> {
    public static boolean unmarshalHit;
    public static boolean marshalHit;

    public MyCalendarType unmarshal(Calendar arg0) throws Exception {
        unmarshalHit = true;
        throw new NullPointerException();
    }

    public Calendar marshal(MyCalendarType arg0) throws Exception {
        marshalHit = true;
        throw new NullPointerException();
    }
}
