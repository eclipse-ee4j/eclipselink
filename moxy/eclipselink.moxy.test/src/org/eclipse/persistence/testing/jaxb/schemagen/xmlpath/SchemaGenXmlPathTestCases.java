/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Matt MacIvor - October 2011 - 2.4 - Initial implementation
 ******************************************************************************/
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
        Class[] classes = new Class[2];
        classes[0] = MyClass.class;
        classes[1] = Phone.class;
        setClasses(classes);
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
        return obj;
    }
}
