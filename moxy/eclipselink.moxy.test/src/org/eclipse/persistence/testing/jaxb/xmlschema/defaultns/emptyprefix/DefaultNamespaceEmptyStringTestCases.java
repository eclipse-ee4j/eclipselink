/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     rbarkhouse - 2.3.3 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.xmlschema.defaultns.emptyprefix;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.testing.jaxb.JAXBTestCases;

public class DefaultNamespaceEmptyStringTestCases extends JAXBTestCases {
    protected final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlschema/defaultns/emptyprefix/instance.xml";

    public DefaultNamespaceEmptyStringTestCases(String name) throws Exception {
        super(name);
    }

    public void setUp() throws Exception {
        setControlDocument(XML_RESOURCE);

        super.setUp();
        Type[] types = new Type[1];
        types[0] = Person.class;
        setTypes(types);
    }

    protected Object getControlObject() {
        Person p = new Person();
        p.name = "Bob Smith";
        p.title = "President";
        return p;
    }

    public void xtestSchemaGen() throws Exception{
        InputStream controlInputStream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/xmlschema/attributeformdefault/unset.xsd");
        List<InputStream> controlSchemas = new ArrayList<InputStream>();
        controlSchemas.add(controlInputStream);
        this.testSchemaGen(controlSchemas);
    }

}
