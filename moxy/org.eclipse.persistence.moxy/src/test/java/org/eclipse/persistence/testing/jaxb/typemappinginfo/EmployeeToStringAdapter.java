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
// mmacivor - December 15/2009 - 2.0.1 - Initial implementation
package org.eclipse.persistence.testing.jaxb.typemappinginfo;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class EmployeeToStringAdapter extends XmlAdapter<String, Employee> {

    @Override
    public String marshal(Employee v) throws Exception {
        return v.firstName;
    }

    @Override
    public Employee unmarshal(String v) throws Exception {
        Employee emp = new Employee();
       emp.firstName = v;
        return emp;
    }

}
