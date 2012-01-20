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
 * Matt MacIvor - 2.3
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.listofobjects;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import javax.activation.DataHandler;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;

import org.eclipse.persistence.jaxb.JAXBUnmarshaller;
import org.eclipse.persistence.testing.jaxb.JAXBTestCases;
import org.w3c.dom.Node;

public class JAXBByteArrayWithDataHandlerTestCases extends JAXBListOfObjectsNoJSONTestCases {

    public static String XML_RESOURCE="org/eclipse/persistence/testing/jaxb/listofobjects/bytearray.xml";
    public JAXBByteArrayWithDataHandlerTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
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
    public void testXMLToObjectFromInputStream() throws Exception { 
    }    
    public void testXMLToObjectFromURL() throws Exception { 
    }
    public void testXMLToObjectFromXMLStreamReaderEx() throws Exception { 
    }  
    public void testUnmarshallerHandler() throws Exception { 
    }      
}
