/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * dmccann - January 06/2010 - 2.0 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlschematypes;

import java.io.File;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.eclipse.persistence.testing.jaxb.externalizedmetadata.ExternalizedMetadataTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlschematypes.Employee;
import org.w3c.dom.Document;

/**
 * Tests XmlSchemaTypes via eclipselink-oxm.xml
 *
 */
public class XmlSchemaTypesTestCases extends ExternalizedMetadataTestCases {
    private static final String CONTEXT_PATH = "org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlschematypes";
    private static final String PATH = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlschematypes/";
    
    /**
     * This is the preferred (and only) constructor.
     * 
     * @param name
     */
    public XmlSchemaTypesTestCases(String name) {
        super(name);
    }

    /**
     * Tests @XmlSchemaTypes schema generation via eclipselink-oxm.xml.  Here, a 
     * package-level xml-schema-types declaration exists.  It has one entry, 
     * whose value "date" should override the one set in code ("year").
     * 
     * Positive test.
     */
    public void testXmlSchemaTypesSchemaGen() {
        MySchemaOutputResolver outputResolver = generateSchema(new Class[] { Employee.class }, CONTEXT_PATH, PATH, 1);
        // validate schema
        String controlSchema = PATH + "schema.xsd";
        compareSchemas(outputResolver.schemaFiles.get(EMPTY_NAMESPACE), new File(controlSchema));
    }
    
    /**
     * Tests @XmlSchemaTypes via eclipselink-oxm.xml.
     * 
     * Positive test.
     */
    public void testXmlSchemaTypes() {
        // load XML metadata
        MySchemaOutputResolver outputResolver = generateSchema(new Class[] { Employee.class }, CONTEXT_PATH, PATH, 1);

        // load instance doc
        String src = PATH + "employee.xml";
        InputStream iDocStream = loader.getResourceAsStream(src);
        if (iDocStream == null) {
            fail("Couldn't load instance doc [" + src + "]");
        }

        // setup control Employee
        GregorianCalendar calendar = new GregorianCalendar();
        Date theDate = new Date(new Long("1262840400000"));
        calendar.setTime(theDate);
        Employee emp = new Employee();
        emp.hireDate = calendar;
        emp.lengthOfEmployment = new BigDecimal("1.000010");
        
        // unmarshal
        Employee obj = null;
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        try {
            obj = (Employee) unmarshaller.unmarshal(iDocStream);
            assertFalse("Unmarshalled object is null.", obj == null);
            assertTrue("Unmarshal failed - object is not equal to control Employee", emp.equals(obj));
        } catch (JAXBException e) {
            e.printStackTrace();
            fail("Unmarshal operation failed.");
        }

        Document testDoc = parser.newDocument();
        Document ctrlDoc = parser.newDocument();
        try {
            ctrlDoc = getControlDocument(src);
        } catch (Exception e) {
            e.printStackTrace();
            fail("An unexpected exception occurred loading control document [" + src + "].");
        }

        // marshal
        Marshaller marshaller = jaxbContext.createMarshaller();
        try {
            marshaller.marshal(emp, testDoc);
            assertTrue("Document comparison failed unxepectedly: ", compareDocuments(ctrlDoc, testDoc));
        } catch (JAXBException e) {
            e.printStackTrace();
            fail("Unmarshal operation failed.");
        }
    }
}