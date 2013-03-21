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
 *     Denise Smith - June 24/2009 - 2.0 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.internal.oxm.record;

import org.eclipse.persistence.internal.oxm.ObjectBuilder;
import org.eclipse.persistence.internal.oxm.NodeValue;
import org.eclipse.persistence.internal.oxm.XMLBinaryDataMappingNodeValue;
import org.eclipse.persistence.internal.oxm.mappings.BinaryDataMapping;
import org.eclipse.persistence.internal.oxm.record.deferred.BinaryMappingContentHandler;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public class BinaryDataUnmarshalRecord extends UnmarshalRecordImpl {
	private BinaryDataMapping xmlBinaryDataMapping;
	private XMLBinaryDataMappingNodeValue xmlBinaryDataMappingNodeValue;
	private UnmarshalRecord parentRecord;
	private BinaryMappingContentHandler handler;
	private ContentHandler activeContentHandler;

	public BinaryDataUnmarshalRecord(ObjectBuilder treeObjectBuilder, UnmarshalRecord parentRecord, XMLBinaryDataMappingNodeValue xmlBinaryDataMappingNodeValue, BinaryDataMapping xmlBinaryDataMapping) {
		super(treeObjectBuilder);
		this.parentRecord = parentRecord;
		this.xmlBinaryDataMappingNodeValue = xmlBinaryDataMappingNodeValue;
		this.xmlBinaryDataMapping = xmlBinaryDataMapping;
		handler = new BinaryMappingContentHandler(parentRecord, xmlBinaryDataMappingNodeValue, xmlBinaryDataMapping);
		activeContentHandler = handler;
	}

	public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
		activeContentHandler.startElement(namespaceURI, localName, qName, atts);
		if (handler.isFinished()) {
			activeContentHandler = handler.getWorkingUnmarshalRecord();
		}
	}

	public void characters(char[] ch, int start, int length) throws SAXException {
		activeContentHandler.characters(ch, start, length);
		if (handler.isFinished()) {
			activeContentHandler = handler.getWorkingUnmarshalRecord();
		}
	}

	public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
		activeContentHandler.endElement(namespaceURI, localName, qName);
		if (handler.isFinished()) {
			activeContentHandler = handler.getWorkingUnmarshalRecord();
		}
	}

	public NodeValue getAttributeChildNodeValue(String namespace, String localName) {
		return null;
	}

	public void endDocument() throws SAXException {
	}

}
