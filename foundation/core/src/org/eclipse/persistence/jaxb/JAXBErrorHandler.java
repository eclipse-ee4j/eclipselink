/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.jaxb;

import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.ValidationEventLocator;
import javax.xml.bind.helpers.ValidationEventImpl;
import javax.xml.bind.helpers.ValidationEventLocatorImpl;
import org.xml.sax.SAXParseException;
import org.xml.sax.SAXException;
import org.xml.sax.ErrorHandler;

public class JAXBErrorHandler implements ErrorHandler {
    private ValidationEventHandler eventHandler;

    public JAXBErrorHandler(ValidationEventHandler validationEventHandler) {
        super();
        eventHandler = validationEventHandler;
    }

    public void warning(SAXParseException exception) throws SAXException {
        ValidationEventLocator eventLocator = new ValidationEventLocatorImpl(exception);
        ValidationEvent event = new ValidationEventImpl(ValidationEvent.WARNING, null, eventLocator, exception);
        if (!eventHandler.handleEvent(event)) {
            throw exception;
        }
    }

    public void error(SAXParseException exception) throws SAXException {
        ValidationEventLocator eventLocator = new ValidationEventLocatorImpl(exception);
        ValidationEvent event = new ValidationEventImpl(ValidationEvent.ERROR, null, eventLocator, exception);
        if (!eventHandler.handleEvent(event)) {
            throw exception;
        }
    }

    public void fatalError(SAXParseException exception) throws SAXException {
        ValidationEventLocator eventLocator = new ValidationEventLocatorImpl(exception);
        ValidationEvent event = new ValidationEventImpl(ValidationEvent.FATAL_ERROR, null, eventLocator, exception);
        if (!eventHandler.handleEvent(event)) {
            throw exception;
        }
    }
}