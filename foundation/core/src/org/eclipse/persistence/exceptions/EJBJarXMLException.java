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

import org.eclipse.persistence.exceptions.i18n.*;

/**
 * <P><B>Purpose</B>: Reading or writing ejb-jar.xml deployment descriptor raise this exception
 */
public class EJBJarXMLException extends EclipseLinkException {
    public static final int READ_EXCEPTION = 72000;
    public static final int INVALID_DOC_TYPE = 72001;

    // MW error codes
    public static final int CONCRETE_INSTANCE_VARIALBE_EXISTS = 72002;
    public static final int EJB_2_0_ATTRIBUTE_NOT_EXIST = 72003;
    public static final int NEITHER_HOME_NOR_REMOTE_INTERFACE_FOUND = 72004;
    public static final int FINDER_NOT_EXIST_ON_REMOTE_HOME_AND_LOCAL_HOME = 72005;
    public static final int NO_PERSISTENCE_TYPE_SPECIFIED = 72006;
    public static final int SELECT_NOT_DEFINED_IN_BEANCLASS = 72007;
    public static final int EJB_DESCRIPTOR_MUST_HAVE_EJB_NAME = 72008;
    public static final int EJB_DESCRIPTOR_MUST_HAVE_PRIMARYKEY_CLASS = 72009;
    public static final int EJB_NAME_MUST_BE_UNIQUE = 72010;
    public static final int EMPTY_TEXT_ATTRIBUTE = 72011;
    public static final int MULTIPLE_ENTITIES_FOUND_FOR_EJB_NAME = 72012;
    public static final int INVALID_CMP_VERSION = 72013;
    public static final int INVALID_EJB_NAME_FOR_RELATIONSHIP_ROLE = 72014;
    public static final int INVALID_MULTIPLICITY = 72015;
    public static final int INVALID_PERSISTENCE_TYPE = 72016;
    public static final int INVALID_QUERY_METHOD_NAME = 72017;
    public static final int NOT_SINGLE_PERSISTENCE_TYPE = 72018;
    public static final int PROJECT_MUST_HAVE_AT_LEAST_ONE_EJB_DESCRIPTOR = 72019;
    public static final int ATTRIBUTE_NOT_EXIST = 72020;
    public static final int EJB_CLASS_NOT_FOUND = 72021;
    public static final int REQUIRED_ATTRIBUTE_NOT_EXIST = 72022;
    public static final int NO_CMR_FIELD_FOR_BEAN_ABSTRACT_SETTER = 72023;

    public EJBJarXMLException(String message) {
        super(message);
    }

    public EJBJarXMLException(String message, Exception internalException) {
        super(message, internalException);
    }

    public static EJBJarXMLException errorReadingDescriptor(Exception exception) {
        Object[] args = {  };

        EJBJarXMLException resourceException = new EJBJarXMLException(ExceptionMessageGenerator.buildMessage(EJBJarXMLException.class, READ_EXCEPTION, args), exception);
        resourceException.setErrorCode(READ_EXCEPTION);
        return resourceException;
    }

    public static EJBJarXMLException nonEJB_2_0_DocType() {
        Object[] args = {  };

        EJBJarXMLException resourceException = new EJBJarXMLException(ExceptionMessageGenerator.buildMessage(EJBJarXMLException.class, INVALID_DOC_TYPE, args));
        resourceException.setErrorCode(INVALID_DOC_TYPE);
        return resourceException;
    }

    public static EJBJarXMLException noCMRForAbstractSetter(String attributeName, String beanName) {
        Object[] args = new Object[2];
        args[0] = attributeName;
        args[1] = beanName;
        EJBJarXMLException resourceException = new EJBJarXMLException(ExceptionMessageGenerator.buildMessage(EJBJarXMLException.class, NO_CMR_FIELD_FOR_BEAN_ABSTRACT_SETTER, args));
        resourceException.setErrorCode(NO_CMR_FIELD_FOR_BEAN_ABSTRACT_SETTER);
        return resourceException;
    }
}