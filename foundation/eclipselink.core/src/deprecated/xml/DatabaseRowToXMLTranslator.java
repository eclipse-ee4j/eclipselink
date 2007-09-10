/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package deprecated.xml;

import java.util.*;
import org.w3c.dom.*;
import deprecated.sdk.*;
import org.eclipse.persistence.sessions.Record;
import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.platform.xml.XMLPlatform;
import org.eclipse.persistence.platform.xml.XMLPlatformException;
import org.eclipse.persistence.platform.xml.XMLPlatformFactory;
import deprecated.xml.XMLDataStoreException;

/**
 * This class has a singular purpose: convert a <code>Record</code> to an XML document.
 * Given a <code>Record</code>, this class will create a corresponding XML DOM element.
 *
 * @see org.eclipse.persistence.sessions.Record
 * @see deprecated.sdk.SDKFieldValue
 *
 * @author Les Davis
 * @since TOPLink/Java 3.0
 * @deprecated since OracleAS TopLink 10<i>g</i> (10.1.3).  This class is replaced by
 *         {@link org.eclipse.persistence.oxm}
 */
public class DatabaseRowToXMLTranslator {

    /**
     * Default constructor.
     */
    public DatabaseRowToXMLTranslator() {
        super();
    }

    /**
     * Return whether any of the elements in the field value are
     * non-null. If all of the elements are null, the field value
     * will not be written out to the XML.
     */
    protected boolean collectionContainsNonNullElements(SDKFieldValue fieldValue) {
        for (Enumeration stream = fieldValue.getElements().elements(); stream.hasMoreElements();) {
            if (stream.nextElement() != null) {
                return true;
            }
        }
        return false;
    }

    /**
     * Return whether all the fields in the database row are null.
     */
    protected boolean rowIsAllNulls(AbstractRecord row) {
        for (Enumeration stream = row.elements(); stream.hasMoreElements();) {
            if (stream.nextElement() != null) {
                return false;
            }
        }

        return true;
    }

    public String toString() {
        return Helper.getShortClassName(this);
    }

    /**
     * Convert the specified database row to an XML document and
     * write it to the specified stream.
     */
    public Element createDOM(String rootElementName, Record row) throws XMLDataStoreException {
        try {
            XMLPlatform xmlPlatform = XMLPlatformFactory.getInstance().getXMLPlatform();
            Document document = xmlPlatform.createDocument();
            return buildRootElement(rootElementName, document, (AbstractRecord)row);
        } catch (XMLPlatformException exception) {
            throw XMLDataStoreException.generalException(exception);
        }
    }

    /**
     * Builds elements from an SDKFieldValue
     */
    protected Node buildSDKFieldValueElement(Document document, Node parent, DatabaseField field, SDKFieldValue value) throws XMLDataStoreException {
        if ((value.getElementDataTypeName() == null) || (value.getElementDataTypeName().length() == 0)) {
            throw XMLDataStoreException.elementDataTypeNameIsRequired(value.getElements());
        }
        if (value.isDirectCollection()) {
            return buildDirectCollectionElement(document, parent, field, value);
        } else {
            return buildNestedRowElement(document, parent, field, value);
        }
    }

    /**
     * builds a DOM element to represent the direct collection in the field value
     */
    protected Node buildDirectCollectionElement(Document document, Node parent, DatabaseField field, SDKFieldValue value) throws XMLDataStoreException {
        String collectionName = field.getName();
        Node collectionRoot = document.createElement(collectionName);
        DatabaseField subField = new DatabaseField(value.getElementDataTypeName(), "");
        for (Enumeration enumtr = value.getElements().elements(); enumtr.hasMoreElements();) {
            Object element = enumtr.nextElement();
            if (element == null) {
                // do nothing
            } else if (element instanceof String) {
                collectionRoot.appendChild(buildSimpleTextElement(document, parent, subField, (String)element));
            } else {
                throw XMLDataStoreException.invalidFieldValue(collectionName, value);
            }
        }
        return collectionRoot;
    }

    /**
     * Builds the root element for the document
     */
    protected Element buildRootElement(String rootElementName, Document document, AbstractRecord row) throws XMLDataStoreException {
        Element rootElement = document.createElement(rootElementName);
        addFieldsToElementFromRow(document, rootElement, row, rootElementName);

        return rootElement;
    }

    /**
     * adds elements representing the fields to the root element
     */
    protected void addFieldsToElementFromRow(Document document, Node parent, AbstractRecord row, String rootElementName) throws XMLDataStoreException {
        for (int i = 0; i < row.getFields().size(); i++) {
            DatabaseField field = (DatabaseField)row.getFields().elementAt(i);
            Object value = row.getValues().elementAt(i);
            Node child = buildElementFromField(document, parent, field, value);
            if (child != null) {
                parent.appendChild(child);
            }
        }
    }

    /**
     * Builds an element from field
     */
    protected Node buildElementFromField(Document document, Node parent, DatabaseField field, Object value) {
        if (value == null) {
            return null;
        } else if (value instanceof SDKFieldValue) {
            if (this.collectionContainsNonNullElements((SDKFieldValue)value)) {
                return buildSDKFieldValueElement(document, parent, field, (SDKFieldValue)value);
            } else {
                return null;
            }
        } else {
            return buildSimpleTextElement(document, parent, field, value);
        }
    }

    /**
     * builds a simple element with a text node value
     */
    protected Node buildSimpleTextElement(Document document, Node parent, DatabaseField field, Object value) {
        Node element = null;
        String stringValue = (String)ConversionManager.getDefaultManager().convertObject(value, ClassConstants.STRING);

        // Handle special field name syntax for attribute (@).
        if (field.getName().length() > 1) {
            if (field.getName().charAt(0) == '@') {
                ((Element)parent).setAttribute(field.getName().substring(1, field.getName().length()), stringValue);
                return null;
            } else {
                element = document.createElement(field.getName());
            }
        }
        Node textValue = document.createTextNode(stringValue);
        element.appendChild(textValue);
        return element;
    }

    /**
     * Build a element representing a nested row
     */
    protected Node buildNestedRowElement(Document document, Node parent, DatabaseField field, SDKFieldValue value) throws XMLDataStoreException {
        String elementDataTypeName = field.getName();
        Node subElement = null;
        for (Enumeration enumtr = value.getElements().elements(); enumtr.hasMoreElements();) {
            Object row = enumtr.nextElement();
            if (row instanceof AbstractRecord) {
                AbstractRecord dbRow = (AbstractRecord)row;

                // This is temporarily assuming no grouping element, and forcing it to work for now.
                if (subElement != null) {
                    parent.appendChild(subElement);
                }
                subElement = document.createElement(elementDataTypeName);
                if (rowIsAllNulls(dbRow)) {
                    //
                } else {
                    addFieldsToElementFromRow(document, subElement, dbRow, elementDataTypeName);
                }
            } else {
                throw XMLDataStoreException.invalidFieldValue(elementDataTypeName, row);
            }
        }
        if (subElement == null) {
            return document.createElement(elementDataTypeName);
        }
        return subElement;
    }
}