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
package org.eclipse.persistence.jaxb;

import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.helpers.ValidationEventImpl;
import javax.xml.bind.helpers.ValidationEventLocatorImpl;

import org.eclipse.persistence.exceptions.EclipseLinkException;
import org.eclipse.persistence.oxm.record.ValidatingMarshalRecord.MarshalSAXParseException;
import org.xml.sax.SAXParseException;
import org.xml.sax.SAXException;
import org.xml.sax.ErrorHandler;

/**
 * <p>Implementation of org.xml.sax.ErrorHandler.  When JAXBMarshaller or JAXBUnmarshaller
 * is given a ValidationEventHandler a JAXBErrorHandler is used to wrap it.  This
 * ErrorHandler is then used by the underlying XMLMarshaller or XMLUnmarshaller.
 * </p>
 */
public class JAXBErrorHandler implements ErrorHandler {

    private ValidationEventHandler eventHandler;

    /**
     * Create a new JAXBErrorHandler with the specified ValidationEventHandler
     * @param validationEventHandler
     */
    public JAXBErrorHandler(ValidationEventHandler validationEventHandler) {
        super();
        eventHandler = validationEventHandler;
    }

    /**
     * Handle warnings  
     * The exception will be given to the ValidationEventHandler at to attempt to handle.
     * @param exception the SAXParseException that occurred
     */
    public void warning(SAXParseException exception) throws SAXException {
        handleException(exception, ValidationEvent.ERROR);
    }

    /**
     * Handle errors.  
     * The exception will be given to the ValidationEventHandler at to attempt to handle.
     * @param exception the SAXParseException that occurred
     */
    public void error(SAXParseException exception) throws SAXException {
        handleException(exception, ValidationEvent.FATAL_ERROR);
    }

    /**
     * Handle fatal errors.  
     * The exception will be given to the ValidationEventHandler at to attempt to handle.
     * @param exception the SAXParseException that occurred
     */
    public void fatalError(SAXParseException exception) throws SAXException {
        handleException(exception, ValidationEvent.FATAL_ERROR);
    }

    
    private void handleException(SAXParseException exception, int severity) throws SAXException {
        ValidationEventLocatorImpl eventLocator = new ValidationEventLocatorImpl(exception);
        if(exception instanceof MarshalSAXParseException) {
            eventLocator.setObject(((MarshalSAXParseException) exception).getObject());
        }
        Throwable linkedException = exception.getCause();
        if(linkedException instanceof EclipseLinkException) {
            linkedException = exception.getCause();
        }
        ValidationEvent event = new ValidationEventImpl(severity, exception.getLocalizedMessage(), eventLocator, linkedException);
        if (!eventHandler.handleEvent(event)) {
            if(linkedException instanceof EclipseLinkException) {
                throw (EclipseLinkException) linkedException;
            }
            throw exception;
        }
    }

    /**
     * Return the ValidationEventHandler associated with this JAXBErrorHandler.
     * @return the ValidationEventHandler associated with this JAXBErrorHandler.
     */
    public ValidationEventHandler getValidationEventHandler() {
        return eventHandler;
    }

}