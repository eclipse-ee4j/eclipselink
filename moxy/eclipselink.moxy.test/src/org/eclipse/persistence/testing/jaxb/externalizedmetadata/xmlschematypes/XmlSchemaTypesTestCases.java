/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
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
// dmccann - January 06/2010 - 2.0 - Initial implementation
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlschematypes;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlschematypes.Employee;

/**
 * Tests XmlSchemaTypes via eclipselink-oxm.xml
 *
 */
public class XmlSchemaTypesTestCases extends JAXBWithJSONTestCases {
    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlschematypes/employee.xml";
    private static final String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlschematypes/employee.json";

    private TimeZone currentTimeZone;

    /**
     * This is the preferred (and only) constructor.
     *
     * @param name
     */
    public XmlSchemaTypesTestCases(String name) throws Exception{
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        setClasses(new Class[]{Employee.class});
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        // This test's instance document contains a date in EST time zone, so temporarily
        // reset the VM time zone
        currentTimeZone = TimeZone.getDefault();
        TimeZone.setDefault(TimeZone.getTimeZone("America/New_York"));
    }

    @Override
    public void tearDown() {
        super.tearDown();
        TimeZone.setDefault(currentTimeZone);
    }

    protected Object getControlObject() {
        Employee emp = new Employee();

        //setup control Employee
        GregorianCalendar calendar = new GregorianCalendar();
        Date theDate = new Date(new Long("1262840400000"));
        calendar.setTime(theDate);

        emp.hireDate = calendar;
        emp.lengthOfEmployment = new BigDecimal("1.000010");

        return emp;
    }

    public Map getProperties(){
        InputStream inputStream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlschematypes/eclipselink-oxm.xml");

        HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
        metadataSourceMap.put("org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlschematypes", new StreamSource(inputStream));
        Map<String, Map<String, Source>> properties = new HashMap<String, Map<String, Source>>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataSourceMap);

        return properties;
    }


    public void testSchemaGen() throws Exception{
        List controlSchemas = new ArrayList();
        InputStream is = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlschematypes/schema.xsd");

        controlSchemas.add(is);

        super.testSchemaGen(controlSchemas);

    }

}
