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
package org.eclipse.persistence.internal.oxm;

import javax.activation.DataHandler;
import javax.xml.namespace.QName;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.eclipse.persistence.exceptions.XMLMarshalException;
import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.internal.oxm.record.MarshalContext;
import org.eclipse.persistence.internal.oxm.record.ObjectMarshalContext;
import org.eclipse.persistence.internal.oxm.record.XMLReader;
import org.eclipse.persistence.internal.oxm.record.deferred.BinaryMappingContentHandler;
import org.eclipse.persistence.internal.queries.ContainerPolicy;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.mappings.converters.Converter;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.XMLMarshaller;
import org.eclipse.persistence.oxm.mappings.XMLBinaryDataCollectionMapping;
import org.eclipse.persistence.oxm.mappings.converters.XMLConverter;
import org.eclipse.persistence.oxm.mappings.nullpolicy.AbstractNullPolicy;
import org.eclipse.persistence.oxm.mappings.nullpolicy.XMLNullRepresentationType;
import org.eclipse.persistence.oxm.record.MarshalRecord;
import org.eclipse.persistence.oxm.record.UnmarshalRecord;

/**
 * INTERNAL:
 * <p><b>Purpose</b>: This is how the XML Binary Data Collection Mapping is 
 * handled when used with the TreeObjectBuilder.</p>
 */

public class XMLBinaryDataCollectionMappingNodeValue extends MappingNodeValue implements ContainerValue {

    private XMLBinaryDataCollectionMapping xmlBinaryDataCollectionMapping;
    private int index = -1;

    public XMLBinaryDataCollectionMappingNodeValue(XMLBinaryDataCollectionMapping mapping) {
        this.xmlBinaryDataCollectionMapping = mapping;
    }

    public void setContainerInstance(Object object, Object containerInstance) {
        xmlBinaryDataCollectionMapping.setAttributeValueInObject(object, containerInstance);
    }

    public Object getContainerInstance() {
        return getContainerPolicy().containerInstance();
    }

    public ContainerPolicy getContainerPolicy() {
        return xmlBinaryDataCollectionMapping.getContainerPolicy();
    }

    protected String getValueToWrite(QName schemaType, Object value, AbstractSession session) {
        return (String) ((XMLConversionManager) session.getDatasourcePlatform().getConversionManager()).convertObject(value, ClassConstants.STRING, schemaType);
    }

    public boolean isOwningNode(XPathFragment xPathFragment) {
        return xPathFragment.getNextFragment() == null || xPathFragment.isAttribute();
    }

    public boolean marshal(XPathFragment xPathFragment, MarshalRecord marshalRecord, Object object, AbstractSession session, NamespaceResolver namespaceResolver) {
        if (xmlBinaryDataCollectionMapping.isReadOnly()) {
            return false;
        }
        Object collection = xmlBinaryDataCollectionMapping.getAttributeAccessor().getAttributeValueFromObject(object);
        if (null == collection) {
            AbstractNullPolicy wrapperNP = xmlBinaryDataCollectionMapping.getWrapperNullPolicy();
            if (wrapperNP != null && wrapperNP.getMarshalNullRepresentation().equals(XMLNullRepresentationType.XSI_NIL)) {
                marshalRecord.nilSimple(namespaceResolver);
                return true;
            } else {
                return false;
            }
        }
  
        ContainerPolicy cp = getContainerPolicy();
        Object iterator = cp.iteratorFor(collection);
        if (!cp.hasNext(iterator)) {
        	return marshalRecord.emptyCollection(xPathFragment, namespaceResolver, xmlBinaryDataCollectionMapping.getWrapperNullPolicy() != null);
        }
        
        XPathFragment groupingFragment = marshalRecord.openStartGroupingElements(namespaceResolver);
        marshalRecord.closeStartGroupingElements(groupingFragment);
          
        marshalRecord.startCollection();
        while (cp.hasNext(iterator)) {
            Object objectValue = cp.next(iterator, session);
          marshalSingleValue(xPathFragment, marshalRecord, object, objectValue, session, namespaceResolver, ObjectMarshalContext.getInstance());
        }
        	marshalRecord.endCollection();
        return true;
    }

    public boolean startElement(XPathFragment xPathFragment, UnmarshalRecord unmarshalRecord, Attributes atts) {
        try {
            XMLField xmlField = (XMLField)xmlBinaryDataCollectionMapping.getField();
            XPathFragment lastFragment = xmlField.getLastXPathFragment();
            if(!lastFragment.isAttribute()) {
                 //set a new content handler to deal with the Include element's event.
                 BinaryMappingContentHandler handler = new BinaryMappingContentHandler(unmarshalRecord, this, this.xmlBinaryDataCollectionMapping);
                 String qnameString = xPathFragment.getLocalName();
                 if (xPathFragment.getPrefix() != null) {
                     qnameString = xPathFragment.getPrefix() + XMLConstants.COLON + qnameString;
                 }
                 handler.startElement(xPathFragment.getNamespaceURI(), xPathFragment.getLocalName(), qnameString, atts);
                 XMLReader xmlReader = unmarshalRecord.getXMLReader();
                 xmlReader.setContentHandler(handler);
                 xmlReader.setLexicalHandler(handler);
            } else if (lastFragment.isAttribute()) {
                //handle swaRef and inline attribute cases here:
                String value = atts.getValue(lastFragment.getNamespaceURI(), lastFragment.getLocalName());
                Object fieldValue = null;
                if (xmlBinaryDataCollectionMapping.isSwaRef()) {
                    if (unmarshalRecord.getUnmarshaller().getAttachmentUnmarshaller() != null) {
                        if (xmlBinaryDataCollectionMapping.getAttributeClassification() == XMLBinaryDataHelper.getXMLBinaryDataHelper().DATA_HANDLER) {
                            fieldValue = unmarshalRecord.getUnmarshaller().getAttachmentUnmarshaller().getAttachmentAsDataHandler(value);
                        } else {
                            fieldValue = unmarshalRecord.getUnmarshaller().getAttachmentUnmarshaller().getAttachmentAsByteArray(value);
                        }
                        xmlBinaryDataCollectionMapping.setAttributeValueInObject(unmarshalRecord.getCurrentObject(), XMLBinaryDataHelper.getXMLBinaryDataHelper().convertObject(fieldValue, xmlBinaryDataCollectionMapping.getAttributeClassification(),
                                unmarshalRecord.getSession()));
                    }
                } else {
                    //value should be base64 binary string
                    fieldValue = ((XMLConversionManager) unmarshalRecord.getSession().getDatasourcePlatform().getConversionManager()).convertSchemaBase64ToByteArray(value);
                    xmlBinaryDataCollectionMapping.setAttributeValueInObject(unmarshalRecord.getCurrentObject(), XMLBinaryDataHelper.getXMLBinaryDataHelper().convertObject(fieldValue, xmlBinaryDataCollectionMapping.getAttributeClassification(),
                            unmarshalRecord.getSession()));
                }
            }
            return true;
        } catch(SAXException ex) {
            throw XMLMarshalException.unmarshalException(ex);
        }
    }

    public void endElement(XPathFragment xPathFragment, UnmarshalRecord unmarshalRecord) {
        unmarshalRecord.resetStringBuffer();
    }

    public void endElement(XPathFragment xPathFragment, UnmarshalRecord unmarshalRecord, Object container) {
        this.endElement(xPathFragment, unmarshalRecord);
    }

    public DataHandler getDataHandlerForObjectValue(Object obj, Class classification) {
        if (classification == DataHandler.class) {
            return (DataHandler) obj;
        }
        return null;
    }

    public boolean isContainerValue() {
        return true;
    }

	@Override
	public boolean marshalSingleValue(XPathFragment xPathFragment, MarshalRecord marshalRecord, Object object, Object objectValue, AbstractSession session, NamespaceResolver namespaceResolver, MarshalContext marshalContext) {
        if(objectValue == null) {
            return false;
        }
        String mimeType = this.xmlBinaryDataCollectionMapping.getMimeType(object);
        if(mimeType == null) {
            mimeType = XMLConstants.EMPTY_STRING;
        }
        XMLMarshaller marshaller = marshalRecord.getMarshaller();
        if (xmlBinaryDataCollectionMapping.getValueConverter() != null) {
            Converter converter = xmlBinaryDataCollectionMapping.getValueConverter();
            if (converter instanceof XMLConverter) {
                objectValue = ((XMLConverter) converter).convertObjectValueToDataValue(objectValue, session, marshaller);
            } else {
                objectValue = converter.convertObjectValueToDataValue(objectValue, session);
            }
        }        
        marshalRecord.openStartElement(xPathFragment, namespaceResolver);
        marshalRecord.closeStartElement();

        if (xmlBinaryDataCollectionMapping.isSwaRef() && marshaller.getAttachmentMarshaller() != null) {
            //object value should be a DataHandler
            String c_id = null;
            byte[] bytes = null;
            if (xmlBinaryDataCollectionMapping.getAttributeElementClass() == XMLBinaryDataHelper.getXMLBinaryDataHelper().DATA_HANDLER) {
                c_id = marshaller.getAttachmentMarshaller().addSwaRefAttachment((DataHandler) objectValue);
            } else {
                XMLBinaryDataHelper.EncodedData data = XMLBinaryDataHelper.getXMLBinaryDataHelper().getBytesForBinaryValue(//
                        objectValue, marshaller, xmlBinaryDataCollectionMapping.getMimeType(object));
                bytes = data.getData();
                c_id = marshaller.getAttachmentMarshaller().addSwaRefAttachment(bytes, 0, bytes.length);

            }
            if(c_id != null) {
                marshalRecord.characters(c_id);
            } else {
                marshalRecord.characters(((XMLField) xmlBinaryDataCollectionMapping.getField()).getSchemaType(), objectValue, mimeType, false);
            }
        } else {
            if (marshalRecord.isXOPPackage() && !xmlBinaryDataCollectionMapping.shouldInlineBinaryData()) {
                XPathFragment lastFrag = ((XMLField) xmlBinaryDataCollectionMapping.getField()).getLastXPathFragment();
                String c_id = XMLConstants.EMPTY_STRING;
                byte[] bytes = null;
                if (objectValue.getClass() == ClassConstants.APBYTE) {
                    bytes = (byte[]) objectValue;
                    c_id = marshaller.getAttachmentMarshaller().addMtomAttachment(bytes, 0, bytes.length, this.xmlBinaryDataCollectionMapping.getMimeType(object), lastFrag.getLocalName(), lastFrag.getNamespaceURI());
                } else if (xmlBinaryDataCollectionMapping.getAttributeElementClass() == XMLBinaryDataHelper.getXMLBinaryDataHelper().DATA_HANDLER) {
                    c_id = marshaller.getAttachmentMarshaller().addMtomAttachment((DataHandler) objectValue, lastFrag.getLocalName(), lastFrag.getNamespaceURI());
                } else {
                    XMLBinaryDataHelper.EncodedData data = XMLBinaryDataHelper.getXMLBinaryDataHelper().getBytesForBinaryValue(//
                            objectValue, marshaller, xmlBinaryDataCollectionMapping.getMimeTypePolicy().getMimeType(object));
                    bytes = data.getData();
                    c_id = marshaller.getAttachmentMarshaller().addMtomAttachment(bytes, 0, bytes.length, //
                            data.getMimeType(), lastFrag.getLocalName(), lastFrag.getNamespaceURI());
                }
                if(c_id == null) {
                    marshalRecord.characters(((XMLField) xmlBinaryDataCollectionMapping.getField()).getSchemaType(), objectValue, mimeType, false);
                } else {
                	
                    boolean addDeclaration = false;
                    String xopPrefix = null;

                    if(marshalRecord.getNamespaceResolver() != null){
                        xopPrefix = marshalRecord.getNamespaceResolver().resolveNamespaceURI(XMLConstants.XOP_URL);
                    }
                    if (xopPrefix == null) {
                        addDeclaration = true;            
                        xopPrefix = marshalRecord.getNamespaceResolver().generatePrefix(XMLConstants.XOP_PREFIX);
                        marshalRecord.getNamespaceResolver().put(xopPrefix, XMLConstants.XOP_URL);
                        namespaceResolver = marshalRecord.getNamespaceResolver();
                    }
                	
                	
                    XPathFragment xopInclude = new XPathFragment(xopPrefix + ":Include");
                    xopInclude.setNamespaceURI(XMLConstants.XOP_URL);
                    marshalRecord.openStartElement(xopInclude, namespaceResolver);
                    marshalRecord.attribute(XMLConstants.EMPTY_STRING, "href", "href", c_id);
                    if (addDeclaration) {                    	                        
                        marshalRecord.namespaceDeclaration(xopPrefix,  XMLConstants.XOP_URL);
                    }
                    marshalRecord.closeStartElement();
                    marshalRecord.endElement(xPathFragment, namespaceResolver);
                    //marshal as an attachment.
                    if (addDeclaration) {
                        marshalRecord.getNamespaceResolver().removeNamespace(XMLConstants.XOP_PREFIX);
                    }
                }
            } else {
                marshalRecord.characters(((XMLField)xmlBinaryDataCollectionMapping.getField()).getSchemaType(), objectValue, mimeType, false);
            }
        }
        marshalRecord.endElement(xPathFragment, namespaceResolver);
        return true;
    }

    public XMLBinaryDataCollectionMapping getMapping() {
        return xmlBinaryDataCollectionMapping;
    }

    public boolean getReuseContainer() {
        return getMapping().getReuseContainer();
    }

    /**
     *  INTERNAL:
     *  Used to track the index of the corresponding containerInstance in the containerInstances Object[] on UnmarshalRecord 
     */  
    public void setIndex(int index){
    	this.index = index;
    }
    
    /**
     * INTERNAL:
     * Set to track the index of the corresponding containerInstance in the containerInstances Object[] on UnmarshalRecord
     * Set during TreeObjectBuilder initialization 
     */
    public int getIndex(){
    	return index;
    }

    /**
     * INTERNAL
     * Return true if an empty container should be set on the object if there
     * is no presence of the collection in the XML document.
     * @since EclipseLink 2.3.3
     */
    public boolean isDefaultEmptyContainer() {
        return getMapping().isDefaultEmptyContainer();
    }

}
