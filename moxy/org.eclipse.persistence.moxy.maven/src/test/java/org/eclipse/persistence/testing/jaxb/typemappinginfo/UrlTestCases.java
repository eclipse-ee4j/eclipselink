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
//     rbarkhouse - 2.3.3 - initial implementation
package org.eclipse.persistence.testing.jaxb.typemappinginfo;

import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.eclipse.persistence.jaxb.TypeMappingInfo;
import org.eclipse.persistence.jaxb.TypeMappingInfo.ElementScope;

public class UrlTestCases extends TypeMappingInfoWithJSONTestCases {

    private String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/typemappinginfo/url.xml";
    private String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/typemappinginfo/url.json";
    private String XSD_RESOURCE = "org/eclipse/persistence/testing/jaxb/typemappinginfo/url.xsd";

    private URL CONTROL_URL;

     public UrlTestCases(String name) throws Exception {
         super(name);
         init();
     }

     public void init() throws Exception {
         setControlDocument(XML_RESOURCE);
         setControlJSON(JSON_RESOURCE);
         setTypeMappingInfos(getTypeMappingInfos());
         CONTROL_URL = new URL("http://www.amazon.com");
     }

     protected TypeMappingInfo[] getTypeMappingInfos() throws Exception {
         if (typeMappingInfos == null) {
             typeMappingInfos = new TypeMappingInfo[1];
             TypeMappingInfo tpi = new TypeMappingInfo();
             tpi.setXmlTagName(new QName("", "testTagname"));
             tpi.setElementScope(ElementScope.Global);
             tpi.setNillable(true);
             tpi.setType(URL.class);
             typeMappingInfos[0] = tpi;
         }
         return typeMappingInfos;
     }

     protected Object getControlObject() {
         QName qname = new QName("", "testTagname");
         JAXBElement jaxbElement = new JAXBElement(qname, URL.class, CONTROL_URL);
         return jaxbElement;
     }

     public Map<String, InputStream> getControlSchemaFiles(){
         InputStream instream = ClassLoader.getSystemResourceAsStream(XSD_RESOURCE);
         Map<String, InputStream> controlSchema = new HashMap<String, InputStream>();
         controlSchema.put("", instream);
         return controlSchema;
     }

}
