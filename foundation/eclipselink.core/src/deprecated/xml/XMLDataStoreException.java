/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package deprecated.xml;

import org.eclipse.persistence.exceptions.i18n.ExceptionMessageGenerator;
import org.eclipse.persistence.platform.xml.XMLPlatformException;
import org.eclipse.persistence.sessions.Record;

/**
 * Exception used for any problem detected while interacting with the  XML "data store".
 *
 * @author Big Country
 * @since TOPLink/Java 3.0
 * @deprecated since OracleAS TopLink 10<i>g</i> (10.1.3).  This class is replaced by
 *         {@link org.eclipse.persistence.oxm}
 */
public class XMLDataStoreException extends deprecated.sdk.SDKDataStoreException {
    public static final int FILE_NOT_FOUND = 13000;
    public static final int UNABLE_TO_CLOSE_WRITE_STREAM = 13001;
    public static final int NOT_A_DIRECTORY = 13002;
    public static final int DIRECTORY_COULD_NOT_BE_CREATED = 13003;
    public static final int DIRECTORY_NOT_FOUND = 13004;
    public static final int FILE_ALREADY_EXISTS = 13005;
    public static final int UNABLE_TO_CREATE_WRITE_STREAM = 13006;
    public static final int INVALID_FIELD_VALUE = 13007;
    public static final int CLASS_NOT_FOUND = 13008;
    public static final int SAX_PARSER_ERROR = 13009;
    public static final int GENERAL_EXCEPTION = 13010;
    public static final int IOEXCEPTION = 13011;
    public static final int UNABLE_TO_CLOSE_READ_STREAM = 13012;
    public static final int HETEROGENEOUS_CHILD_ELEMENTS = 13013;
    public static final int NO_SUCH_METHOD = 13014;
    public static final int ILLEGAL_ACCESS_EXCEPTION = 13015;
    public static final int INVOCATION_TARGET_EXCEPTION = 13016;
    public static final int INSTANTIATION_EXCEPTION = 13017;
    public static final int INSTANTIATION_ILLEGAL_ACCESS_EXCEPTION = 13018;
    public static final int HETEROGENEOUS_TABLE_NAMES = 13019;
    public static final int ELEMENT_DATA_TYPE_NAME_IS_REQUIRED = 13020;

    protected XMLDataStoreException(Exception exception) {
        super(exception);
    }

    protected XMLDataStoreException(Exception exception, String message) {
        super(exception, message);
    }

    protected XMLDataStoreException(String message) {
        super(message);
    }

    public static XMLDataStoreException classNotFound(String className) {
        Object[] args = { className };

        XMLDataStoreException exception = new XMLDataStoreException(ExceptionMessageGenerator.buildMessage(XMLDataStoreException.class, CLASS_NOT_FOUND, args));
        exception.setErrorCode(CLASS_NOT_FOUND);
        return exception;
    }

    public static XMLDataStoreException directoryCouldNotBeCreated(java.io.File directory) {
        Object[] args = { directory };

        XMLDataStoreException exception = new XMLDataStoreException(ExceptionMessageGenerator.buildMessage(XMLDataStoreException.class, DIRECTORY_COULD_NOT_BE_CREATED, args));
        exception.setErrorCode(DIRECTORY_COULD_NOT_BE_CREATED);
        return exception;
    }

    public static XMLDataStoreException directoryNotFound(java.io.File directory) {
        Object[] args = { directory };

        XMLDataStoreException exception = new XMLDataStoreException(ExceptionMessageGenerator.buildMessage(XMLDataStoreException.class, DIRECTORY_NOT_FOUND, args));
        exception.setErrorCode(DIRECTORY_NOT_FOUND);
        return exception;
    }

    public static XMLDataStoreException elementDataTypeNameIsRequired(java.util.Vector elements) {
        Object[] args = { elements, CR };

        XMLDataStoreException exception = new XMLDataStoreException(ExceptionMessageGenerator.buildMessage(XMLDataStoreException.class, ELEMENT_DATA_TYPE_NAME_IS_REQUIRED, args));
        exception.setErrorCode(ELEMENT_DATA_TYPE_NAME_IS_REQUIRED);
        return exception;
    }

    public static XMLDataStoreException fileAlreadyExists(java.io.File file) {
        Object[] args = { file.getAbsolutePath() };

        XMLDataStoreException exception = new XMLDataStoreException(ExceptionMessageGenerator.buildMessage(XMLDataStoreException.class, FILE_ALREADY_EXISTS, args));
        exception.setErrorCode(FILE_ALREADY_EXISTS);
        return exception;
    }

    public static XMLDataStoreException fileNotFound(java.io.File file, java.io.IOException ioException) {
        Object[] args = { file.getAbsolutePath() };

        XMLDataStoreException exception = new XMLDataStoreException(ExceptionMessageGenerator.buildMessage(XMLDataStoreException.class, FILE_NOT_FOUND, args));
        exception.setErrorCode(FILE_NOT_FOUND);
        exception.setInternalException(ioException);
        return exception;
    }

    public static XMLDataStoreException generalException(Exception exception) {
        Object[] args = { exception };

        XMLDataStoreException generalException = new XMLDataStoreException(ExceptionMessageGenerator.buildMessage(XMLDataStoreException.class, GENERAL_EXCEPTION, args));
        generalException.setErrorCode(GENERAL_EXCEPTION);
        generalException.setInternalException(exception);
        return generalException;
    }

    public static XMLDataStoreException heterogeneousChildElements(Object parentNode) {
        Object[] args = { parentNode };

        XMLDataStoreException exception = new XMLDataStoreException(ExceptionMessageGenerator.buildMessage(XMLDataStoreException.class, HETEROGENEOUS_CHILD_ELEMENTS, args));
        exception.setErrorCode(HETEROGENEOUS_CHILD_ELEMENTS);
        return exception;
    }

    public static XMLDataStoreException heterogeneousTableNames(Record row) {
        Object[] args = { row, CR };

        XMLDataStoreException exception = new XMLDataStoreException(ExceptionMessageGenerator.buildMessage(XMLDataStoreException.class, HETEROGENEOUS_TABLE_NAMES, args));
        exception.setErrorCode(HETEROGENEOUS_TABLE_NAMES);
        return exception;
    }

    public static XMLDataStoreException illegalAccessException(java.lang.reflect.Method method) {
        Object[] args = { method };

        XMLDataStoreException exception = new XMLDataStoreException(ExceptionMessageGenerator.buildMessage(XMLDataStoreException.class, ILLEGAL_ACCESS_EXCEPTION, args));
        exception.setErrorCode(ILLEGAL_ACCESS_EXCEPTION);
        return exception;
    }

    public static XMLDataStoreException instantiationException(Class javaClass) {
        Object[] args = { javaClass.getName() };

        XMLDataStoreException exception = new XMLDataStoreException(ExceptionMessageGenerator.buildMessage(XMLDataStoreException.class, INSTANTIATION_EXCEPTION, args));
        exception.setErrorCode(INSTANTIATION_EXCEPTION);
        return exception;
    }

    public static XMLDataStoreException instantiationIllegalAccessException(Class javaClass) {
        Object[] args = { javaClass.getName() };

        XMLDataStoreException exception = new XMLDataStoreException(ExceptionMessageGenerator.buildMessage(XMLDataStoreException.class, ILLEGAL_ACCESS_EXCEPTION, args));
        exception.setErrorCode(INSTANTIATION_ILLEGAL_ACCESS_EXCEPTION);
        return exception;
    }

    public static XMLDataStoreException invalidFieldValue(String fieldName, Object fieldValue) {
        Object[] args = { fieldName, fieldValue, fieldValue.getClass().getName(), CR };

        XMLDataStoreException exception = new XMLDataStoreException(ExceptionMessageGenerator.buildMessage(XMLDataStoreException.class, INVALID_FIELD_VALUE, args));// the field value should be non-null
        exception.setErrorCode(INVALID_FIELD_VALUE);
        return exception;
    }

    public static XMLDataStoreException invocationTargetException(java.lang.reflect.Method method, java.lang.reflect.InvocationTargetException ite) {
        Object[] args = { method, ite.getTargetException(), CR };

        XMLDataStoreException exception = new XMLDataStoreException(ExceptionMessageGenerator.buildMessage(XMLDataStoreException.class, INVOCATION_TARGET_EXCEPTION, args));
        exception.setErrorCode(INVOCATION_TARGET_EXCEPTION);
        return exception;
    }

    public static XMLDataStoreException ioException(java.io.IOException ioException) {
        Object[] args = { ioException };

        XMLDataStoreException exception = new XMLDataStoreException(ExceptionMessageGenerator.buildMessage(XMLDataStoreException.class, IOEXCEPTION, args));
        exception.setErrorCode(IOEXCEPTION);
        exception.setInternalException(ioException);
        return exception;
    }

    public static XMLDataStoreException noSuchMethod(Class javaClass, String methodName) {
        Object[] args = { javaClass.getName(), methodName };

        XMLDataStoreException exception = new XMLDataStoreException(ExceptionMessageGenerator.buildMessage(XMLDataStoreException.class, NO_SUCH_METHOD, args));
        exception.setErrorCode(NO_SUCH_METHOD);
        return exception;
    }

    public static XMLDataStoreException notADirectory(java.io.File directory) {
        Object[] args = { directory };

        XMLDataStoreException exception = new XMLDataStoreException(ExceptionMessageGenerator.buildMessage(XMLDataStoreException.class, NOT_A_DIRECTORY, args));
        exception.setErrorCode(NOT_A_DIRECTORY);
        return exception;
    }

    public static XMLDataStoreException parserError(XMLPlatformException parseException) {
        // parseException is not really an Exception, it is an interface that acts like an Exception
        Object[] args = {  };

        XMLDataStoreException exception = new XMLDataStoreException(ExceptionMessageGenerator.buildMessage(XMLDataStoreException.class, SAX_PARSER_ERROR, args));
        exception.setErrorCode(SAX_PARSER_ERROR);
        return exception;
    }

    public static XMLDataStoreException unableToCloseReadStream(Object translator, java.io.IOException ioException) {
        Object[] args = { translator };

        XMLDataStoreException exception = new XMLDataStoreException(ExceptionMessageGenerator.buildMessage(XMLDataStoreException.class, UNABLE_TO_CLOSE_READ_STREAM, args));
        exception.setErrorCode(UNABLE_TO_CLOSE_READ_STREAM);
        exception.setInternalException(ioException);
        return exception;
    }

    public static XMLDataStoreException unableToCloseWriteStream(XMLCall call, java.io.IOException ioException) {
        Object[] args = { call };

        XMLDataStoreException exception = new XMLDataStoreException(ExceptionMessageGenerator.buildMessage(XMLDataStoreException.class, UNABLE_TO_CLOSE_WRITE_STREAM, args));
        exception.setErrorCode(UNABLE_TO_CLOSE_WRITE_STREAM);
        exception.setInternalException(ioException);
        return exception;
    }

    public static XMLDataStoreException unableToCreateWriteStream(java.io.File file, java.io.IOException ioException) {
        Object[] args = { file };

        XMLDataStoreException exception = new XMLDataStoreException(ExceptionMessageGenerator.buildMessage(XMLDataStoreException.class, UNABLE_TO_CREATE_WRITE_STREAM, args));
        exception.setErrorCode(UNABLE_TO_CREATE_WRITE_STREAM);
        exception.setInternalException(ioException);
        return exception;
    }
}