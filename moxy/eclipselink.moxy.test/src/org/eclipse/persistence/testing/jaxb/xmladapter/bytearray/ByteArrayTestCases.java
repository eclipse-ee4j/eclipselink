/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Blaise Doughan - 2.3.2 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.xmladapter.bytearray;

import org.eclipse.persistence.testing.jaxb.JAXBTestCases;

public class ByteArrayTestCases extends JAXBTestCases {

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmladapter/bytearray.xml";

    public ByteArrayTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        Class[] classes = new Class[1];
        classes[0] = Root.class;
        setClasses(classes);
    }

    @Override
    protected Object getControlObject() {
         Root root = new Root();
         BinaryData binaryData = new BinaryData();
         binaryData.setBytes("FOO".getBytes());
         root.setBinaryData(binaryData);
         return root;
     }

}