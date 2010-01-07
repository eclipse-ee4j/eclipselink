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
package org.eclipse.persistence.testing.oxm.mappings.directtofield.nillable;

import junit.textui.TestRunner;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.nullpolicy.AbstractNullPolicy;
import org.eclipse.persistence.oxm.mappings.nullpolicy.IsSetNullPolicy;
import org.eclipse.persistence.oxm.mappings.nullpolicy.XMLNullRepresentationType;

import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;

public class DirectIsSetNodeNullPolicyTrueTestCases extends XMLMappingTestCases {
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/directtofield/nillable/DirectIsSetNodeNullPolicyTrue.xml";

    public DirectIsSetNodeNullPolicyTrueTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);

        AbstractNullPolicy aNullPolicy = new IsSetNullPolicy();
    	// alter unmarshal policy state
    	aNullPolicy.setNullRepresentedByEmptyNode(true); // no effect
    	aNullPolicy.setNullRepresentedByXsiNil(false); // no effect
    	// alter marshal policy state
    	aNullPolicy.setMarshalNullRepresentation(XMLNullRepresentationType.XSI_NIL);//.EMPTY_NODE);
        ((IsSetNullPolicy)aNullPolicy).setIsSetMethodName("isSetFirstName");
        Project aProject = new DirectNodeNullPolicyProject(true);
        XMLDescriptor employeeDescriptor = (XMLDescriptor) aProject.getDescriptor(Employee.class);
        NamespaceResolver namespaceResolver = new NamespaceResolver();
        namespaceResolver.put(XMLConstants.SCHEMA_INSTANCE_PREFIX, XMLConstants.SCHEMA_INSTANCE_URL);
        employeeDescriptor.setNamespaceResolver(namespaceResolver);        
        XMLDirectMapping aMapping = (XMLDirectMapping) employeeDescriptor.getMappingForAttributeName("firstName");
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
    
     public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.oxm.mappings.directtofield.nillable.DirectIsSetNodeNullPolicyTrueTestCases" };
        TestRunner.main(arguments);
    }
}
