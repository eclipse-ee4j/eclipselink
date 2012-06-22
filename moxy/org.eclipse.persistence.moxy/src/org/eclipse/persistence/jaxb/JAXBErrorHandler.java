/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.jaxb;

import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.helpers.ValidationEventImpl;
import javax.xml.bind.helpers.ValidationEventLocatorImpl;

import org.eclipse.persistence.oxm.record.ValidatingMarshalRecord.MarshalSAXParseException;
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
        handleException(exception, ValidationEvent.WARNING);
    }

    public void error(SAXParseException exception) throws SAXException {
        handleException(exception, ValidationEvent.ERROR);
    }

    public void fatalError(SAXParseException exception) throws SAXException {
        handleException(exception, ValidationEvent.FATAL_ERROR);
    }

    private void handleException(SAXParseException exception, int severity) throws SAXException {
        ValidationEventLocatorImpl eventLocator = new ValidationEventLocatorImpl(exception);
        if(exception instanceof MarshalSAXParseException) {
            eventLocator.setObject(((MarshalSAXParseException) exception).getObject());
        }
        ValidationEvent event = new ValidationEventImpl(severity, exception.getLocalizedMessage(), eventLocator, exception);
        if (!eventHandler.handleEvent(event)) {
            throw exception;
        }
    }

    public ValidationEventHandler getValidationEventHandler() {
        return eventHandler;
    }

}