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
 *     11/10/2011-2.4 Guy Pelletier 
 *       - 357474: Address primaryKey option from tenant discriminator column
 ******************************************************************************/  
package org.eclipse.persistence.mappings.xdb;

import org.eclipse.persistence.exceptions.ConversionException;
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.internal.platform.database.XMLTypePlaceholder;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.platform.xml.XMLComparer;
import org.eclipse.persistence.platform.xml.XMLPlatformFactory;
import org.eclipse.persistence.platform.xml.XMLTransformer;
import org.eclipse.persistence.platform.xml.XMLParser;
import org.eclipse.persistence.sessions.Session;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * <b>Purpose</b>: Mapping used to map from a DOM (org.w3c.Document) or XML String into
 * an Oracle XMLType field, in Oracle 9i XDB.
 *
 * @since Toplink 10.1.3
 */
public class DirectToXMLTypeMapping extends DirectToFieldMapping {
    /**
     * Indicates if we should initialize the whole DOM on a read.
     * This is only used if the user is mapping from an Oracle Document implementation.
     */
    protected boolean shouldReadWholeDocument = false;

    /**
     * Used to convert the DOM to a String.
     */
    private XMLTransformer xmlTransformer;

    /**
     * Used to determine if the XML document has been modified.
     */
    private XMLComparer xmlComparer;
    
    /**
     * Used to convert the String to a DOM
     */
    private  XMLParser xmlParser;
    
    /**
     * INTERNAL:
     * Default to mutable if mapped as a DOM.
     */
    public void preInitialize(AbstractSession session) throws DescriptorException {
        if (this.attributeClassification == null) {
            this.attributeClassification = getAttributeAccessor().getAttributeClass();
        }
        if ((this.isMutable == null) && (this.attributeClassification != ClassConstants.STRING)) {
            setIsMutable(true);            
        }
        super.preInitialize(session);
    }
    
    /**
     * INTERNAL:
     * The mapping is initialized with the given session. This mapping is fully initialized
     * after this.
     */
    @Override
    public void initialize(AbstractSession session) throws DescriptorException {
        super.initialize(session);
        setFieldClassification(XMLTypePlaceholder.class);
    }

    public DirectToXMLTypeMapping() {
        super();
        this.xmlTransformer = XMLPlatformFactory.getInstance().getXMLPlatform().newXMLTransformer();
        this.xmlTransformer.setFormattedOutput(false);
        this.xmlParser = XMLPlatformFactory.getInstance().getXMLPlatform().newXMLParser();
        this.xmlComparer = new XMLComparer();
    }

    /**
     * PUBLIC:
     * @param boolean - determines if the Oracle XDB DOM should be fully initialized
     * on a read.
     */
    public void setShouldReadWholeDocument(boolean readWholeDocument) {
        this.shouldReadWholeDocument = readWholeDocument;
    }

    /**
     * PUBLIC:
     * @return boolean - returns true if currently initializing DOMs on reads.
     */
    public boolean shouldReadWholeDocument() {
        return shouldReadWholeDocument;
    }

    /**
     * INTERNAL:
     * Get the attribute value for the given field value. If we're mapping to a
     * Document, we need to check if we should return the Oracle DOM or build a
     * new one.
     */
    @Override
    public Object getObjectValue(Object fieldValue, Session session) throws ConversionException {
        Object attributeValue = fieldValue;
        try {
            if (attributeValue != null) {
                if (this.attributeClassification != ClassConstants.STRING) {
                    String xml = (String)attributeValue;
                    java.io.StringReader reader = new java.io.StringReader(xml);
                    return this.xmlParser.parse(reader);
                }
            }
        } catch (Exception ex) {
            throw ConversionException.couldNotBeConverted(fieldValue, this.attributeClassification, ex);
        }

        if ((attributeValue == null) && (this.nullValue != null)) {// Translate default null value
            return this.nullValue;
        }

        // Allow for user defined conversion to the object value.
        if (this.converter != null) {
            attributeValue = this.converter.convertDataValueToObjectValue(attributeValue, session);
        }
        return attributeValue;
    }

    @Override
    public boolean isDirectToXMLTypeMapping() {
        return true;
    }

    /**
     * INTERNAL:
     * Clone the DOM Document if required.
     */
    @Override
    protected Object buildCloneValue(Object attributeValue, AbstractSession session) {
        Object newAttributeValue = attributeValue;
        if (isMutable() && attributeValue != null) {
            if ((getAttributeClassification() == ClassConstants.DOCUMENT) || (getAttributeClassification() == ClassConstants.NODE)) {
                Document doc = (Document)attributeValue;
                newAttributeValue = doc.cloneNode(true);
            }
        }
        return newAttributeValue;
    }

    /**
     * INTERNAL:
     * Compare the attribute values.
     * Compare Nodes if mapped as a DOM.
     */
    @Override
    protected boolean compareObjectValues(Object firstValue, Object secondValue, AbstractSession session) {
        // PERF: Check identity before conversion.
        if (firstValue == secondValue) {
            return true;
        }
        if ((firstValue == null) && (secondValue == null)) {
            return true;
        }
        if ((firstValue == null) || (secondValue == null)) {
            return false;
        }
        if (getAttributeClassification() == ClassConstants.STRING) {
            return firstValue.equals(secondValue);
        } else {
            Object one = getFieldValue(firstValue, session);
            Object two = getFieldValue(secondValue, session);
            if (one instanceof Node && two instanceof Node) {
                return this.xmlComparer.isNodeEqual((Node)one, (Node)two);
            }
            return one.equals(two);
        }        
    }
}
