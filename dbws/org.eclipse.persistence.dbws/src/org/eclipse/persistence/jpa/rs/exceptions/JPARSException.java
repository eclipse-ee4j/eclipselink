/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//      gonural - initial implementation
package org.eclipse.persistence.jpa.rs.exceptions;

import org.eclipse.persistence.exceptions.EclipseLinkException;
import org.eclipse.persistence.exceptions.JPARSErrorCodes;
import org.eclipse.persistence.exceptions.i18n.ExceptionMessageGenerator;
import org.eclipse.persistence.logging.AbstractSessionLog;

import javax.ws.rs.core.Response.Status;

public class JPARSException extends EclipseLinkException {
    private Status httpStatusCode;

    /**
     * Instantiates a new JPARS exception.
     */
    public JPARSException() {
        super();
    }

    /* (non-Javadoc)
     * @see org.eclipse.persistence.exceptions.EclipseLinkException#getMessage()
     */
    @Override
    public String getMessage() {
        return super.getUnformattedMessage();
    }

    /**
     * Gets the http status code.
     *
     * @return the http status code
     */
    public Status getHttpStatusCode() {
        return httpStatusCode;
    }

    /**
     * Sets the http status code.
     *
     * @param httpStatusCode the new http status code
     */
    public void setHttpStatusCode(Status httpStatusCode) {
        this.httpStatusCode = httpStatusCode;
    }

    public JPARSException(String message) {
        super(message);
    }

    private JPARSException(String msg, Throwable internalException) {
        super(msg, internalException);
    }

    /**
     * Entity not found.
     *
     * @param entityType the entity type
     * @param entityId the entity id
     * @param persistenceUnit the persistence unit
     * @return the JPARS exception
     */
    public static JPARSException entityNotFound(String entityType, String entityId, String persistenceUnit) {
        Object[] args = { entityType, entityId, persistenceUnit };

        String msg = ExceptionMessageGenerator.buildMessage(JPARSException.class, JPARSErrorCodes.ENTITY_NOT_FOUND, args);
        JPARSException exception = new JPARSException(msg);
        exception.setErrorCode(JPARSErrorCodes.ENTITY_NOT_FOUND);
        exception.setHttpStatusCode(Status.NOT_FOUND);
        return exception;
    }

    /**
     * Class descriptor could not be found for entity.
     *
     * @param entityType the entity type
     * @param persistenceUnit the persistence unit
     * @return the JPARS exception
     */
    public static JPARSException classOrClassDescriptorCouldNotBeFoundForEntity(String entityType, String persistenceUnit) {
        Object[] args = { entityType, persistenceUnit };

        String msg = ExceptionMessageGenerator.buildMessage(JPARSException.class, JPARSErrorCodes.CLASS_OR_CLASS_DESCRIPTOR_COULD_NOT_BE_FOUND, args);
        JPARSException exception = new JPARSException(msg);
        exception.setErrorCode(JPARSErrorCodes.CLASS_OR_CLASS_DESCRIPTOR_COULD_NOT_BE_FOUND);
        exception.setHttpStatusCode(Status.NOT_FOUND);

        return exception;
    }

    /**
     * Attribute could not be found for entity.
     *
     * @param attributeName the attribute name
     * @param entityType the entity type
     * @param entityId the entity id
     * @param persistenceUnit the persistence unit
     * @return the JPARS exception
     */
    public static JPARSException attributeCouldNotBeFoundForEntity(String attributeName, String entityType, String entityId, String persistenceUnit) {
        Object[] args = { attributeName, entityType, entityId, persistenceUnit };

        String msg = ExceptionMessageGenerator.buildMessage(JPARSException.class, JPARSErrorCodes.ATTRIBUTE_COULD_NOT_BE_FOUND_FOR_ENTITY, args);
        JPARSException exception = new JPARSException(msg);
        exception.setErrorCode(JPARSErrorCodes.ATTRIBUTE_COULD_NOT_BE_FOUND_FOR_ENTITY);
        exception.setHttpStatusCode(Status.NOT_FOUND);

        return exception;
    }

    /**
     * Selection query for attribute could not be found for entity.
     *
     * @param attributeName the attribute name
     * @param entityType the entity type
     * @param entityId the entity id
     * @param persistenceUnit the persistence unit
     * @return the JPARS exception
     */
    public static JPARSException selectionQueryForAttributeCouldNotBeFoundForEntity(String attributeName, String entityType, String entityId, String persistenceUnit) {
        Object[] args = { attributeName, entityType, entityId, persistenceUnit };

        String msg = ExceptionMessageGenerator.buildMessage(JPARSException.class, JPARSErrorCodes.SELECTION_QUERY_FOR_ATTRIBUTE_COULD_NOT_BE_FOUND_FOR_ENTITY, args);
        JPARSException exception = new JPARSException(msg);
        exception.setErrorCode(JPARSErrorCodes.SELECTION_QUERY_FOR_ATTRIBUTE_COULD_NOT_BE_FOUND_FOR_ENTITY);
        exception.setHttpStatusCode(Status.NOT_FOUND);

        return exception;
    }

    /**
     * Invalid paging request.
     *
     * @return the JPARS exception
     */
    public static JPARSException invalidPagingRequest() {
        Object[] args = {};

        String msg = ExceptionMessageGenerator.buildMessage(JPARSException.class, JPARSErrorCodes.INVALID_PAGING_REQUEST, args);
        JPARSException exception = new JPARSException(msg);
        exception.setErrorCode(JPARSErrorCodes.INVALID_PAGING_REQUEST);
        exception.setHttpStatusCode(Status.BAD_REQUEST);

        return exception;
    }

    /**
     * Invalid paging request.
     *
     * @return the JPARS exception
     */
    public static JPARSException invalidParameter(String parameterName, String invalidValue) {
        final Object[] args = { parameterName, invalidValue };
        final String msg = ExceptionMessageGenerator.buildMessage(JPARSException.class, JPARSErrorCodes.INVALID_PARAMETER, args);
        AbstractSessionLog.getLog().info(msg);

        final JPARSException exception = new JPARSException(msg);
        exception.setErrorCode(JPARSErrorCodes.INVALID_PARAMETER);
        exception.setHttpStatusCode(Status.BAD_REQUEST);

        return exception;
    }

    /**
     * Pagination parameters are used in non-pageable resource.
     *
     * @return the JPARS exception
     */
    public static JPARSException paginationParameterForNotPageableResource() {
        Object[] args = {};

        String msg = ExceptionMessageGenerator.buildMessage(JPARSException.class, JPARSErrorCodes.PAGINATION_PARAMETER_USED_FOR_NOT_PAGEABLE_RESOURCE, args);
        JPARSException exception = new JPARSException(msg);
        exception.setErrorCode(JPARSErrorCodes.PAGINATION_PARAMETER_USED_FOR_NOT_PAGEABLE_RESOURCE);
        exception.setHttpStatusCode(Status.BAD_REQUEST);

        return exception;
    }

    /**
     * Both fields and excludeFields parameters are present in request.
     *
     * @return the JPARS exception
     */
    public static JPARSException fieldsFilteringBothParametersPresent() {
        Object[] args = {};

        String msg = ExceptionMessageGenerator.buildMessage(JPARSException.class, JPARSErrorCodes.FIELDS_FILTERING_BOTH_PARAMETERS_PRESENT, args);
        JPARSException exception = new JPARSException(msg);
        exception.setErrorCode(JPARSErrorCodes.FIELDS_FILTERING_BOTH_PARAMETERS_PRESENT);
        exception.setHttpStatusCode(Status.BAD_REQUEST);

        return exception;
    }

    /**
     * Database mapping could not be found for entity attribute.
     *
     * @param attributeName the attribute name
     * @param entityType the entity type
     * @param entityId the entity id
     * @param persistenceUnit the persistence unit
     * @return the JPARS exception
     */
    public static JPARSException databaseMappingCouldNotBeFoundForEntityAttribute(String attributeName, String entityType, String entityId, String persistenceUnit) {
        Object[] args = { attributeName, entityType, entityId, persistenceUnit };

        String msg = ExceptionMessageGenerator.buildMessage(JPARSException.class, JPARSErrorCodes.DATABASE_MAPPING_COULD_NOT_BE_FOUND_FOR_ENTITY_ATTRIBUTE, args);
        JPARSException exception = new JPARSException(msg);
        exception.setErrorCode(JPARSErrorCodes.DATABASE_MAPPING_COULD_NOT_BE_FOUND_FOR_ENTITY_ATTRIBUTE);
        exception.setHttpStatusCode(Status.NOT_FOUND);

        return exception;
    }

    /**
     * Attribute could not be updated.
     *
     * @param attributeName the attribute name
     * @param entityType the entity type
     * @param entityId the entity id
     * @param persistenceUnit the persistence unit
     * @return the JPARS exception
     */
    public static JPARSException attributeCouldNotBeUpdated(String attributeName, String entityType, String entityId, String persistenceUnit) {
        Object[] args = { attributeName, entityType, entityId, persistenceUnit };

        String msg = ExceptionMessageGenerator.buildMessage(JPARSException.class, JPARSErrorCodes.ATTRIBUTE_COULD_NOT_BE_UPDATED, args);
        JPARSException exception = new JPARSException(msg);
        exception.setErrorCode(JPARSErrorCodes.ATTRIBUTE_COULD_NOT_BE_UPDATED);
        exception.setHttpStatusCode(Status.INTERNAL_SERVER_ERROR);

        return exception;
    }

    /**
     * Invalid service version.
     *
     * @param serviceVersion the service version
     * @return the JPARS exception
     */
    public static JPARSException invalidServiceVersion(String serviceVersion) {
        Object[] args = { serviceVersion };

        String msg = ExceptionMessageGenerator.buildMessage(JPARSException.class, JPARSErrorCodes.INVALID_SERVICE_VERSION, args);
        JPARSException exception = new JPARSException(msg);
        exception.setErrorCode(JPARSErrorCodes.INVALID_SERVICE_VERSION);
        exception.setHttpStatusCode(Status.BAD_REQUEST);

        return exception;
    }

    /**
     * Invalid remove attribute request.
     *
     * @param attributeName the attribute name
     * @param entityType the entity type
     * @param entityId the entity id
     * @param persistenceUnit the persistence unit
     * @return the JPARS exception
     */
    public static JPARSException invalidRemoveAttributeRequest(String attributeName, String entityType, String entityId, String persistenceUnit) {
        Object[] args = { entityType, entityId, persistenceUnit };

        String msg = ExceptionMessageGenerator.buildMessage(JPARSException.class, JPARSErrorCodes.INVALID_ATTRIBUTE_REMOVAL_REQUEST, args);
        JPARSException exception = new JPARSException(msg);
        exception.setErrorCode(JPARSErrorCodes.INVALID_ATTRIBUTE_REMOVAL_REQUEST);
        exception.setHttpStatusCode(Status.BAD_REQUEST);

        return exception;
    }

    /**
     * Response could not be built for find attribute request.
     *
     * @param attributeName the attribute name
     * @param entityType the entity type
     * @param entityId the entity id
     * @param persistenceUnit the persistence unit
     * @return the JPARS exception
     */
    public static JPARSException responseCouldNotBeBuiltForFindAttributeRequest(String attributeName, String entityType, String entityId, String persistenceUnit) {
        Object[] args = { entityType, entityId, persistenceUnit };

        String msg = ExceptionMessageGenerator.buildMessage(JPARSException.class, JPARSErrorCodes.RESPONSE_COULD_NOT_BE_BUILT_FOR_FIND_ATTRIBUTE_REQUEST, args);
        JPARSException exception = new JPARSException(msg);
        exception.setErrorCode(JPARSErrorCodes.RESPONSE_COULD_NOT_BE_BUILT_FOR_FIND_ATTRIBUTE_REQUEST);
        exception.setHttpStatusCode(Status.INTERNAL_SERVER_ERROR);

        return exception;
    }

    /**
     * Response could not be built for named query request.
     *
     * @param query the query
     * @param persistenceUnit the persistence unit
     * @return the JPARS exception
     */
    public static JPARSException responseCouldNotBeBuiltForNamedQueryRequest(String query, String persistenceUnit) {
        Object[] args = { query, persistenceUnit };

        String msg = ExceptionMessageGenerator.buildMessage(JPARSException.class, JPARSErrorCodes.RESPONSE_COULD_NOT_BE_BUILT_FOR_NAMED_QUERY_REQUEST, args);
        JPARSException exception = new JPARSException(msg);
        exception.setErrorCode(JPARSErrorCodes.RESPONSE_COULD_NOT_BE_BUILT_FOR_NAMED_QUERY_REQUEST);
        exception.setHttpStatusCode(Status.INTERNAL_SERVER_ERROR);

        return exception;
    }

    /**
     * Object referred by link does not exist.
     *
     * @param entityType the entity type
     * @param entityId the entity id
     * @return the JPARS exception
     */
    public static JPARSException objectReferredByLinkDoesNotExist(String entityType, Object entityId) {
        Object[] args = { entityType, entityId };

        String msg = ExceptionMessageGenerator.buildMessage(JPARSException.class, JPARSErrorCodes.OBJECT_REFERRED_BY_LINK_DOES_NOT_EXIST, args);
        JPARSException exception = new JPARSException(msg);
        exception.setErrorCode(JPARSErrorCodes.OBJECT_REFERRED_BY_LINK_DOES_NOT_EXIST);
        exception.setHttpStatusCode(Status.NOT_FOUND);

        return exception;
    }

    /**
     * Session bean lookup is invalid.
     *
     * @param jndiName
     *            the jndi name
     * @return the JPARS exception
     */
    public static JPARSException jndiNamePassedIsInvalid(String jndiName) {
        Object[] args = { jndiName };

        String msg = ExceptionMessageGenerator.buildMessage(JPARSException.class, JPARSErrorCodes.JNDI_NAME_IS_INVALID, args);
        JPARSException exception = new JPARSException(msg);
        exception.setErrorCode(JPARSErrorCodes.JNDI_NAME_IS_INVALID);
        exception.setHttpStatusCode(Status.FORBIDDEN);

        return exception;
    }

    /**
     * Session bean lookup failed.
     *
     * @param jndiName
     *            the jndi name
     * @return the JPARS exception
     */
    public static JPARSException sessionBeanCouldNotBeFound(String jndiName) {
        Object[] args = { jndiName };

        String msg = ExceptionMessageGenerator.buildMessage(JPARSException.class, JPARSErrorCodes.SESSION_BEAN_COULD_NOT_BE_FOUND, args);
        JPARSException exception = new JPARSException(msg);
        exception.setErrorCode(JPARSErrorCodes.SESSION_BEAN_COULD_NOT_BE_FOUND);
        exception.setHttpStatusCode(Status.NOT_FOUND);

        return exception;
    }

    /**
     * Invalid configuration.
     *
     * @return the JPARS exception
     */
    public static JPARSException invalidConfiguration() {
        Object[] args = {};

        String msg = ExceptionMessageGenerator.buildMessage(JPARSException.class, JPARSErrorCodes.INVALID_CONFIGURATION, args);
        JPARSException exception = new JPARSException(msg);
        exception.setErrorCode(JPARSErrorCodes.INVALID_CONFIGURATION);
        exception.setHttpStatusCode(Status.INTERNAL_SERVER_ERROR);

        return exception;
    }

    /**
     * Entity is not idempotent.
     *
     * @param entityType the entity type
     * @param persistenceUnit the persistence unit
     * @return the JPARS exception
     */
    public static JPARSException entityIsNotIdempotent(String entityType, String persistenceUnit) {
        Object[] args = { entityType, persistenceUnit };

        String msg = ExceptionMessageGenerator.buildMessage(JPARSException.class, JPARSErrorCodes.ENTITY_NOT_IDEMPOTENT, args);
        JPARSException exception = new JPARSException(msg);
        exception.setErrorCode(JPARSErrorCodes.ENTITY_NOT_IDEMPOTENT);
        exception.setHttpStatusCode(Status.BAD_REQUEST);

        return exception;
    }

    /**
     * Persistence context could not be bootstrapped.
     *
     * @param persistenceUnit the persistence unit
     * @return the JPARS exception
     */
    public static JPARSException persistenceContextCouldNotBeBootstrapped(String persistenceUnit) {
        Object[] args = { persistenceUnit, persistenceUnit };

        String msg = ExceptionMessageGenerator.buildMessage(JPARSException.class, JPARSErrorCodes.PERSISTENCE_CONTEXT_COULD_NOT_BE_BOOTSTRAPPED, args);
        JPARSException exception = new JPARSException(msg);
        exception.setErrorCode(JPARSErrorCodes.PERSISTENCE_CONTEXT_COULD_NOT_BE_BOOTSTRAPPED);
        exception.setHttpStatusCode(Status.INTERNAL_SERVER_ERROR);

        return exception;
    }

    /**
     * Exception occurred.
     *
     * @param exception the exception
     * @return the JPARS exception
     */
    public static JPARSException exceptionOccurred(Exception exception) {
        if (exception instanceof JPARSException) {
            return (JPARSException)exception;
        }

        int errorCode = JPARSErrorCodes.AN_EXCEPTION_OCCURRED;
        String msg = ExceptionMessageGenerator.buildMessage(JPARSException.class, errorCode, new Object[] { exception.getClass().getSimpleName() }).trim();

        if (exception instanceof EclipseLinkException) {
            errorCode = ((EclipseLinkException) exception).getErrorCode();
            msg = ((EclipseLinkException) exception).getClass().getName().trim();
        } else if (exception.getCause() instanceof EclipseLinkException) {
            errorCode = ((EclipseLinkException) (exception.getCause())).getErrorCode();
            msg = ((EclipseLinkException) (exception.getCause())).getClass().getName().trim();
        }

        JPARSException jparsException = new JPARSException(msg, exception);
        jparsException.setErrorCode(errorCode);
        jparsException.setInternalException(exception);

        return jparsException;
    }
}
