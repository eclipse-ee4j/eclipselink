/*******************************************************************************
 * Copyright (c) 1998, 2009 Oracle. All rights reserved.
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
package org.eclipse.persistence.mappings.xdb;

import org.eclipse.persistence.exceptions.ConversionException;
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.internal.platform.database.XMLTypePlaceholder;
import org.eclipse.persistence.internal.queries.JoinedAttributeManager;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.platform.xml.XMLComparer;
import org.eclipse.persistence.platform.xml.XMLPlatformFactory;
import org.eclipse.persistence.platform.xml.XMLTransformer;
import org.eclipse.persistence.platform.xml.XMLParser;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.queries.ObjectBuildingQuery;
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
     * The mapping is initialized with the given session. This mapping is fully initialized
     * after this.
     */
    public void initialize(AbstractSession session) throws DescriptorException {
        super.initialize(session);
        setFieldClassification(XMLTypePlaceholder.class);
    }

    public DirectToXMLTypeMapping() {
        super();
        xmlTransformer = XMLPlatformFactory.getInstance().getXMLPlatform().newXMLTransformer();
        xmlTransformer.setFormattedOutput(false);
        xmlParser = XMLPlatformFactory.getInstance().getXMLPlatform().newXMLParser();
        xmlComparer = new XMLComparer();
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
    public Object getAttributeValue(Object fieldValue, AbstractSession session) throws DescriptorException {
        Object attributeValue = fieldValue;
        try {
            if (attributeValue != null) {
                if (getAttributeClassification() != ClassConstants.STRING) {
                    String xml = (String)attributeValue;
                    java.io.StringReader reader = new java.io.StringReader(xml);
                    return xmlParser.parse(reader);
                }
            }
        } catch (Exception ex) {
            throw ConversionException.couldNotBeConverted(fieldValue, getAttributeClassification(), ex);
        }
        return attributeValue;
    }

    public boolean isDirectToXMLTypeMapping() {
        return true;
    }

    /**
     * INTERNAL:
     * Build a clone of the Document for comparision at commit time.
     */
    public void buildClone(Object original, Object clone, UnitOfWorkImpl unitOfWork) {
        Object attributeValue = getAttributeValueFromObject(original);
        if (attributeValue != null) {
            if ((getAttributeClassification() == ClassConstants.DOCUMENT) || (getAttributeClassification() == ClassConstants.NODE)) {
                Document doc = (Document)attributeValue;
                setAttributeValueInObject(clone, doc.cloneNode(true));
            } else {
                super.buildClone(original, clone, unitOfWork);
            }
        }
    }

    /**
     * INTERNAL:
     * Compare the objects to see if an update is required. This is probably not
     * the best way to do this.
     */
    public boolean compareObjects(Object firstObject, Object secondObject, AbstractSession session) {
        if (getAttributeClassification() == ClassConstants.STRING) {
            return firstObject.equals(secondObject);
        } else {
            Object one = getFieldValue(getAttributeValueFromObject(firstObject), session);
            Object two = getFieldValue(getAttributeValueFromObject(secondObject), session);
            if ((one == null) && (two == null)) {
                return true;
            }
            if ((one == null) || (two == null)) {
                return false;
            }
            if (one instanceof Node && two instanceof Node) {
                return xmlComparer.isNodeEqual((Node)one, (Node)two);
            }
            return one.equals(two);
        }
    }
    public Object valueFromRow(AbstractRecord row, JoinedAttributeManager joinManager, ObjectBuildingQuery query, AbstractSession executionSession) {
        // PERF: Direct variable access.
        Object fieldValue = row.get(this.field);
        Object attributeValue = getAttributeValue(fieldValue, executionSession);

        return attributeValue;
    }      
}
