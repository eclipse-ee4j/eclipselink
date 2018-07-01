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
package org.eclipse.persistence.testing.oxm.mappings.choice;

import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.oxm.mappings.XMLWithJSONMappingTestCases;

public class XMLChoiceMappingComplexValueTestCases extends XMLWithJSONMappingTestCases {

  private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/choice/ChoiceComplexValue.xml";
  private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/choice/ChoiceComplexValue.json";

  public XMLChoiceMappingComplexValueTestCases(String name) throws Exception {
    super(name);
    setControlDocument(XML_RESOURCE);
    setControlJSON(JSON_RESOURCE);
    //setSession(SESSION_NAME);
    setProject(new EmployeeProject());
  }

  protected Object getControlObject() {
    Employee employee = new Employee();
    employee.name = "Jane Doe";

    Address addr = new Address();
    addr.street = "45 O'Connor";
    addr.city = "Ottawa";
    employee.choice = addr;

    employee.phone = "123-4567";

    return employee;

  }

  public Project getNewProject(Project originalProject, ClassLoader classLoader) {
      Project project = super.getNewProject(originalProject, classLoader);
     // project.getDatasourceLogin().setPlatform(new SAXPlatform());

      return project;
  }

}
