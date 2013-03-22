/*******************************************************************************
 * Copyright (c) 2011, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 *
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *  - rbarkhouse - 07 October 2011 - 2.4 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.jaxb;

import javax.xml.bind.ValidationEventHandler;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

/**
 * <p>
 * IDResolver can be subclassed to allow customization of the ID/IDREF processing of
 * JAXBUnmarshaller.  A custom IDResolver can be specified on the Unmarshaller as follows:
 * </p>
 *
 * <p>
 * <code>
 * IDResolver customResolver = new MyIDResolver();
 * jaxbUnmarshaller.setProperty(JAXBContext.ID_RESOLVER, customResolver);
 * </code>
 * </p>
 *
 * @see JAXBUnmarshaller
 * @since 2.3.3
 */
public abstract class IDResolver extends org.eclipse.persistence.oxm.IDResolver {

    /**
     * <p>
     * Called when unmarshalling begins.
     * </p>
     *
     * @param eventHandler Any errors encountered during the unmarshal process should be reported to this handler.
     *
     * @throws SAXException
     */
    public void startDocument(ValidationEventHandler eventHandler) throws SAXException {}

    /**
     * INTERNAL
     */
    public final void startDocument(ErrorHandler errorHandler) throws SAXException {
        JAXBErrorHandler jeh = (JAXBErrorHandler) errorHandler;
        startDocument(jeh.getValidationEventHandler());
    }

}