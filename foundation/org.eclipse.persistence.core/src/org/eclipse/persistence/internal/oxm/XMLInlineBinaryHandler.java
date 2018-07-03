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
// mmacivor - June 05/2008 - 1.0 - Initial implementation
package org.eclipse.persistence.internal.oxm;

import org.xml.sax.SAXException;
import org.eclipse.persistence.internal.core.queries.CoreContainerPolicy;
import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.internal.oxm.mappings.BinaryDataCollectionMapping;
import org.eclipse.persistence.internal.oxm.mappings.BinaryDataMapping;
import org.eclipse.persistence.internal.oxm.mappings.Field;
import org.eclipse.persistence.internal.oxm.mappings.Mapping;
import org.eclipse.persistence.internal.oxm.mappings.XMLConverterMapping;
import org.eclipse.persistence.internal.oxm.record.UnmarshalRecord;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.mappings.nullpolicy.AbstractNullPolicy;

public class XMLInlineBinaryHandler extends org.eclipse.persistence.internal.oxm.record.UnmarshalRecordImpl {
    NodeValue nodeValue;
    Mapping mapping;
    boolean isCollection = false;
    XMLConverterMapping converter;
    UnmarshalRecord parent;
    CharSequence characters;

    public XMLInlineBinaryHandler(UnmarshalRecord parent, NodeValue nodeValue, Mapping mapping, XMLConverterMapping converter, boolean isCollection) {
        super(null);
        this.nodeValue = nodeValue;
        this.isCollection = isCollection;
        this.mapping = mapping;
        this.parent = parent;
        this.converter = converter;
        this.setUnmarshaller((XMLUnmarshaller) parent.getUnmarshaller());
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
       Field field = null;
       Object value = this.getCharacters();

       boolean isHex = Constants.HEX_BINARY_QNAME.equals(((XMLField)this.mapping.getField()).getSchemaType());

       Class attributeClassification = null;
       AbstractNullPolicy nullPolicy;
       boolean isSwaRef = false;
       CoreContainerPolicy cp = null;
       if(isCollection) {
           isSwaRef = ((BinaryDataCollectionMapping)mapping).isSwaRef();
           field = (Field)((BinaryDataCollectionMapping)mapping).getField();
           attributeClassification =((BinaryDataCollectionMapping)mapping).getAttributeElementClass();
           nullPolicy =((BinaryDataCollectionMapping)mapping).getNullPolicy();
           cp = ((BinaryDataCollectionMapping)mapping).getContainerPolicy();
       } else {
           isSwaRef = ((BinaryDataMapping)mapping).isSwaRef();
           field = (Field)((BinaryDataMapping)mapping).getField();
           attributeClassification =((BinaryDataMapping)mapping).getAttributeClassification();
           nullPolicy =((BinaryDataMapping)mapping).getNullPolicy();

       }

       if (isSwaRef && (parent.getUnmarshaller().getAttachmentUnmarshaller() != null)) {
           if(attributeClassification != null && attributeClassification == XMLBinaryDataHelper.getXMLBinaryDataHelper().DATA_HANDLER) {
               value = parent.getUnmarshaller().getAttachmentUnmarshaller().getAttachmentAsDataHandler(value.toString());
           } else {
               value = parent.getUnmarshaller().getAttachmentUnmarshaller().getAttachmentAsByteArray(value.toString());
           }
       } else {
           Object valueFromReader = this.parent.getXMLReader().getValue(getCharacters(), attributeClassification);

           if(parent.isNil() && parent.getXMLReader().isNullRepresentedByXsiNil(nullPolicy)){
               value = null;
               isCollection = isCollection && parent.getXMLReader().isInCollection();
           }
           else{
               if(null != valueFromReader) {
                   value = valueFromReader;
               } else {
                   String valueString = value.toString();
                   if(valueString.length() == 0 && nullPolicy.isNullRepresentedByEmptyNode()){
                       value = null;
                   }else{
                       if(field.usesSingleNode()){
                           if(isHex) {
                               value = parent.getConversionManager().convertHexBinaryListToByteArrayList(valueString, cp, parent.getSession());
                           } else {
                               value = parent.getConversionManager().convertSchemaBase64ListToByteArrayList(valueString, cp, parent.getSession());
                           }
                       }else{
                           if(isHex) {
                               value = parent.getConversionManager().convertObject(valueString, ClassConstants.APBYTE, Constants.HEX_BINARY_QNAME);
                           } else {
                               value = parent.getConversionManager().convertSchemaBase64ToByteArray(valueString);
                           }
                       }
                   }
               }
               value = XMLBinaryDataHelper.getXMLBinaryDataHelper().convertObject(value, attributeClassification, parent.getSession(), cp);
           }
       }
        value = converter.convertDataValueToObjectValue(value, parent.getSession(), parent.getUnmarshaller());
       if(isCollection) {
           parent.addAttributeValue((ContainerValue)nodeValue, value);
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
