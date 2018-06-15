/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Denise Smith - November 5, 2009
package org.eclipse.persistence.testing.jaxb.xmlelementref;

import java.math.BigDecimal;
import java.util.ArrayList;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class XMLElementRefConverterTestCases extends JAXBWithJSONTestCases{
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlelementref/converter.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlelementref/converter.json";

    public XMLElementRefConverterTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        Class[] classes = new Class[1];
        classes[0] = ComplexTypeObjectFactory.class;
        setClasses(classes);
    }

    protected Object getControlObject() {

        ComplexType ct = new ComplexType();
        ct.setGlobal(true);
        BigDecimal bd = new BigDecimal("1.1");
        ComplexType.TestLocal testLocal = new ComplexType.TestLocal(bd);
        ct.setLocal(testLocal);
        QName qname = new QName("clazz/typeDef", "root");
        JAXBElement jaxbElement = new JAXBElement<ComplexType>(qname, ComplexType.class, ct);

        return jaxbElement;
     }
}
