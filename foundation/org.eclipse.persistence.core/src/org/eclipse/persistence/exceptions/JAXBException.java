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
* mmacivor - June 11/2008 - 1.0 - Initial implementation
******************************************************************************/
package org.eclipse.persistence.exceptions;

import org.eclipse.persistence.exceptions.i18n.ExceptionMessageGenerator;

/**
 * <b>Purpose:</b>
 * <ul><li>This class provides an implementation of EclipseLinkException specific to the EclipseLink JAXB implementation</li>
 * </ul>
 * <p/>
 * <b>Responsibilities:</b>
 * <ul>
 * <li>Return a JAXBException that can be thrown around input parameters.
 * <li>Return a JAXBException that wraps an existing exception with additional input parameters.
 * </ul>
 * @since Oracle EclipseLink 1.0
 */
public class JAXBException extends EclipseLinkException {

    public static final int NO_OBJECT_FACTORY_OR_JAXB_INDEX_IN_PATH = 50000;
    
    protected JAXBException(String message) {
        super(message);
    }

    protected JAXBException(String message, Exception internalException) {
        super(message, internalException);
    }

    
    public static JAXBException noObjectFactoryOrJaxbIndexInPath(String path) {
        Object[] args = { path };
        JAXBException exception = new JAXBException(ExceptionMessageGenerator.buildMessage(JAXBException.class, NO_OBJECT_FACTORY_OR_JAXB_INDEX_IN_PATH, args));
        exception.setErrorCode(NO_OBJECT_FACTORY_OR_JAXB_INDEX_IN_PATH);
        return exception;
    }
}
