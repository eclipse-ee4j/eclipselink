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
package org.eclipse.persistence.testing.oxm.mappings.onetoone.keyonsource.eis.roottoroot;

import org.eclipse.persistence.eis.EISDescriptor;
import org.eclipse.persistence.eis.EISLogin;
import org.eclipse.persistence.eis.interactions.XQueryInteraction;
import org.eclipse.persistence.eis.mappings.EISDirectMapping;
import org.eclipse.persistence.eis.mappings.EISOneToOneMapping;
import org.eclipse.persistence.eis.adapters.xmlfile.XMLFileEISConnectionSpec;
import org.eclipse.persistence.eis.adapters.xmlfile.XMLFilePlatform;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.queries.InsertObjectQuery;
import org.eclipse.persistence.testing.oxm.mappings.onetoone.keyonsource.*;

public class RootToRootProject extends org.eclipse.persistence.sessions.Project {

  public RootToRootProject() {
    addDescriptor(getEmployeeDescriptor());
    addDescriptor(getProjectDescriptor());
    
    EISLogin login = new EISLogin(new XMLFilePlatform());
		login.setConnectionSpec(new XMLFileEISConnectionSpec());
		login.setProperty("directory", "./org/eclipse/persistence/testing/oxm/mappings/onetoone/keyonsource/eis/roottoroot/");
		setLogin(login);
  }

 
  private EISDescriptor getEmployeeDescriptor() {
    EISDescriptor descriptor = new EISDescriptor();
    descriptor.setJavaClass(Employee.class);
    descriptor.setDataTypeName("employee");
    descriptor.setPrimaryKeyFieldName("first-name/text()");
    
    EISDirectMapping firstNameMapping = new EISDirectMapping();
    firstNameMapping.setAttributeName("firstName");
    firstNameMapping.setXPath("first-name/text()");
    descriptor.addMapping(firstNameMapping); 
    
    EISOneToOneMapping projectMapping = new EISOneToOneMapping();
    
    projectMapping.setReferenceClass(Project.class);
    projectMapping.setAttributeName("project");
    projectMapping.dontUseIndirection();
    XQueryInteraction projectInteraction = new XQueryInteraction();	
		projectInteraction.setFunctionName("read-project");
		projectInteraction.setProperty("fileName", "project.xml");
		projectInteraction.setXQueryString("project[@id='#@project-id']");
		projectInteraction.setOutputResultPath("result");			
		projectMapping.setSelectionCall(projectInteraction);
		projectMapping.addForeignKeyFieldName("@project-id", "@id");

    descriptor.addMapping(projectMapping); 
    
    // Insert
		XQueryInteraction insertCall = new XQueryInteraction();
		insertCall.setFunctionName("insert");
		insertCall.setProperty("fileName", "employee.xml");
		insertCall.setXQueryString("employee");
		descriptor.getQueryManager().setInsertCall(insertCall);
		insertCall.setInputRootElementName("employee");
		insertCall.setOutputRootElementName("employee");
		insertCall.setXQueryString("employee");
		descriptor.getQueryManager().setInsertQuery(new InsertObjectQuery(insertCall));
		
    // Read object
		
		XQueryInteraction readObjectCall = new XQueryInteraction();
		readObjectCall.setFunctionName("read");
		readObjectCall.setProperty("fileName", "employee.xml");
		readObjectCall.setXQueryString("employee[first-name='#first-name/text()']");
		readObjectCall.setOutputResultPath("result");
		descriptor.getQueryManager().setReadObjectCall(readObjectCall);
		
		// Read all
		XQueryInteraction readAllCall = new XQueryInteraction();
		readAllCall.setFunctionName("read-all");
		readAllCall.setProperty("fileName", "employee.xml");
		readAllCall.setXQueryString("employee");
		readAllCall.setOutputResultPath("result");
		descriptor.getQueryManager().setReadAllCall(readAllCall);

	  // Delete
    XQueryInteraction deleteCall = new XQueryInteraction();
    deleteCall.setFunctionName("delete");
    deleteCall.setProperty("fileName", "employee.xml");
		deleteCall.setXQueryString("employee[first-name='#first-name/text()']");
    descriptor.getQueryManager().setDeleteCall(deleteCall);

	  //Update
    XQueryInteraction updateCall = new XQueryInteraction();
    updateCall.setFunctionName("update");
    updateCall.setProperty("fileName", "employee.xml");
 		updateCall.setXQueryString("employee[first-name='#first-name/text()']");
    descriptor.getQueryManager().setUpdateCall(updateCall);
    return descriptor;
  }
   
  private ClassDescriptor getProjectDescriptor() {
    EISDescriptor descriptor = new EISDescriptor();
    descriptor.setJavaClass(Project.class);
    descriptor.setDataTypeName("project");
    descriptor.setPrimaryKeyFieldName("@id");
      
    EISDirectMapping nameMapping = new EISDirectMapping();
    nameMapping.setAttributeName("name");
    nameMapping.setXPath("name/text()");
    descriptor.addMapping(nameMapping); 

    EISDirectMapping idMapping = new EISDirectMapping();
    idMapping.setAttributeName("id");
    idMapping.setXPath("@id");
    descriptor.addMapping(idMapping);
    
    // Insert
		XQueryInteraction insertCall = new XQueryInteraction();
		insertCall.setXQueryString("project");
		insertCall.setFunctionName("insert");
		insertCall.setProperty("fileName", "project.xml");
		descriptor.getQueryManager().setInsertCall(insertCall);

		// Read object
		XQueryInteraction readObjectCall = new XQueryInteraction();
		readObjectCall.setFunctionName("read");
		readObjectCall.setProperty("fileName", "project.xml");
		readObjectCall.setXQueryString("project[@id='#@id']");
		readObjectCall.setOutputResultPath("result");
		descriptor.getQueryManager().setReadObjectCall(readObjectCall);

		// Read all
		XQueryInteraction readAllCall = new XQueryInteraction();
		readAllCall.setFunctionName("read-all");
		readAllCall.setProperty("fileName", "project.xml");
		readAllCall.setXQueryString("project");
		readAllCall.setOutputResultPath("result");
		descriptor.getQueryManager().setReadAllCall(readAllCall);
    
		  // Delete
		XQueryInteraction deleteCall = new XQueryInteraction();
		deleteCall.setFunctionName("delete");
		readAllCall.setProperty("fileName", "project.xml");
		readObjectCall.setXQueryString("project[@id='#@id']");
		descriptor.getQueryManager().setDeleteCall(deleteCall);

		  //Update
		XQueryInteraction updateCall = new XQueryInteraction();
		updateCall.setFunctionName("update");
		readAllCall.setProperty("fileName", "project.xml");
		readObjectCall.setXQueryString("project[@id='#@id']");
    descriptor.getQueryManager().setUpdateCall(updateCall);
		
		return descriptor;
  }
}

