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
 *     dmccann - September 17/2009 - 1.2 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.jaxb.compiler;

import org.eclipse.persistence.logging.AbstractSessionLog;
import org.eclipse.persistence.logging.SessionLog;

/**
 *  <p>Class used to log warnings during the processing of JAXB annotations and
 *  OXM XML bindings files.</p> 
 */
public class JAXBMetadataLogger {

    public final static String NO_PROPERTY_FOR_JAVA_ATTRIBUTE = "jaxb_metadata_warning_ignoring_java_attribute";
    public final static String INVALID_BOUND_TYPE = "jaxb_metadata_warning_invalid_bound_type";
    public final static String NO_CLASSES_TO_PROCESS = "jaxb_metadata_warning_no_classes_to_process";
    public final static String INVALID_JAVA_ATTRIBUTE = "jaxb_metadata_warning_invalid_java_attribute";
    public final static String INVALID_TYPE_ON_MAP = "jaxb_metadata_warning_ignoring_type_on_map";

    /**
     * Create a new JAXBMetadataLogger
     */
    public JAXBMetadataLogger() {
    }

    /**
     * Create a new JAXBMetadataLogger and set the logLevel.
     * 
     * @param logLevel
     * @see SessionLog
     */
    public JAXBMetadataLogger(int logLevel) {
        AbstractSessionLog.getLog().setLevel(logLevel);
    }

    /**
     * Logs a message at the SessionLog.INFO level.
     * 
     * @param message The message to log
     * @param args The arguments corresponding with this message
     * @see SessionLog
     */
    public void log(String message, Object[] args) {
        AbstractSessionLog.getLog().log(SessionLog.INFO, message, args);
    }

    /**
     * Logs a Warning message to the SessionLog.
     * 
     * @param message The message to log
     * @param args The arguments corresponding with this message
     * @see SessionLog
     */
    public void logWarning(String message, Object[] args) {
        AbstractSessionLog.getLog().log(SessionLog.WARNING, message, args);
    }

    /**
     * Logs a Severe message to the SessionLog. Typically called when EclipseLink is not in a state
     * to continue
     * 
     * @param throwable
     * @see SessionLog
     */
    public void logException(Throwable throwable) {
        AbstractSessionLog.getLog().logThrowable(SessionLog.SEVERE, throwable);
    }
}
