/*
 * Copyright (c) 1998, 2019 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.platform.xml;

import java.net.URL;
import java.util.Map;

import org.eclipse.persistence.oxm.XMLDescriptor;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.ErrorHandler;

public interface XMLPlatform {

    /**
     * Creates a new document.
     * @return the new document
     * @throws XMLPlatformException
     */
    Document createDocument() throws XMLPlatformException;

    /**
     * Creates a new document with the specified public and system
     * identifiers in the DOCTYPE, and adds a root element with the
     * specified name.
     * @param  name the name of the root element
     *         publicIdentifier the public identifier
     *         systemIdentifier the system identifier
     * @return the new document
     * @throws XMLPlatformException
     */
    Document createDocumentWithPublicIdentifier(String name, String publicIdentifier, String systemIdentifier) throws XMLPlatformException;

    /**
     * Creates a new document with the specified system identifier in
     * the DOCTYPE, and adds a root element with the specified name.
     * @param  name the name of the root element
     *         systemIdentifier the system identifier
     * @return the new document
     * @throws XMLPlatformException
     */
    Document createDocumentWithSystemIdentifier(String name, String systemIdentifier) throws XMLPlatformException;

    /**
     * Check to see if the text node represents a whitespace node.
     * @param text a potential whitespace node
     * @return if the text node represents a whitespace node.
     */
    boolean isWhitespaceNode(Text text);

    /**
     * Return the namespace URI for the specified namespace prefix
     * relative to the context node.
     * @param  contextNode the node to be looking for the namespace
     *         URI
     *         namespacePrefix the namespace prefix
     * @return the namespace URI for the specified prefix
     * @throws XMLPlatformException
     */
    String resolveNamespacePrefix(Node contextNode, String namespacePrefix) throws XMLPlatformException;

    /**
     * Execute advanced XPath statements that are required for TopLink EIS.
     * @param  contextNode the node relative to which the XPath
     *         statement will be executed.
     *         xPath the XPath statement
     *         namespaceResolver used to resolve namespace prefixes
     *         to the corresponding namespace URI
     * @return the XPath result
     * @throws XMLPlatformException
     */
    NodeList selectNodesAdvanced(Node contextNode, String xPath, XMLNamespaceResolver xmlNamespaceResolver) throws XMLPlatformException;

    /**
     * Execute advanced XPath statements that are required for TopLink EIS.
     * @param contextNode
     * @param xPath
     * @param xmlNamespaceResolver
     * @return
     * @throws XMLPlatformException
     */
    Node selectSingleNodeAdvanced(Node contextNode, String xPath, XMLNamespaceResolver xmlNamespaceResolver) throws XMLPlatformException;

    /**
     * Return a concrete implementation of the XML parser abstraction that is
     * compatible with the XML Platform.
     * @return a platform specific XML parser
     */
    XMLParser newXMLParser();

    /**
     * Return a concrete implementation of the XML parser abstraction that is
     * compatible with the XML Platform, based on these parser features.
     * @return a platform specific XML parser
     */
    XMLParser newXMLParser(Map<String, Boolean> parserFeatures);

    /**
     * Return a concrete implementation of the XML transformer abstraction that is
     * compatible with the XML Platform.
     * @return a platform specific XML transformer
     */
    XMLTransformer newXMLTransformer();

    /**
     * Validate the document against the XML Schema
     * @param  document the document to be validated
     * @param  xmlSchemaURL the XML Schema
     * @param  errorHandler a mechanism for selectively ignoring errors
     * @return true if the document is valid, else false
     * @throws XMLPlatformException
     */
    boolean validateDocument(Document document, URL xmlSchemaURL, ErrorHandler errorHandler) throws XMLPlatformException;

    /**
     * Validates a document fragment against a complex type or element in the XML schema
     *
     * @return true if the document fragment is valid, false otherwise
     */
    boolean validate(Element elem, XMLDescriptor xmlDescriptor, ErrorHandler handler) throws XMLPlatformException;

    /**
    *
    * @param next Element to qualify
    */
    void namespaceQualifyFragment(Element next);

    void setDisableSecureProcessing(boolean disableSecureProcessing);

    boolean isSecureProcessingDisabled();
}
