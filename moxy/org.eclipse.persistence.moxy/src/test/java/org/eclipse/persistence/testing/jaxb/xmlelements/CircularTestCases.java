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
//     Denise Smith - June 14, 2013
package org.eclipse.persistence.testing.jaxb.xmlelements;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class CircularTestCases extends JAXBWithJSONTestCases{
     private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlelements/root.xml";
     private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlelements/root.json";
     private final static String XSD_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlelements/root.xsd";
     private final static String JSON_SCHEMA_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlelements/root_schema.json";

     public CircularTestCases(String name) throws Exception {
            super(name);
            setControlDocument(XML_RESOURCE);
            setControlJSON(JSON_RESOURCE);
            Class[] classes = new Class[1];
            classes[0] = Root.class;
            setClasses(classes);
        }

     protected Object getControlObject() {
         Root theRoot = new Root();
         theRoot.name = "aName";

         Root theRoot2 = new Root();
         theRoot2.name = "childName";
         theRoot.children = new ArrayList<Object>();
         theRoot.children.add(theRoot2);
         theRoot.children.add("someString");
         return theRoot;
     }

     public void testSchemaGeneration() throws Exception{
         List<InputStream> controlSchemas = new ArrayList<InputStream>();
         controlSchemas.add(getClass().getClassLoader().getResourceAsStream(XSD_RESOURCE));
         super.testSchemaGen(controlSchemas);
     }


     public void testJSONSchemaGen() throws Exception{
         InputStream controlSchema = classLoader.getResourceAsStream(JSON_SCHEMA_RESOURCE);
         super.generateJSONSchema(controlSchema);

     }

}
