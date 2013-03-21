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

import java.util.Enumeration;
import java.util.Vector;
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
import org.eclipse.persistence.internal.oxm.mappings.BinaryDataCollectionMapping;
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
import org.eclipse.persistence.oxm.mappings.nullpolicy.AbstractNullPolicy;
import org.eclipse.persistence.oxm.mappings.nullpolicy.XMLNullRepresentationType;
import org.eclipse.persistence.oxm.record.DOMRecord;
import org.eclipse.persistence.oxm.record.XMLRecord;
import org.eclipse.persistence.queries.ObjectBuildingQuery;
import org.eclipse.persistence.sessions.Session;

/**
 * <p><b>Purpose:</b>Provide a mapping for a collection of binary data values that can be treated
 * as either inline binary values or as an attachment.
 * <p><b>Responsibilities:</b><ul>
 * <li>Handle converting binary types (byte[], Image etc) to base64</li>
 * <li>Make callbacks to AttachmentMarshaller/AttachmentUnmarshaller</li>
 * <li>Write out approriate attachment information (xop:include) </li>
 * </ul>
 *  <p>XMLBinaryDataCollectionMapping represents a mapping of a collection of binary data in the object model
 *  to XML. This can either be written directly as inline binary data (base64) or 
 *  passed through as an MTOM or SWAREF attachment.
 *  <p>The following types are allowable to be mapped using an XMLBinaryDataMapping:<ul>
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
public class XMLBinaryDataCollectionMapping extends XMLCompositeDirectCollectionMapping implements BinaryDataCollectionMapping<AbstractSession, AttributeAccessor, ContainerPolicy, Converter, ClassDescriptor, DatabaseField, XMLMarshaller, MimeTypePolicy, Session, XMLUnmarshaller, XMLRecord> {
    private boolean shouldInlineBinaryData;
    private MimeTypePolicy mimeTypePolicy;
    private boolean isSwaRef;
    private Class collectionContentType;
    private static final String INCLUDE = "Include";

    public XMLBinaryDataCollectionMapping() {
        collectionContentType = ClassConstants.APBYTE;
        mimeTypePolicy = new FixedMimeTypePolicy(null);
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
        return getMimeType(null);
    }

    public MimeTypePolicy getMimeTypePolicy() {
        return mimeTypePolicy;
    }

    /**
     * Allow implementer to set the MimeTypePolicy class FixedMimeTypePolicy or AttributeMimeTypePolicy (dynamic)
     * @param aPolicy MimeTypePolicy
     */
    public void setMimeTypePolicy(MimeTypePolicy mimeTypePolicy) {
        this.mimeTypePolicy = mimeTypePolicy;
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

    public boolean isAbstractCompositeDirectCollectionMapping() {
        return false;
    }

    /**
    * Set the Mapping field name attribute to the given XPath String
    * @param xpathString String
    */
    public void setXPath(String xpathString) {
        XMLField field = new XMLField(xpathString);
        field.setSchemaType(XMLConstants.BASE_64_BINARY_QNAME);
        setField(new XMLField(xpathString));
    }

    @Override
    public void writeFromObjectIntoRow(Object object, AbstractRecord row, AbstractSession session, WriteType writeType) {
        XMLRecord record = (XMLRecord) row;
        XMLMarshaller marshaller = record.getMarshaller();
        Object attributeValue = getAttributeValueFromObject(object);

        ContainerPolicy cp = this.getContainerPolicy();
        Vector elements = new Vector(cp.sizeFor(attributeValue));
        XMLField field = (XMLField) getField();
        NamespaceResolver resolver = field.getNamespaceResolver();
        boolean isAttribute = field.getLastXPathFragment().isAttribute();
        String prefix = null;
        XMLField includeField = null;
        if (!isAttribute) {
            if (record.isXOPPackage() && !isSwaRef() && !shouldInlineBinaryData()) {
                field = (XMLField) getField();

                // If the field's resolver is non-null and has an entry for XOP, 
                // use it - otherwise, create a new resolver, set the XOP entry, 
                // on it, and use it instead.
                // We do this to avoid setting the XOP namespace declaration on
                // a given field or descriptor's resolver, as it is only required
                // on the current element
                if (resolver != null) {
                    prefix = resolver.resolveNamespaceURI(XMLConstants.XOP_URL);
                }
                if (prefix == null) {
                    prefix = XMLConstants.XOP_PREFIX;//"xop";
                    resolver = new NamespaceResolver();
                    resolver.put(prefix, XMLConstants.XOP_URL);
                }
                includeField = new XMLField(prefix + XMLConstants.COLON + INCLUDE + "/@href");
                includeField.setNamespaceResolver(resolver);
            }
        }
        XMLField textField = new XMLField(field.getXPath() + '/' + XMLConstants.TEXT);
        textField.setNamespaceResolver(field.getNamespaceResolver());
        textField.setSchemaType(field.getSchemaType());
        //field = textField;

        boolean inline = false;
        for (Object iter = cp.iteratorFor(attributeValue); cp.hasNext(iter);) {
            Object element = cp.next(iter, session);
            element = getValueToWrite(element, object, record, field, includeField, session);
            if(element == null){
                AbstractNullPolicy nullPolicy = getNullPolicy();
                if (nullPolicy == null) {
                    elements.addElement(null);
                } else {
                    if (nullPolicy.getMarshalNullRepresentation() == XMLNullRepresentationType.XSI_NIL) {
                        elements.addElement(XMLRecord.NIL);
                    } else if (nullPolicy.getMarshalNullRepresentation() == XMLNullRepresentationType.ABSENT_NODE) {
                        // Do nothing
                    } else {
                        elements.addElement(XMLConstants.EMPTY_STRING);
                    }
                }
            }else{
            
                
                if(element.getClass() == ClassConstants.ABYTE) {
                    inline = true;
                }
                elements.addElement(element);
            }
        }
        Object fieldValue = null;
        if (!elements.isEmpty()) {
            fieldValue = this.getDescriptor().buildFieldValueFromDirectValues(elements, elementDataTypeName, session);
        }
        if(inline) {
            row.put(textField, fieldValue);
        } else {
            row.put(field, fieldValue);
        }
    }

    public Object getValueToWrite(Object value, Object parent, XMLRecord record, XMLField field, XMLField includeField, AbstractSession session) {
        XMLMarshaller marshaller = record.getMarshaller();
        Object element = convertObjectValueToDataValue(value, session, record.getMarshaller());
        boolean isAttribute = ((XMLField) getField()).getLastXPathFragment().isAttribute();

        if(element == null){
            return null;
        }
        
        if (isAttribute) {
            if (isSwaRef() && (marshaller.getAttachmentMarshaller() != null)) {
                //should be a DataHandler here
                try {
                    String id = marshaller.getAttachmentMarshaller().addSwaRefAttachment((DataHandler) element);
                    element = id;
                } catch (ClassCastException cce) {
                    throw XMLMarshalException.invalidSwaRefAttribute(getAttributeClassification().getName());
                }
            } else {
                //inline case
                XMLBinaryDataHelper.EncodedData data = XMLBinaryDataHelper.getXMLBinaryDataHelper().getBytesForBinaryValue(element, record.getMarshaller(), mimeTypePolicy.getMimeType(parent));
                String base64Value = ((XMLConversionManager) session.getDatasourcePlatform().getConversionManager()).buildBase64StringFromBytes(data.getData());
                element = base64Value;
            }
        } else {
            if (record.isXOPPackage() && !isSwaRef() && !shouldInlineBinaryData()) {
                //write as attachment
                String c_id = XMLConstants.EMPTY_STRING;
                byte[] bytes = null;
                if ((getAttributeElementClass() == ClassConstants.ABYTE) || (getAttributeElementClass() == ClassConstants.APBYTE)) {
                    if (getAttributeElementClass() == ClassConstants.ABYTE) {
                        element = session.getDatasourcePlatform().getConversionManager().convertObject(element, ClassConstants.APBYTE);
                    }
                    bytes = (byte[])element;
                    c_id = marshaller.getAttachmentMarshaller().addMtomAttachment(bytes, 0, bytes.length, this.mimeTypePolicy.getMimeType(parent), field.getLastXPathFragment().getLocalName(),
                            field.getLastXPathFragment().getNamespaceURI());
                } else if (getAttributeElementClass() == XMLBinaryDataHelper.getXMLBinaryDataHelper().DATA_HANDLER) {
                    c_id = marshaller.getAttachmentMarshaller().addMtomAttachment((DataHandler) element, field.getLastXPathFragment().getLocalName(), field.getLastXPathFragment().getNamespaceURI());
                    if(c_id == null) {
                        XMLBinaryDataHelper.EncodedData data = XMLBinaryDataHelper.getXMLBinaryDataHelper().getBytesForBinaryValue(element, marshaller, this.mimeTypePolicy.getMimeType(parent));
                        bytes = data.getData();
                    }
                } else {
                    XMLBinaryDataHelper.EncodedData data = XMLBinaryDataHelper.getXMLBinaryDataHelper().getBytesForBinaryValue(element, marshaller, this.mimeTypePolicy.getMimeType(parent));
                    bytes = data.getData();
                    c_id = marshaller.getAttachmentMarshaller().addMtomAttachment(bytes, 0, bytes.length, data.getMimeType(), field.getLastXPathFragment().getLocalName(), field.getLastXPathFragment().getNamespaceURI());
                }
                
                if(c_id == null) {
                    element = bytes;
                } else {
                    DOMRecord include = new DOMRecord(field.getLastXPathFragment().getLocalName());
                    include.setSession(session);
                    include.put(includeField, c_id);
                    element = include;

                    // Need to call setAttributeNS on the record, unless the xop prefix
                    // is defined on the descriptor's resolver already
                    NamespaceResolver resolver = ((XMLField) getField()).getNamespaceResolver();
                    if (resolver == null || resolver.resolveNamespaceURI(XMLConstants.XOP_URL) == null) {
                        resolver = new NamespaceResolver();
                        resolver.put(XMLConstants.XOP_PREFIX, XMLConstants.XOP_URL);
                        String xpath = XMLConstants.XOP_PREFIX + XMLConstants.COLON + INCLUDE;
                        XMLField incField = new XMLField(xpath);
                        incField.setNamespaceResolver(resolver);
                        Object obj = include.getIndicatingNoEntry(incField);
                        if (obj != null && obj instanceof DOMRecord) {
                            if (((DOMRecord) obj).getDOM().getNodeType() == Node.ELEMENT_NODE) {
                                ((Element) ((DOMRecord) obj).getDOM()).setAttributeNS(javax.xml.XMLConstants.XMLNS_ATTRIBUTE_NS_URI, javax.xml.XMLConstants.XMLNS_ATTRIBUTE + XMLConstants.COLON + XMLConstants.XOP_PREFIX, XMLConstants.XOP_URL);
                            }
                        }
                    }
                }
            } else if (isSwaRef() && (marshaller.getAttachmentMarshaller() != null)) {
                //element should be a data-handler
                try {
                    String c_id = marshaller.getAttachmentMarshaller().addSwaRefAttachment((DataHandler) element);
                    element = c_id;
                } catch (Exception ex) {
                }
            } else {
                //inline
            	if (!((getAttributeElementClass() == ClassConstants.ABYTE) || (getAttributeElementClass() == ClassConstants.APBYTE))) {
                    element = XMLBinaryDataHelper.getXMLBinaryDataHelper().getBytesForBinaryValue(element, marshaller, this.mimeTypePolicy.getMimeType(parent)).getData();
                }
            }
        }
        return element;
    }

    public void writeSingleValue(Object value, Object parent, XMLRecord record, AbstractSession session) {
        XMLMarshaller marshaller = record.getMarshaller();
        XMLField field = (XMLField) getField();
        NamespaceResolver resolver = field.getNamespaceResolver();
        boolean isAttribute = field.getLastXPathFragment().isAttribute();
        String prefix = null;
        XMLField includeField = null;
        if (!isAttribute) {
            if (record.isXOPPackage() && !isSwaRef() && !shouldInlineBinaryData()) {
                field = (XMLField) getField();

                // If the field's resolver is non-null and has an entry for XOP, 
                // use it - otherwise, create a new resolver, set the XOP entry, 
                // on it, and use it instead.
                // We do this to avoid setting the XOP namespace declaration on
                // a given field or descriptor's resolver, as it is only required
                // on the current element
                if (resolver != null) {
                    prefix = resolver.resolveNamespaceURI(XMLConstants.XOP_URL);
                }
                if (prefix == null) {
                    prefix = XMLConstants.XOP_PREFIX;//"xop";
                    resolver = new NamespaceResolver();
                    resolver.put(prefix, XMLConstants.XOP_URL);
                }

                includeField = new XMLField(prefix + XMLConstants.COLON + INCLUDE + "/@href");
                includeField.setNamespaceResolver(resolver);
            }
        }
        XMLField textField = new XMLField(field.getXPath() + '/' +XMLConstants.TEXT);
        textField.setNamespaceResolver(field.getNamespaceResolver());
        textField.setSchemaType(field.getSchemaType());
        
        Object valueToWrite = getValueToWrite(value, parent, record, field, includeField, session);
        if(!isAttribute && valueToWrite.getClass() == ClassConstants.ABYTE) {
            //if the returned value is a byte[] and not an XMLRecord, just write it inline
            record.add(textField, valueToWrite);
        }
        record.add(field, valueToWrite);
    }

    public Object valueFromRow(AbstractRecord row, JoinedAttributeManager joinManager, ObjectBuildingQuery query, CacheKey cacheKey, AbstractSession executionSession, boolean isTargetProtected, Boolean[] wasCacheUsed) {
        ContainerPolicy cp = this.getContainerPolicy();

        Object fieldValue = row.getValues(this.getField());
        if (fieldValue == null) {
            if (reuseContainer) {
                Object currentObject = ((XMLRecord) row).getCurrentObject();
                Object container = getAttributeAccessor().getAttributeValueFromObject(currentObject);
                return container != null ? container : cp.containerInstance();
            } else {
                return cp.containerInstance();
            }
        }

        Vector fieldValues = this.getDescriptor().buildDirectValuesFromFieldValue(fieldValue);
        if (fieldValues == null) {
            if (reuseContainer) {
                Object currentObject = ((XMLRecord) row).getCurrentObject();
                Object container = getAttributeAccessor().getAttributeValueFromObject(currentObject);
                return container != null ? container : cp.containerInstance();
            } else {
                return cp.containerInstance();
            }
        }

        Object result = null;
        if (reuseContainer) {
            Object currentObject = ((XMLRecord) row).getCurrentObject();
            Object container = getAttributeAccessor().getAttributeValueFromObject(currentObject);
            result = container != null ? container : cp.containerInstance();
        } else {
            result = cp.containerInstance(fieldValues.size());
        }

        for (Enumeration stream = fieldValues.elements(); stream.hasMoreElements();) {
            Object element = stream.nextElement();

            // PERF: Direct variable access.
            //Object value = row.get(field);
            //Object fieldValue = null;
            XMLUnmarshaller unmarshaller = ((XMLRecord) row).getUnmarshaller();
            if (element instanceof String) {
                if (this.isSwaRef() && (unmarshaller.getAttachmentUnmarshaller() != null)) {
                    fieldValue = unmarshaller.getAttachmentUnmarshaller().getAttachmentAsDataHandler((String) element);
                } else if (!this.isSwaRef()) {
                    //should be base64
                    byte[] bytes = ((XMLConversionManager) executionSession.getDatasourcePlatform().getConversionManager()).convertSchemaBase64ToByteArray(element);
                    fieldValue = bytes;
                }
            } else {
                //this was an element, so do the XOP/SWAREF/Inline binary cases for an element
                XMLRecord record = (XMLRecord) element;
                if (getNullPolicy().valueIsNull((Element) record.getDOM())) {
                    fieldValue = null;
                }
                else{
                    record.setSession(executionSession);

                    if ((unmarshaller.getAttachmentUnmarshaller() != null) && unmarshaller.getAttachmentUnmarshaller().isXOPPackage() && !this.isSwaRef() && !this.shouldInlineBinaryData()) {
                        //look for the include element:
                        String xpath = XMLConstants.EMPTY_STRING;
    
                        //  need a prefix for XOP
                        String prefix = null;
                        NamespaceResolver descriptorResolver = ((XMLDescriptor) getDescriptor()).getNamespaceResolver();
                        if (descriptorResolver != null) {
                            prefix = descriptorResolver.resolveNamespaceURI(XMLConstants.XOP_URL);
                        }
                        if (prefix == null) {
                            prefix = XMLConstants.XOP_PREFIX;
                        }
                        NamespaceResolver tempResolver = new NamespaceResolver();
                        tempResolver.put(prefix, XMLConstants.XOP_URL);
                        xpath = prefix + XMLConstants.COLON + INCLUDE + "/@href";
                        XMLField field = new XMLField(xpath);
                        field.setNamespaceResolver(tempResolver);
                        String includeValue = (String) record.get(field);
                        if (element != null && includeValue != null) {
                        	if ((getAttributeElementClass() == ClassConstants.ABYTE) || (getAttributeElementClass() == ClassConstants.APBYTE)) {
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
                        fieldValue = ((XMLConversionManager) executionSession.getDatasourcePlatform().getConversionManager()).convertSchemaBase64ToByteArray(fieldValue);
                    }
                }
            }
            Object attributeValue = convertDataValueToObjectValue(fieldValue, executionSession, unmarshaller);
            cp.addInto(attributeValue, result, query.getSession());
        }
        return result;
    }

    public void setCollectionContentType(Class javaClass) {
    	setAttributeElementClass(javaClass);
    }

    /*
     * This is the same as calling getAttributeElementClass()
     * If not set by the user than byte[].class is the default
     */
    public Class getCollectionContentType() {
    	return getAttributeElementClass();
    }
    
    /**
     * PUBLIC:
     * Set the class each element in the object's
     * collection should be converted to, before the collection
     * is inserted into the object.
     * This is optional - if left null, the elements will be added
     * to the object's collection unconverted.
     */
    @Override
    public void setAttributeElementClass(Class attributeElementClass) {
        super.setAttributeElementClass(attributeElementClass);
        this.collectionContentType = attributeElementClass;
    }
    
    @Override
    public Class getAttributeElementClass() {
        Class elementClass = super.getAttributeElementClass();
        if(elementClass == null) {
            return this.collectionContentType;
        }
        return elementClass;
    }

}