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
 * Wrapper for any exception that occurred through native CMP migration process.
 */
public class MigrationUtilityException extends EclipseLinkException {
    public final static int WLS_MULTIPLE_JARS_WITH_INPUT_ORION_NOT_SUPPORTED = 26001;
    public final static int FAILED_TO_CREATE_JAR_CLASSLOADER = 26002;
    public final static int FAILED_TO_CREATE_DIRECTORY = 26003;
    public final static int FILE_NOT_ACCESSIBLE = 26004;
    public final static int FILE_NOT_DELETABLE = 26005;
    public final static int FAILED_TO_READ_INPUTSTREAM = 26006;
    public final static int FAILED_TO_CLOSE_STREAM = 26007;
    public final static int FAILED_TO_CLOSE_ZIPFILE = 26008;
    public final static int JAR_FILE_NOT_ACCESSIBLE = 26009;
    public final static int QUERY_NOT_WELL_DEFINED = 26010;
    public final static int FAIL_TO_LOAD_CLASS_FOR_QUERY = 26011;
    public final static int FINDER_NOT_DEFINED_IN_ENTITY_HOME = 26012;
    public final static int EJB_SELECT_NOT_DEFINED_IN_ENTITY_BEAN_CLASS = 26013;
    public final static int ENTITY_IN_WLS_CMP_JAR_NOT_DEFINED_IN_WLS_EJB_JAR = 26014;
    public final static int NO_ENTITY_DEFINED_IN_WLS_CMP_JAR = 26015;
    public final static int WLS_CMP_DESCRIPTOR_FILE_NOT_FOUND = 26016; 
    public final static int CMP_DESCRIPTOR_NOT_FOUND_IN_JAR = 26017;
    public final static int NOT_ALL_ENTITIES_IN_EJB_JAR_MAPPED_IN_ORION = 26018;
    public final static int ENTITY_NOT_MAPPED_IN_ORION = 26019;
    public final static int ENTITY_IN_ORION_NOT_IN_EJB_JAR = 26020;
    public final static int ENTITY_NOT_MAPPED_IN_WLS = 26021;

    public MigrationUtilityException(String message) {
        super(message);
    }

    protected MigrationUtilityException(String message, Exception internalException) {
        super(message, internalException);
    }

    public static MigrationUtilityException multipleWlsMigratableJarsWithInputOrionXmlNotSupported(String earFilePath) {
        Object[] args = { earFilePath };
        MigrationUtilityException exception = new MigrationUtilityException(ExceptionMessageGenerator.buildMessage(MigrationUtilityException.class, WLS_MULTIPLE_JARS_WITH_INPUT_ORION_NOT_SUPPORTED, args));
        exception.setErrorCode(WLS_MULTIPLE_JARS_WITH_INPUT_ORION_NOT_SUPPORTED);
        return exception;
    }

    public static MigrationUtilityException failedToCreateJarClassloader(String jarUrlFile, Exception internalEx) {
        Object[] args = { jarUrlFile };
        MigrationUtilityException exception = new MigrationUtilityException(ExceptionMessageGenerator.buildMessage(MigrationUtilityException.class, FAILED_TO_CREATE_JAR_CLASSLOADER, args), internalEx);
        exception.setErrorCode(FAILED_TO_CREATE_JAR_CLASSLOADER);
        return exception;
    }

    public static MigrationUtilityException failedToCreateDirectory(String directoryPath, Exception internalEx) {
        Object[] args = { directoryPath };
        MigrationUtilityException exception = new MigrationUtilityException(ExceptionMessageGenerator.buildMessage(MigrationUtilityException.class, FAILED_TO_CREATE_DIRECTORY, args), internalEx);
        exception.setErrorCode(FAILED_TO_CREATE_DIRECTORY);
        return exception;
    }

    public static MigrationUtilityException fileNotAccessible(String filePath, Exception internalEx) {
        Object[] args = { filePath };
        MigrationUtilityException exception = new MigrationUtilityException(ExceptionMessageGenerator.buildMessage(MigrationUtilityException.class, FILE_NOT_ACCESSIBLE, args), internalEx);
        exception.setErrorCode(FILE_NOT_ACCESSIBLE);
        return exception;
    }

    public static MigrationUtilityException fileNotDeletable(String filePath, Exception internalEx) {
        Object[] args = { filePath };
        MigrationUtilityException exception = new MigrationUtilityException(ExceptionMessageGenerator.buildMessage(MigrationUtilityException.class, FILE_NOT_DELETABLE, args), internalEx);
        exception.setErrorCode(FILE_NOT_DELETABLE);
        return exception;
    }

    public static MigrationUtilityException failedToReadInputstream(Exception internalEx) {
        Object[] args = {  };
        MigrationUtilityException exception = new MigrationUtilityException(ExceptionMessageGenerator.buildMessage(MigrationUtilityException.class, FAILED_TO_READ_INPUTSTREAM, args), internalEx);
        exception.setErrorCode(FAILED_TO_READ_INPUTSTREAM);
        return exception;
    }

    public static MigrationUtilityException failedToCloseStream(Exception internalEx) {
        Object[] args = {  };
        MigrationUtilityException exception = new MigrationUtilityException(ExceptionMessageGenerator.buildMessage(MigrationUtilityException.class, FAILED_TO_CLOSE_STREAM, args), internalEx);
        exception.setErrorCode(FAILED_TO_CLOSE_STREAM);
        return exception;
    }

    public static MigrationUtilityException failedToCloseZipFile(String filePath, Exception internalEx) {
        Object[] args = { filePath };
        MigrationUtilityException exception = new MigrationUtilityException(ExceptionMessageGenerator.buildMessage(MigrationUtilityException.class, FAILED_TO_CLOSE_ZIPFILE, args), internalEx);
        exception.setErrorCode(FAILED_TO_CLOSE_ZIPFILE);
        return exception;
    }

    public static MigrationUtilityException jarFileNotAccessible(String jarFilePath, Exception internalEx) {
        Object[] args = { jarFilePath };
        MigrationUtilityException exception = new MigrationUtilityException(ExceptionMessageGenerator.buildMessage(MigrationUtilityException.class, JAR_FILE_NOT_ACCESSIBLE, args), internalEx);
        exception.setErrorCode(JAR_FILE_NOT_ACCESSIBLE);
        return exception;
    }

    public static MigrationUtilityException queryNotWellDefined(String queryName, Class[] params, String entityName) {
        Object[] args = { queryName, params, entityName };
        MigrationUtilityException exception = new MigrationUtilityException(ExceptionMessageGenerator.buildMessage(MigrationUtilityException.class, QUERY_NOT_WELL_DEFINED, args));
        exception.setErrorCode(QUERY_NOT_WELL_DEFINED);
        return exception;
    }

    public static MigrationUtilityException failToLoadClassForQuery(String queryName, String paramType, String entityName) {
        Object[] args = { queryName, paramType, entityName };
        MigrationUtilityException exception = new MigrationUtilityException(ExceptionMessageGenerator.buildMessage(MigrationUtilityException.class, FAIL_TO_LOAD_CLASS_FOR_QUERY, args));
        exception.setErrorCode(FAIL_TO_LOAD_CLASS_FOR_QUERY);
        return exception;
    }

    public static MigrationUtilityException finderNotDefinedInEntityHome(String finderName, Class[] params, String entityName) {
        Object[] args = { finderName, params, entityName };
        MigrationUtilityException exception = new MigrationUtilityException(ExceptionMessageGenerator.buildMessage(MigrationUtilityException.class, FINDER_NOT_DEFINED_IN_ENTITY_HOME, args));
        exception.setErrorCode(FINDER_NOT_DEFINED_IN_ENTITY_HOME);
        return exception;
    }

    public static MigrationUtilityException ejbSelectNotDefinedInEntityBeanClass(String ejbSelectName, Class[] params, String entityName) {
        Object[] args = { ejbSelectName, params, entityName };
        MigrationUtilityException exception = new MigrationUtilityException(ExceptionMessageGenerator.buildMessage(MigrationUtilityException.class, EJB_SELECT_NOT_DEFINED_IN_ENTITY_BEAN_CLASS, args));
        exception.setErrorCode(EJB_SELECT_NOT_DEFINED_IN_ENTITY_BEAN_CLASS);
        return exception;
    }

    public static MigrationUtilityException entityInWlsCmpJarNotDefinedInWlsEjbJar(String entityName) {
        Object[] args = { entityName };
        MigrationUtilityException exception = new MigrationUtilityException(ExceptionMessageGenerator.buildMessage(MigrationUtilityException.class, ENTITY_IN_WLS_CMP_JAR_NOT_DEFINED_IN_WLS_EJB_JAR, args));
        exception.setErrorCode(ENTITY_IN_WLS_CMP_JAR_NOT_DEFINED_IN_WLS_EJB_JAR);
        return exception;
    }

    public static MigrationUtilityException noEntityDefinedInWlsCmpJar() {
        Object[] args = {  };
        MigrationUtilityException exception = new MigrationUtilityException(ExceptionMessageGenerator.buildMessage(MigrationUtilityException.class, NO_ENTITY_DEFINED_IN_WLS_CMP_JAR, args));
        exception.setErrorCode(NO_ENTITY_DEFINED_IN_WLS_CMP_JAR);
        return exception;
    }

    public static MigrationUtilityException wlsCmpDescriptorFileNotFound(String cmpDescFileName, String fileDrectory, Exception internalEx) {
        Object[] args = { cmpDescFileName, fileDrectory };
        MigrationUtilityException exception = new MigrationUtilityException(ExceptionMessageGenerator.buildMessage(MigrationUtilityException.class, WLS_CMP_DESCRIPTOR_FILE_NOT_FOUND, args));
        exception.setErrorCode(WLS_CMP_DESCRIPTOR_FILE_NOT_FOUND);
        exception.setInternalException(internalEx);
        return exception;
    }

    public static MigrationUtilityException cmpDescriptorFileNotFoundInJar(String jarFileName, String cmpDescriptorName) {
        Object[] args = { jarFileName, cmpDescriptorName };
        MigrationUtilityException exception = new MigrationUtilityException(ExceptionMessageGenerator.buildMessage(MigrationUtilityException.class, CMP_DESCRIPTOR_NOT_FOUND_IN_JAR, args));
        exception.setErrorCode(CMP_DESCRIPTOR_NOT_FOUND_IN_JAR);
        return exception;
    }
    
     public static MigrationUtilityException notAllEntitiesInEjbJarMappedInOrion(List notMappedEntities, String jarFileName) {
        Object[] args = {notMappedEntities, jarFileName };
        MigrationUtilityException exception = new MigrationUtilityException(ExceptionMessageGenerator.buildMessage(MigrationUtilityException.class, NOT_ALL_ENTITIES_IN_EJB_JAR_MAPPED_IN_ORION, args));
        exception.setErrorCode(NOT_ALL_ENTITIES_IN_EJB_JAR_MAPPED_IN_ORION);
        return exception;
    }
    
    public static MigrationUtilityException entityNotMappedInOrion(String entityName) {
        Object[] args = { entityName };
        MigrationUtilityException exception = new MigrationUtilityException(ExceptionMessageGenerator.buildMessage(MigrationUtilityException.class, ENTITY_NOT_MAPPED_IN_ORION, args));
        exception.setErrorCode(ENTITY_NOT_MAPPED_IN_ORION);
        return exception;
    }
    
    public static MigrationUtilityException entityNotMappedInWLS(String entityName) {
        Object[] args = { entityName };
        MigrationUtilityException exception = new MigrationUtilityException(ExceptionMessageGenerator.buildMessage(MigrationUtilityException.class, ENTITY_NOT_MAPPED_IN_ORION, args));
        exception.setErrorCode(ENTITY_NOT_MAPPED_IN_WLS);
        return exception;
    }
    
    public static MigrationUtilityException entityNameNotSpecifiedInEjbJar(String entityNameInOrionEjbJar) {
        Object[] args = { entityNameInOrionEjbJar };
        MigrationUtilityException exception = new MigrationUtilityException(ExceptionMessageGenerator.buildMessage(MigrationUtilityException.class, ENTITY_IN_ORION_NOT_IN_EJB_JAR, args));
        exception.setErrorCode(ENTITY_IN_ORION_NOT_IN_EJB_JAR);
        return exception;
    }
}