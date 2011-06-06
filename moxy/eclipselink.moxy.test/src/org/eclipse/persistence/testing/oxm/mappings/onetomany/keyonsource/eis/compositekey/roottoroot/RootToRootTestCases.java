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
package org.eclipse.persistence.testing.oxm.mappings.onetomany.keyonsource.eis.compositekey.roottoroot;

import java.util.ArrayList;
import java.util.Vector;
import org.eclipse.persistence.eis.interactions.XQueryInteraction;
import org.eclipse.persistence.internal.eis.adapters.xmlfile.XMLFileInteractionSpec;
import org.eclipse.persistence.testing.oxm.mappings.EISMappingTestCases;
import org.eclipse.persistence.testing.oxm.mappings.onetomany.keyonsource.*;
import org.eclipse.persistence.testing.oxm.mappings.onetomany.keyonsource.eis.*;
                                         
public class RootToRootTestCases extends EISMappingTestCases {
  
	private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/onetomany/keyonsource/eis/compositekey/roottoroot/writing/employee_control.xml";
  private final static String XML_TEST_RESOURCE="org/eclipse/persistence/testing/oxm/mappings/onetomany/keyonsource/eis/compositekey/roottoroot/writing/employee.xml";
  private final static String CONTROL_EMPLOYEE1_NAME = "Jane";
    
  private final static long CONTROL_PROJECT1_ID = 1;
  private final static String CONTROL_PROJECT1_NAME = "Project1";
  
  private final static long CONTROL_PROJECT2_ID = 2;
  private final static String CONTROL_PROJECT2_NAME = "Project2";
  
  private final static long CONTROL_PROJECT3_ID = 3;
  private final static String CONTROL_PROJECT3_NAME = "Project3";
  
  public RootToRootTestCases(String name) throws Exception {
    super(name);
    setControlDocument(XML_RESOURCE);
		setProject(new RootToRootProject());
  }

  protected Object getControlObject() {
        
    Project project1 = new Project();
    project1.setId(CONTROL_PROJECT1_ID);
    project1.setName(CONTROL_PROJECT1_NAME);
    
    Project project2 = new Project();
    project2.setId(CONTROL_PROJECT2_ID);
    project2.setName(CONTROL_PROJECT2_NAME);
    
    Project project3 = new Project();
    project3.setId(CONTROL_PROJECT3_ID);
    project3.setName(CONTROL_PROJECT3_NAME);
    
    Employee employee1 = new Employee();
    employee1.setFirstName(CONTROL_EMPLOYEE1_NAME);
    employee1.addProject(project3);
    employee1.addProject(project1);
    
    ArrayList objects = new ArrayList();
    objects.add(employee1);
	  objects.add(project1);
    objects.add(project2);
    objects.add(project3);
    
    return objects;
   		
    //return employee1;
  }
  
  protected ArrayList getRootClasses()
  {
    ArrayList classes = new ArrayList();
    classes.add(Employee.class);
    classes.add(Project.class);
    return classes;
  }

protected Class getSourceClass(){
		return Employee.class;
	}


  protected String getTestDocument()
  {
    return XML_TEST_RESOURCE;
  }

  protected void createTables()
  {
    // Drop tables
		XQueryInteraction interaction = new XQueryInteraction();
 		XMLFileInteractionSpec spec = new XMLFileInteractionSpec();
	
		interaction = new XQueryInteraction();
		interaction.setFunctionName("drop-PROJECT");
		spec = new XMLFileInteractionSpec();
		spec.setFileName("project.xml");
		spec.setInteractionType(XMLFileInteractionSpec.DELETE);
		interaction.setInteractionSpec(spec);
		session.executeNonSelectingCall(interaction);
    
    interaction = new XQueryInteraction();
		interaction.setFunctionName("drop-EMPLOYEE");
		spec = new XMLFileInteractionSpec();
		spec.setFileName("employee.xml");
		spec.setInteractionType(XMLFileInteractionSpec.DELETE);
		interaction.setInteractionSpec(spec);
		session.executeNonSelectingCall(interaction);  
  }
}
