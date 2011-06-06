/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.platform.xml.xdk;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URL;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamResult;
import org.eclipse.persistence.platform.xml.XMLPlatformException;
import org.eclipse.persistence.platform.xml.XMLTransformer;
import oracle.xml.jaxp.JXTransformer;
import oracle.xml.parser.v2.XMLDocument;
import oracle.xml.parser.v2.XMLDocumentFragment;
import oracle.xml.parser.v2.XMLNode;
import oracle.xml.parser.v2.XSLException;
import oracle.xml.parser.v2.XSLProcessor;
import oracle.xml.parser.v2.XSLStylesheet;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

/**
 * <p><b>Purpose</b>:  An implementation of XMLTransformer using Oracle XDK 
 * APIs.</p>
 */

public class XDKTransformer implements XMLTransformer {
    private String encoding;
    private String version;
    private boolean formattedOutput;
    private boolean fragment;

    public XDKTransformer() {
        super();
        setEncoding("utf-8");
        setVersion("1.0");
        setFormattedOutput(true);
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public boolean isFormattedOutput() {
        return formattedOutput;
    }

    public void setFormattedOutput(boolean shouldFormat) {
        formattedOutput = shouldFormat;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void transform(Node sourceNode, OutputStream resultOutputStream) throws XMLPlatformException {
        try {
            OutputStreamWriter resultWriter = new OutputStreamWriter(resultOutputStream, getEncoding());
            transform(sourceNode, resultWriter);
        } catch (UnsupportedEncodingException e) {
            throw XMLPlatformException.xmlPlatformTransformException(e);
        }
    }

    public void transform(Node sourceNode, ContentHandler resultContentHandler) throws XMLPlatformException {
        try {
            XMLNode xmlNode = (XMLNode)sourceNode;
            xmlNode.reportSAXEvents(resultContentHandler);
        } catch (SAXException e) {
            throw XMLPlatformException.xmlPlatformTransformException(e);
        }
    }

    public void transform(Node sourceNode, Result result) throws XMLPlatformException {
        DOMSource source = new DOMSource(sourceNode);
        transform(source, result);
    }

    public void transform(Node sourceNode, Writer resultWriter) throws XMLPlatformException {
        try {
            XMLDocument xmlDocument;
            if (sourceNode.getNodeType() == Node.DOCUMENT_NODE) {
                xmlDocument = (XMLDocument)sourceNode;
            } else {
                xmlDocument = (XMLDocument)sourceNode.getOwnerDocument();
            }

            if (isFragment()) {
                xmlDocument.setEncoding(null);
                xmlDocument.setVersion(null);
            } else {
                xmlDocument.setEncoding(getEncoding());
                xmlDocument.setVersion(getVersion());
            }

            XMLNode xmlNode = (XMLNode)sourceNode;
            PrintWriter printWriter = new PrintWriter(resultWriter);
            XDKPrintDriver xdkPrintDriver = new XDKPrintDriver(printWriter);
            xdkPrintDriver.setFormattedOutput(isFormattedOutput());
            xdkPrintDriver.setEncoding(getEncoding());
            xdkPrintDriver.print(xmlNode);

            resultWriter.flush();
        } catch (IOException e) {
            throw XMLPlatformException.xmlPlatformTransformException(e);
        }
    }

    public void transform(Source source, Result result) throws XMLPlatformException {
        try {
            JXTransformer transformer = new JXTransformer();
            
            if ((result instanceof StreamResult) && (isFragment())) {
                transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            }

            if ((result instanceof SAXResult) && (!isFragment())) {
                ((SAXResult)result).getHandler().startDocument();
            }
            transformer.transform(source, result);
            if ((result instanceof SAXResult) && (!isFragment())) {
                ((SAXResult)result).getHandler().endDocument();
            }
        } catch (TransformerException e) {
            throw XMLPlatformException.xmlPlatformTransformException(e);
        } catch (SAXException e) {
            throw XMLPlatformException.xmlPlatformTransformException(e);
        }
    }

    public void transform(Document sourceDocument, Node resultParentNode, URL stylesheet) throws XMLPlatformException {
        try {
            XSLProcessor xslProcessor = new XSLProcessor();
            XSLStylesheet xslStylesheet = xslProcessor.newXSLStylesheet(stylesheet);
            XMLDocument xmlDocument = (XMLDocument)sourceDocument;
            XMLDocumentFragment resultDocumentFragment = xslProcessor.processXSL(xslStylesheet, xmlDocument);
            resultParentNode.appendChild(resultDocumentFragment);
        } catch (XSLException e) {
            throw XMLPlatformException.xmlPlatformTransformException(e);
        }
    }

    public void setFragment(boolean fragment) {
        this.fragment = fragment;
    }

    public boolean isFragment() {
        return fragment;
    }
}
