/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.oxm.mappings.choicecollection;

import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;

public class XMLChoiceCollectionMappingMixedTestCases extends XMLMappingTestCases {

  private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/choicecollection/ChoiceCollectionMixed.xml";

  public XMLChoiceCollectionMappingMixedTestCases(String name) throws Exception {
    super(name);
    setControlDocument(XML_RESOURCE);
    //setSession(SESSION_NAME);
    setProject(new EmployeeProject());
  }

  protected Object getControlObject() {
    Employee employee = new Employee();
    employee.name = "Jane Doe";
    
    employee.choice = new java.util.Vector<Object>();
    employee.choice.add("123 Fake Street");
    employee.choice.add(new Integer(12));
    Address addr = new Address();
    addr.city = "Ottawa";
    addr.street = "45 O'Connor";
    employee.choice.add(addr);
    employee.choice.add(new Integer(14));
    
    employee.phone = "123-4567"; 
    
    return employee;

  }

}