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
// Denise Smith - 2.4
package org.eclipse.persistence.testing.jaxb.xmlpath;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class XmlPathToElementWithXmlAttributeTestCases extends JAXBWithJSONTestCases {
       private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlpath/xmlpathtoelementwithxmlattribute.xml";
       private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlpath/xmlpathtoelementwithxmlattribute.json";

        public XmlPathToElementWithXmlAttributeTestCases(String name) throws Exception {
            super(name);
            setControlDocument(XML_RESOURCE);
            setControlJSON(JSON_RESOURCE);
            setClasses(new Class[]{TestObjectToElementWithXmlAttribute.class});
        }

        @Override
        protected Object getControlObject() {
            TestObjectToElementWithXmlAttribute obj = new TestObjectToElementWithXmlAttribute();
            obj.theFlag = true;
              return obj;
        }

        public void testSchemaGen() throws Exception{
               List<InputStream> controlSchemas = new ArrayList<InputStream>();
               InputStream is = getClass().getClassLoader().getResourceAsStream("org/eclipse/persistence/testing/jaxb/xmlpath/xmlpathtoelementwithxmlattribute.xsd");
               controlSchemas.add(is);
               super.testSchemaGen(controlSchemas);
        }
}
