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
package org.eclipse.persistence.testing.oxm.mappings.directtofield.nillable;

import org.eclipse.persistence.oxm.mappings.nullpolicy.AbstractNullPolicy;
import org.eclipse.persistence.oxm.mappings.nullpolicy.IsSetNullPolicy;
import org.eclipse.persistence.oxm.mappings.nullpolicy.XMLNullRepresentationType;

import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.oxm.mappings.XMLWithJSONMappingTestCases;

public class DirectIsSetOptionalNodeNullPolicyNonNillableElementDefaultSetDefaultTestCases extends XMLWithJSONMappingTestCases {
	// UC 3-3b
	/*
	<xsd:element name='employee'>
	<xsd:complexType><xsd:sequence>
		<xsd:element name='fn' type='xsd:string' default='John'/>
	</xsd:sequence></xsd:complexType>
	</xsd:element>

	Use Case #3-3b - Set to Default Value
	Unmarshal From							fn Property						Marshal To
	<employee><fn>John</fn></employee>		Get = 'John'	IsSet = true	<employee><fn>John<fn/></employee>
	 */

    private final static String XML_RESOURCE = //
    "org/eclipse/persistence/testing/oxm/mappings/directtofield/nillable/DirectIsSetOptionalNodeNullPolicyNonNillableElementDefaultSetDefault.xml";
    private final static String JSON_RESOURCE = //
    "org/eclipse/persistence/testing/oxm/mappings/directtofield/nillable/DirectIsSetOptionalNodeNullPolicyNonNillableElementDefaultSetDefault.json";

    public DirectIsSetOptionalNodeNullPolicyNonNillableElementDefaultSetDefaultTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        AbstractNullPolicy aNullPolicy = new IsSetNullPolicy();
    	// alter unmarshal policy state
    	aNullPolicy.setNullRepresentedByEmptyNode(false); // no effect (defaults are only for SDO)
    	aNullPolicy.setNullRepresentedByXsiNil(false); // no effect (defaults are only for SDO)
    	// alter marshal policy state
    	aNullPolicy.setMarshalNullRepresentation(XMLNullRepresentationType.EMPTY_NODE); // no effect (defaults are only for SDO)
        ((IsSetNullPolicy)aNullPolicy).setIsSetMethodName("isSetFirstName");
        Project aProject = new DirectNodeNullPolicyProject(true);
        XMLDirectMapping aMapping = (XMLDirectMapping)aProject.getDescriptor(Employee.class)//
        .getMappingForAttributeName("firstName");
        aMapping.setNullPolicy(aNullPolicy);
        setProject(aProject);
    }

    protected Object getControlObject() {
        Employee anEmployee = new Employee();
        anEmployee.setId(123);
        anEmployee.setFirstName("John"); // default
        anEmployee.setLastName("Doe");
        return anEmployee;
    }
}
