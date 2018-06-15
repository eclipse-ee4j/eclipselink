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
//     Denise Smith - August 14/2009
package org.eclipse.persistence.testing.jaxb.jaxbelement.simple;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Map;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.eclipse.persistence.testing.jaxb.jaxbelement.JAXBElementTestCases;

public class JAXBElementLongTestCases extends JAXBElementTestCases {
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/jaxbelement/simple/long.xml";

    public JAXBElementLongTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setTargetClass(long.class);
    }

    public Class[] getClasses(){
        Class[] classes = new Class[1];
        classes[0] = Long.class;
        return classes;
    }

    protected Object getControlObject() {
        long longValue = 11;
        JAXBElement<Long> jbe = new JAXBElement<Long>(new QName("a", "b"),Long.class, longValue);
        return jbe;
    }

    public void testSchemaGen() throws Exception{
        super.testSchemaGen(new ArrayList<InputStream>());
    }

}
