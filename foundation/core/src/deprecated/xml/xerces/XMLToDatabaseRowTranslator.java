/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package deprecated.xml.xerces;

import java.util.*;
import java.io.*;
import org.w3c.dom.*;
import org.eclipse.persistence.platform.xml.XMLParser;
import org.eclipse.persistence.platform.xml.XMLPlatform;
import org.eclipse.persistence.platform.xml.XMLPlatformException;
import org.eclipse.persistence.platform.xml.XMLPlatformFactory;
import org.eclipse.persistence.sessions.DatabaseRecord;
import org.eclipse.persistence.sessions.Record;
import deprecated.sdk.SDKFieldValue;
import org.eclipse.persistence.internal.helper.*;
import deprecated.xml.XMLDataStoreException;

/**
 * This class has a singular purpose: convert an XML document to a <code>Record</code>.
 * Given Reader on an XML document, build and return the corresponding
 * <code>Record</code>.
 *
 * @see org.eclipse.persistence.sessions.Record
 * @see deprecated.sdk.SDKFieldValue
 * @see org.eclipse.persistence.internal.helper.DatabaseField
 * @see XMLDocument
 *
 * @author Les Davis
 * @since TOPLink/Java 3.0
 * @deprecated since OracleAS TopLink 10<i>g</i> (10.1.3).  This class is replaced by
 *         {@link org.eclipse.persistence.oxm}
 */
public class XMLToDatabaseRowTranslator {

    /**The parser used to parse the XML document and build a DOM tree. */
    private static XMLParser parser;

    /** The attribute name used in version 4.5 and earlier. */
    private static final String EMPTY_AGGREGATE_ATTRIBUTE_NAME_45 = "emptyAggregate";

    /** XML attributes and values. */
    public static String NULL_ATTRIBUTE_NAME = "null";
    public static String EMPTY_AGGREGATE_ATTRIBUTE_NAME = "empty-aggregate";
    public static String ATTRIBUTE_VALUE_TRUE = "true";
    public static String ATTRIBUTE_VALUE_FALSE = "false";

    /**
     * Default constructor.
     */
    public XMLToDatabaseRowTranslator() {
        super();
    }

    /**
     * Build and return an SDKFieldValue whose contents are determined
     * by the children of the specified node.
     * The parentNode must have homogenous children.
     */
    protected SDKFieldValue buildCompositeFieldValue(Node parentNode) throws XMLDataStoreException {
        Node childNode = parentNode.getFirstChild();

        // skip over ignorable whitespace
        while (this.nodeIsIgnorable(childNode)) {
            childNode = childNode.getNextSibling();
        }

        // cache these settings, so we can verify that all the other nodes are similar
        String elementDataTypeName = childNode.getNodeName();
        boolean isDirectCollection = this.nodeIsSimple(childNode);

        // build up the elements
        Vector elements = new Vector(parentNode.getChildNodes().getLength());
        while (childNode != null) {
            // make sure the node is the same type as the first
            if ((this.nodeIsSimple(childNode) != isDirectCollection) || (!childNode.getNodeName().equals(elementDataTypeName))) {
                throw XMLDataStoreException.heterogeneousChildElements(parentNode);
            }

            if (isDirectCollection) {
                elements.addElement(this.buildSimpleFieldValue(childNode));
            } else {
                elements.addElement(this.buildRecord(childNode));
            }
            childNode = childNode.getNextSibling();
            // skip over ignorable whitespace
            while (this.nodeIsIgnorable(childNode)) {
                childNode = childNode.getNextSibling();
            }
        }
        return new SDKFieldValue(elements, elementDataTypeName, isDirectCollection);
    }

    /**
     * Build and return a TopLink database row whose contents
     * are determined by the specified node.
     */
    protected Record buildRecord(Node parentNode) throws XMLDataStoreException {
        DatabaseRecord row = new DatabaseRecord(parentNode.getChildNodes().getLength());

        // loop through child nodes
        for (Node childNode = parentNode.getFirstChild(); childNode != null;) {
            if (!this.nodeIsIgnorable(childNode)) {
                DatabaseField field = new DatabaseField(childNode.getNodeName(), parentNode.getNodeName());
                Object value = this.buildFieldValue(childNode);
                row.put(field, value);
            }
            childNode = childNode.getNextSibling();
        }
        return row;
    }

    /**
     * Build and return an XML document that can be transmogrified into a database row.
     */
    protected Document buildDocument(Reader stream) throws XMLDataStoreException {
        Document document = null;

        XMLParser parser = getParser();
        try {
            // don't allow multiple threads to use the parser simultaneously
            synchronized (parser) {
                document = parser.parse(stream);
            }
        } catch (XMLPlatformException parseException) {
            throw XMLDataStoreException.parserError(parseException);
        } catch (Exception exception) {
            throw XMLDataStoreException.generalException(exception);
        }

        return document;
    }

    /**
     * Figure out whether the node is simple or composite
     * and return the appropriate value to put in the database row.
     */
    protected Object buildFieldValue(Node node) {
        if (this.nodeIsSimple(node)) {
            return this.buildSimpleFieldValue(node);
        } else {
            return this.buildCompositeFieldValue(node);
        }
    }

    /**
     * The node is a simple field value - figure out its value.
     */
    protected Object buildSimpleFieldValue(Node node) {
        if (node.hasChildNodes()) {
            return node.getFirstChild().getNodeValue();// <foo>bar</foo>
        } else if (this.nodeIsNull(node)) {
            return null;// <foo null="true"/>
        } else {
            return "";// <foo></foo> or <foo/>
        }
    }

    /**
     * Lazy initialize the parser.
     */
    private static XMLParser getParser() {
        if (parser == null) {
            try {
                XMLPlatform xmlPlatform = XMLPlatformFactory.getInstance().getXMLPlatform();
                parser = xmlPlatform.newXMLParser();
                parser.setNamespaceAware(false);
                parser.setWhitespacePreserving(false);
            } catch (Exception exception) {
                throw XMLDataStoreException.generalException(exception);
            }
        }
        return parser;
    }

    /**
     * Return whether the node is an empty aggregate (not a "direct" value).
     * e.g. <foo emptyAggregate="true"/> or <foo emptyAggregate="true"></foo>
     */
    protected boolean nodeIsEmptyAggregate(Node node) {
        if (node.hasAttributes()) {
            Node attribute = node.getAttributes().getNamedItem(EMPTY_AGGREGATE_ATTRIBUTE_NAME);

            //check for older empty aggregate tag
            if (attribute == null) {
                attribute = node.getAttributes().getNamedItem(EMPTY_AGGREGATE_ATTRIBUTE_NAME_45);
            }
            String attributeValue = null;
            if (attribute != null) {
                attributeValue = attribute.getNodeValue();
            }
            return (attributeValue != null) && attributeValue.equals(ATTRIBUTE_VALUE_TRUE);
        }
        return false;
    }

    /**
     * Return whether the node is ignorable whitespace.
     */
    protected boolean nodeIsIgnorable(Node node) {
        XMLPlatform xmlPlatform = XMLPlatformFactory.getInstance().getXMLPlatform();
        return (node instanceof Text) && xmlPlatform.isWhitespaceNode((Text)node);
    }

    /**
     * Return whether the node is a null value.
     * e.g. <foo null="true"/> or <foo null="true"></foo>
     */
    protected boolean nodeIsNull(Node node) {
        if (node.hasAttributes()) {
            Node nullAttribute = node.getAttributes().getNamedItem(NULL_ATTRIBUTE_NAME);
            String nullAttributeValue = null;
            if (nullAttribute != null) {
                nullAttributeValue = nullAttribute.getNodeValue();
            }

            return (nullAttributeValue != null) && nullAttributeValue.equals(ATTRIBUTE_VALUE_TRUE);
        }
        return false;
    }

    /**
     * Return whether the node is simple (no nodes are nested within it).
     */
    protected boolean nodeIsSimple(Node node) {
        if (this.nodeIsEmptyAggregate(node)) {
            return false;// <foo emptyAggregate="true"/> or <foo emptyAggregate="true"></foo>
        }

        if (!node.hasChildNodes()) {
            return true;// <foo null="true"/> or <foo></foo> or <foo/>
        }

        // <foo>bar</foo> will return true
        return (node.getChildNodes().getLength() == 1) && (node.getFirstChild() instanceof Text);
    }

    /**
     * Read an XML document from the specified input stream and
     * convert it to a TopLink database row.
     * Close the stream and return the row.
     */
    public Record read(Reader inputStream) throws XMLDataStoreException {
        Document document = null;
        try {
            document = this.buildDocument(inputStream);
        } finally {
            try {
                inputStream.close();
            } catch (IOException exception) {
                throw XMLDataStoreException.unableToCloseReadStream(this, exception);
            }
        }
        return this.buildRecord(document.getDocumentElement());
    }

    public String toString() {
        return Helper.getShortClassName(this);
    }
}