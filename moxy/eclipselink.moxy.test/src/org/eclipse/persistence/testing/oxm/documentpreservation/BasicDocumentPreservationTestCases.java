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
package org.eclipse.persistence.testing.oxm.documentpreservation;

import java.io.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.eclipse.persistence.oxm.*;
import org.eclipse.persistence.testing.oxm.*;
import org.w3c.dom.Document;

/**
 *  @version $Header: BasicDocumentPreservationTestCases.java 30-jul-2007.15:32:24 dmccann Exp $
 *  @author  mmacivor
 *  @since   release specific (what release of product did this appear in)
 */
public class BasicDocumentPreservationTestCases extends OXTestCase {
    public XMLContext context;
    public XMLMarshaller marshaller;
    public XMLUnmarshaller unmarshaller;
    public DocumentBuilder parser;

    public BasicDocumentPreservationTestCases() {
        super("Basic Document Preservation Tests");
    }

    public BasicDocumentPreservationTestCases(String name) {
        super(name);
    }

    public void setUp() throws Exception {
        context = this.getXMLContext("DocumentPreservationSession");
        marshaller = context.createMarshaller();
        unmarshaller = context.createUnmarshaller();
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        builderFactory.setNamespaceAware(true);
        builderFactory.setIgnoringElementContentWhitespace(true);
        parser = builderFactory.newDocumentBuilder();
    }

    public void testRoundTrip() throws Exception {
        Document controlDocument = parse("org/eclipse/persistence/testing/oxm/documentpreservation/roundTrip.xml");
        Document sourceDocument = parse("org/eclipse/persistence/testing/oxm/documentpreservation/roundTrip.xml");

        Employee emp = (Employee)unmarshaller.unmarshal(sourceDocument);

        Document outputDoc = (Document)marshaller.objectToXML(emp);
        assertXMLIdentical(controlDocument, outputDoc);
    }

    public void testNullDirectMapping() throws Exception {
        Document controlDocument = parse("org/eclipse/persistence/testing/oxm/documentpreservation/null_direct_mapping.xml");
        Document sourceDocument = parse("org/eclipse/persistence/testing/oxm/documentpreservation/roundTrip.xml");

        Employee emp = (Employee)unmarshaller.unmarshal(sourceDocument);
        emp.setLastName(null);
        emp.getAddress().setCity(null);

        Document outputDoc = (Document)marshaller.objectToXML(emp);
        assertXMLIdentical(controlDocument, outputDoc);
    }

    public void testMissingElement() throws Exception {
        Document controlDocument = parse("org/eclipse/persistence/testing/oxm/documentpreservation/missing_element_result.xml");
        Document sourceDocument = parse("org/eclipse/persistence/testing/oxm/documentpreservation/missing_element_source.xml");

        Employee emp = (Employee)unmarshaller.unmarshal(sourceDocument);

        CanadianAddress addr = new CanadianAddress();
        addr.setStreet("2001 Odessy Drive");
        addr.setCity("Ottawa");
        addr.setProvince("ON");
        addr.setPostalCode("A1A 1A1");
        emp.setAddress(addr);
        Document outputDoc = (Document)marshaller.objectToXML(emp);
        assertXMLIdentical(controlDocument, outputDoc);
    }

    private Document parse(String resource) throws Exception {
        InputStream stream = getClass().getClassLoader().getResourceAsStream(resource);
        Document document = parser.parse(stream);
        removeEmptyTextNodes(document);
        return document;
    }
}
