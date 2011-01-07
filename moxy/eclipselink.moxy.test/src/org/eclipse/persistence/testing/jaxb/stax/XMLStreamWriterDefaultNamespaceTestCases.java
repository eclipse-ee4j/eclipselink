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
 * Oracle = 2.2 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.stax;

import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;

import org.eclipse.persistence.jaxb.JAXBContextFactory;

import junit.framework.TestCase;

public class XMLStreamWriterDefaultNamespaceTestCases extends TestCase {
    
    public void testDefaultNamespaceOverride() throws Exception {
        if(System.getProperty("java.version").contains("1.6")) {
        
            JAXBContext ctx = JAXBContextFactory.createContext(new Class[]{Employee.class}, null);
            StringWriter writer = new StringWriter();
            XMLStreamWriter streamWriter = XMLOutputFactory.newInstance().createXMLStreamWriter(writer);
            streamWriter.writeStartElement("", "root", "someNamespace");
            streamWriter.writeDefaultNamespace("someNamespace");
            Marshaller marshaller = ctx.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FRAGMENT, new Boolean(true));
        
            marshaller.marshal(new JAXBElement(new QName("employee"), Employee.class, new Employee()), streamWriter);
            streamWriter.writeEndElement();
            streamWriter.writeEndDocument();
            streamWriter.flush();
        
            String xml = "<root xmlns=\"someNamespace\"><employee xmlns=\"\"></employee></root>";
            assertTrue("Incorrect XML: " + writer.toString(), writer.toString().equals(xml));
        }
        
    }

}
