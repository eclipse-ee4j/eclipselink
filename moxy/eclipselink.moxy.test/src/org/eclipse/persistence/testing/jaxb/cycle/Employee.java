/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//  - rbarkhouse - 23 April 2012 - 2.4 - Initial implementation
package org.eclipse.persistence.testing.jaxb.cycle;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;

import com.sun.xml.bind.CycleRecoverable;

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
