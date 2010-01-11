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
package org.eclipse.persistence.tools.workbench.test.models.projects;

import org.eclipse.persistence.tools.workbench.test.models.eis.employee.NormalHoursTransformer;

import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.xml.MWEisDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.xml.MWXmlPrimaryKeyPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.xml.MWEisReturningPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.xml.MWEisTransactionalPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.xml.MWRootEisDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWXmlDirectCollectionMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWXmlDirectMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWXmlTransformationMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClass;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.MWProject;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.xml.MWEisLoginSpec;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.xml.MWEisProject;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.xml.MWEisInteraction;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.xml.MWEisQueryManager;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWElementDeclaration;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWXmlSchema;

/**
 * Created to test returning policy features
 */
public class ReturningPolicyEisProject extends XmlTestProject{

	public ReturningPolicyEisProject()
	{
		super();
	}
	
	@Override
	protected MWProject buildEmptyProject() {
		return new MWEisProject("ReturningEIS", MWEisLoginSpec.JMS_ADAPTER_NAME, spiManager());
	}
	
	@Override
	protected void initializeSchemas() {
		super.initializeSchemas();
		this.addSchema("eis-employee.xsd", "/schema/eis-employee.xsd");
	}
	
	@Override
	public void initializeDescriptors() {
		super.initializeDescriptors();

		MWEisDescriptor descriptor = (MWEisDescriptor) this.addDescriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.eis.employee.Employee");
		descriptor.asRootEisDescriptor();

		initializeEmployeeDescriptor();
	}
	
	public MWRootEisDescriptor getEmployeeDescriptor() {
		return (MWRootEisDescriptor)  getProject().descriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.eis.employee.Employee");		
	}
	
	public void initializeEmployeeDescriptor() {
		MWRootEisDescriptor employeeDescriptor = getEmployeeDescriptor();
		
		MWXmlSchema employeeSchema = (MWXmlSchema) getProject().getSchemaRepository().schemas().next();
		MWElementDeclaration employeeElement = employeeSchema.element("employee");
        MWClass employeeClass = employeeDescriptor.getMWClass();
		
		employeeDescriptor.setSchemaContext(employeeElement);
        MWXmlPrimaryKeyPolicy primaryKeyPolicy = ((MWEisTransactionalPolicy) employeeDescriptor.getTransactionalPolicy()).getPrimaryKeyPolicy();
        primaryKeyPolicy.addPrimaryKey("@id");

        MWEisQueryManager queryManager = (MWEisQueryManager) employeeDescriptor.getQueryManager();
        MWEisInteraction readObjectInteraction = queryManager.getReadObjectInteraction();
        readObjectInteraction.setFunctionName("readEmployeeById");
        readObjectInteraction.addInputArgument("id", "@id");
        
        MWEisInteraction insertInteraction = queryManager.getInsertInteraction();
        insertInteraction.setFunctionName("insertEmployee");
        insertInteraction.addInputArgument("id", "@id");
        
        MWEisInteraction updateInteraction = queryManager.getUpdateInteraction();
        updateInteraction.setFunctionName("updateEmployee");
        updateInteraction.addInputArgument("id", "@id");

        
        MWXmlDirectMapping idMapping = 
            (MWXmlDirectMapping) employeeDescriptor.addDirectMapping(employeeClass.attributeNamed("gender"));
        idMapping.getXmlField().setXpath("@id");
        
        MWXmlDirectMapping firstNameMapping = 
            (MWXmlDirectMapping) employeeDescriptor.addDirectMapping(employeeClass.attributeNamed("firstName"));
        firstNameMapping.getXmlField().setXpath("personal-information/@first-name");

        MWXmlDirectMapping lastNameMapping = 
            (MWXmlDirectMapping) employeeDescriptor.addDirectMapping(employeeClass.attributeNamed("lastName"));
        lastNameMapping.getXmlField().setXpath("personal-information/@last-name");

        MWXmlDirectCollectionMapping responsibilitiesMapping = 
            (MWXmlDirectCollectionMapping) employeeDescriptor.addDirectCollectionMapping(employeeClass.attributeNamed("responsibilities"));
        responsibilitiesMapping.getXmlField().setXpath("responsibility/text()");
        responsibilitiesMapping.getContainerPolicy().getDefaultingContainerClass().usesDefaultContainerClass();

        MWXmlTransformationMapping normalHoursMapping = 
            (MWXmlTransformationMapping) employeeDescriptor.addTransformationMapping(employeeClass.attributeNamed("normalHours"));
        normalHoursMapping.setAttributeTransformer(normalHoursMapping.typeFor(NormalHoursTransformer.class));
        normalHoursMapping.addFieldTransformerAssociation("working-hours/start-time/text()", normalHoursMapping.typeFor(NormalHoursTransformer.class));
        normalHoursMapping.addFieldTransformerAssociation("working-hours/end-time/text()", normalHoursMapping.typeFor(NormalHoursTransformer.class));

        
        //initialize policies	
		employeeDescriptor.addReturningPolicy();
		
		MWEisReturningPolicy returningPolicy = (MWEisReturningPolicy) employeeDescriptor.getReturningPolicy();
		
		returningPolicy.addInsertFieldReadOnlyFlag("personal-information/@first-name").setReturnOnly(true);
		
		returningPolicy.addUpdateField("personal-information/@last-name");
	}
}
