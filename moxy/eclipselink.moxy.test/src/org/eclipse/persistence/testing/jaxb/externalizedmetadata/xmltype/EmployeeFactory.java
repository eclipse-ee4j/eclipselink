/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * dmccann - April 13/2010 - 2.1 - Initial implementation
 ******************************************************************************/
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
