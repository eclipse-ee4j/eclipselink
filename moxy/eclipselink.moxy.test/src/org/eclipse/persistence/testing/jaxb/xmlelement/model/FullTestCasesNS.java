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

package org.eclipse.persistence.testing.jaxb.xmlelement.model;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.dom.DOMSource;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.junit.Before;
import org.junit.BeforeClass;
import org.w3c.dom.Document;

public class FullTestCasesNS extends FullTestCases{
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlelement/model/fullNS.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlelement/model/fullNS.json";

    public FullTestCasesNS(String name) throws Exception {
         super(name);
         setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
    }

    public Map getProperties(){
          Map overrides = new HashMap();
            String overridesString =
            "<?xml version='1.0' encoding='UTF-8'?>" +
            "<xml-bindings xmlns='http://www.eclipse.org/eclipselink/xsds/persistence/oxm'>" +
            "<xml-schema namespace='myuri'/>"+
            "<java-types>" +
            "<java-type name='org.eclipse.persistence.testing.jaxb.xmlelement.model.Order'>" +
                "<java-attributes>" +
                   "<xml-element java-attribute='customer' namespace='testuri2'/>" +
                "</java-attributes>" +
                "</java-type>" +
            "</java-types>" +

            "</xml-bindings>";

            DOMSource src = null;
            try {
                Document doc = parser.parse(new ByteArrayInputStream(overridesString.getBytes()));
                src = new DOMSource(doc.getDocumentElement());
            } catch (Exception e) {
                e.printStackTrace();
                fail("An error occurred during setup");
            }

            overrides.put("org.eclipse.persistence.testing.jaxb.xmlelement.model", src);

            Map props = new HashMap();
            props.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, overrides);
        return props;
    }
}
