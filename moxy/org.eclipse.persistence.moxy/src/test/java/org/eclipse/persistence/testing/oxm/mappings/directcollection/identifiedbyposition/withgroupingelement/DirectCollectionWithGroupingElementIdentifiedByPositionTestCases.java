/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.oxm.mappings.directcollection.identifiedbyposition.withgroupingelement;

import java.util.Vector;

import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;
import org.eclipse.persistence.testing.oxm.mappings.directcollection.Employee;


public class DirectCollectionWithGroupingElementIdentifiedByPositionTestCases extends XMLMappingTestCases {

  private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/directcollection/identifiedbyposition/withgroupingelement/DirectCollectionWithGroupingElementIdentifiedByPosition.xml";
  private final static int CONTROL_ID = 123;
    private final static String CONTROL_RESPONSIBILITY1 = "make the coffee";
  private final static String CONTROL_RESPONSIBILITY2 = "do the dishes";
  private final static String CONTROL_RESPONSIBILITY3 = "take out the garbage";

  private final static String CONTROL_RESPONSIBILITY4 = "rake the leaves";
  private final static String CONTROL_RESPONSIBILITY5 = "mow the lawn";


  public DirectCollectionWithGroupingElementIdentifiedByPositionTestCases(String name) throws Exception {
    super(name);
    setControlDocument(XML_RESOURCE);
        setProject(new DirectCollectionWithGroupingElementIdentifiedByPositionProject());
  }

  protected Object getControlObject() {
        Vector responsibilities = new Vector();
        responsibilities.addElement(CONTROL_RESPONSIBILITY1);
        responsibilities.addElement(CONTROL_RESPONSIBILITY2);
        responsibilities.addElement(CONTROL_RESPONSIBILITY3);

        Vector responsibilities2 = new Vector();
        responsibilities2.addElement(CONTROL_RESPONSIBILITY4);
        responsibilities2.addElement(CONTROL_RESPONSIBILITY5);

    Employee employee = new Employee();
    employee.setID(CONTROL_ID);
        employee.setResponsibilities(responsibilities);
        employee.setOutdoorResponsibilities(responsibilities2);
    return employee;
  }

}

