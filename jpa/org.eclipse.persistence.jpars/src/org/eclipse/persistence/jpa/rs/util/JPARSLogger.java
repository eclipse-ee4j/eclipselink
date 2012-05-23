/*******************************************************************************
 * Copyright (c) 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *      tware - initial 
 ******************************************************************************/
package org.eclipse.persistence.jpa.rs.util;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.persistence.jpa.rs.logging.LoggingLocalization;

public class JPARSLogger {
    
    static final Logger logger = Logger.getLogger("org.eclipse.persistence.jpars");    
    
    /**
     * INTERNAL:
     * Logging utility method.
     */
    public static void log(String message, Level level, Object[] params) {
        logger.log(level, LoggingLocalization.buildMessage(message, params));
    }
    
    public static void finest(String message, Object[] params){
        log(message, Level.FINEST, params);
    }

    public static void fine(String message, Object[] params){
        log(message, Level.FINE, params);
    }
    
    public static void warning(String message, Object[] params){
        log(message, Level.WARNING, params);
    }
    
    public static void exception(String message, Object[] params, Exception exc){
        logger.log(Level.FINER, LoggingLocalization.buildMessage(message, params), exc);
    }
    
    public static void setLogLevel(Level level){
        logger.setLevel(level);
    }
    
}

