/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//  - rbarkhouse - 21 March 2013 - 2.4.2 - Initial implementation
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlelementwrapper;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.Marshaller;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.jaxb.JAXBContextProperties;
import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.oxm.MediaType;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class XmlElementWrapperDefaultNameTestCases extends JAXBWithJSONTestCases {

    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlelementwrapper/employee-wrapper-default.xml";
    private static final String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlelementwrapper/employee-wrapper-default.json";

    private static final String OXM_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlelementwrapper/wrapper-default-oxm.xml";

    private static final String PACKAGE_NAME = "org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlelementwrapper";

    public XmlElementWrapperDefaultNameTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[] { Company.class, Employee.class });
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        initXsiType();
    }

    public Map getProperties() {
        InputStream inputStream = ClassLoader.getSystemResourceAsStream(OXM_RESOURCE);

        HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
        metadataSourceMap.put(PACKAGE_NAME, new StreamSource(inputStream));

        Map<String, Map<String, Source>> properties = new HashMap<String, Map<String, Source>>();
        properties.put(JAXBContextProperties.OXM_METADATA_SOURCE, metadataSourceMap);

        return properties;
    }

    @Override
    protected Object getControlObject() {
        Company c = new Company();
        c.name = "ACME Inc.";

        Employee emp1 = new Employee();
        emp1.thing = "Elmer";

        Employee emp2 = new Employee();
        emp2.thing = "Sylvester";

        c.employees.add(emp1);
        c.employees.add(emp2);

        return c;
    }

}
