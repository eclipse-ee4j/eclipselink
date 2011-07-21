/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.oxm.mappings.compositeobject.singleelement;

import org.eclipse.persistence.testing.oxm.mappings.XMLWithJSONMappingTestCases;

public class CompositeObjectSingleElementTestCases extends XMLWithJSONMappingTestCases {

  private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/compositeobject/singleelement/CompositeObjectSingleElement.xml";
  private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/compositeobject/singleelement/CompositeObjectSingleElement.json";

  private final static int CONTROL_EMPLOYEE_ID = 123;
  private final static String CONTROL_EMAIL_ADDRESS_USER_ID = "jane.doe";

  public CompositeObjectSingleElementTestCases(String name) throws Exception {
    super(name);
    setControlDocument(XML_RESOURCE);
    setControlJSON(JSON_RESOURCE);
		setProject(new CompositeObjectSingleElementProject());
  }

  protected Object getControlObject() {
    Employee employee = new Employee();
    employee.setID(CONTROL_EMPLOYEE_ID);

    EmailAddress emailAddress = new EmailAddress();
    emailAddress.setUserID(CONTROL_EMAIL_ADDRESS_USER_ID);
    employee.setEmailAddress(emailAddress);
    
    return employee;
  }

}
