/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.platform.xml.jaxp;

import java.io.OutputStream;
import java.io.Writer;
import java.net.URL;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.eclipse.persistence.platform.xml.XMLPlatformException;
import org.eclipse.persistence.platform.xml.XMLTransformer;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;

/**
 * <p><b>Purpose</b>:  An implementation of XMLTransformer using JAXP 1.3 APIs.</p>
 */

public class JAXPTransformer implements XMLTransformer {
    private boolean fragment;
    private static final String NO = "no";
    private static final String YES = "yes";
    private Transformer transformer;
    private String encoding;
    private boolean formatted;
    private String version;

    public String getEncoding() {
    	return encoding;
    }

    public void setEncoding(String encoding) {
    	this.encoding = encoding;
    	if(transformer != null){
            transformer.setOutputProperty(OutputKeys.ENCODING, encoding);
    	}
    }

    public boolean isFormattedOutput() {
    	return formatted;
    }

    public void setFormattedOutput(boolean shouldFormat) {
    	this.formatted = shouldFormat;
    	if(transformer != null){
            if (shouldFormat) {
                transformer.setOutputProperty(OutputKeys.INDENT, YES);
            } else {
                transformer.setOutputProperty(OutputKeys.INDENT, NO);
            }
    	}
    }

    public String getVersion() {    
    	return version;
    }

    public void setVersion(String version) {
    	this.version = version;
    	if(transformer != null){
            transformer.setOutputProperty(OutputKeys.VERSION, version);
    	}
    }

    public void transform(Node sourceNode, OutputStream resultOutputStream) throws XMLPlatformException {
        DOMSource source = new DOMSource(sourceNode);
        StreamResult result = new StreamResult(resultOutputStream);
        if (isFragment()) {
            getTransformer().setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        }
        transform(source, result);
    }

    public void transform(Node sourceNode, ContentHandler resultContentHandler) throws XMLPlatformException {
        DOMSource source = new DOMSource(sourceNode);
        SAXResult result = new SAXResult(resultContentHandler);

        transform(source, result);
    }

    public void transform(Node sourceNode, Result result) throws XMLPlatformException {
        DOMSource source = null;
        if ((isFragment()) && (result instanceof SAXResult)) {
            if (sourceNode instanceof Document) {
                source = new DOMSource(((Document)sourceNode).getDocumentElement());
            }
        } else {
            source = new DOMSource(sourceNode);
        }
        transform(source, result);
    }

    public void transform(Node sourceNode, Writer resultWriter) throws XMLPlatformException {
        DOMSource source = new DOMSource(sourceNode);
        StreamResult result = new StreamResult(resultWriter);

        if (isFragment()) {
        	getTransformer().setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        }
        transform(source, result);
    }

    public void transform(Source source, Result result) throws XMLPlatformException {
        try {
            if ((result instanceof StreamResult) && (isFragment())) {
            	getTransformer().setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            }
            getTransformer().transform(source, result);
        } catch (TransformerException e) {
            throw XMLPlatformException.xmlPlatformTransformException(e);
        }
    }

    //NB - this does NOT use the TransformerFactory singleton
    public void transform(Document sourceDocument, Node resultParentNode, URL stylesheet) throws XMLPlatformException {
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            transformerFactory.setErrorListener(new TransformErrorListener());
            StreamSource stylesheetSource = new StreamSource(stylesheet.openStream());
            Transformer transformer = transformerFactory.newTransformer(stylesheetSource);
            DOMSource source = new DOMSource(sourceDocument);
            DOMResult result = new DOMResult(resultParentNode);
            transformer.transform(source, result);
        } catch (Exception e) {
            throw XMLPlatformException.xmlPlatformTransformException(e);
        }
    }

    public void setFragment(boolean fragment) {
        this.fragment = fragment;
    }

    public boolean isFragment() {
        return fragment;
    }

    //NB - this DOES use the TransformerFactory singleton
    public Transformer getTransformer() {
        if (transformer == null) {
            try {
                transformer = TransformerFactoryHelper.getTransformerFactory().newTransformer();
                if (formatted) {
                    transformer.setOutputProperty(OutputKeys.INDENT, YES);
                } else {
                    transformer.setOutputProperty(OutputKeys.INDENT, NO);
                }
                if (encoding != null) {
                    transformer.setOutputProperty(OutputKeys.ENCODING, encoding);
                }
                if (version != null) {
                    transformer.setOutputProperty(OutputKeys.VERSION, version);
                }
            } catch (TransformerConfigurationException e) {
                throw XMLPlatformException.xmlPlatformTransformException(e);
            }
        }
        return transformer;
    }

    private static class TransformerFactoryHelper {
        //http://www.cs.umd.edu/~pugh/java/memoryModel/DoubleCheckedLocking.html helper class
        private static final TransformerFactory transformerFactory = TransformerFactory.newInstance();
        static TransformerFactory getTransformerFactory() {
            return transformerFactory;
        }
    }

    private static class TransformErrorListener implements ErrorListener {

        public void error(TransformerException exception) throws TransformerException {
            throw XMLPlatformException.xmlPlatformTransformException(exception);
        }

        public void fatalError(TransformerException exception) throws TransformerException {
            throw XMLPlatformException.xmlPlatformTransformException(exception);
        }

        public void warning(TransformerException exception) throws TransformerException {
            throw XMLPlatformException.xmlPlatformTransformException(exception);
        }

    }

}
