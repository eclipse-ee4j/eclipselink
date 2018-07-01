/*
 * Copyright (c) 2014, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Marcel Valovy - 2.6 - initial implementation
package org.eclipse.persistence.exceptions;

import org.eclipse.persistence.exceptions.i18n.ExceptionMessageGenerator;

/**
 * BeanValidationException should be used to represent any exception that happens during Bean Validation in MOXy.
 *
 * @author Marcel Valovy - marcel.valovy@oracle.com
 * @since 2.6
 */
public final class BeanValidationException extends EclipseLinkException {

    /* 7500-7519 reserved for runtime. */
    public static final int PROVIDER_NOT_FOUND = 7500;
    public static final int ILLEGAL_VALIDATION_MODE = 7501;
    public static final int CONSTRAINT_VIOLATION = 7510;

    /* 7520-7530 reserved for Schemagen. */
    public static final int NOT_NULL_AND_NILLABLE = 7525;


    /**
     * INTERNAL:
     * EclipseLink exceptions should only be thrown by EclipseLink.
     */
    public BeanValidationException(String msg) {
        super(msg);
    }

    /**
     * INTERNAL:
     * EclipseLink exceptions should only be thrown by EclipseLink.
     */
    public BeanValidationException(String msg, Throwable internalException) {
        super(msg, internalException);
    }


    /* Runtime. */
    public static BeanValidationException constraintViolation(Object[] args, Throwable internalException) {
        BeanValidationException bve = new BeanValidationException(ExceptionMessageGenerator.buildMessage
                (BeanValidationException.class, CONSTRAINT_VIOLATION, args), internalException);
        bve.setErrorCode(CONSTRAINT_VIOLATION);
        return bve;
    }

    public static BeanValidationException providerNotFound(String prefix, Throwable internalException) {
        BeanValidationException bve = new BeanValidationException(ExceptionMessageGenerator.buildMessage
                (BeanValidationException.class, PROVIDER_NOT_FOUND, new Object[]{prefix}), internalException);
        bve.setErrorCode(PROVIDER_NOT_FOUND);
        return bve;
    }


    public static BeanValidationException illegalValidationMode(String prefix, String modeName) {
        BeanValidationException bve = new BeanValidationException(ExceptionMessageGenerator.buildMessage
                (BeanValidationException.class, ILLEGAL_VALIDATION_MODE, new Object[]{prefix, modeName}));
        bve.setErrorCode(ILLEGAL_VALIDATION_MODE);
        return bve;
    }

    /* SchemaGen. */
    public static BeanValidationException notNullAndNillable(String propertyName) {
        BeanValidationException bve = new BeanValidationException(ExceptionMessageGenerator.buildMessage
                (BeanValidationException.class, NOT_NULL_AND_NILLABLE, new Object[]{propertyName}));
        bve.setErrorCode(NOT_NULL_AND_NILLABLE);
        return bve;
    }
}
