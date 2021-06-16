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
//     Praba Vijayaratnam - 2.3 - initial implementation
package org.eclipse.persistence.testing.jaxb.javadoc.xmlattachmentref;

import jakarta.activation.DataHandler;
import jakarta.activation.FileDataSource;

import org.eclipse.persistence.testing.jaxb.JAXBTestCases;

public class XmlAttachmentRefExampleTest extends JAXBTestCases{

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/javadoc/xmlattachmentref/xmlattachmentref.xml";

    public XmlAttachmentRefExampleTest(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        Class[] classes = new Class[1];
        classes[0] = RootObject.class;
        setClasses(classes);
    }

    protected Object getControlObject() {

        RootObject example = new RootObject();

        DataHandler data = new DataHandler("THISISATEXTSTRINGFORTHISDATAHANDLER", "data");
        DataHandler body = new DataHandler("THISISATEXTSTRINGFORTHISBODY", "body");
        example.data = data;
        example.body = body;
        return example;
    }


}
