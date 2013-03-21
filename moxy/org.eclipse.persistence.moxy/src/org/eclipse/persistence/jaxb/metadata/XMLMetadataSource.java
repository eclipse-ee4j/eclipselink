/*******************************************************************************
 * Copyright (c) 2011, 2013 Oracle and/or its affiliates. All rights reserved.
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
import java.net.MalformedURLException;
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
    private String xmlBindingsLocation;

    /**
     * The XML bindings metadata will be loaded from a File.
     */
    public XMLMetadataSource(File xmlBindings) {
        if(xmlBindings == null) {
            throw new IllegalArgumentException();
        }
        xmlBindingsSource = new StreamSource(xmlBindings);
    }

    /**
     * The XML bindings metadata will be loaded from an InputStream.
     */
    public XMLMetadataSource(InputStream xmlBindings) {
        if(xmlBindings == null) {
            throw new IllegalArgumentException();
        }
        xmlBindingsSource = new StreamSource(xmlBindings);
    }

    /**
     * The XML bindings metadata will be loaded from Reader.
     */
    public XMLMetadataSource(Reader xmlBindings) {
        if(xmlBindings == null) {
            throw new IllegalArgumentException();
        }
        xmlBindingsSource = new StreamSource(xmlBindings);
    }

    /**
     * The XML bindings metadata will be loaded from a URL.
     */
    public XMLMetadataSource(URL xmlBindings) {
        if(xmlBindings == null) {
            throw new IllegalArgumentException();
        }
        xmlBindingsURL = xmlBindings;
    }

    /**
     * The XML bindings metadata will be loaded from an XMLEventReader.
     */
    public XMLMetadataSource(XMLEventReader xmlBindings) {
        if(xmlBindings == null) {
            throw new IllegalArgumentException();
        }
        try {
            xmlBindingsSource = new StAXSource(xmlBindings);
        } catch (XMLStreamException e) {
            org.eclipse.persistence.exceptions.JAXBException.couldNotUnmarshalMetadata(e);
        }
    }

    /**
     * The XML bindings metadata will be loaded from an XMLStreamReader.
     */
    public XMLMetadataSource(XMLStreamReader xmlBindings) {
        if(xmlBindings == null) {
            throw new IllegalArgumentException();
        }
        xmlBindingsSource = new StAXSource(xmlBindings);
    }

    /**
     * The XML bindings metadata will be loaded from a Source.
     */
    public XMLMetadataSource(Source xmlBindings) {
        if(xmlBindings == null) {
            throw new IllegalArgumentException();
        }
        xmlBindingsSource = xmlBindings;
    }

    /**
     * The XML bindings metadata will be loaded from a Node.
     */    
    public XMLMetadataSource(Node xmlBindings) {
        if(xmlBindings == null) {
            throw new IllegalArgumentException();
        }
        xmlBindingsSource = new DOMSource(xmlBindings);
    }

    /**
     * The XML bindings metadata will be loaded from an InputSource.
     */
    public XMLMetadataSource(InputSource xmlBindings) {
        if(xmlBindings == null) {
            throw new IllegalArgumentException();
        }
        xmlBindingsSource = new SAXSource(xmlBindings);
    }
    
    /**
     * XML bindings metatdata will be loaded either as a URL or as a classpath reference
     */
    public XMLMetadataSource(String xmlBindings) {
        if(xmlBindings == null) {
            throw new IllegalArgumentException();
        }
        //If this is a valid URL, store it as URL. Otherwise store as a String and
        //try to load from the classpath later.
        try {
            this.xmlBindingsURL = new URL(xmlBindings);
        } catch (MalformedURLException ex) {
            xmlBindingsLocation = xmlBindings;
        }
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
            if(null != xmlBindingsLocation) {
                URL url = classLoader.getResource(xmlBindingsLocation);
                if(url == null) {
                    // throw exception
                    throw org.eclipse.persistence.exceptions.JAXBException.unableToLoadMetadataFromLocation(xmlBindingsLocation);  
                }
                return (XmlBindings) unmarshaller.unmarshal(url);
            }
        } catch(JAXBException e) {
            throw org.eclipse.persistence.exceptions.JAXBException.couldNotUnmarshalMetadata(e);
        }
        return null;
    }

}