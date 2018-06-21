/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
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
// dmccann - April 13/2010 - 2.1 - Initial implementation
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmltype;

public class EmployeeFactory {
    public Employee getNewEmployee() {
        Employee emp = new Employee();
        emp.id = XmlTypeTestCases.EMP_ID;
        emp.firstName = XmlTypeTestCases.EMP_FIRST;
        emp.lastName = XmlTypeTestCases.EMP_LAST;
        emp.fromFactoryMethod = true;
        return emp;
    }
}
