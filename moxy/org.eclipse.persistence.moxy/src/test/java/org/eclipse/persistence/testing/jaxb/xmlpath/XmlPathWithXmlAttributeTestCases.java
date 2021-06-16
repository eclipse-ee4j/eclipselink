/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
// Denise Smith - 2.4
package org.eclipse.persistence.testing.jaxb.xmlpath;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class XmlPathWithXmlAttributeTestCases extends JAXBWithJSONTestCases{

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlpath/xmlpathandxmlattribute.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlpath/xmlpathandxmlattribute.json";

    public XmlPathWithXmlAttributeTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        setClasses(new Class[]{TestObjectToAttributeWithXmlAttribute.class});
    }

    @Override
    protected Object getControlObject() {
        TestObjectToAttributeWithXmlAttribute obj = new TestObjectToAttributeWithXmlAttribute();
        obj.theFlag = true;
          return obj;
    }

    public void testSchemaGen() throws Exception{
           List<InputStream> controlSchemas = new ArrayList<InputStream>();
           InputStream is = getClass().getClassLoader().getResourceAsStream("org/eclipse/persistence/testing/jaxb/xmlpath/xmlpathandxmlattribute.xsd");
           controlSchemas.add(is);
           super.testSchemaGen(controlSchemas);
    }
}
