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

import java.net.URL;

import org.eclipse.persistence.exceptions.i18n.*;

public class StaticWeaveException  extends EclipseLinkException {

    private String resourceName = null;
    
    public static final int EXCEPTION_OPENNING_ARCHIVE = 40001;
    public static final int EXCEPTION_NO_SOURCE_SPECIFIED = 40002;
    public static final int EXCEPTION_NO_TARGET_SPECIFIED = 40003;
    public static final int EXCEPTION_NO_SUPPORT_WEAVING_INPLACE_FOR_JAR = 40004;
    public static final int EXCEPTION_OPEN_LOGGING_FILE = 40005;
    public static final int EXCEPTION_FOR_ILLEGALE_LOGGING_LEVEL = 40006;
    public static final int EXCEPTION_WEAVING = 40007;

    /**
     * INTERNAL:
     * EclipseLink exceptions should only be thrown by EclipseLink.
     */
    public StaticWeaveException() {
        super();
    }

    /**
     * INTERNAL:
     * EclipseLink exceptions should only be thrown by EclipseLink.
     */
    protected StaticWeaveException(String message) {
        super(message);
    }

    /**
     * INTERNAL:
     * EclipseLink exceptions should only be thrown by EclipseLink.
     */
    protected StaticWeaveException(String message, Throwable internalException) {
        super(message);
        setInternalException(internalException);
    }
    
    /**
     * INTERNAL:
     * EclipseLink exceptions should only be thrown by EclipseLink.
     */
    public static StaticWeaveException exceptionOpeningArchive(URL archive, Exception cause) {
        Object[] args = { archive };

        StaticWeaveException openArchiveException = new StaticWeaveException(ExceptionMessageGenerator.buildMessage(StaticWeaveException.class, EXCEPTION_OPENNING_ARCHIVE, args),cause);
        openArchiveException.setResourceName(archive.toString());
        openArchiveException.setErrorCode(EXCEPTION_OPENNING_ARCHIVE);
        return openArchiveException;
    }
    
    /**
     * INTERNAL:
     * EclipseLink exceptions should only be thrown by EclipseLink.
     */
    public static StaticWeaveException weaveInplaceForJar(String filePath) {
        Object[] args = { filePath };
        
        StaticWeaveException loadingException = new StaticWeaveException(ExceptionMessageGenerator.buildMessage(StaticWeaveException.class, EXCEPTION_NO_SUPPORT_WEAVING_INPLACE_FOR_JAR, args));
        loadingException.setResourceName(filePath);
        loadingException.setErrorCode(EXCEPTION_NO_SUPPORT_WEAVING_INPLACE_FOR_JAR);
        return loadingException;
    }
    
    /**
     * INTERNAL:
     * EclipseLink exceptions should only be thrown by EclipseLink.
     */
    public static StaticWeaveException missingSource() {
        Object[] args = { null };
        
        StaticWeaveException missingSourceException = new StaticWeaveException(ExceptionMessageGenerator.buildMessage(StaticWeaveException.class, EXCEPTION_NO_SOURCE_SPECIFIED, args));
        missingSourceException.setResourceName(null);
        missingSourceException.setErrorCode(EXCEPTION_NO_SOURCE_SPECIFIED);
        return missingSourceException;
    }
    
    /**
     * INTERNAL:
     * EclipseLink exceptions should only be thrown by EclipseLink.
     */
    public static StaticWeaveException missingTarget() {
        Object[] args = { null };
        
        StaticWeaveException missingTargetException = new StaticWeaveException(ExceptionMessageGenerator.buildMessage(StaticWeaveException.class, EXCEPTION_NO_TARGET_SPECIFIED, args));
        missingTargetException.setResourceName(null);
        missingTargetException.setErrorCode(EXCEPTION_NO_TARGET_SPECIFIED);
        return missingTargetException;
    }

    /**
     * INTERNAL:
     * EclipseLink exceptions should only be thrown by EclipseLink.
     */
    public static StaticWeaveException openLoggingFileException(String LoggingFile,Exception cause) {
        Object[] args = { LoggingFile };
        
        StaticWeaveException loadingException = new StaticWeaveException(ExceptionMessageGenerator.buildMessage(StaticWeaveException.class, EXCEPTION_OPEN_LOGGING_FILE, args), cause);
        loadingException.setResourceName(LoggingFile);
        loadingException.setErrorCode(EXCEPTION_OPEN_LOGGING_FILE);
        return loadingException;
    }
    
    /**
     * INTERNAL:
     * EclipseLink exceptions should only be thrown by EclipseLink.
     */
    public static StaticWeaveException illegalLoggingLevel(String loggingLevel) {
        Object[] args = { loggingLevel };
        
        StaticWeaveException loadingException = new StaticWeaveException(ExceptionMessageGenerator.buildMessage(StaticWeaveException.class, EXCEPTION_FOR_ILLEGALE_LOGGING_LEVEL, args));
        loadingException.setResourceName(loggingLevel);
        loadingException.setErrorCode(EXCEPTION_FOR_ILLEGALE_LOGGING_LEVEL);
        return loadingException;
    }
    
    
    public static StaticWeaveException exceptionPerformWeaving(Exception cause, Object sourceJar) {
        Object[] args = { sourceJar };

        StaticWeaveException loadingException = new StaticWeaveException(ExceptionMessageGenerator.buildMessage(StaticWeaveException.class, EXCEPTION_WEAVING, args), cause);
        loadingException.setResourceName(null);
        loadingException.setErrorCode(EXCEPTION_WEAVING);
        return loadingException;
    }

    
    public String getResourceName(){
        return resourceName;
    }
    
    public void setResourceName(String resourceName){
        this.resourceName = resourceName;        
    }
    
}
