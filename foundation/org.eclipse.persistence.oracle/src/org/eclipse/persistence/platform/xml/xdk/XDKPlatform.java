/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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

import java.lang.reflect.Method;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.StringTokenizer;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.platform.xml.XMLNamespaceResolver;
import org.eclipse.persistence.platform.xml.XMLParser;
import org.eclipse.persistence.platform.xml.XMLPlatform;
import org.eclipse.persistence.platform.xml.XMLPlatformException;
import org.eclipse.persistence.platform.xml.XMLPlatformFactory;
import org.eclipse.persistence.platform.xml.XMLSchemaReference;
import org.eclipse.persistence.platform.xml.XMLTransformer;
import oracle.xml.parser.schema.XMLSchema;
import oracle.xml.parser.schema.XSDBuilder;
import oracle.xml.parser.schema.XSDComplexType;
import oracle.xml.parser.schema.XSDConstantValues;
import oracle.xml.parser.schema.XSDElement;
import oracle.xml.parser.schema.XSDNode;
import oracle.xml.parser.schema.XSDValidator;
import oracle.xml.parser.v2.XMLDocument;
import oracle.xml.parser.v2.XMLElement;
import oracle.xml.parser.v2.XMLError;
import oracle.xml.parser.v2.XMLNode;
import oracle.xml.parser.v2.XMLParseException;
import oracle.xml.parser.v2.XSLException;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * <p><b>Purpose</b>:  An implementation of XMLPlatform using Oracle XDK APIs.</p>
 */

public class XDKPlatform implements XMLPlatform {
    private Method buildSchemaMethod;

    public XDKPlatform() {
        super();
        try {
            Class[] argTypes = { URL.class };
            buildSchemaMethod = Helper.getDeclaredMethod(XSDBuilder.class, "build", argTypes);
        } catch (NoSuchMethodException e) {
        }
    }

    /**
     * Execute advanced XPath statements that are required for TopLink EIS.
     * @param contextNode
     * @param xPath
     * @param xmlNamespaceResolver
     * @return
     * @throws XMLPlatformException
     */
    public Node selectSingleNodeAdvanced(Node contextNode, String xPath, XMLNamespaceResolver xmlNamespaceResolver) throws XMLPlatformException {
        try {
            XMLNode xmlNode = (XMLNode)contextNode;
            XDKNamespaceResolver xdkNamespaceResolver = new XDKNamespaceResolver(xmlNamespaceResolver);
            return xmlNode.selectSingleNode(xPath, xdkNamespaceResolver);
        } catch (XSLException e) {
            throw XMLPlatformException.xmlPlatformInvalidXPath(e);
        }
    }

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
    public NodeList selectNodesAdvanced(Node contextNode, String xPath, XMLNamespaceResolver xmlNamespaceResolver) throws XMLPlatformException {
        try {
            XMLNode xmlNode = (XMLNode)contextNode;
            XDKNamespaceResolver xdkNamespaceResolver = new XDKNamespaceResolver(xmlNamespaceResolver);
            return xmlNode.selectNodes(xPath, xdkNamespaceResolver);
        } catch (XSLException e) {
            throw XMLPlatformException.xmlPlatformInvalidXPath(e);
        }
    }

    public Document createDocument() throws XMLPlatformException {
        return new XMLDocument();
    }

    public Document createDocumentWithPublicIdentifier(String name, String publicIdentifier, String systemIdentifier) throws XMLPlatformException {
        try {
            XMLDocument xmlDocument = (XMLDocument)createDocument();
            Element rootElement = xmlDocument.createElement(name);
            xmlDocument.appendChild(rootElement);
            xmlDocument.setDoctype(name, systemIdentifier, publicIdentifier);
            return xmlDocument;
        } catch (Exception e) {
            throw XMLPlatformException.xmlPlatformCouldNotCreateDocument(e);
        }
    }

    public Document createDocumentWithSystemIdentifier(String name, String systemIdentifier) throws XMLPlatformException {
        try {
            if (null == systemIdentifier) {
                Document document = createDocument();
                Element rootElement = document.createElement(name);
                document.appendChild(rootElement);
                return document;
            }

            XMLDocument xmlDocument = (XMLDocument)createDocument();
            Element rootElement = xmlDocument.createElement(name);
            xmlDocument.appendChild(rootElement);
            xmlDocument.setDoctype(name, systemIdentifier, null);
            return xmlDocument;
        } catch (Exception e) {
            throw XMLPlatformException.xmlPlatformCouldNotCreateDocument(e);
        }
    }

    public boolean isWhitespaceNode(Text text) {
        try {
            String value = text.getNodeValue();
            if (null == value) {
                return false;
            } else {
                return value.trim().equals("");
            }
        } catch (NullPointerException e) {
            // The 9.0.4 XDK will throw a NPE on getNoderValue() if the node value is null.
            return false;
        }
    }

    public String resolveNamespacePrefix(Node contextNode, String namespacePrefix) throws XMLPlatformException {
        if (null == namespacePrefix) {
            if (null == contextNode.getPrefix()) {
                return contextNode.getNamespaceURI();
            }
        } else if (namespacePrefix.equals(contextNode.getPrefix())) {
            return contextNode.getNamespaceURI();
        }

        if (contextNode.getNodeType() == Node.ELEMENT_NODE) {
            Element contextElement = (Element)contextNode;
            Attr namespaceDeclaration = contextElement.getAttributeNode("xmlns:" + namespacePrefix);
            if (null != namespaceDeclaration) {
                return namespaceDeclaration.getValue();
            }
        }

        Node parentNode = contextNode.getParentNode();
        if (parentNode != null && parentNode.getNodeType() == Node.ELEMENT_NODE) {        
            return resolveNamespacePrefix(parentNode, namespacePrefix);
        }

        return null;
    }

    public XMLParser newXMLParser() {
        return new XDKParser();
    }

    public XMLParser newXMLParser(Map<String, Boolean> parserFeatures) {
        return new XDKParser();
    }

    public XMLTransformer newXMLTransformer() {
        return new XDKTransformer();
    }

    /**
     * Validates a document against an XML schema
     *
     * @param document - the document to be validated
     * @param xmlSchemaURL - the schema URL
     * @param errorHandler - the error handler
     * @return true if the document fragment is valid, false otherwise
     */
    public boolean validateDocument(Document document, URL xmlSchemaURL, ErrorHandler errorHandler) throws XMLPlatformException {
        XMLSchema xmlSchema = null;
        XSDValidator validator = null;
        try {
            Object[] args = { xmlSchemaURL };
            xmlSchema = (XMLSchema)buildSchemaMethod.invoke(new XSDBuilder(), args);
            validator = new XSDValidator();
        } catch (Exception e) {
            throw XMLPlatformException.xmlPlatformErrorResolvingXMLSchema(xmlSchemaURL, e);
        }

        // set the schema to be validated against
        validator.setXMLProperty(XSDValidator.FIXED_SCHEMA, xmlSchema);

        XMLError xmlErr = new XMLError();
        try {
            validator.setError(xmlErr);
        } catch (org.xml.sax.SAXException saxex) {
            throw XMLPlatformException.xmlPlatformValidationException(saxex);
        }

        try {
            ((XMLDocument)document).validateContent(validator, true);
        } catch (XMLParseException e) {
            // Ignore this exception, the XMLError will be used to determine if theree
            // were any errors.			
        }
        handleErrors(xmlErr, errorHandler);

        return true;
    }

    /**
     * Validates a document fragment against a complex type or element in the XML schema
     *
     * @param document - the document which contains the document fragment to be validated
     * @param schemaReference - the path to the complex type or element to be validated against in the schema
     * @param handler - the error handler
     * @return true if the document fragment is valid, false otherwise
     */
    public boolean validate(Element elem, XMLDescriptor xmlDescriptor, ErrorHandler errorHandler) throws XMLPlatformException {
        XMLSchemaReference schemaReference = xmlDescriptor.getSchemaReference();
        NamespaceResolver nsResolver = xmlDescriptor.getNamespaceResolver();

        // build a schema using the URL in the schema reference, and setup a validator
        XMLSchema xmlSchema;
        XSDValidator validator = null;
        try {
            Object[] args = { schemaReference.getURL() };
            xmlSchema = (XMLSchema)buildSchemaMethod.invoke(new XSDBuilder(), args);
            validator = new XSDValidator();
        } catch (Exception ex) {
            throw XMLPlatformException.xmlPlatformValidationException(ex);
        }

        // set the schema to be validated against
        validator.setXMLProperty(XSDValidator.FIXED_SCHEMA, xmlSchema);

        // set the node to be validated against
        XSDNode xsdNode = getNodeFromSchemaReference(xmlSchema, schemaReference, nsResolver);

        // if xsdNode is null, the schema context string is empty or the target could not be found
        if (xsdNode == null) {
            validator.setXMLProperty(XSDNode.ROOT_NODE, null);
        }

        if (schemaReference.getType() == XMLSchemaReference.ELEMENT) {
            if (xmlDescriptor.getDefaultRootElement() != null) {
                validator.setXMLProperty(XSDNode.ROOT_NODE, xsdNode);
            } else {
                validator.setXMLProperty(XSDNode.ROOT_NODE, ((XSDElement)xsdNode).getType());
            }
        } else {
            validator.setXMLProperty(XSDNode.ROOT_NODE, xsdNode);
        }

        XMLError xmlErr = new XMLError();
        try {
            validator.setError(xmlErr);
        } catch (org.xml.sax.SAXException saxex) {
            throw XMLPlatformException.xmlPlatformValidationException(saxex);
        }

        try {
            ((XMLElement)elem).validateContent(validator, true);
        } catch (XMLParseException e) {
            // Ignore this exception, the XMLError will be used to determine if theree
            // were any errors.			
        }
        handleErrors(xmlErr, errorHandler);

        return true;
    }

    private void handleErrors(XMLError xmlErr, ErrorHandler errorHandler) {
        try {
            int numberOfMessages = xmlErr.getNumMessages();
            SAXParseException saxParseException;
            for (int x = 0; x < numberOfMessages; x++) {
                saxParseException = new SAXParseException(xmlErr.getMessage(x), xmlErr.getPublicId(x), xmlErr.getSystemId(x), xmlErr.getLineNumber(x), xmlErr.getColumnNumber(x), xmlErr.getException(x));

                if (null == errorHandler) {
                    throw saxParseException;
                }
                errorHandler.fatalError(saxParseException);
            }
        } catch (SAXException xmlex) {
            throw XMLPlatformException.xmlPlatformValidationException(xmlex);
        }
    }

    /**
     * This convenience method will parse a schema reference and return the node to be
     * validated against.
     *
     * @param xmlSchema - the schema to be used for validation
     * @param schemaRef - the schema reference object
     * @return the node to be validated against, null if not found
     */
    private XSDNode getNodeFromSchemaReference(XMLSchema xmlSchema, XMLSchemaReference schemaRef, NamespaceResolver nsResolver) {
        if (schemaRef == null) {
            return null;
        }

        // schema context should be in the format '/prefix:nodeName/.."
        // tokenize the schema context to find the node that is to be validated
        StringTokenizer nodes = new StringTokenizer(schemaRef.getSchemaContext(), "/");

        // if no tokens, then invalid schema context
        if (!(nodes.hasMoreTokens())) {
            return null;
        }

        String namespace = "";
        String nodeName = nodes.nextToken();

        // look for a prefix
        StringTokenizer prefixes = new StringTokenizer(nodeName, ":");

        if (prefixes.countTokens() > 1) {
            // look for a namespace
            namespace = nsResolver.resolveNamespacePrefix(prefixes.nextToken());
            if (namespace == null) {
                namespace = "";
            }
        }

        // else no prefix
        nodeName = prefixes.nextToken();

        // handle simple/complex type definitions
        if (schemaRef.getType() == XMLSchemaReference.SIMPLE_TYPE) {
            return xmlSchema.getType(namespace, nodeName, XSDConstantValues.DATATYPE);
        }

        if (schemaRef.getType() == XMLSchemaReference.COMPLEX_TYPE) {
            return xmlSchema.getType(namespace, nodeName, XSDConstantValues.TYPE);
        }

        // handle elements
        XSDNode node = xmlSchema.getElement(namespace, nodeName);

        // loop through schema context tokens - 'node' will contain the target when completed
        while (nodes.hasMoreTokens()) {
            node = findChildNode((XSDElement)node, nodes.nextToken());

            // if node is null, couldn't find child
            if (node == null) {
                return null;
            }
        }
        return node;
    }

    /**
     * This convenience method will iterate through a parent element's children and return the
     * node corresponding to 'nodeName'.
     *
     * @param parent - the parent element
     * @param childName - the node name to be located
     * @return the child node with name matching 'childName', null if not found
     */
    protected XSDNode findChildNode(XSDElement parent, String childName) {
        XSDNode[] children;
        XSDNode node = null;
        boolean successful = false;

        // get the parent node's children
        children = ((XSDComplexType)parent.getType()).getElementSet();

        // iterate over child nodes looking for the child
        for (int i = 0; i < children.length; i++) {
            node = children[i];

            if (node.getName().equals(childName)) {
                successful = true;
                break;
            }
        }

        if (successful) {
            return node;
        }
        return null;
    }
    
     public void namespaceQualifyFragment(Element next) {
        namespaceQualifyFragment(next, new ArrayList<String>());
    }

    //pass list of prefixes declared and encountered
    private void namespaceQualifyFragment(Element next, List<String> declaredPrefixes) {
        String elementUri = next.getNamespaceURI();
        String elementPrefix = next.getPrefix();
        if (elementPrefix != null) {
            //see if this prefix is already declared if yes - do nothing, if no declare
            Attr namespaceDeclaration = next.getAttributeNode(XMLConstants.XMLNS +":" + elementPrefix);
            if ((null == namespaceDeclaration) && !declaredPrefixes.contains(elementPrefix)) {
                (next).setAttributeNS(XMLConstants.XMLNS_URL, XMLConstants.XMLNS + ":" + elementPrefix, elementUri);
                declaredPrefixes.add(elementPrefix);
            }
        }

        //check all attributes prefixes and if any of them arent declared add them also.            
        NamedNodeMap attributes = next.getAttributes();
        int attributesSize = attributes.getLength();
        for (int i = 0; i < attributesSize; i++) {
            Attr nextAttribute = (Attr)attributes.item(i);
            String attributePrefix = nextAttribute.getPrefix();
            if (attributePrefix != null) {
                //if attribute is a namespace declaration add to declared list 
                if (XMLConstants.XMLNS_URL.equals(nextAttribute.getNamespaceURI())) {
                    declaredPrefixes.add(nextAttribute.getLocalName());
                } else {
                    Attr namespaceDeclaration = next.getAttributeNode(XMLConstants.XMLNS +":" + attributePrefix);
                    if ((null == namespaceDeclaration) && !declaredPrefixes.contains(attributePrefix)) {
                        String attributeUri = nextAttribute.getNamespaceURI();
                        (next).setAttributeNS(XMLConstants.XMLNS_URL, XMLConstants.XMLNS + ":" + attributePrefix, attributeUri);
                        declaredPrefixes.add(attributePrefix);
                    }

                    //if xsi:type declaration deal with that value
                    if (XMLConstants.SCHEMA_INSTANCE_URL.equals(nextAttribute.getNamespaceURI()) && XMLConstants.SCHEMA_TYPE_ATTRIBUTE.equals(nextAttribute.getLocalName())) {                        
                        String value = nextAttribute.getValue();
                        int colonIndex = value.indexOf(':');
                        if (colonIndex > -1) {
                            String prefix = value.substring(0, colonIndex);
                            namespaceDeclaration = next.getAttributeNode(XMLConstants.XMLNS +":" + prefix);
                            if ((null == namespaceDeclaration) && !declaredPrefixes.contains(prefix)) {                                
                                String uri = XMLPlatformFactory.getInstance().getXMLPlatform().resolveNamespacePrefix(next, prefix);
                                (next).setAttributeNS(XMLConstants.XMLNS_URL, XMLConstants.XMLNS + ":" + prefix, uri);
                                declaredPrefixes.add(prefix);
                            }
                        }
                    }
                }
            }
        }

        NodeList children = next.getChildNodes();
        int numberOfNodes = children.getLength();
        for (int i = 0; i < numberOfNodes; i++) {
            Node nextNode = children.item(i);
            if (nextNode.getNodeType() == Node.ELEMENT_NODE) {
                Element child = (Element)nextNode;
                namespaceQualifyFragment(child, declaredPrefixes);
            }
        }
    }
}
