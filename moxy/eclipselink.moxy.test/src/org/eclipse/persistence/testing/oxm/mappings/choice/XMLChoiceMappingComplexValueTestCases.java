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
package org.eclipse.persistence.testing.oxm.mappings.choice;

import org.eclipse.persistence.oxm.platform.SAXPlatform;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;

public class XMLChoiceMappingComplexValueTestCases extends XMLMappingTestCases {

  private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/choice/ChoiceComplexValue.xml";

  public XMLChoiceMappingComplexValueTestCases(String name) throws Exception {
    super(name);
    setControlDocument(XML_RESOURCE);
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
