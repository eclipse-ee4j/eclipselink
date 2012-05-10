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

import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.oxm.mappings.nullpolicy.AbstractNullPolicy;
import org.eclipse.persistence.oxm.mappings.nullpolicy.IsSetNullPolicy;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.oxm.mappings.XMLWithJSONMappingTestCases;

public class DirectIsSetOptionalNodeNullPolicyNonNillableElementNonDefaultSetNonNullTestCases extends XMLWithJSONMappingTestCases {
	// UC 0-0 and UC 3
	/*
	<xsd:element name='employee'>
	<xsd:complexType><xsd:sequence>
		<xsd:element name='fn' type='xsd:string'/>
	</xsd:sequence></xsd:complexType>
	</xsd:element>

	Use Case #0-0 - Set to Non-Null Value/.
	Unmarshal From													fn Property								Marshal To
	<employee><fn>Jane</fn></employee>		Get = 'Jane'	IsSet = true	<employee><fn>Jane</fn></employee>
	 */
    private final static String XML_RESOURCE = //
    "org/eclipse/persistence/testing/oxm/mappings/directtofield/nillable/DirectIsSetOptionalNodeNullPolicyNonNillableElementNonDefaultSetNonNull.xml";
    private final static String JSON_RESOURCE = //
    "org/eclipse/persistence/testing/oxm/mappings/directtofield/nillable/DirectIsSetOptionalNodeNullPolicyNonNillableElementNonDefaultSetNonNull.json";
    																	 
    public DirectIsSetOptionalNodeNullPolicyNonNillableElementNonDefaultSetNonNullTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        // use default policy with default flags and marshal enum
        AbstractNullPolicy aNullPolicy = new IsSetNullPolicy();        
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
        anEmployee.setFirstName("Jane");
        anEmployee.setLastName("Doe");
        return anEmployee;
    }
}
