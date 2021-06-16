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
package org.eclipse.persistence.testing.oxm.events;

import java.util.ArrayList;

import org.eclipse.persistence.oxm.*;

/**
 *  @version $Header: UnmarshalListenerImpl.java 17-may-2006.14:47:42 mmacivor Exp $
 *  @author  mmacivor
 *  @since   release specific (what release of product did this appear in)
 */

public class UnmarshalListenerImpl implements XMLUnmarshalListener {
    static Integer EMPLOYEE_BEFORE_UNMARSHAL = new Integer(0);
    static Integer ADDRESS_BEFORE_UNMARSHAL = new Integer(1);
    static Integer PHONE_BEFORE_UNMARSHAL = new Integer(2);
    static Integer EMPLOYEE_AFTER_UNMARSHAL = new Integer(3);
    static Integer ADDRESS_AFTER_UNMARSHAL = new Integer(4);
    static Integer PHONE_AFTER_UNMARSHAL = new Integer(5);

    public ArrayList events = null;

    public UnmarshalListenerImpl() {
        events = new ArrayList();
    }
    public void beforeUnmarshal(Object obj, Object parent) {
        if(obj instanceof Employee) {
            events.add(EMPLOYEE_BEFORE_UNMARSHAL);
        } else if(obj instanceof Address) {
            events.add(ADDRESS_BEFORE_UNMARSHAL);
        } else if(obj instanceof PhoneNumber) {
            events.add(PHONE_BEFORE_UNMARSHAL);
        }
    }
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
