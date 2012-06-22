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
package org.eclipse.persistence.tools.workbench.test.models.projects;

import javax.xml.namespace.QName;

import org.eclipse.persistence.tools.workbench.test.models.eis.employee.Employee;
import org.eclipse.persistence.tools.workbench.test.models.eis.employee.NormalHoursTransformer;

import org.eclipse.persistence.descriptors.ReturningPolicy;
import org.eclipse.persistence.eis.EISDescriptor;
import org.eclipse.persistence.eis.EISLogin;
import org.eclipse.persistence.eis.interactions.XMLInteraction;
import org.eclipse.persistence.eis.mappings.EISCompositeDirectCollectionMapping;
import org.eclipse.persistence.eis.mappings.EISDirectMapping;
import org.eclipse.persistence.eis.mappings.EISTransformationMapping;
import org.eclipse.persistence.internal.queries.CollectionContainerPolicy;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.platform.database.AttunityPlatform;
import org.eclipse.persistence.sessions.Project;

public class ReturningPolicyEisRuntimeProject {
	
	private Project runtimeProject;
	
	public ReturningPolicyEisRuntimeProject() {
		super();
		this.initialize();
	}
	
	private void initialize() {
		this.runtimeProject = new Project();
		this.runtimeProject.setName("ReturningEIS");
		this.runtimeProject.setLogin(new EISLogin(new AttunityPlatform()));
		this.initializeDescriptors();
	}
	
	private void initializeDescriptors() {
		this.runtimeProject.addDescriptor(this.buildEmployeeDescriptor());

	}
	
	private EISDescriptor buildEmployeeDescriptor() {
		EISDescriptor employeeDescriptor = new EISDescriptor();
		employeeDescriptor.setNamespaceResolver(new NamespaceResolver());
		employeeDescriptor.setJavaClassName(Employee.class.getName());
		employeeDescriptor.setDataTypeName("employee");

        employeeDescriptor.addPrimaryKeyField(new XMLField("@id"));
        XMLInteraction readInteraction = new XMLInteraction();
        readInteraction.setFunctionName("readEmployeeById");
        readInteraction.addArgument("id", "@id");
        employeeDescriptor.getDescriptorQueryManager().setReadObjectCall(readInteraction);

        XMLInteraction updateInteraction = new XMLInteraction();
        updateInteraction.setFunctionName("updateEmployee");
        updateInteraction.addArgument("id", "@id");
        employeeDescriptor.getDescriptorQueryManager().setUpdateCall(updateInteraction);

        XMLInteraction insertInteraction = new XMLInteraction();
        insertInteraction.setFunctionName("insertEmployee");
        insertInteraction.addArgument("id", "@id");
        employeeDescriptor.getDescriptorQueryManager().setInsertCall(insertInteraction);
       
        
        
        //initialize policies	
		employeeDescriptor.setReturningPolicy(new ReturningPolicy());
		employeeDescriptor.getReturningPolicy().addFieldForInsertReturnOnly(new XMLField("personal-information/@first-name"));
		employeeDescriptor.getReturningPolicy().addFieldForUpdate(new XMLField("personal-information/@last-name"));
			
 		
        EISDirectMapping idMapping = new EISDirectMapping();
        idMapping.setAttributeName("gender");
        idMapping.setXPath("@id");
        employeeDescriptor.addMapping(idMapping);
        
        EISDirectMapping firstNameMapping = new EISDirectMapping();
        firstNameMapping.setAttributeName("firstName");
        firstNameMapping.setXPath("personal-information/@first-name");
        employeeDescriptor.addMapping(firstNameMapping);
 
        EISDirectMapping lastNameMapping = new EISDirectMapping();
        lastNameMapping.setAttributeName("lastName");
        lastNameMapping.setXPath("personal-information/@last-name");
        employeeDescriptor.addMapping(lastNameMapping);
 
        EISCompositeDirectCollectionMapping responsibilitiesMapping = new EISCompositeDirectCollectionMapping();
        responsibilitiesMapping.setAttributeName("responsibilities");
        responsibilitiesMapping.setXPath("responsibility/text()");
        responsibilitiesMapping.setContainerPolicy(new CollectionContainerPolicy("java.util.Vector"));
        employeeDescriptor.addMapping(responsibilitiesMapping);
                
        EISTransformationMapping normalHoursMapping = new EISTransformationMapping();
        normalHoursMapping.setAttributeName("normalHours");
        normalHoursMapping.setAttributeTransformerClassName("org.eclipse.persistence.tools.workbench.test.models.eis.employee.NormalHoursTransformer");
        
        XMLField startTimeField = new XMLField("working-hours/start-time/text()");
        startTimeField.setSchemaType(new QName(XMLConstants.SCHEMA_URL, XMLConstants.DATE_TIME));
        normalHoursMapping.addFieldTransformerClassName(startTimeField, NormalHoursTransformer.class.getName());       
         
        XMLField endTimeField = new XMLField("working-hours/end-time/text()");
        endTimeField.setSchemaType(new QName(XMLConstants.SCHEMA_URL, XMLConstants.DATE_TIME));
        normalHoursMapping.addFieldTransformerClassName(endTimeField, NormalHoursTransformer.class.getName());
        employeeDescriptor.addMapping(normalHoursMapping);
                
        return employeeDescriptor;
	
	}
	

	
	public Project getRuntimeProject() {
		return this.runtimeProject;
	}
}
