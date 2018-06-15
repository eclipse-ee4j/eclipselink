/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
// dmccann - November 08/2010 - 2.2 - Initial implementation
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlmetadatacomplete;

import java.io.File;
import java.net.URISyntaxException;

import org.eclipse.persistence.testing.jaxb.externalizedmetadata.ExternalizedMetadataTestCases;

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
    public void testNoOverrideSchemaGen() throws URISyntaxException {
        MySchemaOutputResolver resolver = generateSchemaWithFileName(classes, CONTEXT_PATH, OXM_DOC, 1);
        // validate the schema
        compareSchemas(resolver.schemaFiles.get(EMPTY_NAMESPACE),
                new File(Thread.currentThread().getContextClassLoader().getResource(XSD_DOC).toURI()));
    }

    /**
     * Test schema generation with oxm.xml overrides.
     *
     * Positive test.
     */
    public void testOverrideSchemaGen() throws URISyntaxException {
        MyStreamSchemaOutputResolver resolver = new MyStreamSchemaOutputResolver();
        generateSchemaWithFileName(classes, CONTEXT_PATH, OXM_OVERRIDES_DOC, 2, resolver);
        // validate the schema2
        compareSchemas(resolver.schemaFiles.get(EMPTY_NAMESPACE).toString(),
                new File(Thread.currentThread().getContextClassLoader().getResource(XSD_DOC_OVERRIDE_1).toURI()));
        compareSchemas(resolver.schemaFiles.get(NSX_NAMESPACE).toString(),
                new File(Thread.currentThread().getContextClassLoader().getResource(XSD_DOC_OVERRIDE_2).toURI()));
    }
}
