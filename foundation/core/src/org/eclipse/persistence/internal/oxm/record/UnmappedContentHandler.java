/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.oxm.record;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 * INTERNAL:
 * This class receives all events corresponding to unmapped XML content.
 */
public class UnmappedContentHandler implements ContentHandler {

	private int depth;
	private XMLReader xmlReader;
	private ContentHandler parentContentHandler;
	
	public UnmappedContentHandler(XMLReader xmlReader, ContentHandler parentContentHandler) {
		super();
		this.depth = 0;
		this.xmlReader = xmlReader;
		this.parentContentHandler = parentContentHandler;
	}
	
	public void characters(char[] ch, int start, int length)
			throws SAXException {
	}

	public void endDocument() throws SAXException {
	}

	public void endElement(String uri, String localName, String name)
			throws SAXException {
		this.depth--;
		if(0 == depth) {
			xmlReader.setContentHandler(this.parentContentHandler);
		}
	}

	public void endPrefixMapping(String prefix) throws SAXException {
	}

	public void ignorableWhitespace(char[] ch, int start, int length)
			throws SAXException {
	}

	public void processingInstruction(String target, String data)
			throws SAXException {
	}

	public void setDocumentLocator(Locator locator) {
	}

	public void skippedEntity(String name) throws SAXException {
	}

	public void startDocument() throws SAXException {
	}

	public void startElement(String uri, String localName, String name,
			Attributes atts) throws SAXException {
		this.depth++;
	}

	public void startPrefixMapping(String prefix, String uri)
			throws SAXException {
	}
	
}