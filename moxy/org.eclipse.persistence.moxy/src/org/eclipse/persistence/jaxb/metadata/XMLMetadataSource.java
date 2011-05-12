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
 *     Blaise Doughan - 2.3 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.jaxb.metadata;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import java.util.Map;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stax.StAXSource;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.jaxb.JAXBContext;
import org.eclipse.persistence.jaxb.compiler.CompilerHelper;
import org.eclipse.persistence.jaxb.xmlmodel.XmlBindings;
import org.eclipse.persistence.logging.SessionLog;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

/**
 * A concrete implementation of MetadataSource that can represent the following
 * metadata input types;
 * <ul><li>java.io.File</li>
 * <li>java.io.InputStream</li>
 * <li>java.io.Reader</li>
 * <li>java.net.URL</li>
 * <li>javax.xml.stream.XMLEventReader</li>
 * <li>javax.xml.stream.XMLStreamReader</li>
 * <li>javax.xml.transform.Source</li>
 * <li>org.w3c.dom.Node</li>
 * <li>org.xml.sax.InputSource</li></ul>
 */
public class XMLMetadataSource extends MetadataSourceAdapter {

    private Source xmlBindingsSource;
    private URL xmlBindingsURL;

    /**
     * The XML bindings metadata will be loaded from a File.
     */
    public XMLMetadataSource(File xmlBindings) {
        xmlBindingsSource = new StreamSource(xmlBindings);
    }

    /**
     * The XML bindings metadata will be loaded from an InputStream.
     */
    public XMLMetadataSource(InputStream xmlBindings) {
        xmlBindingsSource = new StreamSource(xmlBindings);
    }

    /**
     * The XML bindings metadata will be loaded from Reader.
     */
    public XMLMetadataSource(Reader xmlBindings) {
        xmlBindingsSource = new StreamSource(xmlBindings);
    }

    /**
     * The XML bindings metadata will be loaded from a URL.
     */
    public XMLMetadataSource(URL xmlBindings) {
        xmlBindingsURL = xmlBindings;
    }

    /**
     * The XML bindings metadata will be loaded from an XMLEventReader.
     */
    public XMLMetadataSource(XMLEventReader xmlBindings) {
        try {
            xmlBindingsSource = new StAXSource(xmlBindings);
        } catch (XMLStreamException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * The XML bindings metadata will be loaded from an XMLStreamReader.
     */
    public XMLMetadataSource(XMLStreamReader xmlBindings) {
        xmlBindingsSource = new StAXSource(xmlBindings);
    }

    /**
     * The XML bindings metadata will be loaded from a Source.
     */
    public XMLMetadataSource(Source xmlBindings) {
        xmlBindingsSource = xmlBindings;
    }

    /**
     * The XML bindings metadata will be loaded from a Node.
     */    
    public XMLMetadataSource(Node xmlBindings) {
        xmlBindingsSource = new DOMSource(xmlBindings);
    }

    /**
     * The XML bindings metadata will be loaded from an InputSource.
     */
    public XMLMetadataSource(InputSource xmlBindings) {
        xmlBindingsSource = new SAXSource(xmlBindings);
    }

    @Override
    public XmlBindings getXmlBindings(Map<String, ?> properties, ClassLoader classLoader) {
        try {
            JAXBContext jaxbContext = CompilerHelper.getXmlBindingsModelContext();
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            if(null != xmlBindingsSource) {
                return (XmlBindings) unmarshaller.unmarshal(xmlBindingsSource);
            }
            if(null != xmlBindingsURL) {
                return (XmlBindings) unmarshaller.unmarshal(xmlBindingsURL);
            }
        } catch(JAXBException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

}
