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
package org.eclipse.persistence.testing.oxm.mappings.directtofield.defaultnullvalue.xmlelement;

import junit.textui.TestRunner;

import org.eclipse.persistence.oxm.XMLRoot;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.oxm.OXTestCase.Metadata;
import org.eclipse.persistence.testing.oxm.OXTestCase.Platform;
import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;
import org.eclipse.persistence.testing.oxm.mappings.directtofield.defaultnullvalue.Employee;

import org.w3c.dom.Document;

public class EmptyElementTestCases extends XMLMappingTestCases {

	private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/directtofield/defaultnullvalue/xmlelement/EmptyElement.xml";
	private final static String XML_WRITE_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/directtofield/defaultnullvalue/xmlelement/EmptyElementWrite.xml";

	/**
	 * Empty Element single tag With nullValue=EMPTY_STRING "<employee/>", "<employee/>",
	 * true, false); // Expected result "<employee><first-name/></employee>", true, false); // Input document
	 */

	public EmptyElementTestCases(String name) throws Exception {
		super(name);
		setControlDocument(XML_RESOURCE);
		//setWriteControlDocument(XML_WRITE_RESOURCE);
		Project aProject = new DefaultNullValueElementProject();
		setProject(aProject);
		// Force empty element marshalling
		XMLDirectMapping firstNameMapping = (XMLDirectMapping) aProject
				.getDescriptor(Employee.class)//
				.getMappingForAttributeName("firstName");
		// firstNameMapping.getNullPolicy().setMarshalNullRepresentation(XMLNullRepresentationType.EMPTY_NODE);
		XMLDirectMapping numericNoNullValueMapping = (XMLDirectMapping) aProject
				.getDescriptor(Employee.class)//
				.getMappingForAttributeName("numericNoNullValue");
		// numericNoNullValueMapping.getNullPolicy().setMarshalNullRepresentation(XMLNullRepresentationType.EMPTY_NODE);
		XMLDirectMapping idMapping = (XMLDirectMapping) aProject.getDescriptor(
				Employee.class)//
				.getMappingForAttributeName("id");
		// idMapping.getNullPolicy().setMarshalNullRepresentation(XMLNullRepresentationType.EMPTY_NODE);
	}

	protected Object getControlObject() {
		Employee employee = new Employee();
		// We currently have different behavior when using XMLProjectReader
  	    if(platform==Platform.DOM && metadata==Metadata.XML_ECLIPSELINK) {
			employee.setID(DefaultNullValueElementProject.CONTROL_ID);
			// See bug#209398 - current behavior for deployment xml is to not preserve the nullValue of ""
			//employee.setFirstName(DefaultNullValueElementProject.CONTROL_FIRSTNAME);
		} else {
			employee.setID(DefaultNullValueElementProject.CONTROL_ID);
			// See bug#209398 - current behavior for deployment xml is to not preserve the nullValue of ""
			employee	.setFirstName(DefaultNullValueElementProject.CONTROL_FIRSTNAME);
		}
		return employee;
	}

	public Object getWriteControlObject() {
		Employee employee = new Employee();
		// We currently have different behavior when using XMLProjectReader
  	    if(platform==Platform.DOM && metadata==Metadata.XML_ECLIPSELINK) {
			employee.setID(DefaultNullValueElementProject.CONTROL_ID);
			// See bug#209398 - current behavior for deployment xml is to not
			// preserve the nullValue of ""
			//employee.setFirstName(DefaultNullValueElementProject.CONTROL_FIRSTNAME);
		} else {
			employee.setID(DefaultNullValueElementProject.CONTROL_ID);
			// See bug#209398 - current behavior for deployment xml is to not preserve the nullValue of ""
			employee	.setFirstName(DefaultNullValueElementProject.CONTROL_FIRSTNAME);
		}
		return employee;
	}

	// public void testObjectToXMLDocument() throws Exception { }

	public void testObjectToXMLDocumentToObjectRoundTrip() throws Exception {
		// Object to XMLDocument
		Document testDocument = xmlMarshaller.objectToXML(getWriteControlObject());
		objectToXMLDocumentTest(testDocument);

		// XMLDocument to Object
		Object testObject = xmlUnmarshaller.unmarshal(testDocument);
		log("Expected:");
		log(getReadControlObject().toString());
		log("Actual:");
		log(testObject.toString());
		if ((getReadControlObject() instanceof XMLRoot)
				&& (testObject instanceof XMLRoot)) {
			XMLRoot controlObj = (XMLRoot) getReadControlObject();
			XMLRoot testObj = (XMLRoot) testObject;
			compareXMLRootObjects(controlObj, testObj);
		} else {
			assertEquals(getReadControlObject(), testObject);
		}
	}

	public static void main(String[] args) {
		String[] arguments = {
				"-c",
				"oracle.toplink.testing.ox.mappings.directtofield.defaultnullvalue.xmlelement.EmptyElementTestCases" };
		TestRunner.main(arguments);
	}
}
