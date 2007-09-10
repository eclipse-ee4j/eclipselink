/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.oxm.record;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import org.eclipse.persistence.exceptions.XMLMarshalException;
import org.eclipse.persistence.internal.oxm.XMLConversionManager;
import org.eclipse.persistence.internal.oxm.XMLObjectBuilder;
import org.eclipse.persistence.internal.oxm.XPathFragment;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLRoot;
import org.eclipse.persistence.oxm.XMLUnmarshaller;
import org.eclipse.persistence.oxm.record.DOMRecord;
import org.eclipse.persistence.platform.xml.XMLParser;
import org.eclipse.persistence.platform.xml.XMLPlatformException;
import org.eclipse.persistence.platform.xml.XMLPlatformFactory;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;

/**
 * INTERNAL:
 * <p><b>Purpose:</b>Provide an implementation of PlatformUnmarshaller that makes use of the DOM
 * unmarshal code. Used by the DOMPlatform
 * <p><b>Responsibilities:</b><ul>
 * <li>Implement the required unmarshal methods from platform unmarshaller</li>
 * <li>Perform xml-to-object conversions</li>
 * </ul>
 * @author bdoughan
 * @see org.eclipse.persistence.oxm.platform.DOMPlatform
 *
 */
public class DOMUnmarshaller implements PlatformUnmarshaller {
    private XMLParser parser;
    private XMLContext xmlContext;

    public DOMUnmarshaller(XMLContext xmlContext) {
        super();
        parser = XMLPlatformFactory.getInstance().getXMLPlatform().newXMLParser();
        parser.setNamespaceAware(true);
        parser.setValidationMode(XMLParser.NONVALIDATING);
        this.xmlContext = xmlContext;
    }

    public EntityResolver getEntityResolver() {
        return parser.getEntityResolver();
    }

    public void setEntityResolver(EntityResolver entityResolver) {
        parser.setEntityResolver(entityResolver);
    }

    public ErrorHandler getErrorHandler() {
        return parser.getErrorHandler();
    }

    public void setErrorHandler(ErrorHandler errorHandler) {
        parser.setErrorHandler(errorHandler);
    }

    public int getValidationMode() {
        return parser.getValidationMode();
    }

    public void setValidationMode(int validationMode) {
        parser.setValidationMode(validationMode);
    }

    public void setWhitespacePreserving(boolean isWhitespacePreserving) {
        parser.setWhitespacePreserving(isWhitespacePreserving);
    }

    public void setSchemas(Object[] schemas) {
        try {
            parser.setXMLSchemas(schemas);
        } catch (XMLPlatformException e) {
            throw XMLMarshalException.errorSettingSchemas(e, schemas);
        }
    }

    public Object unmarshal(File file, XMLUnmarshaller unmarshaller) {
        try {
            Document document = null;
            document = parser.parse(file);
            return xmlToObject(new DOMRecord(document), unmarshaller);
        } catch (XMLPlatformException e) {
            throw XMLMarshalException.unmarshalException(e);
        }
    }

    public Object unmarshal(File file, Class clazz, XMLUnmarshaller unmarshaller) {
        try {
            Document document = null;
            document = parser.parse(file);
            return xmlToObject(new DOMRecord(document), clazz, unmarshaller);
        } catch (XMLPlatformException e) {
            throw XMLMarshalException.unmarshalException(e);
        }
    }

    public Object unmarshal(InputStream inputStream, XMLUnmarshaller unmarshaller) {
        try {
            Document document = null;
            document = parser.parse(inputStream);
            return xmlToObject(new DOMRecord(document), unmarshaller);
        } catch (XMLPlatformException e) {
            throw XMLMarshalException.unmarshalException(e);
        }
    }

    public Object unmarshal(InputStream inputStream, Class clazz, XMLUnmarshaller unmarshaller) {
        try {
            Document document = null;
            document = parser.parse(inputStream);
            return xmlToObject(new DOMRecord(document), clazz, unmarshaller);
        } catch (XMLPlatformException e) {
            throw XMLMarshalException.unmarshalException(e);
        }
    }

    public Object unmarshal(InputSource inputSource, XMLUnmarshaller unmarshaller) {
        try {
            Document document = null;
            document = parser.parse(inputSource);
            return xmlToObject(new DOMRecord(document), unmarshaller);
        } catch (XMLPlatformException e) {
            throw XMLMarshalException.unmarshalException(e);
        }
    }

    public Object unmarshal(InputSource inputSource, Class clazz, XMLUnmarshaller unmarshaller) {
        try {
            Document document = null;
            document = parser.parse(inputSource);
            return xmlToObject(new DOMRecord(document), clazz, unmarshaller);
        } catch (XMLPlatformException e) {
            throw XMLMarshalException.unmarshalException(e);
        }
    }

    public Object unmarshal(Node node, XMLUnmarshaller unmarshaller) {
        Element element = null;
        switch (node.getNodeType()) {
        case Node.DOCUMENT_NODE: {
            element = ((Document) node).getDocumentElement();
            break;
        }
        case Node.ELEMENT_NODE: {
            element = (Element) node;
            break;
        }
        default:
            throw XMLMarshalException.unmarshalException();
        }
        return xmlToObject(new DOMRecord(element), unmarshaller);
    }

    public Object unmarshal(Node node, Class clazz, XMLUnmarshaller unmarshaller) {
        Element element = null;
        switch (node.getNodeType()) {
        case Node.DOCUMENT_NODE: {
            element = ((Document) node).getDocumentElement();
            break;
        }
        case Node.ELEMENT_NODE: {
            element = (Element) node;
            break;
        }
        default:
            throw XMLMarshalException.unmarshalException();
        }
        return xmlToObject(new DOMRecord(element), clazz, unmarshaller);
    }

    public Object unmarshal(Reader reader, XMLUnmarshaller unmarshaller) {
        try {
            Document document = null;
            document = parser.parse(reader);
            return xmlToObject(new DOMRecord(document), unmarshaller);
        } catch (XMLPlatformException e) {
            throw XMLMarshalException.unmarshalException(e);
        }
    }

    public Object unmarshal(Reader reader, Class clazz, XMLUnmarshaller unmarshaller) {
        try {
            Document document = null;
            document = parser.parse(reader);
            return xmlToObject(new DOMRecord(document), clazz, unmarshaller);
        } catch (XMLPlatformException e) {
            throw XMLMarshalException.unmarshalException(e);
        }
    }

    public Object unmarshal(Source source, XMLUnmarshaller unmarshaller) {
        try {
            Document document = null;
            document = parser.parse(source);
            return xmlToObject(new DOMRecord(document), unmarshaller);
        } catch (XMLPlatformException e) {
            throw XMLMarshalException.unmarshalException(e);
        }
    }

    public Object unmarshal(Source source, Class clazz, XMLUnmarshaller unmarshaller) {
        try {
            Document document = null;
            document = parser.parse(source);
            return xmlToObject(new DOMRecord(document), clazz, unmarshaller);
        } catch (XMLPlatformException e) {
            throw XMLMarshalException.unmarshalException(e);
        }
    }

    public Object unmarshal(URL url, XMLUnmarshaller unmarshaller) {
        try {
            Document document = null;
            document = parser.parse(url);
            return xmlToObject(new DOMRecord(document), unmarshaller);
        } catch (XMLPlatformException e) {
            throw XMLMarshalException.unmarshalException(e);
        }
    }

    public Object unmarshal(URL url, Class clazz, XMLUnmarshaller unmarshaller) {
        try {
            Document document = null;
            document = parser.parse(url);
            return xmlToObject(new DOMRecord(document), clazz, unmarshaller);
        } catch (XMLPlatformException e) {
            throw XMLMarshalException.unmarshalException(e);
        }
    }

    /**
     * INTERNAL: Return the descriptor for the document.
     */
    protected XMLDescriptor getDescriptor(DOMRecord xmlRecord) throws XMLMarshalException {
        QName rootQName = new QName(xmlRecord.getNamespaceURI(), xmlRecord.getLocalName());
        XMLDescriptor xmlDescriptor = xmlContext.getDescriptor(rootQName);
        if (null == xmlDescriptor) {
            // Try to find a descriptor based on the schema type
            String type = ((Element)xmlRecord.getDOM()).getAttributeNS(XMLConstants.SCHEMA_INSTANCE_URL, "type");
            if (null != type) {
                XPathFragment typeFragment = new XPathFragment(type);
                String namespaceURI = xmlRecord.resolveNamespacePrefix(typeFragment.getPrefix());
                typeFragment.setNamespaceURI(namespaceURI);
                xmlDescriptor = xmlContext.getDescriptorByGlobalType(typeFragment);
            }
            if (null == xmlDescriptor) {
                throw XMLMarshalException.noDescriptorWithMatchingRootElement(rootQName.toString());
            }
        }
        return xmlDescriptor;
    }

    /**
     * INTERNAL: Find the Descriptor corresponding to the context node of the
     * XMLRecord, and then convert the XMLRecord to an instance of the
     * corresponding object.
     * 
     * @param xmlRecord
     *            The XMLRecord to unmarshal from
     * @return the object which resulted from unmarshalling the given XMLRecord
     * @throws XMLMarshalException
     *             if an error occurred during unmarshalling
     */
    public Object xmlToObject(DOMRecord xmlRecord, XMLUnmarshaller unmarshaller) throws XMLMarshalException {
        XMLDescriptor xmlDescriptor = getDescriptor(xmlRecord);
        return xmlToObject(xmlRecord, xmlDescriptor.getJavaClass(), unmarshaller);
    }

    /**
     * INTERNAL: Convert the Oracle XMLDocument to the reference-class.
     */
    public Object xmlToObject(DOMRecord xmlRow, Class referenceClass, XMLUnmarshaller unmarshaller) throws XMLMarshalException {
        // handle case where the reference class is a primitive wrapper - in
        // this case, we need to use the conversion manager to convert the 
        // node's value to the primitive wrapper class, then create, 
        // populate and return an XMLRoot
        if (XMLConversionManager.getDefaultXMLManager().getDefaultJavaTypes().get(referenceClass) != null) {
            // we're assuming that since we're unmarshalling to a primitive
            // wrapper, the root
            // element has a single text node
            Object nodeVal;
            try {
                Text rootTxt = (Text) xmlRow.getDOM().getFirstChild();
                nodeVal = rootTxt.getNodeValue();
            } catch (Exception ex) {
                // here, either the root element doesn't have a text node as a
                // first child,
                // or there is no first child at all - in any case, try
                // converting null
                nodeVal = null;
            }

            Object obj = XMLConversionManager.getDefaultXMLManager().convertObject(nodeVal, referenceClass);
            XMLRoot xmlRoot = new XMLRoot();
            xmlRoot.setObject(obj);            
			String lName = xmlRow.getDOM().getLocalName();
			if (lName == null) {
				lName = xmlRow.getDOM().getNodeName();
			}
            xmlRoot.setLocalName(lName);
            xmlRoot.setNamespaceURI(xmlRow.getDOM().getNamespaceURI());
            return xmlRoot;
        }

        // for XMLObjectReferenceMappings we need a non-shared cache, so
        // try and get a Unit Of Work from the XMLContext
        AbstractSession session = xmlContext.getReadSession(referenceClass);
        
        ReadObjectQuery query = new ReadObjectQuery();
        query.setReferenceClass(referenceClass);
        query.setSession(session);

        XMLDescriptor descriptor = (XMLDescriptor) session.getDescriptor(referenceClass);
        if (descriptor == null) {
            throw XMLMarshalException.descriptorNotFoundInProject(referenceClass.getName());
        }

        xmlRow.setUnmarshaller(unmarshaller);
        xmlRow.setDocPresPolicy(xmlContext.getDocumentPreservationPolicy(session));
        XMLObjectBuilder objectBuilder = (XMLObjectBuilder) descriptor.getObjectBuilder();
        Object object = objectBuilder.buildObject(query, xmlRow, null);
        
        // resolve mapping references
        unmarshaller.resolveReferences(session);

        String elementNamespaceUri = xmlRow.getDOM().getNamespaceURI();
        String elementLocalName = xmlRow.getDOM().getLocalName();
        if (elementLocalName == null) {
            elementLocalName = xmlRow.getDOM().getNodeName();
        }
        String elementPrefix = xmlRow.getDOM().getPrefix();
        return descriptor.wrapObjectInXMLRoot(object, elementNamespaceUri, elementLocalName, elementPrefix);
    }
}
