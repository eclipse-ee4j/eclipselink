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
package org.eclipse.persistence.testing.oxm.mappings.directtofield.defaultnullvalue.xmlelement;

import org.eclipse.persistence.testing.oxm.OXTestCase.Platform;
import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;
import org.eclipse.persistence.testing.oxm.mappings.directtofield.defaultnullvalue.Employee;

public class MissingElementTestCases extends XMLMappingTestCases {

  private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/directtofield/defaultnullvalue/xmlelement/MissingElement.xml";

  public MissingElementTestCases(String name) throws Exception {
    super(name);
    setControlDocument(XML_RESOURCE);
        setProject(new DefaultNullValueElementProject());
  }

  protected Object getControlObject() {
        Employee employee = new Employee();
        // We currently have different behavior when using XMLProjectReader
          if(platform==Platform.DOM && metadata==Metadata.XML_ECLIPSELINK) {
            employee.setID(DefaultNullValueElementProject.CONTROL_ID);
            // See bug#209398 - current behavior for deployment xml is to not preserve the nullValue of ""
            //employee.setFirstName(DefaultNullValueElementProject.CONTROL_FIRSTNAME);
        } else {
            employee.setID(DefaultNullValueElementProject.CONTROL_ID);
            // See bug#209398 - current behavior for deployment xml is to not preserve the nullValue of ""
            employee.setFirstName(DefaultNullValueElementProject.CONTROL_FIRSTNAME);
        }

        return employee;
      }

}
