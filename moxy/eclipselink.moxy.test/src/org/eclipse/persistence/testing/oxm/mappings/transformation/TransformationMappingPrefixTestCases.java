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
//     mmacivor - Initial implementation
package org.eclipse.persistence.testing.oxm.mappings.transformation;

import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;

public class TransformationMappingPrefixTestCases extends XMLMappingTestCases
{
  public TransformationMappingPrefixTestCases(String name) throws Exception {
    super(name);
    setProject(new TransformationMappingTestProject());
    setControlDocument("org/eclipse/persistence/testing/oxm/mappings/transformation/employee1_withprefix.xml");
    setWriteControlDocument("org/eclipse/persistence/testing/oxm/mappings/transformation/employee1.xml");
  }

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
}


