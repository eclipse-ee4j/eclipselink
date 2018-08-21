/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
// Denise Smith - 2.4 - April 2012
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlanyelement;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class XmlAnyElementAdapterListTestCases extends JAXBWithJSONTestCases{
    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlanyelement/employee-with-list-adapter.xml";
    private static final String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlanyelement/employee-with-list-adapter.json";

    public XmlAnyElementAdapterListTestCases(String name) throws Exception{
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        setClasses(new Class[]{EmployeeWithList.class});

    }

      public Map getProperties(){
            InputStream inputStream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlanyelement/eclipselink-oxm-xml-adapter-list.xml");

            HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
            metadataSourceMap.put("org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlanyelement", new StreamSource(inputStream));
            Map<String, Map<String, Source>> properties = new HashMap<String, Map<String, Source>>();
            properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataSourceMap);

            return properties;
        }

      protected Object getControlObject() {
          EmployeeWithList ctrlEmpWithList = new EmployeeWithList();
          ctrlEmpWithList.a = 1;
          ctrlEmpWithList.b = "3";
          ctrlEmpWithList.stuff = new ArrayList<Object>();
          Dom stuff = new Dom();
          stuff.stuffStr = "This is some stuff";
          ctrlEmpWithList.stuff.add(stuff);
          stuff = new Dom();
          stuff.stuffStr = "This is some more stuff";
          ctrlEmpWithList.stuff.add(stuff);
          return ctrlEmpWithList;
      }

}
