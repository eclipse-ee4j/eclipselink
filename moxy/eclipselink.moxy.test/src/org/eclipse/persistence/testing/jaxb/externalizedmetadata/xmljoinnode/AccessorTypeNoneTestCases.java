/*
 * Copyright (c) 2014, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//  - rbarkhouse - 11 October 2012 - 2.4 - Initial implementation
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmljoinnode;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.Marshaller;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.jaxb.JAXBContextProperties;
import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.eclipse.persistence.oxm.MediaType;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class AccessorTypeNoneTestCases extends JAXBWithJSONTestCases {

    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmljoinnode/company-atn.xml";
    private static final String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmljoinnode/company-atn.json";
    private static final String BINDINGS_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmljoinnode/accessor-type-none-oxm.xml";

    public AccessorTypeNoneTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        setClasses(new Class[] { Company.class });
    }

    public Object getControlObject() {
        AtnCompany company = new AtnCompany();

        AtnEmployee e1 = new AtnEmployee(); e1.id = 0; e1.name = "Octimus";
        AtnEmployee e2 = new AtnEmployee(); e2.id = 1; e2.name = "Lauris";
        AtnEmployee e3 = new AtnEmployee(); e3.id = 2; e3.name = "Fco";

        e1.manager = e3; e2.manager = e3;

        AtnEmployee r2 = new AtnEmployee(); r2.id = 242; r2.name = "Report1";
        AtnEmployee r1 = new AtnEmployee(); r1.id = 243; r1.name = "Report2";

        e1.reports = new ArrayList<AtnEmployee>();
        e1.reports.add(r1);
        e1.reports.add(r1);
        e1.reports.add(r2);
        e1.reports.add(r2);


        company.employees.add(e1); company.employees.add(e2); company.employees.add(e3);
        company.employees.add(r1);
        company.employees.add(r2);

        return company;
    }

    public Map getProperties() {
        InputStream inputStream = ClassLoader.getSystemResourceAsStream(BINDINGS_RESOURCE);
        HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
        metadataSourceMap.put("org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmljoinnode", new StreamSource(inputStream));
        Map<String, Map<String, Source>> properties = new HashMap<String, Map<String, Source>>();
        properties.put(JAXBContextProperties.OXM_METADATA_SOURCE, metadataSourceMap);

        return properties;
    }

}
