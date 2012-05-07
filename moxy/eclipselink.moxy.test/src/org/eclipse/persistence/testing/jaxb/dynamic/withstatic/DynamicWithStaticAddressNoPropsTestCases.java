/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Matt MacIvor - 2.4 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.dynamic.withstatic;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.persistence.dynamic.DynamicEntity;
import org.eclipse.persistence.internal.dynamic.DynamicEntityImpl;
import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.eclipse.persistence.jaxb.dynamic.DynamicJAXBContext;
import org.eclipse.persistence.jaxb.dynamic.DynamicJAXBContextFactory;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class DynamicWithStaticAddressNoPropsTestCases extends JAXBWithJSONTestCases {

//  private DynamicJAXBContext jaxbContext;
  private static final String RESOURCE_DIR = "org/eclipse/persistence/testing/jaxb/dynamic/withstatic/";

  // Schema files used to test each feature
  private static final String NO_PROPS = RESOURCE_DIR + "address-no-props-oxm.xml";
  private static final String CONTROL_DOC = RESOURCE_DIR + "customer1.xml";
  private static final String JSON_CONTROL_DOC = RESOURCE_DIR + "customer1.json";
  private static final String PACKAGE = "org.eclipse.persistence.testing.jaxb.dynamic.withstatic";

  public DynamicWithStaticAddressNoPropsTestCases(String name) throws Exception {
      super(name);
      setupContext();
      setControlDocument(CONTROL_DOC);
      setControlJSON(JSON_CONTROL_DOC);
  }

  public String getName() {
      return "Dynamic JAXB: OXM: " + super.getName();
  }

  @Override
  protected Object getControlObject() {
      DynamicEntity employee = ((DynamicJAXBContext)jaxbContext).newDynamicEntity(PACKAGE + "." + "Customer");    
      DynamicEntity phone1 = ((DynamicJAXBContext)jaxbContext).newDynamicEntity(PACKAGE + "." + "PhoneNumber");
      DynamicEntity phone2 = ((DynamicJAXBContext)jaxbContext).newDynamicEntity(PACKAGE + "." + "PhoneNumber");
      phone1.set("value", "555-WORK");
      phone1.set("type", "work");
      phone2.set("value", "555-HOME");
      phone2.set("type", "home");
      
      ArrayList phones = new ArrayList();
      phones.add(phone1);
      phones.add(phone2);
      employee.set("phoneNumber", phones);
      
      Address address = new Address();
      address.city = "Any Town";
      address.street = "123 Some Street";
      
      employee.set("address", address);
      employee.set("name", "Jane Doe");
      
      return employee;
  }
  
  
  
  void setupContext() throws Exception {
      classLoader = Thread.currentThread().getContextClassLoader();
      InputStream iStream = classLoader.getResourceAsStream(NO_PROPS);
      if (iStream == null) {
          fail("Couldn't load metadata file [" + NO_PROPS + "]");
      }

      Map<String, Object> properties = new HashMap<String, Object>();
      properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, iStream);

      jaxbContext = DynamicJAXBContextFactory.createContext(PACKAGE, null, properties);
      xmlContext = ((org.eclipse.persistence.jaxb.JAXBContext)jaxbContext).getXMLContext();
      setProject(xmlContext.getSession(0).getProject());
      jaxbMarshaller = jaxbContext.createMarshaller();
      jaxbUnmarshaller = jaxbContext.createUnmarshaller();
      
      jaxbMarshaller.setProperty(MarshallerProperties.JSON_VALUE_WRAPPER, "value");
      jaxbUnmarshaller.setProperty(UnmarshallerProperties.JSON_VALUE_WRAPPER, "value");
  }
  
  public void xmlToObjectTest(Object testObject, Object controlObject) throws Exception {
      log("\n**xmlToObjectTest**");
      log("Expected:");
      log(controlObject.toString());
      log("Actual:");
      log(testObject.toString());
      
      compareDynamicEntities(testObject, controlObject);
  }
  
  public void jsonToObjectTest(Object testObject) throws Exception {	  
      log("\n**xmlToObjectTest**");
      log("Expected:");
      log(getJSONReadControlObject().toString());
      log("Actual:");
      log(testObject.toString());
      
      compareDynamicEntities(testObject, getJSONReadControlObject());
  }

  private void compareDynamicEntities(Object testObject, Object controlObject) {
      if(testObject instanceof DynamicEntity && controlObject instanceof DynamicEntity) {
          DynamicEntity test = (DynamicEntity)testObject;
          DynamicEntity control = (DynamicEntity)controlObject;
          for(String key: ((DynamicEntityImpl)test).getPropertiesMap().keySet()) {
              Object testValue = test.get(key);
              Object controlValue = control.get(key);
              if(testValue instanceof List && controlValue instanceof List) {
                  for(int i = 0; i < ((List)testValue).size(); i++) {
                      Object nextTestValue = ((List)testValue).get(i);
                      Object nextControlValue = ((List)controlValue).get(i);
                      if(nextTestValue instanceof DynamicEntity) {
                          compareDynamicEntities(nextTestValue, nextControlValue);
                      } else {
                          assertEquals(nextTestValue, nextControlValue);
                      }
                  }
              }
              else if(testValue instanceof DynamicEntity) {
                  compareDynamicEntities(testValue, controlValue);
              } else {
                  assertEquals(testValue, controlValue);
              }
          }
      } else {
          assertEquals(testObject, controlObject);
      }
      
  }    
}
