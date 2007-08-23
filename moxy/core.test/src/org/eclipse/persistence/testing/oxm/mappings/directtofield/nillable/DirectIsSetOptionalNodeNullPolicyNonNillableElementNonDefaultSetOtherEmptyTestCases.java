/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/
package org.eclipse.persistence.testing.oxm.mappings.directtofield.nillable;

import org.eclipse.persistence.oxm.mappings.IsSetOptionalNodeNullPolicy;
import org.eclipse.persistence.oxm.mappings.NodeNullPolicy;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;

public class DirectIsSetOptionalNodeNullPolicyNonNillableElementNonDefaultSetOtherEmptyTestCases extends XMLMappingTestCases {
	// UC 1-4
	/*
	<xsd:element name='employee'>
	<xsd:complexType><xsd:sequence>
		<xsd:element name='fn' type='xsd:string'/>
	</xsd:sequence></xsd:complexType>
	</xsd:element>

	Use Case #1-4 - Other
	Unmarshal From								fn Property					Marshal To
	<employee><fn xsi:nil='true'/></employee>	Get = null	IsSet = true	<employee><fn/></employee>
	 */

    private final static String XML_RESOURCE = //
    "org/eclipse/persistence/testing/oxm/mappings/directtofield/nillable/DirectIsSetOptionalNodeNullPolicyNonNillableElementNonDefaultSetOtherEmpty.xml";

    public DirectIsSetOptionalNodeNullPolicyNonNillableElementNonDefaultSetOtherEmptyTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);

        NodeNullPolicy aNodeNullPolicy = new IsSetOptionalNodeNullPolicy();
        ((IsSetOptionalNodeNullPolicy)aNodeNullPolicy).setIsSetMethodName("isSetFirstName");
        Project aProject = new DirectNodeNullPolicyProject(true);
        XMLDirectMapping aMapping = (XMLDirectMapping)aProject.getDescriptor(Employee.class)//
        .getMappingForAttributeName("firstName");
        aMapping.setNodeNullPolicy(aNodeNullPolicy);
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