/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//  - rbarkhouse - 08 February 2012 - 2.3.3 - Initial implementation
package org.eclipse.persistence.internal.oxm.record;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Method;

import org.eclipse.persistence.exceptions.XMLMarshalException;
import org.eclipse.persistence.internal.core.helper.CoreClassConstants;
import org.eclipse.persistence.internal.oxm.CharacterEscapeHandler;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;

/**
 * INTERNAL:
 * <p>
 * This class provides an implementation of CharacterEscapeHandler that wraps
 * a CharacterEscapeHandler from the Sun JAXB Implementation.
 * </p>
 */
public class CharacterEscapeHandlerWrapper implements CharacterEscapeHandler {

    private final static String ESCAPE_METHOD_NAME = "escape";
    private final static Class[] PARAMS = new Class[] {
        CoreClassConstants.APCHAR, CoreClassConstants.PINT, CoreClassConstants.PINT, CoreClassConstants.PBOOLEAN, Writer.class };

    private Object handler;
    private Method escapeMethod;

    public CharacterEscapeHandlerWrapper(Object sunHandler) {
        this.handler = sunHandler;
        Class handlerClass = sunHandler.getClass();

        try {
            this.escapeMethod = PrivilegedAccessHelper.getMethod(handlerClass, ESCAPE_METHOD_NAME, PARAMS, false);
        } catch (Exception ex) {
            throw XMLMarshalException.errorProcessingCharacterEscapeHandler(ESCAPE_METHOD_NAME, sunHandler, ex);
        }
    }

    public void escape(char[] buffer, int start, int length, boolean isAttributeValue, Writer out) throws IOException {
        try {
            Object[] params = new Object[] { buffer, start, length, isAttributeValue, out };
            PrivilegedAccessHelper.invokeMethod(this.escapeMethod, this.handler, params);
        } catch (Exception ex) {
            throw XMLMarshalException.errorInvokingCharacterEscapeHandler(ESCAPE_METHOD_NAME, this.handler, ex);
        }
    }

    public Object getHandler() {
        return this.handler;
    }

}
