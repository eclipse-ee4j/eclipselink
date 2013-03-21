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
package org.eclipse.persistence.oxm.mappings;

import javax.activation.DataHandler;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.exceptions.XMLMarshalException;
import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.identitymaps.CacheKey;
import org.eclipse.persistence.internal.oxm.XMLBinaryDataHelper;
import org.eclipse.persistence.internal.oxm.XMLConversionManager;
import org.eclipse.persistence.internal.oxm.mappings.BinaryDataMapping;
import org.eclipse.persistence.internal.oxm.mappings.Field;
import org.eclipse.persistence.internal.queries.ContainerPolicy;
import org.eclipse.persistence.internal.queries.JoinedAttributeManager;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.mappings.AttributeAccessor;
import org.eclipse.persistence.mappings.converters.Converter;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.XMLMarshaller;
import org.eclipse.persistence.oxm.XMLUnmarshaller;
import org.eclipse.persistence.oxm.record.DOMRecord;
import org.eclipse.persistence.oxm.record.XMLRecord;
import org.eclipse.persistence.queries.ObjectBuildingQuery;
import org.eclipse.persistence.sessions.Session;

/**
 * <p><b>Purpose:</b>Provide a mapping for binary data that can be treated as either inline or as
 * an attachment.
 * <p><b>Responsibilities:</b><ul>
 * <li>Handle converting binary types (byte[], Image etc) to base64</li>
 * <li>Make callbacks to AttachmentMarshaller/AttachmentUnmarshaller</li>
 * <li>Write out approriate attachment information (xop:include) </li>
 * </ul>
 *  <p>XMLBinaryDataMapping represents a mapping of binary data in the object model
 *  to XML. This can either be written directly as inline binary data (base64) or 
 *  passed through as an MTOM or SWAREF attachment.
 *  <p>The following typed are allowable to be mapped using an XMLBinaryDataMapping:<ul>
 *  <li>java.awt.Image</li>
 *  <li>byte[]</li>
 *  <li>javax.activation.DataHandler</li>
 *  <li>javax.xml.transform.Source</li>
 *  <li>javax.mail.internet.MimeMultipart</li>
 *  </ul>
 *  <p><b>Setting the XPath</b>: TopLink XML mappings make use of XPath statements to find the relevant
 * data in an XML document.  The XPath statement is relative to the context node specified in the descriptor.
 * The XPath may contain path and positional information;  the last node in the XPath forms the local
 * node for the binary mapping. The XPath is specified on the mapping using the <code>setXPath</code>
 * method.
 * 
 * <p><b>Inline Binary Data</b>: Set this flag if you want to always inline binary data for this mapping.
 * This will disable consideration for attachment handling for this mapping.
 * 
 * <p><b>SwaRef</b>: Set this flag in order to specify that the target node of this mapping is of type
 * xs:swaref
 *   
 *  @see org.eclipse.persistence.oxm.attachment.XMLAttachmentMarshaller
 *  @see org.eclipse.persistence.oxm.attachment.XMLAttachmentUnmarshaller
 *  @see org.eclipse.persistence.oxm.mappings.MimeTypePolicy
 *  @since   TopLink 11.1.1.0.0g
 */
public class XMLBinaryDataMapping extends XMLDirectMapping implements BinaryDataMapping<AbstractSession, AttributeAccessor, ContainerPolicy, Converter, ClassDescriptor, DatabaseField, XMLMarshaller, MimeTypePolicy, Session, XMLUnmarshaller, XMLRecord> {
    private boolean shouldInlineBinaryData;
    private MimeTypePolicy mimeTypePolicy;
    private boolean isSwaRef;
    private static final String include = ":Include/@href";

    public XMLBinaryDataMapping() {
    }

    public boolean shouldInlineBinaryData() {
        return shouldInlineBinaryData;
    }

    public void setShouldInlineBinaryData(boolean b) {
        shouldInlineBinaryData = b;
    }

    /**
     * INTERNAL
     */
    public String getMimeType(Object anObject) {
        if (mimeTypePolicy == null) {
            return null;
        } else {
            return mimeTypePolicy.getMimeType(anObject);
        }
    }

    /**
     * INTERNAL
     */
    public String getMimeType() {
        if(mimeTypePolicy == null) {
            return null;
        }
        return mimeTypePolicy.getMimeType(null);
    }

    public MimeTypePolicy getMimeTypePolicy() {
        return mimeTypePolicy;
    }

    /**
     * Allow implementer to set the MimeTypePolicy class FixedMimeTypePolicy or AttributeMimeTypePolicy (dynamic)
     * @param aPolicy MimeTypePolicy
     */
    public void setMimeTypePolicy(MimeTypePolicy aPolicy) {
        mimeTypePolicy = aPolicy;
    }

    /**
     * Force mapping to set default FixedMimeTypePolicy using the MimeType string as argument
     * @param mimeTypeString
     */
    public void setMimeType(String mimeTypeString) {
        // use the following to set dynamically - mapping.setMimeTypePolicy(new FixedMimeTypePolicy(property.getMimeType()));
        mimeTypePolicy = new FixedMimeTypePolicy(mimeTypeString);
    }

    public boolean isSwaRef() {
        return isSwaRef;
    }

    public void setSwaRef(boolean swaRef) {
        isSwaRef = swaRef;
    }

    /**
     * Set the Mapping field name attribute to the given XPath String
     * @param xpathString String
     */
    public void setXPath(String xpathString) {
        setField(new XMLField(xpathString));
    }

    @Override
    public void writeFromObjectIntoRow(Object object, AbstractRecord row, AbstractSession session, WriteType writeType) {
        Object attributeValue = getAttributeValueFromObject(object);
        if (attributeValue == null) {
            XMLField field = (XMLField) getField();

            if(getNullPolicy() != null && !field.getXPathFragment().isSelfFragment()) {
               getNullPolicy().directMarshal((Field) this.getField(), (XMLRecord) row, object);
            }
            return;            
        }
        writeSingleValue(attributeValue, object, (XMLRecord) row, session);
    }

    public void writeSingleValue(Object attributeValue, Object parent, XMLRecord record, AbstractSession session) {
        XMLMarshaller marshaller = record.getMarshaller();
        attributeValue = convertObjectValueToDataValue(attributeValue, session, record.getMarshaller());
        XMLField field = (XMLField) getField();
        if (field.getLastXPathFragment().isAttribute()) {
            if (isSwaRef() && (marshaller.getAttachmentMarshaller() != null)) {
                //should be a DataHandler here
                try {
                    String value = null;
                    if (getAttributeClassification() == XMLBinaryDataHelper.getXMLBinaryDataHelper().DATA_HANDLER) {
                        value = marshaller.getAttachmentMarshaller().addSwaRefAttachment((DataHandler) attributeValue);
                    } else {
                        XMLBinaryDataHelper.EncodedData data = XMLBinaryDataHelper.getXMLBinaryDataHelper().getBytesForBinaryValue(//
                                attributeValue, marshaller, getMimeType(parent));
                        byte[] bytes = data.getData();
                        value = marshaller.getAttachmentMarshaller().addSwaRefAttachment(bytes, 0, bytes.length);

                    }
                    record.put(field, value);
                } catch (ClassCastException cce) {
                    throw XMLMarshalException.invalidSwaRefAttribute(getAttributeClassification().getName());
                }
            } else {
                //inline case
                XMLBinaryDataHelper.EncodedData data = XMLBinaryDataHelper.getXMLBinaryDataHelper().getBytesForBinaryValue(attributeValue, record.getMarshaller(), getMimeType(parent));
                String base64Value = ((XMLConversionManager) session.getDatasourcePlatform().getConversionManager()).buildBase64StringFromBytes(data.getData());
                record.put(field, base64Value);
            }
            return;
        }
        if (record.isXOPPackage() && !isSwaRef() && !shouldInlineBinaryData()) {
            //write as attachment
            String c_id = XMLConstants.EMPTY_STRING;
            byte[] bytes = null;
            String elementName = field.getLastXPathFragment().getLocalName();
            String namespaceUri = field.getLastXPathFragment().getNamespaceURI();
            if(field.getLastXPathFragment().isSelfFragment()) {
            	//If it's a self mapping, get the element from the DOM record
            	DOMRecord domRecord = (DOMRecord)record;
            	if(domRecord.getDOM().getNodeType() == Node.ELEMENT_NODE) {
            		elementName = domRecord.getDOM().getLocalName();
            		namespaceUri = domRecord.getDOM().getNamespaceURI();
            	}
            }
            if ((getAttributeClassification() == ClassConstants.ABYTE) || (getAttributeClassification() == ClassConstants.APBYTE)) {
                if (getAttributeClassification() == ClassConstants.ABYTE) {
                    attributeValue = ((XMLConversionManager) session.getDatasourcePlatform().getConversionManager()).convertObject(attributeValue, ClassConstants.APBYTE);
                }
                bytes = (byte[])attributeValue;
                c_id = marshaller.getAttachmentMarshaller().addMtomAttachment(//
                        bytes, 0,//
                        bytes.length,// 
                        this.getMimeType(parent),//
                        elementName,// 
                        namespaceUri);//
            } else if (getAttributeClassification() == XMLBinaryDataHelper.getXMLBinaryDataHelper().DATA_HANDLER) {
                c_id = marshaller.getAttachmentMarshaller().addMtomAttachment(//
                        (DataHandler) attributeValue, elementName, namespaceUri);
                if(c_id == null) {
                    //get the bytes so we can write it out inline
                    XMLBinaryDataHelper.EncodedData data = XMLBinaryDataHelper.getXMLBinaryDataHelper().getBytesForBinaryValue(//
                            attributeValue, marshaller, getMimeType(parent));
                    bytes = data.getData();
                }
            } else {
                XMLBinaryDataHelper.EncodedData data = XMLBinaryDataHelper.getXMLBinaryDataHelper().getBytesForBinaryValue(//
                        attributeValue, marshaller, getMimeType(parent));
                bytes = data.getData();
                c_id = marshaller.getAttachmentMarshaller().addMtomAttachment(bytes, 0,//
                        bytes.length,//
                        data.getMimeType(),//
                        elementName,//
                        namespaceUri);
            }
            if(c_id == null) {
            	XMLField textField = null;
            	if(field.isSelfField()){
                    textField = new XMLField(XMLConstants.TEXT);
            	}else{
                    textField = new XMLField(field.getXPath() + '/' + XMLConstants.TEXT);
            	}
                textField.setNamespaceResolver(field.getNamespaceResolver());
                textField.setSchemaType(field.getSchemaType());
                record.put(textField, bytes);
                //write out bytes inline
            } else {
                String xpath = this.getXPath();
                String prefix = null;
                boolean prefixAlreadyDefined = false;
                //  If the field's resolver is non-null and has an entry for XOP, 
                //  use it - otherwise, create a new resolver, set the XOP entry, 
                // on it, and use it instead.
                // We do this to avoid setting the XOP namespace declaration on
                // a given field or descriptor's resolver, as it is only required
                // on the current element
                NamespaceResolver resolver = field.getNamespaceResolver();
                if (resolver != null) {
                    prefix = resolver.resolveNamespaceURI(XMLConstants.XOP_URL);
                }
                if (prefix == null) {
                    prefix = XMLConstants.XOP_PREFIX;
                    resolver = new NamespaceResolver();
                    resolver.put(prefix, XMLConstants.XOP_URL);
                } else {
                    prefixAlreadyDefined = true;
                }
                
                
                String incxpath = null;
                if(field.isSelfField()){
                	incxpath = prefix + ":Include";
                	xpath = (prefix + include);
                }else{
                    incxpath = xpath + '/' + prefix + ":Include";
                    xpath += ('/' + prefix + include);
                }
                
                XMLField xpathField = new XMLField(xpath);
                xpathField.setNamespaceResolver(resolver);
                record.put(xpathField, c_id);

                // Need to call setAttributeNS on the record, unless the xop prefix
                // is defined on the descriptor's resolver already
                XMLField incField = new XMLField(incxpath);
                incField.setNamespaceResolver(resolver);
                Object obj = record.getIndicatingNoEntry(incField);
                if (!prefixAlreadyDefined && obj != null && obj instanceof DOMRecord) {
                    if (((DOMRecord) obj).getDOM().getNodeType() == Node.ELEMENT_NODE) {
                        ((Element) ((DOMRecord) obj).getDOM()).setAttributeNS(javax.xml.XMLConstants.XMLNS_ATTRIBUTE_NS_URI, javax.xml.XMLConstants.XMLNS_ATTRIBUTE + XMLConstants.COLON + prefix, XMLConstants.XOP_URL);
                    }
                }
            }
        } else if (isSwaRef() && (marshaller.getAttachmentMarshaller() != null)) {
            //AttributeValue should be a data-handler
            try {
                String c_id = null;
                if (getAttributeClassification() == XMLBinaryDataHelper.getXMLBinaryDataHelper().DATA_HANDLER) {
                    c_id = marshaller.getAttachmentMarshaller().addSwaRefAttachment((DataHandler) attributeValue);
                } else {
                    XMLBinaryDataHelper.EncodedData data = XMLBinaryDataHelper.getXMLBinaryDataHelper().getBytesForBinaryValue(//
                            attributeValue, marshaller, getMimeType(parent));
                    byte[] bytes = data.getData();
                    c_id = marshaller.getAttachmentMarshaller().addSwaRefAttachment(bytes, 0, bytes.length);
                }
                XMLField textField = new XMLField(field.getXPath() + '/' + XMLConstants.TEXT);
                textField.setNamespaceResolver(field.getNamespaceResolver());
                textField.setSchemaType(field.getSchemaType());
                record.put(textField, c_id);
            } catch (Exception ex) {
            }
        } else {
            //inline
            XMLField textField = null;
            if(field.isSelfField()){
                textField = new XMLField(XMLConstants.TEXT);
            }else{
                textField = new XMLField(field.getXPath() + '/' + XMLConstants.TEXT);
            }
            textField.setNamespaceResolver(field.getNamespaceResolver());
            textField.setSchemaType(field.getSchemaType());
            if ((getAttributeClassification() == ClassConstants.ABYTE) || (getAttributeClassification() == ClassConstants.APBYTE)) {
                record.put(textField, attributeValue);
            } else {
                byte[] bytes = XMLBinaryDataHelper.getXMLBinaryDataHelper().getBytesForBinaryValue(//
                        attributeValue, marshaller, getMimeType(parent)).getData();
                record.put(textField, bytes);
            }
        }
    }

    public Object valueFromRow(AbstractRecord row, JoinedAttributeManager joinManager, ObjectBuildingQuery query, CacheKey cacheKey, AbstractSession executionSession, boolean isTargetProtected, Boolean[] wasCacheUsed) {
        // PERF: Direct variable access.
        Object value = row.get(this.field);
        if (value == null) {
            return value;
        }
        Object fieldValue = null;
        XMLUnmarshaller unmarshaller = ((XMLRecord) row).getUnmarshaller();
        if (value instanceof String) {
            if (this.isSwaRef() && (unmarshaller.getAttachmentUnmarshaller() != null)) {
                if (getAttributeClassification() == XMLBinaryDataHelper.getXMLBinaryDataHelper().DATA_HANDLER) {
                    fieldValue = unmarshaller.getAttachmentUnmarshaller().getAttachmentAsDataHandler((String) value);
                } else {
                    fieldValue = unmarshaller.getAttachmentUnmarshaller().getAttachmentAsByteArray((String) value);
                }
            } else if (!this.isSwaRef()) {
                //should be base64
                byte[] bytes = ((XMLConversionManager) executionSession.getDatasourcePlatform().getConversionManager()).convertSchemaBase64ToByteArray(value);
                fieldValue = bytes;
            }
        } else if(value instanceof byte[] || value instanceof Byte[]){
        	fieldValue = value;
        } else {
            //this was an element, so do the XOP/SWAREF/Inline binary cases for an element
            XMLRecord record = (XMLRecord) value;

            if (getNullPolicy().valueIsNull((Element) record.getDOM())) {
                return null;
            }
            
           
            record.setSession(executionSession);

            if ((unmarshaller.getAttachmentUnmarshaller() != null) && unmarshaller.getAttachmentUnmarshaller().isXOPPackage() && !this.isSwaRef() && !this.shouldInlineBinaryData()) {
                //look for the include element:
                String xpath = XMLConstants.EMPTY_STRING;
                //  need a prefix for XOP
                String prefix = null;
                NamespaceResolver descriptorResolver = ((XMLDescriptor) getDescriptor()).getNamespaceResolver();
                // 20061023: handle NPE on null NSR
                if (descriptorResolver != null) {
                    prefix = descriptorResolver.resolveNamespaceURI(XMLConstants.XOP_URL);
                }
                if (prefix == null) {
                    prefix = XMLConstants.XOP_PREFIX;
                }
                NamespaceResolver tempResolver = new NamespaceResolver();
                tempResolver.put(prefix, XMLConstants.XOP_URL);
                xpath = prefix + include;
                XMLField field = new XMLField(xpath);
                field.setNamespaceResolver(tempResolver);
                String includeValue = (String) record.get(field);
                if (includeValue != null) {
                    if ((getAttributeClassification() == ClassConstants.ABYTE) || (getAttributeClassification() == ClassConstants.APBYTE)) {
                        fieldValue = unmarshaller.getAttachmentUnmarshaller().getAttachmentAsByteArray(includeValue);
                    } else {
                        fieldValue = unmarshaller.getAttachmentUnmarshaller().getAttachmentAsDataHandler(includeValue);
                    }
                } else {
                    //If we didn't find the Include element, check for inline
                    fieldValue = record.get(XMLConstants.TEXT);
                    //should be a base64 string
                    fieldValue = ((XMLConversionManager) executionSession.getDatasourcePlatform().getConversionManager()).convertSchemaBase64ToByteArray(fieldValue);
                }
            } else if ((unmarshaller.getAttachmentUnmarshaller() != null) && isSwaRef()) {
                String refValue = (String) record.get(XMLConstants.TEXT);
                if (refValue != null) {
                    fieldValue = unmarshaller.getAttachmentUnmarshaller().getAttachmentAsDataHandler(refValue);
                }
            } else {
                fieldValue = record.get(XMLConstants.TEXT);
                //should be a base64 string
                if (fieldValue != null) {
                    fieldValue = ((XMLConversionManager) executionSession.getDatasourcePlatform().getConversionManager()).convertSchemaBase64ToByteArray(fieldValue);
                } else {
                    fieldValue = new byte[0];
                }
            }
        }
        Object attributeValue = convertDataValueToObjectValue(fieldValue, executionSession, unmarshaller);
        attributeValue = XMLBinaryDataHelper.getXMLBinaryDataHelper().convertObject(attributeValue, getAttributeClassification(), executionSession);

        return attributeValue;
    }

    public boolean isAbstractDirectMapping() {
        return false;
    }

    @Override
    public boolean isAbstractColumnMapping() {
        return false;
    }

}
