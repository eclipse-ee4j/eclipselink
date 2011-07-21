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
package org.eclipse.persistence.testing.oxm.mappings.directtofield.nillable;

import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.nullpolicy.AbstractNullPolicy;
import org.eclipse.persistence.oxm.mappings.nullpolicy.IsSetNullPolicy;
import org.eclipse.persistence.oxm.mappings.nullpolicy.XMLNullRepresentationType;

import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.oxm.mappings.XMLWithJSONMappingTestCases;

public class DirectIsSetNullPolicyElementSetNillableIsSetTrueTestCases extends XMLWithJSONMappingTestCases {
	// TC UC 0-10 to 0-13, 3-0
    private final static String XML_RESOURCE = //
        "org/eclipse/persistence/testing/oxm/mappings/directtofield/nillable/DirectIsSetNullPolicyElementSetNillableIsSetTrue.xml";
    private final static String JSON_RESOURCE = //
        "org/eclipse/persistence/testing/oxm/mappings/directtofield/nillable/DirectIsSetNullPolicyElementSetNillableIsSetTrue.json";
    private final static String JSON_WRITE_RESOURCE = //
        "org/eclipse/persistence/testing/oxm/mappings/directtofield/nillable/DirectIsSetNullPolicyElementSetNillableIsSetTrueWrite.json";
    public DirectIsSetNullPolicyElementSetNillableIsSetTrueTestCases(String name) throws Exception {
         super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        setControlJSONWrite(JSON_WRITE_RESOURCE);
        AbstractNullPolicy aNullPolicy = new IsSetNullPolicy();
    	// Alter unmarshal policy state
    	aNullPolicy.setNullRepresentedByEmptyNode(true);
    	aNullPolicy.setNullRepresentedByXsiNil(false);  // no effect
    	// Alter marshal policy state
    	aNullPolicy.setMarshalNullRepresentation(XMLNullRepresentationType.XSI_NIL);
        ((IsSetNullPolicy)aNullPolicy).setIsSetMethodName("isSetFirstName");
        Project aProject = new DirectNodeNullPolicyProject(true);
        
        // Add xsi namespace map entry to the resolver (we don't pick up the one on the xml instance doc)
        XMLDescriptor employeeDescriptor = (XMLDescriptor) aProject.getDescriptor(Employee.class);
        NamespaceResolver namespaceResolver = new NamespaceResolver();
        namespaceResolver.put(XMLConstants.SCHEMA_INSTANCE_PREFIX, XMLConstants.SCHEMA_INSTANCE_URL);
        employeeDescriptor.setNamespaceResolver(namespaceResolver);        
        
        XMLDirectMapping aMapping = (XMLDirectMapping)aProject.getDescriptor(Employee.class)//
        	.getMappingForAttributeName("firstName");
        aMapping.setNullPolicy(aNullPolicy);
        setProject(aProject);
    }

    protected Object getControlObject() {
        Employee anEmployee = new Employee();
        anEmployee.setId(123);
        anEmployee.setFirstName(null);
        anEmployee.setLastName("Doe");
        return anEmployee;
    }
     
}
