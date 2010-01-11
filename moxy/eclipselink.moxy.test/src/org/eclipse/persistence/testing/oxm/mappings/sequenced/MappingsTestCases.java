/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.oxm.sequenced.Setting;
import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;

public class MappingsTestCases extends XMLMappingTestCases {
    
    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/sequenced/Mappings.xml";
    private static final String CONTROL_EMPLOYEE_FIRST_NAME = "Jane";
    private static final String CONTROL_EMPLOYEE_LAST_NAME = "Doe";
    private static final String CONTROL_ADDRESS_ID = "1";
    private static final String CONTROL_ADDRESS_STREET = "1 A Street";
    private static final String CONTROL_ADDRESS_CITY = "Town A";
    private static final String CONTROL_FRAGMENT_NAME = "fragment";
    private static final String CONTROL_FRAGMENT_1_TEXT = "Hello";
    private static final String CONTROL_FRAGMENT_2_TEXT = "World";
    private static final String CONTROL_ANY_ADDRESS_1_STREET = "1 Any Street";
    private static final String CONTROL_ANY_ADDRESS_1_CITY = "First Any Town";
    private static final String CONTROL_ANY_ADDRESS_2_STREET = "2 Any Street";
    private static final String CONTROL_ANY_ADDRESS_2_CITY = "Second Any Town";
    private static final String CONTROL_DEPENDENT_FIRST_NAME = "Dependent First";
    private static final String CONTROL_DEPENDENT_LAST_NAME = "Dependent Last";
    private static final String CONTROL_DEPENDENT_2_FIRST_NAME = "Dependent 2 First";
    private static final String CONTROL_DEPENDENT_2_LAST_NAME = "Dependent 2 Last";
    
    private EmployeeProject EMPLOYEE_PROJECT;
    
    public MappingsTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        EMPLOYEE_PROJECT = new EmployeeProject();
        setProject(EMPLOYEE_PROJECT);
    }
    
    public Object getControlObject() {
        Document document;
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            document = db.newDocument();
        } catch(ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
        
        Employee controlEmployee = new Employee();
        
        Setting lnPersonalInfoSetting = new Setting(null, "personal-info");
        controlEmployee.getSettings().add(lnPersonalInfoSetting);
        Setting lastNameSetting = new Setting(null, "last-name");
        lnPersonalInfoSetting.addChild(lastNameSetting);       
        Setting lastNameTextSetting = new Setting(null, "text()");
        lastNameTextSetting.setObject(controlEmployee);
        DatabaseMapping lastNameMapping = EMPLOYEE_PROJECT.getDescriptor(Employee.class).getMappingForAttributeName("lastName");
        lastNameTextSetting.setMapping(lastNameMapping);        
        lastNameTextSetting.setValue(CONTROL_EMPLOYEE_LAST_NAME);
        lastNameSetting.addChild(lastNameTextSetting);        

        Address address = new Address();
        address.setId(CONTROL_ADDRESS_ID);
        address.setStreet(CONTROL_ADDRESS_STREET);
        address.setCity(CONTROL_ADDRESS_CITY);
        Setting addressSetting = new Setting(null, "address");
        addressSetting.setObject(controlEmployee);
        DatabaseMapping addressMapping = EMPLOYEE_PROJECT.getDescriptor(Employee.class).getMappingForAttributeName("address");
        addressSetting.setMapping(addressMapping);
        addressSetting.setValue(address);
        controlEmployee.getSettings().add(addressSetting);
        
        Setting fnPersonalInfoSetting = new Setting(null, "personal-info");
        controlEmployee.getSettings().add(fnPersonalInfoSetting);
        Setting firstNameSetting = new Setting("urn:example", "first-name");
        fnPersonalInfoSetting.addChild(firstNameSetting);
        Setting firstNameTextSetting = new Setting(null, "text()");
        firstNameTextSetting.setObject(controlEmployee);
        DatabaseMapping firstNameMapping = EMPLOYEE_PROJECT.getDescriptor(Employee.class).getMappingForAttributeName("firstName");
        firstNameTextSetting.setMapping(firstNameMapping);        
        firstNameTextSetting.setValue(CONTROL_EMPLOYEE_FIRST_NAME);
        firstNameSetting.addChild(firstNameTextSetting);
        
        Setting dependentSetting = new Setting(null, "dependent");
        controlEmployee.getSettings().add(dependentSetting);
        dependentSetting.setObject(controlEmployee);
        DatabaseMapping dependentMapping = EMPLOYEE_PROJECT.getDescriptor(Employee.class).getMappingForAttributeName("dependent");
        dependentSetting.setMapping(dependentMapping);
        dependentSetting.setValue(getControlDependent(address));
        
        
        
        
/*
        controlEmployee.setLastName(CONTROL_EMPLOYEE_LAST_NAME);
        
        Address address1 = new Address();
        address1.setId(CONTROL_ADDRESS_1_ID);
        address1.setStreet(CONTROL_ADDRESS_1_STREET);
        address1.setCity(CONTROL_ADDRESS_1_CITY);
        controlEmployee.setAddress(address1);

        Address anyAddress1 = new Address();
        anyAddress1.setStreet(CONTROL_ANY_ADDRESS_1_STREET);
        anyAddress1.setCity(CONTROL_ANY_ADDRESS_1_CITY);
        controlEmployee.setAny(anyAddress1);
        
        Dependent dependent1 = new Dependent();
        dependent1.setAddress(address1);
        dependent1.setLastName(CONTROL_DEPENDENT_1_LAST_NAME);
        dependent1.setFirstName(CONTROL_DEPENDENT_1_FIRST_NAME);
        controlEmployee.setDependent(dependent1);
        
        Element fragment1 = document.createElement(CONTROL_FRAGMENT_NAME);
        Text fragmentText1 = document.createTextNode(CONTROL_FRAGMENT_1_TEXT);
        fragment1.appendChild(fragmentText1);
        controlEmployee.setNode(fragment1);
        
        controlEmployee.setFirstName(CONTROL_EMPLOYEE_FIRST_NAME);

        Address address2 = new Address();
        address2.setId(CONTROL_ADDRESS_2_ID);
        address2.setStreet(CONTROL_ADDRESS_2_STREET);
        address2.setCity(CONTROL_ADDRESS_2_CITY);
        controlEmployee.setAddress(address2);

        Address anyAddress2 = new Address();
        anyAddress2.setStreet(CONTROL_ANY_ADDRESS_2_STREET);
        anyAddress2.setCity(CONTROL_ANY_ADDRESS_2_CITY);
        controlEmployee.setAny(anyAddress2);
        
        Dependent dependent2 = new Dependent();
        dependent2.setFirstName(CONTROL_DEPENDENT_2_FIRST_NAME);
        dependent2.setAddress(address2);
        dependent2.setLastName(CONTROL_DEPENDENT_2_LAST_NAME);
        controlEmployee.setDependent(dependent2);

        Element fragment2 = document.createElement(CONTROL_FRAGMENT_NAME);
        Text fragmentText2 = document.createTextNode(CONTROL_FRAGMENT_2_TEXT);
        fragment2.appendChild(fragmentText2);
        controlEmployee.setNode(fragment2);
        */
        return controlEmployee;
    }
    
    private Dependent getControlDependent(Address address) {
        Dependent dependent = new Dependent();
        
        Setting dependentLastNameSetting = new Setting(null, "last-name");
        dependent.getSettings().add(dependentLastNameSetting);
        Setting dependentLastNameTextSetting = new Setting(null, "text()");
        dependentLastNameTextSetting.setObject(dependent);
        DatabaseMapping dependentLastNameMapping = EMPLOYEE_PROJECT.getDescriptor(Dependent.class).getMappingForAttributeName("lastName");
        dependentLastNameTextSetting.setMapping(dependentLastNameMapping);        
        dependentLastNameTextSetting.setValue(CONTROL_DEPENDENT_LAST_NAME);
        dependentLastNameSetting.addChild(dependentLastNameTextSetting);

        Setting addressIdSetting = new Setting(null, "address-id");
        dependent.getSettings().add(addressIdSetting);
        Setting addressIdTextSetting = new Setting(null, "text()");
        addressIdTextSetting.setObject(dependent);
        DatabaseMapping addressMapping = EMPLOYEE_PROJECT.getDescriptor(Dependent.class).getMappingForAttributeName("address");
        addressIdTextSetting.setMapping(addressMapping);        
        addressIdTextSetting.setValue(address);
        addressIdSetting.addChild(addressIdTextSetting);
        
        Setting dependentFirstNameSetting = new Setting(null, "first-name");
        dependent.getSettings().add(dependentFirstNameSetting);
        Setting dependentFirstNameTextSetting = new Setting(null, "text()");
        dependentFirstNameTextSetting.setObject(dependent);
        DatabaseMapping dependentFirstNameMapping = EMPLOYEE_PROJECT.getDescriptor(Dependent.class).getMappingForAttributeName("firstName");
        dependentFirstNameTextSetting.setMapping(dependentFirstNameMapping);        
        dependentFirstNameTextSetting.setValue(CONTROL_DEPENDENT_FIRST_NAME);
        dependentFirstNameSetting.addChild(dependentFirstNameTextSetting);
        
        return dependent;
    }
    
    public void xmlToObjectTest(Object testObject) throws Exception {
        Employee testEmployee = (Employee) testObject;
        // assertEquals(10, testEmployee.getSettings().size());
        super.xmlToObjectTest(testObject); 
    }    
    
}
