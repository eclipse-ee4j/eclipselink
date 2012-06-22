/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.oxm.mappings.sequenced;

import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.oxm.sequenced.Setting;
import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;

public class AttributeTestCases extends XMLMappingTestCases {
    
    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/sequenced/Attribute.xml";
    private static final String CONTROL_EMPLOYEE_ID = "123";
    private static final String CONTROL_EMPLOYEE_FIRST_NAME = "Jane";
  
    private static final EmployeeProject EMPLOYEE_PROJECT = new EmployeeProject();
    
    public AttributeTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setProject(EMPLOYEE_PROJECT);
    }
    
    public Object getControlObject() {
        Employee controlEmployee = new Employee();
        controlEmployee.setId(CONTROL_EMPLOYEE_ID);
        controlEmployee.setFirstName(CONTROL_EMPLOYEE_FIRST_NAME);
        
        Setting textSetting = new Setting(null, "text()");
        DatabaseMapping firstNameMapping = EMPLOYEE_PROJECT.getDescriptor(Employee.class).getMappingForAttributeName("firstName");
        textSetting.setMapping(firstNameMapping);
        textSetting.setObject(controlEmployee);
        textSetting.setValue(CONTROL_EMPLOYEE_FIRST_NAME, false);
        Setting fnSetting = new Setting("urn:example", "first-name");
        fnSetting.addChild(textSetting);
        Setting piSetting = new Setting(null, "personal-info");
        piSetting.addChild(fnSetting);
        controlEmployee.getSettings().add(piSetting);
        
        
        return controlEmployee;
    }
    
    public void xmlToObjectTest(Object testObject) throws Exception {
        Employee testEmployee = (Employee) testObject;
        assertEquals(1, testEmployee.getSettings().size());
        super.xmlToObjectTest(testObject); 
    }    
    
}
