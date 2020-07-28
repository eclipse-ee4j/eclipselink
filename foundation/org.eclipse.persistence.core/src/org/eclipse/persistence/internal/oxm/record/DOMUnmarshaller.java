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
package org.eclipse.persistence.internal.oxm.record;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.validation.Schema;

import org.eclipse.persistence.exceptions.XMLMarshalException;
import org.eclipse.persistence.internal.core.helper.CoreClassConstants;
import org.eclipse.persistence.internal.core.sessions.CoreAbstractSession;
import org.eclipse.persistence.internal.oxm.Constants;
import org.eclipse.persistence.internal.oxm.Root;
import org.eclipse.persistence.internal.oxm.XMLConversionManager;
import org.eclipse.persistence.internal.oxm.XMLObjectBuilder;
import org.eclipse.persistence.internal.oxm.XPathFragment;
import org.eclipse.persistence.internal.oxm.mappings.Descriptor;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.oxm.XMLRoot;
import org.eclipse.persistence.oxm.XMLUnmarshaller;
import org.eclipse.persistence.oxm.record.DOMRecord;
import org.eclipse.persistence.platform.xml.SAXDocumentBuilder;
import org.eclipse.persistence.platform.xml.XMLParser;
import org.eclipse.persistence.platform.xml.XMLPlatform;
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
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

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
    private XMLUnmarshaller xmlUnmarshaller;
    private boolean isResultAlwaysXMLRoot;
    private boolean disableSecureProcessing = false;
    private EntityResolver entityResolver;
    private ErrorHandler errorHandler;
    private Map<String, Boolean> parserFeatures;
    private boolean isWhitespacePreserving;
    private int validationMode = XMLParser.NONVALIDATING;
    private Schema schema;
    private Object[] schemas;
    private boolean shouldReset = true;

    public DOMUnmarshaller(XMLUnmarshaller xmlUnmarshaller, Map<String, Boolean> parserFeatures) {
        super();
        this.parserFeatures = parserFeatures == null ? new HashMap<>() : parserFeatures;
        this.xmlUnmarshaller = xmlUnmarshaller;
    }

    private XMLParser getParser() {
        if (parser == null || shouldReset) {
            XMLPlatform xmlPlatform = XMLPlatformFactory.getInstance().getXMLPlatform();
            xmlPlatform.setDisableSecureProcessing(isSecureProcessingDisabled());
            parser = xmlPlatform.newXMLParser(parserFeatures);
            parser.setNamespaceAware(true);
            if (errorHandler != null) {
                parser.setErrorHandler(errorHandler);
            }
            if (entityResolver != null) {
                parser.setEntityResolver(entityResolver);
            }
            if (schemas != null) {
                try {
                    parser.setXMLSchemas(schemas);
                } catch (XMLPlatformException e) {
                    throw XMLMarshalException.errorSettingSchemas(e, schemas);
                }
            }
            if (schema != null) {
                parser.setXMLSchema(schema);
            }
            parser.setValidationMode(validationMode);
            parser.setWhitespacePreserving(isWhitespacePreserving);
            shouldReset = false;
        }
        return parser;
    }

    @Override
    public EntityResolver getEntityResolver() {
        return entityResolver;
    }

    @Override
    public void setEntityResolver(EntityResolver entityResolver) {
        this.entityResolver = entityResolver;
        if (parser != null) {
            parser.setEntityResolver(entityResolver);
        }
    }

    @Override
    public ErrorHandler getErrorHandler() {
        return errorHandler;
    }

    @Override
    public void setErrorHandler(ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
        if (parser != null) {
            parser.setErrorHandler(errorHandler);
        }
    }

    @Override
    public int getValidationMode() {
        return validationMode;
    }

    @Override
    public void setValidationMode(int validationMode) {
        this.validationMode = validationMode;
        if (parser != null) {
            parser.setValidationMode(validationMode);
        }
    }

    @Override
    public void setWhitespacePreserving(boolean isWhitespacePreserving) {
        this.isWhitespacePreserving = isWhitespacePreserving;
        if (parser != null) {
            parser.setWhitespacePreserving(isWhitespacePreserving);
        }
    }

    @Override
    public void setSchemas(Object[] schemas) {
        this.schemas = schemas;
        if (parser != null) {
            try {
                parser.setXMLSchemas(schemas);
            } catch (XMLPlatformException e) {
                throw XMLMarshalException.errorSettingSchemas(e, schemas);
            }
        }
    }

    @Override
    public void setSchema(Schema schema) {
        this.schema = schema;
        if (parser != null) {
            parser.setXMLSchema(schema);
        }
    }

    @Override
    public Schema getSchema() {
        return schema;
    }

    @Override
    public Object unmarshal(File file) {
        return unmarshal(file, null);
    }

    @Override
    public Object unmarshal(File file, Class clazz) {
        if(!xmlUnmarshaller.isApplicationXML()){
            throw XMLMarshalException.unsupportedMediaTypeForPlatform();
        }
        try {
            Document document = null;
            document = getParser().parse(file);
            return xmlToObject(new DOMRecord(document), clazz);
        } catch (XMLPlatformException e) {
            throw XMLMarshalException.unmarshalException(e);
        } finally {
            xmlUnmarshaller.getStringBuffer().reset();
        }
    }

    @Override
    public Object unmarshal(InputStream inputStream) {
        return unmarshal(inputStream, null);
    }

    @Override
    public Object unmarshal(InputStream inputStream, Class clazz) {
        return unmarshal(new InputSource(inputStream), clazz);
    }

    @Override
    public Object unmarshal(InputSource inputSource) {
        return unmarshal(inputSource, null);
    }

    @Override
    public Object unmarshal(InputSource inputSource, Class clazz) {
        if(!xmlUnmarshaller.isApplicationXML()){
            throw XMLMarshalException.unsupportedMediaTypeForPlatform();
        }
        try {
            Document document = null;
            document = getParser().parse(inputSource);
            return xmlToObject(new DOMRecord(document), clazz);
        } catch (XMLPlatformException e) {
            throw XMLMarshalException.unmarshalException(e);
        } finally {
            xmlUnmarshaller.getStringBuffer().reset();
        }
    }

    @Override
    public Object unmarshal(Node node) {
        return unmarshal(node, null);
    }

    @Override
    public Object unmarshal(Node node, Class clazz) {
        if(!xmlUnmarshaller.isApplicationXML()){
            throw XMLMarshalException.unsupportedMediaTypeForPlatform();
        }
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
        return xmlToObject(new DOMRecord(element), clazz);
    }

    @Override
    public Object unmarshal(Reader reader) {
        return unmarshal(reader, null);
    }

    @Override
    public Object unmarshal(Reader reader, Class clazz) {
        return unmarshal(new InputSource(reader), clazz);
    }

    @Override
    public Object unmarshal(Source source) {
        return unmarshal(source, null);
    }

    @Override
    public Object unmarshal(Source source, Class clazz) {
        if(!xmlUnmarshaller.isApplicationXML()){
            throw XMLMarshalException.unsupportedMediaTypeForPlatform();
        }
        try {
            Document document = null;
            document = getParser().parse(source);
            return xmlToObject(new DOMRecord(document), clazz);
        } catch (XMLPlatformException e) {
            throw XMLMarshalException.unmarshalException(e);
        } finally {
            xmlUnmarshaller.getStringBuffer().reset();
        }
    }

    @Override
    public Object unmarshal(URL url) {
        return unmarshal(url, null);
    }

    @Override
    public Object unmarshal(URL url, Class clazz) {
        if(!xmlUnmarshaller.isApplicationXML()){
            throw XMLMarshalException.unsupportedMediaTypeForPlatform();
        }
        try {
            Document document = null;
            document = getParser().parse(url);
            return xmlToObject(new DOMRecord(document), clazz);
        } catch (XMLPlatformException e) {
            throw XMLMarshalException.unmarshalException(e);
        } finally {
            xmlUnmarshaller.getStringBuffer().reset();
        }
    }

    @Override
    public Object unmarshal(XMLReader xmlReader, InputSource inputSource) {
       return unmarshal(xmlReader, inputSource, null);
    }

    @Override
    public Object unmarshal(XMLReader xmlReader, InputSource inputSource, Class clazz) {
        if(!xmlUnmarshaller.isApplicationXML()){
            throw XMLMarshalException.unsupportedMediaTypeForPlatform();
        }
        try {
            SAXDocumentBuilder saxDocumentBuilder = new SAXDocumentBuilder();
            xmlReader.setContentHandler(saxDocumentBuilder);
            xmlReader.parse(inputSource);
            return xmlToObject(new DOMRecord(saxDocumentBuilder.getDocument()), clazz);
        } catch(IOException e) {
            throw XMLMarshalException.unmarshalException(e);
        } catch(SAXException e) {
            throw XMLMarshalException.unmarshalException(e);
        } finally {
            xmlUnmarshaller.getStringBuffer().reset();
        }
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
    public Object xmlToObject(DOMRecord xmlRecord) throws XMLMarshalException {
         return xmlToObject(xmlRecord, null);
    }

    /**
     * INTERNAL: Convert the Oracle XMLDocument to the reference-class.
     */
    public Object xmlToObject(DOMRecord xmlRow, Class referenceClass) throws XMLMarshalException {
        try{
            //Try to get the Encoding and Version from DOM3 APIs if available
            String xmlEncoding = "UTF-8";
            String xmlVersion = "1.0";

            try {
                xmlEncoding = xmlRow.getDocument().getXmlEncoding() != null ? xmlRow.getDocument().getXmlEncoding() : xmlEncoding;
                xmlVersion = xmlRow.getDocument().getXmlVersion() != null ? xmlRow.getDocument().getXmlVersion() : xmlVersion;
            } catch (Exception ex) {
                //if the methods aren't available, then just use the default values
            }

            XMLContext xmlContext = xmlUnmarshaller.getXMLContext();

            // handle case where the reference class is a primitive wrapper - in
            // this case, we need to use the conversion manager to convert the
            // node's value to the primitive wrapper class, then create,
            // populate and return an XMLRoot
            if (referenceClass != null && (XMLConversionManager.getDefaultJavaTypes().get(referenceClass) != null ||CoreClassConstants.XML_GREGORIAN_CALENDAR.isAssignableFrom(referenceClass)
                    ||CoreClassConstants.DURATION.isAssignableFrom(referenceClass))){
                // we're assuming that since we're unmarshalling to a primitive
                // wrapper, the root element has a single text node
                Object nodeVal;
                try {
                    Text rootTxt = (Text) xmlRow.getDOM().getFirstChild();
                    nodeVal = rootTxt.getNodeValue();
                } catch (Exception ex) {
                    // here, either the root element doesn't have a text node as a
                    // first child, or there is no first child at all - in any case,
                    // try converting null
                    nodeVal = null;
                }

                Object obj = ((XMLConversionManager) xmlContext.getSession().getDatasourcePlatform().getConversionManager()).convertObject(nodeVal, referenceClass);
                Root xmlRoot = new XMLRoot();
                xmlRoot.setObject(obj);
                String lName = xmlRow.getDOM().getLocalName();
                if (lName == null) {
                    lName = xmlRow.getDOM().getNodeName();
                }
                xmlRoot.setLocalName(lName);
                xmlRoot.setNamespaceURI(xmlRow.getDOM().getNamespaceURI());
                xmlRoot.setEncoding(xmlEncoding);
                xmlRoot.setVersion(xmlVersion);
                return xmlRoot;
            }
            Descriptor descriptor = null;
            CoreAbstractSession readSession = null;
            boolean shouldWrap = true;
            if(referenceClass == null){
                QName rootQName = new QName(xmlRow.getNamespaceURI(), xmlRow.getLocalName());
                descriptor = xmlContext.getDescriptor(rootQName);
                if (null == descriptor) {
                    String type = ((Element) xmlRow.getDOM()).getAttributeNS(javax.xml.XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, "type");
                    if (null != type) {
                         XPathFragment typeFragment = new XPathFragment(type);
                         String namespaceURI = xmlRow.resolveNamespacePrefix(typeFragment.getPrefix());
                         typeFragment.setNamespaceURI(namespaceURI);
                         descriptor = xmlContext.getDescriptorByGlobalType(typeFragment);
                     }
                 }else{
                     if(null != descriptor.getDefaultRootElementField() && !descriptor.isResultAlwaysXMLRoot() && !xmlUnmarshaller.isResultAlwaysXMLRoot()){
                         String descLocalName = descriptor.getDefaultRootElementField().getXPathFragment().getLocalName();
                         String localName = xmlRow.getDOM().getLocalName();
                         if (localName == null) {
                           localName = xmlRow.getDOM().getNodeName();
                           }
                     String namespaceURI = xmlRow.getDOM().getNamespaceURI();
                         if( descLocalName != null && descLocalName.equals(localName) ){
                             String descUri = descriptor.getDefaultRootElementField().getXPathFragment().getNamespaceURI();
                             if((namespaceURI == null && descUri == null ) || (namespaceURI !=null &&namespaceURI.length() == 0 && descUri == null ) || (namespaceURI != null && namespaceURI.equals(descUri))){
                                 //found a descriptor based on root element then know we won't need to wrap in an XMLRoot
                                shouldWrap = false;
                             }
                         }
                     }
                 }

                 if (null == descriptor) {
                     throw XMLMarshalException.noDescriptorWithMatchingRootElement(rootQName.toString());
                 }else{
                     readSession = xmlContext.getSession(descriptor.getJavaClass());
                 }
            } else {
                // for XMLObjectReferenceMappings we need a non-shared cache, so
                // try and get a Unit Of Work from the XMLContext
                readSession = xmlContext.getSession(referenceClass);
                descriptor = (Descriptor)readSession.getDescriptor(referenceClass);
                if (descriptor == null) {
                    throw XMLMarshalException.descriptorNotFoundInProject(referenceClass.getName());
                }
            }

            Object object = null;
            if(null == xmlRow.getDOM().getAttributes().getNamedItemNS(javax.xml.XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, Constants.SCHEMA_NIL_ATTRIBUTE)) {
                xmlRow.setUnmarshaller(xmlUnmarshaller);
                xmlRow.setDocPresPolicy(xmlContext.getDocumentPreservationPolicy((AbstractSession) readSession));
                XMLObjectBuilder objectBuilder = (XMLObjectBuilder) descriptor.getObjectBuilder();

                ReadObjectQuery query = new ReadObjectQuery();
                query.setReferenceClass(referenceClass);
                query.setSession((AbstractSession) readSession);
                object = objectBuilder.buildObject(query, xmlRow, null);

                // resolve mapping references
                xmlRow.resolveReferences(readSession, xmlUnmarshaller.getIDResolver());
            }

            String elementNamespaceUri = xmlRow.getDOM().getNamespaceURI();
            String elementLocalName = xmlRow.getDOM().getLocalName();
            if (elementLocalName == null) {
                elementLocalName = xmlRow.getDOM().getNodeName();
            }
            String elementPrefix = xmlRow.getDOM().getPrefix();
            if(shouldWrap || descriptor.isResultAlwaysXMLRoot() || isResultAlwaysXMLRoot){
                return descriptor.wrapObjectInXMLRoot(object, elementNamespaceUri, elementLocalName, elementPrefix, xmlEncoding, xmlVersion, this.isResultAlwaysXMLRoot, true, xmlUnmarshaller);
            }else{
                return object;
            }
        }finally{
            xmlUnmarshaller.getStringBuffer().reset();
        }
    }

    @Override
    public boolean isResultAlwaysXMLRoot() {
        return this.isResultAlwaysXMLRoot;
    }

    @Override
    public void setResultAlwaysXMLRoot(boolean alwaysReturnRoot) {
        this.isResultAlwaysXMLRoot = alwaysReturnRoot;
    }

    @Override
    public void mediaTypeChanged() {
       //do nothing
    }

    @Override
    public final boolean isSecureProcessingDisabled() {
        return disableSecureProcessing;
    }

    @Override
    public final void setDisableSecureProcessing(boolean disableSecureProcessing) {
        shouldReset = this.disableSecureProcessing ^ disableSecureProcessing;
        this.disableSecureProcessing = disableSecureProcessing;
    }
}
