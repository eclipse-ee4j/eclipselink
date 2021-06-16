/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Denise Smith, February 2013
package org.eclipse.persistence.testing.jaxb.xmlinverseref;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.JAXBContext;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class XmlInverseRefBidirectional2TestCases extends JAXBWithJSONTestCases{
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlinverseref/bidirectional2.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlinverseref/bidirectional2.json";
    private final static String XSD_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlinverseref/bidirectional.xsd";

    public XmlInverseRefBidirectional2TestCases(String name) throws Exception {
        super(name);
        setControlJSON(JSON_RESOURCE);
        setControlDocument(XML_RESOURCE);
        setClasses(new Class[]{Person.class});
    }

    @Override
    protected Object getControlObject() {
        Person p = new Person();
        p.name = "theName";
        Address addr = new Address();
        addr.street = "theStreet";
        addr.owner = p;
        p.addr = addr;
        return addr;
    }

    public void testSchemaGen() throws Exception{
        List<InputStream> controlSchemas = new ArrayList<InputStream>();
        controlSchemas.add(getClass().getClassLoader().getResourceAsStream(XSD_RESOURCE));
        super.testSchemaGen(controlSchemas);
    }


}
