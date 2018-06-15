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
// Matt MacIvor - 2.4
package org.eclipse.persistence.testing.jaxb.prefixmapper;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(namespace="someuri")
@XmlType(name="emp-type", namespace="someuri")
public class Employee extends Person {

    public int employeeId;

    public Person manager;

    public boolean equals(Object obj) {
        Employee e = (Employee)obj;

        return super.equals(e) && employeeId == e.employeeId && (manager == e.manager || manager.equals(e.manager));
    }
}
