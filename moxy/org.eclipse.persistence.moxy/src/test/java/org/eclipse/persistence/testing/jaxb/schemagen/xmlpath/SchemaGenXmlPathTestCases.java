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
//     Matt MacIvor - October 2011 - 2.4 - Initial implementation
package org.eclipse.persistence.testing.jaxb.schemagen.xmlpath;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.testing.jaxb.JAXBTestCases;

public class SchemaGenXmlPathTestCases extends JAXBTestCases {
    protected final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/schemagen/xmlpath/xmlpath.xml";

    public SchemaGenXmlPathTestCases(String name) throws Exception {
        super(name);
        init();
    }

    public void init() throws Exception {
        setControlDocument(XML_RESOURCE);
        setClasses(new Class[] { MyClass.class, Phone.class, Address.class, CanadianAddress.class });
    }

    public  List<InputStream> getControlSchemaFiles(){
        InputStream instream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/schemagen/xmlpath/xmlpath.xsd");
        List<InputStream> controlSchema = new ArrayList<InputStream>();
        controlSchema.add(instream);
        return controlSchema;
    }

    public void testSchemaGen() throws Exception {
        testSchemaGen(getControlSchemaFiles());
    }

    protected Object getControlObject() {
        MyClass obj = new MyClass();
        obj.name = "Bob Jones";
        obj.email = "bob@myemail.com";
        obj.confirmed = true;
        obj.homePhone = new Phone();
        obj.homePhone.number = "123-4567";
        obj.workPhone = new Phone();
        obj.workPhone.number = "345-6789";
        CanadianAddress a = new CanadianAddress();
        a.street = "123 Main St.";
        a.postalCode = "A1B 2C3";
        a.province = "Nunavut";
        obj.address = a;
        return obj;
    }

}
