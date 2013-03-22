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
package org.eclipse.persistence.eis;

import javax.resource.ResourceException;
import org.eclipse.persistence.exceptions.i18n.ExceptionMessageGenerator;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.queries.Call;

/**
 * <p> Use an <code>EISException</code> when any problem is detected while 
 * interacting with an EIS datasource
 * 
 * @author James
 * @since OracleAS TopLink 10<i>g</i> (10.0.3)
 */
public class EISException extends org.eclipse.persistence.exceptions.DatabaseException { 
    public static final int INCORRECT_LOGIN_INSTANCE_PROVIDED = 17002;
    public static final int PROP_NOT_SET = 17007;
    public static final int INVALID_PROP = 17008;
    public static final int PROPS_NOT_SET = 17009;
    public static final int OUTPUT_UNSUPPORTED_MSG_TYPE = 17010;
    public static final int NO_CONN_FACTORY = 17011;
    public static final int INVALID_INTERACTION_SPEC_TYPE = 17012;
    public static final int INVALID_RECORD_TYPE = 17013;
    public static final int UNKNOWN_INTERACTION_SPEC_TYPE = 17014;
    public static final int INVALID_INPUT = 17015;
    public static final int TIMEOUT = 17016;
    public static final int INPUT_UNSUPPORTED_MSG_TYPE = 17017;
    public static final int INVALID_METHOD_INVOCATION = 17018;
    public static final int TX_SESSION_TEST_ERROR = 17019;
    public static final int INVALID_AQ_INTERACTION_SPEC_TYPE = 17020;
    public static final int INVALID_AQ_RECORD_TYPE = 17021;
    public static final int INVALID_AQ_INPUT = 17022;
    public static final int INVALID_FACTORY_ATTRIBUTES = 17023;
    public static final int COULD_NOT_DELETE_FILE = 17024;
    public static final int GROUPING_ELEMENT_REQUIRED = 17025;
    public static final int EIS_EXCEPTION = 91000;
    public static final int RESOURCE_EXCEPTION = 90000;

    public EISException(Exception exception) {
        this(exception, exception.toString());
    }

    public EISException(String message) {
        super(message);
    }

    public EISException(Exception exception, String message) {
        this(message);
        setInternalException(exception);
    }

    public static EISException resourceException(Exception resourceException, EISAccessor accessor, AbstractSession session) {
        EISException exception = new EISException(resourceException);
        exception.setErrorCode(RESOURCE_EXCEPTION);
        exception.setInternalException(resourceException);
        exception.setAccessor(accessor);
        exception.setSession(session);
        return exception;
    }

    public static EISException resourceException(ResourceException resourceException, EISAccessor accessor, AbstractSession session) {
        return resourceException((Exception)resourceException, accessor, session);
    }

    public static EISException resourceException(ResourceException resourceException, Call call, EISAccessor accessor, AbstractSession session) {
        EISException exception = resourceException(resourceException, accessor, session);
        exception.setCall(call);
        return exception;
    }

    public static EISException createResourceException(Object[] args, int errorCode) {
        ResourceException resourceException = new ResourceException(ExceptionMessageGenerator.buildMessage(EISException.class, errorCode, args));
        EISException exception = new EISException(resourceException);
        exception.setErrorCode(RESOURCE_EXCEPTION);
        exception.setInternalException(resourceException);
        return exception;
    }

    public static EISException createException(Exception ex) {
        EISException exception = new EISException(ex);
        exception.setErrorCode(EIS_EXCEPTION);
        return exception;
    }

    public static EISException createException(Object[] args, int errorCode) {
        EISException exception = new EISException(ExceptionMessageGenerator.buildMessage(EISException.class, errorCode, args));
        exception.setErrorCode(errorCode);
        return exception;
    }

    public static EISException propertyNotSet(String property) {
        return EISException.createException(new Object[] { property }, PROP_NOT_SET);
    }

    public static EISException propertiesNotSet(String property1, String property2) {
        return EISException.createException(new Object[] { property1, property2 }, PROPS_NOT_SET);
    }

    public static EISException invalidProperty(String property) {
        return EISException.createException(new Object[] { property }, INVALID_PROP);
    }

    public static EISException unsupportedMessageInOutputRecord() {
        return EISException.createException(new Object[] {  }, OUTPUT_UNSUPPORTED_MSG_TYPE);
    }

    public static EISException unsupportedMessageInInputRecord() {
        return EISException.createException(new Object[] {  }, INPUT_UNSUPPORTED_MSG_TYPE);
    }

    public static EISException noConnectionFactorySpecified() {
        return EISException.createException(new Object[] {  }, NO_CONN_FACTORY);
    }

    public static EISException invalidInteractionSpecType() {
        return EISException.createException(new Object[] {  }, INVALID_INTERACTION_SPEC_TYPE);
    }

    public static EISException invalidAQInteractionSpecType() {
        return EISException.createResourceException(new Object[] {  }, INVALID_AQ_INTERACTION_SPEC_TYPE);
    }

    public static EISException invalidRecordType() {
        return EISException.createException(new Object[] {  }, INVALID_RECORD_TYPE);
    }

    public static EISException invalidAQRecordType() {
        return EISException.createResourceException(new Object[] {  }, INVALID_AQ_RECORD_TYPE);
    }

    public static EISException unknownInteractionSpecType() {
        return EISException.createException(new Object[] {  }, UNKNOWN_INTERACTION_SPEC_TYPE);
    }

    public static EISException invalidConnectionFactoryAttributes() {
        return EISException.createResourceException(new Object[] {  }, INVALID_FACTORY_ATTRIBUTES);
    }

    public static EISException invalidInput() {
        return EISException.createException(new Object[] {  }, INVALID_INPUT);
    }

    public static EISException invalidAQInput() {
        return EISException.createResourceException(new Object[] {  }, INVALID_AQ_INPUT);
    }

    public static EISException timeoutOccurred() {
        return EISException.createException(new Object[] {  }, TIMEOUT);
    }

    public static EISException invalidMethodInvocation() {
        return EISException.createException(new Object[] {  }, INVALID_METHOD_INVOCATION);
    }

    public static EISException transactedSessionTestError() {
        return EISException.createException(new Object[] {  }, TX_SESSION_TEST_ERROR);
    }

    public static EISException groupingElementRequired() {
        return EISException.createException(new Object[] {  }, GROUPING_ELEMENT_REQUIRED);
    }

    public static EISException couldNotDeleteFile(Object[] args) {
        return EISException.createResourceException(args, COULD_NOT_DELETE_FILE);
    }
    
    public static EISException incorrectLoginInstanceProvided(Class loginClass) {
        Object[] args = { loginClass };
        EISException exception = new EISException(ExceptionMessageGenerator.buildMessage(EISException.class, INCORRECT_LOGIN_INSTANCE_PROVIDED, args));
        exception.setErrorCode(INCORRECT_LOGIN_INSTANCE_PROVIDED);
        return exception;
    }
}
