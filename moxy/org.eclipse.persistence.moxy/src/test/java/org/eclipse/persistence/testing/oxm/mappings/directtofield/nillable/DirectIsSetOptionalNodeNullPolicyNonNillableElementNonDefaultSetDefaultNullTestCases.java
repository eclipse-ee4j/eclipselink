/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.oxm.mappings.directtofield.nillable;

import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.oxm.mappings.nullpolicy.AbstractNullPolicy;
import org.eclipse.persistence.oxm.mappings.nullpolicy.IsSetNullPolicy;
import org.eclipse.persistence.oxm.mappings.nullpolicy.XMLNullRepresentationType;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.oxm.mappings.XMLWithJSONMappingTestCases;

public class DirectIsSetOptionalNodeNullPolicyNonNillableElementNonDefaultSetDefaultNullTestCases extends XMLWithJSONMappingTestCases {
    // UC 1-3
    /*
    <xsd:element name='employee'>
    <xsd:complexType><xsd:sequence>
        <xsd:element name='fn' type='xsd:string'/>
    </xsd:sequence></xsd:complexType>
    </xsd:element>

    Use Case #1-3 - Set to Null/Default Value
    Unmarshal From                    fn Property    Marshal To
    <employee><fn/></employee>        Get = null    IsSet = true    <employee><fn/></employee>
     */

    private final static String XML_RESOURCE = //
    "org/eclipse/persistence/testing/oxm/mappings/directtofield/nillable/DirectIsSetOptionalNodeNullPolicyNonNillableElementNonDefaultSetDefaultNull.xml";
    private final static String JSON_RESOURCE = //
    "org/eclipse/persistence/testing/oxm/mappings/directtofield/nillable/DirectIsSetOptionalNodeNullPolicyNonNillableElementNonDefaultSetDefaultNull.json";

    public DirectIsSetOptionalNodeNullPolicyNonNillableElementNonDefaultSetDefaultNullTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        AbstractNullPolicy aNullPolicy = new IsSetNullPolicy();
        // alter unmarshal policy state
        aNullPolicy.setNullRepresentedByEmptyNode(true);
        aNullPolicy.setNullRepresentedByXsiNil(false);
        // alter marshal policy state
        aNullPolicy.setMarshalNullRepresentation(XMLNullRepresentationType.EMPTY_NODE);

        ((IsSetNullPolicy)aNullPolicy).setIsSetMethodName("isSetFirstName");
        Project aProject = new DirectNodeNullPolicyProject(true);
        XMLDescriptor employeeDescriptor = (XMLDescriptor) aProject.getDescriptor(Employee.class);
        NamespaceResolver namespaceResolver = new NamespaceResolver();
        namespaceResolver.put(XMLConstants.SCHEMA_INSTANCE_PREFIX, javax.xml.XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI);
        employeeDescriptor.setNamespaceResolver(namespaceResolver);

        XMLDirectMapping aMapping = (XMLDirectMapping)aProject.getDescriptor(Employee.class).getMappingForAttributeName("firstName");
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
