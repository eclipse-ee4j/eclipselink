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

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathException;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.eclipse.persistence.internal.oxm.Constants;
import org.eclipse.persistence.platform.xml.XMLNamespaceResolver;
import org.eclipse.persistence.platform.xml.XMLParser;
import org.eclipse.persistence.platform.xml.XMLPlatform;
import org.eclipse.persistence.platform.xml.XMLPlatformException;
import org.eclipse.persistence.platform.xml.XMLPlatformFactory;
import org.eclipse.persistence.platform.xml.XMLTransformer;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

/**
 * <p><b>Purpose</b>:  An implementation of XMLPlatform using JAXP 1.3 APIs.</p>
 */

public class JAXPPlatform implements XMLPlatform {
	
	private XPathFactory xPathFactory;
	private SchemaFactory schemaFactory;
    
    public JAXPPlatform() {
        super();
    }

    public XPathFactory getXPathFactory() {
    	if(null == xPathFactory) {
    		xPathFactory = XPathFactory.newInstance();
    	}
    	return xPathFactory;
    }

    public SchemaFactory getSchemaFactory() {
    	if(null == schemaFactory) {
    		schemaFactory = SchemaFactory.newInstance(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI);
    	}
    	return schemaFactory;
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
    public NodeList selectNodesAdvanced(Node contextNode, String xPathString, XMLNamespaceResolver xmlNamespaceResolver) throws XMLPlatformException {
    	try {
	        XPath xPath = getXPathFactory().newXPath();
	        if(null != xmlNamespaceResolver) {
	        	JAXPNamespaceContext namespaceContext = new JAXPNamespaceContext(xmlNamespaceResolver);
	        	xPath.setNamespaceContext(namespaceContext);
	        }
	        XPathExpression xPathExpression = xPath.compile(xPathString);
	        return (NodeList) xPathExpression.evaluate(contextNode, XPathConstants.NODESET);
    	} catch(XPathException e) {
            throw XMLPlatformException.xmlPlatformInvalidXPath(e);    		
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
    public Node selectSingleNodeAdvanced(Node contextNode, String xPathString, XMLNamespaceResolver xmlNamespaceResolver) throws XMLPlatformException {
    	try {
	        XPath xPath = getXPathFactory().newXPath();
	        if(null != xmlNamespaceResolver) {
	        	JAXPNamespaceContext namespaceContext = new JAXPNamespaceContext(xmlNamespaceResolver);
	        	xPath.setNamespaceContext(namespaceContext);
	        }
	        XPathExpression xPathExpression = xPath.compile(xPathString);
	        return (Node) xPathExpression.evaluate(contextNode, XPathConstants.NODE);
    	} catch(XPathException e) {
            throw XMLPlatformException.xmlPlatformInvalidXPath(e);    		
    	}
    }

    public boolean isWhitespaceNode(Text text) {
        String value = text.getNodeValue();
        if (null == value) {
            return false;
        } else {
            return value.trim().equals("");
        }
    }

    public XMLParser newXMLParser() {
        return new JAXPParser();
    }

    public XMLParser newXMLParser(Map<String, Boolean> parserFeatures) {
        return new JAXPParser(parserFeatures);
    }

    public XMLTransformer newXMLTransformer() {
        return new JAXPTransformer();
    }

    public Document createDocument() throws XMLPlatformException {
        try {
            DocumentBuilder documentBuilder = DocumentBuilderFactoryHelper.getDocumentBuilderFactory().newDocumentBuilder();
            return documentBuilder.newDocument();
        } catch (Exception e) {
            throw XMLPlatformException.xmlPlatformCouldNotCreateDocument(e);
        }
    }

    public Document createDocumentWithPublicIdentifier(String name, String publicIdentifier, String systemIdentifier) throws XMLPlatformException {
        try {
            if (null == publicIdentifier) {
                return createDocumentWithSystemIdentifier(name, systemIdentifier);
            }

            DocumentBuilder documentBuilder = DocumentBuilderFactoryHelper.getDocumentBuilderFactory().newDocumentBuilder();
            DOMImplementation domImpl = documentBuilder.getDOMImplementation();
            DocumentType docType = domImpl.createDocumentType(name, publicIdentifier, systemIdentifier);
            Document document = domImpl.createDocument(null, name, docType);
            return document;
        } catch (Exception e) {
            throw XMLPlatformException.xmlPlatformCouldNotCreateDocument(e);
        }
    }

    public Document createDocumentWithSystemIdentifier(String name, String systemIdentifier) throws XMLPlatformException {
        try {
            Document document = null;

            if (null == systemIdentifier) {
                document = createDocument();
                Element rootElement = document.createElement(name);
                document.appendChild(rootElement);
                return document;
            }

            DocumentBuilder documentBuilder = DocumentBuilderFactoryHelper.getDocumentBuilderFactory().newDocumentBuilder();
            DOMImplementation domImpl = documentBuilder.getDOMImplementation();
            DocumentType docType = domImpl.createDocumentType(name, null, systemIdentifier);
            document = domImpl.createDocument(null, name, docType);
            return document;
        } catch (Exception e) {
            throw XMLPlatformException.xmlPlatformCouldNotCreateDocument(e);
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
            Attr namespaceDeclaration = null;
            if(namespacePrefix != null) {
                namespaceDeclaration = contextElement.getAttributeNode("xmlns:" + namespacePrefix);
            } else {
                //look for default namespace declaration for null prefix
                namespaceDeclaration = contextElement.getAttributeNode(javax.xml.XMLConstants.XMLNS_ATTRIBUTE);
            }
            if (null != namespaceDeclaration) {
                return namespaceDeclaration.getValue();
            }
        }

        Node parentNode = contextNode.getParentNode();
        if (parentNode!=null && parentNode.getNodeType() == Node.ELEMENT_NODE) {
            return resolveNamespacePrefix(parentNode, namespacePrefix);
        }

        return null;
    }

    public boolean validateDocument(Document document, URL xmlSchemaURL, ErrorHandler errorHandler) throws XMLPlatformException {
    	Schema xmlSchema;
    	try {
    		xmlSchema = getSchemaFactory().newSchema(xmlSchemaURL);
    	} catch(SAXException e) {
            throw XMLPlatformException.xmlPlatformErrorResolvingXMLSchema(xmlSchemaURL, e);    		
    	}
    	try {
    		Validator validator = xmlSchema.newValidator();
    		validator.setErrorHandler(errorHandler);
    		validator.validate(new DOMSource(document));
    	} catch(Exception e) {
            throw XMLPlatformException.xmlPlatformValidationException(e);
    	}
		return true;
    }

    public boolean validate(Element elem, org.eclipse.persistence.oxm.XMLDescriptor xmlDescriptor, ErrorHandler handler) throws XMLPlatformException {
        return true;
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
            Attr namespaceDeclaration = next.getAttributeNode(javax.xml.XMLConstants.XMLNS_ATTRIBUTE+":" + elementPrefix);
            if ((null == namespaceDeclaration) && !declaredPrefixes.contains(elementPrefix)) {
                (next).setAttributeNS(javax.xml.XMLConstants.XMLNS_ATTRIBUTE_NS_URI, javax.xml.XMLConstants.XMLNS_ATTRIBUTE+ ":" + elementPrefix, elementUri);
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
                if (javax.xml.XMLConstants.XMLNS_ATTRIBUTE_NS_URI.equals(nextAttribute.getNamespaceURI())) {
                    declaredPrefixes.add(nextAttribute.getLocalName());
                } else {
                    Attr namespaceDeclaration = next.getAttributeNode(javax.xml.XMLConstants.XMLNS_ATTRIBUTE +":" + attributePrefix);
                    if ((null == namespaceDeclaration) && !declaredPrefixes.contains(attributePrefix)) {
                        String attributeUri = nextAttribute.getNamespaceURI();
                        (next).setAttributeNS(javax.xml.XMLConstants.XMLNS_ATTRIBUTE_NS_URI, javax.xml.XMLConstants.XMLNS_ATTRIBUTE + ":" + attributePrefix, attributeUri);
                        declaredPrefixes.add(attributePrefix);
                    }

                    //if xsi:type declaration deal with that value
                    if (javax.xml.XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI.equals(nextAttribute.getNamespaceURI()) && Constants.SCHEMA_TYPE_ATTRIBUTE.equals(nextAttribute.getLocalName())) {                        
                        String value = nextAttribute.getValue();
                        int colonIndex = value.indexOf(':');
                        if (colonIndex > -1) {
                            String prefix = value.substring(0, colonIndex);
                            namespaceDeclaration = next.getAttributeNode(javax.xml.XMLConstants.XMLNS_ATTRIBUTE +":" + prefix);
                            if ((null == namespaceDeclaration) && !declaredPrefixes.contains(prefix)) {                                
                                String uri = XMLPlatformFactory.getInstance().getXMLPlatform().resolveNamespacePrefix(next, prefix);
                                (next).setAttributeNS(javax.xml.XMLConstants.XMLNS_ATTRIBUTE_NS_URI, javax.xml.XMLConstants.XMLNS_ATTRIBUTE + ":" + prefix, uri);
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
    
    private static class DocumentBuilderFactoryHelper {
        private static DocumentBuilderFactory documentBuilderFactory;
        static {
            documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilderFactory.setNamespaceAware(true);
        }
        static DocumentBuilderFactory getDocumentBuilderFactory() {
            return documentBuilderFactory;
        }
    }
}
