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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.oxm.mappings.directcollection.arraylist;

import java.util.ArrayList;

import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;

public class DirectCollectionArrayListTestCases extends XMLMappingTestCases {

  private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/directcollection/arraylist/DirectCollectionArrayList.xml";
  private final static int CONTROL_ID = 123;
    private final static String CONTROL_RESPONSIBILITY1 = "make the coffee";
  private final static String CONTROL_RESPONSIBILITY2 = "do the dishes";
  private final static String CONTROL_RESPONSIBILITY3 = "take out the garbage";

  public DirectCollectionArrayListTestCases(String name) throws Exception {
    super(name);
    setControlDocument(XML_RESOURCE);
        setProject(new DirectCollectionArrayListProject());
  }

  protected Object getControlObject() {
        ArrayList responsibilities = new ArrayList();
        responsibilities.add(CONTROL_RESPONSIBILITY1);
        responsibilities.add(CONTROL_RESPONSIBILITY2);
        responsibilities.add(CONTROL_RESPONSIBILITY3);

    Employee employee = new Employee();
    employee.setID(CONTROL_ID);
        employee.setResponsibilities(responsibilities);
    return employee;
  }

}

