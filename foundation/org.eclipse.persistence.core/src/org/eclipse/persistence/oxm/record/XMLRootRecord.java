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
package org.eclipse.persistence.oxm.record;

import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.oxm.XMLRoot;
import org.eclipse.persistence.internal.oxm.StrBuffer;
import org.eclipse.persistence.internal.oxm.XMLConversionManager;
import org.eclipse.persistence.internal.oxm.mappings.Mapping;
import org.eclipse.persistence.internal.oxm.record.UnmarshalRecordImpl;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * Record for handling simple root elements that have a single text child node, 
 * and are being unmarshalled to a primitive wrapper object.  The characters
 * method will be used to gather the text to be converted.
 */
public class XMLRootRecord extends UnmarshalRecord {

    private Class targetClass;
    private StrBuffer characters;
    private boolean shouldReadChars;
    private int elementCount;

    /**
     * Default constructor.
     */
    public XMLRootRecord(Class cls) {
        super(new UnmarshalRecordImpl(null));
        targetClass = cls;
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (characters == null) {
            characters = new StrBuffer();
        }

        if (shouldReadChars) {
            characters.append(ch, start, length);
        }
    }

    @Override
    public void characters(CharSequence characters) throws SAXException {
        if(null != characters) {
            String string = characters.toString();
            characters(string.toCharArray(), 0, string.length());
        }
    }

    @Override
    public void endDocument() throws SAXException {
    }

    @Override
    public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
        // once the root element is closed (or any sub-elements for that matter) we don't
        // want to process any more characters
        shouldReadChars = false;
    }

    /**
     * Return a populated XMLRoot object.
     */
    @Override
    public Object getCurrentObject() {
        XMLRoot xmlRoot = new XMLRoot();
        xmlRoot.setLocalName(getLocalName());
        xmlRoot.setNamespaceURI(getRootElementNamespaceUri());
        if(currentObject == null) {
            // this assumes that since we're unmarshalling to a primitive wrapper, the root
            // element has a single text node.  if, however, the root element doesn't have
            // a text node as a first child, we'll try converting null
            String val = null;
            if (characters != null) {
                val = characters.toString();
            }
            xmlRoot.setObject(((XMLConversionManager) session.getDatasourcePlatform().getConversionManager()).convertObject(val, targetClass));
        } else {
            xmlRoot.setObject(currentObject);
        }
        return xmlRoot;
    }

    @Override
    public void startDocument() throws SAXException {
        characters = null;
        elementCount = 0;
        shouldReadChars = true;
    }

    @Override
    public void initializeRecord(Mapping selfRecordMapping) throws SAXException {
    }
   	
    @Override
    public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
        // set the root element's prefix qualified name and namespace prefix
        if (getRootElementName() == null) {
            setRootElementName(qName);
            setLocalName(localName);
            setRootElementNamespaceUri(namespaceURI);
        }
        if(XMLConstants.EMPTY_STRING.equals(localName)) {
            return;
        }
        elementCount++;
        if (elementCount > 1) {
            // we only want to process characters from the first text child;
            // if a subelement occurs, we will stop
            shouldReadChars = false;
        }
    }

}