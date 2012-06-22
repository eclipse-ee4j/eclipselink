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

import org.eclipse.persistence.exceptions.XMLMarshalException;
import org.eclipse.persistence.internal.oxm.record.XMLReader;
import org.eclipse.persistence.mappings.converters.Converter;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.attachment.XMLAttachmentUnmarshaller;
import org.eclipse.persistence.oxm.mappings.XMLBinaryDataCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLBinaryDataMapping;
import org.eclipse.persistence.oxm.mappings.converters.XMLConverter;
import org.eclipse.persistence.oxm.record.UnmarshalRecord;
import org.eclipse.persistence.mappings.DatabaseMapping;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * INTERNAL:
 * <p><b>Purpose:</b>This class is a content handler that specifically handles the "Include" element in an mtom style
 * attachment. 
 * @author  mmacivor
 */

public class XMLBinaryAttachmentHandler extends UnmarshalRecord {
    UnmarshalRecord record;
    DatabaseMapping mapping;
    String c_id = null;
    Converter converter;
    NodeValue nodeValue;
    boolean isCollection = false;
    
    private static final String INCLUDE_ELEMENT_NAME = "Include";
    private static final String HREF_ATTRIBUTE_NAME = "href";

    public XMLBinaryAttachmentHandler(UnmarshalRecord unmarshalRecord, NodeValue nodeValue, DatabaseMapping mapping, Converter converter, boolean isCollection) {
        super(null);
        record = unmarshalRecord;
        this.mapping = mapping;
        this.nodeValue = nodeValue;
        this.converter = converter;
        this.isCollection = isCollection;
    }
    
    @Override
    public void characters(char[] ch, int offset, int length) throws SAXException {
        //we don't care about characters here. Probably a whitespace
    }
    @Override
    public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
        if(XMLConstants.XOP_URL.equals(namespaceURI) && (INCLUDE_ELEMENT_NAME.equals(localName) || INCLUDE_ELEMENT_NAME.equals(qName))) {
            this.c_id = atts.getValue("", HREF_ATTRIBUTE_NAME);
        } else {
            //Return control to the UnmarshalRecord
            XMLReader xmlReader = record.getXMLReader();
            xmlReader.setContentHandler(record);
            xmlReader.setLexicalHandler(record);
            record.startElement(namespaceURI, localName, qName, atts);
        }
    }

    @Override
    public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
    	XMLField xmlField = null;
    	if(isCollection) {
            xmlField = (XMLField)((XMLBinaryDataCollectionMapping)mapping).getField();
        } else {
            xmlField = (XMLField)((XMLBinaryDataMapping)mapping).getField();
        }
        if(XMLConstants.XOP_URL.equals(namespaceURI) && (INCLUDE_ELEMENT_NAME.equals(localName) || INCLUDE_ELEMENT_NAME.equals(qName))) {
            //Get the attachment and set it in the object.
            XMLAttachmentUnmarshaller attachmentUnmarshaller = record.getUnmarshaller().getAttachmentUnmarshaller();
            Object data = null;
            Class attributeClassification = null;
            if(isCollection) {
            	attributeClassification = ((XMLBinaryDataCollectionMapping)mapping).getAttributeElementClass();
            } else {
                attributeClassification = mapping.getAttributeClassification();
            }
            if(attachmentUnmarshaller == null) {
                //if there's no attachment unmarshaller, it isn't possible to retrieve
                //the attachment. Throw an exception.
                throw XMLMarshalException.noAttachmentUnmarshallerSet(this.c_id);
            }
            if(attributeClassification.equals(XMLBinaryDataHelper.getXMLBinaryDataHelper().DATA_HANDLER)) {
                data = attachmentUnmarshaller.getAttachmentAsDataHandler(this.c_id);
            } else {
                data = attachmentUnmarshaller.getAttachmentAsByteArray(this.c_id);
            }
            data = XMLBinaryDataHelper.getXMLBinaryDataHelper().convertObject(data, mapping.getAttributeClassification(), record.getSession());
            if (this.converter != null) {
                Converter converter = this.converter;
                if (converter instanceof XMLConverter) {
                    data = ((XMLConverter) converter).convertDataValueToObjectValue(data, record.getSession(), record.getUnmarshaller());
                } else {
                    data = converter.convertDataValueToObjectValue(data, record.getSession());
                }
            }
            //check for collection case
            if (isCollection) {
                if(data != null) {
                    record.addAttributeValue((ContainerValue)nodeValue, data);
                }
            } else {
                record.setAttributeValue(data, mapping);
            }
            //Return control to the UnmarshalRecord
            if(!xmlField.isSelfField()){
                XMLReader xmlReader = record.getXMLReader();
                xmlReader.setContentHandler(record);
                xmlReader.setLexicalHandler(record);
            }
        } else {
            if(!xmlField.isSelfField()){
                //Return control to the parent record
                XMLReader xmlReader = record.getXMLReader();
                xmlReader.setContentHandler(record);
                xmlReader.setLexicalHandler(record);
                record.endElement(namespaceURI, localName, qName);
            }
        }
    }

    public void processingInstruction(String target, String data) throws SAXException {
    }

    public String getCID() {
        return this.c_id;
    }

    public Object getObjectValueFromDataHandler(DataHandler handler, Class cls) {
        return XMLBinaryDataHelper.getXMLBinaryDataHelper().convertObject(handler, cls, record.getSession());
    }

}
