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
//     desmith - July 2012
package org.eclipse.persistence.testing.jaxb.xmlschematype;

import java.math.BigInteger;
import java.util.Calendar;

import jakarta.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class NonNegativeIntegerSchemaTypeTestCases extends JAXBWithJSONTestCases{

     private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlschematype/nonNegativeInteger.xml";
        private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlschematype/nonNegativeInteger.json";

        public NonNegativeIntegerSchemaTypeTestCases(String name) throws Exception {
            super(name);
            setControlDocument(XML_RESOURCE);
            setControlJSON(JSON_RESOURCE);
            Class[] classes = new Class[1];
            classes[0] = Root.class;
            setClasses(classes);
        }

        protected Object getControlObject() {
            Root root  = new Root();
            root.count = new BigInteger("100");
            return root;
        }

}
