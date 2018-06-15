/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.oxm.mappings.directcollection.singlenode;

import java.util.ArrayList;

import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;
import org.eclipse.persistence.testing.oxm.mappings.XMLWithJSONMappingTestCases;

public class DirectCollectionSingleNodeTestCases extends XMLWithJSONMappingTestCases {

  private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/directcollection/singlenode/DirectCollectionSingleNode.xml";
  private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/directcollection/singlenode/DirectCollectionSingleNode.json";

  private final static int CONTROL_ID = 123;

  private final static String CONTROL_RESPONSIBILITY1 = "make_the_coffee";
  private final static String CONTROL_RESPONSIBILITY2 = "do_the_dishes";
  private final static String CONTROL_RESPONSIBILITY3 = "take_out_the_garbage";

  public DirectCollectionSingleNodeTestCases(String name) throws Exception {
    super(name);
    setControlDocument(XML_RESOURCE);
    setControlJSON(JSON_RESOURCE);
    setProject(new DirectCollectionSingleNodeProject());
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

