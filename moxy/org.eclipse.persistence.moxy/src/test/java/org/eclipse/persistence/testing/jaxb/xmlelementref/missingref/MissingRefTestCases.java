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
//     Denise Smith  January 26, 2010 - 2.0.1
package org.eclipse.persistence.testing.jaxb.xmlelementref.missingref;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.eclipse.persistence.testing.jaxb.JAXBTestCases;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class MissingRefTestCases extends JAXBWithJSONTestCases {
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlelementref/missingRef.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlelementref/missingRef.json";

    public MissingRefTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        Class[] classes = new Class[2];
        classes[0] = MissingRefObjectFactory.class;
        classes[1] = Person.class;
        setClasses(classes);
        initXsiType();
    }

    protected Object getControlObject() {
        Person p = new Person();
        p.setName("theName");
        QName qname = new QName("", "root");
        JAXBElement jaxbElement =new JAXBElement(qname, Object.class, p);
        return jaxbElement;
     }

    public void testSchemaGen() throws Exception{

        String xsdFileName = "org/eclipse/persistence/testing/jaxb/xmlelementref/missingRef.xsd";

        InputStream instream = ClassLoader.getSystemResourceAsStream(xsdFileName);

        List<InputStream> controlSchema = new ArrayList<InputStream>();
        controlSchema.add(instream);

        testSchemaGen(controlSchema);
    }
}
