/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.jaxb.events;

import java.util.ArrayList;

import jakarta.xml.bind.Unmarshaller;

public class JAXBUnmarshalListenerImpl extends Unmarshaller.Listener {
    static Integer EMPLOYEE_BEFORE_UNMARSHAL = 0;
    static Integer ADDRESS_BEFORE_UNMARSHAL = 1;
    static Integer PHONE_BEFORE_UNMARSHAL = 2;
    static Integer EMPLOYEE_AFTER_UNMARSHAL = 3;
    static Integer ADDRESS_AFTER_UNMARSHAL = 4;
    static Integer PHONE_AFTER_UNMARSHAL = 5;

    public ArrayList events = null;

    public JAXBUnmarshalListenerImpl() {
        events = new ArrayList();
    }
    @Override
    public void beforeUnmarshal(Object obj, Object parent) {
        if(obj instanceof Employee) {
            events.add(EMPLOYEE_BEFORE_UNMARSHAL);
        } else if(obj instanceof Address) {
            events.add(ADDRESS_BEFORE_UNMARSHAL);
        } else if(obj instanceof PhoneNumber) {
            events.add(PHONE_BEFORE_UNMARSHAL);
        }
    }
    @Override
    public void afterUnmarshal(Object obj, Object parent) {
        if(obj instanceof Employee) {
            events.add(EMPLOYEE_AFTER_UNMARSHAL);
        } else if(obj instanceof Address) {
            events.add(ADDRESS_AFTER_UNMARSHAL);
        } else if(obj instanceof PhoneNumber) {
            events.add(PHONE_AFTER_UNMARSHAL);
        }
    }
}
