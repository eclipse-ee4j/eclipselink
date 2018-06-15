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
package org.eclipse.persistence.testing.jaxb.xmladapter.compositecollection;

import java.util.Date;
import javax.xml.bind.annotation.adapters.XmlAdapter;

public final class MyDateAdapter extends XmlAdapter<MyDateType, Date> {
    public MyDateType marshal(Date arg0) throws Exception {
        MyDateType dType = new MyDateType();
        dType.day = arg0.getDate();
        dType.month = arg0.getMonth();
        dType.year = arg0.getYear();
        return dType;
    }

    public Date unmarshal(MyDateType arg0) throws Exception {
        return new Date(arg0.year, arg0.month, arg0.day);
    }
}
