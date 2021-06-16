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
// Matt MacIvor - 2.3
package org.eclipse.persistence.testing.jaxb.listofobjects;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import jakarta.activation.DataHandler;
import jakarta.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

public class JAXBByteArrayWithDataHandlerTestCases extends JAXBListOfObjectsTestCases {

    public static String XML_RESOURCE="org/eclipse/persistence/testing/jaxb/listofobjects/bytearray.xml";
    public static String JSON_RESOURCE="org/eclipse/persistence/testing/jaxb/listofobjects/bytearray.json";

    public JAXBByteArrayWithDataHandlerTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        setClasses(new Class[]{byte[].class, DataHandler.class});
    }

    @Override
    protected Object getControlObject() {
        JAXBElement<byte[]> elem = new JAXBElement<byte[]>(new QName("root"), byte[].class, new byte[]{1, 2, 3, 4, 5, 6, 7});
        return elem;
    }

    @Override
    public List<InputStream> getControlSchemaFiles() {
        // TODO Auto-generated method stub
        return new ArrayList<InputStream>();
    }

   @Override
    protected Type getTypeToUnmarshalTo() throws Exception {
        return byte[].class;
    }

    @Override
    public Class getUnmarshalClass(){
        return byte[].class;
    }

    @Override
    protected String getNoXsiTypeControlResourceName() {
        return XML_RESOURCE;
    }

    public void testXMLToObjectFromNode() throws Exception {
    }
    public void testXMLToObjectFromXMLStreamReader() throws Exception {
    }
    public void testXMLToObjectFromXMLEventReader() throws Exception {
    }
    public void testXMLToObjectFromStreamSource() throws Exception {
    }
    public void testUnmarshallerHandler() throws Exception {
    }
}
