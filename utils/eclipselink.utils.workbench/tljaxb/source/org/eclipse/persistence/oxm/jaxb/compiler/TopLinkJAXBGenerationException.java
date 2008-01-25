/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.oxm.jaxb.compiler;


/**
 *
 * @author lddavis
 *
 * For exceptions that occur during JAXB project generation
 */
public class TopLinkJAXBGenerationException extends Exception {
    Exception wrappedException;

    public TopLinkJAXBGenerationException() {
        super();
    }

    public TopLinkJAXBGenerationException(Exception exception) {
        super(exception);
        this.wrappedException = exception;
    }

    public Exception getWrappedException() {
        return this.wrappedException;
    }
}