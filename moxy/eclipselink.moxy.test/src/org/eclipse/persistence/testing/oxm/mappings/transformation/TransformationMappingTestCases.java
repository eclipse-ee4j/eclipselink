/*
 * Copyright (c) 1998, 2019 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.oxm.mappings.transformation;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.sessions.CopyGroup;
import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;

public class TransformationMappingTestCases extends XMLMappingTestCases
{
  public TransformationMappingTestCases(String name) throws Exception {
    super(name);
    setProject(new TransformationMappingTestProject());
    setControlDocument("org/eclipse/persistence/testing/oxm/mappings/transformation/employee1.xml");
  }

    @Override
    public Object getControlObject()
  {
    Employee emp = new Employee();
    emp.setName("John Smith");
    String[] hours = new String[2];
    hours[0] = "9:00AM";
    hours[1] = "5:00PM";
    emp.setNormalHours(hours);

    return emp;
  }

  public void testCopyObject() throws Exception
  {
    AbstractSession session = (AbstractSession)super.xmlContext.getSession(0);
    ClassDescriptor descriptor = session.getDescriptor(Employee.class);
    CopyGroup policy = new CopyGroup();
    policy.setSession(session);
    Employee emp1 = (Employee)getControlObject();
    Employee emp2 = (Employee)descriptor.getObjectBuilder().copyObject(emp1, policy);
    boolean equal = descriptor.getObjectBuilder().compareObjects(emp1, emp2, session);
    assertTrue("The copy of the employee doesn't match the original", equal);
  }
}


