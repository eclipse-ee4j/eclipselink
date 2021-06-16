/*
 * Copyright (c) 2012, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Blaise Doughan - 2.3.3 - initial implementation
package org.eclipse.persistence.testing.jaxb.annotations.xmlpath.attributecollection;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.testing.jaxb.JAXBTestCases;

public class XmlPathCollectionRefAttributeTestCases extends JAXBTestCases {

    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/annotations/xmlpath/CollectionRefAttribute.xml";
    private static final String SCHEMA_RESOURCE = "org/eclipse/persistence/testing/jaxb/annotations/xmlpath/CollectionRefAttribute.xsd";

    public XmlPathCollectionRefAttributeTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[] {XmlPathCollectionRefAttributeRoot.class});
        setControlDocument(XML_RESOURCE);
    }

    @Override
    protected Object getControlObject() {
        List<XmlPathCollectionRefAttributeChild> list = new ArrayList<XmlPathCollectionRefAttributeChild>(2);
        XmlPathCollectionRefAttributeChild child1 = new XmlPathCollectionRefAttributeChild();
        child1.setId("A");
        list.add(child1);
        XmlPathCollectionRefAttributeChild child2 = new XmlPathCollectionRefAttributeChild();
        child2.setId("B");
        list.add(child2);

        XmlPathCollectionRefAttributeRoot root = new XmlPathCollectionRefAttributeRoot();
        root.setAttribute(list);
        root.setElement(list);
        root.setXpathAttribute(list);
        root.setXpathAttributeList(list);

        return root;
    }

    public void testSchemaGen() throws Exception{
        List<InputStream> controlSchemas = new ArrayList<InputStream>(1);
        InputStream is = getClass().getClassLoader().getResourceAsStream(SCHEMA_RESOURCE);
        controlSchemas.add(is);
        super.testSchemaGen(controlSchemas);
    }

}
