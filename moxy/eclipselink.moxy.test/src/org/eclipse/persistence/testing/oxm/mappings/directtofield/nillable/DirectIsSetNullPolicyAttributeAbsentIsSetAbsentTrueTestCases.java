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

public class DirectIsSetNullPolicyAttributeAbsentIsSetAbsentTrueTestCases extends XMLWithJSONMappingTestCases {
	// TC UC 7-4, 5-9
    private final static String XML_RESOURCE = //
    "org/eclipse/persistence/testing/oxm/mappings/directtofield/nillable/DirectIsSetNullPolicyAttributeAbsentIsSetAbsentTrue.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/directtofield/nillable/DirectIsSetNullPolicyAttributeAbsentIsSetAbsentTrue.json";

    public DirectIsSetNullPolicyAttributeAbsentIsSetAbsentTrueTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        AbstractNullPolicy aNullPolicy = new IsSetNullPolicy();
    	// Alter unmarshal policy state
        // Note: setting IsSetPerformedForAbsentNode to true is not valid here
        aNullPolicy.setNullRepresentedByEmptyNode(true); // No effect
    	aNullPolicy.setNullRepresentedByXsiNil(false);  // No effect
    	// Alter marshal policy state
    	aNullPolicy.setMarshalNullRepresentation(XMLNullRepresentationType.ABSENT_NODE);
        ((IsSetNullPolicy)aNullPolicy).setIsSetMethodName("isSetFirstName");
        Project aProject = new DirectNodeNullPolicyProject(false);
        XMLDirectMapping aMapping = (XMLDirectMapping)aProject.getDescriptor(Employee.class)//
        .getMappingForAttributeName("firstName");
        aMapping.setNullPolicy(aNullPolicy);
        setProject(aProject);
    }

    protected Object getControlObject() {
        Employee anEmployee = new Employee();
        anEmployee.setId(123);
        // isSet = false if we do not perform a set
        anEmployee.setFirstName(null); // TODO: UC 5-9 Is not valid
        anEmployee.setLastName("Doe");
        return anEmployee;
    }

    
	public void testObjectToXMLDocument() throws Exception {}
    public void testObjectToXMLStringWriter() throws Exception {}
    public void testObjectToContentHandler() throws Exception {}
    public void testXMLToObjectFromURL() throws Exception {}
    public void testUnmarshallerHandler() throws Exception {}
//    public void testXMLToObjectFromInputStream() throws Exception {}
 }
