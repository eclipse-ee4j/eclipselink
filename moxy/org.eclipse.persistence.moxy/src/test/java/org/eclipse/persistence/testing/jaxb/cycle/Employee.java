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
//  - rbarkhouse - 23 April 2012 - 2.4 - Initial implementation
package org.eclipse.persistence.testing.jaxb.cycle;

import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAttribute;

import org.glassfish.jaxb.runtime.CycleRecoverable;

public class Employee implements CycleRecoverable {

    public int id;
    public String name;
    public List<ContactInfo> contactInfos = new ArrayList<ContactInfo>();

    public Object onCycleDetected(Context arg0) {
        // Ensure Context's marshaller was set
        String marshallerString = arg0.getMarshaller().toString();

        if (id < 1000) {
            // Return an object of a built-in Java type
            return new Integer(this.id);
        } else {
            // Return an object of a diffenet type (one that is known to this context)
            EmployeePointer p = new EmployeePointer();
            p.empId = this.id;
            return p;
        }
    }

}
