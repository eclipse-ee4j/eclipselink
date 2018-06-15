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
// dmccann - June 17/2009 - 2.0 - Initial implementation
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlaccessortype.packagelevel.nooverride;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;


/**
 * Tests the @XmlAccessorType set in package-info.java.  No overrides will
 * be performed.
 *
 * Positive test.
 */
public class XmlAccessorTypePackageNoOverrideTestCases extends JAXBWithJSONTestCases {
    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlaccessortype/employee-none.xml";
    private static final String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlaccessortype/employee-none.json";

    /**
     * This is the preferred (and only) constructor.
     *
     * @param name
     */
    public XmlAccessorTypePackageNoOverrideTestCases(String name) throws Exception{
        super(name);
        setClasses(new Class[]{Employee.class});
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
    }

    protected Object getControlObject() {
        Employee emp = new Employee(0);
        return emp;
    }

    public Object getWriteControlObject() {
        Employee emp = new Employee(666);
        emp.firstName = "firstName";
        emp.lastName = "lastName";
        return emp;
    }

    public void testInstanceDocValidation() throws Exception {
        InputStream schema = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlaccessortype/employee.xsd");
        StreamSource schemaSource = new StreamSource(schema);

        MyMapStreamSchemaOutputResolver outputResolver = new MyMapStreamSchemaOutputResolver();
        getJAXBContext().generateSchema(outputResolver);

        InputStream instanceDocStream = ClassLoader.getSystemResourceAsStream(XML_RESOURCE);
        String result = validateAgainstSchema(instanceDocStream, schemaSource, outputResolver );
        assertTrue("Instance doc validation (employee.xml) failed unxepectedly: " + result, result == null);
    }

    public void testSchemaGen() throws Exception {
        List controlSchemas = new ArrayList();
        InputStream is = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlaccessortype/employee.xsd");
        controlSchemas.add(is);
        super.testSchemaGen(controlSchemas);
    }

    /**
     * Tests the @XmlAccessorType set in package-info.java.  No overrides will
     * be performed.
     *
     * Negative test.
     */
    public void testNegativeInstanceDocValidation() throws Exception {
        InputStream schema = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlaccessortype/employee.xsd");
        StreamSource schemaSource = new StreamSource(schema);

        MyMapStreamSchemaOutputResolver outputResolver = new MyMapStreamSchemaOutputResolver();
        getJAXBContext().generateSchema(outputResolver);

        InputStream instanceDocStream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlaccessortype/employee-property.xml");
        String result = validateAgainstSchema(instanceDocStream, schemaSource, outputResolver );
        assertTrue("Instance doc validation (employee.xml) failed unxepectedly: " + result, result != null);
    }

}
