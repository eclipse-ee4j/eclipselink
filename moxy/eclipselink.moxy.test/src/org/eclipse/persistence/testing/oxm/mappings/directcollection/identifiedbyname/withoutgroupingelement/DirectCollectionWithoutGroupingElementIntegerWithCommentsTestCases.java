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
package org.eclipse.persistence.testing.oxm.mappings.directcollection.identifiedbyname.withoutgroupingelement;

import java.util.Vector;
import java.io.InputStream;

import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;
import org.eclipse.persistence.testing.oxm.mappings.directcollection.Employee;

public class DirectCollectionWithoutGroupingElementIntegerWithCommentsTestCases extends XMLMappingTestCases {
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/directcollection/identifiedbyname/withoutgroupingelement/DirectCollectionWithoutGroupingElementIntegerWithComment.xml";
    private final static int CONTROL_ID = 123;
    private final static Integer CONTROL_RESPONSIBILITY1 = new Integer(100);
    private final static Integer CONTROL_RESPONSIBILITY2 = new Integer(200);
    private final static Integer CONTROL_RESPONSIBILITY3 = new Integer(300);

    public DirectCollectionWithoutGroupingElementIntegerWithCommentsTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setProject(new DirectCollectionWithoutGroupingElementIdentifiedByNameIntegerProject());
    }

    protected Object getControlObject() {
        Vector responsibilities = new Vector();
        responsibilities.addElement(CONTROL_RESPONSIBILITY1);
        responsibilities.addElement(CONTROL_RESPONSIBILITY2);
        responsibilities.addElement(CONTROL_RESPONSIBILITY3);

        Employee employee = new Employee();
        employee.setID(CONTROL_ID);
        employee.setResponsibilities(responsibilities);
        return employee;
    }
		
		 protected Document getWriteControlDocument() throws Exception {		    
																																																  
        String xmlResource = "org/eclipse/persistence/testing/oxm/mappings/directcollection/identifiedbyname/withoutgroupingelement/DirectCollectionWithoutGroupingElementIntegerWithCommentWriting.xml";
        InputStream inputStream = ClassLoader.getSystemResourceAsStream(xmlResource);

        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        builderFactory.setNamespaceAware(true);
        builderFactory.setIgnoringElementContentWhitespace(true);
        DocumentBuilder parser = builderFactory.newDocumentBuilder();
        Document writeControlDocument = parser.parse(inputStream);
        removeEmptyTextNodes(writeControlDocument);
        return writeControlDocument;
    }

    public void testObjectToContentHandler() throws Exception {
        // DO NOTHING BECAUSE CONTENT HANDLER CAN NOT READ COMMENTS
    }
}
