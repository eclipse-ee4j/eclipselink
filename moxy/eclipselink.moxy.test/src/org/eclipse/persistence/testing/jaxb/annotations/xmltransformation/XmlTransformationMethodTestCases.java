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
// Oracle = 2.2 - Initial implementation
package org.eclipse.persistence.testing.jaxb.annotations.xmltransformation;

import org.eclipse.persistence.testing.jaxb.JAXBTestCases;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class XmlTransformationMethodTestCases extends JAXBWithJSONTestCases{
public XmlTransformationMethodTestCases(String name) throws Exception {
      super(name);
      setClasses(new Class[] {EmployeeTransformationMethod.class});
      setControlDocument("org/eclipse/persistence/testing/oxm/mappings/transformation/employee1.xml");
      setControlJSON("org/eclipse/persistence/testing/oxm/mappings/transformation/employee1.json");
   }

  public Object getControlObject() {
      EmployeeTransformationMethod emp = new EmployeeTransformationMethod();
      emp.name = "John Smith";
      String[] hours = new String[2];
      hours[0] = "9:00AM";
      hours[1] = "5:00PM";
      emp.normalHours = hours;
      return emp;
  }
}
