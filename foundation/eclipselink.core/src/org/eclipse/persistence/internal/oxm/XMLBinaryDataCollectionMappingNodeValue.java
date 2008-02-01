/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.oxm;

import javax.activation.DataHandler;
import javax.xml.namespace.QName;
import org.xml.sax.Attributes;
import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.internal.oxm.record.MarshalContext;
import org.eclipse.persistence.internal.oxm.record.ObjectMarshalContext;
import org.eclipse.persistence.internal.queries.ContainerPolicy;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.mappings.converters.Converter;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.XMLMarshaller;
import org.eclipse.persistence.oxm.mappings.XMLBinaryDataCollectionMapping;
import org.eclipse.persistence.oxm.mappings.converters.XMLConverter;
import org.eclipse.persistence.oxm.record.MarshalRecord;
import org.eclipse.persistence.oxm.record.UnmarshalRecord;

/**
 * INTERNAL:
 * <p><b>Purpose</b>: This is how the XML Binary Data Collection Mapping is 
 * handled when used with the TreeObjectBuilder.</p>
 */

public class XMLBinaryDataCollectionMappingNodeValue extends NodeValue implements ContainerValue {

    private XMLBinaryDataCollectionMapping xmlBinaryDataCollectionMapping; 

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
    
    protected String getValueToWrite(QName schemaType, Object value) {
        return (String)XMLConversionManager.getDefaultXMLManager().convertObject(value, ClassConstants.STRING, schemaType);
    }

    public boolean isOwningNode(XPathFragment xPathFragment) {
        return xPathFragment.getNextFragment() == null || xPathFragment.getNextFragment().isAttribute();
    }

    public boolean marshal(XPathFragment xPathFragment, MarshalRecord marshalRecord, Object object, AbstractSession session, NamespaceResolver namespaceResolver) {
        if (xmlBinaryDataCollectionMapping.isReadOnly()) {
            return false;
        }
        Object collection = xmlBinaryDataCollectionMapping.getAttributeAccessor().getAttributeValueFromObject(object);
        if (null == collection) {
            return false;
        }
        String xopPrefix = null;
        // If the field's resolver is non-null and has an entry for XOP, 
        // use it - otherwise, create a new resolver, set the XOP entry, 
        // on it, and use it instead.
        // We do this to avoid setting the XOP namespace declaration on
        // a given field or descriptor's resolver, as it is only required
        // on the current element
        if (namespaceResolver != null) {
            xopPrefix = namespaceResolver.resolveNamespaceURI(XMLConstants.XOP_URL);
        }
        if (xopPrefix == null || namespaceResolver == null) {
            xopPrefix = XMLConstants.XOP_PREFIX;
            marshalRecord.getNamespaceResolver().put(xopPrefix, XMLConstants.XOP_URL);
        }
        
        ContainerPolicy cp = getContainerPolicy();
        Object iterator = cp.iteratorFor(collection);
        while(cp.hasNext(iterator)) {
            Object objectValue = cp.next(iterator, session);
            marshalSingleValue(xPathFragment, marshalRecord, object, objectValue, session, namespaceResolver, ObjectMarshalContext.getInstance());
                }
        
        marshalRecord.getNamespaceResolver().removeNamespace(XMLConstants.XOP_PREFIX);
        return true;
    }
    

    public boolean startElement(XPathFragment xPathFragment, UnmarshalRecord unmarshalRecord, Attributes atts) {
        XMLField xmlField = (XMLField)xmlBinaryDataCollectionMapping.getField();
        XPathFragment lastFragment = xmlField.getLastXPathFragment();
        if(!xmlBinaryDataCollectionMapping.isSwaRef() && !xmlBinaryDataCollectionMapping.shouldInlineBinaryData() && !lastFragment.isAttribute()) {
            //check to see if this is an attachment
            
            if(unmarshalRecord.getUnmarshaller().getAttachmentUnmarshaller() != null && unmarshalRecord.getUnmarshaller().getAttachmentUnmarshaller().isXOPPackage()) {
                //set a new content handler to deal with the Include element's event.
                XMLBinaryAttachmentHandler handler = new XMLBinaryAttachmentHandler(unmarshalRecord, this, this.xmlBinaryDataCollectionMapping);
                unmarshalRecord.getXMLReader().setContentHandler(handler);
            } 
        } else if(lastFragment.isAttribute()) {
            //handle swaRef and inline attribute cases here:
            String value = atts.getValue(lastFragment.getNamespaceURI(), lastFragment.getLocalName());
            Object fieldValue = null;
            if(xmlBinaryDataCollectionMapping.isSwaRef()) {
                if(unmarshalRecord.getUnmarshaller().getAttachmentUnmarshaller() != null) {
                    if(xmlBinaryDataCollectionMapping.getAttributeClassification() == XMLBinaryDataHelper.getXMLBinaryDataHelper().DATA_HANDLER) {
                        fieldValue = unmarshalRecord.getUnmarshaller().getAttachmentUnmarshaller().getAttachmentAsDataHandler(value);
                    } else {
                        fieldValue = unmarshalRecord.getUnmarshaller().getAttachmentUnmarshaller().getAttachmentAsByteArray(value);
                    }
                    xmlBinaryDataCollectionMapping.setAttributeValueInObject(unmarshalRecord.getCurrentObject(), XMLBinaryDataHelper.getXMLBinaryDataHelper().convertObject(fieldValue, xmlBinaryDataCollectionMapping.getAttributeClassification()));
                }
            } else {
                //value should be base64 binary string
                fieldValue = XMLConversionManager.getDefaultXMLManager().convertSchemaBase64ToByteArray(value);
                xmlBinaryDataCollectionMapping.setAttributeValueInObject(unmarshalRecord.getCurrentObject(), XMLBinaryDataHelper.getXMLBinaryDataHelper().convertObject(fieldValue, xmlBinaryDataCollectionMapping.getAttributeClassification()));
            }
        }
        return true;
    }


    public void endElement(XPathFragment xPathFragment, UnmarshalRecord unmarshalRecord) {
        if(!xmlBinaryDataCollectionMapping.shouldInlineBinaryData() && !xmlBinaryDataCollectionMapping.isSwaRef() && unmarshalRecord.getUnmarshaller().getAttachmentUnmarshaller() != null && unmarshalRecord.getUnmarshaller().getAttachmentUnmarshaller().isXOPPackage()) {
            //handled in start element
            unmarshalRecord.resetStringBuffer();
            return;
        }

        Object value = unmarshalRecord.getStringBuffer().toString();
        unmarshalRecord.resetStringBuffer();

        if(xmlBinaryDataCollectionMapping.isSwaRef() && unmarshalRecord.getUnmarshaller().getAttachmentUnmarshaller() != null) {
            if(xmlBinaryDataCollectionMapping.getAttributeClassification() == XMLBinaryDataHelper.getXMLBinaryDataHelper().DATA_HANDLER) {
                value = unmarshalRecord.getUnmarshaller().getAttachmentUnmarshaller().getAttachmentAsDataHandler((String)value);
            } else {
                value = unmarshalRecord.getUnmarshaller().getAttachmentUnmarshaller().getAttachmentAsByteArray((String)value);
            }
            if(xmlBinaryDataCollectionMapping.getValueConverter() != null) {
                Converter converter = xmlBinaryDataCollectionMapping.getValueConverter();
                if(converter instanceof XMLConverter) {
                    value = ((XMLConverter)converter).convertDataValueToObjectValue(value, unmarshalRecord.getSession(), unmarshalRecord.getUnmarshaller());
                } else {
                    value = converter.convertDataValueToObjectValue(value, unmarshalRecord.getSession());
                }
            }
        } else {
            value = XMLConversionManager.getDefaultXMLManager().convertSchemaBase64ToByteArray(value);
            if(xmlBinaryDataCollectionMapping.getValueConverter() != null) {
                Converter converter = xmlBinaryDataCollectionMapping.getValueConverter();
                if(converter instanceof XMLConverter) {
                    value = ((XMLConverter)converter).convertDataValueToObjectValue(value, unmarshalRecord.getSession(), unmarshalRecord.getUnmarshaller());
                } else {
                    value = converter.convertDataValueToObjectValue(value, unmarshalRecord.getSession());
                }
            }            
        }
        value = XMLBinaryDataHelper.getXMLBinaryDataHelper().convertObject(value, xmlBinaryDataCollectionMapping.getAttributeClassification());
        if(value != null) {
            Object container = unmarshalRecord.getContainerInstance(this);
            unmarshalRecord.addAttributeValue(this, value);
        }

    }

    public DataHandler getDataHandlerForObjectValue(Object obj, Class classification) {
        if(classification == DataHandler.class) {
            return (DataHandler)obj;
        }
        return null;
    }   
    
    public boolean isContainerValue() {
        return true;
    }
    public void marshalSingleValue(XPathFragment xPathFragment, MarshalRecord marshalRecord, Object object, Object objectValue, AbstractSession session, NamespaceResolver namespaceResolver, MarshalContext marshalContext) {
        boolean addDeclaration = false;
        boolean removePrefix = false;
        String xopPrefix = null;
        if(namespaceResolver != null) {
            xopPrefix = namespaceResolver.resolveNamespaceURI(XMLConstants.XOP_URL);
        }
        if(xopPrefix == null) {
            //check for it in the MarshalRecord's NamespaceResolver
            addDeclaration = true;
            xopPrefix = marshalRecord.getNamespaceResolver().resolveNamespaceURI(XMLConstants.XOP_URL);
            if(xopPrefix == null) {
                //if it's still null, add it, and make a note to remove it later
                removePrefix = true;
                xopPrefix = XMLConstants.XOP_PREFIX;
                marshalRecord.getNamespaceResolver().put(xopPrefix, XMLConstants.XOP_URL);
            }
            namespaceResolver = marshalRecord.getNamespaceResolver();
        }
        
        XMLMarshaller marshaller = marshalRecord.getMarshaller();
        if(xmlBinaryDataCollectionMapping.getValueConverter() != null) {
            Converter converter = xmlBinaryDataCollectionMapping.getValueConverter();
            if(converter instanceof XMLConverter) {
                objectValue = ((XMLConverter)converter).convertObjectValueToDataValue(objectValue, session, marshaller);
            } else {
                objectValue = converter.convertObjectValueToDataValue(objectValue, session);
            }
        }
        XPathFragment groupingFragment = marshalRecord.openStartGroupingElements(namespaceResolver);
        marshalRecord.closeStartGroupingElements(groupingFragment);
        marshalRecord.openStartElement(xPathFragment, namespaceResolver);
        marshalRecord.closeStartElement();
    
        if(xmlBinaryDataCollectionMapping.isSwaRef() && marshaller.getAttachmentMarshaller() != null) {
            //object value should be a DataHandler
            String c_id = null;
            if (xmlBinaryDataCollectionMapping.getAttributeClassification() == XMLBinaryDataHelper.getXMLBinaryDataHelper().DATA_HANDLER) {
                c_id = marshaller.getAttachmentMarshaller().addSwaRefAttachment((DataHandler)objectValue);
            } else {
                XMLBinaryDataHelper.EncodedData data = XMLBinaryDataHelper.getXMLBinaryDataHelper().getBytesForBinaryValue(//
                        objectValue, marshaller, xmlBinaryDataCollectionMapping.getMimeType(object));
                        byte[] bytes = data.getData();
                        c_id = marshaller.getAttachmentMarshaller().addSwaRefAttachment(bytes, 0, bytes.length);
                
            }
            marshalRecord.characters(c_id);
        } else {
            if(marshaller.getAttachmentMarshaller() != null && marshaller.getAttachmentMarshaller().isXOPPackage() && !xmlBinaryDataCollectionMapping.shouldInlineBinaryData()) {
                XPathFragment lastFrag = ((XMLField)xmlBinaryDataCollectionMapping.getField()).getLastXPathFragment();
                String c_id = "";
                if(objectValue.getClass() == ClassConstants.APBYTE) {
                    byte[] bytes = (byte[])objectValue;
                    c_id = marshaller.getAttachmentMarshaller().addMtomAttachment(bytes, 0, bytes.length, lastFrag.getLocalName(), lastFrag.getNamespaceURI(), null);
                } else if(xmlBinaryDataCollectionMapping.getAttributeClassification() == XMLBinaryDataHelper.getXMLBinaryDataHelper().DATA_HANDLER) {
                    c_id = marshaller.getAttachmentMarshaller().addMtomAttachment((DataHandler)objectValue, lastFrag.getLocalName(), lastFrag.getNamespaceURI());    
                } else {
                    XMLBinaryDataHelper.EncodedData data = XMLBinaryDataHelper.getXMLBinaryDataHelper().getBytesForBinaryValue(//
                            objectValue, marshaller, xmlBinaryDataCollectionMapping.getMimeTypePolicy().getMimeType(object));
                    byte[] bytes = data.getData();
                    c_id = marshaller.getAttachmentMarshaller().addMtomAttachment(bytes, 0, bytes.length, //
                            data.getMimeType(), lastFrag.getLocalName(), lastFrag.getNamespaceURI());
                }
                XPathFragment xopInclude = new XPathFragment(xopPrefix + ":Include");
                xopInclude.setNamespaceURI(XMLConstants.XOP_URL);
                marshalRecord.openStartElement(xopInclude, namespaceResolver);
                marshalRecord.attribute("", "href", "href", c_id);
                if (addDeclaration) {
                    marshalRecord.attribute(XMLConstants.XMLNS_URL, xopPrefix, XMLConstants.XMLNS + ":" + xopPrefix, XMLConstants.XOP_URL);
                    //marshalRecord.attribute(new XPathFragment("@xmlns:" + xopPrefix), namespaceResolver, XMLConstants.XOP_URL);
                }
                marshalRecord.closeStartElement();
                marshalRecord.endElement(xPathFragment, namespaceResolver);
                //marshal as an attachment.
            } else {
                String value = "";
                if(objectValue.getClass() == ClassConstants.ABYTE || objectValue.getClass() == ClassConstants.APBYTE) {
                    value = getValueToWrite(((XMLField)xmlBinaryDataCollectionMapping.getField()).getSchemaType(), objectValue);
                } else {
                    byte[] bytes = XMLBinaryDataHelper.getXMLBinaryDataHelper().getBytesForBinaryValue(//
                            objectValue, marshaller,xmlBinaryDataCollectionMapping.getMimeTypePolicy().getMimeType(object)).getData();
                    value = getValueToWrite(((XMLField)xmlBinaryDataCollectionMapping.getField()).getSchemaType(), bytes);
                }
                marshalRecord.characters(value);
            }
        }
        marshalRecord.endElement(xPathFragment, namespaceResolver);
        if(removePrefix) {
            marshalRecord.getNamespaceResolver().removeNamespace(XMLConstants.XOP_PREFIX);
        }
    }

    public XMLBinaryDataCollectionMapping getMapping() {
        return xmlBinaryDataCollectionMapping;
    }

}
