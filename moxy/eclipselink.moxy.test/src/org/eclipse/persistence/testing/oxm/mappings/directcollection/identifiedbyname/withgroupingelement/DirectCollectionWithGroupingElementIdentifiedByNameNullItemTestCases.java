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
package org.eclipse.persistence.testing.oxm.mappings.directcollection.identifiedbyname.withgroupingelement;

import java.io.InputStream;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;
import org.eclipse.persistence.testing.oxm.mappings.directcollection.Employee;

import org.w3c.dom.Document;

public class DirectCollectionWithGroupingElementIdentifiedByNameNullItemTestCases extends XMLMappingTestCases 
{
	private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/directcollection/identifiedbyname/withgroupingelement/DirectCollectionWithGroupingElementNullItem.xml";
	private final static String XML_WRITE_CONTROL_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/directcollection/identifiedbyname/withgroupingelement/DirectCollectionWithGroupingElementNullItemWriteControl.xml";
	
  private final static int CONTROL_ID = 123;
  private final static String CONTROL_RESPONSIBILITY1 = "do the dishes";

	public DirectCollectionWithGroupingElementIdentifiedByNameNullItemTestCases(String name) throws Exception
	{
		super(name);
    setControlDocument(XML_RESOURCE);
		setProject(new DirectCollectionWithGroupingElementIdentifiedByNameProject());
	}
	
	protected Object getControlObject() {
		Vector responsibilities = new Vector();

		responsibilities.addElement(null);
		responsibilities.addElement(CONTROL_RESPONSIBILITY1);
		responsibilities.addElement(null);

    Employee employee = new Employee();
    employee.setID(CONTROL_ID);
		employee.setResponsibilities(responsibilities);
    return employee;
  }
	
	public Document getWriteControlDocument() throws Exception{
		InputStream inputStream = ClassLoader.getSystemResourceAsStream(XML_WRITE_CONTROL_RESOURCE);
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		builderFactory.setNamespaceAware(true);
		builderFactory.setIgnoringElementContentWhitespace(true);
		DocumentBuilder parser = builderFactory.newDocumentBuilder();
		Document returnDoc = parser.parse(inputStream);
		removeEmptyTextNodes(controlDocument);
		return returnDoc;
	}
	
	/*
	 * Nulls are read in as empty collections.
	 */
	public Object getReadControlObject() {
    Employee employee = new Employee();
    employee.setID(CONTROL_ID);		
		Vector responsibilities = new Vector();
		responsibilities.addElement(null);
		responsibilities.addElement(CONTROL_RESPONSIBILITY1);
		responsibilities.addElement(null);
		employee.setResponsibilities(responsibilities);
    return employee;	
	}
  
    public void testObjectToContentHandler() throws Exception {
        // DO NOTHING
    }  
}
