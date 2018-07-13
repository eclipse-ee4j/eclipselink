/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
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

import org.eclipse.persistence.internal.helper.XMLHelper;
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

    @Override
    public String getEncoding() {
        return encoding;
    }

    @Override
    public void setEncoding(String encoding) {
        this.encoding = encoding;
        if(transformer != null){
            transformer.setOutputProperty(OutputKeys.ENCODING, encoding);
        }
    }

    @Override
    public boolean isFormattedOutput() {
        return formatted;
    }

    @Override
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

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public void setVersion(String version) {
        this.version = version;
        if(transformer != null){
            transformer.setOutputProperty(OutputKeys.VERSION, version);
        }
    }

    @Override
    public void transform(Node sourceNode, OutputStream resultOutputStream) throws XMLPlatformException {
        DOMSource source = new DOMSource(sourceNode);
        StreamResult result = new StreamResult(resultOutputStream);
        if (isFragment()) {
            getTransformer().setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        }
        transform(source, result);
    }

    @Override
    public void transform(Node sourceNode, ContentHandler resultContentHandler) throws XMLPlatformException {
        DOMSource source = new DOMSource(sourceNode);
        SAXResult result = new SAXResult(resultContentHandler);

        transform(source, result);
    }

    @Override
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

    @Override
    public void transform(Node sourceNode, Writer resultWriter) throws XMLPlatformException {
        DOMSource source = new DOMSource(sourceNode);
        StreamResult result = new StreamResult(resultWriter);

        if (isFragment()) {
            getTransformer().setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        }
        transform(source, result);
    }

    @Override
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
    @Override
    public void transform(Document sourceDocument, Node resultParentNode, URL stylesheet) throws XMLPlatformException {
        try {
            TransformerFactory transformerFactory = XMLHelper.createTransformerFactory(false);
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

    @Override
    public void setFragment(boolean fragment) {
        this.fragment = fragment;
    }

    @Override
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
        static TransformerFactory getTransformerFactory() {
            return XMLHelper.createTransformerFactory(false);
        }
    }

    private static class TransformErrorListener implements ErrorListener {

        @Override
        public void error(TransformerException exception) throws TransformerException {
            throw XMLPlatformException.xmlPlatformTransformException(exception);
        }

        @Override
        public void fatalError(TransformerException exception) throws TransformerException {
            throw XMLPlatformException.xmlPlatformTransformException(exception);
        }

        @Override
        public void warning(TransformerException exception) throws TransformerException {
            throw XMLPlatformException.xmlPlatformTransformException(exception);
        }

    }

}
