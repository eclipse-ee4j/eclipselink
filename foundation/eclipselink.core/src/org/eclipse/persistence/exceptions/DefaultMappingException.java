/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.exceptions;

import java.util.List;
import org.eclipse.persistence.exceptions.i18n.ExceptionMessageGenerator;

/**
 * <P><B>Purpose</B>:
 * Wrapper for any exception that occurred through OC4J cmp deafult mapping.
 */
public class DefaultMappingException extends EclipseLinkException {
    public final static int FINDER_PARAMETER_TYPE_NOT_FOUND = 20001;
    public final static int FINDER_NOT_DEFINED_IN_HOME = 20002;
    public final static int EJB_SELECT_NOT_DEFINED_IN_BEAN = 20003;
    public final static int FINDER_NOT_START_WITH_FIND_OR_EJBSELECT = 20004;
    public final static int GETTER_NOT_FOUND = 20005;
    public final static int FIELD_NOT_FOUND = 20006;

    public DefaultMappingException(String message) {
        super(message);
    }

    protected DefaultMappingException(String message, Exception internalException) {
        super(message, internalException);
    }

    public static DefaultMappingException finderParameterTypeNotFound(String beanName, String finderName, String finderParameterTypeString) {
        Object[] args = { beanName, finderName, finderParameterTypeString };

        DefaultMappingException exception = new DefaultMappingException(ExceptionMessageGenerator.buildMessage(DefaultMappingException.class, FINDER_PARAMETER_TYPE_NOT_FOUND, args));
        exception.setErrorCode(FINDER_PARAMETER_TYPE_NOT_FOUND);
        return exception;
    }

    public static DefaultMappingException finderNotDefinedInHome(String beanName, String finderName, List finderParameters) {
        Object[] args = { beanName, finderName, finderParameters.toArray() };
        DefaultMappingException exception = new DefaultMappingException(ExceptionMessageGenerator.buildMessage(DefaultMappingException.class, FINDER_NOT_DEFINED_IN_HOME, args));
        exception.setErrorCode(FINDER_NOT_DEFINED_IN_HOME);
        return exception;
    }

    public static DefaultMappingException finderNotStartWithFindOrEjbSelect(String beanName, String finderName) {
        Object[] args = { beanName, finderName };
        DefaultMappingException exception = new DefaultMappingException(ExceptionMessageGenerator.buildMessage(DefaultMappingException.class, FINDER_NOT_START_WITH_FIND_OR_EJBSELECT, args));
        exception.setErrorCode(FINDER_NOT_START_WITH_FIND_OR_EJBSELECT);
        return exception;
    }

    public static DefaultMappingException ejbSelectNotDefinedInBean(String beanName, String ejbSelectName, List ejbSelectParameters) {
        Object[] args = { beanName, ejbSelectName, ejbSelectParameters.toArray() };
        DefaultMappingException exception = new DefaultMappingException(ExceptionMessageGenerator.buildMessage(DefaultMappingException.class, EJB_SELECT_NOT_DEFINED_IN_BEAN, args));
        exception.setErrorCode(EJB_SELECT_NOT_DEFINED_IN_BEAN);
        return exception;
    }

    public static DefaultMappingException getterNotFound(String getter, String beanName) {
        Object[] args = { getter, beanName };
        DefaultMappingException exception = new DefaultMappingException(ExceptionMessageGenerator.buildMessage(DefaultMappingException.class, GETTER_NOT_FOUND, args));
        exception.setErrorCode(GETTER_NOT_FOUND);
        return exception;
    }

    public static DefaultMappingException fieldNotFound(String field, String beanName) {
        Object[] args = { field, beanName };
        DefaultMappingException exception = new DefaultMappingException(ExceptionMessageGenerator.buildMessage(DefaultMappingException.class, FIELD_NOT_FOUND, args));
        exception.setErrorCode(FIELD_NOT_FOUND);
        return exception;
    }
}