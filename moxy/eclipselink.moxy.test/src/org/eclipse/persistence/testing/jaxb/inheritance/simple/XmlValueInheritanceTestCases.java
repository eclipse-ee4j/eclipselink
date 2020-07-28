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
//    Denise Smith - June 2012

package org.eclipse.persistence.testing.jaxb.inheritance.simple;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;


public class XmlValueInheritanceTestCases extends JAXBWithJSONTestCases{

    public XmlValueInheritanceTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[] { Simple.class, Complex.class});
        setControlDocument("org/eclipse/persistence/testing/jaxb/inheritance/simple/xmlvalue.xml");
        setControlJSON("org/eclipse/persistence/testing/jaxb/inheritance/simple/xmlvalue.json");
    }

    public Object getControlObject() {
        Complex complex = new Complex();
        complex.foo = "aaa";
        complex.bar = "bbb";
        return complex;
    }

    public void testSchemaGen() throws Exception{
        List<InputStream> controlSchemas = new ArrayList<InputStream>();
        InputStream instream1 = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/inheritance/simple/schema.xsd");
        controlSchemas.add(instream1);
        super.testSchemaGen(controlSchemas);
    }

}
