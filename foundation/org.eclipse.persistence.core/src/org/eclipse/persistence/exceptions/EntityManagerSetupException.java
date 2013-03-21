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
package org.eclipse.persistence.exceptions;

import org.eclipse.persistence.exceptions.i18n.*;

public class EntityManagerSetupException extends EclipseLinkException {
    public static final int SESSIONS_XML_VALIDATION_EXCEPTION = 28001;
    public static final int WRONG_SESSION_TYPE_EXCEPTION = 28002;
    public static final int MISSING_SERVER_PLATFORM_EXCEPTION = 28003;
    public static final int ERROR_IN_SETUP_OF_EM = 28004;
    public static final int EXCEPTION_IN_SETUP_OF_EM = 28005;
    public static final int CLASS_NOT_FOUND_FOR_PROPERTY = 28006;
    public static final int FAILED_TO_INSTANTIATE_SERVER_PLATFORM = 28007;
    public static final int CLASS_NOT_FOUND_WHILE_PROCESSING_ANNOTATIONS = 28008;
    public static final int ATTEMPTED_REDEPLOY_WITHOUT_CLOSE = 28009;
    public static final int JTA_PERSISTENCE_UNIT_INFO_MISSING_JTA_DATA_SOURCE = 28010;
    public static final int SESSION_REMOVED_DURING_DEPLOYMENT = 28011;
    public static final int WRONG_PROPERTY_VALUE_TYPE = 28012;
    public static final int CANNOT_DEPLOY_WITHOUT_PREDEPLOY = 28013;
    public static final int FAILED_WHILE_PROCESSING_PROPERTY = 28014;
    public static final int FAILED_TO_INSTANTIATE_LOGGER = 28015;
    public static final int PU_NOT_EXIST = 28016;
    public static final int CANNOT_PREDEPLOY = 28017;
    public static final int PREDEPLOY_FAILED = 28018;
    public static final int DEPLOY_FAILED = 28019;
    public static final int SESSION_LOADED_FROM_SESSIONSXML_MUST_BE_SERVER_SESSION = 28020;
    public static final int ATTEMPTED_LOAD_SESSION_WITHOUT_NAME_PROVIDED = 28021;
    public static final int WRONG_WEAVING_PROPERTY_VALUE = 28022;
    public static final int METHOD_INVOCATION_FAILED = 28023;
    public static final int CANNOT_ACCESS_METHOD_ON_OBJECT=28024;
    public static final int NO_TEMPORARY_CLASSLOADER_AVAILABLE=28025;    
    public static final int CREATE_CONTAINER_EMF_NOT_SUPPORTED_IN_OSGI=28026;  
    public static final int COULD_NOT_FIND_PERSISTENCE_UNIT_BUNDLE=28027;  
    public static final int FAILED_TO_INSTANTIATE_PROPERTY = 28028;
    public static final int COMPOSITE_INCOMPATIBLE_WITH_SESSIONS_XML = 28029;
    public static final int COMPOSITE_MEMBER_CANNOT_BE_USED_STANDALONE = 28030;
    public static final int MISSING_PROPERTY = 28031;
    
    /**
     * INTERNAL:
     * EclipseLink exceptions should only be thrown by EclipseLink.
     */
    public EntityManagerSetupException() {
        super();
    }

    /**
     * INTERNAL:
     * EclipseLink exceptions should only be thrown by EclipseLink.
     */
    protected EntityManagerSetupException(String message) {
        super(message);
    }

    /**
     * INTERNAL:
     * EclipseLink exceptions should only be thrown by EclipseLink.
     */
    protected EntityManagerSetupException(String message, Throwable internalException) {
        super(message);
        setInternalException(internalException);
    }

    public static EntityManagerSetupException attemptedRedeployWithoutClose(String sessionName) {
        Object[] args = { sessionName };

        EntityManagerSetupException setupException = new EntityManagerSetupException(ExceptionMessageGenerator.buildMessage(EntityManagerSetupException.class, ATTEMPTED_REDEPLOY_WITHOUT_CLOSE, args));
        setupException.setErrorCode(ATTEMPTED_REDEPLOY_WITHOUT_CLOSE);
        return setupException;
    }

    public static EntityManagerSetupException missingServerPlatformException(String sessionName, String xmlFileName) {
        Object[] args = { sessionName, xmlFileName };

        EntityManagerSetupException setupException = new EntityManagerSetupException(ExceptionMessageGenerator.buildMessage(EntityManagerSetupException.class, MISSING_SERVER_PLATFORM_EXCEPTION, args));
        setupException.setErrorCode(MISSING_SERVER_PLATFORM_EXCEPTION);
        return setupException;
    }

    public static EntityManagerSetupException sessionRemovedDuringDeployment(String sessionName){
        Object[] args = { sessionName };

        EntityManagerSetupException setupException = new EntityManagerSetupException(ExceptionMessageGenerator.buildMessage(EntityManagerSetupException.class, SESSION_REMOVED_DURING_DEPLOYMENT, args));
        setupException.setErrorCode(SESSION_REMOVED_DURING_DEPLOYMENT);
        return setupException;	
    }
    
    public static EntityManagerSetupException sessionXMLValidationException(String sessionName, String xmlFileName, ValidationException exception) {
        Object[] args = { sessionName, xmlFileName };

        EntityManagerSetupException setupException = new EntityManagerSetupException(ExceptionMessageGenerator.buildMessage(EntityManagerSetupException.class, SESSIONS_XML_VALIDATION_EXCEPTION, args), exception);
        setupException.setErrorCode(SESSIONS_XML_VALIDATION_EXCEPTION);
        return setupException;
    }

    public static EntityManagerSetupException wrongSessionTypeException(String sessionName, String xmlFileName, Exception exception) {
        Object[] args = { sessionName, xmlFileName };

        EntityManagerSetupException setupException = new EntityManagerSetupException(ExceptionMessageGenerator.buildMessage(EntityManagerSetupException.class, WRONG_SESSION_TYPE_EXCEPTION, args), exception);
        setupException.setErrorCode(WRONG_SESSION_TYPE_EXCEPTION);
        return setupException;
    }

    public static EntityManagerSetupException errorInSetupOfEM() {
        Object[] args = {  };

        EntityManagerSetupException setupException = new EntityManagerSetupException(ExceptionMessageGenerator.buildMessage(EntityManagerSetupException.class, ERROR_IN_SETUP_OF_EM, args));
        setupException.setErrorCode(ERROR_IN_SETUP_OF_EM);
        return setupException;
    }

    public static EntityManagerSetupException exceptionInSetupOfEM(Exception exception) {
        Object[] args = {  };

        EntityManagerSetupException setupException = new EntityManagerSetupException(ExceptionMessageGenerator.buildMessage(EntityManagerSetupException.class, EXCEPTION_IN_SETUP_OF_EM, args), exception);
        setupException.setErrorCode(EXCEPTION_IN_SETUP_OF_EM);
        return setupException;
    }

    public static EntityManagerSetupException classNotFoundForProperty(String className, String propertyName, Exception exception) {
        Object[] args = { className, propertyName };

        EntityManagerSetupException setupException = new EntityManagerSetupException(ExceptionMessageGenerator.buildMessage(EntityManagerSetupException.class, CLASS_NOT_FOUND_FOR_PROPERTY, args), exception);
        setupException.setErrorCode(CLASS_NOT_FOUND_FOR_PROPERTY);
        return setupException;
    }

    public static EntityManagerSetupException failedToInstantiateServerPlatform(String serverPlatformClass, String serverPlatformString, Exception exception) {
        Object[] args = { serverPlatformClass, serverPlatformString };

        EntityManagerSetupException setupException = new EntityManagerSetupException(ExceptionMessageGenerator.buildMessage(EntityManagerSetupException.class, FAILED_TO_INSTANTIATE_SERVER_PLATFORM, args), exception);
        setupException.setErrorCode(FAILED_TO_INSTANTIATE_SERVER_PLATFORM);
        return setupException;
    }

    public static EntityManagerSetupException missingProperty(String property) {
        Object[] args = { property };

        EntityManagerSetupException setupException = new EntityManagerSetupException(ExceptionMessageGenerator.buildMessage(EntityManagerSetupException.class, MISSING_PROPERTY, args));
        setupException.setErrorCode(MISSING_PROPERTY);
        return setupException;
    }

    public static EntityManagerSetupException failedToInstantiateProperty(String className, String property, Exception exception) {
        Object[] args = { className, property };

        EntityManagerSetupException setupException = new EntityManagerSetupException(ExceptionMessageGenerator.buildMessage(EntityManagerSetupException.class, FAILED_TO_INSTANTIATE_PROPERTY, args), exception);
        setupException.setErrorCode(FAILED_TO_INSTANTIATE_PROPERTY);
        return setupException;
    }
    
    public static EntityManagerSetupException classNotFoundWhileProcessingAnnotations(String className, Exception exception) {
        Object[] args = { className };

        EntityManagerSetupException setupException = new EntityManagerSetupException(ExceptionMessageGenerator.buildMessage(EntityManagerSetupException.class, CLASS_NOT_FOUND_WHILE_PROCESSING_ANNOTATIONS, args), exception);
        setupException.setErrorCode(CLASS_NOT_FOUND_WHILE_PROCESSING_ANNOTATIONS);
        return setupException;
    }
    
    public static EntityManagerSetupException jtaPersistenceUnitInfoMissingJtaDataSource(String persistenceUnitInfoName) {
        Object[] args = { persistenceUnitInfoName };

        EntityManagerSetupException setupException = new EntityManagerSetupException(ExceptionMessageGenerator.buildMessage(EntityManagerSetupException.class, JTA_PERSISTENCE_UNIT_INFO_MISSING_JTA_DATA_SOURCE, args));
        setupException.setErrorCode(JTA_PERSISTENCE_UNIT_INFO_MISSING_JTA_DATA_SOURCE);
        return setupException;
    }
    
    public static EntityManagerSetupException wrongPropertyValueType(String value, String expectedType, String propertyName) {
        Object[] args = { value, expectedType, propertyName };

        EntityManagerSetupException setupException = new EntityManagerSetupException(ExceptionMessageGenerator.buildMessage(EntityManagerSetupException.class, WRONG_PROPERTY_VALUE_TYPE, args));
        setupException.setErrorCode(WRONG_PROPERTY_VALUE_TYPE);
        return setupException;
    }
    
    public static EntityManagerSetupException cannotDeployWithoutPredeploy(String persistenceUnitName, String state, Exception exception) {
        Object[] args = { persistenceUnitName, state };

        EntityManagerSetupException setupException = new EntityManagerSetupException(ExceptionMessageGenerator.buildMessage(EntityManagerSetupException.class, CANNOT_DEPLOY_WITHOUT_PREDEPLOY, args), exception);
        setupException.setErrorCode(CANNOT_DEPLOY_WITHOUT_PREDEPLOY);
        return setupException;
    }

    public static EntityManagerSetupException failedWhileProcessingProperty(String propertyName, String propertyValue, Exception exception) {
        Object[] args = { propertyName, propertyValue };

        EntityManagerSetupException setupException = new EntityManagerSetupException(ExceptionMessageGenerator.buildMessage(EntityManagerSetupException.class, FAILED_WHILE_PROCESSING_PROPERTY, args), exception);
        setupException.setErrorCode(FAILED_WHILE_PROCESSING_PROPERTY);
        return setupException;
    }

    public static EntityManagerSetupException failedToInstantiateLogger(String loggerClassName, String propertyName, Exception exception) {
        Object[] args = { loggerClassName, propertyName };

        EntityManagerSetupException setupException = new EntityManagerSetupException(ExceptionMessageGenerator.buildMessage(EntityManagerSetupException.class, FAILED_TO_INSTANTIATE_LOGGER, args), exception);
        setupException.setErrorCode(FAILED_TO_INSTANTIATE_LOGGER);
        return setupException;
    }

    public static EntityManagerSetupException puNotExist(String puName) {
        Object[] args = { puName};

        EntityManagerSetupException setupException = new EntityManagerSetupException(ExceptionMessageGenerator.buildMessage(EntityManagerSetupException.class, PU_NOT_EXIST, args));
        setupException.setErrorCode(PU_NOT_EXIST);
        return setupException;
    }

    public static EntityManagerSetupException cannotPredeploy(String persistenceUnitName, String state, Exception exception) {
        Object[] args = { persistenceUnitName, state };

        EntityManagerSetupException setupException = new EntityManagerSetupException(ExceptionMessageGenerator.buildMessage(EntityManagerSetupException.class, CANNOT_PREDEPLOY, args), exception);
        setupException.setErrorCode(CANNOT_PREDEPLOY);
        return setupException;
    }

    public static EntityManagerSetupException predeployFailed(String persistenceUnitName, Throwable exception) {
        Object[] args = { persistenceUnitName };

        EntityManagerSetupException setupException = new EntityManagerSetupException(ExceptionMessageGenerator.buildMessage(EntityManagerSetupException.class, PREDEPLOY_FAILED, args), exception);
        setupException.setErrorCode(PREDEPLOY_FAILED);
        return setupException;
    }

    public static EntityManagerSetupException deployFailed(String persistenceUnitName, Throwable exception) {
        Object[] args = { persistenceUnitName };

        EntityManagerSetupException setupException = new EntityManagerSetupException(ExceptionMessageGenerator.buildMessage(EntityManagerSetupException.class, DEPLOY_FAILED, args), exception);
        setupException.setErrorCode(DEPLOY_FAILED);
        return setupException;
    }
    
    public static EntityManagerSetupException sessionLoadedFromSessionsXMLMustBeServerSession(String sessionName,String sessionsXML, Object session) {
        Object[] args = { sessionName,sessionsXML,session.getClass() };

        EntityManagerSetupException setupException = new EntityManagerSetupException(ExceptionMessageGenerator.buildMessage(EntityManagerSetupException.class, SESSION_LOADED_FROM_SESSIONSXML_MUST_BE_SERVER_SESSION, args));
        setupException.setErrorCode(SESSION_LOADED_FROM_SESSIONSXML_MUST_BE_SERVER_SESSION);
        return setupException;
    }

    public static EntityManagerSetupException sessionNameNeedBeSpecified(String PUName, String sessionsXML) {
        Object[] args = { PUName,sessionsXML };

        EntityManagerSetupException setupException = new EntityManagerSetupException(ExceptionMessageGenerator.buildMessage(EntityManagerSetupException.class, ATTEMPTED_LOAD_SESSION_WITHOUT_NAME_PROVIDED, args));
        setupException.setErrorCode(ATTEMPTED_LOAD_SESSION_WITHOUT_NAME_PROVIDED);
        return setupException;
    }
    
    public static EntityManagerSetupException wrongWeavingPropertyValue() {
        Object[] args = { };

        EntityManagerSetupException setupException = new EntityManagerSetupException(ExceptionMessageGenerator.buildMessage(EntityManagerSetupException.class, WRONG_WEAVING_PROPERTY_VALUE, args));
        setupException.setErrorCode(WRONG_WEAVING_PROPERTY_VALUE);
        return setupException;
    }
    
    public static EntityManagerSetupException methodInvocationFailed(java.lang.reflect.Method aMethod, Object anObject, Exception ex) {
        Object[] args = {aMethod, anObject, anObject.getClass() };

        EntityManagerSetupException setupException = new EntityManagerSetupException(ExceptionMessageGenerator.buildMessage(EntityManagerSetupException.class, METHOD_INVOCATION_FAILED, args),ex);
        setupException.setErrorCode(METHOD_INVOCATION_FAILED);
        return setupException;
    }
    
    public static EntityManagerSetupException cannotAccessMethodOnObject(java.lang.reflect.Method aMethod, Object anObject) {
        Object[] args = {aMethod, anObject, anObject.getClass() };

        EntityManagerSetupException setupException = new EntityManagerSetupException(ExceptionMessageGenerator.buildMessage(EntityManagerSetupException.class, CANNOT_ACCESS_METHOD_ON_OBJECT, args));
        setupException.setErrorCode(CANNOT_ACCESS_METHOD_ON_OBJECT);
        return setupException;
    }

    /**
     * INTERNAL:
     * The implementation of getNewTempClassLoader is returning null instead of a temporary ClassLoader instance.<br>
     * @param PUName
     * @return
     */
    public static EntityManagerSetupException noTemporaryClassLoaderAvailable(String PUName) {
        Object[] args = { PUName };
        EntityManagerSetupException setupException = new EntityManagerSetupException(ExceptionMessageGenerator.buildMessage(//
                EntityManagerSetupException.class, NO_TEMPORARY_CLASSLOADER_AVAILABLE, args));
        setupException.setErrorCode(NO_TEMPORARY_CLASSLOADER_AVAILABLE);
        return setupException;
    }
    
    /**
     * INTERNAL:
     * Our OSGI persistence provider does not support a JavaEE-type deployment
     * @param PUName
     * @return
     */
    public static EntityManagerSetupException createContainerEntityManagerFactoryNotSupportedInOSGi() {
        EntityManagerSetupException setupException = new EntityManagerSetupException(ExceptionMessageGenerator.buildMessage(//
                EntityManagerSetupException.class, CREATE_CONTAINER_EMF_NOT_SUPPORTED_IN_OSGI, null));
        setupException.setErrorCode(CREATE_CONTAINER_EMF_NOT_SUPPORTED_IN_OSGI);
        return setupException;
    }
    
    /**
     * INTERNAL:
     * An OSGi application is trying to instantiate a persistence unit for which a bundle does not exist
     * @param PUName
     * @return
     */
    public static EntityManagerSetupException couldNotFindPersistenceUnitBundle(String persistenceUnitName) {
        Object[] args = { persistenceUnitName };
        EntityManagerSetupException setupException = new EntityManagerSetupException(ExceptionMessageGenerator.buildMessage(//
                EntityManagerSetupException.class, COULD_NOT_FIND_PERSISTENCE_UNIT_BUNDLE, args));
        setupException.setErrorCode(COULD_NOT_FIND_PERSISTENCE_UNIT_BUNDLE);
        return setupException;
    }

    /**
     * INTERNAL:
     * Persistence unit tries to use sessions.xml and to be a composite.
     * @param PUName
     * @return
     */
    public static EntityManagerSetupException compositeIncompatibleWithSessionsXml(String persistenceUnitName) {
        Object[] args = { persistenceUnitName };
        EntityManagerSetupException setupException = new EntityManagerSetupException(ExceptionMessageGenerator.buildMessage(
                EntityManagerSetupException.class, COMPOSITE_INCOMPATIBLE_WITH_SESSIONS_XML, args));
        setupException.setErrorCode(COMPOSITE_INCOMPATIBLE_WITH_SESSIONS_XML);
        return setupException;
    }

    /**
     * INTERNAL:
     * Persistence unit tries to use sessions.xml and to be a composite.
     * @param PUName
     * @return
     */
    public static EntityManagerSetupException compositeMemberCannotBeUsedStandalone(String persistenceUnitName) {
        Object[] args = { persistenceUnitName };
        EntityManagerSetupException setupException = new EntityManagerSetupException(ExceptionMessageGenerator.buildMessage(
                EntityManagerSetupException.class, COMPOSITE_MEMBER_CANNOT_BE_USED_STANDALONE, args));
        setupException.setErrorCode(COMPOSITE_MEMBER_CANNOT_BE_USED_STANDALONE);
        return setupException;
    }
}

