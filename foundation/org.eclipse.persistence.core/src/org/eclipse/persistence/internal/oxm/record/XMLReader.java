/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
package org.eclipse.persistence.internal.oxm.record;

import java.io.IOException;

import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.oxm.mappings.XMLMapping;

/**
 * INTERNAL:
 * <p><b>Purpose:</b>Provide a wrapper for an org.xml.sax.XMLReader instance and define some extra
 * event methods that can be used by TopLink during the unmarshal process. These events are no ops
 * in this class, but may be overridden in subclasses.
 * <p><b>Responsibilities</b><ul>
 * <li>Wrap an instance of org.xml.sax.XMLReader and provide all the required API</li>
 * <li>Provide empty implementations of some callback methods that can be overridden in subclasses</li>
 * 
 *  @see org.eclipse.persistence.internal.oxm.record.DOMReader
 *  @author  mmacivor
 *  @since   release specific (what release of product did this appear in)
 */

public class XMLReader implements org.xml.sax.XMLReader {

    private org.xml.sax.XMLReader reader;

    public XMLReader(org.xml.sax.XMLReader internalReader) {
        this.reader = internalReader;
    }

    public XMLReader() {}

    public boolean getFeature (String name) throws SAXNotRecognizedException, SAXNotSupportedException {
        return reader.getFeature(name);
    }
    
    public void setFeature (String name, boolean value) throws SAXNotRecognizedException, SAXNotSupportedException {
        reader.setFeature(name, value);
    }
    
    public Object getProperty (String name) throws SAXNotRecognizedException, SAXNotSupportedException {
        return reader.getProperty(name);
    }

    public void setProperty (String name, Object value) throws SAXNotRecognizedException, SAXNotSupportedException {
        reader.setProperty(name, value);
    }
        
    public void setEntityResolver (EntityResolver resolver) {
        reader.setEntityResolver(resolver);
    }

    public EntityResolver getEntityResolver () {
        return reader.getEntityResolver();
    }

    public void setDTDHandler (DTDHandler handler) {
        reader.setDTDHandler(handler);
    }

    public DTDHandler getDTDHandler () {
        return reader.getDTDHandler();
    }

    public void setContentHandler (ContentHandler handler) {
        reader.setContentHandler(handler);
    }
    

    public ContentHandler getContentHandler () {
        return reader.getContentHandler();
    }

    public void setErrorHandler (ErrorHandler handler) {
        reader.setErrorHandler(handler);
    }

    public ErrorHandler getErrorHandler () {
        return reader.getErrorHandler();
    }

    public void parse(InputSource input) throws IOException, SAXException {
        reader.parse(input);
    }


    public void parse (String systemId) throws IOException, SAXException {
        reader.parse(systemId);
    }
 
    public void newObjectEvent(Object object, Object parent, XMLMapping selfRecordMapping) {
        //no op in this class.
    }

    public Object getCurrentObject(AbstractSession session, XMLMapping selfRecordMapping) {
        return null;
    }

}
