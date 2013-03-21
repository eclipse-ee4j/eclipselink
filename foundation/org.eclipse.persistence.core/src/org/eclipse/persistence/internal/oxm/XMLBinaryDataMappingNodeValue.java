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
package org.eclipse.persistence.internal.oxm;

import javax.activation.DataHandler;
import javax.xml.namespace.QName;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.eclipse.persistence.core.sessions.CoreSession;
import org.eclipse.persistence.exceptions.XMLMarshalException;
import org.eclipse.persistence.internal.core.helper.CoreClassConstants;
import org.eclipse.persistence.internal.core.sessions.CoreAbstractSession;
import org.eclipse.persistence.internal.oxm.XMLBinaryDataHelper;
import org.eclipse.persistence.internal.oxm.mappings.BinaryDataMapping;
import org.eclipse.persistence.internal.oxm.mappings.Field;
import org.eclipse.persistence.internal.oxm.record.BinaryDataUnmarshalRecord;
import org.eclipse.persistence.internal.oxm.record.MarshalContext;
import org.eclipse.persistence.internal.oxm.record.MarshalRecord;
import org.eclipse.persistence.internal.oxm.record.ObjectMarshalContext;
import org.eclipse.persistence.internal.oxm.record.UnmarshalRecord;
import org.eclipse.persistence.internal.oxm.record.XMLReader;
import org.eclipse.persistence.internal.oxm.record.deferred.BinaryMappingContentHandler;

/**
 * INTERNAL:
 * <p><b>Purpose</b>: This is how the XML Binary Data Mapping is handled when
 * used with the TreeObjectBuilder.</p>
 * @author  mmacivor
 */

public class XMLBinaryDataMappingNodeValue extends NodeValue implements NullCapableValue {
    private BinaryDataMapping xmlBinaryDataMapping;

    protected String getValueToWrite(QName schemaType, Object value, CoreAbstractSession session) {
        return (String) ((XMLConversionManager) session.getDatasourcePlatform().getConversionManager()).convertObject(value, CoreClassConstants.STRING, schemaType);
    }

    public boolean isOwningNode(XPathFragment xPathFragment) {
        return (xPathFragment.getNextFragment() == null) || xPathFragment.isAttribute();
    }

    public XMLBinaryDataMappingNodeValue(BinaryDataMapping mapping) {
        this.xmlBinaryDataMapping = mapping;
    }

    public boolean marshal(XPathFragment xPathFragment, MarshalRecord marshalRecord, Object object, CoreAbstractSession session, NamespaceResolver namespaceResolver) {
        return marshal(xPathFragment, marshalRecord, object, session, namespaceResolver, ObjectMarshalContext.getInstance(), null);
    }

    public boolean marshal(XPathFragment xPathFragment, MarshalRecord marshalRecord, Object object, CoreAbstractSession session, NamespaceResolver namespaceResolver, MarshalContext marshalContext, XPathFragment rootFragment) {
        if (xmlBinaryDataMapping.isReadOnly()) {
            return false;
        }
        Object objectValue = marshalContext.getAttributeValue(object, xmlBinaryDataMapping);
        return this.marshalSingleValue(xPathFragment, marshalRecord, object, objectValue, session, namespaceResolver, marshalContext, rootFragment);
    }

    public boolean marshalSingleValue(XPathFragment xPathFragment, MarshalRecord marshalRecord, Object object, Object objectValue, CoreAbstractSession session, NamespaceResolver namespaceResolver, MarshalContext marshalContext) {
        return marshalSingleValue(xPathFragment, marshalRecord, object, objectValue, session, namespaceResolver, marshalContext, null);
    }
    
    public boolean marshalSingleValue(XPathFragment xPathFragment, MarshalRecord marshalRecord, Object object, Object objectValue,CoreAbstractSession session, NamespaceResolver namespaceResolver, MarshalContext marshalContext, XPathFragment rootFragment) {
        XPathFragment xmlRootFrag = null;

        if (objectValue instanceof Root) {
        	Root xmlRoot = (Root) objectValue;
            xmlRootFrag = new XPathFragment();
            if (xmlRoot.getNamespaceURI() != null && !xmlRoot.getNamespaceURI().equals(namespaceResolver.getDefaultNamespaceURI())) {
                String prefix = namespaceResolver.resolveNamespaceURI(xmlRoot.getNamespaceURI());
                xmlRootFrag.setXPath(prefix + Constants.COLON + xmlRoot.getLocalName());
                xmlRootFrag.setNamespaceURI(xmlRoot.getNamespaceURI());
            }else{
            	xmlRootFrag.setXPath(xmlRoot.getLocalName());
            	if(xmlRoot.getNamespaceURI() != null && xmlRoot.getNamespaceURI().length() > 0) {
            	    xmlRootFrag.setNamespaceURI(xmlRoot.getNamespaceURI());
            	}
            }
        }

        Marshaller marshaller = marshalRecord.getMarshaller();
        objectValue = xmlBinaryDataMapping.convertObjectValueToDataValue(objectValue, session, marshaller);
        XPathFragment groupingFragment = marshalRecord.openStartGroupingElements(namespaceResolver);
        if(xPathFragment.isAttribute()){
            if (objectValue == null) {
                marshalRecord.closeStartGroupingElements(groupingFragment);
                return true;
            }
        }else {
            marshalRecord.closeStartGroupingElements(groupingFragment);
            XPathFragment elementFragment = xPathFragment;
            if(xmlRootFrag != null) {
                elementFragment = xmlRootFrag;
            }
            if (objectValue == null) {
                XPathNode holderXPathNode = new XPathNode();
                holderXPathNode.setXPathFragment(elementFragment);
                marshalRecord.addGroupingElement(holderXPathNode);
                boolean returnVal = xmlBinaryDataMapping.getNullPolicy().directMarshal(xPathFragment, marshalRecord, object, session, namespaceResolver);
                if(returnVal){
                    marshalRecord.endElement(elementFragment, namespaceResolver);
                }
                marshalRecord.removeGroupingElement(holderXPathNode);
                return returnVal;
              }else if(!xPathFragment.isSelfFragment){
                marshalRecord.openStartElement(elementFragment, namespaceResolver);
                marshalRecord.closeStartElement();
            } 
        }


       
        
        // figure out CID or bytes 
        String c_id = null;
        byte[] bytes = null;
        String mimeType = this.xmlBinaryDataMapping.getMimeType(object);
        String attachmentType = mimeType;
        if(mimeType == null) {
            mimeType = "";
            attachmentType = "application/octet-stream";
        }
        if (xmlBinaryDataMapping.isSwaRef() && (marshaller.getAttachmentMarshaller() != null)) {
            //object value should be a DataHandler
            if (xmlBinaryDataMapping.getAttributeClassification() == XMLBinaryDataHelper.getXMLBinaryDataHelper().DATA_HANDLER) {
                c_id = marshaller.getAttachmentMarshaller().addSwaRefAttachment((DataHandler) objectValue);
            } else {
                XMLBinaryDataHelper.EncodedData data = XMLBinaryDataHelper.getXMLBinaryDataHelper().getBytesForBinaryValue(//
                        objectValue, marshaller, xmlBinaryDataMapping.getMimeType(object));
                bytes = data.getData();
                c_id = marshaller.getAttachmentMarshaller().addSwaRefAttachment(bytes, 0, bytes.length);
            }
        } else if (marshalRecord.isXOPPackage() && !xmlBinaryDataMapping.shouldInlineBinaryData()) {
            XPathFragment lastFrag = ((Field) xmlBinaryDataMapping.getField()).getLastXPathFragment();
            if(xmlRootFrag != null) {
                lastFrag = xmlRootFrag;
            }
            String localName = null;
            String namespaceUri = null;
            if(rootFragment != null) {
                localName = rootFragment.getLocalName();
                namespaceUri = rootFragment.getNamespaceURI();
            }
            if(!lastFrag.isSelfFragment) {
                localName = lastFrag.getLocalName();
                namespaceUri = lastFrag.getNamespaceURI();
            }
            if (objectValue.getClass() == CoreClassConstants.APBYTE) {
                bytes = (byte[]) objectValue;
                c_id = marshaller.getAttachmentMarshaller().addMtomAttachment(bytes, 0, bytes.length, attachmentType, localName, namespaceUri);
            } else if (xmlBinaryDataMapping.getAttributeClassification() == XMLBinaryDataHelper.getXMLBinaryDataHelper().DATA_HANDLER) {
                c_id = marshaller.getAttachmentMarshaller().addMtomAttachment((DataHandler) objectValue, localName, namespaceUri);
            } else {
                XMLBinaryDataHelper.EncodedData data = XMLBinaryDataHelper.getXMLBinaryDataHelper().getBytesForBinaryValue(//
                        objectValue, marshaller, xmlBinaryDataMapping.getMimeType(object));
                bytes = data.getData();
                c_id = marshaller.getAttachmentMarshaller().addMtomAttachment(bytes, 0, bytes.length, data.getMimeType(), localName, namespaceUri);
            }
        }

        // handle attributes
        if (xPathFragment.isAttribute()) {
            // if the CID is null there's nothing to write out
            if (c_id != null) {
                marshalRecord.attribute(xPathFragment, namespaceResolver, c_id);
            }else {
                String value = getValueToWrite(((Field) xmlBinaryDataMapping.getField()).getSchemaType(), objectValue, session);
                marshalRecord.attribute(xPathFragment, namespaceResolver, value);
            }
            marshalRecord.closeStartGroupingElements(groupingFragment);
            return true;
        }
        
        if (xmlBinaryDataMapping.isSwaRef() && (marshaller.getAttachmentMarshaller() != null)) {
            if(c_id != null) {
                marshalRecord.characters(c_id);
            } else {
                marshalRecord.characters(((Field) xmlBinaryDataMapping.getField()).getSchemaType(), objectValue, mimeType, false);
            }
        } else {
            if (marshalRecord.isXOPPackage() && !xmlBinaryDataMapping.shouldInlineBinaryData()) {
                if(c_id == null) {
                    marshalRecord.characters(((Field) xmlBinaryDataMapping.getField()).getSchemaType(), objectValue, mimeType, false);
                } else {
                    String xopPrefix = null;
                    // If the field's resolver is non-null and has an entry for XOP, 
                    // use it - otherwise, create a new resolver, set the XOP entry, 
                    // on it, and use it instead.
                    // We do this to avoid setting the XOP namespace declaration on
                    // a given field or descriptor's resolver, as it is only required
                    // on the current element

                    // 20061023: handle NPE on null NSR
                    if (namespaceResolver != null) {
                        xopPrefix = namespaceResolver.resolveNamespaceURI(Constants.XOP_URL);
                    }
                    boolean addDeclaration = false;
                    if (xopPrefix == null || namespaceResolver == null) {
                        addDeclaration = true;
                        xopPrefix = Constants.XOP_PREFIX;
                        namespaceResolver = new org.eclipse.persistence.oxm.NamespaceResolver();
                        namespaceResolver.put(xopPrefix, Constants.XOP_URL);
                    }
                    XPathFragment xopInclude = new XPathFragment(xopPrefix + ":Include");
                    xopInclude.setNamespaceURI(Constants.XOP_URL);
                    marshalRecord.openStartElement(xopInclude, namespaceResolver);
                    marshalRecord.attribute(Constants.EMPTY_STRING, "href", "href", c_id);
                    if (addDeclaration) {                        
                        marshalRecord.namespaceDeclaration(xopPrefix,  Constants.XOP_URL);
                    }
                    marshalRecord.closeStartElement();
                    marshalRecord.endElement(xopInclude, namespaceResolver);
                    //marshal as an attachment
                }
            } else {
                marshalRecord.characters(((Field)xmlBinaryDataMapping.getField()).getSchemaType(), objectValue, mimeType, false);
            }
        }
        
        if(!xPathFragment.isSelfFragment()){
        	marshalRecord.endElement(xPathFragment, namespaceResolver);
        }
        return true;
    }
    
    public boolean startElement(XPathFragment xPathFragment, UnmarshalRecord unmarshalRecord, Attributes atts) {
        try {
            
            unmarshalRecord.removeNullCapableValue(this);
            Field xmlField = (Field) xmlBinaryDataMapping.getField();
            XPathFragment lastFragment = xmlField.getLastXPathFragment();
            BinaryMappingContentHandler handler = new BinaryMappingContentHandler(unmarshalRecord, this, this.xmlBinaryDataMapping);
            String qnameString = xPathFragment.getLocalName();
            if (xPathFragment.getPrefix() != null) {
                qnameString = xPathFragment.getPrefix() + Constants.COLON + qnameString;
            }
            handler.startElement(xPathFragment.getNamespaceURI(), xPathFragment.getLocalName(), qnameString, atts);
            XMLReader xmlReader = unmarshalRecord.getXMLReader();
            xmlReader.setContentHandler(handler);
            xmlReader.setLexicalHandler(handler);
            return true;
        } catch(SAXException ex) {
            throw XMLMarshalException.unmarshalException(ex);
        }
    }

    public void endElement(XPathFragment xPathFragment, UnmarshalRecord unmarshalRecord) {
        unmarshalRecord.resetStringBuffer();
    }

    /**
     * Handle swaRef and inline attribute cases.
     */
    public void attribute(UnmarshalRecord unmarshalRecord, String URI, String localName, String value) {
        unmarshalRecord.removeNullCapableValue(this);
        Field xmlField = (Field) xmlBinaryDataMapping.getField();
        XPathFragment lastFragment = xmlField.getLastXPathFragment();
        
        Object fieldValue = null;
        if (xmlBinaryDataMapping.isSwaRef()) {
            if (unmarshalRecord.getUnmarshaller().getAttachmentUnmarshaller() != null) {
                if (xmlBinaryDataMapping.getAttributeClassification() == XMLBinaryDataHelper.getXMLBinaryDataHelper().DATA_HANDLER) {
                    fieldValue = unmarshalRecord.getUnmarshaller().getAttachmentUnmarshaller().getAttachmentAsDataHandler(value);
                } else {
                    fieldValue = unmarshalRecord.getUnmarshaller().getAttachmentUnmarshaller().getAttachmentAsByteArray(value);
                }
                xmlBinaryDataMapping.setAttributeValueInObject(unmarshalRecord.getCurrentObject(), XMLBinaryDataHelper.getXMLBinaryDataHelper().convertObject(fieldValue, xmlBinaryDataMapping.getAttributeClassification(), unmarshalRecord.getSession()));
            }
        } else {
            // value should be base64 binary string
            fieldValue = ((XMLConversionManager) unmarshalRecord.getSession().getDatasourcePlatform().getConversionManager()).convertSchemaBase64ToByteArray(value);
            xmlBinaryDataMapping.setAttributeValueInObject(unmarshalRecord.getCurrentObject(), XMLBinaryDataHelper.getXMLBinaryDataHelper().convertObject(fieldValue, xmlBinaryDataMapping.getAttributeClassification(), unmarshalRecord.getSession()));
        }
    }

    public void setNullValue(Object object, CoreSession session) {
        Object value = xmlBinaryDataMapping.getObjectValue(null, session);
        xmlBinaryDataMapping.setAttributeValueInObject(object, value);
    }

    public boolean isNullCapableValue() {
        return xmlBinaryDataMapping.getNullPolicy().getIsSetPerformedForAbsentNode();
    }

    public DataHandler getDataHandlerForObjectValue(Object obj, Class classification) {
        if (classification == DataHandler.class) {
            return (DataHandler) obj;
        }
        return null;
    }
    
    public BinaryDataMapping getMapping() {
        return this.xmlBinaryDataMapping;
    }
    
    public UnmarshalRecord buildSelfRecord(UnmarshalRecord unmarshalRecord, Attributes atts) {   
        unmarshalRecord.removeNullCapableValue(this);
        BinaryDataUnmarshalRecord newRecord = new BinaryDataUnmarshalRecord(null, unmarshalRecord, this, xmlBinaryDataMapping);
        return newRecord;     	
    }
    
    public void endSelfNodeValue(UnmarshalRecord unmarshalRecord, UnmarshalRecord selfRecord, Attributes attributes) {
        unmarshalRecord.resetStringBuffer();
    }

}
