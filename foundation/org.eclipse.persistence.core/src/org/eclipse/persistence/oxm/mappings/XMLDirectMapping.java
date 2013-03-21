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

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.exceptions.ConversionException;
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.internal.core.sessions.CoreAbstractSession;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.identitymaps.CacheKey;
import org.eclipse.persistence.internal.oxm.XMLConversionManager;
import org.eclipse.persistence.internal.oxm.mappings.DirectMapping;
import org.eclipse.persistence.internal.oxm.mappings.Field;
import org.eclipse.persistence.internal.oxm.record.AbstractMarshalRecord;
import org.eclipse.persistence.internal.oxm.record.AbstractUnmarshalRecord;
import org.eclipse.persistence.internal.queries.ContainerPolicy;
import org.eclipse.persistence.internal.queries.JoinedAttributeManager;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.XMLMarshaller;
import org.eclipse.persistence.oxm.XMLUnmarshaller;
import org.eclipse.persistence.mappings.AttributeAccessor;
import org.eclipse.persistence.mappings.converters.Converter;
import org.eclipse.persistence.mappings.foundation.AbstractDirectMapping;
import org.eclipse.persistence.oxm.mappings.converters.XMLConverter;
import org.eclipse.persistence.oxm.mappings.nullpolicy.AbstractNullPolicy;
import org.eclipse.persistence.oxm.mappings.nullpolicy.NullPolicy;
import org.eclipse.persistence.oxm.record.DOMRecord;
import org.eclipse.persistence.oxm.record.XMLRecord;
import org.eclipse.persistence.queries.ObjectBuildingQuery;
import org.eclipse.persistence.sessions.Session;

/**
 * <p>XML Direct Mappings map a Java attribute directly to XML attribute or text node.
 *
 * <p><b>XML Direct Mappings can be used in the following scenarios</b>:<ul>
 * <li>Mapping to a Text Node</li>
 * <li>Mapping to an Attribute</li>
 * <li>Mapping to a Specified Schema Type</li>
 * <li>Mapping to a List Field</li>
 * <li>Mapping to a Union Field</li>
 * <li>Mapping to a Union of Lists</li>
 * <li>Mapping to a Union of Unions</li>
 * <li>Mapping with a Simple Type Translator</li>
 * </ul>
 *
 * <p><b>Setting the XPath</b>: TopLink XML mappings make use of XPath statements to find the relevant
 * data in an XML document.  The XPath statement is relative to the context node specified in the descriptor.
 * The XPath may contain node type, path, and positional information.  The XPath is specified on the
 * mapping using the <code>setXPath</code> method.
 *
 * <p>The following XPath statements may be used to specify the location of XML data relating to an object's
 * name attribute:
 *
 * <p><table border="1">
 * <tr>
 * <th id="c1" align="left">XPath</th>
 * <th id="c2" align="left">Description</th>
 * </tr>
 * <tr>
 * <td headers="c1">@name</td>
 * <td headers="c2">The "@" character indicates that the node is an attribute.</td>
 * </tr>
 * <tr>
 * <td headers="c1">text()</td>
 * <td headers="c2">"text()" indicates that the node is a text node.  In this case the name value in the
 * text node belongs to the context node.</td>
 * </tr>
 * <tr>
 * <td headers="c1">full-name/text()</td>
 * <td headers="c2">The name information is stored in the text node of the full-name element.</td>
 * </tr>
 * <tr>
 * <td headers="c1" nowrap="true">personal-info/name/text()</td>
 * <td headers="c2">The XPath statement may be used to specify any valid path.</td>
 * </tr>
 * <tr>
 * <td headers="c1">name[2]/text()</td>
 * <td headers="c2">The XPath statement may contain positional information.  In this case the name
 * information is stored in the text node of the second occurrence of the name element.</td>
 * </tr>
 * </table>
 *
 * <p><b>Mapping to a Specific Schema Type</b>: In most cases TopLink can determine the target format in the
 * XML document.  However, there are cases where you must specify which one of a number of possible targets
 * TopLink should use. For example, a java.util.Calendar could be marshalled to a schema date, time, or dateTime,
 * or a byte[] could be marshalled to a schema hexBinary or base64Binary node.
 *
 * <!--
 *    <?xml version="1.0" encoding="UTF-8"?>
 *    <xsd:schema xmlns:xsd="http://www.w3.org/2001/XMLSchema">
 *        <xsd:element name="customer" type="customer-type"/>
 *        <xsd:complexType name="customer-type">
 *            <xsd:sequence>
 *                <xsd:element name="picture" type="xsd:hexBinary"/>
 *                <xsd:element name="resume" type="xsd:base64Binary"/>
 *            </xsd:sequence>
 *        </xsd:complexType>
 *    </xsd:schema>
 * -->
 *
 * <p><em>XML Schema</em><br>
 * <code>
 * &lt;?xml version="1.0" encoding="UTF-8"?&gt;<br>
 * &lt;xsd:schema xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;<br>
 * &nbsp;&nbsp;&lt;xsd:element name="customer" type="customer-type"/&gt;<br>
 * &nbsp;&nbsp;&lt;xsd:complexType name="customer-type"&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;xsd:sequence&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;xsd:element name="picture" type="xsd:hexBinary"/&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;xsd:element name="resume" type="xsd:base64Binary"/&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;/xsd:sequence&gt;<br>
 * &nbsp;&nbsp;&lt;/xsd:complexType&gt;<br>
 * &lt;/xsd:schema&gt;<br>
 * </code>
 *
 * <p><em>Code Sample</em><br>
 * <code>
 * XMLDirectMapping pictureMapping = new XMLDirectMapping();<br>
 * pictureMapping.setAttributeName("picture");<br>
 * pictureMapping.setXPath("picture/text()");<br>
 * XMLField pictureField = (XMLField) pictureMapping.getField();<br>
 * pictureField.setSchemaType(XMLConstants.HEX_BINARY_QNAME);<br>
 * </code>
 *
 * <p><b>Mapping to a Union Field</b>:
 *
 * <!--
 * <?xml version="1.0" encoding="UTF-8"?>
 * <xsd:schema xmlns:xsd="http://www.w3.org/2001/XMLSchema">
 *        <xsd:element name="customer" type="customer-type"/>
 *        <xsd:complexType name="customer-type">
 *            <xsd:sequence>
 *                <xsd:element name="shoe-size" type="size-type"/>
 *            </xsd:sequence>
 *        </xsd:complexType>
 *        <xsd:simpleType name="size-type">
 *            <xsd:union memberTypes="xsd:decimal xsd:string"/>
 *        </xsd:simpleType>
 * </xsd:schema>
 * -->
 *
 * <p><em>XML Schema</em><br>
 * <code>
 * &lt;?xml version="1.0" encoding="UTF-8"?&gt;<br>
 * &lt;xsd:schema xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;<br>
 * &nbsp;&nbsp;&lt;xsd:element name="customer" type="customer-type"/&gt;<br>
 * &nbsp;&nbsp;&lt;xsd:complexType name="customer-type"&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;xsd:sequence&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;xsd:element name="shoe-size" type="size-type"/&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;/xsd:sequence&gt;<br>
 * &nbsp;&nbsp;&lt;/xsd:complexType&gt;<br>
 * &nbsp;&nbsp;&lt;xsd:simpleType name="size-type"&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;xsd:union memberTypes="xsd:decimal xsd:string"/&gt;<br>
 * &nbsp;&nbsp;&lt;/xsd:simpleType&gt;<br>
 * &lt;/xsd:schema&gt;<br>
 * </code>
 *
 * <p><em>Code Sample</em><br>
 * <code>
 * XMLDirectMapping shoeSizeMapping = new XMLDirectMapping();<br>
 * shoeSizeMapping.setAttributeName("shoeSize");<br>
 * XMLUnionField shoeSizeField = new XMLUnionField();<br>
 * shoeSizeField.setXPath("shoe-size/text()");<br>
 * shoeSizeField.addSchemaType(XMLConstants.DECIMAL_QNAME);<br>
 * shoeSizeField.addSchemaType(XMLConstants.STRING_QNAME);<br>
 * shoeSizeMapping.setField(shoeSizeField);<br>
 * </code>
 *
 * <p><b>Preserving the Node Type</b>: If the type of a node is not defined in your XML schema,
 * you can configure an XML Direct Mapping to use the xsi:type attribute to provide type information.
 *
 * <p><em>Code Sample</em><br>
 * <code>
 * XMLDirectMapping numberMapping = new XMLDirectMapping();<br>
 * numberMapping.setAttributeName("number");<br>
 * numberMapping.setXPath("number/text()");<br>
 * XMLField numberField = (XMLField) numberMapping.getField();<br>
 * numberField.setIsTypedTextField(true);<br>
 * </code>
 *
 * <p><b>More Information</b>: For more information about using the XML Direct Mapping, see the
 * "Understanding XML Mappings" chapter of the Oracle TopLink Developer's Guide.
 *
 * @since Oracle TopLink 10<i>g</i> Release 2 (10.1.3)
 */
public class XMLDirectMapping extends AbstractDirectMapping implements XMLMapping, DirectMapping<AbstractSession, AttributeAccessor, ContainerPolicy, Converter, ClassDescriptor, DatabaseField, XMLMarshaller, Session, XMLUnmarshaller, XMLRecord>, XMLNillableMapping {
   
    AbstractNullPolicy nullPolicy;
    public boolean isCDATA;
    private boolean isWriteOnly;
    private boolean isCollapsingStringValues;
    private boolean isNormalizingStringValues;
    private boolean isNullValueMarshalled = false;

    public XMLDirectMapping() {
        super();
        // The default policy is NullPolicy
        nullPolicy = new NullPolicy();
        nullPolicy.setNullRepresentedByEmptyNode(true);
        isCDATA = false;
    }

    /**
     * Set the AbstractNullPolicy on the mapping<br>
     * The default policy is NullPolicy.<br>
     *
     * @param aNullPolicy
     */
    public void setNullPolicy(AbstractNullPolicy aNullPolicy) {
        nullPolicy = aNullPolicy;
    }

    /**
     * INTERNAL:
     * Get the AbstractNullPolicy from the Mapping.<br>
     * The default policy is NullPolicy.<br>
     * @return
     */
    public AbstractNullPolicy getNullPolicy() {
        return nullPolicy;
    }


    /**
     * INTERNAL:
     */
    public boolean isXMLMapping() {
        return true;
    }

    /**
     * Set the Mapping field name attribute to the given XPath String
     * @param xpathString String
     */
    public void setXPath(String xpathString) {
        if ((xpathString.indexOf(XMLConstants.ATTRIBUTE) == -1) && (!xpathString.endsWith(XMLConstants.TEXT))) {        	
            xpathString += '/' + XMLConstants.TEXT;
        }
        setField(new XMLField(xpathString));
    }
    public void initialize(AbstractSession session) throws DescriptorException {
        super.initialize(session);
        ((XMLField)getField()).setIsCDATA(this.isCDATA());
        String xpathString = ((XMLField)getField()).getXPath();
        if (this.isAbstractDirectMapping() && (xpathString.indexOf(XMLConstants.ATTRIBUTE) == -1) && (!xpathString.endsWith(XMLConstants.TEXT))) {            
            throw DescriptorException.invalidXpathForXMLDirectMapping(this);
        }
    }

    /**
     * INTERNAL:
     * Allows for subclasses to convert the attribute value.
     */
    public Object getAttributeValue(Object fieldValue, AbstractSession session, AbstractUnmarshalRecord record) {
    	// Unmarshal DOM
        // If attribute is empty string representing (null) then return the nullValue
        boolean isNullRepresentedByEmptyNode = nullPolicy.isNullRepresentedByEmptyNode();
        boolean isNullRepresentedByXsiNil = nullPolicy.isNullRepresentedByXsiNil();
        if (isNullRepresentedByEmptyNode && XMLConstants.EMPTY_STRING.equals(fieldValue)) {
           fieldValue = null;
        } else if (null == fieldValue && !isNullRepresentedByEmptyNode) {
            fieldValue = XMLConstants.EMPTY_STRING;
        }

        // PERF: Direct variable access.
        Object attributeValue = fieldValue;
        // If attribute is absent check the policy
        if(attributeValue == XMLRecord.noEntry) {
            if(!getNullPolicy().getIsSetPerformedForAbsentNode()) {
                return attributeValue;
            } else {
                fieldValue = null;
                attributeValue = null;
            }
        }

        if (attributeValue == XMLRecord.NIL && isNullRepresentedByXsiNil) {
            fieldValue = null;
            attributeValue = null;
        }

        // Allow for user defined conversion to the object value.       
        if (converter != null) {
            attributeValue = convertDataValueToObjectValue(attributeValue, session, (XMLUnmarshaller) record.getUnmarshaller());
        } else {
            // PERF: Avoid conversion check when not required.
            if ((attributeValue == null) || (attributeValue.getClass() != this.attributeObjectClassification)) {
                try {
                    attributeValue = session.getDatasourcePlatform().convertObject(attributeValue, this.attributeClassification);
                } catch (ConversionException e) {
                    throw ConversionException.couldNotBeConverted(this, getDescriptor(), e);
                }
            }
        }
        if (attributeValue == null) {// Translate default null value, conversion may have produced null.
            attributeValue = this.nullValue;
        }

        return attributeValue;
    }

    /**
     * INTERNAL:
     * Convert the attribute value to a field value.
     * Process any converter if defined, and check for null values.
     */
    public Object getFieldValue(Object attributeValue, CoreAbstractSession session, AbstractMarshalRecord record) {
    	// Marshal
        // PERF: This method is a major performance code point,
        // so has been micro optimized and uses direct variable access.
        Object fieldValue = attributeValue;

        if ((this.nullValue != null) && (this.nullValue.equals(fieldValue)) && !this.isNullValueMarshalled && !((XMLField)field).isRequired()) {
            return null;
        }

        // Allow for user defined conversion to the object value.       
        fieldValue = convertObjectValueToDataValue(fieldValue, (AbstractSession) session, (XMLMarshaller) record.getMarshaller());
   
        if (fieldValue != null) {
        	 Class fieldClassification = getFieldClassification(field);
             // PERF: Avoid conversion if not required.
        	 if(fieldClassification != fieldValue.getClass()){
        		 try {
                     fieldValue = session.getPlatform(descriptor.getJavaClass()).convertObject(fieldValue, fieldClassification);
                 } catch (ConversionException exception) {
                     throw ConversionException.couldNotBeConverted(this, descriptor, exception);
                 }
        	 }
        }
        return fieldValue;
    }
    
    /**
     * Get the XPath String
     * @return String the XPath String associated with this Mapping
     */
    public String getXPath() {
        return getFieldName();
    }

    /**
     * INTERNAL:
     * Return the mapping's attribute value from the row.
     * The execution session is passed for the case of building a UnitOfWork clone
     * directly from a row, the session set in the query will not know which platform to use
     * for converting the value.  Allows the correct session to be passed in.
     */
    public Object valueFromRow(AbstractRecord row, JoinedAttributeManager joinManager, ObjectBuildingQuery query, CacheKey cacheKey, AbstractSession executionSession, boolean isTargetProtected, Boolean[] wasCacheUsed) {
        // PERF: Direct variable access.
        boolean shouldCheckForXsiNil = getNullPolicy().isNullRepresentedByXsiNil();
        return getAttributeValue(((DOMRecord)row).getIndicatingNoEntry(this.field, false, shouldCheckForXsiNil), executionSession, (XMLRecord)row);
    }

    /**
     * INTERNAL:
     * Get a value from the object and set that in the respective field of the row.
     */
    @Override
    public void writeFromObjectIntoRow(Object object, AbstractRecord row, AbstractSession session, WriteType writeType) {
        if (isReadOnly()) {
            return;
        }
        writeSingleValue(getAttributeValueFromObject(object), object, (XMLRecord)row, session);
    }
    
    protected void writeValueIntoRow(AbstractRecord row, DatabaseField aField, Object fieldValue) {
            row.put(getField(), fieldValue);
    }
    
    public void writeSingleValue(Object value, Object parent, XMLRecord row, AbstractSession session) {
        Object fieldValue = getFieldValue(value, session, row);
        if(fieldValue == null && getNullPolicy() != null) {
            getNullPolicy().directMarshal((Field) this.getField(), row, parent);
        } else {
            writeValueIntoRow(row, getField(), fieldValue);
        }
    }
    
    public void setAttributeValueInObject(Object object, Object value) throws DescriptorException {
        // PERF: Direct variable access.
        if(isWriteOnly()) {
            return;
        }
        try {
            if(value == XMLRecord.noEntry) {
                return;                    
            }
            if (value != null && value instanceof String) {
                if(isCollapsingStringValues) {
                    value = XMLConversionManager.getDefaultXMLManager().collapseStringValue((String)value);
                } else if(isNormalizingStringValues) {
                    value = XMLConversionManager.getDefaultXMLManager().normalizeStringValue((String)value);
                }
            }
            this.attributeAccessor.setAttributeValueInObject(object, value);
        } catch (DescriptorException exception) {
            exception.setMapping(this);
            throw exception;
        }
    }
    
    public void setIsCDATA(boolean CDATA) {
        isCDATA = CDATA;
    }
    
    public boolean isCDATA() {
        return isCDATA;
    }
    
    public boolean isWriteOnly() {
        return this.isWriteOnly;
    }
    
    public void setIsWriteOnly(boolean b) {
        this.isWriteOnly = b;
    }

    @Override
    public boolean isCloningRequired() {
        return false;
    }

    public void preInitialize(AbstractSession session) throws DescriptorException {
        getAttributeAccessor().setIsWriteOnly(this.isWriteOnly());
        getAttributeAccessor().setIsReadOnly(this.isReadOnly());
        super.preInitialize(session);
    }
    
    
    /**
     * PUBLIC: 
     * Returns true if this mapping is normalizing string values on unmarshal before setting
     * them in the object. Normalize replaces any CR, LF or Tab characters with a 
     * single space character. 
     */
    public boolean isNormalizingStringValues() {
        return this.isNormalizingStringValues;
    }
    
    /**
     * PUBLIC:
     * Indicates that this mapping should normalize all string values before setting them
     * in the object on unmarshal. Normalize replaces any CR, LF or Tab characters with a 
     * single space character. 
     * @param normalize
     */
    public void setNormalizingStringValues(boolean normalize) {
        this.isNormalizingStringValues = normalize;
    }
    
    
    /**
     * PUBLIC:
     * Indicates that this mapping should collapse all string values before setting them
     * in the object on unmarshal. Collapse removes leading and trailing whitespaces, and replaces
     * any sequence of whitepsace characters with a single space. 
     * @param collapse
     */
    public void setCollapsingStringValues(boolean collapse) {
        this.isCollapsingStringValues = collapse;
    }
    
    /**
     * PUBLIC:
     * Returns true if this mapping should collapse all string values before setting them
     * in the object on unmarshal. Collapse removes leading and trailing whitespaces, and replaces
     * any sequence of whitepsace characters with a single space. 
     */
    public boolean isCollapsingStringValues() {
        return this.isCollapsingStringValues;
    }

    /**
     * PUBLIC:
     * Returns true if this mapping's value should be marshalled, in the case that 
     * it is equal to the default null value. 
     */
    public boolean isNullValueMarshalled() {
        return this.isNullValueMarshalled;
    }

    /**
     * PUBLIC:
     * Set whether this mapping's value should be marshalled, in the case that 
     * it is equal to the default null value. 
     */
    public void setNullValueMarshalled(boolean value) {
        this.isNullValueMarshalled = value;
    }

    /**
     * INTERNAL
     * @since EclipseLink 2.5.0
     */
    public Object convertObjectValueToDataValue(Object value, Session session, XMLMarshaller marshaller) {
        if (hasConverter()) {
            if (converter instanceof XMLConverter) {
                return ((XMLConverter)converter).convertObjectValueToDataValue(value, session, marshaller);
            } else {
                return converter.convertObjectValueToDataValue(value, session);
            }
        }
        return value;
    }

    /**
     * INTERNAL
     * @since EclipseLink 2.5.0
     */
    public Object convertDataValueToObjectValue(Object fieldValue, Session session, XMLUnmarshaller unmarshaller) {
        if (hasConverter()) {
            if (converter instanceof XMLConverter) {
                return ((XMLConverter)converter).convertDataValueToObjectValue(fieldValue, session, unmarshaller);
            } else {
                return converter.convertDataValueToObjectValue(fieldValue, session);
            }
        }
        return fieldValue;
    }

}