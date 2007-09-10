/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package deprecated.xml.stream;

import java.io.*;
import java.util.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.platform.xml.XMLParser;
import org.eclipse.persistence.platform.xml.XMLPlatform;
import org.eclipse.persistence.platform.xml.XMLPlatformFactory;
import org.eclipse.persistence.platform.xml.XMLTransformer;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.sessions.Record;
import deprecated.xml.xerces.DefaultXMLTranslator;

/**
 * INTERNAL:
 * <p>
 * <b>Purpose</b>:
 * <p> Implementation for writing SDK output in to a single stream
 *
 * @deprecated since OracleAS TopLink 10<i>g</i> (10.1.3).  This class is replaced by
 *         {@link org.eclipse.persistence.oxm}
 */
public class XMLStreamDatabase {
    public final static String ROOT_NODE_NAME = "toplink-command";
    private Hashtable elementStore;

    public XMLStreamDatabase() {
        super();
        elementStore = new Hashtable();
    }

    public XMLStreamDatabase(Document document, Project project) {
        this();
        XMLPlatform xmlPlatform = XMLPlatformFactory.getInstance().getXMLPlatform();
        XMLTransformer xmlTransformer = xmlPlatform.newXMLTransformer();
        try {
            Iterator descriptors = project.getDescriptors().values().iterator();
            while (descriptors.hasNext()) {
                ClassDescriptor descriptor = (ClassDescriptor)descriptors.next();
                if (descriptor.isAggregateDescriptor()) {
                    continue;
                }

                String xmlElementName = descriptor.getTableName();
                NodeList xmlElements = document.getElementsByTagName(xmlElementName);

                for (int x = 0; x < xmlElements.getLength(); x++) {
                    Element node = (Element)xmlElements.item(x);

                    if (node.getParentNode().getNodeName().equalsIgnoreCase(ROOT_NODE_NAME)) {
                        StringWriter stringWriter = new StringWriter();
                        xmlTransformer.transform(node, stringWriter);
                        String elementString = stringWriter.toString();

                        // Build the unique key
                        List orderedPrimaryKeyElements = descriptor.getPrimaryKeyFields();
                        DefaultXMLTranslator translator = new DefaultXMLTranslator();

                        Record row = translator.read(new StringReader(elementString));
                        String uniqueKey = buildUniqueKey(row, orderedPrimaryKeyElements);

                        getXMLWriters(xmlElementName).put(uniqueKey, stringWriter);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * INTERNAL:
     * return an XMLDocument as XMLCommandConverter expected
     */
    public Document getDocument() {
        XMLPlatform xmlPlatform = XMLPlatformFactory.getInstance().getXMLPlatform();
        XMLParser parser = xmlPlatform.newXMLParser();
        parser.setNamespaceAware(false);
        parser.setWhitespacePreserving(false);
        Document document = null;
        Node newChild;

        try {
            document = xmlPlatform.createDocument();
            document.appendChild(document.createElement(ROOT_NODE_NAME));

            Enumeration elements = elementStore.elements();
            Enumeration xmlStrings;
            while (elements.hasMoreElements()) {
                xmlStrings = ((Hashtable)elements.nextElement()).elements();
                while (xmlStrings.hasMoreElements()) {
                    String xmlString = xmlStrings.nextElement().toString();
                    InputSource inputSource = new InputSource(new StringReader(xmlString));
                    newChild = document.importNode(parser.parse(inputSource).getDocumentElement(), true);
                    document.getDocumentElement().appendChild(newChild);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return document;
    }

    void putWriter(String rootElementName, Record row, Vector orderedPrimaryKeyElements, Writer writer) {
        String uniqueKey = buildUniqueKey(row, orderedPrimaryKeyElements);
        getXMLWriters(rootElementName).put(uniqueKey, writer);
    }

    Writer getWriter(String rootElementName, Record row, Vector orderedPrimaryKeyElements) {
        Hashtable instanceStore = getXMLWriters(rootElementName);
        Writer newWriter = new StringWriter();
        String uniqueKey = buildUniqueKey(row, orderedPrimaryKeyElements);
        getXMLWriters(rootElementName).put(uniqueKey, newWriter);
        return newWriter;
    }

    Reader getReader(String rootElementName, Record row, Vector orderedPrimaryKeyElements) {
        try {
            Hashtable instanceStore = getXMLWriters(rootElementName);
            String uniqueKey = buildUniqueKey(row, orderedPrimaryKeyElements);
            Writer writer = (Writer)getXMLWriters(rootElementName).get(uniqueKey);
            return new StringReader(writer.toString());
        } catch (NullPointerException e) {
            return null;
        }
    }

    Enumeration getReaders(String rootElementName) {
        Vector readers = new Vector();
        Writer writer;
        for (Enumeration writers = getXMLWriters(rootElementName).elements();
                 writers.hasMoreElements();) {
            writer = (Writer)writers.nextElement();
            readers.add(new StringReader(writer.toString()));
        }
        return readers.elements();
    }

    Enumeration getReaders(String rootElementName, Vector foreignKeys, Vector orderedForeignKeyElements) {
        Vector readers = new Vector(foreignKeys.size());
        String uniqueKey;
        Writer writer;
        for (Enumeration stream = foreignKeys.elements(); stream.hasMoreElements();) {
            uniqueKey = buildUniqueKey((AbstractRecord)stream.nextElement(), orderedForeignKeyElements);
            writer = (Writer)getXMLWriters(rootElementName).get(uniqueKey);
            readers.add(new StringReader(writer.toString()));
        }
        return readers.elements();
    }

    void createStreamSource(String rootElementName) {
        if (!this.elementStore.containsKey(rootElementName)) {
            this.elementStore.put(rootElementName, new Hashtable());
        }
    }

    void dropStreamSource(String rootElementName) {
        this.elementStore.remove(rootElementName);
    }

    /**
     * The Writers corresponding to the documents the specified root element
     **/
    private Hashtable getXMLWriters(String rootElementName) {
        createStreamSource(rootElementName);
        return (Hashtable)elementStore.get(rootElementName);
    }

    /**
     * Return a file name for the specified primary key.
     */
    private String buildUniqueKey(Record row, List orderedPrimaryKeyElements) {
        Writer sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        Vector values = this.extractPrimaryKeyValues(row, orderedPrimaryKeyElements);
        for (Enumeration stream = values.elements(); stream.hasMoreElements();) {
            pw.print(stream.nextElement());
        }
        return sw.toString();
    }

    /**
     * Extract the values for the specified primary key elements
     * from the specified row. Keep them in the same order
     * as the elements.
     */
    private Vector extractPrimaryKeyValues(Record row, List orderedPrimaryKeyElements) {
        Vector result = new Vector(orderedPrimaryKeyElements.size());
        for (int index = 0; index < orderedPrimaryKeyElements.size(); index++) {
            result.addElement(row.get((DatabaseField)orderedPrimaryKeyElements.get(index)));
        }
        return result;
    }
}