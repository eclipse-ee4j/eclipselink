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
// Denise Smith - 2.3
package org.eclipse.persistence.testing.jaxb.jaxbcontext.withjaxbindex;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

/*
 * For a context created by class[] with a jaxb.index in the package we should process all classes in the array
 * However we should not automatically process the ObjectFactory unless they are necessary
 * In this case since there is an XmlElementRef we SHOULD process the ObjectFactory class and include ClassB (which is only referenced in ObjectFactory)
 * We should also NOT process those listed in the jaxb.index (in this case ClassC)
 */
public class JAXBContextByClassArrayWithRefTestCases  extends JAXBWithJSONTestCases{
     protected final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/jaxbcontext/withjaxbindex/jaxbcontextbycontextpathwithref.xml";
     protected final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/jaxbcontext/withjaxbindex/jaxbcontextbycontextpathwithref.json";

        public JAXBContextByClassArrayWithRefTestCases(String name) throws Exception {
            super(name);
        }

        public void setUp() throws Exception {
            setControlDocument(XML_RESOURCE);
            setControlJSON(JSON_RESOURCE);
            super.setUp();
            Class[] classes = new Class[]{ClassAWithElementRef.class};
            setTypes(classes);
        }

        protected Object getControlObject() {
            ClassAWithElementRef classA = new ClassAWithElementRef();
            JAXBElement<String> jbe = new JAXBElement<String>(new QName("a") ,String.class ,"someValue");
            classA.setTheValue(jbe);

            return classA;
        }

        public void testSchemaGen() throws Exception{
            InputStream controlInputStream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/jaxbcontext/withjaxbindex/jaxbcontextbycontextpathwithref.xsd");
            List<InputStream> controlSchemas = new ArrayList<InputStream>();
            controlSchemas.add(controlInputStream);
            this.testSchemaGen(controlSchemas);
        }
}
