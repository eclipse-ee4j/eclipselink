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
* mmacivor - June 05/2008 - 1.0 - Initial implementation
******************************************************************************/
package org.eclipse.persistence.internal.oxm;

import org.xml.sax.SAXException;

import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.converters.Converter;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.mappings.XMLBinaryDataCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLBinaryDataMapping;
import org.eclipse.persistence.oxm.mappings.converters.XMLConverter;
import org.eclipse.persistence.oxm.record.UnmarshalRecord;



public class XMLInlineBinaryHandler extends UnmarshalRecord {
    NodeValue nodeValue;
    DatabaseMapping mapping;
    boolean isCollection = false;
    Converter converter;
    UnmarshalRecord parent;
    CharSequence characters;
    
    public XMLInlineBinaryHandler(UnmarshalRecord parent, NodeValue nodeValue, DatabaseMapping mapping, Converter converter, boolean isCollection) {
        super(null);
        this.nodeValue = nodeValue;
        this.isCollection = isCollection;
        this.mapping = mapping;
        this.parent = parent;
        this.converter = converter;
        this.setUnmarshaller(parent.getUnmarshaller());
    }

    @Override
    public CharSequence getCharacters() {
        if(null != characters) {
            return characters;
        }
        return super.getCharacters();
    }

    @Override
    public void characters(char[] ch, int offset, int length) throws SAXException {
        this.getStringBuffer().append(ch, offset, length);
    }
    
    @Override
    public void characters(CharSequence characters) throws SAXException {
        this.characters = characters;
    }

    @Override
    public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
       //Since we know this was a simple or empty element, we know that we only got a characters event and then this. Process the
       //text.
       XMLField field = null;
       Object value = this.getCharacters();

       Class attributeClassification = null;
       boolean isSwaRef = false;
       if(isCollection) {
           isSwaRef = ((XMLBinaryDataCollectionMapping)mapping).isSwaRef();
           field = (XMLField)((XMLBinaryDataCollectionMapping)mapping).getField();
           attributeClassification =((XMLBinaryDataCollectionMapping)mapping).getAttributeElementClass();
       } else {
           isSwaRef = ((XMLBinaryDataMapping)mapping).isSwaRef();
           field = (XMLField)((XMLBinaryDataMapping)mapping).getField();
           attributeClassification =((XMLBinaryDataMapping)mapping).getAttributeClassification();
       }
           
       if (isSwaRef && (parent.getUnmarshaller().getAttachmentUnmarshaller() != null)) {    	  
           if(attributeClassification != null && attributeClassification == XMLBinaryDataHelper.getXMLBinaryDataHelper().DATA_HANDLER) {
               value = parent.getUnmarshaller().getAttachmentUnmarshaller().getAttachmentAsDataHandler(value.toString());
           } else {
               value = parent.getUnmarshaller().getAttachmentUnmarshaller().getAttachmentAsByteArray(value.toString());
           }
           if (converter != null) {
               if (converter instanceof XMLConverter) {
                   value = ((XMLConverter)converter).convertDataValueToObjectValue(value, parent.getSession(), parent.getUnmarshaller());
               } else {
                   value = converter.convertDataValueToObjectValue(value, parent.getSession());
               }
           }
       } else {
           Object valueFromReader = this.parent.getXMLReader().getValue(getCharacters(), attributeClassification);
           if(null != valueFromReader) {
               value = valueFromReader;
           } else {
               value = XMLConversionManager.getDefaultXMLManager().convertSchemaBase64ToByteArray(value.toString());
           } 
           if (converter != null) {
               if (converter instanceof XMLConverter) {
                   value = ((XMLConverter)converter).convertDataValueToObjectValue(value, parent.getSession(), parent.getUnmarshaller());
               } else {
                   value = converter.convertDataValueToObjectValue(value, parent.getSession());
               }
           } else {
               value = XMLBinaryDataHelper.getXMLBinaryDataHelper().convertObject(value, attributeClassification, parent.getSession());
           }
       }
    
       if(isCollection) {
           if(value != null) {
               parent.addAttributeValue((ContainerValue)nodeValue, value);
           }
       } else {
           parent.setAttributeValue(value, mapping);
       }
       
       if(!field.isSelfField()){
           //Return control to the parent record
           parent.getXMLReader().setContentHandler(parent);
           parent.endElement(namespaceURI, localName, qName);       
       }
       resetStringBuffer();
   }

    @Override
    public void resetStringBuffer() {
        super.resetStringBuffer();
        characters = null;
    }

}