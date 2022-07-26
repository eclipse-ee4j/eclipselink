/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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

import org.eclipse.persistence.oxm.mappings.nullpolicy.AbstractNullPolicy;
import org.eclipse.persistence.oxm.mappings.nullpolicy.IsSetNullPolicy;
import org.eclipse.persistence.oxm.mappings.nullpolicy.XMLNullRepresentationType;

import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.oxm.mappings.XMLWithJSONMappingTestCases;

public class DirectIsSetNullPolicyAttributeAbsentIsSetAbsentTrueTestCases extends XMLWithJSONMappingTestCases {
    // TC UC 7-4, 5-9
    private final static String XML_RESOURCE = //
    "org/eclipse/persistence/testing/oxm/mappings/directtofield/nillable/DirectIsSetNullPolicyAttributeAbsentIsSetAbsentTrue.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/directtofield/nillable/DirectIsSetNullPolicyAttributeAbsentIsSetAbsentTrue.json";

    public DirectIsSetNullPolicyAttributeAbsentIsSetAbsentTrueTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        IsSetNullPolicy aNullPolicy = new IsSetNullPolicy();
        // Alter unmarshal policy state
        // Note: setting IsSetPerformedForAbsentNode to true is not valid here
        aNullPolicy.setNullRepresentedByEmptyNode(true); // No effect
        aNullPolicy.setNullRepresentedByXsiNil(false);  // No effect
        // Alter marshal policy state
        aNullPolicy.setMarshalNullRepresentation(XMLNullRepresentationType.ABSENT_NODE);
        aNullPolicy.setIsSetMethodName("isSetFirstName");
        Project aProject = new DirectNodeNullPolicyProject(false);
        XMLDirectMapping aMapping = (XMLDirectMapping)aProject.getDescriptor(Employee.class)//
        .getMappingForAttributeName("firstName");
        aMapping.setNullPolicy(aNullPolicy);
        setProject(aProject);
    }

    @Override
    protected Object getControlObject() {
        Employee anEmployee = new Employee();
        anEmployee.setId(123);
        // isSet = false if we do not perform a set
        anEmployee.setFirstName(null); // TODO: UC 5-9 Is not valid
        anEmployee.setLastName("Doe");
        return anEmployee;
    }


    @Override
    public void testObjectToXMLDocument() throws Exception {}
    @Override
    public void testObjectToXMLStringWriter() throws Exception {}
    @Override
    public void testObjectToContentHandler() throws Exception {}
    @Override
    public void testXMLToObjectFromURL() throws Exception {}
    @Override
    public void testUnmarshallerHandler() throws Exception {}
//    public void testXMLToObjectFromInputStream() throws Exception {}
 }
