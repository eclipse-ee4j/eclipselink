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
package org.eclipse.persistence.testing.oxm.mappings.onetoone.keyontarget.eis.roottoroot;

import java.util.ArrayList;
import org.eclipse.persistence.eis.interactions.XQueryInteraction;
import org.eclipse.persistence.internal.eis.adapters.xmlfile.XMLFileInteractionSpec;
import org.eclipse.persistence.testing.oxm.mappings.EISMappingTestCases;
import org.eclipse.persistence.testing.oxm.mappings.onetoone.keyontarget.*;
                                         
public class RootToRootTestCases extends EISMappingTestCases {

  private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/onetoone/keyontarget/eis/roottoroot/writing/employee_control.xml";
  private final static String XML_TEST_RESOURCE="org/eclipse/persistence/testing/oxm/mappings/onetoone/keyontarget/eis/roottoroot/writing/employee.xml";
  private final static String CONTROL_EMPLOYEE1_NAME = "Jack";
  private final static String CONTROL_EMPLOYEE2_NAME = "Jill";
  private final static String CONTROL_EMPLOYEE3_NAME = "Joe";
  private final static String EMP_MOD_NAME = "AAA";
	private final static String PROJECT_MOD_NAME = "BBB";
	  
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
        
    ArrayList objects = new ArrayList();
           
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
    employee1.setProject(project1);
    
    Employee employee2 = new Employee();
    employee2.setFirstName(CONTROL_EMPLOYEE2_NAME);
    employee2.setProject(project2);
    
    Employee employee3 = new Employee();
    employee3.setFirstName(CONTROL_EMPLOYEE3_NAME);
    employee3.setProject(project3);
    
    objects.add(employee1);
    objects.add(employee2);
    objects.add(employee3);   
    
    objects.add(project1);
    objects.add(project2);
    objects.add(project3);
    
    return objects;
   
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
	
	public static void main(String[] args)
  {
    String[] arguments = {"-c", "org.eclipse.persistence.testing.oxm.mappings.onetoone.keyontarget.eis.roottoroot.RootToRootTestCases"};
    junit.textui.TestRunner.main(arguments);		
  }
}

