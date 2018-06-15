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
package org.eclipse.persistence.testing.oxm.mappings.choicecollection;

import java.io.FileReader;
import java.io.InputStream;

import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.oxm.XMLRoot;
import org.eclipse.persistence.oxm.XMLUnmarshaller;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.sessions.factories.XMLProjectReader;
import org.eclipse.persistence.testing.oxm.mappings.XMLWithJSONMappingTestCases;

public class XMLChoiceCollectionMappingMixedTestCases extends XMLWithJSONMappingTestCases {

  private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/choicecollection/ChoiceCollectionMixed.xml";
  private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/choicecollection/ChoiceCollectionMixed.json";
  private final static String DEPLOYMENT_XML_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/choicecollection/deploymentXML-file.xml";

  public XMLChoiceCollectionMappingMixedTestCases(String name) throws Exception {
    super(name);
    setControlDocument(XML_RESOURCE);
    setControlJSON(JSON_RESOURCE);
    //setSession(SESSION_NAME);
    setProject(new EmployeeProject());
  }


  public Object getReadControlObject() {
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

        employee.choice.add("addressString");

        employee.phone = "123-4567";

        return employee;
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

    XMLRoot xmlRoot = new XMLRoot();
    xmlRoot.setLocalName("simpleAddress");
    xmlRoot.setObject("addressString");
    employee.choice.add(xmlRoot);

    employee.phone = "123-4567";

    return employee;
  }

  public  Object getJSONReadControlObject() {
        Employee employee = new Employee();
        employee.name = "Jane Doe";

        employee.choice = new java.util.Vector<Object>();
        employee.choice.add("123 Fake Street");
        employee.choice.add(new Integer(12));
        employee.choice.add(new Integer(14));
        Address addr = new Address();
        addr.city = "Ottawa";
        addr.street = "45 O'Connor";
        employee.choice.add(addr);

        employee.choice.add("addressString");


        employee.phone = "123-4567";

        return employee;
      }

  public Project getNewProject(Project originalProject, ClassLoader classLoader) {
      Project project = super.getNewProject(originalProject, classLoader);
      //project.getDatasourceLogin().setPlatform(new SAXPlatform());

      return project;
  }

  public void testReadDeploymentXML() {
      try {
          // Read the deploymentXML-file.xml back in with XMLProjectReader
          Project newProject = XMLProjectReader.read(DEPLOYMENT_XML_RESOURCE, Thread.currentThread().getContextClassLoader());
          XMLContext ctx = new XMLContext(newProject);
          XMLUnmarshaller unmarshaller = ctx.createUnmarshaller();
          InputStream instream = ClassLoader.getSystemResourceAsStream(XML_RESOURCE);
          Employee emp = (Employee) unmarshaller.unmarshal(instream);
          instream.close();
          Object[] choices = emp.choice.toArray();
          assertTrue("Choice collection did not unmarshal properly", (choices!=null && choices.length>0));
      } catch (Exception x) {
          x.printStackTrace();
          fail("Deployment XML read test failed");
      }
  }
}
