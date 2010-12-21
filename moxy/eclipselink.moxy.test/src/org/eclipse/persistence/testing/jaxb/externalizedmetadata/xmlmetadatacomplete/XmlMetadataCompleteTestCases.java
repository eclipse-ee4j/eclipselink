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
 * dmccann - November 08/2010 - 2.2 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlmetadatacomplete;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Vector;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;

import junit.textui.TestRunner;

import org.eclipse.persistence.jaxb.JAXBContext;
import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCollectionReferenceMapping;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.ExternalizedMetadataTestCases;
import org.w3c.dom.Document;

/**
 * Tests ignoring annotations via xml-mapping-metadata-complete.
 *
 */
public class XmlMetadataCompleteTestCases extends ExternalizedMetadataTestCases {
    private static final String CONTEXT_PATH = "org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlmetadatacomplete";
    private static final String PATH = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlmetadatacomplete/";
    private static final String OXM_DOC = PATH + "employee-oxm.xml";
    private static final String OXM_OVERRIDES_DOC = PATH + "employee-overrides-oxm.xml";
    private static final String XSD_DOC = PATH + "employee.xsd";
    private static final String XSD_DOC_OVERRIDE_1 = PATH + "employee-override-1.xsd";
    private static final String XSD_DOC_OVERRIDE_2 = PATH + "employee-override-2.xsd";
    private static final String NSX_NAMESPACE = "http://www.example.com/xsds/real";
    private Class[] classes;

    /**
     * This is the preferred (and only) constructor.
     * 
     * @param name
     */
    public XmlMetadataCompleteTestCases(String name) {
        super(name);
    }
    
    /**
     * This method will be responsible for schema generation, which will create the 
     * JAXBContext we will use.  The eclipselink metadata file will be validated
     * as well.
     * 
     */
    public void setUp() throws Exception {
        super.setUp();
        classes = new Class[] { Employee.class };
    }

    /**
     * Test schema generation w/o oxm.xml overrides.
     * 
     * Positive test.
     */
    public void testNoOverrideSchemaGen() {
        MySchemaOutputResolver resolver = generateSchemaWithFileName(classes, CONTEXT_PATH, OXM_DOC, 1);
        // validate the schema
        compareSchemas(resolver.schemaFiles.get(EMPTY_NAMESPACE), new File(XSD_DOC));
    }

    /**
     * Test schema generation with oxm.xml overrides.
     * 
     * Positive test.
     */
    public void testOverrideSchemaGen() {
        MySchemaOutputResolver resolver = generateSchemaWithFileName(classes, CONTEXT_PATH, OXM_OVERRIDES_DOC, 2);
        // validate the schema2
        compareSchemas(resolver.schemaFiles.get(EMPTY_NAMESPACE), new File(XSD_DOC_OVERRIDE_1));
        compareSchemas(resolver.schemaFiles.get(NSX_NAMESPACE), new File(XSD_DOC_OVERRIDE_2));
    }
}
